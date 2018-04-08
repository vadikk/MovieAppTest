package com.example.vadym.movieapp.dagger;

import android.app.Application;

/**
 * Created by Vadym on 04.04.2018.
 */

public class MovieAppAplication extends Application {

    private MovieComponent movieComponent;
    private FavoriteActivityComponent favoriteActivityComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        movieComponent = DaggerMovieComponent.builder()
                .appModule(new AppModule(this))
                .recyclerModule(new RecyclerModule())
                .build();

        favoriteActivityComponent = DaggerFavoriteActivityComponent.builder()
                .appModule(new AppModule(this))
                .favoriteModule(new FavoriteModule())
                .build();
    }

    public MovieComponent getMovieComponent() {
        return movieComponent;
    }

    public FavoriteActivityComponent getFavoriteActivityComponent() {
        return favoriteActivityComponent;
    }
}
