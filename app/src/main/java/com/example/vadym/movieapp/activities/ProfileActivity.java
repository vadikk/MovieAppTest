package com.example.vadym.movieapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.constans.Constant;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    public static String CHANGE_BD = "bd";
    public static boolean changeBD = false;
    private static int RC_SIGN_IN = 123;
    @BindView(R.id.profileSignOut)
    CardView profileSignOut;
    @BindView(R.id.btnSignOut)
    Button signOut;
    @BindView(R.id.hello)
    TextView titleText;
    @BindView(R.id.emailInfo)
    TextView emailInfo;
    @BindView(R.id.signInBtn)
    Button signIn;
    private SharedPreferences preferences;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle(getString(R.string.profile));

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        preferences = getSharedPreferences(Constant.APP_PREFS, Context.MODE_PRIVATE);
        signOut.setOnClickListener(this);
        signIn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            updateUI(mAuth.getCurrentUser());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                profileSignOut.setVisibility(View.VISIBLE);
                FirebaseUser user = mAuth.getCurrentUser();
                changeBD = true;
                updateUI(user);
            } else {
                Toast.makeText(ProfileActivity.this, "Authentication FAILED.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signIn() {

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.EmailBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build()
                        )).build(), RC_SIGN_IN);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        profileSignOut.setVisibility(View.INVISIBLE);
                        signIn.setVisibility(View.VISIBLE);
                        changeBD = true;

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            signIn.setVisibility(View.INVISIBLE);
            profileSignOut.setVisibility(View.VISIBLE);

            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri image = user.getPhotoUrl();
            Log.d("TAG", "image " + image);

            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constant.NAME, name);
            editor.putString(Constant.EMAIL, email);
            editor.putString(Constant.IMAGE, String.valueOf(image));
            editor.apply();

            titleText.setText(getResources().getString(R.string.hello, name));
            emailInfo.setText(getResources().getString(R.string.email_info, email));
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.btnSignOut:
                signOut();
                break;
            case R.id.signInBtn:
                signIn();
                break;
            default:
                break;
        }

    }
}
