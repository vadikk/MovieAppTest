package com.example.vadym.movieapp.api;

import com.example.vadym.movieapp.constans.Constant;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Vadym on 26.03.2018.
 */

public class Interceptor implements okhttp3.Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
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
}
