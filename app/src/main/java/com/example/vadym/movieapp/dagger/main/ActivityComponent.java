package com.example.vadym.movieapp.dagger.main;

import com.example.vadym.movieapp.activities.MainActivity;
import com.example.vadym.movieapp.dagger.ActivityMovieModule;
import com.example.vadym.movieapp.dagger.ActivityScope;
import com.example.vadym.movieapp.dagger.MovieComponent;

import dagger.Component;

@ActivityScope
@Component(dependencies = MovieComponent.class,modules = {ActivityMovieModule.class,
        MvpContractModule.class})
public interface ActivityComponent {
    void inject(MainActivity activity);

}
