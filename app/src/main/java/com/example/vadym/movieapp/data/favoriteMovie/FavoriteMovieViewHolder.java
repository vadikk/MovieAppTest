package com.example.vadym.movieapp.data.favoriteMovie;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vadym on 06.02.2018.
 */

public class FavoriteMovieViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.movieImageFavorite)
    ImageView imageView;
    @BindView(R.id.movieTitleFavorite)
    TextView title;
    @BindView(R.id.overviewFavorite)
    TextView overview;

    public FavoriteMovieViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void setImage(Movie movie) {
        String poster = movie.getImage();

        Picasso.with(this.itemView.getContext())
                .load(itemView.getResources().getString(R.string.load_picture, Constant.TMDB_IMAGE, poster))
                .placeholder(android.R.drawable.ic_btn_speak_now)
                .into(imageView);
    }

    public void setText(Movie movie) {
        title.setText(movie.getTitle());
        overview.setText(itemView.getResources().getString(R.string.overview, movie.getOverview()));

    }
}
