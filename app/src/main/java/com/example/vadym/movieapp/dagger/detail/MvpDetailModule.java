package com.example.vadym.movieapp.dagger.detail;

import android.content.SharedPreferences;

import com.example.vadym.movieapp.mvp.detail.DetailContract;
import com.example.vadym.movieapp.mvp.detail.DetailModelImpl;
import com.example.vadym.movieapp.mvp.detail.DetailPresenterImpl;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.firebase.firestore.DocumentReference;

import dagger.Module;
import dagger.Provides;

@Module
public class MvpDetailModule {

    private DetailContract.DetailView view;

    public MvpDetailModule(DetailContract.DetailView view) {
        this.view = view;
    }

    @Provides
    DetailContract.DetailView getView() {
        return view;
    }

    @Provides
    DetailContract.DetailModel getModel(SharedPreferences sharedPreferences, DocumentReference firestoreDB, MovieListModel viewModel) {
        return new DetailModelImpl(sharedPreferences, firestoreDB, viewModel);
    }

    @Provides
    DetailContract.DetailPresenter getPresenter(DetailContract.DetailView view, DetailContract.DetailModel model) {
        return new DetailPresenterImpl(view, model);
    }
}
