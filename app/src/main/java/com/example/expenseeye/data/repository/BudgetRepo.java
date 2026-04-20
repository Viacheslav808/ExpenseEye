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
 * Thin wrapper around {@link BudgetDao} that guarantees all writes
 * happen on a background thread. Reads return LiveData so observers
 * stay on the main thread automatically.
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

    public LiveData<List<Budget>> getAllBudgets() {
        return budgetDao.getAllBudgets();
    }

    /**
     * Sums transactions for this budget's category within its period.
     * Blocking — must be called off the main thread.
     */
    public double getSpentForBudget(Budget budget) {
        return budgetDao.getSpentForCategory(
                budget.getCategoryId(),
                budget.getPeriodStart(),
                budget.getPeriodEnd()
        );
    }
}