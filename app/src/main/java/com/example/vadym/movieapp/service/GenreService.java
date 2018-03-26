package com.example.vadym.movieapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;

import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.room.MovieDB;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GenreService extends Service {

    public static boolean loadFromDB = false;
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

        int id = sharedPreferences.getInt("language", 0);
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
            default:
                language = "en";
                return language;
        }
    }

    private void getAllGenres() {

        Observable flowable = MovieRetrofit.getRetrofit().getGenres(getLanguage())
                .subscribeOn(Schedulers.newThread())
                .flatMap(genres -> Observable.just(genres.getGenres()))
                .filter(genres -> !genres.isEmpty())
                .flatMap(Observable::fromIterable)
                .flatMapCompletable(this::insertGenre).toObservable()
                .doOnSubscribe(subscription -> loadFromDB = true)
                .doFinally(this::stopSelf);

        Disposable disposable = flowable.subscribe();
        compositeDisposable.add(disposable);

    }

    public Completable insertGenre(Genres.Genre genre) {
        return Completable.fromAction(() -> {
            db.movieDao().insertGenres(genre);
        }).subscribeOn(Schedulers.io());
    }

}
