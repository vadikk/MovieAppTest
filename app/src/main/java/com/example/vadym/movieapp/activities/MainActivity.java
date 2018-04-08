package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.api.ApiError;
import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.dagger.MovieAppAplication;
import com.example.vadym.movieapp.data.listMovie.MovieRecyclerAdapter;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.room.MovieListModel;
import com.example.vadym.movieapp.service.GenreService;
import com.example.vadym.movieapp.service.Genres;
import com.example.vadym.movieapp.util.BottomSheet;
import com.example.vadym.movieapp.util.ErrorUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnStarClickListener, OnBottomSheetListener, OnMovieClickListener {

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
    @Named("movie")
    LinearLayoutManager manager;

    private String searchText;
    private int total = 0;
    private boolean isLoading = false;
    private boolean isSearch = false;
    private boolean isfilter = false;
    private int page = 1;

    private MovieListModel viewModel;
    private SharedPreferences sharedPreferences;
    private CompositeDisposable compositeDisposable;
    private BottomSheet bottomSheet;

    private DocumentReference firestoreDB = FirebaseFirestore.getInstance().collection("movie").document("movieData");
    private Map<String, Movie> movieMap = new HashMap<>();
    private String movieID = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MovieListModel.class);

        ButterKnife.bind(this);

        ((MovieAppAplication) getApplication()).getMovieComponent()
                .inject(this);

        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        compositeDisposable = new CompositeDisposable();

        bottomSheet = new BottomSheet();
        bottomSheet.setBottomListener(this);

        //viewModel.deleteAll();

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

    private void subscribeUIMovie() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Log.d("TAG", "isAuth " + "OKK");
            readFromFirebase();
        } else {
            Log.d("TAG", "isAuth " + "OK2");
            Flowable flowable = viewModel.getItems()
                    .subscribeOn(Schedulers.io())
                    .filter(list -> !list.isEmpty())
                    .flatMap(Flowable::fromIterable)
                    .flatMapCompletable(movie -> setFavoritID(movie)).toFlowable()
                    .observeOn(AndroidSchedulers.mainThread());

            Disposable disposableMovie = flowable.subscribe();
            compositeDisposable.add(disposableMovie);
        }


        Disposable disposable = viewModel.getGenres()
                .subscribeOn(Schedulers.io())
                .filter(genres -> !genres.isEmpty())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(genres -> {
                    if (GenreService.loadFromDB) {
                        GenreService.loadFromDB = false;
                        for (Genres.Genre genre : genres) {
                            Log.d("TAG", " ID " + genre.getId() + " name " + genre.getName());
                        }
                    }
                });

        compositeDisposable.add(disposable);
    }

    private Completable setFavoritID(Movie movie) {
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

    private String getLanguage() {
        String language = null;

        int id = sharedPreferences.getInt("language", 0);
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
            default:
                language = "en";
                return language;
        }
    }

    private void loadMoreData(String searchText, int page) {

        cardView.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.VISIBLE);

        Flowable responseCall = MovieRetrofit.getRetrofit().getMovie(searchText, page, getLanguage())
                .doOnNext(movieResponce -> total = Integer.parseInt(movieResponce.getTotalResults()))
                .flatMap(movieResponce -> Flowable.just(movieResponce.getMovieList()))
                .filter(list -> !list.isEmpty())
                .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMapCompletable(this::addAllItemToAdapter).toFlowable()
                .onErrorReturn(throwable -> {
                    showError(throwable);
                    return true;
                });


        Disposable disposable = responseCall.subscribe();
        compositeDisposable.add(disposable);
    }

    private void showError(Throwable throwable) {
        HttpException httpException = (HttpException) throwable;
        Response response = httpException.response();
        ApiError errorMessage = ErrorUtil.parseError(response);
        showFailView(errorMessage.getMessage());
    }

    private Completable addAllItemToAdapter(List<Movie> list) {
        return Completable.fromAction(() -> {
            adapter.addAll(list);
            isLoading = false;
            setProgressBarGone();
        });
    }

    private void setProgressBarGone() {
        Completable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> bar.setVisibility(View.GONE));
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (ProfileActivity.changeBD) {
            bar.setVisibility(View.VISIBLE);
            ProfileActivity.changeBD = false;
            adapter.clear();
            adapter.deleteAllFavoritID();
            setProgressBarGone();
            subscribeUIMovie();

        }

        navigationView.setCheckedItem(R.id.nav_films);
        init();
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null)
            compositeDisposable.clear();
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
                    addToFirebase(movie.getId(), movie);
                } else {
                    addToBD(movie);
                    adapter.setFavoritID(movie.getId());
                }

            } else {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    adapter.deleteFavoritID(movie.getId());
                    deleteFromFirebase(movie.getId());
                } else {
                    deleteFromBD(movie);
                    adapter.deleteFavoritID(movie.getId());
                }
            }
        }

    }

    private void addToBD(Movie movie) {
        viewModel.insertItem(movie);
    }

    private void deleteFromBD(Movie movie) {
        viewModel.deleteItem(movie);
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

        Flowable responseCall = MovieRetrofit.getRetrofit().getDiscoverMovie(getLanguage(), Constant.SORT,
                "false", "false", page, vote, year, runTime)
                .doOnNext(movieResponce -> total = Integer.parseInt(movieResponce.getTotalResults()))
                .flatMap(movieResponce -> Flowable.just(movieResponce.getMovieList()))
                .filter(list -> !list.isEmpty())
                .delay(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .flatMapCompletable(this::addAllItemToAdapter).toFlowable()
                .onErrorReturn(throwable -> {
                    showError(throwable);
                    return true;
                });

        Disposable disposable = responseCall.subscribe();
        compositeDisposable.add(disposable);
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

    private void addToFirebase(String position, Movie movie) {

        movieID = movie.getId();
        movieMap.put(movieID, movie);

        firestoreDB.set(movieMap, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG", "DocumentSnapshot added with ID: ");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("TAG", "Error adding document", e);
            }
        });
    }

    private void deleteFromFirebase(String position) {

        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put(position, FieldValue.delete());

        firestoreDB.update(deleteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "DocumentSnapshot successfully deleted!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readFromFirebase() {
        firestoreDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Log.d("TAG", "DocumentSnapshot data: " + snapshot.getData());
                        Map<Object, Object> map = new HashMap<>();
                        map.putAll(snapshot.getData());
                        getMovieFromMap(map);

                    }
                }
            }
        });

    }

    private void getMovieFromMap(Map<Object, Object> map) {
        Completable.fromAction(() -> {
            for (Object obj : map.values()) {
                Gson gson = new Gson();
                JsonElement jsonElement = gson.toJsonTree(obj);
                Movie movie = gson.fromJson(jsonElement, Movie.class);
                Log.d("TAG", "ID " + movie.getId());
                adapter.setFavoritID(movie.getId());
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }
}
