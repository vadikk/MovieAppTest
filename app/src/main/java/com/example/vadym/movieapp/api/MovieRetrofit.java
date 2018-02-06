package com.example.vadym.movieapp.api;

import com.example.vadym.movieapp.constans.Constant;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieRetrofit {

    private static Retrofit retrofit;

    public static MovieApi getRetrofit() {

        OkHttpClient.Builder okhttp = new OkHttpClient.Builder();
        // TODO: 2/6/18 Винеси інтерсептор в окремий клас
        okhttp.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("api_key",Constant.API_KEY)
                        .build();

                Request.Builder builder = original.newBuilder()
                        .url(url);

                Request request = builder.build();
                return chain.proceed(request);
            }
        });
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .client(okhttp.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(MovieApi.class);
    }

    public static Retrofit getReferenceRetrofit(){
        return retrofit;
    }
}
