package com.example.expenseeye.model.reports;

import com.example.expenseeye.data.model.Budget;
import com.example.expenseeye.data.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Eevaluates budgets against a list of transactions.
 * Spending is calculated using each budget's own stored period
 * (periodStart → periodEnd), not a hardcoded calendar month.
 */
public class BudgetEvaluator {

    public static List<BudgetEvaluation> evaluate(
            List<Budget> budgets,
            List<Transaction> transactions,
            Map<Integer, String> categoryNames
    ) {
        List<BudgetEvaluation> results = new ArrayList<>();
        for (Budget budget : budgets) {
            double spent = sumSpent(budget, transactions);
            String categoryName = categoryNames.getOrDefault(budget.getCategoryId(), "Unknown");
            results.add(new BudgetEvaluation(
                    budget.getId(),
                    budget.getName(),
                    categoryName,
                    budget.getLimitAmount(),
                    spent
            ));
        }
        return results;
    }

    /**
     * Sums the spending for a given budget by respecting the budget's
     * stored period window. This makes the evaluator flexible for any
     * future budget period (weekly, monthly, yearly, trip-based, etc.).
     */
    private static double sumSpent(Budget budget, List<Transaction> transactions) {
        long budgetStart = budget.getPeriodStart();
        long budgetEnd = budget.getPeriodEnd();

        double total = 0;
        for (Transaction t : transactions) {
            if (t.getCategoryId() == budget.getCategoryId()
                    && t.getDate() >= budgetStart
                    && t.getDate() <= budgetEnd) {
                total += t.getAmount();
            }
        }
        return total;
    }
}