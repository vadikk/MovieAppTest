package com.example.vadym.movieapp.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.vadym.movieapp.R;
import com.example.vadym.movieapp.constans.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.btnReg)
    Button btnSubmit;
    @BindView(R.id.editTextPasswordReg)
    EditText passwordText;
    @BindView(R.id.editTextEmailReg)
    EditText emailText;
    @BindView(R.id.editTextNameProfile)
    EditText nameText;
    @BindView(R.id.registration)
    CardView registrCardView;
    @BindView(R.id.profileSignOut)
    CardView profileSignOut;
    @BindView(R.id.btnSignOut)
    Button signOut;
    @BindView(R.id.hello)
    TextView titleText;
    @BindView(R.id.emailInfo)
    TextView emailInfo;

    private boolean isAutoSignIn = false;
    private SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // TODO: 3/6/18 А де вхід? чому я не можу зайти в профіль, якщо я вже був зареєстрованим?
        // TODO: 3/6/18 Рагульний дизайн, поля - не рівні.
        // TODO: 3/6/18 Не можна повернутися назад в програмі.
        // TODO: 3/6/18 При поверненні назад - не оновлюж інфу на дровері.
        
        ButterKnife.bind(this);

        init();

        btnSubmit.setOnClickListener(this);
        signOut.setOnClickListener(this);

        passwordEditTextListener();
    }

    private void init() {

        preferences = getSharedPreferences(Constant.APP_PREFS, Context.MODE_PRIVATE);
        isAutoSignIn = preferences.getBoolean(Constant.IS_SIGN_IN, false);
        if (isAutoSignIn) {

            showProfile();
        }
    }

    private void setSettingsSharedPreference() {

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Constant.NAME, name);
        editor.putString(Constant.EMAIL, email);
        editor.putString(Constant.PASSWORD, password);
        editor.putBoolean(Constant.IS_SIGN_IN, true);
        editor.apply();
    }

    private void passwordEditTextListener() {
        if (isRegisterOK()) {
            passwordText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int i, KeyEvent keyEvent) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (i) {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:
                                setSettingsSharedPreference();
                                showProfile();
                                return true;
                            default:
                                return false;
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {
            case R.id.btnReg:
                if (isRegisterOK()) {
                    setSettingsSharedPreference();
                    showProfile();
                }
                break;
            case R.id.btnSignOut:
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                profileSignOut.setVisibility(View.INVISIBLE);
                registrCardView.setVisibility(View.VISIBLE);

                break;
            default:
                break;
        }


    }

    private void showProfile() {

        registrCardView.setVisibility(View.INVISIBLE);
        profileSignOut.setVisibility(View.VISIBLE);

        String title = preferences.getString(Constant.NAME, "");
        String email = preferences.getString(Constant.EMAIL, "");

        titleText.setText(getResources().getString(R.string.hello, title));
        emailInfo.setText(getResources().getString(R.string.email_info, email));

    }

    private boolean isRegisterOK() {

        String name = nameText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();

        if (nameText.getText().length() == 0) {
            nameText.setError(getString(R.string.name_error));
            return false;
        } else if (emailText.getText().length() == 0) {
            emailText.requestFocus();
            emailText.setError(getString(R.string.email_error));
            return false;
        } else if (passwordText.getText().length() == 0) {
            passwordText.requestFocus();
            passwordText.setError(getString(R.string.password_error));
            return false;
        }

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            return true;
        }

        return false;
    }
}
