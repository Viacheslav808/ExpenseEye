package com.example.expenseeye.repository.reports;

import com.example.expenseeye.data.FinanceStore;
import com.example.expenseeye.model.finance.AccountRecord;
import com.example.expenseeye.model.finance.CategoryRecord;
import com.example.expenseeye.model.finance.TransactionRecord;
import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportRepo {

    private final FinanceStore financeStore;

    public ReportRepo() {
        this(FinanceStore.getInstance());
    }

    public ReportRepo(FinanceStore financeStore) {
        this.financeStore = financeStore;
    }

    public List<MonthlySpending> getMonthlySpending() {
        Map<String, Double> totals = new HashMap<>();
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMM yyyy", Locale.US);

        for (TransactionRecord row : financeStore.getTransactions()) {
            if (!"expense".equalsIgnoreCase(row.getType())) {
                continue;
            }
            String key = row.getDate().withDayOfMonth(1).format(monthFormat);
            totals.put(key, totals.getOrDefault(key, 0.0) + row.getAmount());
        }

        List<MonthlySpending> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            result.add(new MonthlySpending(entry.getKey(), entry.getValue()));
        }
        result.sort(Comparator.comparing(MonthlySpending::getMonthLabel));
        return result;
    }

    public List<CategoryTotal> getCategoryTotals() {
        Map<Integer, String> categoryNames = new HashMap<>();
        for (CategoryRecord category : financeStore.getCategories()) {
            categoryNames.put(category.getId(), category.getName());
        }

        Map<String, Double> totals = new HashMap<>();
        for (TransactionRecord row : financeStore.getTransactions()) {
            if (!"expense".equalsIgnoreCase(row.getType())) {
                continue;
            }
            String categoryName = categoryNames.getOrDefault(row.getCategoryId(), "Unknown");
            totals.put(categoryName, totals.getOrDefault(categoryName, 0.0) + row.getAmount());
        }

        List<CategoryTotal> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            result.add(new CategoryTotal(entry.getKey(), entry.getValue()));
        }
        result.sort((a, b) -> Double.compare(b.getTotal(), a.getTotal()));
        return result;
    }

    public List<AccountSummary> getAccountSummaries() {
        Map<Integer, String> accountNames = new HashMap<>();
        for (AccountRecord account : financeStore.getAccounts()) {
            accountNames.put(account.getId(), account.getName());
        }

        Map<Integer, Double> income = new HashMap<>();
        Map<Integer, Double> expense = new HashMap<>();

        for (TransactionRecord row : financeStore.getTransactions()) {
            if ("income".equalsIgnoreCase(row.getType())) {
                income.put(row.getAccountId(), income.getOrDefault(row.getAccountId(), 0.0) + row.getAmount());
            } else {
                expense.put(row.getAccountId(), expense.getOrDefault(row.getAccountId(), 0.0) + row.getAmount());
            }
        }

        List<AccountSummary> result = new ArrayList<>();
        for (Integer accountId : allAccountIds(accountNames, income, expense)) {
            result.add(new AccountSummary(
                    accountNames.getOrDefault(accountId, "Unknown"),
                    income.getOrDefault(accountId, 0.0),
                    expense.getOrDefault(accountId, 0.0)
            ));
        }
        result.sort((a, b) -> Double.compare(b.getNet(), a.getNet()));
        return result;
    }

    private List<Integer> allAccountIds(Map<Integer, String> accountNames, Map<Integer, Double> income, Map<Integer, Double> expense) {
        List<Integer> accountIds = new ArrayList<>(accountNames.keySet());
        for (Integer account : income.keySet()) {
            if (!accountIds.contains(account)) {
                accountIds.add(account);
            }
        }
        for (Integer account : expense.keySet()) {
            if (!accountIds.contains(account)) {
                accountIds.add(account);
            }
        }
        return accountIds;
    }
}
