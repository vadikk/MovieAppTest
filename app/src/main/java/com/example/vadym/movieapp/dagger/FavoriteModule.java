package com.example.vadym.movieapp.dagger;

import android.app.Application;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vadym on 05.04.2018.
 */
@Module
public class FavoriteModule {

    public FavoriteModule() {
    }

    @Named("favorite")
    @Provides
    LinearLayoutManager getLinearManager(Application application) {
        Context context = application;
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        return manager;
    }
}
