package com.example.exchangeratealerts.activities;

import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.adapters.TargetsRecyclerViewAdapter;
import com.example.exchangeratealerts.dialogs.TargetDialog;
import com.example.exchangeratealerts.models.CurrencyAlert;
import com.example.exchangeratealerts.models.CurrencyTarget;
import com.example.exchangeratealerts.modules.APIClient;
import com.example.exchangeratealerts.modules.PrivateStorage;

import java.util.Arrays;
import java.util.List;

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

        updateCurrencyTargetList();
    }

    List<CurrencyTarget> currencyTargetList;
    private void updateCurrencyTargetList() {
        String token = storage.getTokenPref();

        client.GetCurrencyTargets(token, new APIClient.CurrencyTargetsCallback() {
            @Override
            public void onSuccess(CurrencyTarget[] targets) {
                currencyTargetList = Arrays.asList(targets);
                RecyclerView recyclerView = findViewById(R.id.targetsRecycler);
                client.GetCurrencyAlerts(token, new APIClient.GetCurrencyAlertsCallback() {
                    @Override
                    public void onSuccess(CurrencyAlert[] alerts) {
                        recyclerView.setAdapter(new TargetsRecyclerViewAdapter(targets, alerts));
                    }

                    @Override
                    public void onFailure(int httpCode) {

                    }
                });

            }

            @Override
            public void onFailure(int httpCode) {

            }
        });
    }

    public void onAddTargetClick(View view) {
        TargetDialog dialog = new TargetDialog(
                "",
                "",
                "",
                view.getContext());
        dialog.show(getSupportFragmentManager(), "EditTargetDialog");
    }
}
