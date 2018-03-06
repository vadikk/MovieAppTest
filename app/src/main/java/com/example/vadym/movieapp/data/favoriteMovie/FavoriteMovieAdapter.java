package com.example.vadym.movieapp.data.favoriteMovie;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.activities.OnMovieClickListener;
import com.example.vadym.movieapp.model.Movie;

import java.util.List;


/**
 * Created by Vadym on 06.02.2018.
 */

public class FavoriteMovieAdapter extends RecyclerView.Adapter<FavoriteMovieViewHolder> {

    private List<Movie> favoriteList;
    private OnMovieClickListener listener;

    // TODO: 3/6/18 Не анйкращий варіант - краще створити адаптер пустим, засетити його, а потім додавати/видаляти з нього.
    
    public FavoriteMovieAdapter(List<Movie> list) {
        favoriteList = list;
    }

    public void setOnMovieClickListener(OnMovieClickListener listener) {
        this.listener = listener;
    }

    public void deleteFromList(int position) {
        if (position < 0 || position >= getItemCount()) {
            return;
        }
        notifyItemRemoved(position);
        favoriteList.remove(position);
    }

    // TODO: 3/6/18 Намагайся такі куск робити nullable, потім студя тобі буде підсвічувати місця, де може буть щось нулабл. 
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
        // TODO: 3/6/18 Краще юзай holder.getAdapterPosition()
        Movie movie = favoriteList.get(position);

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
