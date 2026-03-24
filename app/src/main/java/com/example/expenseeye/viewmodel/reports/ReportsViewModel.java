package com.example.expenseeye.viewmodel.reports;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;
import com.example.expenseeye.repository.reports.ReportRepo;

import java.util.List;

public class ReportsViewModel {

    private final MutableLiveData<List<MonthlySpending>> monthlySpending = new MutableLiveData<>();
    private final MutableLiveData<List<CategoryTotal>> categoryTotals = new MutableLiveData<>();
    private final MutableLiveData<List<AccountSummary>> accountSummaries = new MutableLiveData<>();

    public ReportsViewModel(ReportRepo reportRepo) {
        monthlySpending.setValue(reportRepo.getMonthlySpending());
        categoryTotals.setValue(reportRepo.getCategoryTotals());
        accountSummaries.setValue(reportRepo.getAccountSummaries());
    }

    public LiveData<List<MonthlySpending>> monthlySpending() {
        return monthlySpending;
    }

    public LiveData<List<CategoryTotal>> categoryTotals() {
        return categoryTotals;
    }

    public LiveData<List<AccountSummary>> accountSummaries() {
        return accountSummaries;
    }
}
