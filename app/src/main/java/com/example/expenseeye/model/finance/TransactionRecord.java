package com.example.expenseeye.model.finance;

import java.time.LocalDate;

public class TransactionRecord {
    private final int id;
    private final int userId;
    private final int accountId;
    private final int categoryId;
    private final String type;
    private final double amount;
    private final LocalDate date;

    public TransactionRecord(int id, int userId, int accountId, int categoryId, String type, double amount, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.accountId = accountId;
        this.categoryId = categoryId;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getAccountId() {
        return accountId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }
}
