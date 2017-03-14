//=======================================================================================
//                 点量软件，致力于做最专业的通用软件库，节省您的开发时间
// 
//	Copyright:	Copyright (c) Dolit.cn 
//  版权所有：	点量软件有限公司
//
//              如果您是个人作为非商业目的使用，您可以自由、免费的使用点量软件库和演示程序，
//              也期待收到您反馈的意见和建议，共同改进点量产品
//              如果您是商业使用，那么您需要联系作者申请产品的商业授权。
//              点量软件库所有演示程序的代码对外公开，软件库的代码只限付费用户个人使用。
//        
//  官方网站：  http://www.dolit.cn   http://blog.dolit.cn 
//
//======================================================================================= 

#include <jni.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include <stdio.h>

#include "MD5_Hash.h"
#include "cert.h"
#define LOG_TAG "DolitMediaPlayer/JNI/cert"


/*

packageName：cn.dolit.encplayer.demo
public static final String strPlayEngineKey1 = "dolitPlayerEngine";		
public static final String strPlayEngineKey2 = "PoweredBy-www.Dolit.cn";
 * Debug : 99:72:1D:A8:5D:E4:0C:5A:01:01:48:FC:18:E6:FE:3E

// cert中需要使用，要计算得出，使用工具软件
static unsigned char RC4Char[] = {0xea, 0xee, 0x52, 0xde, 0xbe, 0xca, 0x6f, 0x96, 0x61, 0x5, 0xb4, 0x2e, 0x57, 0x22, 0x7f, 0xdf, 0xbe, 0x1c, 0xa0, 0xf4, 0x63, 0x36, 0x9e, 0x9d, 0xc4, 0xba, 0x2f, 0x7d, 0xbd, 0xc3, 0x2c, 0x70};
static unsigned char RC4Char2[] = {0xbc, 0xbf, 0xe7, 0xfb, 0x46, 0x23, 0x9e, 0xbf, 0x7a, 0xd5, 0xd7, 0x56, 0x8b, 0x70, 0xc1, 0xf7, 0x0, 0x9a, 0xcf, 0xed, 0xcf, 0xaf, 0xf, 0xb5, 0xa5, 0xa1, 0x9c, 0x88, 0x93, 0x5d, 0x33, 0x1d};
==========================================================
*/

int is_equal(char* buf)
{
	unsigned char RC4Char[] = {0xea, 0xee, 0x52, 0xde, 0xbe, 0xca, 0x6f, 0x96, 0x61, 0x5, 0xb4, 0x2e, 0x57, 0x22, 0x7f, 0xdf, 0xbe, 0x1c, 0xa0, 0xf4, 0x63, 0x36, 0x9e, 0x9d, 0xc4, 0xba, 0x2f, 0x7d, 0xbd, 0xc3, 0x2c, 0x70};
    unsigned char RC4Char2[] = {0xbc, 0xbf, 0xe7, 0xfb, 0x46, 0x23, 0x9e, 0xbf, 0x7a, 0xd5, 0xd7, 0x56, 0x8b, 0x70, 0xc1, 0xf7, 0x0, 0x9a, 0xcf, 0xed, 0xcf, 0xaf, 0xf, 0xb5, 0xa5, 0xa1, 0x9c, 0x88, 0x93, 0x5d, 0x33, 0x1d};
	if (memcmp(buf, RC4Char2, 32) == 0
           || memcmp(buf, RC4Char, 32) == 0)
		return 0;

	return 1;
}

char g_cert[33] = {0};

