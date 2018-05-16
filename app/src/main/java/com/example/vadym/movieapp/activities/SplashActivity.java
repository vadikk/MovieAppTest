package com.example.vadym.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.vadym.movieapp.BuildConfig;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Vadym on 25.02.2018.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showSplashByTime();
    }

    private void showSplashByTime() {
        Completable.timer(BuildConfig.LOAD_TIME, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                });

    }
}
