package com.example.exchangeratealerts.workers;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.exchangeratealerts.R;
import com.example.exchangeratealerts.models.CurrencyAlert;
import com.example.exchangeratealerts.models.JWT;
import com.example.exchangeratealerts.modules.APIClient;
import com.example.exchangeratealerts.modules.PrivateStorage;

import org.json.JSONException;

import java.util.Arrays;

public class AlertWorker extends Worker {
    private final APIClient client;
    private final PrivateStorage storage;

    private final Context context;

    private int notification_id = 0;

    public AlertWorker(Context ctx, WorkerParameters workerParams) {
        super(ctx, workerParams);
        client = new APIClient();
        storage = new PrivateStorage(ctx);
        context = ctx;
        createNotificationChannel();
    }

    private void sendAlertNotification() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        var builder = new NotificationCompat.Builder(context, "exchangeratealerts.news")
                .setSmallIcon(R.drawable.currency_exchange)
                .setContentTitle(context.getString(R.string.alert_notification_title))
                .setContentText(context.getString(R.string.alert_notification_body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notification_id, builder.build());
        }
    }

    private void sendCredentialFailureNotification() {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        var builder = new NotificationCompat.Builder(context, "exchangeratealerts.news")
                .setSmallIcon(R.drawable.currency_exchange)
                .setContentTitle(context.getString(R.string.alert_notification_credential_failure_title))
                .setContentText(context.getString(R.string.alert_notification_credential_failure_body))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(notification_id, builder.build());
        }
    }

    private void updateAlerts(String token) {
        client.GetCurrencyAlerts(token, new APIClient.GetCurrencyAlertsCallback() {
            @Override
            public void onSuccess(CurrencyAlert[] alerts) {
                notification_id++;

                CurrencyAlert[] pastAlerts = storage.getAlertsPref();
                if (!Arrays.equals(alerts, pastAlerts)) {
                    storage.setAlertsPref(alerts);
                    sendAlertNotification();
                }
            }

            @Override
            public void onFailure(int httpCode) {

            }
        });
    }

    @NonNull
    @Override
    public Result doWork() {
        client.Login(storage.getUsernamePref(), storage.getPasswordPref(), new APIClient.LoginCallback() {
            @Override
            public void onSuccess(JWT token) {
                updateAlerts(token.token);
            }

            @Override
            public void onFailure(int httpCode) {
                WorkManager.getInstance(context).cancelAllWork();
                sendCredentialFailureNotification();
            }
        });
        return Result.success();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Exchange Rate Alerts";
            String description = "Notifications for currency rate alerts.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String channelId = "exchangeratealerts.news";

            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(context, NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
