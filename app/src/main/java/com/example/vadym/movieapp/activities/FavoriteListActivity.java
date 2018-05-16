package com.example.vadym.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.dagger.MovieAppAplication;
import com.example.vadym.movieapp.dagger.favorite.DaggerFavoriteActivityComponent;
import com.example.vadym.movieapp.dagger.favorite.MvpFavoriteModule;
import com.example.vadym.movieapp.dagger.ActivityMovieModule;
import com.example.vadym.movieapp.data.favoriteMovie.FavoriteMovieAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.mvp.favorite.FavoriteContract;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoriteListActivity extends AppCompatActivity
        implements OnMovieClickListener, FavoriteContract.FavoriteView {

    public static String FAVORITE_MOVIE = "favorite";

    @BindView(R.id.recyclerViewFavorite)
    RecyclerView recyclerView;

    @Inject
    FavoriteMovieAdapter adapter;
    @Inject
    LinearLayoutManager manager;
    @Inject
    FavoriteContract.FavoritePresenter presenter;

    private Movie deletedMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_favorite_list);
        setTitle(getString(R.string.favorite_list));

        ButterKnife.bind(this);

        DaggerFavoriteActivityComponent.builder()
                .activityMovieModule(new ActivityMovieModule(this))
                .movieComponent(MovieAppAplication.get(this).getMovieComponent())
                .mvpFavoriteModule(new MvpFavoriteModule(this))
                .build()
                .inject(this);

        adapter.setOnMovieClickListener(FavoriteListActivity.this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        presenter.subscribeUIMovie();
        presenter.deleteItemBySwipe();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
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

    @Override
    public void deleteIdFromAdapter(List<Movie> movies) {
        Intent intent = new Intent();
        intent.putExtra(FAVORITE_MOVIE, (Serializable) movies);
        setResult(2, intent);
    }

    @Override
    public RecyclerView getRecyclerView() {
        return recyclerView;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == 1) {
                deletedMovie = (Movie) data.getSerializableExtra(MovieDetailsActivity.MOVIEDETAILS);
                if (!deletedMovie.isFavorite()) {
                    adapter.updateFavoriteList(deletedMovie);
                    presenter.deleteListMovie().add(deletedMovie);
                }
            }
        }
        Intent intent = new Intent();
        intent.putExtra(FAVORITE_MOVIE, (Serializable) presenter.deleteListMovie());
        setResult(2, intent);
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

    @Override
    public FavoriteMovieAdapter getAdapter() {
        return adapter;
    }

}
