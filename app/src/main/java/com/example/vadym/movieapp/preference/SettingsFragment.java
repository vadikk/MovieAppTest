package com.example.vadym.movieapp.preference;

import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.constans.Constant;

/**
 * Created by Vadym on 18.02.2018.
 */

public class SettingsFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preference);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        PreferenceManager.setDefaultValues(getActivity(),R.xml.preference,true);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Preference preference = findPreference(s);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) findPreference(s);
            // TODO: 3/6/18 Краще мати якусь дефолтну мову.
                int index = listPreference.findIndexOfValue(sharedPreferences.getString(s,""));
                editor = preferences.edit();
                editor.putInt("language",index).apply();

                String text = String.valueOf(listPreference.getEntries()[index]);
                Log.d("TAG", "item " + index);
        }
    }
}
