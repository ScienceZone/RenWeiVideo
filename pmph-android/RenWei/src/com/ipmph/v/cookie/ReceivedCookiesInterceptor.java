package com.ipmph.v.cookie;

import java.io.IOException;
import java.util.List;

import android.util.Log;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Response;

public class ReceivedCookiesInterceptor implements Interceptor {  
    @Override  
    public Response intercept(Chain chain) throws IOException {  
        Response originalResponse = chain.proceed(chain.request());  
        List<String> cookieList =  originalResponse.headers("Set-Cookie");  
        if(cookieList != null) {  
            for(String s:cookieList) {//Cookie的格式为:cookieName=cookieValue;path=xxx  
                //保存你需要的cookie数据  
            	Log.d("gaolei", "cookieList.toString()-----------------" + cookieList.toString());
            }  
        }  
        return originalResponse;  
    }  
}  
