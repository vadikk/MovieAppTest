package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.data.favoriteMovie.FavoriteMovieAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.room.MovieListModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteListActivity extends AppCompatActivity {

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
    }

    private void subscribeUIMovie() {

        viewModel.getItems().observe(FavoriteListActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> list) {

                if (list != null && viewModel.getItems().getValue() != null) {
                    int size = viewModel.getItems().getValue().size();
                    Log.d("TAG", " size " + size);
                }

                if (viewModel != null)
                    favoriteMovieList = viewModel.getItems().getValue();

                adapter = new FavoriteMovieAdapter(favoriteMovieList);
                recyclerView.setAdapter(adapter);


                for (int i = 0; i < list.size(); i++) {
                    Movie movie = list.get(i);
                    Log.d("TAG", " Item " + movie.getTitle());
                }
            }
        });
    }
}
