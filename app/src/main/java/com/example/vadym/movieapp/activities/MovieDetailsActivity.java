package com.example.vadym.movieapp.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.api.ApiError;
import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.room.MovieListModel;
import com.example.vadym.movieapp.util.ErrorUtil;
import com.example.vadym.movieapp.util.UpdateListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity implements View.OnClickListener {

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
    private boolean isFirstStartActivity = false;
    private boolean isClick = false;

    private String titleMovie = null;
    private String image = null;
    private String overviewDet = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(MovieListModel.class);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            movieId = bundle.getString("detail");

        }
        if (movieId != null) {
            getMovieDetail(movieId);
        }

        subcribeOnUI();
        shoProgressBar();


        imageButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (setID.contains(movieId)) {
            isClick = true;
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher_white));

        } else {
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
        }

        if (isClick) {
            viewModel.deleteByID(movieId);
            UpdateListener.updateAdapter(movieId);
        }
    }

    private void subcribeOnUI() {

        viewModel.getItems().observe(MovieDetailsActivity.this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> list) {
                for (int i = 0; i < list.size(); i++) {
                    Movie movie = list.get(i);
                    setID.add(movie.getId());
                }

                if (!isFirstStartActivity) {
                    isFirstStartActivity = true;
                    if (setID.contains(movieId)) {
                        imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
                    } else {
                        imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher_white));
                    }
                }


            }
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
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void shoProgressBar() {
        detailBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                detailBar.setVisibility(View.INVISIBLE);
                cardViewDet.setVisibility(View.VISIBLE);
                cardViewDet2.setVisibility(View.VISIBLE);
            }
        }, 2000);
    }

    private void getValueFromMovieDatils(MovieDetails movieDetails) {

        image = movieDetails.getPoster();
        overviewDet = movieDetails.getOverview();
        titleMovie = movieDetails.getTitle();
    }

    private void getMovieDetail(String id) {

        Call<MovieDetails> detailsCall = MovieRetrofit.getRetrofit().getMovieDetails(id);
        detailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if (response.isSuccessful()) {
                    MovieDetails movieDetails = response.body();
                    getValueFromMovieDatils(movieDetails);

                    List<String> names = new ArrayList<>();

                    for (MovieDetails.MovieGenre genre : movieDetails.getGenres()) {
                        names.add(genre.getName());
                    }

                    String genres = TextUtils.join(", ", names);

                    category.setText(getResources().getString(R.string.categories, genres));

                    title.setText(movieDetails.getTitle());
                    releasedDate.setText(getResources().getString(R.string.release_date, movieDetails.getReleased()));

                    if ("0".equals(movieDetails.getBudget())) {
                        budget.setText(getResources().getString(R.string.budget, "n/a"));
                    } else
                        budget.setText(getResources().getString(R.string.budget2, movieDetails.getBudget(), "$"));

                    runTime.setText(getResources().getString(R.string.runtime, movieDetails.getRuntime(), "m"));
                    status.setText(getResources().getString(R.string.status, movieDetails.getStatus()));
                    overview.setText(getResources().getString(R.string.overview, movieDetails.getOverview()));

                    if (("").equals(movieDetails.getTagline())) {
                        tag.setText(getResources().getString(R.string.tag, "n/a"));
                    } else {
                        tag.setText(getResources().getString(R.string.tag, movieDetails.getTagline()));
                    }


                    Picasso.with(getApplicationContext())
                            .load(String.format("%s", Constant.TMDB_IMAGE + movieDetails.getPoster()))
                            .placeholder(android.R.drawable.ic_btn_speak_now)
                            .into(imageView);
                } else {
                    ApiError error = ErrorUtil.parseError(response);
                    showFailView(error.getMessage());
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                showFailView(t.getMessage());
            }
        });
    }

    private void showFailView(String message) {
        cardViewDet.setVisibility(View.INVISIBLE);
        errorViewCard.setVisibility(View.VISIBLE);
        TextView errorText = errorViewCard.findViewById(R.id.errorTitle);
        if (errorText != null)
            errorText.setText(message);
    }

    private void refreshDetailActivity() {
        errorViewCard.setVisibility(View.INVISIBLE);
        cardViewDet.setVisibility(View.VISIBLE);
        getMovieDetail(movieId);
    }


}
