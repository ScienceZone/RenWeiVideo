/*
 * FLV demuxer
 * Copyright (c) 2003 The FFmpeg Project
 *
 * This demuxer will generate a 1 byte extradata for VP6F content.
 * It is composed of:
 *  - upper 4bits: difference between encoded width and visible width
 *  - lower 4bits: difference between encoded height and visible height
 *
 * This file is part of FFmpeg.
 *
 * FFmpeg is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * FFmpeg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with FFmpeg; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#include "libavutil/avstring.h"
#include "libavutil/channel_layout.h"
#include "libavutil/dict.h"
#include "libavutil/opt.h"
#include "libavutil/intfloat.h"
#include "libavutil/mathematics.h"
#include "libavcodec/bytestream.h"
#include "libavcodec/mpeg4audio.h"
#include "avformat.h"
#include "internal.h"
#include "avio_internal.h"
#include "flv.h"

#define VALIDATE_INDEX_TS_THRESH 2500


/****************************************************************************
 * Dolit Encrypt 
 ****************************************************************************/

#define SWAP(a, b) ((a) ^= (b), (b) ^= (a), (a) ^= (b))

const unsigned char rc4PasswordArr[] = {'M','u', 'S', 'h', 'i', 'Z', 'a', 'i', 'X', 'i',
'A', 'N', 'Y', 'G', 'J', 'I', 'A', 'O', 'Y', 'U','E', 'P', 
'I', 'N', 'G', 'P', 'A', 'I', '2', '0', '1', '5'
};

const int rc4PassSize = sizeof (rc4PasswordArr) / sizeof (rc4PasswordArr[0]);

typedef struct hls_rc4_s 
{
    unsigned char sbox[256];      /* Encryption array             */
    unsigned char key[256];      /* Numeric key values           */
    int bInit;
}hls_rc4_s;

static void InitRC4Key(hls_rc4_s *rc4)
{
    int n = 0, m = 0;
    const unsigned char* pszKey = rc4PasswordArr;
    int keyLen = rc4PassSize;

    if (rc4->bInit == 1)
        return;
    if (rc4 == NULL )
        return;

    memset(rc4->sbox,0,256);
    memset(rc4->key,0,256);

    for (m = 0;  m < 256; m++)  /* Initialize the key sequence */
    {
        *(rc4->key + m)= *(pszKey + (m % keyLen));
        *(rc4->sbox + m) = m + 13;
    }

    for (m=0; m < 256; m++)
    {
        n = (n + 2 + *(rc4->sbox+m) + *(rc4->key + m)) &0xff;
        SWAP(*(rc4->sbox + m),*(rc4->sbox + n));
    }
    rc4->bInit = 1;
}

// 可逆加密函数，加密一次后，如果再次调用，就是解密
    /*
    * encryptData: 要处理的字符串指针
    * dataLen: 要处理的字符串长度
    * pos: 这个要处理的字符串在整个文件中的偏移量。 因为我们的加密不同位置（偏移量）的密钥不同，是随着整个长度而变化的，因此需要知道位移
    *      比如，a这个字符，如果位置在第一位加密后的字符可能是x，但如果在文件的中间，可能加密后就是y。
    */
static void Encrypt(unsigned char *pszText, int64_t textLen, int64_t pos, hls_rc4_s *rc4) 
{
    int64_t ilen = textLen + pos;
    int64_t i = 0;
    if (rc4 == NULL)
        return;

    if (!rc4->bInit)
        InitRC4Key(rc4);

    for (i = pos; i < ilen; i++)
    {
        int h = ((int)i + 6) &0xff;
        int j = (h + rc4->key[h] + *(rc4->sbox + h)) &0xff;
        int k = *(rc4->sbox + ((*(rc4->sbox + h) + *(rc4->sbox + j)) &0xff ));

        if(k == rc4->key[2])       /* avoid '\0' beween the decoded text; */
            k = 0;

        *(pszText + i - pos) ^=  k;  
    }
    //pos = ilen;   //已经解密过的不可重复解密
}

typedef struct {
    const AVClass *class; ///< Class for private options.
    int trust_metadata;   ///< configure streams according onMetaData
    int wrong_dts;        ///< wrong dts due to negative cts
    uint8_t *new_extradata[FLV_STREAM_TYPE_NB];
    int new_extradata_size[FLV_STREAM_TYPE_NB];
    int last_sample_rate;
    int last_channels;
    struct {
        int64_t dts;
        int64_t pos;
    } validate_index[2];
    int validate_next;
    int validate_count;
    int searched_for_end;

    int64_t file_offset;

    void *opaque;           /**< A private pointer, passed to the read/write/seek/...functions. */
    int (*read_packet)(void *opaque, uint8_t *buf, int buf_size);
    int (*write_packet)(void *opaque, uint8_t *buf, int buf_size);
    int64_t (*seek)(void *opaque, int64_t offset, int whence);

    hls_rc4_s  rc4;
} FLDVContext;

//static unsigned long swap_bytes(unsigned long a)
//{
//    return (a >> 24) | ((a & 0xff0000) >> 8) | ((a & 0xff00) << 8) | ((a & 0xff) << 24);
//}


//fldv_RC4DecodeData(flv, s->pb->buffer, s->pb->bytes_read, (s->pb->buffer_size * (s->pb->bytes_read - 1) / s->pb->buffer_size);
static int fldv_RC4DecodeData(FLDVContext *c, uint8_t *buf, int64_t buf_size, int64_t pos)
{
    if (buf_size <= 0 || buf == NULL)
        return 0;

    Encrypt(buf, buf_size, pos, &c->rc4);    
    return 0;
}

static int RC4DecodeData(uint8_t *buf, int64_t buf_size, int64_t pos)
{
    hls_rc4_s rc4; 
    if (buf_size <= 0 || buf == NULL)
        return 0;

    rc4.bInit = 0;
    Encrypt(buf, buf_size, pos, &rc4);
    return 0;
}

static int fldv_decode_read(void *opaque, uint8_t *buf, int buf_size)
{
    /*
    AVFormatContext * s = opaque; 
    FLDVContext *flv = s->priv_data;    
    int n = avio_read(s->pb, buf, buf_size);
    //int n = s->pb->read_packet(s->pb->opaque, buf, buf_size);
    if (n < 0)
        return n;

    //fldv_RC4DecodeData(flv, buf, buf_size, ((flv->fpb->bytes_read - 1) / flv->fpb->buffer_size) * flv->fpb->buffer_size);
    //fldv_RC4DecodeData(flv, buf, buf_size, flv->fpb->bytes_read);
    fldv_RC4DecodeData(flv, buf, n, flv->file_offset);
    flv->file_offset += n;
    return n;*/

    //AVFormatContext * s = opaque; 
    FLDVContext *flv = opaque; //s->priv_data;  
    int n = 0;
    if (!flv->read_packet)
        return 0;
    n = flv->read_packet(flv->opaque, buf, buf_size);

/*av_log(NULL, AV_LOG_ERROR,
        "decode_read: file_offset %d, buf_size %d, n %d.\n",
        (int)flv->file_offset, buf_size, n);
*/

    fldv_RC4DecodeData(flv, buf, n, flv->file_offset);
    flv->file_offset += n;
    return n;
}

