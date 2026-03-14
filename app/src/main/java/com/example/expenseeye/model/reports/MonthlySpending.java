package com.example.expenseeye.model.reports;

public class MonthlySpending {
    private final String monthLabel;
    private final double total;

    public MonthlySpending(String monthLabel, double total) {
        this.monthLabel = monthLabel;
        this.total = total;
    }

    public String getMonthLabel() {
        return monthLabel;
    }

    public double getTotal() {
        return total;
    }
}
