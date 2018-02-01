package com.example.vadym.movieapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.example.vadym.movieapp.Api.MovieRetrofit;
import com.example.vadym.movieapp.Data.MovieRecyclerAdapter;
import com.example.vadym.movieapp.Model.Movie;
import com.example.vadym.movieapp.Model.MovieResponce;
import com.example.vadym.movieapp.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieListActivity extends AppCompatActivity implements OnMovieClickListener {
    // TODO: 1/31/18 Нема ButterKnife.
    private RecyclerView recyclerView;
    private MovieRecyclerAdapter adapter;
    private String searchText;
    private int total = 0;
    private boolean isLoading = true;
    private int page = 1;
    private ProgressBar bar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchView = findViewById(R.id.search);

        bar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);

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
                loadMoreData(query,page);
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

                if(isLoading) return;

                int lastVisibleItemCount = manager.findLastVisibleItemPosition();
                int allLoadedItemsCount = adapter.getItemCount();
                int loadShouldStartPosition = (int)(allLoadedItemsCount*0.8);
                // TODO: 1/31/18 Нема форматування.
                if(loadShouldStartPosition<=lastVisibleItemCount && allLoadedItemsCount<total){
                    page++;
                    isLoading = true;
                }

                if(isLoading)
                    loadMoreData(searchText,page);
            }
        });

    }

    private void loadMoreData(String searchText, int page){
        // TODO: 1/31/18 Навіщо ти це залишив? Ти не розумєш, що робиться ця функція? Для тебе не було нічого дивного, що апа довго шукає фільм?
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(isLoading){
                    isLoading = false;
                    bar.setVisibility(View.VISIBLE);
                    getMovie(searchText,page);
                }
            }
        },3000);
    }

    private void getMovie(String search, int page){

       Call<MovieResponce> responseCall = MovieRetrofit.getRetrofit().getMovie(search,page);
        responseCall.enqueue(new Callback<MovieResponce>() {
            @Override
            public void onResponse(Call<MovieResponce> call, Response<MovieResponce> response) {
                if(response.isSuccessful()){

                    MovieResponce movieResponce = response.body();
                    total = Integer.parseInt(movieResponce.getTotal_results());
                    List<Movie> movies = movieResponce.getMovieList();
                    adapter.addAll(movies);
                    isLoading = false;

                    // TODO: 1/31/18 Також дурня, я це робив для того, щоб прогрес бар більше часу світився, бо він швидко пропадав. А ти не спитав навіь навіщо це.
                    bar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            bar.setVisibility(View.GONE);
                        }
                    },2000);
                }
            }

            @Override
            public void onFailure(Call<MovieResponce> call, Throwable t) {
                // TODO: 1/31/18 Нема обпрацюванян помилки.
            }
        });

    }

    @Override
    public void onMovieClick(int position) {
        // TODO: 1/31/18 Нема форматування.
        Intent intent = new Intent(MovieListActivity.this,MovieDetailsActivity.class);
        intent.putExtra("detail", adapter.getMovie(position));
        startActivity(intent);
    }

}
