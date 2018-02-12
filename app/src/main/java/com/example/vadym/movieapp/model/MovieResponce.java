package com.example.vadym.movieapp.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vadym on 29.01.2018.
 */

public class MovieResponce {

    private String page;
    @SerializedName("total_results")
    private String totalResults;
    @SerializedName("total_pages")
    private String totalPages;
    @Nullable
    private List<Movie> results = new ArrayList<>();

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(String totalResults) {
        this.totalResults = totalResults;
    }

    public String getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(String totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getMovieList() {
        return results;
    }

    public void setMovieList(@Nullable List<Movie> movieList) {
        // TODO: 2/12/18 Тут тре щось зробити з цим. Якщо в тебе тут 10 елементів, а прийшло на вхід нул, то ми маємо ті 10 залишити?
        if (movieList != null) {
            this.results = movieList;
        }
    }
}
