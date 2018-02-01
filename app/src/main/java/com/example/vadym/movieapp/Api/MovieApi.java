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
    // TODO: 1/31/18 ключ має зберігатися десь в окреммоу місці і тре обдумати деталі, як його додавати, а не кожен раз писати тут.
    @GET("3/search/movie?api_key=c33e25174af866c5c102772d92d0e480_n0pE")
    Call<MovieResponce> getMovie(@Query("query") String name, @Query("page") int number);

    // TODO: 1/31/18 тут URL має слеш зпереду, а зверху - ні, іноді це може буть причиною падіння API.
    @GET("/3/movie/{id}?api_key=c33e25174af866c5c102772d92d0e480_n0pE")
    Call<MovieDetails> getMovieDetails(@Path("id") String id);
}