int GetAPKInfo(JNIEnv *env, jobject context, char** strPackageName, int * packageNameLen, char** apkInfo, int * outApkInfoLen)
{
	jclass cls = (*env)->GetObjectClass(env, context);
	if (!cls)
	{
		//LOGE(" obj class is null ");
		return 0;
	}
	//jclass cls = env->FindClass("android/content/ContextWrapper");

	// this.getPackageManager();
	jmethodID mid =  (*env)->GetMethodID(env, cls, "getPackageManager", "()Landroid/content/pm/PackageManager;");
	if (!mid)
	{
		//LOGE(" getPackageManager method is null ");
		return 0;
	}
	jobject pm = (*env)->CallObjectMethod(env, context, mid);
	if (!pm)
	{
		//LOGE(" pm  is null ");
		return 0;
	}

	// this.getPackageName();
	mid = (*env)->GetMethodID(env, cls, "getPackageName", "()Ljava/lang/String;");
	if (!mid)
	{
		//LOGE(" getPackageName method is null ");
		return 0;
	}
    jstring packageName = (jstring)(*env)->CallObjectMethod(env, context, mid);
	if (!packageName)
	{
		//LOGE(" packageName is null ");
		return 0;
	}

    // packageManager->getPackageInfo(packageName, GET_SIGNATURES);
	cls = (*env)->GetObjectClass(env, pm);
	if (!cls)
	{
		//LOGE(" GetObjectClass getPackageInfo is null ");
		return 0;
	}
	mid  = (*env)->GetMethodID(env, cls, "getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
	if (!mid)
	{
		//LOGE(" GetMethodID getPackageInfo is null ");
		return 0;
	}
	jobject packageInfo = (*env)->CallObjectMethod(env, pm, mid, packageName, 0x40); //GET_SIGNATURES = 64;
	if (!packageInfo)
	{
		//LOGE(" packageInfo is null ");
		return 0;
	}
	cls = (*env)->GetObjectClass(env, packageInfo);
	if (!cls)
	{
		//LOGE(" GetObjectClass signatures is null ");
		return 0;
	}
	jfieldID fid = (*env)->GetFieldID(env, cls, "signatures", "[Landroid/content/pm/Signature;");
	if (!fid)
	{
		//LOGE(" GetFieldID signatures is null ");
		return 0;
	}

	jobjectArray signatures = (jobjectArray)(*env)->GetObjectField(env, packageInfo, fid);
	if (!signatures)
	{
		//LOGE(" fid signatures is null ");
		return 0;
	}

	jobject sig = (*env)->GetObjectArrayElement(env, signatures, 0);
	if (!sig)
	{
		//LOGE(" GetObjectArrayElement signatures is null ");
		(*env)->DeleteLocalRef(env, sig);
		return 0;
	}

	cls = (*env)->GetObjectClass(env, sig);
	mid = (*env)->GetMethodID(env, cls, "toByteArray", "()[B");
	jbyteArray byts = (jbyteArray) (*env)->CallObjectMethod(env, sig, mid);
	if (!byts)
	{
		//LOGE(" byts is null ");
		(*env)->DeleteLocalRef(env, sig);
		return 0;
	}
	jsize len = (*env)->GetArrayLength(env, byts);
	if (len <= 0 || len > 1024 * 1024 * 5)
	{
		//LOGE(" len is 0 : %d ", len);
		(*env)->DeleteLocalRef(env, sig);
		return 0;
	}
	char * data = (char*)(*env)->GetByteArrayElements(env, byts, 0);
	if (!data)
	{
		(*env)->DeleteLocalRef(env, sig);
		(*env)->ReleaseByteArrayElements(env, byts, (jbyte*)data, 0);
		return 0;
	}

	*apkInfo = (char*)malloc(len);
	*outApkInfoLen = len;
	memcpy(*apkInfo, data, *outApkInfoLen);

	(*env)->ReleaseByteArrayElements(env, byts, (jbyte*)data, 0);
	(*env)->DeleteLocalRef(env, sig);

	const char *pName = (*env)->GetStringUTFChars(env, packageName, 0);
	if (pName && strlen(pName) > 0)
	{
		*packageNameLen = strlen(pName);
		*strPackageName = (char*)malloc(*packageNameLen);
		memcpy(*strPackageName, pName, *packageNameLen);
	}
    (*env)->ReleaseStringUTFChars(env, packageName, pName);
	return 1;
}

const char g_hexArray[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

void MD5Apk(const unsigned char * src, unsigned int len, char * outBuf)
{
    unsigned char buf[16] = {0};
    md5it(buf, src, len);

    int j = 0, k = 0;
    for (j = 0; j < 16; j++)
    {
      int m = buf[j];
      outBuf[k++] = g_hexArray[(0xF & ((m >> 4) & 0x7FFFFFFF))];
      outBuf[k++] = g_hexArray[(m & 0xF)];
    }
}

/****************************************************************************
 * Dolit Encrypt 
 ****************************************************************************/

#define SWAP(a, b) ((a) ^= (b), (b) ^= (a), (a) ^= (b))

void InitRC4Key(hls_rc4_t *rc4, const char * pszKey, int keyLen)
{
    if (!rc4 || !pszKey || keyLen <= 0)
        return;
     if (rc4->bInit == 1)
        return;

    memset(rc4->sbox,0,256);
    memset(rc4->key,0,256);

    int n = 0;
    int m = 0;
    for (m = 0;  m < 256; m++)  /* Initialize the key sequence */
    {
        *(rc4->key + m)= *(pszKey + (m % keyLen));
        *(rc4->sbox + m) = m;
    }

    for (m=0; m < 256; m++)
    {
        n = (n + *(rc4->sbox+m) + *(rc4->key + m)) &0xff;
        SWAP(*(rc4->sbox + m),*(rc4->sbox + n));
    }
    rc4->bInit = 1;
}

void Encrypt(unsigned char *pszText, int textLen, hls_rc4_t *rc4) 
{
    if (rc4 == NULL || !rc4->bInit)
        return;

    int ilen = textLen;
    int i = 0;
    for (i = 0; i < ilen; i++)
    {
        int h = ((int)i + 1) &0xff;
        int j = (h + rc4->key[h] + *(rc4->sbox + h)) &0xff;
        int k = *(rc4->sbox + ((*(rc4->sbox + h) + *(rc4->sbox + j)) &0xff ));

        if(k == rc4->key[2])       /* avoid '\0' beween the decoded text; */
            k = 0;

        *(pszText + i) ^=  k;  
    }
}

void toLowerStr(char * ch, int len)
{
	for (int i = 0; i < len; ++i)
	{
		ch[i] = tolower(ch[i]);
	}
}

/*
 * Class:     cn_dolit_siteparser_DolitSiteParser
 * Method:    Init
 * Signature: (Ljava/lang/String;Ljava/lang/String;Landroid/content/Context;)I
 */
JNIEXPORT jint JNICALL IjkMediaPlayer_native_KeyInit
  (JNIEnv *env, jobject obj, jstring key1, jstring key2, jobject context)
{
	const char *pKey1 = (*env)->GetStringUTFChars(env, key1,  0);
	const char *pKey2 = (*env)->GetStringUTFChars(env, key2,  0);

	if (!pKey1 || !pKey2 )
		return 1;

	char * strPackageName = NULL;
	char * apkInfo = NULL;
	int packageNameLen = 0, apkInfoLen = 0;
	int nGet = GetAPKInfo(env, context, &strPackageName, &packageNameLen, &apkInfo, &apkInfoLen );
	if (nGet != 1 || strPackageName == NULL || apkInfo == NULL || packageNameLen == 0 || apkInfoLen == 0)
	{
		//LOGD(" cannot get apk info, apkInfo : %s , name: %s ", apkInfo, strPackageName);
		(*env)->ReleaseStringUTFChars(env, key1,  pKey1);
		(*env)->ReleaseStringUTFChars(env, key2,  pKey2);

		if (strPackageName)
			free(strPackageName);
		if (apkInfo)
			free(apkInfo);
		return 1;
	}

	int nRet = 1;
	char md5[33] = {0};
	MD5Apk((unsigned char*)apkInfo, apkInfoLen, md5);
	toLowerStr(md5, 32);

	char seed[1024] = {0};
	char * p = seed;
	strcpy(p, md5);
	p += 32;

	strcpy(p, "P1x3a49axW1XuD1u7H");
	p += strlen("P1x3a49axW1XuD1u7H");

	toLowerStr(strPackageName, packageNameLen);

	memcpy(p, strPackageName, packageNameLen);
	p += packageNameLen;

	strcpy(p,  "1&1&9x276543$}a");
	p+= strlen("1&1&9x276543$}a");

	strcpy(p, pKey1);
	p += strlen(pKey1);

	strcpy(p, ".");
	p += 1;
	strcpy(p, pKey2);
	p += strlen(pKey2);

	int seedLen = p - seed;

	unsigned char buf[16] = {0};
    md5it(buf, (const unsigned char *)seed, (unsigned int)seedLen);
    for (int i = 0; i < 16; i++)
    {
        sprintf(g_cert + (i*2), "%02x", buf[i]);
    }

	if (strPackageName)
		free(strPackageName);
	if (apkInfo)
		free(apkInfo);

	(*env)->ReleaseStringUTFChars(env, key1,  pKey1);
	(*env)->ReleaseStringUTFChars(env, key2,  pKey2);

	char certBuf[32] = {0};
	memcpy(certBuf, g_cert, 32);

	g_rc4.bInit = 0;
	InitRC4Key(&g_rc4, "xuvt327haww3xAcao083u/MP4.60@1", 30);
	Encrypt((unsigned char*)certBuf, 32, &g_rc4);

	if(is_equal(certBuf) != 0)
		nRet = -4;
	else
		nRet = 0;

	return nRet;
}

