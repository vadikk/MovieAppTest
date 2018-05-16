package com.example.vadym.movieapp.activities;

import android.content.Intent;
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
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.dagger.MovieAppAplication;
import com.example.vadym.movieapp.dagger.detail.DaggerDetailActivityComponent;
import com.example.vadym.movieapp.dagger.detail.MvpDetailModule;
import com.example.vadym.movieapp.dagger.ActivityMovieModule;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.mvp.detail.DetailContract;
import com.example.vadym.movieapp.util.ErrorUtil;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity
        implements View.OnClickListener, DetailContract.DetailView {

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

    @Inject
    DetailContract.DetailPresenter presenter;

    @Nullable
    private String movieId = null;
    private boolean isClick = false;
    private Movie movie = null;
    private List<String> names = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        setTitle(getString(R.string.detail_info));
        ButterKnife.bind(this);

        DaggerDetailActivityComponent.builder()
                .movieComponent(MovieAppAplication.get(this).getMovieComponent())
                .activityMovieModule(new ActivityMovieModule(this))
                .mvpDetailModule(new MvpDetailModule(this))
                .build()
                .inject(this);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            movie = (Movie) bundle.getSerializable(MOVIEDETAILS);

            if (movie != null)
                movieId = movie.getId();

        }

        presenter.subcribeOnUI();
        shoProgressBar();
        if (movieId != null) {
            presenter.getMovieDetail(movieId);
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
            presenter.addToBD(movie.getId(), movie);

        } else {
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher_white));
            movie.setFavorite(false);
            interactionWithAdapter(movie);
            presenter.deleteFromBD(movie);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void init() {
        if (presenter.getID().contains(movieId)) {
            isClick = true;
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
        } else {
            isClick = false;
            imageButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher_white));
        }
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

    @Override
    public void shoProgressBar() {
        Completable.timer(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    detailBar.setVisibility(View.GONE);
                    cardViewDet.setVisibility(View.VISIBLE);
                    cardViewDet2.setVisibility(View.VISIBLE);
                });
    }

    @Override
    public void fillTextView(MovieDetails responce) {
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
        super.onDestroy();
        presenter.onDestroy();
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
        presenter.getMovieDetail(movieId);
    }

    @Override
    public void showError(Throwable throwable) {
        HttpException httpException = (HttpException) throwable;
        Response response = httpException.response();
        ApiError errorMessage = ErrorUtil.parseError(response);
        showFailView(errorMessage.getMessage());
    }
}
