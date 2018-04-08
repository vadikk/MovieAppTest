package com.example.vadym.movieapp.dagger;

import com.example.vadym.movieapp.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Vadym on 04.04.2018.
 */
@Singleton
@Component(modules = {AppModule.class, RecyclerModule.class})
public interface MovieComponent {
    void inject(MainActivity activity);

}
