package com.example.vadym.movieapp.api;

import com.example.vadym.movieapp.constans.Constant;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Vadym on 15.02.2018.
 */

public class OkHttpClient {

    public static okhttp3.OkHttpClient.Builder getOkHttpClient() {

        okhttp3.OkHttpClient.Builder okhttp = new okhttp3.OkHttpClient.Builder();
        okhttp.addInterceptor(new Interceptor() {
            @Override
            // TODO: 3/6/18 Тут краще інтерсептор сам в окремий клас винести.
            public Response intercept(Interceptor.Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key", Constant.API_KEY)
                        .build();

                Request.Builder builder = original.newBuilder()
                        .url(url);

                Request request = builder.build();
                return chain.proceed(request);
            }
        });
        return okhttp;
    }

}
