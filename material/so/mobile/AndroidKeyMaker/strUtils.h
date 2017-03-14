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

// ==================================================================
//	std::string相关的辅助工具函数		
// ==================================================================

#ifndef DOLIT_CN_SiteParser_StrUtils_H
#define DOLIT_CN_SiteParser_StrUtils_H

//#include "base.h"

#include <string>
#include <vector>
#include <sstream>
#include <iomanip>

// declaration
namespace strutil {

    std::string trimLeft(const std::string& str);
    std::string trimRight(const std::string& str);
    std::string trim(const std::string& str);
    std::string delChar(std::string &str, const char *to_del);

    std::string replace(std::string &str, const char *string_to_replace, const char *new_string);
	std::string formatString(const char *fmt, ...);

    std::string toLower(const std::string& str);
    std::string toUpper(const std::string& str);

    std::string repeat(char c, int n);
    std::string repeat(const std::string& str, int n);

    bool startsWith(const std::string& str, const std::string& substr);
    bool endsWith(const std::string& str, const std::string& substr);
    bool equalsIgnoreCase(const std::string& str1, const std::string& str2);

    std::string toBinaryString(const UINT64 & u);
    UINT64 parseBinaryString(const std::string &str);

    template<class T> T parseString(const std::string& str);
    template<class T> T parseHexString(const std::string& str);
    template<bool> bool parseString(const std::string& str);

    template<class T> std::string toString(const T& value);
    template<class T> std::string toHexString(const T& value, int width = 0);
    std::string toString(const bool& value);

    std::vector<std::string> split(const std::string& str, const std::string& delimiters);
    std::vector<std::string> splitStr(std::string str, const std::string &pattern);

    // 寻找第几处字符串
    std::string::size_type find_pos(std::string str, const std::string &pattern, int count);
}

// Tokenizer class
namespace strutil {
    class Tokenizer {
    public:
        static const std::string DEFAULT_DELIMITERS;
        Tokenizer(const std::string& str);
        Tokenizer(const std::string& str, const std::string& delimiters);

        bool nextToken();
        bool nextToken(const std::string& delimiters);
        const std::string getToken() const;

        /**
        * to reset the tokenizer. After reset it, the tokenizer can get
        * the tokens from the first token.
        */
        void reset();

    protected:
        size_t m_Offset;
        const std::string m_String;
        std::string m_Token;
        std::string m_Delimiters;
    };

}

// implementation of template functions
namespace strutil {

    template<class T> T parseString(const std::string& str) {
        T value;
        std::istringstream iss(str);
        iss >> value;
        return value;
    }

    template<class T> T parseHexString(const std::string& str) {
        T value;
        std::istringstream iss(str);
        iss >> hex >> value;
        return value;
    }

    template<class T> std::string toString(const T& value) {
        std::ostringstream oss;
        oss << value;
        return oss.str();
    }

    template<class T> std::string toHexString(const T& value, int width) {
        std::ostringstream oss;
        oss << hex;
        if (width > 0) {
            oss << setw(width) << setfill('0');
        }
        oss << value;
        return oss.str();
    }

}

#endif //DOLIT_CN_SiteParser_StrUtils_H
