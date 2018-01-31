package com.example.vadym.movieapp.Api;

import com.example.vadym.movieapp.Model.MovieDetails;
import com.example.vadym.movieapp.Model.MovieResponce;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Vadym on 29.01.2018.
 */

public interface MovieApi {

    @GET("3/search/movie?api_key=c33e25174af866c5c102772d92d0e480")
    Call<MovieResponce> getMovie(@Query("query") String name, @Query("page") int number);

    @GET("/3/movie/{id}?api_key=c33e25174af866c5c102772d92d0e480")
    Call<MovieDetails> getMovieDetails(@Path("id")String id);
}
