package com.example.expenseeye.repository.budget;

public class BudgetEvaluator {
    public double usagePercent(double spent, double budgetLimit) {
        if (budgetLimit <= 0) {
            return 0;
        }
        return (spent / budgetLimit) * 100;
    }

    public boolean isOverLimit(double spent, double budgetLimit) {
        return spent > budgetLimit;
    }
}
