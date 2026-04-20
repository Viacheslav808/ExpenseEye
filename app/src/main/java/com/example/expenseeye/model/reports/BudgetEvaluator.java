package com.example.expenseeye.model.reports;

import com.example.expenseeye.data.model.Budget;
import com.example.expenseeye.data.model.Transaction;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

// Pure-logic class: evaluates budgets against a list of transactions.
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

    private static double sumSpent(Budget budget, List<Transaction> transactions) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startOfMonth = cal.getTimeInMillis();

        cal.add(Calendar.MONTH, 1);
        cal.add(Calendar.MILLISECOND, -1);
        long endOfMonth = cal.getTimeInMillis();

        double total = 0;
        for (Transaction t : transactions) {
            if (t.getCategoryId() == budget.getCategoryId()
                    && t.getDate() >= startOfMonth
                    && t.getDate() <= endOfMonth) {
                total += t.getAmount();
            }
        }
        return total;
    }
}