static int fldv_decode_write(void *opaque, uint8_t *buf, int buf_size)
{
    //AVFormatContext * s = opaque;     
    //avio_write(s->pb, buf, buf_size);
    //return s->pb->error;

    //AVFormatContext * s = opaque; 
    FLDVContext *flv = opaque; //s->priv_data;  
    int n = 0;
    if (!flv->write_packet)
        return 0;
    n = flv->write_packet(flv->opaque, buf, buf_size);

/*av_log(NULL, AV_LOG_ERROR,
        "decode_write: file_offset %d, buf_size %d, n %d.\n",
        (int)flv->file_offset, buf_size, n);*/
    //fldv_RC4DecodeData(flv, buf, n, flv->file_offset);
    //flv->file_offset += n;
    return n;
}

static int64_t fldv_decode_seek(void *opaque, int64_t offset, int whence)
{
    //AVFormatContext * s = opaque; 
    //FLDVContext *flv = s->priv_data;  
    //flv->file_offset = offset;
    //return avio_seek(s->pb, offset, whence);
    //AVFormatContext * s = opaque; 
    FLDVContext *flv = opaque; //s->priv_data; 

    if (!flv->seek)
        return 0;

    if (offset != 0)
    {
        if (whence == 0 || whence == SEEK_SET)
            flv->file_offset = offset;
        else
        {
            if (whence == SEEK_CUR)
                flv->file_offset += offset;
        }
    }
/*av_log(NULL, AV_LOG_ERROR,
        "decode_seek: file_offset %d, offset %d, whence %d.\n",
        (int)flv->file_offset, (int)offset, whence);*/
    return flv->seek(flv->opaque, offset, whence);    
}

static int fldv_probe(AVProbeData *p)
{
    //av_log(NULL, AV_LOG_ERROR, "fldv_probe FLDV size:%d.\n", p->buf_size);
    const uint8_t *d;
    char decode_buf[32] = {0};
    int64_t len = p->buf_size;
    if (len > 32)
        len = 32;
    memcpy(decode_buf, p->buf, len);
    RC4DecodeData(decode_buf, len, 0);

    d = decode_buf; //p->buf;
    //av_log(NULL, AV_LOG_ERROR, "fldv_probe FLDV decode d[0]:%d, d[1]:%d, d[2]:%d.\n", d[0], d[1], d[2]);
    if (d[0] == 'F' &&
        d[1] == 'L' &&
        d[2] == 'V' &&
        d[3] < 5 && d[5] == 0 &&
        AV_RB32(d + 5) > 8) {
        return AVPROBE_SCORE_MAX;
    }
    return 0;
}

static AVStream *create_stream(AVFormatContext *s, int codec_type)
{
    AVStream *st = avformat_new_stream(s, NULL);
    if (!st)
        return NULL;
    st->codec->codec_type = codec_type;
    if (s->nb_streams>=3 ||(   s->nb_streams==2
                           && s->streams[0]->codec->codec_type != AVMEDIA_TYPE_DATA
                           && s->streams[1]->codec->codec_type != AVMEDIA_TYPE_DATA))
        s->ctx_flags &= ~AVFMTCTX_NOHEADER;

    avpriv_set_pts_info(st, 32, 1, 1000); /* 32 bit pts in ms */
    return st;
}

static int fldv_same_audio_codec(AVCodecContext *acodec, int flags)
{
    int bits_per_coded_sample = (flags & FLV_AUDIO_SAMPLESIZE_MASK) ? 16 : 8;
    int flv_codecid           = flags & FLV_AUDIO_CODECID_MASK;
    int codec_id;

    if (!acodec->codec_id && !acodec->codec_tag)
        return 1;

    if (acodec->bits_per_coded_sample != bits_per_coded_sample)
        return 0;

    switch (flv_codecid) {
    // no distinction between S16 and S8 PCM codec flags
    case FLV_CODECID_PCM:
        codec_id = bits_per_coded_sample == 8
                   ? AV_CODEC_ID_PCM_U8
#if HAVE_BIGENDIAN
                   : AV_CODEC_ID_PCM_S16BE;
#else
                   : AV_CODEC_ID_PCM_S16LE;
#endif
        return codec_id == acodec->codec_id;
    case FLV_CODECID_PCM_LE:
        codec_id = bits_per_coded_sample == 8
                   ? AV_CODEC_ID_PCM_U8
                   : AV_CODEC_ID_PCM_S16LE;
        return codec_id == acodec->codec_id;
    case FLV_CODECID_AAC:
        return acodec->codec_id == AV_CODEC_ID_AAC;
    case FLV_CODECID_ADPCM:
        return acodec->codec_id == AV_CODEC_ID_ADPCM_SWF;
    case FLV_CODECID_SPEEX:
        return acodec->codec_id == AV_CODEC_ID_SPEEX;
    case FLV_CODECID_MP3:
        return acodec->codec_id == AV_CODEC_ID_MP3;
    case FLV_CODECID_NELLYMOSER_8KHZ_MONO:
    case FLV_CODECID_NELLYMOSER_16KHZ_MONO:
    case FLV_CODECID_NELLYMOSER:
        return acodec->codec_id == AV_CODEC_ID_NELLYMOSER;
    case FLV_CODECID_PCM_MULAW:
        return acodec->sample_rate == 8000 &&
               acodec->codec_id    == AV_CODEC_ID_PCM_MULAW;
    case FLV_CODECID_PCM_ALAW:
        return acodec->sample_rate == 8000 &&
               acodec->codec_id    == AV_CODEC_ID_PCM_ALAW;
    default:
        return acodec->codec_tag == (flv_codecid >> FLV_AUDIO_CODECID_OFFSET);
    }
}

