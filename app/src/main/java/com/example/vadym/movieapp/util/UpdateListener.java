package com.example.vadym.movieapp.util;

import com.example.vadym.movieapp.activities.OnUpdateRecyclerAdapterListener;

/**
 * Created by Vadym on 15.02.2018.
 */

// TODO: 3/6/18 Така конструкція не дуже правильна. Ти маєш сетити цей  лістенер в якийсь об'єкт і той об'єкт має викликати updateAdapter.
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
