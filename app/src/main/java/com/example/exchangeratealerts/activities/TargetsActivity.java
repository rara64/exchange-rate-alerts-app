package com.example.exchangeratealerts.activities;

import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.modules.APIClient;
import com.example.exchangeratealerts.modules.PrivateStorage;

public class TargetsActivity extends AppCompatActivity {
    private static APIClient client;
    private static PrivateStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.targets_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.targets), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        client = new APIClient();
        storage = new PrivateStorage(getApplicationContext());

        TextView welcomeTitle = findViewById(R.id.welcomeTitle);
        welcomeTitle.setText(
                getString(R.string.targets_welcome_title,
                storage.getUsernamePref()));
    }


}
