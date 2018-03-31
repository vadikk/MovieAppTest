package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.data.favoriteMovie.FavoriteMovieAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FavoriteListActivity extends AppCompatActivity
        implements OnMovieClickListener {

    public static String FAVORITE_MOVIE = "favorite";

    @BindView(R.id.recyclerViewFavorite)
    RecyclerView recyclerView;

    private DocumentReference firestoreDB = FirebaseFirestore.getInstance().collection("movie").document("movieData");
    private FavoriteMovieAdapter adapter;
    private List<Movie> favoriteMovieList = new ArrayList<>();
    private MovieListModel viewModel;
    private CompositeDisposable compositeDB;
    private List<Movie> deleteListMovie = new ArrayList<>();
    private Movie deletedMovie;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite_list);
        setTitle(getString(R.string.favorite_list));

        ButterKnife.bind(this);

        compositeDB = new CompositeDisposable();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        viewModel = ViewModelProviders.of(this).get(MovieListModel.class);

        subscribeUIMovie();
        deleteItemBySwipe();
    }

    @Override
    protected void onDestroy() {
        if (compositeDB != null)
            compositeDB.clear();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteIdFromAdapter(List<Movie> movies) {
        Intent intent = new Intent();
        intent.putExtra(FAVORITE_MOVIE, (Serializable) movies);
        setResult(2, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                deletedMovie = (Movie) data.getSerializableExtra(MovieDetailsActivity.MOVIEDETAILS);
                if (deletedMovie.isFavorite()) {
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.updateFavoriteList(deletedMovie);
                    deleteListMovie.add(deletedMovie);
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra(FAVORITE_MOVIE, (Serializable) deleteListMovie);
        setResult(2, intent);
    }

    private void deleteItemBySwipe() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                Toast.makeText(getApplicationContext()," On Swiped ",Toast.LENGTH_SHORT).show();
                int position = viewHolder.getAdapterPosition();
                Movie movie = adapter.getMovie(position);

                deleteListMovie.add(movie);
                deleteIdFromAdapter(deleteListMovie);
                adapter.deleteFromList(position);
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    deleteFromFirebase(movie.getId());
                } else {
                    deleteFromBD(movie);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteFromBD(Movie movie) {
        viewModel.deleteItem(movie);
    }

    private void subscribeUIMovie() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            readFromFirebase();
        } else {
            compositeDB.add(viewModel.getItems().subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(list -> {

                        if (viewModel != null)
                            favoriteMovieList = list;

                        adapter = new FavoriteMovieAdapter();
                        adapter.addFavoriteMovieAdapter(favoriteMovieList);
                        adapter.setOnMovieClickListener(FavoriteListActivity.this);
                        recyclerView.setAdapter(adapter);
                    }));
        }

    }


    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(FavoriteListActivity.this, MovieDetailsActivity.class);
        Movie movie = adapter.getMovie(position);
        if (movie == null)
            return;
        intent.putExtra(MovieDetailsActivity.MOVIEDETAILS, movie);
        startActivityForResult(intent, 1);
    }

    private void readFromFirebase() {

        firestoreDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Map<Object, Object> map = new HashMap<>();
                        map.putAll(snapshot.getData());

                        adapter = new FavoriteMovieAdapter();

                        for (Object obj : map.values()) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(obj);
                            Movie movie = gson.fromJson(jsonElement, Movie.class);
                            favoriteMovieList.add(movie);
                        }
                        adapter.addFavoriteMovieAdapter(favoriteMovieList);
                        adapter.setOnMovieClickListener(FavoriteListActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                }
            }
        });
    }

    private void deleteFromFirebase(String position) {

        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put(position, FieldValue.delete());

        firestoreDB.update(deleteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(FavoriteListActivity.this, "Successfully deleted!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
