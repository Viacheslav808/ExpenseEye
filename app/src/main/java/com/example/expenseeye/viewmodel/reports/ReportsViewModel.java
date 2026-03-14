package com.example.expenseeye.viewmodel.reports;

import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;
import com.example.expenseeye.repository.reports.ReportRepo;

import java.util.List;

public class ReportsViewModel {

    private final ReportRepo reportRepo;

    public ReportsViewModel(ReportRepo reportRepo) {
        this.reportRepo = reportRepo;
    }

    public List<MonthlySpending> monthlySpending() {
        return reportRepo.getMonthlySpending();
    }

    public List<CategoryTotal> categoryTotals() {
        return reportRepo.getCategoryTotals();
    }

    public List<AccountSummary> accountSummaries() {
        return reportRepo.getAccountSummaries();
    }
}
