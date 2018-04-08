package com.example.vadym.movieapp.dagger;

import com.example.vadym.movieapp.activities.FavoriteListActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Vadym on 05.04.2018.
 */
@Singleton
@Component(modules = {AppModule.class, FavoriteModule.class})
public interface FavoriteActivityComponent {
    void inject(FavoriteListActivity activity);

}
