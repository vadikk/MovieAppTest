package com.example.vadym.movieapp.Data;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vadym.movieapp.Constans.Constant;
import com.example.vadym.movieapp.Model.Movie;
import com.example.vadym.movieapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    ImageView imageView;

    public MovieViewHolder(View itemView) {
        super(itemView);
        //Redundant casting.
        title = (TextView) itemView.findViewById(R.id.movieTitleID);
        imageView = (ImageView) itemView.findViewById(R.id.movieImageID);
    }

    public void setMovie(Movie movie){
        String moviePoster = movie.getImage();
        title.setText(movie.getTitle());

        Picasso.with(this.itemView.getContext())
                .load(Constant.TMDB_IMAGE + moviePoster)
                .placeholder(android.R.drawable.ic_btn_speak_now)
                .into(imageView);
    }
}
