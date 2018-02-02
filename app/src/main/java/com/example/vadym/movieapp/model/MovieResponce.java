package com.example.vadym.movieapp.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieResponce {

    private String page;
    private String total_results;
    private String total_pages;
    @Nullable
    private List<Movie> results = new ArrayList<>();

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTotal_results() {
        return total_results;
    }

    public void setTotal_results(String total_results) {
        this.total_results = total_results;
    }

    public String getTotal_pages() {
        return total_pages;
    }

    public void setTotal_pages(String total_pages) {
        this.total_pages = total_pages;
    }

    public List<Movie> getMovieList() {
        return results;
    }

    public void setMovieList(@Nullable List<Movie> movieList) {
        this.results = movieList;
    }
}
