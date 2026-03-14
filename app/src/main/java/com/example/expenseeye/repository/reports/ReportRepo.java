package com.example.expenseeye.repository.reports;

import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reports repository.
 *
 * For this milestone, it aggregates from an internal transaction sample set so the reports screen
 * can be demoed without changing teammates' finance/auth implementations.
 *
 * Integration point: replace sampleRows() with TransactionDao/CategoryDao/AccountDao queries.
 */
public class ReportRepo {

    public List<MonthlySpending> getMonthlySpending() {
        Map<String, Double> totals = new HashMap<>();
        DateTimeFormatter monthFormat = DateTimeFormatter.ofPattern("MMM yyyy", Locale.US);

        for (TransactionRow row : sampleRows()) {
            if (!"expense".equalsIgnoreCase(row.type)) {
                continue;
            }
            String key = row.date.withDayOfMonth(1).format(monthFormat);
            totals.put(key, totals.getOrDefault(key, 0.0) + row.amount);
        }

        List<MonthlySpending> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            result.add(new MonthlySpending(entry.getKey(), entry.getValue()));
        }
        result.sort(Comparator.comparing(MonthlySpending::getMonthLabel));
        return result;
    }

    public List<CategoryTotal> getCategoryTotals() {
        Map<String, Double> totals = new HashMap<>();

        for (TransactionRow row : sampleRows()) {
            if (!"expense".equalsIgnoreCase(row.type)) {
                continue;
            }
            totals.put(row.category, totals.getOrDefault(row.category, 0.0) + row.amount);
        }

        List<CategoryTotal> result = new ArrayList<>();
        for (Map.Entry<String, Double> entry : totals.entrySet()) {
            result.add(new CategoryTotal(entry.getKey(), entry.getValue()));
        }
        result.sort((a, b) -> Double.compare(b.getTotal(), a.getTotal()));
        return result;
    }

    public List<AccountSummary> getAccountSummaries() {
        Map<String, Double> income = new HashMap<>();
        Map<String, Double> expense = new HashMap<>();

        for (TransactionRow row : sampleRows()) {
            if ("income".equalsIgnoreCase(row.type)) {
                income.put(row.account, income.getOrDefault(row.account, 0.0) + row.amount);
            } else {
                expense.put(row.account, expense.getOrDefault(row.account, 0.0) + row.amount);
            }
        }

        List<AccountSummary> result = new ArrayList<>();
        for (String account : allAccounts(income, expense)) {
            result.add(new AccountSummary(
                    account,
                    income.getOrDefault(account, 0.0),
                    expense.getOrDefault(account, 0.0)
            ));
        }
        result.sort((a, b) -> Double.compare(b.getNet(), a.getNet()));
        return result;
    }

    private List<String> allAccounts(Map<String, Double> income, Map<String, Double> expense) {
        List<String> accounts = new ArrayList<>(income.keySet());
        for (String account : expense.keySet()) {
            if (!accounts.contains(account)) {
                accounts.add(account);
            }
        }
        return accounts;
    }

    private List<TransactionRow> sampleRows() {
        List<TransactionRow> rows = new ArrayList<>();

        rows.add(new TransactionRow("income", 3300, "Salary", "Chequing", LocalDate.of(2026, 1, 2)));
        rows.add(new TransactionRow("expense", 930, "Rent", "Chequing", LocalDate.of(2026, 1, 5)));
        rows.add(new TransactionRow("expense", 260, "Groceries", "Credit Card", LocalDate.of(2026, 1, 10)));
        rows.add(new TransactionRow("expense", 120, "Transport", "Credit Card", LocalDate.of(2026, 1, 16)));

        rows.add(new TransactionRow("income", 3400, "Salary", "Chequing", LocalDate.of(2026, 2, 2)));
        rows.add(new TransactionRow("expense", 930, "Rent", "Chequing", LocalDate.of(2026, 2, 5)));
        rows.add(new TransactionRow("expense", 290, "Groceries", "Credit Card", LocalDate.of(2026, 2, 10)));
        rows.add(new TransactionRow("expense", 220, "Dining", "Credit Card", LocalDate.of(2026, 2, 18)));
        rows.add(new TransactionRow("expense", 145, "Utilities", "Chequing", LocalDate.of(2026, 2, 21)));

        rows.add(new TransactionRow("income", 3400, "Salary", "Chequing", LocalDate.of(2026, 3, 2)));
        rows.add(new TransactionRow("expense", 930, "Rent", "Chequing", LocalDate.of(2026, 3, 5)));
        rows.add(new TransactionRow("expense", 310, "Groceries", "Credit Card", LocalDate.of(2026, 3, 11)));
        rows.add(new TransactionRow("expense", 160, "Transport", "Credit Card", LocalDate.of(2026, 3, 15)));
        rows.add(new TransactionRow("expense", 205, "Dining", "Credit Card", LocalDate.of(2026, 3, 23)));

        return rows;
    }

    private static class TransactionRow {
        final String type;
        final double amount;
        final String category;
        final String account;
        final LocalDate date;

        TransactionRow(String type, double amount, String category, String account, LocalDate date) {
            this.type = type;
            this.amount = amount;
            this.category = category;
            this.account = account;
            this.date = date;
        }
    }
}
