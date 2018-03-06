package com.example.vadym.movieapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.room.MovieDB;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GenreService extends Service {

    private SharedPreferences sharedPreferences;
    private MovieDB db;
    private CompositeDisposable compositeDisposable;

    public GenreService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        db = MovieDB.getInstance(getApplication());
        compositeDisposable = new CompositeDisposable();

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
        if (compositeDisposable != null)
            compositeDisposable.clear();
        super.onDestroy();
    }

    private String getLanguage() {
        String language = null;

        int id = sharedPreferences.getInt("language", 20);
        switch (id) {
            case 0:
                language = "en";
                return language;
            case 1:
                language = "de";
                return language;
            case 2:
                language = "ru";
                return language;
            // TODO: 3/6/18 Краще тут взяти англ по-дефолту, щоб на всякий випадок поверталося англійською. Або, якщо на сервері дефолтно англійською, можна лишити нул.
            default:
                return null;
        }
    }

    private void getAllGenres() {

        Flowable<Genres> genresFlowable = MovieRetrofit.getRetrofit().getGenres(getLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Disposable disposable = genresFlowable.subscribe(response -> {
            Genres genres = response;
            List<Genres.Genre> genreList = genres.getGenres();
            for (Genres.Genre gen : genreList) {
                insertGenre(gen);
            }
            // TODO: 3/6/18 Тут такий прикол - в тебе додавання в базу відбувається асинхронно і може виконуватися довше і то не гарантує, що закінчиться все до твого виклику stopSelf.
            // TODO: Краще зробити це в одному комплексі. Наприклад, отримати жанри, кожного із жанрів додати в базу. а при сабскрайбі - зупинити сервіс.
            //TODO: глянь таку штуку як flatMap.
            stopSelf();
        });
        compositeDisposable.add(disposable);

    }

    public void insertGenre(Genres.Genre genre) {
        Completable.fromAction(() -> {
            db.movieDao().insertGenres(genre);
        }).subscribeOn(Schedulers.io()).subscribe();
    }

}
