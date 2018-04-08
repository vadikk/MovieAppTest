package com.example.vadym.movieapp.data.favoriteMovie;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.activities.OnMovieClickListener;
import com.example.vadym.movieapp.model.Movie;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Vadym on 06.02.2018.
 */

public class FavoriteMovieAdapter extends RecyclerView.Adapter<FavoriteMovieViewHolder> {

    private List<Movie> favoriteList = new ArrayList<>();
    private OnMovieClickListener listener;

    @Inject
    public FavoriteMovieAdapter() {
    }

    public void addFavoriteMovieAdapter(List<Movie> list) {
        int startPos = getItemCount();
        notifyItemRangeInserted(startPos, list.size());
        favoriteList.addAll(list);
    }

    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.listener = listener;
    }

    public void updateFavoriteList(Movie movie) {
        for (int i = 0; i < getItemCount(); i++) {
            Movie movieFav = favoriteList.get(i);
            if (movie.getId().equals(movieFav.getId())) {
                favoriteList.remove(movieFav);
                notifyItemRemoved(i);
                return;
            }
        }
    }

    public void deleteFromList(int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }
        notifyItemRemoved(position);
        favoriteList.remove(position);
    }

    @Nullable
    public Movie getMovie(int position) {
        if (position < 0 || position >= getItemCount()) {
            return null;
        }
        return favoriteList.get(position);
    }

    @Override
    public FavoriteMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_movie_view, parent, false);
        return new FavoriteMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FavoriteMovieViewHolder holder, int position) {

        Movie movie = favoriteList.get(holder.getAdapterPosition());

        if (movie != null) {
            holder.setImage(movie);
            holder.setText(movie);

            holder.itemView.setOnClickListener((view -> onMovieClick(holder.getAdapterPosition())));
        }

    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    private void onMovieClick(int position) {
        if (listener != null) {
            listener.onMovieClick(position);
        }
    }
}
