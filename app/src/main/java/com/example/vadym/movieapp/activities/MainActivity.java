package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.data.listMovie.MovieRecyclerAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieResponce;
import com.example.vadym.movieapp.room.MovieListModel;
import com.example.vadym.movieapp.service.GenreService;
import com.example.vadym.movieapp.service.Genres;
import com.example.vadym.movieapp.util.BottomSheet;
import com.example.vadym.movieapp.util.UpdateListener;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnStarClickListener, OnUpdateRecyclerAdapterListener, OnBottomSheetListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar bar;
    @BindView(R.id.search)
    SearchView searchView;
    //    @BindView(R.id.toolbar)
//    Toolbar toolbar;
    @BindView(R.id.cardErrorView)
    CardView cardView;
    @BindView(R.id.errorTitle)
    TextView errorText;
    @BindView(R.id.filterButton)
    ImageButton filterBtn;


    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    private MovieRecyclerAdapter adapter;
    private String searchText;
    private int total = 0;
    private boolean isLoading = false;
    private boolean isSearch = false;
    private boolean isfilter = false;
    private int page = 1;

    private MovieListModel viewModel;
    private Set<String> dbLoadList = new HashSet<>();
    private SharedPreferences sharedPreferences;
    private CompositeDisposable compositeDisposable;
    private CompositeDisposable compositeDB;
    private CompositeDisposable compositeDBGenres;
    private BottomSheet bottomSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // TODO: 3/6/18 При поверненні назад із favourite - не зкидує селект із favourite.
        // TODO: 3/6/18 При повторному вході - не показує інфу про користувача.
        // TODO: 3/6/18 Клін на фільм не працює.

        viewModel = ViewModelProviders.of(this).get(MovieListModel.class);

        ButterKnife.bind(this);
//        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        compositeDisposable = new CompositeDisposable();
        compositeDB = new CompositeDisposable();
        compositeDBGenres = new CompositeDisposable();

        bottomSheet = new BottomSheet();
        bottomSheet.setBottomListener(this);

        UpdateListener.setOnUpdateRecyclerListener(this);
        //viewModel.deleteAll();

        startService(new Intent(this, GenreService.class));


        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);

        adapter = new MovieRecyclerAdapter(dbLoadList);
        adapter.setOnClickListener(this);
        recyclerView.setAdapter(adapter);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
                isSearch = true;
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

                if (isLoading && isSearch)
                    loadMoreData(searchText, page);
                if (isLoading && isfilter)
                    loadMoreFilterData(page, bottomSheet.getVoteCountText(), bottomSheet.getYearText(), bottomSheet.getRuntimeText());

            }
        });
        subscribeUIMovie();

        filterBtn.setOnClickListener(view -> {
            showDetailDialog();

        });
    }

    private void subscribeUIMovie() {

        Flowable<List<Movie>> flowable = viewModel.getItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDB.add(flowable.subscribe(movieList -> {
            if (movieList == null) {
                return;
            }
// TODO: 3/6/18 flatMap тобі в допомогу.
            for (int i = 0; i < movieList.size(); i++) {
                Movie movie = movieList.get(i);
                dbLoadList.add(movie.getId());
//                Log.d("TAG", " Item " + movie.getTitle() + " bool " + movie.isFavorite());
                Log.d("TAG", " Size " + dbLoadList.size() + " ID " + movie.getId());
            }
        }, error -> {
            Log.d("TAG", "Error");
        }));

        //Глянь чего более раза выводит это
        // TODO: 3/6/18 Тому що ти  спочатку вижираєш збережені дані, а потім срвіс базу оновлює і ти другий раз отримуєш дані.
        compositeDBGenres.add(viewModel.getGenres().observeOn(AndroidSchedulers.mainThread()).subscribe(list -> {
            if (list == null)
                return;

            Log.d("TAG", "SIze " + list.size());

            for (Genres.Genre gen : list) {
                Log.d("TAG", " ID " + gen.getId() + " name " + gen.getName());
            }
        }));

    }

    private String getLanguage() {
        String language = null;
        // TODO: 3/6/18 При чому тут 20???
        int id = sharedPreferences.getInt("language", 20);
        switch (id) {
            case 0:
                language = "en";
                return language;
            case 1:
                language = "de";
                return language;
            case 2:
                language = "ru";
                return language;
            // TODO: 3/6/18 Встав дефолтну мову.
            default:
                return null;
        }
    }

    private void loadMoreData(String searchText, int page) {

        cardView.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);
        Flowable<MovieResponce> responseCall = MovieRetrofit.getRetrofit().getMovie(searchText, page, getLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Disposable disposable = responseCall.subscribe(movieResponce -> {

            MovieResponce responce = movieResponce;
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
        }, error -> {
//            ApiError errorMessage = ErrorUtil.parseError(error);
            Log.d("TAG", error.getMessage());
            showFailView(error.getMessage());
        });
        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null)
            compositeDisposable.clear();

        if (compositeDB != null)
            compositeDB.clear();

        if (compositeDBGenres != null)
            compositeDBGenres.clear();

        super.onDestroy();
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
        } else if (id == R.id.nav_login) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
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
            // TODO: 3/6/18 Краще спочатку робити щось в адаптері(видаляти елемент, додавати), а потім вже нотифікувати самого адаптера.
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


    private void showDetailDialog() {

        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public void submit() {
        adapter.clear();
        page = 1;
        loadMoreFilterData(page, bottomSheet.getVoteCountText(), bottomSheet.getYearText(), bottomSheet.getRuntimeText());
    }

    private void loadMoreFilterData(int page, String vote, String year, String runTime) {
        cardView.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);
        isLoading = true;
        isfilter = true;
        Flowable<MovieResponce> responseCall = MovieRetrofit.getRetrofit().getDiscoverMovie(getLanguage(), Constant.SORT,
                "false", "false", page, vote, year, runTime)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        Disposable disposable = responseCall.subscribe(movieResponce -> {

            MovieResponce responce = movieResponce;
            total = Integer.parseInt(movieResponce.getTotalResults());
            Log.d("TAG", "total " + total);
            List<Movie> movies = movieResponce.getMovieList();

            adapter.addAll(movies);
            isLoading = false;
            bar.postDelayed(new Runnable() {
                @Override
                public void run() {
                    bar.setVisibility(View.GONE);
                }
            }, 1000);
        }, error -> {
//            ApiError errorMessage = ErrorUtil.parseError(error);
            Log.d("TAG", error.getMessage());
            showFailView(error.getMessage());
        });
        compositeDisposable.add(disposable);
    }
}