static void fldv_set_audio_codec(AVFormatContext *s, AVStream *astream,
                                AVCodecContext *acodec, int flv_codecid)
{
    switch (flv_codecid) {
    // no distinction between S16 and S8 PCM codec flags
    case FLV_CODECID_PCM:
        acodec->codec_id = acodec->bits_per_coded_sample == 8
                           ? AV_CODEC_ID_PCM_U8
#if HAVE_BIGENDIAN
                           : AV_CODEC_ID_PCM_S16BE;
#else
                           : AV_CODEC_ID_PCM_S16LE;
#endif
        break;
    case FLV_CODECID_PCM_LE:
        acodec->codec_id = acodec->bits_per_coded_sample == 8
                           ? AV_CODEC_ID_PCM_U8
                           : AV_CODEC_ID_PCM_S16LE;
        break;
    case FLV_CODECID_AAC:
        acodec->codec_id = AV_CODEC_ID_AAC;
        break;
    case FLV_CODECID_ADPCM:
        acodec->codec_id = AV_CODEC_ID_ADPCM_SWF;
        break;
    case FLV_CODECID_SPEEX:
        acodec->codec_id    = AV_CODEC_ID_SPEEX;
        acodec->sample_rate = 16000;
        break;
    case FLV_CODECID_MP3:
        acodec->codec_id      = AV_CODEC_ID_MP3;
        astream->need_parsing = AVSTREAM_PARSE_FULL;
        break;
    case FLV_CODECID_NELLYMOSER_8KHZ_MONO:
        // in case metadata does not otherwise declare samplerate
        acodec->sample_rate = 8000;
        acodec->codec_id    = AV_CODEC_ID_NELLYMOSER;
        break;
    case FLV_CODECID_NELLYMOSER_16KHZ_MONO:
        acodec->sample_rate = 16000;
        acodec->codec_id    = AV_CODEC_ID_NELLYMOSER;
        break;
    case FLV_CODECID_NELLYMOSER:
        acodec->codec_id = AV_CODEC_ID_NELLYMOSER;
        break;
    case FLV_CODECID_PCM_MULAW:
        acodec->sample_rate = 8000;
        acodec->codec_id    = AV_CODEC_ID_PCM_MULAW;
        break;
    case FLV_CODECID_PCM_ALAW:
        acodec->sample_rate = 8000;
        acodec->codec_id    = AV_CODEC_ID_PCM_ALAW;
        break;
    default:
        avpriv_request_sample(s, "Audio codec (%x)",
               flv_codecid >> FLV_AUDIO_CODECID_OFFSET);
        acodec->codec_tag = flv_codecid >> FLV_AUDIO_CODECID_OFFSET;
    }
}

static int fldv_same_video_codec(AVCodecContext *vcodec, int flags)
{
    int flv_codecid = flags & FLV_VIDEO_CODECID_MASK;

    if (!vcodec->codec_id && !vcodec->codec_tag)
        return 1;

    switch (flv_codecid) {
    case FLV_CODECID_H263:
        return vcodec->codec_id == AV_CODEC_ID_FLV1;
    case FLV_CODECID_SCREEN:
        return vcodec->codec_id == AV_CODEC_ID_FLASHSV;
    case FLV_CODECID_SCREEN2:
        return vcodec->codec_id == AV_CODEC_ID_FLASHSV2;
    case FLV_CODECID_VP6:
        return vcodec->codec_id == AV_CODEC_ID_VP6F;
    case FLV_CODECID_VP6A:
        return vcodec->codec_id == AV_CODEC_ID_VP6A;
    case FLV_CODECID_H264:
        return vcodec->codec_id == AV_CODEC_ID_H264;
    default:
        return vcodec->codec_tag == flv_codecid;
    }
}

static int fldv_set_video_codec(AVFormatContext *s, AVStream *vstream,
                               int flv_codecid, int read)
{
    AVCodecContext *vcodec = vstream->codec;
    switch (flv_codecid) {
    case FLV_CODECID_H263:
        vcodec->codec_id = AV_CODEC_ID_FLV1;
        break;
    case FLV_CODECID_REALH263:
        vcodec->codec_id = AV_CODEC_ID_H263;
        break; // Really mean it this time
    case FLV_CODECID_SCREEN:
        vcodec->codec_id = AV_CODEC_ID_FLASHSV;
        break;
    case FLV_CODECID_SCREEN2:
        vcodec->codec_id = AV_CODEC_ID_FLASHSV2;
        break;
    case FLV_CODECID_VP6:
        vcodec->codec_id = AV_CODEC_ID_VP6F;
    case FLV_CODECID_VP6A:
        if (flv_codecid == FLV_CODECID_VP6A)
            vcodec->codec_id = AV_CODEC_ID_VP6A;
        if (read) {
            if (vcodec->extradata_size != 1) {
                ff_alloc_extradata(vcodec, 1);
            }
            if (vcodec->extradata)
                vcodec->extradata[0] = avio_r8(s->pb);
            else
                avio_skip(s->pb, 1);
        }
        return 1;     // 1 byte body size adjustment for flv_read_packet()
    case FLV_CODECID_H264:
        vcodec->codec_id = AV_CODEC_ID_H264;
        vstream->need_parsing = AVSTREAM_PARSE_HEADERS;
        return 3;     // not 4, reading packet type will consume one byte
    case FLV_CODECID_MPEG4:
        vcodec->codec_id = AV_CODEC_ID_MPEG4;
        return 3;
    default:
        avpriv_request_sample(s, "Video codec (%x)", flv_codecid);
        vcodec->codec_tag = flv_codecid;
    }

    return 0;
}

static int amf_get_string(AVIOContext *ioc, char *buffer, int buffsize)
{
    int length = avio_rb16(ioc);
    if (length >= buffsize) {
        avio_skip(ioc, length);
        return -1;
    }

    avio_read(ioc, buffer, length);

    buffer[length] = '\0';

    return length;
}

