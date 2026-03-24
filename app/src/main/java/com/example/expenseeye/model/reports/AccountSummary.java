package com.example.expenseeye.model.reports;

public class AccountSummary {
    private final String accountName;
    private final double totalSpent;
    private final int transactionCount;

    public AccountSummary(String accountName, double totalSpent, int transactionCount) {
        this.accountName = accountName;
        this.totalSpent = totalSpent;
        this.transactionCount = transactionCount;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public double getAverageAmount() {
        return transactionCount == 0 ? 0.0 : totalSpent / transactionCount;
    }
}
