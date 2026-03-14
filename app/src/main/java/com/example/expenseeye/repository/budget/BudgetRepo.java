package com.example.expenseeye.repository.budget;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expenseeye.data.FinanceStore;
import com.example.expenseeye.model.budget.Budget;
import com.example.expenseeye.model.finance.TransactionRecord;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BudgetRepo {
    private final BudgetDao budgetDao;
    private final FinanceStore financeStore;
    private final MutableLiveData<Map<Integer, Double>> spendingByCategory = new MutableLiveData<>();

    public BudgetRepo(BudgetDao budgetDao, FinanceStore financeStore) {
        this.budgetDao = budgetDao;
        this.financeStore = financeStore;
        refresh();
    }

    public List<Budget> getBudgets() {
        return budgetDao.getAllBudgets();
    }

    public LiveData<Map<Integer, Double>> getSpendingByCategory() {
        return spendingByCategory;
    }

    public void refresh() {
        Map<Integer, Double> totals = new HashMap<>();
        for (TransactionRecord row : financeStore.getTransactions()) {
            if (!"expense".equalsIgnoreCase(row.getType())) {
                continue;
            }
            totals.put(row.getCategoryId(), totals.getOrDefault(row.getCategoryId(), 0.0) + row.getAmount());
        }
        spendingByCategory.setValue(totals);
    }
}
