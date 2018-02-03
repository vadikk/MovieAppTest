package com.example.vadym.movieapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.StringJoiner;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    @Nullable
    private Movie movie = null;
    @Nullable
    private String movieId = null;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null){
            movie = (Movie) bundle.getSerializable("detail");
            // TODO: 1/31/18 ???????
            // TODO: 2/3/18 Якщо ти зайшов би в функцію getSerializable(), то побачив би, що вона вертає Nullable.
            // TODO: 2/3/18 Це значить, що в тебе може не бути того об'єкту, який ти там шукаєш.
            // TODO: 2/3/18 Наприклад, коли ти помилився ключем, тому воно і було потенційно нул.
            // TODO: 2/3/18 Якщо б ти помилився ключем, або передало нул із попередньої актівіті, то був би NPE.
            movieId = movie.getId();

        }

        // TODO: 2/3/18 Дивись, яка тут ситуація. Якщо в тебе не зайшло в попередній блок, то в тебе movieId буде нулове. 
        // TODO: 2/3/18 Тут палка з двох кінців, я не знаю, як поведе себе АРІ, коли не прийде потрібний параметр, можливо, варто показувати помилку, якщо детектимо, що тут нул.

        getMovieDetail(movieId);
    }

    private void getMovieDetail(String id){

        Call<MovieDetails> detailsCall = MovieRetrofit.getRetrofit().getMovieDetails(id, Constant.API_KEY);
        detailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if(response.isSuccessful()){
                    MovieDetails movieDetails = response.body();

                    List<MovieDetails.MovieGenre> genres = movieDetails.getGenres();

                    StringJoiner joiner = new StringJoiner(", ");
                    // TODO: 2/3/18 Глянь на конструкцію foreach, яка студією надається, не буде мозолити очі з індексами.
                    for (int i = 0; i < genres.size(); i++) {
                        joiner.add(genres.get(i).getName());
                    }

                    String genre_name = joiner.toString();

                    // TODO: 2/3/18 https://developer.android.com/guide/topics/resources/string-resource.html#FormattingAndStyling
                    category.setText(String.valueOf(getText(R.string.categories) + " " + genre_name));

                    title.setText(movieDetails.getTitle());
                    releasedDate.setText(String.valueOf(getText(R.string.release_date) + " " + movieDetails.getReleased()));

                    if (movieDetails.getBudget().equals(String.valueOf(0))) {
                        budget.setText(String.valueOf(getText(R.string.budget) + " " + "n/a"));
                    }else
                        budget.setText(String.valueOf(getText(R.string.budget) + " " + movieDetails.getBudget() + "$"));

                    runTime.setText(String.valueOf(getText(R.string.runtime) + " " + movieDetails.getRuntime() + "m"));
                    status.setText(String.valueOf(getText(R.string.status) + " " + movieDetails.getStatus()));
                    overview.setText(String.valueOf(getText(R.string.overview) + " " + movieDetails.getOverview()));

                    if (("").equals(movieDetails.getTagline())) {
                        tag.setText(String.valueOf(getText(R.string.tag) + " " + "n/a"));
                    }else {
                        tag.setText(String.valueOf(getText(R.string.tag) + " " + movieDetails.getTagline()));
                    }


                    Picasso.with(getApplicationContext())
                            .load(Constant.TMDB_IMAGE + movieDetails.getPoster())
                            .placeholder(android.R.drawable.ic_btn_speak_now)
                            .into(imageView);
                } else {
                    showFailView();
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                showFailView();
            }
        });
    }

    private void showFailView() {
        View view = getLayoutInflater().inflate(R.layout.error_view, null);
        setContentView(view);
        view.setVisibility(View.VISIBLE);
    }
}
