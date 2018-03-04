package com.example.vadym.movieapp.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieRetrofit {

    private static Retrofit retrofit;

    public static MovieApi getRetrofit() {

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .client(OkHttpClient.getOkHttpClient().build())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        return retrofit.create(MovieApi.class);
    }

    public static Retrofit getReferenceRetrofit() {
        return retrofit;
    }


}
