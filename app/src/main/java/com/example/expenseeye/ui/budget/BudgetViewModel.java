package com.example.expenseeye.ui.budget;

import android.app.Application;

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

public class BudgetViewModel extends AndroidViewModel {

    private final BudgetRepo budgetRepo;
    private final FinanceRepo financeRepo;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private final LiveData<List<Budget>> budgets;
    private final LiveData<List<Category>> categories;
    private final LiveData<List<Transaction>> transactions;

    private final MediatorLiveData<List<BudgetEvaluation>> evaluations = new MediatorLiveData<>();

    public BudgetViewModel(@NonNull Application application) {
        super(application);
        budgetRepo = new BudgetRepo(application);
        financeRepo = FinanceRepoProvider.get(application);

        budgets = budgetRepo.getAllBudgets();
        categories = financeRepo.getAllCategories();
        transactions = financeRepo.getAllTransactions();

        evaluations.addSource(budgets, b -> recompute());
        evaluations.addSource(transactions, t -> recompute());
        evaluations.addSource(categories, c -> recompute());
    }

    private void recompute() {
        List<Budget> b = budgets.getValue();
        List<Transaction> t = transactions.getValue();
        List<Category> c = categories.getValue();
        if (b == null || t == null || c == null) return;

        Map<Integer, String> nameMap = new HashMap<>();
        for (Category cat : c) nameMap.put(cat.getId(), cat.getName());

        evaluations.setValue(BudgetEvaluator.evaluate(b, t, nameMap));
    }

    public LiveData<List<BudgetEvaluation>> getEvaluations() { return evaluations; }
    public LiveData<List<Budget>> getBudgets() { return budgets; }
    public LiveData<List<Category>> getCategories() { return categories; }

    public void insertBudget(Budget budget) {
        executor.execute(() -> budgetRepo.insertBudget(budget));
    }

    public void updateBudget(Budget budget) {
        executor.execute(() -> budgetRepo.updateBudget(budget));
    }

    public void deleteBudgetById(int budgetId) {
        executor.execute(() -> {
            List<Budget> all = budgets.getValue();
            if (all == null) return;
            for (Budget bg : all) {
                if (bg.getId() == budgetId) {
                    budgetRepo.deleteBudget(bg);
                    break;
                }
            }
        });
    }
}