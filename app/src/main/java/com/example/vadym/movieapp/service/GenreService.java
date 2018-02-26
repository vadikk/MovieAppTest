package com.example.vadym.movieapp.service;

import android.app.Service;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.room.MovieDB;
import com.example.vadym.movieapp.room.MovieDao;
import com.example.vadym.movieapp.room.MovieListModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenreService extends Service {

    private SharedPreferences sharedPreferences;
    private MovieDB db;

    public GenreService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        db = MovieDB.getInstance(getApplication());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        getAllGenres();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private String getLanguage(){
        String language = null;

        int id = sharedPreferences.getInt("language",20);
        switch (id){
            case 0:
                language = "en";
                return language;
            case 1:
                language = "de";
                return language;
            case 2:
                language = "ru";
                return language;
            default:
                return null;
        }
    }

    private void getAllGenres(){

        new Thread(new Runnable() {
            @Override
            public void run() {

                Call<Genres> genresCall = MovieRetrofit.getRetrofit().getGenres(getLanguage());
                genresCall.enqueue(new Callback<Genres>() {
                    @Override
                    public void onResponse(Call<Genres> call, Response<Genres> response) {
                        if(response.isSuccessful()){
                            Genres genres = response.body();
                            List<Genres.Genre> genreList = genres.getGenres();
                            for (Genres.Genre gen:genreList) {
                                insertGenre(gen);
                            }
//                            Log.d("TAG","SizeG " + genres.getGenres().size());
                            stopSelf();
                        }else {
                            stopSelf();
                        }
                    }

                    @Override
                    public void onFailure(Call<Genres> call, Throwable t) {

                    }
                });
            }
        }).start();

    }

    public void insertGenre(Genres.Genre genre){new insertGenresAsyncTask(db).execute(genre);}

    private static class insertGenresAsyncTask extends AsyncTask<Genres.Genre,Void,Void> {

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
}
