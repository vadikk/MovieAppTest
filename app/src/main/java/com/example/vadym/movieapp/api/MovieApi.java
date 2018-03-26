package com.example.vadym.movieapp.api;

import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.model.MovieResponce;
import com.example.vadym.movieapp.service.Genres;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
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
    Single<MovieDetails> getMovieDetails(@Path("id") String id, @Query("language") String language);

    @GET("3/genre/movie/list?")
    Observable<Genres> getGenres(@Query("language") String language);

    @GET("3/discover/movie?")
    Flowable<MovieResponce> getDiscoverMovie(@Query("language") String language, @Query("sort_by") String sort, @Query("include_adult") String adult,
                                             @Query("include_video") String video, @Query("page") Integer page, @Query("vote_count.gte") String vote,
                                             @Query("year") String year, @Query("with_runtime.gte") String runtime);
}
