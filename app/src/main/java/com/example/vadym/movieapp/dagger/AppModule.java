package com.example.vadym.movieapp.dagger;

import android.app.Application;

import com.example.vadym.movieapp.dagger.MovieAppAplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vadym on 04.04.2018.
 */
@Module
public class AppModule {
//    Application application;
    private MovieAppAplication application;

    public AppModule(MovieAppAplication application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application getApplication() {
        return application;
    }
}
