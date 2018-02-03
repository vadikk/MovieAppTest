package com.example.vadym.movieapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieRetrofit {

    public static MovieApi getRetrofit() {
        // TODO: 2/3/18 То добре, що ти виніс  ключі в окреме місце, але ти знову їх русками кожен раз вказуєш.
        // TODO: 2/3/18 Глянь на таку штуку. як Interceptor, щоб додавати ключ автоматом до кожного запиту.
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MovieApi.class);
    }
}
