package com.example.expenseeye.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final Context context;

    public NotificationService(Context context) {
        this.context = context;
    }

    public interface BudgetAlertListener {
        void onBudgetExceeded(String message);
    }

    private BudgetAlertListener listener;


    private final List<String> pendingMessages = new ArrayList<>();

    public void setBudgetAlertListener(BudgetAlertListener listener) {
        this.listener = listener;


        if (!pendingMessages.isEmpty()) {
            String combined = String.join("\n• ", pendingMessages);
            combined = "You exceeded multiple budgets:\n• " + combined;

            listener.onBudgetExceeded(combined);
            pendingMessages.clear();
        }
    }

    public void sendBudgetAlert(String message) {
        if (listener != null) {
            listener.onBudgetExceeded(message);
        } else {
            pendingMessages.add(message);
        }
    }
}
