package com.example.vadym.movieapp.dagger;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vadym on 04.04.2018.
 */
@Module
public class RecyclerModule {

    public RecyclerModule() {
    }

    @Named("movie")
    @Provides
    LinearLayoutManager getManager(Application application) {
        Context context = (Context) application;
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }
}
