package com.example.vadym.movieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Vadym on 25.02.2018.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: 3/6/18 Давай хай сплеш буде 3 секунди. боякось швидко він пролетів.
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
