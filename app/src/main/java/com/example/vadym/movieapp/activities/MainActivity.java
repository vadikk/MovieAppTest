package com.example.vadym.movieapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.api.ApiError;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.dagger.MovieAppAplication;
import com.example.vadym.movieapp.dagger.ActivityMovieModule;
import com.example.vadym.movieapp.dagger.main.DaggerActivityComponent;
import com.example.vadym.movieapp.dagger.main.MvpContractModule;
import com.example.vadym.movieapp.data.listMovie.MovieRecyclerAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.mvp.main.MainContract;
import com.example.vadym.movieapp.service.GenreService;
import com.example.vadym.movieapp.util.BottomSheet;
import com.example.vadym.movieapp.util.ErrorUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnStarClickListener, OnBottomSheetListener, OnMovieClickListener, MainContract.MovieView {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.progressBar)
    ProgressBar bar;
    @BindView(R.id.search)
    SearchView searchView;
    @BindView(R.id.cardErrorView)
    CardView cardView;
    @BindView(R.id.errorTitle)
    TextView errorText;
    @BindView(R.id.filterButton)
    ImageButton filterBtn;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @Inject
    MovieRecyclerAdapter adapter;
    @Inject
    LinearLayoutManager manager;
    @Inject
    MainContract.MoviePresenter presenter;

    private BottomSheet bottomSheet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        DaggerActivityComponent.builder()
                .movieComponent(MovieAppAplication.get(this).getMovieComponent())
                .mvpContractModule(new MvpContractModule(this))
                .activityMovieModule(new ActivityMovieModule(this))
                .build()
                .inject(this);

        setSupportActionBar(toolbar);

        bottomSheet = new BottomSheet();
        bottomSheet.setBottomListener(this);

        startService(new Intent(this, GenreService.class));

        adapter.setOnClickListener(this);
        adapter.setOnMovieListener(this);

        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                adapter.clear();
                presenter.clickEnterOnSearchView(query, 1, presenter.getLanguage());
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

                presenter.recyclerScrollListener(manager.findLastVisibleItemPosition(), adapter.getItemCount());

            }
        });
        presenter.subscribeUIMovie();

        filterBtn.setOnClickListener(view -> {
            showDetailDialog();

        });

        init();
        navigationView.setCheckedItem(R.id.nav_films);
    }

    private void init() {
        View headerView = navigationView.getHeaderView(0);
        TextView userID = headerView.findViewById(R.id.userID);
        TextView userEmail = headerView.findViewById(R.id.userEmail);
        ImageView imageViewUser = headerView.findViewById(R.id.imageViewUser);

        String id = null;
        String email = null;
        String image = null;

        SharedPreferences preferences = getSharedPreferences(Constant.APP_PREFS, Context.MODE_PRIVATE);
        id = preferences.getString(Constant.NAME, null);
        email = preferences.getString(Constant.EMAIL, null);
        image = preferences.getString(Constant.IMAGE, null);

        if (image != null)
            Picasso.with(this).load(image).into(imageViewUser);

        if (id == null || email == null || image == null) {
            id = getResources().getString(R.string.android_studio);
            email = getResources().getString(R.string.android_studio_android_com);
            imageViewUser.setImageResource(R.mipmap.ic_launcher_round);
        }

        userID.setText(id);
        userEmail.setText(email);
    }

    @Override
    public Completable setFavoritID(Movie movie) {
        return Completable.fromAction(() -> {
            adapter.setFavoritID(movie.getId());
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Movie movie = null;
        if (requestCode == 1) {
            switch (resultCode) {
                case 1:
                    movie = (Movie) data.getSerializableExtra(MovieDetailsActivity.MOVIEDETAILS);
                    boolean isClick = data.getBooleanExtra("Bool", false);
                    if (isClick) {
                        adapter.setFavoritID(movie.getId());
                        adapter.addMovieWithID(movie);

                    } else {
                        adapter.deleteFavoritID(movie.getId());
                    }
                    break;
                case 2:
                    List<Movie> deleteList = (List<Movie>) data.getSerializableExtra(FavoriteListActivity.FAVORITE_MOVIE);
                    if (deleteList != null) {
                        for (Movie deleteMovie : deleteList) {
                            if (adapter.ifExist(deleteMovie.getId())) {
                                adapter.deleteFavoritID(deleteMovie.getId());
                            }
                        }
                    }
                    break;
                default:
                    break;
            }

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (ProfileActivity.changeBD) {
            showProgressBar();
            ProfileActivity.changeBD = false;
            adapter.clear();
            adapter.deleteAllFavoritID();
            hideProgressBar();
            presenter.subscribeUIMovie();

        }

        navigationView.setCheckedItem(R.id.nav_films);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
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
//            startActivity(intent);
            startActivityForResult(intent, 1);
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
            adapter.notifyItemChanged(position);

            if (movie.isFavorite()) {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    adapter.setFavoritID(movie.getId());
                    presenter.addToFirebase(movie.getId(), movie);
                } else {
                    presenter.addToBD(movie);
                    adapter.setFavoritID(movie.getId());
                }

            } else {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    adapter.deleteFavoritID(movie.getId());
                    presenter.deleteFromFirebase(movie.getId());
                } else {
                    presenter.deleteFromBD(movie);
                    adapter.deleteFavoritID(movie.getId());
                }
            }
        }

    }

    private void showDetailDialog() {

        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public void submit() {
        adapter.clear();
        presenter.loadMoreFilterData(1, bottomSheet.getVoteCountText(), bottomSheet.getYearText(), bottomSheet.getRuntimeText());
    }

    @Override
    public void onMovieClick(int position) {
        Intent intent = new Intent(MainActivity.this, MovieDetailsActivity.class);
        Movie movie = adapter.getMovie(position);
        if (movie == null)
            return;
        intent.putExtra(MovieDetailsActivity.MOVIEDETAILS, movie);
//        startActivity(intent);
        startActivityForResult(intent, 1);
    }

    @Override
    public void showProgressBar() {
        bar.setVisibility(View.VISIBLE);

    }

    @Override
    public void hideProgressBar() {
        Completable.timer(1 / 2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> bar.setVisibility(View.GONE));
    }

    @Override
    public Completable showData(List<Movie> list) {
        cardView.setVisibility(View.INVISIBLE);
        return Completable.fromAction(() -> {
            adapter.addAll(list);

        });
    }

    @Override
    public void showErrorView(Throwable throwable) {
        HttpException httpException = (HttpException) throwable;
        Response response = httpException.response();
        ApiError errorMessage = ErrorUtil.parseError(response);
        showFailView(errorMessage.getMessage());
    }

    @Override
    public void addFavoritID(String favoritID) {
        adapter.setFavoritID(favoritID);
    }
}
