package com.example.vadym.movieapp.dagger.main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.vadym.movieapp.api.MovieApi;
import com.example.vadym.movieapp.mvp.main.MainContract;
import com.example.vadym.movieapp.mvp.main.MovieModelImpl;
import com.example.vadym.movieapp.mvp.main.MoviePresenterImpl;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.firebase.firestore.DocumentReference;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MvpContractModule {

    private MainContract.MovieView view;

    public MvpContractModule(MainContract.MovieView view) {
        this.view = view;
    }

    @Provides
    public MainContract.MovieView getView() {
        return view;
    }

    @Provides
    public MainContract.MovieModel getModel(SharedPreferences sharedPreferences, MovieListModel viewModel, DocumentReference firestoreDB, MovieApi api){
        return new MovieModelImpl(sharedPreferences, viewModel, firestoreDB,api);
    }

    @Provides
    public MainContract.MoviePresenter getPresenter(MainContract.MovieView view, MainContract.MovieModel movieModel){
        return new MoviePresenterImpl(view,movieModel);
    }
}
