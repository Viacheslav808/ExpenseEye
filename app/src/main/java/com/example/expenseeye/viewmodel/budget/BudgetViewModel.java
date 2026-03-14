package com.example.expenseeye.viewmodel.budget;

import androidx.lifecycle.LiveData;

import com.example.expenseeye.model.budget.Budget;
import com.example.expenseeye.repository.budget.BudgetEvaluator;
import com.example.expenseeye.repository.budget.BudgetRepo;

import java.util.List;
import java.util.Map;

public class BudgetViewModel {
    private final BudgetRepo budgetRepo;
    private final BudgetEvaluator budgetEvaluator;

    public BudgetViewModel(BudgetRepo budgetRepo, BudgetEvaluator budgetEvaluator) {
        this.budgetRepo = budgetRepo;
        this.budgetEvaluator = budgetEvaluator;
    }

    public List<Budget> budgets() {
        return budgetRepo.getBudgets();
    }

    public LiveData<Map<Integer, Double>> categorySpending() {
        return budgetRepo.getSpendingByCategory();
    }

    public double budgetUsage(double spent, double budgetLimit) {
        return budgetEvaluator.usagePercent(spent, budgetLimit);
    }

    public boolean overLimit(double spent, double budgetLimit) {
        return budgetEvaluator.isOverLimit(spent, budgetLimit);
    }
}
