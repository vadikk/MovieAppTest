package com.example.vadym.movieapp.api;

import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.model.MovieResponce;
import com.example.vadym.movieapp.service.Genres;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Vadym on 29.01.2018.
 */

public interface MovieApi {

    @GET("3/search/movie?")
    Flowable<MovieResponce> getMovie(@Query("query") String name, @Query("page") int number, @Query("language") String language);

    @GET("3/movie/{id}?")
    Observable<MovieDetails> getMovieDetails(@Path("id") String id, @Query("language") String language);

    @GET("3/genre/movie/list?")
    Flowable<Genres> getGenres(@Query("language") String language);
}
