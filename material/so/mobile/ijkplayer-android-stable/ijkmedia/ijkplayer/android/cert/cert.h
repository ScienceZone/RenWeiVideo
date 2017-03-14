

#ifndef ___DOLTT_CERT_H___
#define ___DOLTT_CERT_H___  

JNIEXPORT jint JNICALL IjkMediaPlayer_native_KeyInit
  (JNIEnv *env, jobject obj, jstring key1, jstring key2, jobject context);


extern char g_cert[];

typedef struct hls_rc4_s 
{
    unsigned char sbox[256];      /* Encryption array   */
    unsigned char key[256];      /* Numeric key values  */
    int bInit;
}hls_rc4_t;

hls_rc4_t  g_rc4;

void Encrypt(unsigned char *pszText, int textLen, hls_rc4_t *rc4);

int is_equal(char* buf);

#endif 
