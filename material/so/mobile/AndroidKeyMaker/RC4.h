//=======================================================================================
//                 点量软件－－ 贡献点滴力量  点亮一丝烛光
// 
//	Copyright:	Copyright (c) 点量软件有限公司 
//  版权所有：	点量软件有限公司 (QQ:52401692   <support at dolit.cn>)
//
//              如果您是个人作为非商业目的使用，您可以自由、免费的使用点量软件库和演示程序，
//              也期待收到您反馈的意见和建议，共同改进点量产品
//              如果您是商业使用，那么您需要联系作者申请产品的商业授权。
//              点量软件库所有演示程序的代码对外公开，软件库的代码只限付费用户个人使用。
//        
//  官方网站：  http://www.dolit.cn      http://blog.dolit.cn
//
//======================================================================================= 


// 因为RC4不能对任意一个位置进行解密（需要每次从头解密），这样如果解密第100M的数据，也要从前面99.9M开始解，浪费CPU
// 因此，这里是基于RC4的算法思想，改进为自己的加密算法

#if defined(_MSC_VER)
#pragma once

typedef unsigned char uint8_t;
typedef unsigned long uint32_t;
typedef unsigned __int64 UINT64;

// Other compilers

#else	// defined(_MSC_VER)

#include <stdint.h>
#include <string.h>


#endif // !defined(_MSC_VER)

#define SWAP(a, b) ((a) ^= (b), (b) ^= (a), (a) ^= (b))

class CRC4 
{
private:
    unsigned char sbox[256];      /* Encryption array             */
    unsigned char key[256],k;     /* Numeric key values           */

    bool m_bInit;
public:

    CRC4 () 
    {
        memset(sbox,0,256);
        memset(key,0,256);
        m_bInit = false;
    }
    virtual ~CRC4 ()
    {							
        memset(sbox,0,256);  /* remove Key traces in memory  */
        memset(key,0,256);   
    }

    void InitKey (const char * pszKey, int keyLen)
    {
        if (m_bInit)
            return;

        int n = 0;
        int m = 0;
        for (m = 0;  m < 256; m++)  /* Initialize the key sequence */
        {
            *(key + m)= *(pszKey + (m % keyLen));
            *(sbox + m) = m;
        }

        for (m=0; m < 256; m++)
        {
            n = (n + *(sbox+m) + *(key + m)) &0xff;
            SWAP(*(sbox + m),*(sbox + n));
        }
        m_bInit = true;
    }

    char *Encrypt(char *pszText, int textLen, const char *pszKey, int keyLen, bool bDebug = false) 
    {
        InitKey (pszKey, keyLen);

        UINT64 ilen = textLen;
        UINT64 i = 0;
        for (i = 0; i < ilen; i++)
        {
            int h = ((int)i + 1) &0xff;
            int j = (h + key[h] + *(sbox + h)) &0xff;
            int k = *(sbox + ((*(sbox + h) + *(sbox + j)) &0xff ));

            if(k == key[2])       /* avoid '\0' beween the decoded text; */
                k = 0;

            *(pszText + i) ^=  k;  
        }
        return pszText;
    }
};

