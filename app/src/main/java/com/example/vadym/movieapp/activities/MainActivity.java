package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.api.ApiError;
import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.data.listMovie.MovieRecyclerAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieResponce;
import com.example.vadym.movieapp.room.MovieListModel;
import com.example.vadym.movieapp.util.ErrorUtil;
import com.example.vadym.movieapp.util.UpdateListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnStarClickListener, OnUpdateRecyclerAdapterListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar bar;
    @BindView(R.id.search)
    SearchView searchView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cardErrorView)
    CardView cardView;
    @BindView(R.id.errorTitle)
    TextView errorText;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private MovieRecyclerAdapter adapter;
    private String searchText;
    private int total = 0;
    private boolean isLoading = false;
    private int page = 1;

    private MovieListModel viewModel;
    private Set<String> dbLoadList = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MovieListModel.class);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        UpdateListener.setOnUpdateRecyclerListener(this);
        //viewModel.deleteAll();
        subscribeUIMovie();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new MovieRecyclerAdapter(dbLoadList);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchText = query;
                adapter.clear();
                page = 1;
                isLoading = true;
                loadMoreData(query, page);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (isLoading) return;

                int lastVisibleItemCount = manager.findLastVisibleItemPosition();
                int allLoadedItemsCount = adapter.getItemCount();
                int loadShouldStartPosition = (int) (allLoadedItemsCount * 0.8);

                if (loadShouldStartPosition <= lastVisibleItemCount && allLoadedItemsCount < total) {
                    page++;
                    isLoading = true;
                }

                if (isLoading)
                    loadMoreData(searchText, page);

            }
        });

    }

    private void subscribeUIMovie() {
        viewModel.getItems().observe(MainActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> list) {
                if (list == null)
                    return;
                for (int i = 0; i < list.size(); i++) {
                    Movie movie = list.get(i);
                    dbLoadList.add(movie.getId());
                    Log.d("TAG", " Item " + movie.getTitle() + " bool " + movie.isFavorite());
                    Log.d("TAG", " Size " + dbLoadList.size() + " ID " + movie.getId());
                }
            }
        });

    }

    private void loadMoreData(String searchText, int page) {

        cardView.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);
        Call<MovieResponce> responseCall = MovieRetrofit.getRetrofit().getMovie(searchText, page);
        responseCall.enqueue(new Callback<MovieResponce>() {
            @Override
            public void onResponse(Call<MovieResponce> call, Response<MovieResponce> response) {
                Log.d("TAG", "code " + response.code());
                if (response.isSuccessful()) {

                    MovieResponce movieResponce = response.body();
                    total = Integer.parseInt(movieResponce.getTotalResults());
                    List<Movie> movies = movieResponce.getMovieList();

                    adapter.addAll(movies);
                    isLoading = false;

                    bar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bar.setVisibility(View.GONE);
                        }
                    }, 1000);
                } else {
                    ApiError error = ErrorUtil.parseError(response);
                    Log.d("TAG", error.getMessage());
                    showFailView(error.getMessage());

                }
            }

            @Override
            public void onFailure(Call<MovieResponce> call, Throwable t) {
                showFailView(t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_favorite) {
            Intent intent = new Intent(MainActivity.this, FavoriteListActivity.class);
            startActivity(intent);
        }else if (id == R.id.nav_login) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }

        // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFailView(String message) {

        bar.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);

        TextView errorText = cardView.findViewById(R.id.errorTitle);
        if (errorText != null)
            errorText.setText(message);
    }


    @Override
    public void onClickStar(int position) {
        Movie movie = adapter.getMovie(position);
        if (movie != null) {
            movie.setFavorite(!movie.isFavorite());
            adapter.notifyItemChanged(position);

            if (movie.isFavorite()) {
                addToBD(movie);
                adapter.setFavoritID(movie.getId());

            } else {
                deleteFromBD(movie);
                adapter.deleteFavoritID(movie.getId());
            }
        }

    }

    private void addToBD(Movie movie) {

        viewModel.insertItem(movie);
    }

    private void deleteFromBD(Movie movie) {
        viewModel.deleteItem(movie);
    }

    @Override
    public void updateRecyclerAdapter(String id) {
        if (adapter.ifExist(id)) {
            adapter.deleteFavoritID(id);
        }
    }
}
