package com.example.vadym.movieapp.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.constans.Constant;
import com.example.vadym.movieapp.preference.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {


    // TODO: 3/6/18 В тебе тут нема тулбара і можливості повернутися наад через програму.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.settings_content, new SettingsFragment()).commit();

    }
}
