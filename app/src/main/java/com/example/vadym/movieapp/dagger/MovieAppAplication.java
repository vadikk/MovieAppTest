package com.example.vadym.movieapp.dagger;

import android.app.Application;
import android.content.Context;


/**
 * Created by Vadym on 04.04.2018.
 */

public class MovieAppAplication extends Application {

    private MovieComponent movieComponent;

    public static MovieAppAplication get(Context context) {
        return (MovieAppAplication) context.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        movieComponent = DaggerMovieComponent.builder()
                .appModule(new AppModule(this))
                .contextModule(new ContextModule(this))
                .build();
    }

    public MovieComponent getMovieComponent() {
        return movieComponent;
    }

}
