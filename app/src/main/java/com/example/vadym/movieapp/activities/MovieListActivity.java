package com.example.vadym.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.vadym.movieapp.api.ApiError;
import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.data.MovieRecyclerAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieResponce;
import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.util.ErrorUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity implements OnMovieClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar bar;
    @BindView(R.id.search)
    SearchView searchView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cardError_view)
    CardView cardView;

    private MovieRecyclerAdapter adapter;
    private String searchText;
    private int total = 0;
    private boolean isLoading = false;
    private int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new MovieRecyclerAdapter();
        adapter.setOnMovieClickListener(this);
        recyclerView.setAdapter(adapter);

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

    private void loadMoreData(String searchText, int page) {

        cardView.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);
        Call<MovieResponce> responseCall = MovieRetrofit.getRetrofit().getMovie( searchText, page);
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
                    Log.d("TAG", error.getStatus_message());
                    showFailView();
                }
            }

            @Override
            public void onFailure(Call<MovieResponce> call, Throwable t) {
                showFailView();
            }
        });
    }


    @Override
    public void onMovieClick(int position) {

        Intent intent = new Intent(MovieListActivity.this, MovieDetailsActivity.class);
        intent.putExtra("detail", adapter.getMovie(position));
        startActivity(intent);
    }

    private void showFailView() {

        bar.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);
    }

}
