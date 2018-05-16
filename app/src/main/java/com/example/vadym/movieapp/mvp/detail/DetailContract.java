package com.example.vadym.movieapp.mvp.detail;

import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.firebase.firestore.DocumentReference;

import java.util.Map;
import java.util.Set;

public interface DetailContract {

    interface DetailView {
        void shoProgressBar();

        void showError(Throwable throwable);

        void fillTextView(MovieDetails responce);

        void init();
    }

    interface DetailPresenter {
        void getMovieDetail(String id);

        void addToBD(String id, Movie movie);

        void deleteFromBD(Movie movie);

        void onDestroy();

        Set<String> getID();

        void subcribeOnUI();

        void readFromFirebase();
    }

    interface DetailModel {
        String getLanguage();

        void deleteFromFirebase(String id);

        void addToFirebase(String id, Movie movie);

        Map<String, Movie> getMovieMap();

        DocumentReference getFirestoreDB();

        MovieListModel getViewModel();
    }
}
