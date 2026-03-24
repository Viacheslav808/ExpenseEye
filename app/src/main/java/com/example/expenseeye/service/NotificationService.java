package com.example.expenseeye.service;

import android.content.Context;
import android.widget.Toast;

public class NotificationService {

    private final Context context;

    public NotificationService(Context context) {
        this.context = context.getApplicationContext();
    }

    public void sendBudgetAlert(Integer userId, String message) {
        Toast.makeText(context, "Budget Alert: " + message, Toast.LENGTH_LONG).show();
    }

    public void sendWarning(Integer userId, String message) {
        Toast.makeText(context, "Warning: " + message, Toast.LENGTH_SHORT).show();
    }

    public void scheduleNotification(Integer userId, Long date, String message) {
        Toast.makeText(context, "Scheduled: " + message, Toast.LENGTH_SHORT).show();
    }

    public void runChecks() {
        // TODO: When Budget classes exist, check for overages here
        // TODO: When Reminder classes exist, check reminders here
    }
}
