package com.example.expenseeye.ui.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;
import com.example.expenseeye.repository.reports.ReportRepo;
import com.example.expenseeye.viewmodel.reports.ReportsViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CANADA);

    public ReportsFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reports, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ReportsViewModel viewModel = new ReportsViewModel(new ReportRepo());

        List<MonthlySpending> monthly = viewModel.monthlySpending();
        List<CategoryTotal> categories = viewModel.categoryTotals();
        List<AccountSummary> accounts = viewModel.accountSummaries();

        TextView monthlyTotalText = view.findViewById(R.id.text_monthly_total);
        TextView monthlyCaptionText = view.findViewById(R.id.text_monthly_caption);

        double monthlyTotal = 0.0;
        for (MonthlySpending item : monthly) {
            monthlyTotal += item.getTotal();
        }

        monthlyTotalText.setText(currency.format(monthlyTotal));
        monthlyCaptionText.setText("Across " + monthly.size() + " months of tracked expenses");

        RecyclerView monthlyList = view.findViewById(R.id.list_monthly_spending);
        RecyclerView categoryList = view.findViewById(R.id.list_category_totals);
        RecyclerView accountList = view.findViewById(R.id.list_account_summaries);

        monthlyList.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryList.setLayoutManager(new LinearLayoutManager(requireContext()));
        accountList.setLayoutManager(new LinearLayoutManager(requireContext()));

        monthlyList.setAdapter(new StatAdapter(toMonthlyStatItems(monthly)));
        categoryList.setAdapter(new StatAdapter(toCategoryStatItems(categories)));
        accountList.setAdapter(new AccountSummaryAdapter(accounts));
    }

    private List<StatAdapter.StatItem> toMonthlyStatItems(List<MonthlySpending> source) {
        List<StatAdapter.StatItem> items = new ArrayList<>();
        for (MonthlySpending month : source) {
            items.add(new StatAdapter.StatItem(month.getMonthLabel(), currency.format(month.getTotal())));
        }
        return items;
    }

    private List<StatAdapter.StatItem> toCategoryStatItems(List<CategoryTotal> source) {
        List<StatAdapter.StatItem> items = new ArrayList<>();
        for (CategoryTotal category : source) {
            items.add(new StatAdapter.StatItem(category.getCategoryName(), currency.format(category.getTotal())));
        }
        return items;
    }
}
