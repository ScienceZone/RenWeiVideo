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

#include "stdafx.h"

#include "strUtils.h"
#include <algorithm>
#include <bitset>

namespace strutil {

	using namespace std;

	string trimLeft(const string& str) {
		string t = str;
		string::iterator i;
		for (i = t.begin(); i != t.end(); i++) {
			if (!isspace(*i)) {
				break;
			}
		}
		if (i == t.end()) {
			t.clear();
		} else {
			t.erase(t.begin(), i);
		}
		return t;
	}

	string trimRight(const string& str) {
		if (str.begin() == str.end()) {
			return str;
		}

		string t = str;
		string::iterator i;
		for (i = t.end() - 1; ;i--) {
			if (!isspace(*i)) {
				t.erase(i + 1, t.end());
				break;
			}
			if (i == t.begin()) {
				t.clear();
				break;
			}
		}
		return t;
	}

	string trim(const string& str) {
		string t = str;

		string::iterator i;
		for (i = t.begin(); i != t.end(); i++) {
			if (!isspace(*i)) {
				break;
			}
		}
		if (i == t.end()) {
			t.clear();
			return t;
		} else {
			t.erase(t.begin(), i);
		}

		for (i = t.end() - 1; ;i--) {
			if (!isspace(*i)) {
				t.erase(i + 1, t.end());
				break;
			}
			if (i == t.begin()) {
				t.clear();
				break;
			}
		}

		return t;
	}

    std::string delChar(std::string &str, const char *to_del)
    {
        str.erase(std::remove(str.begin(), str.end(), '\0'), str.end());
        return str;
    }

    std::string replace(std::string &str, const char *string_to_replace, const char *new_string)   
    {   
        int index = (int)str.find(string_to_replace);  
        while(index != std::string::npos)   
        {  
            str.replace(index, strlen(string_to_replace), new_string); 
            index = (int)str.find(string_to_replace, index + strlen(new_string));   
        }   
        return str;   
    } 

		std::string formatString(const char *fmt, ...)
	{
		std::string strResult="";
		if (NULL != fmt)
		{
			va_list marker;
			va_start(marker, fmt);                            //初始化变量参数
#ifdef _WIN32
			size_t nLength = _vscprintf(fmt, marker) + 1;    //获取格式化字符串长度
			std::vector<char> vBuffer(nLength, '\0');        //创建用于存储格式化字符串的字符数组
			int nWritten = _vsnprintf(&vBuffer[0], vBuffer.size(), fmt, marker);
			if (nWritten > 0)
			{
				strResult = &vBuffer[0];
			}
#else
			/*size_t nLength = vsnprintf(NULL,0, fmt, marker) + 1;    //获取格式化字符串长度
			std::vector<char> vBuffer(nLength, '\0');        //创建用于存储格式化字符串的字符数组
			int nWritten = vsnprintf(&vBuffer[0], vBuffer.size(), fmt, marker);
			cout << "nLength: " << nLength << " nWrite: " << nWritten << ", vBuf: " << &vBuffer[0] << endl;*/

           
		   char buff[100];
		   memset(buff, 0, sizeof(buff));
           
		   int nWritten = vsnprintf( buff, sizeof(buff) - 1, fmt, marker); // C4996
           
		   if (nWritten > 0)
           {
                strResult = buff;
           }
#endif

			va_end(marker);                                    //重置变量参数
		}
		return strResult;
	}

	string toLower(const string& str) {
		string t = str;
#ifdef _WIN32
		transform(t.begin(), t.end(), t.begin(), tolower);
#else
		std::transform(t.begin(), t.end(), t.begin(), ::tolower);
#endif
		return t;
	}

	string toUpper(const string& str) {
		string t = str;
#ifdef _WIN32
		transform(t.begin(), t.end(), t.begin(), toupper);
#else
		std::transform(t.begin(), t.end(), t.begin(), ::toupper);
#endif
		return t;
	}


