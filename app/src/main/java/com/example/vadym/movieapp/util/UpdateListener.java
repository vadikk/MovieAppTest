package com.example.vadym.movieapp.util;

import com.example.vadym.movieapp.activities.OnUpdateRecyclerAdapterListener;

/**
 * Created by Vadym on 15.02.2018.
 */

public class UpdateListener {

    private static OnUpdateRecyclerAdapterListener listener;

    public static void setOnUpdateRecyclerListener(OnUpdateRecyclerAdapterListener newListener) {
        listener = newListener;
    }

    public static void updateAdapter(String id) {
        if (listener != null) {
            listener.updateRecyclerAdapter(id);
        }
    }
}