static int parse_keyframes_index(AVFormatContext *s, AVIOContext *ioc,
                                 AVStream *vstream, int64_t max_pos)
{
    FLDVContext *flv       = s->priv_data;
    unsigned int timeslen = 0, fileposlen = 0, i;
    char str_val[256];
    int64_t *times         = NULL;
    int64_t *filepositions = NULL;
    int ret                = AVERROR(ENOSYS);
    int64_t initial_pos    = avio_tell(ioc);

    if (vstream->nb_index_entries>0) {
        av_log(s, AV_LOG_WARNING, "Skipping duplicate index\n");
        return 0;
    }

    if (s->flags & AVFMT_FLAG_IGNIDX)
        return 0;

    while (avio_tell(ioc) < max_pos - 2 &&
           amf_get_string(ioc, str_val, sizeof(str_val)) > 0) {
        int64_t **current_array;
        unsigned int arraylen;

        // Expect array object in context
        if (avio_r8(ioc) != AMF_DATA_TYPE_ARRAY)
            break;

        arraylen = avio_rb32(ioc);
        if (arraylen>>28)
            break;

        if       (!strcmp(KEYFRAMES_TIMESTAMP_TAG , str_val) && !times) {
            current_array = &times;
            timeslen      = arraylen;
        } else if (!strcmp(KEYFRAMES_BYTEOFFSET_TAG, str_val) &&
                   !filepositions) {
            current_array = &filepositions;
            fileposlen    = arraylen;
        } else
            // unexpected metatag inside keyframes, will not use such
            // metadata for indexing
            break;

        if (!(*current_array = av_mallocz(sizeof(**current_array) * arraylen))) {
            ret = AVERROR(ENOMEM);
            goto finish;
        }

        for (i = 0; i < arraylen && avio_tell(ioc) < max_pos - 1; i++) {
            if (avio_r8(ioc) != AMF_DATA_TYPE_NUMBER)
                goto invalid;
            current_array[0][i] = av_int2double(avio_rb64(ioc));
        }
        if (times && filepositions) {
            // All done, exiting at a position allowing amf_parse_object
            // to finish parsing the object
            ret = 0;
            break;
        }
    }

    if (timeslen == fileposlen && fileposlen>1 && max_pos <= filepositions[0]) {
        for (i = 0; i < fileposlen; i++) {
            av_add_index_entry(vstream, filepositions[i], times[i] * 1000,
                               0, 0, AVINDEX_KEYFRAME);
            if (i < 2) {
                flv->validate_index[i].pos = filepositions[i];
                flv->validate_index[i].dts = times[i] * 1000;
                flv->validate_count        = i + 1;
            }
        }
    } else {
invalid:
        av_log(s, AV_LOG_WARNING, "Invalid keyframes object, skipping.\n");
    }

finish:
    av_freep(&times);
    av_freep(&filepositions);
    avio_seek(ioc, initial_pos, SEEK_SET);
    return ret;
}

static int amf_parse_object(AVFormatContext *s, AVStream *astream,
                            AVStream *vstream, const char *key,
                            int64_t max_pos, int depth)
{
    AVCodecContext *acodec, *vcodec;
    FLDVContext *flv = s->priv_data;
    AVIOContext *ioc;
    AMFDataType amf_type;
    char str_val[1024];
    double num_val;

    num_val  = 0;
    ioc      = s->pb;
    amf_type = avio_r8(ioc);

    switch (amf_type) {
    case AMF_DATA_TYPE_NUMBER:
        num_val = av_int2double(avio_rb64(ioc));
        break;
    case AMF_DATA_TYPE_BOOL:
        num_val = avio_r8(ioc);
        break;
    case AMF_DATA_TYPE_STRING:
        if (amf_get_string(ioc, str_val, sizeof(str_val)) < 0) {
            av_log(s, AV_LOG_ERROR, "AMF_DATA_TYPE_STRING parsing failed\n");
            return -1;
        }
        break;
    case AMF_DATA_TYPE_OBJECT:
        if ((vstream || astream) && key &&
            ioc->seekable &&
            !strcmp(KEYFRAMES_TAG, key) && depth == 1)
            if (parse_keyframes_index(s, ioc, vstream ? vstream : astream,
                                      max_pos) < 0)
                av_log(s, AV_LOG_ERROR, "Keyframe index parsing failed\n");

        while (avio_tell(ioc) < max_pos - 2 &&
               amf_get_string(ioc, str_val, sizeof(str_val)) > 0)
            if (amf_parse_object(s, astream, vstream, str_val, max_pos,
                                 depth + 1) < 0)
                return -1;     // if we couldn't skip, bomb out.
        if (avio_r8(ioc) != AMF_END_OF_OBJECT) {
            av_log(s, AV_LOG_ERROR, "Missing AMF_END_OF_OBJECT in AMF_DATA_TYPE_OBJECT\n");
            return -1;
        }
        break;
    case AMF_DATA_TYPE_NULL:
    case AMF_DATA_TYPE_UNDEFINED:
    case AMF_DATA_TYPE_UNSUPPORTED:
        break;     // these take up no additional space
    case AMF_DATA_TYPE_MIXEDARRAY:
        avio_skip(ioc, 4);     // skip 32-bit max array index
        while (avio_tell(ioc) < max_pos - 2 &&
               amf_get_string(ioc, str_val, sizeof(str_val)) > 0)
            // this is the only case in which we would want a nested
            // parse to not skip over the object
            if (amf_parse_object(s, astream, vstream, str_val, max_pos,
                                 depth + 1) < 0)
                return -1;
        if (avio_r8(ioc) != AMF_END_OF_OBJECT) {
            av_log(s, AV_LOG_ERROR, "Missing AMF_END_OF_OBJECT in AMF_DATA_TYPE_MIXEDARRAY\n");
            return -1;
        }
        break;
    case AMF_DATA_TYPE_ARRAY:
    {
        unsigned int arraylen, i;

        arraylen = avio_rb32(ioc);
        for (i = 0; i < arraylen && avio_tell(ioc) < max_pos - 1; i++)
            if (amf_parse_object(s, NULL, NULL, NULL, max_pos,
                                 depth + 1) < 0)
                return -1;      // if we couldn't skip, bomb out.
    }
    break;
    case AMF_DATA_TYPE_DATE:
        avio_skip(ioc, 8 + 2);  // timestamp (double) and UTC offset (int16)
        break;
    default:                    // unsupported type, we couldn't skip
        av_log(s, AV_LOG_ERROR, "unsupported amf type %d\n", amf_type);
        return -1;
    }

    if (key) {
		acodec = astream ? astream->codec : NULL;
        vcodec = vstream ? vstream->codec : NULL;

        // stream info doesn't live any deeper than the first object
        if (depth == 1) {
            if (amf_type == AMF_DATA_TYPE_NUMBER ||
                amf_type == AMF_DATA_TYPE_BOOL) {
                if (!strcmp(key, "duration"))
                    s->duration = num_val * AV_TIME_BASE;
                else if (!strcmp(key, "videodatarate") && vcodec &&
                         0 <= (int)(num_val * 1024.0))
                    vcodec->bit_rate = num_val * 1024.0;
                else if (!strcmp(key, "audiodatarate") && acodec &&
                         0 <= (int)(num_val * 1024.0))
                    acodec->bit_rate = num_val * 1024.0;
                else if (!strcmp(key, "datastream")) {
                    AVStream *st = create_stream(s, AVMEDIA_TYPE_DATA);
                    if (!st)
                        return AVERROR(ENOMEM);
                    st->codec->codec_id = AV_CODEC_ID_TEXT;
                } else if (flv->trust_metadata) {
                    if (!strcmp(key, "videocodecid") && vcodec) {
                        fldv_set_video_codec(s, vstream, num_val, 0);
                    } else if (!strcmp(key, "audiocodecid") && acodec) {
                        int id = ((int)num_val) << FLV_AUDIO_CODECID_OFFSET;
                        fldv_set_audio_codec(s, astream, acodec, id);
                    } else if (!strcmp(key, "audiosamplerate") && acodec) {
                        acodec->sample_rate = num_val;
                    } else if (!strcmp(key, "audiosamplesize") && acodec) {
                        acodec->bits_per_coded_sample = num_val;
                    } else if (!strcmp(key, "stereo") && acodec) {
                        acodec->channels       = num_val + 1;
                        acodec->channel_layout = acodec->channels == 2 ?
                                                 AV_CH_LAYOUT_STEREO :
                                                 AV_CH_LAYOUT_MONO;
                    } else if (!strcmp(key, "width") && vcodec) {
                        vcodec->width = num_val;
                    } else if (!strcmp(key, "height") && vcodec) {
                        vcodec->height = num_val;
                    }
                }
            }
        }

        if (amf_type == AMF_DATA_TYPE_OBJECT && s->nb_streams == 1 &&
           ((!acodec && !strcmp(key, "audiocodecid")) ||
            (!vcodec && !strcmp(key, "videocodecid"))))
                s->ctx_flags &= ~AVFMTCTX_NOHEADER; //If there is either audio/video missing, codecid will be an empty object

        if (!strcmp(key, "duration")        ||
            !strcmp(key, "filesize")        ||
            !strcmp(key, "width")           ||
            !strcmp(key, "height")          ||
            !strcmp(key, "videodatarate")   ||
            !strcmp(key, "framerate")       ||
            !strcmp(key, "videocodecid")    ||
            !strcmp(key, "audiodatarate")   ||
            !strcmp(key, "audiosamplerate") ||
            !strcmp(key, "audiosamplesize") ||
            !strcmp(key, "stereo")          ||
            !strcmp(key, "audiocodecid")    ||
            !strcmp(key, "datastream"))
            return 0;

        s->event_flags |= AVFMT_EVENT_FLAG_METADATA_UPDATED;
        if (amf_type == AMF_DATA_TYPE_BOOL) {
            av_strlcpy(str_val, num_val > 0 ? "true" : "false",
                       sizeof(str_val));
            av_dict_set(&s->metadata, key, str_val, 0);
        } else if (amf_type == AMF_DATA_TYPE_NUMBER) {
            snprintf(str_val, sizeof(str_val), "%.f", num_val);
            av_dict_set(&s->metadata, key, str_val, 0);
        } else if (amf_type == AMF_DATA_TYPE_STRING)
            av_dict_set(&s->metadata, key, str_val, 0);
    }

    return 0;
}

