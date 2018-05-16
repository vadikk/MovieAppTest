package com.example.vadym.movieapp.dagger.detail;

import com.example.vadym.movieapp.activities.MovieDetailsActivity;
import com.example.vadym.movieapp.dagger.ActivityMovieModule;
import com.example.vadym.movieapp.dagger.ActivityScope;
import com.example.vadym.movieapp.dagger.MovieComponent;

import dagger.Component;

@ActivityScope
@Component(dependencies = MovieComponent.class, modules = {MvpDetailModule.class, ActivityMovieModule.class})
public interface DetailActivityComponent {
    void inject(MovieDetailsActivity activity);
}
