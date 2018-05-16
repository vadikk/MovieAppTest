package com.example.vadym.movieapp.mvp.main;

import android.support.annotation.NonNull;
import android.util.Log;

import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.service.GenreService;
import com.example.vadym.movieapp.service.Genres;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MoviePresenterImpl implements MainContract.MoviePresenter {

    private MainContract.MovieView view;
    private MainContract.MovieModel model;
    private CompositeDisposable compositeDisposable;
    private int total = 0;
    private boolean isLoading = true;
    private boolean isSearch = false;
    private boolean isfilter = false;
    private String searchText;
    private int page = 1;
    private String vote;
    private String year;
    private String runTime;

    @Inject
    public MoviePresenterImpl(MainContract.MovieView view, MainContract.MovieModel model) {
        compositeDisposable = new CompositeDisposable();
        this.view = view;
        this.model = model;
    }

    @Override
    public void onDestroy() {
        if (compositeDisposable != null)
            compositeDisposable.clear();
        view = null;
    }

    @Override
    public void clickEnterOnSearchView(String text, int page, String language) {
        searchText = text;
        isSearch = true;

        Flowable responseCall = model.getMovie(text, page, language)
                .doOnNext(movieResponce -> total = Integer.parseInt(movieResponce.getTotalResults()))
                .flatMap(movieResponce -> Flowable.just(movieResponce.getMovieList()))
                .filter(list -> !list.isEmpty())
                .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMapCompletable(view::showData).toFlowable()
                .doOnComplete(() -> {
                    view.hideProgressBar();
                    isLoading = false;
                })
                .onErrorReturn(throwable -> {
                    view.showErrorView(throwable);
                    return true;
                }).doOnSubscribe(subscription -> {
                    view.showProgressBar();
                });


        Disposable disposable = responseCall.subscribe();
        compositeDisposable.add(disposable);

    }

    @Override
    public void recyclerScrollListener(int lastVisibleItemCount, int allLoadedItemsCount) {

        if (isLoading) return;

        int loadShouldStartPosition = (int) (allLoadedItemsCount * 0.8);

        if (loadShouldStartPosition <= lastVisibleItemCount && allLoadedItemsCount < total) {
            page++;
            isLoading = true;
        }

        if (isLoading && isSearch)
            clickEnterOnSearchView(searchText, page, getLanguage());
        if (isLoading && isfilter)
            loadMoreFilterData(page, vote, year, runTime);

//        Log.d("TAG", "Total " + total + " allLoadedItemsCount " + allLoadedItemsCount + " isLoading " + isLoading);
    }

    @Override
    public String getLanguage() {
        return model.getLanguage();
    }

    @Override
    public void loadMoreFilterData(int page, String vote, String year, String runTime) {
        this.vote = vote;
        this.year = year;
        this.runTime = runTime;
        isfilter = true;

        Flowable responseCall = MovieRetrofit.getRetrofit().getDiscoverMovie(getLanguage(), Constant.SORT,
                "false", "false", page, vote, year, runTime)
                .doOnNext(movieResponce -> total = Integer.parseInt(movieResponce.getTotalResults()))
                .flatMap(movieResponce -> Flowable.just(movieResponce.getMovieList()))
                .filter(list -> !list.isEmpty())
                .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMapCompletable(view::showData).toFlowable()
                .doOnComplete(() -> {
                    view.hideProgressBar();
                    isLoading = false;
                })
                .onErrorReturn(throwable -> {
                    view.showErrorView(throwable);
                    return true;
                }).doOnSubscribe(subscription -> {
                    view.showProgressBar();
                });

        Disposable disposable = responseCall.subscribe();
        compositeDisposable.add(disposable);
    }

    @Override
    public void subscribeUIMovie() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("TAG", "isAuth " + "OKK");
            readFromFirebase();
        } else {
            Log.d("TAG", "isAuth " + "OK2");
            Flowable flowable = model.getItems()
                    .subscribeOn(Schedulers.io())
                    .filter(list -> !list.isEmpty())
                    .flatMap(Flowable::fromIterable)
                    .flatMapCompletable(movie -> view.setFavoritID(movie)).toFlowable()
                    .observeOn(AndroidSchedulers.mainThread());

            Disposable disposableMovie = flowable.subscribe();
            compositeDisposable.add(disposableMovie);
        }

        Disposable disposable = model.getGenres()
                .subscribeOn(Schedulers.io())
                .filter(genres -> !genres.isEmpty())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(genres -> {
                    if (GenreService.loadFromDB) {
                        GenreService.loadFromDB = false;
                        for (Genres.Genre genre : genres) {
                            Log.d("TAG", " ID " + genre.getId() + " name " + genre.getName());
                        }
                    }
                });

        compositeDisposable.add(disposable);

    }

    @Override
    public void addToBD(Movie movie) {
        model.addToBD(movie);
    }

    @Override
    public void deleteFromBD(Movie movie) {
        model.deleteFromBD(movie);
    }

    @Override
    public void addToFirebase(String position, Movie movie) {
        model.addToFirebase(position, movie);
    }

    @Override
    public void deleteFromFirebase(String position) {
        model.deleteFromFirebase(position);
    }

    public void readFromFirebase() {
        model.getFirestoreDB().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + snapshot.getData());
                        Map<Object, Object> map = new HashMap<>();
                        map.putAll(snapshot.getData());
                        getMovieFromMap(map);

                    }
                }
            }
        });

    }

    private void getMovieFromMap(Map<Object, Object> map) {
        Completable.fromAction(() -> {
            for (Object obj : map.values()) {
                Gson gson = new Gson();
                JsonElement jsonElement = gson.toJsonTree(obj);
                Movie movie = gson.fromJson(jsonElement, Movie.class);
                view.addFavoritID(movie.getId());
                Log.d("TAG", "ID " + movie.getId());
            }
        }).subscribeOn(Schedulers.io()).subscribe();

    }
}
