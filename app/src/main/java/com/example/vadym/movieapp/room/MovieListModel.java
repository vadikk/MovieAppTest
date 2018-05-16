package com.example.vadym.movieapp.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.service.Genres;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Vadym on 11.02.2018.
 */

public class MovieListModel extends AndroidViewModel {

    private MovieDB db;

    public MovieListModel(@NonNull Application application) {
        super(application);

        db = MovieDB.getInstance(getApplication());
    }

    public Flowable<List<Movie>> getItems() {
        return db.movieDao().getAll();
    }

    public Single<List<Genres.Genre>> getGenres() {
        return db.movieDao().getAllGenre();
    }

    public void insertItem(Movie movie) {

        Completable.fromAction(() -> {
            db.movieDao().insert(movie);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void deleteItem(Movie movie) {
        Completable.fromAction(() -> {
            db.movieDao().delete(movie);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void deleteAll() {
        Completable.fromAction(() -> {
            db.movieDao().deleteAll();
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    public void deleteByID(String id) {
        Completable.fromAction(() -> {
            db.movieDao().deleteByID(id);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

}
