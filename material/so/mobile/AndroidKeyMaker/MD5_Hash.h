

#ifndef ___DOLTT_MD5_H___
#define ___DOLTT_MD5_H___  

#pragma once

namespace MD5
{
    std::string Hash(std::string &src);
    void md5it(unsigned char *outbuffer, /* 16 bytes */
                    const unsigned char *input, unsigned int len);
}; 

#endif 
