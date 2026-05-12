package com.example.exchangeratealerts.activities;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.icu.text.MessageFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.adapters.TargetsRecyclerViewAdapter;
import com.example.exchangeratealerts.dialogs.SettingsDialog;
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

        updateCurrencyTargetList(false);

        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipeRefreshLayout);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateCurrencyTargetList(true);
            }
        });
    }

    private List<CurrencyTarget> currencyTargetList;

    private void handleUpdateFailure(boolean isSwipeRefresh) {
        LinearLayout emptyRecyclerLayout = findViewById(R.id.emptyCollectionLayout);
        LinearLayout loadingDataSpinner = findViewById(R.id.loadingDataSpinner);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = findViewById(R.id.targetsRecycler);

        if (!isSwipeRefresh) {
            loadingDataSpinner.setVisibility(GONE);
        }
        else {
            refreshLayout.setRefreshing(false);
        }

        if (recyclerView.getAdapter() != null && recyclerView.getAdapter().getItemCount() > 0) {
            recyclerView.setVisibility(VISIBLE);
        }
        else {
            emptyRecyclerLayout.setVisibility(VISIBLE);
        }

        Toast toast = Toast.makeText(recyclerView.getContext(), getString(R.string.targets_refresh_failed), Toast.LENGTH_LONG);
        toast.show();
    }

    public void updateCurrencyTargetList(boolean isSwipeRefresh) {
        LinearLayout loadingDataSpinner = findViewById(R.id.loadingDataSpinner);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.swipeRefreshLayout);
        RecyclerView recyclerView = findViewById(R.id.targetsRecycler);
        recyclerView.setVisibility(GONE);

        if (!isSwipeRefresh) {
            loadingDataSpinner.setVisibility(VISIBLE);
        }

        String token = storage.getTokenPref();
        LinearLayout emptyRecyclerLayout = findViewById(R.id.emptyCollectionLayout);

        client.GetCurrencyTargets(token, new APIClient.ResponseCallback<CurrencyTarget[]>() {
            @Override
            public void onSuccess(CurrencyTarget[] targets) {
                currencyTargetList = Arrays.asList(targets);
                client.GetCurrencyAlerts(token, new APIClient.ResponseCallback<CurrencyAlert[]>() {
                    @Override
                    public void onSuccess(CurrencyAlert[] alerts) {

                        if (targets.length == 0) {
                            emptyRecyclerLayout.setVisibility(VISIBLE);
                        }
                        else {
                            emptyRecyclerLayout.setVisibility(GONE);
                            recyclerView.setAdapter(new TargetsRecyclerViewAdapter(targets, alerts));
                        }

                        if (!isSwipeRefresh) {
                            loadingDataSpinner.setVisibility(GONE);
                        }
                        else {
                            refreshLayout.setRefreshing(false);
                        }

                        if (targets.length != 0)
                            recyclerView.setVisibility(VISIBLE);
                    }

                    @Override
                    public void onFailure(int httpCode) {
                        handleUpdateFailure(isSwipeRefresh);
                    }
                });

            }

            @Override
            public void onFailure(int httpCode) {
                handleUpdateFailure(isSwipeRefresh);
            }
        });
    }

    public void onAddTargetClick(View view) {
        TargetDialog dialog = new TargetDialog(
                "",
                "",
                "",
                view.getContext(),
                new TargetDialog.TargetDialogCallback() {
                    @Override
                    public void onSuccess() {
                        updateCurrencyTargetList(false);
                    }
                });
        dialog.show(getSupportFragmentManager(), "EditTargetDialog");
    }

    public void onSettingsClick(View view) {
        SettingsDialog dialog = new SettingsDialog(view.getContext(), storage);
        dialog.show(getSupportFragmentManager(), "SettingsDialog");
    }
}
