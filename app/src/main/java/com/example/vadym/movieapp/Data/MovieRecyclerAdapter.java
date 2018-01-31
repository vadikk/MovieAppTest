package com.example.vadym.movieapp.Data;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.vadym.movieapp.Activities.OnMovieClickListener;
import com.example.vadym.movieapp.Model.Movie;
import com.example.vadym.movieapp.R;

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

        if(movie==null)
            return;

        holder.setMovie(movie);

        holder.itemView.setOnClickListener((v)->{
            onMovieClick(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    private void onMovieClick(int position){

        if(listener!=null){
            listener.onMovieClick(position);
        }
    }
}
