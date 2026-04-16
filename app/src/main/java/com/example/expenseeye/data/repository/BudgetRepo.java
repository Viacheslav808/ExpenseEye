package com.example.expenseeye.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.expenseeye.data.dao.BudgetDao;
import com.example.expenseeye.data.database.ExpenseEyeDatabase;
import com.example.expenseeye.data.model.Budget;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BudgetRepo {

    private final BudgetDao budgetDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BudgetRepo(Context context) {
        ExpenseEyeDatabase db = ExpenseEyeDatabase.getInstance(context);
        budgetDao = db.budgetDao();
    }

    public void insertBudget(Budget budget) {
        executor.execute(() -> budgetDao.insert(budget));
    }

    public void updateBudget(Budget budget) {
        executor.execute(() -> budgetDao.update(budget));
    }

    public void deleteBudget(Budget budget) {
        executor.execute(() -> budgetDao.delete(budget));
    }

    public LiveData<List<Budget>> getBudgetsForUser(int userId) {
        return budgetDao.getBudgetsForUser(userId);
    }

    public double getSpentForBudget(Budget budget) {
        return budgetDao.getSpentForCategory(
                budget.getUserId(),
                budget.getCategoryId(),
                budget.getPeriodStart(),
                budget.getPeriodEnd()
        );
    }
}