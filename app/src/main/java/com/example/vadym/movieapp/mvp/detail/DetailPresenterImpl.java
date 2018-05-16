package com.example.vadym.movieapp.mvp.detail;

import android.support.annotation.NonNull;

import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class DetailPresenterImpl implements DetailContract.DetailPresenter {

    private DetailContract.DetailView view;
    private DetailContract.DetailModel model;
    private CompositeDisposable compositeDisposable;
    private String titleMovie = null;
    private String image = null;
    private String overviewDet = null;
    private Set<String> setID = new HashSet<>();

    @Inject
    public DetailPresenterImpl(DetailContract.DetailView view, DetailContract.DetailModel model) {
        this.view = view;
        this.model = model;
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void getMovieDetail(String id) {
        Single<MovieDetails> detailsObservable = MovieRetrofit.getRetrofit().getMovieDetails(id, model.getLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(movieDetails -> {

                    getValueFromMovieDatils(movieDetails);
                    view.fillTextView(movieDetails);
                })
                .onErrorReturn(throwable -> {
                    view.showError(throwable);
                    return new MovieDetails();
                });

        Disposable disposable = detailsObservable.subscribe();
        compositeDisposable.add(disposable);
    }

    private void getValueFromMovieDatils(MovieDetails movieDetails) {

        image = movieDetails.getPoster();
        overviewDet = movieDetails.getOverview();
        titleMovie = movieDetails.getTitle();
    }

    @Override
    public void addToBD(String id, Movie movie) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            model.addToFirebase(movie.getId(), movie);
        else
            model.getViewModel().insertItem(movie);
    }

    @Override
    public void deleteFromBD(Movie movie) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            model.deleteFromFirebase(movie.getId());
        else
            model.getViewModel().deleteByID(movie.getId());
    }


    @Override
    public void onDestroy() {
        view = null;
        if (compositeDisposable != null)
            compositeDisposable.clear();
    }

    @Override
    public Set<String> getID() {
        return setID;
    }

    @Override
    public void subcribeOnUI() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            readFromFirebase();
        } else {
            compositeDisposable.add(model.getViewModel().getItems()
                    .subscribeOn(Schedulers.io())
                    .flatMap(Flowable::fromIterable)
                    .flatMapCompletable(this::addMovieIDToSet)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }
    }

    private Completable addMovieIDToSet(Movie movie) {
        return Completable.fromAction(() -> {
            setID.add(movie.getId());
        });
    }

    @Override
    public void readFromFirebase() {
        model.getFirestoreDB().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Map<Object, Object> map = new HashMap<>();
                        map.putAll(snapshot.getData());

                        for (Object obj : map.values()) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(obj);
                            Movie movieGson = gson.fromJson(jsonElement, Movie.class);
                            setID.add(movieGson.getId());
                        }
                        view.init();
                    }
                }
            }
        });
    }
}
