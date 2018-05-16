package com.example.vadym.movieapp.mvp.main;

import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieResponce;
import com.example.vadym.movieapp.service.Genres;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface MainContract {

    interface MovieView {
        void showProgressBar();

        void hideProgressBar();

        Completable showData(List<Movie> list);

        void showErrorView(Throwable throwable);

        Completable setFavoritID(Movie movie);

        void addFavoritID(String favoritID);
    }

    interface MoviePresenter {
        void onDestroy();

        void clickEnterOnSearchView(String text, int page, String language);

        void recyclerScrollListener(int lastVisibleItemCount, int allLoadedItemsCount);

        String getLanguage();

        void loadMoreFilterData(int page, String vote, String year, String runTime);

        void subscribeUIMovie();

        void addToBD(Movie movie);

        void deleteFromBD(Movie movie);

        void addToFirebase(String position, Movie movie);

        void deleteFromFirebase(String position);

    }

    interface MovieModel {
        Flowable<MovieResponce> getMovie(String name, int page, String language);

        String getLanguage();

        Single<List<Genres.Genre>> getGenres();

        Flowable<List<Movie>> getItems();

        void addToBD(Movie movie);

        void deleteFromBD(Movie movie);

        void addToFirebase(String position, Movie movie);

        void deleteFromFirebase(String position);

        DocumentReference getFirestoreDB();
    }
}