#define TYPE_ONTEXTDATA 1
#define TYPE_UNKNOWN 2

static int fldv_read_metabody(AVFormatContext *s, int64_t next_pos)
{
    AMFDataType type;
    AVStream *stream, *astream, *vstream;
    AVStream av_unused *dstream;
    AVIOContext *ioc;
    int i;
    // only needs to hold the string "onMetaData".
    // Anything longer is something we don't want.
    char buffer[11];

    astream = NULL;
    vstream = NULL;
    dstream = NULL;
    ioc     = s->pb;

    // first object needs to be "onMetaData" string
    type = avio_r8(ioc);
    if (type != AMF_DATA_TYPE_STRING ||
        amf_get_string(ioc, buffer, sizeof(buffer)) < 0)
        return TYPE_UNKNOWN;

    if (!strcmp(buffer, "onTextData"))
        return TYPE_ONTEXTDATA;

    if (strcmp(buffer, "onMetaData") && strcmp(buffer, "onCuePoint"))
        return TYPE_UNKNOWN;

    // find the streams now so that amf_parse_object doesn't need to do
    // the lookup every time it is called.
    for (i = 0; i < s->nb_streams; i++) {
        stream = s->streams[i];
        if (stream->codec->codec_type == AVMEDIA_TYPE_VIDEO)
            vstream = stream;
        else if (stream->codec->codec_type == AVMEDIA_TYPE_AUDIO)
            astream = stream;
        else if (stream->codec->codec_type == AVMEDIA_TYPE_DATA)
            dstream = stream;
    }

    // parse the second object (we want a mixed array)
    if (amf_parse_object(s, astream, vstream, buffer, next_pos, 0) < 0)
        return -1;

    return 0;
}

/*
File Header在当前版本中总是由9个字节组成
    第1-3字节为文件标识（Signature），总为“FLV”（0x46 0x4C 0x56）
    第4字节为版本，目前为1（0x01）。
    第5个字节的前5位保留，必须为0。
    第5个字节的第6位表示是否存在音频Tag。
    第5个字节的第7位保留，必须为0。
    第5个字节的第8位表示是否存在视频Tag。
    第6-9个字节为UI32类型的值，表示从File Header开始到File Body开始的字节数，版本1中总为9。
*/
static int fldv_read_header(AVFormatContext *s)
{
    
    int offset, flags;

    FLDVContext *flv = s->priv_data;
    //av_log(NULL, AV_LOG_ERROR, "fldv_read_header3: %"PRId64", file_offset:%"PRId64".\n", s->pb->bytes_read, flv->file_offset);
    
    fldv_RC4DecodeData(flv, s->pb->buf_ptr, s->pb->bytes_read - (s->pb->buf_ptr - s->pb->buffer), s->pb->buf_ptr - s->pb->buffer);
    flv->file_offset += s->pb->bytes_read - (s->pb->buf_ptr - s->pb->buffer);
    //av_log(NULL, AV_LOG_ERROR, "flv->file_offset:%"PRId64".\n", flv->file_offset);

    flv->opaque = s->pb->opaque;
    flv->read_packet = s->pb->read_packet;
    flv->write_packet = s->pb->write_packet;
    flv->seek = s->pb->seek;

    s->pb->opaque = flv;
    s->pb->read_packet = fldv_decode_read;
    s->pb->write_packet = fldv_decode_write;
    s->pb->seek = fldv_decode_seek;

    avio_skip(s->pb, 4);
    flags = avio_r8(s->pb);
    //av_log(NULL, AV_LOG_ERROR, "fldv_read_header, flags:%d.\n", flags);

    s->ctx_flags |= AVFMTCTX_NOHEADER;

    if (flags & FLV_HEADER_FLAG_HASVIDEO)
        if (!create_stream(s, AVMEDIA_TYPE_VIDEO))
            return AVERROR(ENOMEM);
    if (flags & FLV_HEADER_FLAG_HASAUDIO)
        if (!create_stream(s, AVMEDIA_TYPE_AUDIO))
            return AVERROR(ENOMEM);
    // Flag doesn't indicate whether or not there is script-data present. Must
    // create that stream if it's encountered.

    offset = avio_rb32(s->pb);
    //av_log(NULL, AV_LOG_ERROR, "fldv_read_header, offset:%d.\n", offset);
    avio_seek(s->pb, offset, SEEK_SET);
    avio_skip(s->pb, 4);

    s->start_time = 0;

    return 0;
}

