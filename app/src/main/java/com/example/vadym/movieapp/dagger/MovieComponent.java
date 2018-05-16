package com.example.vadym.movieapp.dagger;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Vadym on 04.04.2018.
 */
@Singleton
@Component(modules = {AppModule.class, ContextModule.class})
public interface MovieComponent {
    void inject(MovieAppAplication appAplication);

    Context getContext();

    Application getApplication();
}
