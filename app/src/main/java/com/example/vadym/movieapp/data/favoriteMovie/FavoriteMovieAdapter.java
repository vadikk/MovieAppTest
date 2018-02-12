package com.example.vadym.movieapp.data.favoriteMovie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.model.Movie;

import java.util.List;


/**
 * Created by Vadym on 06.02.2018.
 */

public class FavoriteMovieAdapter extends RecyclerView.Adapter<FavoriteMovieViewHolder> {

    private List<Movie> favoriteList;

    public FavoriteMovieAdapter(List<Movie> list) {
        favoriteList = list;
    }

    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_movie_view, parent, false);
        return new FavoriteMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteMovieViewHolder holder, int position) {
        Movie movie = favoriteList.get(position);

        if (movie != null) {
            holder.setImage(movie);
            holder.setText(movie);
        }

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }
}
