package com.example.vadym.movieapp.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.vadym.movieapp.model.Movie;

import java.util.List;

/**
 * Created by Vadym on 11.02.2018.
 */

public class MovieListModel extends AndroidViewModel {

    private final LiveData<List<Movie>> items;
    private MovieDB db;

    public MovieListModel(@NonNull Application application) {
        super(application);

        db = MovieDB.getInstance(getApplication());
        items = db.movieDao().getAll();
    }

    public LiveData<List<Movie>> getItems() {
        return items;
    }

    public void insertItem(Movie movie) {
        new insertAsyncTask(db).execute(movie);
    }

    public void deleteItem(Movie movie) {
        new deleteAsyncTask(db).execute(movie);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(db).execute();
    }

    private static class deleteAllAsyncTask extends AsyncTask<Void, Void, Void> {

        private MovieDB db;

        public deleteAllAsyncTask(MovieDB db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.movieDao().deleteAll();
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Movie, Void, Void> {

        private MovieDB db;

        public deleteAsyncTask(MovieDB db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            db.movieDao().delete(movies[0]);
            return null;
        }
    }

    private static class insertAsyncTask extends AsyncTask<Movie, Void, Void> {

        private MovieDB db;

        public insertAsyncTask(MovieDB db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Movie... movies) {
            db.movieDao().insert(movies[0]);
            return null;
        }
    }
}
