package com.example.expenseeye.repository.budget;

import com.example.expenseeye.model.budget.Budget;

import java.util.ArrayList;
import java.util.List;

public class BudgetDao {
    private final List<Budget> budgets = new ArrayList<>();

    public BudgetDao() {
        budgets.add(new Budget(2, 1000));
        budgets.add(new Budget(3, 900));
        budgets.add(new Budget(4, 350));
        budgets.add(new Budget(5, 500));
        budgets.add(new Budget(6, 250));
    }

    public List<Budget> getAllBudgets() {
        return new ArrayList<>(budgets);
    }
}
