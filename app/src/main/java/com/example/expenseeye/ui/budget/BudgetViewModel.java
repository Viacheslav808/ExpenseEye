package com.example.expenseeye.ui.budget;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.expenseeye.data.model.Budget;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.data.model.Transaction;
import com.example.expenseeye.data.repository.BudgetRepo;
import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.data.repository.FinanceRepoProvider;
import com.example.expenseeye.model.reports.BudgetEvaluation;
import com.example.expenseeye.model.reports.BudgetEvaluator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel that exposes a reactive stream of BudgetEvaluations by
 * combining budgets, transactions, and categories. Also handles all
 * budget CRUD operations off the main thread.
 */
public class BudgetViewModel extends AndroidViewModel {

    private final BudgetRepo budgetRepo;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final LiveData<List<Budget>> budgets;
    private final LiveData<List<Category>> categories;
    private final LiveData<List<Transaction>> transactions;

    private final MediatorLiveData<List<BudgetEvaluation>> evaluations = new MediatorLiveData<>();

    public BudgetViewModel(@NonNull Application application) {
        super(application);

        budgetRepo = new BudgetRepo(application);
        FinanceRepo financeRepo = FinanceRepoProvider.get(application);

        SharedPreferences prefs = application.getSharedPreferences("session", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);

        categories = financeRepo.getAllCategories();

        if (userId != -1) {
            budgets = budgetRepo.getBudgetsForUser(userId);
            transactions = financeRepo.getTransactionsForUser(userId);
        } else {
            budgets = null;
            transactions = null;
        }

        if (budgets != null) {
            evaluations.addSource(budgets, b -> recompute());
        }

        evaluations.addSource(categories, c -> recompute());

        if (transactions != null) {
            evaluations.addSource(transactions, t -> recompute());
        }
    }

    // Public streams
    public LiveData<List<BudgetEvaluation>> getEvaluations() {
        return evaluations;
    }

    public LiveData<List<Budget>> getBudgets() {
        return budgets;
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    // Mutations (all run off the main thread)
    public void insertBudget(Budget budget) {
        executor.execute(() -> budgetRepo.insertBudget(budget));
    }

    public void updateBudget(Budget budget) {
        executor.execute(() -> budgetRepo.updateBudget(budget));
    }

    public void deleteBudget(Budget budget) {
        executor.execute(() -> budgetRepo.deleteBudget(budget));
    }

    public void deleteBudgetById(int budgetId) {
        executor.execute(() -> {
            List<Budget> all = budgets != null ? budgets.getValue() : null;
            if (all == null) return;

            for (Budget bg : all) {
                if (bg.getId() == budgetId) {
                    budgetRepo.deleteBudget(bg);
                    return;
                }
            }
        });
    }

    // Internals
    private void recompute() {
        List<Budget> b = budgets != null ? budgets.getValue() : null;
        List<Transaction> t = transactions != null ? transactions.getValue() : null;
        List<Category> c = categories.getValue();

        if (b == null || t == null || c == null) return;

        Map<Integer, String> nameMap = new HashMap<>();
        for (Category cat : c) {
            nameMap.put(cat.getId(), cat.getName());
        }

        evaluations.setValue(BudgetEvaluator.evaluate(b, t, nameMap));
    }
}