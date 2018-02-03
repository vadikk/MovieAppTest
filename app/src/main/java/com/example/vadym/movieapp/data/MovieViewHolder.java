package com.example.vadym.movieapp.data;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.R;
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

    public MovieViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    public void setMovie(Movie movie){
        String moviePoster = movie.getImage();
        title.setText(movie.getTitle());

        Picasso.with(this.itemView.getContext())
                // TODO: 2/3/18 Тут краще форматування String.format заюзати.
                .load(Constant.TMDB_IMAGE + moviePoster)
                .placeholder(android.R.drawable.ic_btn_speak_now)
                .into(imageView);
    }
}
