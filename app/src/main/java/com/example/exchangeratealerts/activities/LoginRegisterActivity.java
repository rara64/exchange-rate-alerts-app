package com.example.exchangeratealerts.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.models.JWT;
import com.example.exchangeratealerts.modules.APIClient;
import com.example.exchangeratealerts.modules.PrivateStorage;

import java.nio.charset.Charset;

public class LoginRegisterActivity extends AppCompatActivity {

    private static APIClient client;
    private static PrivateStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_register_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        client = new APIClient();
        storage = new PrivateStorage(findViewById(R.id.loginButton));

        autoLogin();
    }

    private void autoLogin() {
        String username = new String(
                Base64.decode(
                        storage.getPreferenceString("username"),
                        Base64.DEFAULT), Charset.defaultCharset());

        String password = new String(
                Base64.decode(
                        storage.getPreferenceString("password"),
                        Base64.DEFAULT), Charset.defaultCharset());

        if (!username.isEmpty() && !password.isEmpty()) {
            EditText userInput = findViewById(R.id.userInput);
            EditText passwordInput = findViewById(R.id.passwordInput);
            userInput.setText(username);
            passwordInput.setText(password);
            onLoginClick(findViewById(R.id.loginButton), false);
        }
    }

    private void saveCredentials(String username, String password) {
        storage.setPreferenceString("username",
                new String(
                        Base64.encode(username.getBytes(), Base64.DEFAULT),
                        Charset.defaultCharset()));

        storage.setPreferenceString("password",
                new String(
                        Base64.encode(password.getBytes(), Base64.DEFAULT),
                        Charset.defaultCharset()));
    }

    private void showSpinner() {
        LinearLayout authButtonGroup = findViewById(R.id.authButtonGroup);
        authButtonGroup.setVisibility(GONE);

        ProgressBar spinner = findViewById(R.id.spinner);
        spinner.setVisibility(VISIBLE);
    }

    private void hideSpinner() {
        ProgressBar spinner = findViewById(R.id.spinner);
        spinner.setVisibility(GONE);

        LinearLayout authButtonGroup = findViewById(R.id.authButtonGroup);
        authButtonGroup.setVisibility(VISIBLE);
    }

    public void onLoginClick(View view) {
        onLoginClick(view, true);
    }
    public void onLoginClick(View view, Boolean saveCredentials) {
        showSpinner();

        EditText userInput = findViewById(R.id.userInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        String username = userInput.getText().toString();
        String password = passwordInput.getText().toString();

        client.Login(username, password, new APIClient.LoginCallback() {
            @Override
            public void onSuccess(JWT token) {
                hideSpinner();

                if (saveCredentials)
                    saveCredentials(username, password);

                setContentView(R.layout.targets_activity);
            }

            @Override
            public void onFailure(int httpCode) {
                TextView messageTextView = findViewById(R.id.messageTextView);

                if (httpCode == 401) {
                    messageTextView.setText(R.string.login_failure_unauthorized);
                }
                else {
                    messageTextView.setText(R.string.login_failure_http);
                }

                hideSpinner();
            }
        });
    }

    public void onRegisterClick(View view) {
        onRegisterClick(view, true);
    }
    public void onRegisterClick(View view, Boolean saveCredentials) {
        showSpinner();

        EditText userInput = findViewById(R.id.userInput);
        EditText passwordInput = findViewById(R.id.passwordInput);
        String username = userInput.getText().toString();
        String password = passwordInput.getText().toString();
        client.Register(username, password, new APIClient.RegisterCallback() {
            @Override
            public void onSuccess() {
                hideSpinner();

                if (saveCredentials)
                    saveCredentials(username, password);

                setContentView(R.layout.targets_activity);
            }

            @Override
            public void onFailure(int httpCode) {
                TextView messageTextView = findViewById(R.id.messageTextView);

                if (httpCode == 409) {
                    messageTextView.setText(R.string.login_failure_user_exists);
                }
                else {
                    messageTextView.setText(R.string.login_failure_http);
                }

                hideSpinner();
            }
        });
    }
}