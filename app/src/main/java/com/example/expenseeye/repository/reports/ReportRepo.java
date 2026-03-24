package com.example.expenseeye.repository.reports;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.expenseeye.data.model.TransactionWithDetails;
import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class ReportRepo {

    private final LiveData<List<MonthlySpending>> monthlySpending;
    private final LiveData<List<CategoryTotal>> categoryTotals;
    private final LiveData<List<AccountSummary>> accountSummaries;

    public ReportRepo(Context context) {
        FinanceRepo financeRepo = new FinanceRepo(context.getApplicationContext());
        LiveData<List<TransactionWithDetails>> transactions = financeRepo.getTransactionsWithDetails();

        monthlySpending = Transformations.map(transactions, rows -> buildMonthlySpending(rows));
        categoryTotals = Transformations.map(transactions, rows -> buildCategoryTotals(rows));
        accountSummaries = Transformations.map(transactions, rows -> buildAccountSummaries(rows));
    }

    public LiveData<List<MonthlySpending>> getMonthlySpending() {
        return monthlySpending;
    }

    public LiveData<List<CategoryTotal>> getCategoryTotals() {
        return categoryTotals;
    }

    public LiveData<List<AccountSummary>> getAccountSummaries() {
        return accountSummaries;
    }

    private List<MonthlySpending> buildMonthlySpending(List<TransactionWithDetails> rows) {
        Map<YearMonth, Double> totals = new TreeMap<>();

        if (rows != null) {
            for (TransactionWithDetails row : rows) {
                YearMonth month = YearMonth.from(
                        Instant.ofEpochMilli(row.date)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                );
                totals.put(month, totals.getOrDefault(month, 0.0) + row.amount);
            }
        }

        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMM yyyy", Locale.CANADA);
        List<MonthlySpending> result = new ArrayList<>();

        for (Map.Entry<YearMonth, Double> entry : totals.entrySet()) {
            result.add(new MonthlySpending(entry.getKey().format(monthFormat), entry.getValue()));
        }

        return result;
    }

    private List<CategoryTotal> buildCategoryTotals(List<TransactionWithDetails> rows) {
        Map<String, Double> totals = new HashMap<>();

        if (rows != null) {
            for (TransactionWithDetails row : rows) {
                String categoryName = safeLabel(row.categoryName, "Unknown Category");
                totals.put(categoryName, totals.getOrDefault(categoryName, 0.0) + row.amount);
            }
        }

        List<CategoryTotal> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            result.add(new CategoryTotal(entry.getKey(), entry.getValue()));
        }
        result.sort((a, b) -> Double.compare(b.getTotal(), a.getTotal()));
        return result;
    }

    private List<AccountSummary> buildAccountSummaries(List<TransactionWithDetails> rows) {
        Map<String, Double> totals = new HashMap<>();
        Map<String, Integer> counts = new HashMap<>();

        if (rows != null) {
            for (TransactionWithDetails row : rows) {
                String accountName = safeLabel(row.accountName, "Unknown Account");
                totals.put(accountName, totals.getOrDefault(accountName, 0.0) + row.amount);
                counts.put(accountName, counts.getOrDefault(accountName, 0) + 1);
            }
        }

        List<AccountSummary> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            String accountName = entry.getKey();
            result.add(new AccountSummary(
                    accountName,
                    entry.getValue(),
                    counts.getOrDefault(accountName, 0)
            ));
        }
        result.sort((a, b) -> Double.compare(b.getTotalSpent(), a.getTotalSpent()));
        return result;
    }

    private String safeLabel(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }
}