	string repeat(char c, int n) {
		ostringstream s;
		s << setw(n) << setfill(c) << "";
		return s.str();
	}

	string repeat(const string& str, int n) {
		string s;
		for (int i = 0; i < n; i++) {
			s += str;
		}
		return s;
	}

	bool startsWith(const string& str, const string& substr) {
		return str.find(substr) == 0;
	}

	bool endsWith(const string& str, const string& substr) {
		size_t i = str.rfind(substr);
		return (i != string::npos) && (i == (str.length() - substr.length()));
	}

	bool equalsIgnoreCase(const string& str1, const string& str2) {
		return toLower(str1) == toLower(str2);
	}

    std::string toBinaryString(const UINT64 & u)
    {
#ifdef WIN32
        char buf[128] = {0};
        return _i64toa(u, buf, 2);
#else
        return std::bitset<64>(u).to_string();
#endif
    }

    UINT64 parseBinaryString(const std::string &str)
    {
#ifdef WIN32
        return _strtoui64(str.c_str(), NULL, 2);
#else
        return strtoull(str.c_str(), NULL, 2);
#endif
    }

	template<bool>
		bool parseString(const std::string& str) {
			bool value;
			std::istringstream iss(str);
			iss >> boolalpha >> value;
			return value;
		}

		string toString(const bool& value) {
			ostringstream oss;
			oss << boolalpha << value;
			return oss.str();
		}

		vector<string> split(const string& str, const string& delimiters) {
			vector<string> ss;

			Tokenizer tokenizer(str, delimiters);
			while (tokenizer.nextToken()) {
				ss.push_back(tokenizer.getToken());
			}

			return ss;
		}

        std::vector<std::string> splitStr(std::string str, const std::string &pattern)
        {
            std::string::size_type pos;
            std::vector<std::string> result;
            str += pattern;
            int size = (int)str.size();

            for(int i=0; i<size; i++)
            {
                pos = str.find (pattern, i);
                if((int)pos < size)
                {
                    std::string s = str.substr(i, pos-i);
                    result.push_back(s);
                    i = (int)pos + (int)pattern.size() - 1;
                }    
            }
            return result;
        }

        std::string::size_type find_pos(std::string str, const std::string &pattern, int count)
        {
            std::string::size_type pos;
            int size = (int)str.size();

            int findCount = 0;
            for(int i=0; i<size; i++)
            {
                pos = str.find (pattern, i);
                if(pos < (std::string::size_type)size)
                {
                    findCount ++;
                    if (findCount >= count)
                        return pos;

                    std::string s = str.substr(i, pos-i);
                    i = (int) (pos + pattern.size() - 1);
                }    
            }
            return std::string::npos;
        }
}

namespace strutil {

	const string Tokenizer::DEFAULT_DELIMITERS(" \t\n\r");

	Tokenizer::Tokenizer(const std::string& str)
		: m_String(str), m_Offset(0), m_Delimiters(DEFAULT_DELIMITERS) {}

		Tokenizer::Tokenizer(const std::string& str, const std::string& delimiters)
			: m_String(str), m_Offset(0), m_Delimiters(delimiters) {}

			bool Tokenizer::nextToken() {
				return nextToken(m_Delimiters);
			}

			bool Tokenizer::nextToken(const std::string& delimiters) {
				// find the start charater of the next token.
				size_t i = m_String.find_first_not_of(delimiters, m_Offset);
				if (i == string::npos) {
					m_Offset = m_String.length();
					return false;
				}

				// find the end of the token.
				size_t j = m_String.find_first_of(delimiters, i);
				if (j == string::npos) {
					m_Token = m_String.substr(i);
					m_Offset = m_String.length();
					return true;
				}

				// to intercept the token and save current position
				m_Token = m_String.substr(i, j - i);
				m_Offset = j;
				return true;
			}

			const string Tokenizer::getToken() const {
				return m_Token;
			}

			void Tokenizer::reset() {
				m_Offset = 0;
			}

}
