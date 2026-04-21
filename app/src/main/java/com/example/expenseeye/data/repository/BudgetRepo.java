package com.example.expenseeye.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.expenseeye.data.dao.BudgetDao;
import com.example.expenseeye.data.database.ExpenseEyeDatabase;
import com.example.expenseeye.data.model.Budget;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thin wrapper around BudgetDao that guarantees writes
 * happen on a background thread.
 */
public class BudgetRepo {

    private final BudgetDao budgetDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public BudgetRepo(Context context) {
        ExpenseEyeDatabase db = ExpenseEyeDatabase.getInstance(context);
        this.budgetDao = db.budgetDao();
    }

    // ---- Writes (off main thread) ----

    public void insertBudget(Budget budget) {
        if (budget == null) return;
        executor.execute(() -> budgetDao.insert(budget));
    }

    public void updateBudget(Budget budget) {
        if (budget == null) return;
        executor.execute(() -> budgetDao.update(budget));
    }

    public void deleteBudget(Budget budget) {
        if (budget == null) return;
        executor.execute(() -> budgetDao.delete(budget));
    }

    // ---- Reads ----

    public LiveData<List<Budget>> getBudgetsForUser(int userId) {
        return budgetDao.getBudgetsForUser(userId);
    }

    /**
     * Sums this user's transactions for the budget's category within its period.
     * Blocking — must be called off the main thread.
     */
    public double getSpentForBudget(Budget budget) {
        return budgetDao.getSpentForCategory(
                budget.getUserId(),
                budget.getCategoryId(),
                budget.getPeriodStart(),
                budget.getPeriodEnd()
        );
    }
}