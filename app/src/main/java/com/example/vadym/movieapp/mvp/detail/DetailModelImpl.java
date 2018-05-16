package com.example.vadym.movieapp.mvp.detail;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

public class DetailModelImpl implements DetailContract.DetailModel {

    private SharedPreferences sharedPreferences;
    private DocumentReference firestoreDB;
    private MovieListModel viewModel;
    private Map<String, Movie> movieMap = new HashMap<>();

    @Inject
    public DetailModelImpl(SharedPreferences sharedPreferences, DocumentReference firestoreDB, MovieListModel viewModel) {
        this.sharedPreferences = sharedPreferences;
        this.firestoreDB = firestoreDB;
        this.viewModel = viewModel;
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
    public void deleteFromFirebase(String id) {
        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put(id, FieldValue.delete());

        firestoreDB.update(deleteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "Successfully deleted!");
            }
        });
    }

    @Override
    public void addToFirebase(String id, Movie movie) {
        movieMap.put(id, movie);

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
    public Map<String, Movie> getMovieMap() {
        return movieMap;
    }

    @Override
    public DocumentReference getFirestoreDB() {
        return firestoreDB;
    }

    @Override
    public MovieListModel getViewModel() {
        return viewModel;
    }
}
