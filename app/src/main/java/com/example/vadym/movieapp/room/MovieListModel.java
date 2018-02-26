package com.example.vadym.movieapp.room;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.service.Genres;

import java.util.List;

/**
 * Created by Vadym on 11.02.2018.
 */

public class MovieListModel extends AndroidViewModel {

    private final LiveData<List<Movie>> items;
    private final LiveData<List<Genres.Genre>> genres;
    private MovieDB db;

    public MovieListModel(@NonNull Application application) {
        super(application);

        db = MovieDB.getInstance(getApplication());
        items = db.movieDao().getAll();
        genres = db.movieDao().getAllGenre();
    }

    public LiveData<List<Movie>> getItems() {
        return items;
    }

    public LiveData<List<Genres.Genre>> getGenres(){return genres;}

    public void insertItem(Movie movie) {
        new insertAsyncTask(db).execute(movie);
    }

    public void deleteItem(Movie movie) {
        new deleteAsyncTask(db).execute(movie);
    }

    public void deleteAll() {
        new deleteAllAsyncTask(db).execute();
    }

    public void deleteByID(String id) {
        new deleteByIDAsyncTask(db).execute(id);
    }


    public void insertGenre(Genres.Genre genre){new insertGenresAsyncTask(db).execute(genre);}

    private static class insertGenresAsyncTask extends AsyncTask<Genres.Genre,Void,Void>{

        private MovieDB db;

        public insertGenresAsyncTask(MovieDB db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(Genres.Genre... genres) {
            for (Genres.Genre gen:genres) {
                db.movieDao().insertGenres(gen);
                return null;
            }
            return null;
        }
    }



    private static class deleteByIDAsyncTask extends AsyncTask<String, Void, Void> {

        private MovieDB db;

        public deleteByIDAsyncTask(MovieDB db) {
            this.db = db;
        }

        @Override
        protected Void doInBackground(String... strings) {

            for (String str : strings) {
                db.movieDao().deleteByID(str);
                return null;
            }
            return null;
        }
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
            for (Movie movie : movies) {
                db.movieDao().delete(movie);
                return null;
            }
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
            for (Movie movie : movies) {
                db.movieDao().insert(movie);
                return null;
            }
            return null;
        }
    }
}
