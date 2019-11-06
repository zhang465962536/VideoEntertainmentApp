package com.example.videoapp.Utils;

import com.example.videoapp.MyApplication;

import okhttp3.Callback;
import okhttp3.Request;
/* OKHTTP 工具类*/
public class OkHttpUtils {
    private static final String REQUEST_TAG = "okhttp";

    //对url 进行最基本的声明
    public static Request buildRequest(String url){
            if(MyApplication.isNetWorkAvailable()){
                //如果网络可用
                Request request = new Request.Builder()
                        .tag(REQUEST_TAG)
                        .url(url)
                        .build();
                return request;
            }
            return null;
    }

    public static void execute(String url, Callback callback){
        Request request = buildRequest(url);
        excute(request,callback);
    }

    public static void excute(Request request, Callback callback) {
        MyApplication.getHttpClient().newCall(request).enqueue(callback);
    }
}
