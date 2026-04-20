package com.example.expenseeye.service;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * In-app alert dispatcher for budget events.
 * Supports both "warning" (near limit) and "exceeded" alerts.
 * Queues messages when no listener is attached so nothing is lost while
 * the user is on a different screen.
 */
public class NotificationService {

    public enum Level { WARNING, EXCEEDED }

    public interface BudgetAlertListener {
        void onBudgetAlert(Level level, String message);
    }

    private final Context context;
    private BudgetAlertListener listener;
    private final List<PendingAlert> pending = new ArrayList<>();

    public NotificationService(Context context) {
        this.context = context;
    }

    public void setBudgetAlertListener(BudgetAlertListener listener) {
        this.listener = listener;
        if (listener != null && !pending.isEmpty()) {
            for (PendingAlert p : pending) {
                listener.onBudgetAlert(p.level, p.message);
            }
            pending.clear();
        }
    }

    public void sendBudgetAlert(Level level, String message) {
        if (listener != null) {
            listener.onBudgetAlert(level, message);
        } else {
            pending.add(new PendingAlert(level, message));
        }
    }

    private static class PendingAlert {
        final Level level;
        final String message;
        PendingAlert(Level level, String message) {
            this.level = level;
            this.message = message;
        }
    }
}