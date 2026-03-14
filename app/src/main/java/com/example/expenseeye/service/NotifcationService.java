package com.example.expenseeye.service;

import java.util.ArrayList;
import java.util.List;

public class NotifcationService {

    private final Integer userId;
    private final List<String> sentMessages = new ArrayList<>();

    public NotifcationService(Integer userId, String alertType, String message) {
        this.userId = userId;
        sentMessages.add(format(alertType, message, null, null));
    }

    public void sendBudgetAlert(Integer userId, Integer transactionId, Integer categoryId, String message) {
        sentMessages.add(format("budget", message, transactionId, categoryId));
    }

    public void sendWarning(Integer userId, Integer transactionId, Integer categoryId, String message) {
        sentMessages.add(format("warning", message, transactionId, categoryId));
    }

    public void scheduleNotifcation(Integer userId, Long date, Integer transactionId, Integer categoryId, String message) {
        sentMessages.add(date + ":" + format("scheduled", message, transactionId, categoryId));
    }

    public List<String> history() {
        return new ArrayList<>(sentMessages);
    }

    private String format(String type, String message, Integer transactionId, Integer categoryId) {
        return "user=" + userId
                + "|type=" + type
                + "|transactionId=" + (transactionId == null ? "n/a" : transactionId)
                + "|categoryId=" + (categoryId == null ? "n/a" : categoryId)
                + "|message=" + message;
    }
}
