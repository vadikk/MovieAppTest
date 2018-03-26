package com.example.vadym.movieapp.service;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;

/**
 * Created by Vadym on 26.02.2018.
 */

public class Genres {

    private List<Genre> genres = new ArrayList<>();

    public Genres() {
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    @Entity(tableName = "genre")
    public static class Genre{

        @PrimaryKey
        @NonNull
        private String id;
        private String name;

        public Genre() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
