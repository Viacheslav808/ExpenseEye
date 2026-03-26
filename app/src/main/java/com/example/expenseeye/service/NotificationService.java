package com.example.expenseeye.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.expenseeye.R;




public class NotificationService {

    private final Context context;

    public NotificationService(Context context) {
        this.context = context.getApplicationContext();
    }

    public void sendBudgetAlert(String message) {
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "budget_alerts",
                    "Budget Alerts",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "budget_alerts")
                .setSmallIcon(R.drawable.ic_warning) // use any icon you have
                .setContentTitle("Budget Alert")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public void sendWarning(String message) {
        Toast.makeText(context, "Warning: " + message, Toast.LENGTH_SHORT).show();
    }

    public void scheduleNotification( Long date, String message) {
        Toast.makeText(context, "Scheduled: " + message, Toast.LENGTH_SHORT).show();
    }


}
