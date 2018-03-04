package com.example.vadym.movieapp.room;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.service.Genres;

import java.util.List;

import io.reactivex.Flowable;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Vadym on 10.02.2018.
 */

@Dao
public interface MovieDao {

    @Insert(onConflict = REPLACE)
    void insert(Movie movie);

    @Delete
    void delete(Movie movie);

    @Query("DELETE FROM movie")
    void deleteAll();

    @Query("DELETE FROM movie WHERE id =:id")
    void deleteByID(String id);

    @Update
    void update(Movie movie);

    @Query("SELECT * FROM movie")
    Flowable<List<Movie>> getAll();

    @Query("SELECT * FROM movie WHERE id =:ids")
    Movie getById(int ids);

    @Insert(onConflict = REPLACE)
    void insertGenres(Genres.Genre genre);

    @Query("SELECT * FROM genre")
    Flowable<List<Genres.Genre>> getAllGenre();


}
