package com.example.vadym.movieapp.api;

/**
 * Created by Vadym on 15.02.2018.
 */

public class OkHttpClient {

    public static okhttp3.OkHttpClient.Builder getOkHttpClient() {

        okhttp3.OkHttpClient.Builder okhttp = new okhttp3.OkHttpClient.Builder();
        okhttp.addInterceptor(new Interceptor());
        return okhttp;
    }

}
