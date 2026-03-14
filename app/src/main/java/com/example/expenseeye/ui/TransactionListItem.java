package com.example.expenseeye.ui;

public class TransactionListItem {
    private final String name;
    private final double amount;
    private final String account;
    private final String category;

    public TransactionListItem(String name, double amount, String account, String category) {
        this.name = name;
        this.amount = amount;
        this.account = account;
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public String getAccount() {
        return account;
    }

    public String getCategory() {
        return category;
    }
}
