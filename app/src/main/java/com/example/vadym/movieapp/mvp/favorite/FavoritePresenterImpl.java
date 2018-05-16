package com.example.vadym.movieapp.mvp.favorite;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.vadym.movieapp.model.Movie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FavoritePresenterImpl implements FavoriteContract.FavoritePresenter {

    private FavoriteContract.FavoriteView view;
    private FavoriteContract.FavoriteModel model;

    private List<Movie> favoriteMovieList = new ArrayList<>();
    private CompositeDisposable compositeDB;
    private List<Movie> deleteListMovie = new ArrayList<>();
    private boolean isFirstLoad = false;

    @Inject
    public FavoritePresenterImpl(FavoriteContract.FavoriteView view, FavoriteContract.FavoriteModel model) {
        compositeDB = new CompositeDisposable();
        this.view = view;
        this.model = model;
    }

    @Override
    public void onDestroy() {
        view = null;
        if (compositeDB != null)
            compositeDB.clear();
    }

    @Override
    public void subscribeUIMovie() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            readFromFirebase();
        } else {
            compositeDB.add(model.getItems().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> {
                        if (!isFirstLoad) {
                            isFirstLoad = true;
                            if (model.getViewModel() != null)
                                favoriteMovieList = list;

                            view.getAdapter().addFavoriteMovieAdapter(favoriteMovieList);
                        }
                    }));
        }
    }

    @Override
    public void deleteItemBySwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Movie movie = view.getAdapter().getMovie(position);

                deleteListMovie().add(movie);
                view.deleteIdFromAdapter(deleteListMovie());
                view.getAdapter().deleteFromList(position);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    model.deleteFromFirebase(movie.getId());
                } else {
                    model.deleteFromBD(movie);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(view.getRecyclerView());
    }

    @Override
    public List<Movie> deleteListMovie() {
        return deleteListMovie;
    }

    private void readFromFirebase() {

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
                            Movie movie = gson.fromJson(jsonElement, Movie.class);
                            favoriteMovieList.add(movie);
                        }
                        view.getAdapter().addFavoriteMovieAdapter(favoriteMovieList);
                        view.getRecyclerView().setAdapter(view.getAdapter());
                    }
                }
            }
        });
    }

}
