package com.example.expenseeye.model.reports;

import com.example.expenseeye.data.model.Budget;
import com.example.expenseeye.data.model.Transaction;

import java.util.ArrayList;
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
            String name  = categoryNames.getOrDefault(budget.getCategoryId(), "Unknown");
            results.add(new BudgetEvaluation(name, budget.getLimitAmount(), spent));
        }

        return results;
    }

    private static double sumSpent(Budget budget, List<Transaction> transactions) {
        double total = 0;
        for (Transaction t : transactions) {
            if (t.getCategoryId() == budget.getCategoryId()
                    && t.getDate() >= budget.getPeriodStart()
                    && t.getDate() <= budget.getPeriodEnd()) {
                total += t.getAmount();
            }
        }
        return total;
    }
}
