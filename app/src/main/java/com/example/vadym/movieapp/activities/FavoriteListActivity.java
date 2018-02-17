package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.data.favoriteMovie.FavoriteMovieAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.room.MovieListModel;
import com.example.vadym.movieapp.util.UpdateListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteListActivity extends AppCompatActivity implements OnMovieClickListener {

    @BindView(R.id.recyclerViewFavorite)
    RecyclerView recyclerView;

    private FavoriteMovieAdapter adapter;
    private List<Movie> favoriteMovieList = new ArrayList<>();
    private MovieListModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        ButterKnife.bind(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        viewModel = ViewModelProviders.of(this).get(MovieListModel.class);

        subscribeUIMovie();
        deleteItemBySwipe();
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
                UpdateListener.updateAdapter(movie.getId());
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

        viewModel.getItems().observe(FavoriteListActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> list) {

                if (list != null && viewModel.getItems().getValue() != null) {
                    int size = viewModel.getItems().getValue().size();
                }

                if (viewModel != null)
                    favoriteMovieList = viewModel.getItems().getValue();

                adapter = new FavoriteMovieAdapter(favoriteMovieList);
                adapter.setOnMovieClickListener(FavoriteListActivity.this);
                recyclerView.setAdapter(adapter);

            }
        });
    }

    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(FavoriteListActivity.this, MovieDetailsActivity.class);
        Movie movie = adapter.getMovie(position);
        if (movie == null)
            return;
        intent.putExtra("detail", movie.getId());
        startActivity(intent);
    }
}
