package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.data.favoriteMovie.FavoriteMovieAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.room.MovieListModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FavoriteListActivity extends AppCompatActivity
        implements OnMovieClickListener {

    public static final String FAVORITE_MOVIE = "favorite";

    @BindView(R.id.recyclerViewFavorite)
    RecyclerView recyclerView;

    private FavoriteMovieAdapter adapter;
    private List<Movie> favoriteMovieList = new ArrayList<>();
    private MovieListModel viewModel;
    private CompositeDisposable compositeDB;


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


    private void deleteIdFromAdapter(Movie movie) {
        Intent intent = new Intent();
        intent.putExtra(FAVORITE_MOVIE, movie);
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
//                updateListener.updateAdapter(movie.getId());
                deleteIdFromAdapter(movie);
                adapter.deleteFromList(position);
                deleteFromBD(movie);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void deleteFromBD(Movie movie) {
        viewModel.deleteItem(movie);
    }

    private void subscribeUIMovie() {

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


    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(FavoriteListActivity.this, MovieDetailsActivity.class);
        Movie movie = adapter.getMovie(position);
        if (movie == null)
            return;
        intent.putExtra(MovieDetailsActivity.MOVIEDETAILS, movie);
        startActivity(intent);
    }

}
