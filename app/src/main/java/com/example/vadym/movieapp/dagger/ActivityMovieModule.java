package com.example.vadym.movieapp.dagger;

import android.app.Application;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.example.vadym.movieapp.api.MovieApi;
import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vadym on 04.04.2018.
 */
@Module
public class ActivityMovieModule {

    private AppCompatActivity activity;

    public ActivityMovieModule(AppCompatActivity activity) {
        this.activity = activity;
    }

    @Provides
    AppCompatActivity getActivity() {
        return activity;
    }

    @Provides
    MovieListModel getViewModel() {
        return ViewModelProviders.of(activity).get(MovieListModel.class);
    }

    @Provides
    LinearLayoutManager getManager(Application application) {
        Context context = (Context) application;
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }

    @Provides
    public SharedPreferences getSharedPreferenceModule(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    public DocumentReference getFirestoreDB() {
        return FirebaseFirestore.getInstance().collection("movie").document("movieData");
    }

    @Provides
    public MovieApi getApi() {
        return MovieRetrofit.getRetrofit();
    }
}
