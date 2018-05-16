package com.example.vadym.movieapp.mvp.favorite;

import android.support.v7.widget.RecyclerView;

import com.example.vadym.movieapp.data.favoriteMovie.FavoriteMovieAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import io.reactivex.Flowable;

public interface FavoriteContract {

    interface FavoriteView {
        FavoriteMovieAdapter getAdapter();

        void deleteIdFromAdapter(List<Movie> movies);

        RecyclerView getRecyclerView();
    }

    interface FavoritePresenter {
        void onDestroy();

        void subscribeUIMovie();

        void deleteItemBySwipe();

        List<Movie> deleteListMovie();
    }

    interface FavoriteModel {
        void deleteFromFirebase(String position);

        Flowable<List<Movie>> getItems();

        MovieListModel getViewModel();

        DocumentReference getFirestoreDB();

        void deleteFromBD(Movie movie);
    }
}
