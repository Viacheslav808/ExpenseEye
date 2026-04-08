package com.example.expenseeye.repository.reports;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import com.example.expenseeye.data.model.TransactionWithDetails;
import com.example.expenseeye.data.repository.FinanceRepo;
import com.example.expenseeye.data.repository.FinanceRepoProvider;
import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;
import com.example.expenseeye.model.reports.ReportOverview;

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
    private final LiveData<ReportOverview> reportOverview;

    public ReportRepo(Context context) {
        FinanceRepo financeRepo = FinanceRepoProvider.get(context.getApplicationContext());

        LiveData<List<TransactionWithDetails>> transactions = financeRepo.getTransactionsWithDetails();

        monthlySpending = Transformations.map(transactions, this::buildMonthlySpending);
        categoryTotals = Transformations.map(transactions, this::buildCategoryTotals);
        accountSummaries = Transformations.map(transactions, this::buildAccountSummaries);
        reportOverview = Transformations.map(transactions, this::buildOverview);
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

    public LiveData<ReportOverview> getReportOverview() {
        return reportOverview;
    }

    private List<MonthlySpending> buildMonthlySpending(List<TransactionWithDetails> rows) {
        Map<YearMonth, Double> totals = new TreeMap<>();

        if (rows != null) {
            for (TransactionWithDetails row : rows) {
                YearMonth month = toYearMonth(row.date);
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

    private ReportOverview buildOverview(List<TransactionWithDetails> rows) {
        if (rows == null || rows.isEmpty()) {
            return new ReportOverview(0.0, 0.0, 0, "No data", "No data", "No data");
        }

        double totalSpending = 0.0;

        Map<String, Double> categoryTotals = new HashMap<>();
        Map<String, Double> accountTotals = new HashMap<>();
        Map<YearMonth, Double> monthTotals = new HashMap<>();

        for (TransactionWithDetails row : rows) {
            totalSpending += row.amount;

            String categoryName = safeLabel(row.categoryName, "Unknown Category");
            categoryTotals.put(categoryName, categoryTotals.getOrDefault(categoryName, 0.0) + row.amount);

            String accountName = safeLabel(row.accountName, "Unknown Account");
            accountTotals.put(accountName, accountTotals.getOrDefault(accountName, 0.0) + row.amount);

            YearMonth month = toYearMonth(row.date);
            monthTotals.put(month, monthTotals.getOrDefault(month, 0.0) + row.amount);
        }

        return new ReportOverview(
                totalSpending,
                totalSpending / rows.size(),
                rows.size(),
                findTopLabel(categoryTotals, "No data"),
                findTopLabel(accountTotals, "No data"),
                formatPeakMonth(monthTotals)
        );
    }

    private YearMonth toYearMonth(long epochMillis) {
        return YearMonth.from(
                Instant.ofEpochMilli(epochMillis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
        );
    }

    private String findTopLabel(Map<String, Double> totals, String fallback) {
        String topLabel = fallback;
        double max = Double.MIN_VALUE;

        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                topLabel = entry.getKey();
            }
        }

        return topLabel;
    }

    private String formatPeakMonth(Map<YearMonth, Double> totals) {
        YearMonth peakMonth = null;
        double max = Double.MIN_VALUE;

        for (Map.Entry<YearMonth, Double> entry : totals.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                peakMonth = entry.getKey();
            }
        }

        if (peakMonth == null) {
            return "No data";
        }

        return peakMonth.format(DateTimeFormatter.ofPattern("MMM yyyy", Locale.CANADA));
    }

    private String safeLabel(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value;
    }
}