static int fldv_read_close(AVFormatContext *s)
{
    //av_log(NULL, AV_LOG_ERROR, "fldv_read_close");
    int i;
    FLDVContext *flv = s->priv_data;
    for (i=0; i<FLV_STREAM_TYPE_NB; i++)
        av_freep(&flv->new_extradata[i]);

    flv->opaque = NULL;
    flv->read_packet = NULL;
    flv->write_packet = NULL;
    flv->seek = NULL;

    s->pb->opaque = flv->opaque;
    s->pb->read_packet = flv->read_packet;
    s->pb->write_packet = flv->write_packet;
    s->pb->seek = flv->seek;

    return 0;
}

static int fldv_get_extradata(AVFormatContext *s, AVStream *st, int size)
{
    av_freep(&st->codec->extradata);
    if (ff_get_extradata(st->codec, s->pb, size) < 0)
        return AVERROR(ENOMEM);
    return 0;
}

static int fldv_queue_extradata(FLDVContext *flv, AVIOContext *pb, int stream,
                               int size)
{
    av_free(flv->new_extradata[stream]);
    flv->new_extradata[stream] = av_mallocz(size +
                                            FF_INPUT_BUFFER_PADDING_SIZE);
    if (!flv->new_extradata[stream])
        return AVERROR(ENOMEM);
    flv->new_extradata_size[stream] = size;
    avio_read(pb, flv->new_extradata[stream], size);
    return 0;
}
static void clear_index_entries(AVFormatContext *s, int64_t pos)
{
    int i, j, out;
    av_log(s, AV_LOG_WARNING,
           "Found invalid index entries, clearing the index.\n");
    for (i = 0; i < s->nb_streams; i++) {
        AVStream *st = s->streams[i];
        /* Remove all index entries that point to >= pos */
        out = 0;
        for (j = 0; j < st->nb_index_entries; j++)
            if (st->index_entries[j].pos < pos)
                st->index_entries[out++] = st->index_entries[j];
        st->nb_index_entries = out;
    }
}

/*
static int amf_skip_tag(AVIOContext *pb, AMFDataType type)
{
    int nb = -1, ret, parse_name = 1;

    switch (type) {
    case AMF_DATA_TYPE_NUMBER:
        avio_skip(pb, 8);
        break;
    case AMF_DATA_TYPE_BOOL:
        avio_skip(pb, 1);
        break;
    case AMF_DATA_TYPE_STRING:
        avio_skip(pb, avio_rb16(pb));
        break;
    case AMF_DATA_TYPE_ARRAY:
        parse_name = 0;
    case AMF_DATA_TYPE_MIXEDARRAY:
        nb = avio_rb32(pb);
    case AMF_DATA_TYPE_OBJECT:
        while(!pb->eof_reached && (nb-- > 0 || type != AMF_DATA_TYPE_ARRAY)) {
            if (parse_name) {
                int size = avio_rb16(pb);
                if (!size) {
                    avio_skip(pb, 1);
                    break;
                }
                avio_skip(pb, size);
            }
            if ((ret = amf_skip_tag(pb, avio_r8(pb))) < 0)
                return ret;
        }
        break;
    case AMF_DATA_TYPE_NULL:
    case AMF_DATA_TYPE_OBJECT_END:
        break;
    default:
        return AVERROR_INVALIDDATA;
    }
    return 0;
}


static int flv_data_packet(AVFormatContext *s, AVPacket *pkt,
                           int64_t dts, int64_t next)
{
    AVIOContext *pb = s->pb;
    AVStream *st    = NULL;
    char buf[20];
    int ret = AVERROR_INVALIDDATA;
    int i, length = -1;

    switch (avio_r8(pb)) {
    case AMF_DATA_TYPE_MIXEDARRAY:
        avio_seek(pb, 4, SEEK_CUR);
    case AMF_DATA_TYPE_OBJECT:
        break;
    default:
        goto skip;
    }

    while ((ret = amf_get_string(pb, buf, sizeof(buf))) > 0) {
        AMFDataType type = avio_r8(pb);
        if (type == AMF_DATA_TYPE_STRING && !strcmp(buf, "text")) {
            length = avio_rb16(pb);
            ret    = av_get_packet(pb, pkt, length);
            if (ret < 0)
                goto skip;
            else
                break;
        } else {
            if ((ret = amf_skip_tag(pb, type)) < 0)
                goto skip;
        }
    }

    if (length < 0) {
        ret = AVERROR_INVALIDDATA;
        goto skip;
    }

    for (i = 0; i < s->nb_streams; i++) {
        st = s->streams[i];
        if (st->codec->codec_type == AVMEDIA_TYPE_DATA)
            break;
    }

    if (i == s->nb_streams) {
        st = create_stream(s, AVMEDIA_TYPE_DATA);
        if (!st)
            return AVERROR(ENOMEM);
        st->codec->codec_id = AV_CODEC_ID_TEXT;
    }

    pkt->dts  = dts;
    pkt->pts  = dts;
    pkt->size = ret;

    pkt->stream_index = st->index;
    pkt->flags       |= AV_PKT_FLAG_KEY;

skip:
    avio_seek(s->pb, next + 4, SEEK_SET);

    return ret;
}

*/

