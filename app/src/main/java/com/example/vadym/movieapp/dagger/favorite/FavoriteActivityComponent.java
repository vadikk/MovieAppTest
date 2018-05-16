package com.example.vadym.movieapp.dagger.favorite;

import com.example.vadym.movieapp.activities.FavoriteListActivity;
import com.example.vadym.movieapp.dagger.ActivityMovieModule;
import com.example.vadym.movieapp.dagger.ActivityScope;
import com.example.vadym.movieapp.dagger.MovieComponent;

import dagger.Component;

/**
 * Created by Vadym on 05.04.2018.
 */
@ActivityScope
@Component(dependencies = MovieComponent.class,modules = {MvpFavoriteModule.class, ActivityMovieModule.class})
public interface FavoriteActivityComponent {
    void inject(FavoriteListActivity activity);

}
