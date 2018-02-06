package com.example.vadym.movieapp.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.api.ApiError;
import com.example.vadym.movieapp.api.MovieRetrofit;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.model.MovieDetails;
import com.example.vadym.movieapp.util.ErrorUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
            if(movie!=null){
                movieId = movie.getId();
            }

        }
        // TODO: 2/6/18 Що буде, якщо movieId тут буде нул? Тре захендлти чи перевірити явно.
        getMovieDetail(movieId);
    }

    private void getMovieDetail(String id){

        Call<MovieDetails> detailsCall = MovieRetrofit.getRetrofit().getMovieDetails(id);
        detailsCall.enqueue(new Callback<MovieDetails>() {
            @Override
            public void onResponse(Call<MovieDetails> call, Response<MovieDetails> response) {
                if(response.isSuccessful()){
                    MovieDetails movieDetails = response.body();

//                    List<MovieDetails.MovieGenre> genres = movieDetails.getGenres();
                    List<String> names = new ArrayList<>();
//                    for (int i = 0; i < genres.size(); i++) {
//                        names.add(genres.get(i).getName());
//                    }
                    // TODO: 2/6/18 Ще тако можна зробить. 
                    for (MovieDetails.MovieGenre genre : movieDetails.getGenres()) {
                        names.add(genre.getName());
                    }
                    
                    
                    
                    // TODO: 2/6/18 От і заробив join. Але поміняй назву змінної. 
                    String genre_name = TextUtils.join(", ",names);

                    category.setText(getResources().getString(R.string.categories, genre_name));

                    title.setText(movieDetails.getTitle());
                    releasedDate.setText(getResources().getString(R.string.release_date, movieDetails.getReleased()));
                    // TODO: 2/6/18 Як варіант, можна рівняти тут через  "0".equals(movieDetails.getBudget())
                    // TODO: 2/6/18 Зачасту так роблять.
                    if (movieDetails.getBudget().equals(String.valueOf(0))) {
                        budget.setText(getResources().getString(R.string.budget, "n/a"));
                    }else
                        budget.setText(getResources().getString(R.string.budget2, movieDetails.getBudget(), "$"));

                    runTime.setText(getResources().getString(R.string.runtime,movieDetails.getRuntime(),"m"));
                    status.setText(getResources().getString(R.string.status, movieDetails.getStatus()));
                    overview.setText(getResources().getString(R.string.overview, movieDetails.getOverview()));

                    if (("").equals(movieDetails.getTagline())) {
                        tag.setText(getResources().getString(R.string.tag, "n/a"));
                    }else {
                        tag.setText(getResources().getString(R.string.tag, movieDetails.getTagline()));
                    }


                    Picasso.with(getApplicationContext())
                            .load(String.format("%s",Constant.TMDB_IMAGE + movieDetails.getPoster()))
                            .placeholder(android.R.drawable.ic_btn_speak_now)
                            .into(imageView);
                } else {
                    // TODO: 2/6/18 Хтось тут щось забув зробити.
                    ApiError error = ErrorUtil.parseError(response);
                    showFailView();
                }
            }

            @Override
            public void onFailure(Call<MovieDetails> call, Throwable t) {
                Log.d("TAG", t.getMessage());
                showFailView();
            }
        });
    }

    private void showFailView() {
        // TODO: 2/6/18 Давай тут ти зробиш вьюху як на першому екрані і можливість перезавантажити  дані. 
        // TODO: 2/6/18 Нехай в тулбарі в меню буде рефреша.
        View view = getLayoutInflater().inflate(R.layout.error_view, null);
        setContentView(view);
        view.setVisibility(View.VISIBLE);
    }
}
