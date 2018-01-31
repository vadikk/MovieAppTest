package com.example.vadym.movieapp.Api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieRetrofit {

    public static MovieApi getRetrofit(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieApi api = retrofit.create(MovieApi.class);
        return api;
    }
}