static int fldv_read_packet(AVFormatContext *s, AVPacket *pkt)
{
    //av_log(NULL, AV_LOG_ERROR, "fldv_read_packet\n");
    FLDVContext *flv = s->priv_data;
    int ret, i, type, size, flags;
    int stream_type=-1;
    int64_t next, pos, meta_pos;
    int64_t dts, pts = AV_NOPTS_VALUE;
    int av_uninit(channels);
    int av_uninit(sample_rate);
    AVStream *st    = NULL;
    //av_log(NULL, AV_LOG_ERROR, "fldv_read_packet\n");

    /* pkt size is repeated at end. skip it */
    for (;; avio_skip(s->pb, 4)) {
        pos  = avio_tell(s->pb);
        type = (avio_r8(s->pb) & 0x1F);
        size = avio_rb24(s->pb);
        dts  = avio_rb24(s->pb);
        dts |= avio_r8(s->pb) << 24;
        av_dlog(s, "type:%d, size:%d, dts:%"PRId64" pos:%"PRId64"\n", type, size, dts, avio_tell(s->pb));
        if (avio_feof(s->pb))
            return AVERROR_EOF;
        avio_skip(s->pb, 3); /* stream id, always 0 */
        flags = 0;

        if (flv->validate_next < flv->validate_count) {
            int64_t validate_pos = flv->validate_index[flv->validate_next].pos;
            if (pos == validate_pos) {
                if (FFABS(dts - flv->validate_index[flv->validate_next].dts) <=
                    VALIDATE_INDEX_TS_THRESH) {
                    flv->validate_next++;
                } else {
                    clear_index_entries(s, validate_pos);
                    flv->validate_count = 0;
                }
            } else if (pos > validate_pos) {
                clear_index_entries(s, validate_pos);
                flv->validate_count = 0;
            }
        }

        if (size == 0)
            continue;

        next = size + avio_tell(s->pb);

        if (type == FLV_TAG_TYPE_AUDIO) {
            stream_type = FLV_STREAM_TYPE_AUDIO;
            flags    = avio_r8(s->pb);
            size--;
        } else if (type == FLV_TAG_TYPE_VIDEO) {
            stream_type = FLV_STREAM_TYPE_VIDEO;
            flags    = avio_r8(s->pb);
            size--;
            if ((flags & FLV_VIDEO_FRAMETYPE_MASK) == FLV_FRAME_VIDEO_INFO_CMD)
                goto skip;
        } else if (type == FLV_TAG_TYPE_META) {
            stream_type=FLV_STREAM_TYPE_DATA;
            if (size > 13 + 1 + 4 && dts == 0) { // Header-type metadata stuff
                meta_pos = avio_tell(s->pb);
                if (fldv_read_metabody(s, next) <= 0) {
                    goto skip;
                }
                avio_seek(s->pb, meta_pos, SEEK_SET);
            }
        } else {
            av_log(s, AV_LOG_DEBUG,
                   "Skipping flv packet: type %d, size %d, flags %d.\n",
                   type, size, flags);
skip:
            avio_seek(s->pb, next, SEEK_SET);
            continue;
        }

        /* skip empty data packets */
        if (!size)
            continue;

        /* now find stream */
        for (i = 0; i < s->nb_streams; i++) {
            st = s->streams[i];
            if (stream_type == FLV_STREAM_TYPE_AUDIO) {
                if (st->codec->codec_type == AVMEDIA_TYPE_AUDIO &&
                    (s->audio_codec_id || fldv_same_audio_codec(st->codec, flags)))
                    break;
            } else if (stream_type == FLV_STREAM_TYPE_VIDEO) {
                if (st->codec->codec_type == AVMEDIA_TYPE_VIDEO &&
                    (s->video_codec_id || fldv_same_video_codec(st->codec, flags)))
                    break;
            } else if (stream_type == FLV_STREAM_TYPE_DATA) {
                if (st->codec->codec_type == AVMEDIA_TYPE_DATA)
                    break;
            }
        }
        if (i == s->nb_streams) {
            static const enum AVMediaType stream_types[] = {AVMEDIA_TYPE_VIDEO, AVMEDIA_TYPE_AUDIO, AVMEDIA_TYPE_DATA};
            av_log(s, AV_LOG_WARNING, "Stream discovered after head already parsed\n");
            st = create_stream(s, stream_types[stream_type]);
            if (!st)
                return AVERROR(ENOMEM);

        }
        av_dlog(s, "%d %X %d \n", stream_type, flags, st->discard);

        if ((flags & FLV_VIDEO_FRAMETYPE_MASK) == FLV_FRAME_KEY ||
            stream_type == FLV_STREAM_TYPE_AUDIO)
            av_add_index_entry(st, pos, dts, size, 0, AVINDEX_KEYFRAME);

        if (  (st->discard >= AVDISCARD_NONKEY && !((flags & FLV_VIDEO_FRAMETYPE_MASK) == FLV_FRAME_KEY || (stream_type == FLV_STREAM_TYPE_AUDIO)))
            ||(st->discard >= AVDISCARD_BIDIR  &&  ((flags & FLV_VIDEO_FRAMETYPE_MASK) == FLV_FRAME_DISP_INTER && (stream_type == FLV_STREAM_TYPE_VIDEO)))
            || st->discard >= AVDISCARD_ALL
        ) {
            avio_seek(s->pb, next, SEEK_SET);
            continue;
        }
        break;
    }

    // if not streamed and no duration from metadata then seek to end to find
    // the duration from the timestamps
    if (s->pb->seekable && (!s->duration || s->duration == AV_NOPTS_VALUE) && !flv->searched_for_end) {
        int size;
        const int64_t pos   = avio_tell(s->pb);
		// Read the last 4 bytes of the file, this should be the size of the
        // previous FLV tag. Use the timestamp of its payload as duration.
        int64_t fsize       = avio_size(s->pb);
retry_duration:
        avio_seek(s->pb, fsize - 4, SEEK_SET);
        size = avio_rb32(s->pb);
		// Seek to the start of the last FLV tag at position (fsize - 4 - size)
        // but skip the byte indicating the type.
        avio_seek(s->pb, fsize - 3 - size, SEEK_SET);
        if (size == avio_rb24(s->pb) + 11) {
            uint32_t ts = avio_rb24(s->pb);
            ts         |= avio_r8(s->pb) << 24;
            if (ts)
                s->duration = ts * (int64_t)AV_TIME_BASE / 1000;
            else if (fsize >= 8 && fsize - 8 >= size) {
                fsize -= size+4;
                goto retry_duration;
            }
        }

        avio_seek(s->pb, pos, SEEK_SET);
        flv->searched_for_end = 1;
    }

    if (stream_type == FLV_STREAM_TYPE_AUDIO) {
        int bits_per_coded_sample;
        channels = (flags & FLV_AUDIO_CHANNEL_MASK) == FLV_STEREO ? 2 : 1;
        sample_rate = 44100 << ((flags & FLV_AUDIO_SAMPLERATE_MASK) >>
                                FLV_AUDIO_SAMPLERATE_OFFSET) >> 3;
        bits_per_coded_sample = (flags & FLV_AUDIO_SAMPLESIZE_MASK) ? 16 : 8;
        if (!st->codec->channels || !st->codec->sample_rate ||
            !st->codec->bits_per_coded_sample) {
            st->codec->channels              = channels;
            st->codec->channel_layout        = channels == 1
                                               ? AV_CH_LAYOUT_MONO
                                               : AV_CH_LAYOUT_STEREO;
            st->codec->sample_rate           = sample_rate;
            st->codec->bits_per_coded_sample = bits_per_coded_sample;
        }
        if (!st->codec->codec_id) {
            fldv_set_audio_codec(s, st, st->codec,
                                flags & FLV_AUDIO_CODECID_MASK);
            flv->last_sample_rate =
            sample_rate           = st->codec->sample_rate;
            flv->last_channels    =
            channels              = st->codec->channels;
        } else {
            AVCodecContext ctx = {0};
            ctx.sample_rate = sample_rate;
            fldv_set_audio_codec(s, st, &ctx, flags & FLV_AUDIO_CODECID_MASK);
            sample_rate = ctx.sample_rate;
        }
    } else if (stream_type == FLV_STREAM_TYPE_VIDEO) {
        size -= fldv_set_video_codec(s, st, flags & FLV_VIDEO_CODECID_MASK, 1);
    }

    if (st->codec->codec_id == AV_CODEC_ID_AAC ||
        st->codec->codec_id == AV_CODEC_ID_H264 ||
        st->codec->codec_id == AV_CODEC_ID_MPEG4) {
        int type = avio_r8(s->pb);
        size--;
        if (st->codec->codec_id == AV_CODEC_ID_H264 || st->codec->codec_id == AV_CODEC_ID_MPEG4) {
            // sign extension
            int32_t cts = (avio_rb24(s->pb) + 0xff800000) ^ 0xff800000;
            pts = dts + cts;
            if (cts < 0) { // dts might be wrong
                if (!flv->wrong_dts)
                    av_log(s, AV_LOG_WARNING,
                        "Negative cts, previous timestamps might be wrong.\n");
                flv->wrong_dts = 1;
            } else if (FFABS(dts - pts) > 1000*60*15) {
                av_log(s, AV_LOG_WARNING,
                       "invalid timestamps %"PRId64" %"PRId64"\n", dts, pts);
                dts = pts = AV_NOPTS_VALUE;
            }
        }
        if (type == 0 && (!st->codec->extradata || st->codec->codec_id == AV_CODEC_ID_AAC ||
            st->codec->codec_id == AV_CODEC_ID_H264)) {
            AVDictionaryEntry *t;

            if (st->codec->extradata) {
                if ((ret = fldv_queue_extradata(flv, s->pb, stream_type, size)) < 0)
                    return ret;
                ret = AVERROR(EAGAIN);
                goto leave;
            }
            if ((ret = fldv_get_extradata(s, st, size)) < 0)
                return ret;

            /* Workaround for buggy Omnia A/XE encoder */
            t = av_dict_get(s->metadata, "Encoder", NULL, 0);
            if (st->codec->codec_id == AV_CODEC_ID_AAC && t && !strcmp(t->value, "Omnia A/XE"))
                st->codec->extradata_size = 2;

            if (st->codec->codec_id == AV_CODEC_ID_AAC && 0) {
                MPEG4AudioConfig cfg;

                if (avpriv_mpeg4audio_get_config(&cfg, st->codec->extradata,
                                             st->codec->extradata_size * 8, 1) >= 0) {
                st->codec->channels       = cfg.channels;
                st->codec->channel_layout = 0;
                if (cfg.ext_sample_rate)
                    st->codec->sample_rate = cfg.ext_sample_rate;
                else
                    st->codec->sample_rate = cfg.sample_rate;
                av_dlog(s, "mp4a config channels %d sample rate %d\n",
                        st->codec->channels, st->codec->sample_rate);
                }
            }

            ret = AVERROR(EAGAIN);
            goto leave;
        }
    }

    /* skip empty data packets */
    if (!size) {
        ret = AVERROR(EAGAIN);
        goto leave;
    }

    ret = av_get_packet(s->pb, pkt, size);
    if (ret < 0)
        return ret;
    pkt->dts          = dts;
    pkt->pts          = pts == AV_NOPTS_VALUE ? dts : pts;
    pkt->stream_index = st->index;
    if (flv->new_extradata[stream_type]) {
        uint8_t *side = av_packet_new_side_data(pkt, AV_PKT_DATA_NEW_EXTRADATA,
                                                flv->new_extradata_size[stream_type]);
        if (side) {
            memcpy(side, flv->new_extradata[stream_type],
                   flv->new_extradata_size[stream_type]);
            av_freep(&flv->new_extradata[stream_type]);
            flv->new_extradata_size[stream_type] = 0;
        }
    }
    if (stream_type == FLV_STREAM_TYPE_AUDIO &&
                    (sample_rate != flv->last_sample_rate ||
                     channels    != flv->last_channels)) {
        flv->last_sample_rate = sample_rate;
        flv->last_channels    = channels;
        ff_add_param_change(pkt, channels, 0, sample_rate, 0, 0);
    }

    if (    stream_type == FLV_STREAM_TYPE_AUDIO ||
            ((flags & FLV_VIDEO_FRAMETYPE_MASK) == FLV_FRAME_KEY) ||
            stream_type == FLV_STREAM_TYPE_DATA)
        pkt->flags |= AV_PKT_FLAG_KEY;

leave:
    avio_skip(s->pb, 4);
    return ret;
}

