package com.example.vadym.movieapp.data.listMovie;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.model.Movie;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieViewHolder extends RecyclerView.ViewHolder {


    @BindView(R.id.movieTitleID)
    TextView title;

    @BindView(R.id.movieImageID)
    ImageView imageView;

    @BindView(R.id.movieStar)
    ImageButton star;


    public MovieViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

    }

    public void setMovie(Movie movie) {
        String moviePoster = movie.getImage();
        title.setText(movie.getTitle());

        // TODO: 2/12/18 Назва фільму наїжджає на зірочку.

        if (movie.isFavorite()) {
            star.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.ic_launcher));
        } else {
            star.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.ic_launcher_white));
        }

        Picasso.with(this.itemView.getContext())
                .load(itemView.getResources().getString(R.string.load_picture, Constant.TMDB_IMAGE, moviePoster))
                .placeholder(android.R.drawable.ic_btn_speak_now)
                .into(imageView);
    }
}
