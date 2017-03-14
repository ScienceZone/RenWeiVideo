/*
 * ijkplayer_android.c
 *
 * Copyright (c) 2013 Zhang Rui <bbcallen@gmail.com>
 *
 * This file is part of ijkPlayer.
 *
 * ijkPlayer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * ijkPlayer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ijkPlayer; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

#include "ijkplayer_android.h"

#include <assert.h>
#include "ijksdl/android/ijksdl_android.h"
#include "../ff_fferror.h"
#include "../ff_ffplay.h"
#include "../ijkplayer_internal.h"
#include "../pipeline/ffpipeline_ffplay.h"
#include "pipeline/ffpipeline_android.h"

IjkMediaPlayer *ijkmp_android_create(int(*msg_loop)(void*))
{
    IjkMediaPlayer *mp = ijkmp_create(msg_loop);
    if (!mp)
        goto fail;

    mp->ffplayer->vout = SDL_VoutAndroid_CreateForAndroidSurface();
    if (!mp->ffplayer->vout)
        goto fail;

    mp->ffplayer->aout = SDL_AoutAndroid_CreateForAudioTrack();
    if (!mp->ffplayer->aout)
        goto fail;

    mp->ffplayer->pipeline = ffpipeline_create_from_android(mp->ffplayer);
    if (!mp->ffplayer->pipeline)
        goto fail;

    ffpipeline_set_vout(mp->ffplayer->pipeline, mp->ffplayer->vout);

    return mp;

    fail:
    ijkmp_dec_ref_p(&mp);
    return NULL;
}

void ijkmp_android_set_surface_l(JNIEnv *env, IjkMediaPlayer *mp, jobject android_surface)
{
    if (!mp || !mp->ffplayer || !mp->ffplayer->vout)
        return;

    SDL_VoutAndroid_SetAndroidSurface(env, mp->ffplayer->vout, android_surface);
    ffpipeline_set_surface(env, mp->ffplayer->pipeline, android_surface);
}

void ijkmp_android_set_surface(JNIEnv *env, IjkMediaPlayer *mp, jobject android_surface)
{
    if (!mp)
        return;

    MPTRACE("dlmp_set_android_surface(surface=%p)", (void*)android_surface);
    pthread_mutex_lock(&mp->mutex);
    ijkmp_android_set_surface_l(env, mp, android_surface);
    pthread_mutex_unlock(&mp->mutex);
    MPTRACE("dlmp_set_android_surface(surface=%p)=void", (void*)android_surface);
}

void ijkmp_android_set_volume(JNIEnv *env, IjkMediaPlayer *mp, float left, float right)
{
    if (!mp)
        return;

    MPTRACE("dlmp_android_set_volume(%f, %f)", left, right);
    pthread_mutex_lock(&mp->mutex);

    if (mp && mp->ffplayer && mp->ffplayer->aout) {
        SDL_AoutSetStereoVolume(mp->ffplayer->aout, left, right);
    }

    pthread_mutex_unlock(&mp->mutex);
    MPTRACE("dlmp_android_set_volume(%f, %f)=void", left, right);
}

void ijkmp_android_set_mediacodec_select_callback(IjkMediaPlayer *mp, bool (*callback)(void *opaque, ijkmp_mediacodecinfo_context *mcc), void *opaque)
{
    if (!mp)
        return;

    MPTRACE("dlmp_android_set_mediacodec_select_callback()");
    pthread_mutex_lock(&mp->mutex);

    if (mp && mp->ffplayer && mp->ffplayer->pipeline) {
        ffpipeline_set_mediacodec_select_callback(mp->ffplayer->pipeline, callback, opaque);
    }

    pthread_mutex_unlock(&mp->mutex);
    MPTRACE("dlmp_android_set_mediacodec_select_callback()=void");
}

void ijkmp_android_set_mediacodec_enabled(IjkMediaPlayer *mp, bool enabled)
{
    if (!mp)
         return;

    MPTRACE("dlmp_android_set_mediacodec_enabled(%d)", enabled ? 1 : 0);
    pthread_mutex_lock(&mp->mutex);

    if (mp && mp->ffplayer && mp->ffplayer->pipeline) {
        ffpipeline_set_mediacodec_enabled(mp->ffplayer->pipeline, enabled);
    }

    pthread_mutex_unlock(&mp->mutex);
    MPTRACE("dlmp_android_set_mediacodec_enabled()=void");
}
