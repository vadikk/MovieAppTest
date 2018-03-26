package com.example.vadym.movieapp.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.room.MovieDB;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
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
        Disposable disposable =  MovieRetrofit.getRetrofit().getGenres(getLanguage()).toObservable()
                .subscribeOn(Schedulers.newThread())
                .flatMap(genres -> io.reactivex.Observable.just(genres.getGenres()))
                .filter(genres -> !genres.isEmpty())
                .flatMap(io.reactivex.Observable::fromIterable)
                .flatMapCompletable(genre -> insertGenre(genre))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        //нам не нужно выкидывать его в мейн поток, хай достает его паралелльно, это при сабскрайбе, то там мона заюзать мейн

        // TODO: 3/6/18 Тут такий прикол - в тебе додавання в базу відбувається асинхронно і може виконуватися довше і то не гарантує, що закінчиться все до твого виклику stopSelf.
        // TODO: Краще зробити це в одному комплексі. Наприклад, отримати жанри, кожного із жанрів додати в базу. а при сабскрайбі - зупинити сервіс.
        //TODO: глянь таку штуку як flatMap.
        //todo Не впевенний, що так гарно зроблено, бо трба переглянути мені по другому реактиву апі. Юл я вже трохи призабув, бо на проектах перший переважно(


//        map(genres -> {
//            List<Genres.Genre> genreList = genres.getGenres();
//            insertGenre(genreList.get(0));
//            Log.d("TAG"," value " + genreList.get(0));
//            return genres;
//        })

        compositeDisposable.add(disposable);

    }

    public Completable insertGenre(Genres.Genre genre) {
        return Completable.fromAction(() -> {
            Log.i("insertGenre", genre.getName());
            db.movieDao().insertGenres(genre);
        }).subscribeOn(Schedulers.io()) .observeOn(Schedulers.io());
    }
}
