package com.example.vadym.movieapp.util;

import com.example.vadym.movieapp.activities.OnUpdateRecyclerAdapterListener;
import com.example.vadym.movieapp.data.listMovie.MovieRecyclerAdapter;
import com.example.vadym.movieapp.model.Movie;

/**
 * Created by Vadym on 15.02.2018.
 */

public class UpdateListener {

    private static OnUpdateRecyclerAdapterListener listener;

    public static void setOnUpdateRecyclerListener(OnUpdateRecyclerAdapterListener newListener) {
        listener = newListener;
    }

    public static void deleteID(Movie id) {
        if (listener != null) {
            listener.deleteIDFromAdapter(id);
        }

    }

}