static int fldv_read_seek(AVFormatContext *s, int stream_index,
                         int64_t ts, int flags)
{
    FLDVContext *flv = s->priv_data;
    flv->validate_count = 0;
    return avio_seek_time(s->pb, stream_index, ts, flags);
}

#define OFFSET(x) offsetof(FLDVContext, x)
#define VD AV_OPT_FLAG_VIDEO_PARAM | AV_OPT_FLAG_DECODING_PARAM
static const AVOption options[] = {
    { "flv_metadata", "Allocate streams according to the onMetaData array", OFFSET(trust_metadata), AV_OPT_TYPE_INT, { .i64 = 0 }, 0, 1, VD },
    { NULL }
};

static const AVClass fldv_class = {
    .class_name = "fldvdec",
    .item_name  = av_default_item_name,
    .option     = options,
    .version    = LIBAVUTIL_VERSION_INT,
};

AVInputFormat ff_fldv_demuxer = {
    .name           = "fldv",
    .long_name      = NULL_IF_CONFIG_SMALL("FLDV (Dolit Enc Video)"),
    .priv_data_size = sizeof(FLDVContext),
    .read_probe     = fldv_probe,
    .read_header    = fldv_read_header,
    .read_packet    = fldv_read_packet,
    .read_seek      = fldv_read_seek,
    .read_close     = fldv_read_close,
    .extensions     = "flv,f4v,frmv,fdv,fldv",
    .priv_class     = &fldv_class,
};
