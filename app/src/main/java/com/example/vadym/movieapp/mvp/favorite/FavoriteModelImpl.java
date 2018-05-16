package com.example.vadym.movieapp.mvp.favorite;

import android.util.Log;

import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Flowable;

public class FavoriteModelImpl implements FavoriteContract.FavoriteModel {

    private DocumentReference firestoreDB;
    private MovieListModel viewModel;

    @Inject
    public FavoriteModelImpl(DocumentReference firestoreDB, MovieListModel viewModel) {
        this.firestoreDB = firestoreDB;
        this.viewModel = viewModel;
    }

    @Override
    public void deleteFromFirebase(String position) {
        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put(position, FieldValue.delete());

        firestoreDB.update(deleteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "Successfully deleted!");
            }
        });
    }

    @Override
    public Flowable<List<Movie>> getItems() {
        return viewModel.getItems();
    }

    @Override
    public MovieListModel getViewModel() {
        return viewModel;
    }

    @Override
    public DocumentReference getFirestoreDB() {
        return firestoreDB;
    }

    @Override
    public void deleteFromBD(Movie movie) {
        viewModel.deleteItem(movie);
    }
}
