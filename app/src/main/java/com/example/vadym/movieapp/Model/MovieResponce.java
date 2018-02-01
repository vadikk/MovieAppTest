package com.example.vadym.movieapp.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Vadym on 29.01.2018.
 */
// TODO: 1/31/18 Підсвічено IDE.
public class MovieResponce {

    @SerializedName("page")
    @Expose
    private String page;
    @SerializedName("total_results")
    @Expose
    // TODO: 1/31/18 Підсвічено IDE.
    private String total_results;
    @SerializedName("total_pages")
    @Expose
    private String total_pages;
    @SerializedName("results")
    @Expose
    // TODO: 1/31/18 Хорошим тоном рахується не писати серіалізацію для тих полів, які мають однакову назву з полями респонса.
    private List<Movie> results;

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

    public void setMovieList(List<Movie> movieList) {
        this.results = movieList;
    }
}
