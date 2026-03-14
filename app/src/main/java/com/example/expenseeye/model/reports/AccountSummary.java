package com.example.expenseeye.model.reports;

public class AccountSummary {
    private final String accountName;
    private final double income;
    private final double expense;

    public AccountSummary(String accountName, double income, double expense) {
        this.accountName = accountName;
        this.income = income;
        this.expense = expense;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getIncome() {
        return income;
    }

    public double getExpense() {
        return expense;
    }

    public double getNet() {
        return income - expense;
    }
}
