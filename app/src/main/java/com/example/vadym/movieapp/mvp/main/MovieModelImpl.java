package com.example.vadym.movieapp.mvp.main;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.vadym.movieapp.api.MovieApi;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieResponce;
import com.example.vadym.movieapp.room.MovieListModel;
import com.example.vadym.movieapp.service.Genres;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;
import io.reactivex.Single;


public class MovieModelImpl implements MainContract.MovieModel {

    private SharedPreferences sharedPreferences;
    private MovieListModel viewModel;
    private DocumentReference firestoreDB;
    private MovieApi api;
    private String movieID = null;
    private Map<String, Movie> movieMap = new HashMap<>();


    @Inject
    public MovieModelImpl(SharedPreferences sharedPreferences, MovieListModel viewModel, DocumentReference firestoreDB, MovieApi api) {
        this.api = api;
        this.sharedPreferences = sharedPreferences;
        this.viewModel = viewModel;
        this.firestoreDB = firestoreDB;

    }

    @Override
    public Flowable<MovieResponce> getMovie(String name, int page, String language) {
        return api.getMovie(name, page, language);
    }

    @Override
    public String getLanguage() {
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

    @Override
    public Single<List<Genres.Genre>> getGenres() {
        return viewModel.getGenres();
    }

    @Override
    public Flowable<List<Movie>> getItems() {
        return viewModel.getItems();
    }

    @Override
    public void addToBD(Movie movie) {
        viewModel.insertItem(movie);
    }

    @Override
    public void deleteFromBD(Movie movie) {
        viewModel.deleteItem(movie);
    }

    @Override
    public DocumentReference getFirestoreDB() {
        return firestoreDB;
    }

    @Override
    public void addToFirebase(String position, Movie movie) {

        movieID = movie.getId();
        movieMap.put(movieID, movie);

        firestoreDB.set(movieMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot added with ID: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error adding document", e);
            }
        });
    }

    @Override
    public void deleteFromFirebase(String position) {

        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put(position, FieldValue.delete());

        firestoreDB.update(deleteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot successfully deleted!");
            }
        });
    }
}
