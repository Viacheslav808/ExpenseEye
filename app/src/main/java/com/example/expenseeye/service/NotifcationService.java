package com.example.expenseeye.service;

public class NotifcationService {

    private String alertType;
    private String message;

    private Integer userid;

    public NotifcationService(Integer userid, String alertType, String message) {
        this.userid = userid;
        this.alertType = alertType;
        this.message = message;
    }
    public void sendBudgetAlert(Integer userid, String message) {

    }

    public void sendWarning(Integer userid, String message) {

    }

    public void scheduleNotifcation(Integer userid, Long date, String message) {

    }
}
