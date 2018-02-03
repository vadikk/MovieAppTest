package com.example.vadym.movieapp.data;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.activities.OnMovieClickListener;
import com.example.vadym.movieapp.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieRecyclerAdapter extends RecyclerView.Adapter<MovieViewHolder> {


    private OnMovieClickListener listener;
    private List<Movie> movieList = new ArrayList<>();

    public MovieRecyclerAdapter() {
    }

    public void setOnMovieClickListener(OnMovieClickListener listener){
        this.listener = listener;
    }

    // TODO: 2/3/18 Допиши тут Nullable, тобі потім студія буде підсвічувати сама. що в тебе результат цієї функції може буть нул.
    public Movie getMovie(int position){
        if(position<0 || position>=getItemCount()){
            return null;
        }
        return movieList.get(position);
    }

    public void addAll(List<Movie> movies){
        int positionStart = getItemCount();
        notifyItemRangeInserted(positionStart,movies.size());
        movieList.addAll(movies);
    }

    public void clear(){
        notifyItemRangeRemoved(0,getItemCount());
        movieList.clear();
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_row,parent,false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);

        if(movie!=null){
            holder.setMovie(movie);

            holder.itemView.setOnClickListener((v)->
                onMovieClick(holder.getAdapterPosition()));
        }

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    private void onMovieClick(int position){
        // TODO: 2/3/18 Пошукай в студії в себе хоткеї для форматування коду, воно дозволяє його по стилю і відступах відформатувати.
        if(listener!=null){
            listener.onMovieClick(position);
        }
    }
}
