package com.example.vadym.movieapp.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.example.vadym.movieapp.model.Movie;
import com.example.vadym.movieapp.service.Genres;

/**
 * Created by Vadym on 10.02.2018.
 */

@Database(entities = {Movie.class, Genres.Genre.class}, version = 2)
public abstract class MovieDB extends RoomDatabase {

    private static final String DB_NAME = "movieDB.db";
    private static MovieDB instance;

    public static MovieDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context, MovieDB.class, DB_NAME)
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public static void destroyInstance() {
        instance = null;
    }

    public abstract MovieDao movieDao();


}
