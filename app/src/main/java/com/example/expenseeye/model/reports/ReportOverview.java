package com.example.expenseeye.model.reports;

public class ReportOverview {

    private final double totalSpending;
    private final double averageTransactionValue;
    private final int transactionCount;
    private final String topCategory;
    private final String topAccount;
    private final String peakMonth;

    public ReportOverview(double totalSpending,
                          double averageTransactionValue,
                          int transactionCount,
                          String topCategory,
                          String topAccount,
                          String peakMonth) {
        this.totalSpending = totalSpending;
        this.averageTransactionValue = averageTransactionValue;
        this.transactionCount = transactionCount;
        this.topCategory = topCategory;
        this.topAccount = topAccount;
        this.peakMonth = peakMonth;
    }

    public double getTotalSpending() {
        return totalSpending;
    }

    public double getAverageTransactionValue() {
        return averageTransactionValue;
    }

    public int getTransactionCount() {
        return transactionCount;
    }

    public String getTopCategory() {
        return topCategory;
    }

    public String getTopAccount() {
        return topAccount;
    }

    public String getPeakMonth() {
        return peakMonth;
    }
}
