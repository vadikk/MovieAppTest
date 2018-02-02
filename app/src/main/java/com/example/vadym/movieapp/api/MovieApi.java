package com.example.vadym.movieapp.api;

import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.model.MovieResponce;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Vadym on 29.01.2018.
 */

public interface MovieApi {

    @GET("3/search/movie?")
    Call<MovieResponce> getMovie(@Query("api_key") String api,@Query("query") String name, @Query("page") int number);

    @GET("3/movie/{id}?")
    Call<MovieDetails> getMovieDetails(@Path("id") String id, @Query("api_key") String api);
}
