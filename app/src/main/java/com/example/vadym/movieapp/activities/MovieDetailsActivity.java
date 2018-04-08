package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.room.MovieListModel;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity
        implements View.OnClickListener {

    public static final String MOVIEDETAILS = "detail";

    @BindView(R.id.movieImageDetail)
    ImageView imageView;
    @BindView(R.id.movieTitleDetail)
    TextView title;
    @BindView(R.id.movieReleaseDetail)
    TextView releasedDate;
    @BindView(R.id.movieBudgetDetail)
    TextView budget;
    @BindView(R.id.movieRuntimeDetail)
    TextView runTime;
    @BindView(R.id.movieStatusDetail)
    TextView status;
    @BindView(R.id.overviewDet)
    TextView overview;
    @BindView(R.id.tagDet)
    TextView tag;
    @BindView(R.id.movieGenreDetail)
    TextView category;
    @BindView(R.id.cardErrorView)
    CardView errorViewCard;
    @BindView(R.id.cardViewDet)
    CardView cardViewDet;
    @BindView(R.id.cardViewDet2)
    CardView cardViewDet2;
    @BindView(R.id.progressBarDetail)
    ProgressBar detailBar;
    @BindView(R.id.imageButtonDetail)
    ImageButton imageButton;

    @Nullable
    private String movieId = null;
    private MovieListModel viewModel;
    private Set<String> setID = new HashSet<>();
    private boolean isClick = false;

    private String titleMovie = null;
    private String image = null;
    private String overviewDet = null;
    private SharedPreferences sharedPreferences;
    private CompositeDisposable compositeDisposable;
    private Movie movie = null;
    private List<String> names = new ArrayList<>();
    private DocumentReference firestoreDB = FirebaseFirestore.getInstance().collection("movie").document("movieData");
    private Map<String, Movie> movieMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        setTitle(getString(R.string.detail_info));
        ButterKnife.bind(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        compositeDisposable = new CompositeDisposable();

        viewModel = ViewModelProviders.of(this).get(MovieListModel.class);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            movie = (Movie) bundle.getSerializable(MOVIEDETAILS);

            if (movie != null)
                movieId = movie.getId();

        }

        subcribeOnUI();
        shoProgressBar();
        if (movieId != null) {
            getMovieDetail(movieId);
        }

        imageButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        isClick = !isClick;

        if (isClick) {
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
            movie.setFavorite(true);
            interactionWithAdapter(movie);
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                addToFirebase(movie.getId(), movie);
            else
                viewModel.insertItem(movie);

        } else {
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher_white));
            movie.setFavorite(false);
            interactionWithAdapter(movie);
            if (FirebaseAuth.getInstance().getCurrentUser() != null)
                deleteFromFirebase(movie.getId());
            else
                viewModel.deleteByID(movieId);
        }
    }

    private void subcribeOnUI() {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            readFromFirebase();
        } else {
            compositeDisposable.add(viewModel.getItems()
                    .subscribeOn(Schedulers.io())
                    .flatMap(Flowable::fromIterable)
                    .flatMapCompletable(this::addMovieIDToSet)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    private void deleteFromFirebase(String id) {

        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put(id, FieldValue.delete());

        firestoreDB.update(deleteMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MovieDetailsActivity.this, "Successfully deleted!",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addToFirebase(String id, Movie movie) {

        movieMap.put(id, movie);

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

    private void init() {
        if (setID.contains(movieId)) {
            isClick = true;
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
        } else {
            isClick = false;
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher_white));
        }
    }

    private Completable addMovieIDToSet(Movie movie) {
        return Completable.fromAction(() -> {
            setID.add(movie.getId());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_detail_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {
            case R.id.refreshDetail:
                refreshDetailActivity();
                return true;
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void interactionWithAdapter(Movie movie1) {
        Intent intent = new Intent();
        intent.putExtra(MOVIEDETAILS, movie1);
        intent.putExtra("Bool", isClick);
        setResult(1, intent);
    }

    private void readFromFirebase() {
        firestoreDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Map<Object, Object> map = new HashMap<>();
                        map.putAll(snapshot.getData());

                        for (Object obj : map.values()) {
                            Gson gson = new Gson();
                            JsonElement jsonElement = gson.toJsonTree(obj);
                            Movie movieGson = gson.fromJson(jsonElement, Movie.class);
                            setID.add(movieGson.getId());
                        }
                        init();
                    }
                }
            }
        });

    }

    private void shoProgressBar() {
        Completable.timer(2, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    detailBar.setVisibility(View.GONE);
                    cardViewDet.setVisibility(View.VISIBLE);
                    cardViewDet2.setVisibility(View.VISIBLE);
                });
    }

    private void getValueFromMovieDatils(MovieDetails movieDetails) {

        image = movieDetails.getPoster();
        overviewDet = movieDetails.getOverview();
        titleMovie = movieDetails.getTitle();
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

    private void getMovieDetail(String id) {

        Single<MovieDetails> detailsObservable = MovieRetrofit.getRetrofit().getMovieDetails(id, getLanguage())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(movieDetails -> {

                    getValueFromMovieDatils(movieDetails);
                    fillTextView(movieDetails);
                })
                .onErrorReturn(throwable -> {
                    showError(throwable);
                    return new MovieDetails();
                });

        Disposable disposable = detailsObservable.subscribe();

        compositeDisposable.add(disposable);
    }

    private void fillTextView(MovieDetails responce) {
        for (MovieDetails.MovieGenre genre : responce.getGenres()) {
            names.add(genre.getName());
        }

        String genres = TextUtils.join(", ", names);

        category.setText(getResources().getString(R.string.categories, genres));

        title.setText(responce.getTitle());
        releasedDate.setText(getResources().getString(R.string.release_date, responce.getReleased()));

        if ("0".equals(responce.getBudget())) {
            budget.setText(getResources().getString(R.string.budget, "n/a"));
        } else
            budget.setText(getResources().getString(R.string.budget2, responce.getBudget(), "$"));

        runTime.setText(getResources().getString(R.string.runtime, responce.getRuntime(), "m"));
        status.setText(getResources().getString(R.string.status, responce.getStatus()));
        overview.setText(getResources().getString(R.string.overview, responce.getOverview()));

        if (("").equals(responce.getTagline())) {
            tag.setText(getResources().getString(R.string.tag, "n/a"));
        } else {
            tag.setText(getResources().getString(R.string.tag, responce.getTagline()));
        }


        Picasso.with(getApplicationContext())
                .load(String.format("%s", Constant.TMDB_IMAGE + responce.getPoster()))
                .placeholder(android.R.drawable.ic_btn_speak_now)
                .into(imageView);
    }

    @Override
    protected void onDestroy() {
        if (compositeDisposable != null)
            compositeDisposable.clear();

        super.onDestroy();
    }

    private void showFailView(String message) {

        cardViewDet.setVisibility(View.INVISIBLE);
        cardViewDet2.setVisibility(View.INVISIBLE);
        errorViewCard.setVisibility(View.VISIBLE);

        TextView errorText = errorViewCard.findViewById(R.id.errorTitle);
        if (errorText != null)
            errorText.setText(message);
    }

    private void refreshDetailActivity() {
        errorViewCard.setVisibility(View.INVISIBLE);
        cardViewDet.setVisibility(View.VISIBLE);
        cardViewDet2.setVisibility(View.VISIBLE);
        getMovieDetail(movieId);
    }

    private void showError(Throwable throwable) {
        HttpException httpException = (HttpException) throwable;
        Response response = httpException.response();
        ApiError errorMessage = ErrorUtil.parseError(response);
        showFailView(errorMessage.getMessage());
    }
}
