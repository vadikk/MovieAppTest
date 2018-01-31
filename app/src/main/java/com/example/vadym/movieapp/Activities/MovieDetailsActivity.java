package com.example.vadym.movieapp.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vadym.movieapp.Api.MovieRetrofit;
import com.example.vadym.movieapp.Constans.Constant;
import com.example.vadym.movieapp.Model.Movie;
import com.example.vadym.movieapp.Model.MovieDetails;
import com.example.vadym.movieapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {

    private Movie movie;
    private String movieId;

    private ImageView imageView;
    private TextView title;
    private TextView releasedDate;
    private TextView budget;
    private TextView runTime;
    private TextView status;
    private TextView overview;
    private TextView tag;
    private TextView category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        setUI();

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            movie = (Movie) bundle.getSerializable("detail");
            movieId = movie.getId();
        }

        getMovieDetail(movieId);
    }

    private void setUI(){

        imageView = (ImageView) findViewById(R.id.movieImageDetail);
        title = (TextView) findViewById(R.id.movieTitleDetail);
        releasedDate = (TextView) findViewById(R.id.movieReleaseDetail);
        budget = (TextView) findViewById(R.id.movieBudgetDetail);
        runTime = (TextView) findViewById(R.id.movieRuntimeDetail);
        status = (TextView) findViewById(R.id.movieStatusDetail);
        overview = (TextView) findViewById(R.id.overviewDet);
        tag = (TextView) findViewById(R.id.tagDet);
        category = (TextView) findViewById(R.id.movieGenreDetail);
    }

    private void getMovieDetail(String id){

        Call<MovieDetails> detailsCall = MovieRetrofit.getRetrofit().getMovieDetails(id);
        detailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if(response.isSuccessful()){
                    MovieDetails movieDetails = response.body();

                    List<MovieDetails.MovieGenre> genres = movieDetails.getGenres();
                    StringBuilder categories = new StringBuilder();

                    for (int i=0;i<genres.size();i++){
                        categories.append(genres.get(i).getName() + ", ");
                        if(i==genres.size()-1){
                            categories.append(genres.get(i).getName());
                        }

                    }
                    category.setText(String.valueOf("Categories: " + categories));

                    title.setText(movieDetails.getTitle());
                    releasedDate.setText(String.valueOf("Released: " + movieDetails.getReleased()));

                    if(Integer.parseInt(movieDetails.getBudget())==0){
                        budget.setText(String.valueOf("Budget: " + "n/a"));
                    }else
                        budget.setText(String.valueOf("Budget: " + movieDetails.getBudget() + "$"));

                    runTime.setText(String.valueOf("Runtime: " + movieDetails.getRuntime() + "m"));
                    status.setText(String.valueOf("Status: " + movieDetails.getStatus()));
                    overview.setText(String.valueOf("Overview: " + movieDetails.getOverview()));
                    if(movieDetails.getTagline().equals("")){
                        tag.setText(String.valueOf("Tag: " + "n/a"));
                    }else {
                        tag.setText(String.valueOf("Tag: " + movieDetails.getTagline()));
                    }


                    Picasso.with(getApplicationContext())
                            .load(Constant.TMDB_IMAGE + movieDetails.getPoster())
                            .placeholder(android.R.drawable.ic_btn_speak_now)
                            .into(imageView);
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {

            }
        });
    }
}
