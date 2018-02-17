package com.example.vadym.movieapp.data.listMovie;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.activities.OnStarClickListener;
import com.example.vadym.movieapp.model.Movie;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private OnStarClickListener starListener;
    private List<Movie> movieList = new ArrayList<>();
    private Set<String> favoritID = new HashSet<>();

    public MovieRecyclerAdapter(Set<String> strings) {

        favoritID = strings;
    }

    public void setOnClickListener(OnStarClickListener listener) {
        this.starListener = listener;
    }

    public void setFavoritID(String favoritID) {
        this.favoritID.add(favoritID);
    }

    public void deleteFavoritID(String favoritID) {
        this.favoritID.remove(favoritID);
        notifyDataSetChanged();
    }

    public boolean ifExist(String id) {
        if (favoritID.contains(id))
            return true;

        return false;
    }

    public int getSize() {
        return favoritID.size();
    }

    @Nullable
    public Movie getMovie(int position) {
        if (position < 0 || position >= getItemCount()) {
            return null;
        }
        return movieList.get(position);
    }

    public void addAll(List<Movie> movies) {
        int positionStart = getItemCount();
        notifyItemRangeInserted(positionStart, movies.size());
        movieList.addAll(movies);
    }

    public void clear() {
        notifyItemRangeRemoved(0, getItemCount());
        movieList.clear();
    }

    private void chechListMovie(Movie movie) {

        if (favoritID.contains(movie.getId())) {
            movie.setFavorite(true);
        } else {
            movie.setFavorite(false);
        }
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        chechListMovie(movie);

        if (movie != null) {
            holder.setMovie(movie);

            holder.star.setOnClickListener((view) -> onStarClick(holder.getAdapterPosition()));
        }

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    private void onStarClick(int position) {
        if (starListener != null) {
            starListener.onClickStar(position);
        }
    }

}
