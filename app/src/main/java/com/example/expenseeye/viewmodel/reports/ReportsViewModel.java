package com.example.expenseeye.viewmodel.reports;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;
import com.example.expenseeye.model.reports.ReportOverview;
import com.example.expenseeye.repository.reports.ReportRepo;

import java.util.List;

public class ReportsViewModel extends AndroidViewModel {

    private final LiveData<List<MonthlySpending>> monthlySpending;
    private final LiveData<List<CategoryTotal>> categoryTotals;
    private final LiveData<List<AccountSummary>> accountSummaries;
    private final LiveData<ReportOverview> reportOverview;

    public ReportsViewModel(@NonNull Application application) {
        super(application);

        ReportRepo reportRepo = new ReportRepo(application);
        monthlySpending = reportRepo.getMonthlySpending();
        categoryTotals = reportRepo.getCategoryTotals();
        accountSummaries = reportRepo.getAccountSummaries();
        reportOverview = reportRepo.getReportOverview();
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
}
