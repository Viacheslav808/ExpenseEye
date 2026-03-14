package com.example.expenseeye.ui.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
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
    private List<MonthlySpending> monthly = new ArrayList<>();
    private StatAdapter categoryAdapter;

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

        TextView monthlyTotalText = view.findViewById(R.id.text_monthly_total);
        TextView monthlyCaptionText = view.findViewById(R.id.text_monthly_caption);

        RecyclerView categoryList = view.findViewById(R.id.list_category_totals);
        categoryList.setLayoutManager(new LinearLayoutManager(requireContext()));

        categoryAdapter = new StatAdapter(new ArrayList<>(), item -> {});
        categoryList.setAdapter(categoryAdapter);

        LifecycleOwner owner = getViewLifecycleOwner();
        viewModel.monthlySpending().observe(owner, items -> {
            monthly = items == null ? new ArrayList<>() : new ArrayList<>(items);
            monthlyTotalText.setText(currency.format(totalMonthlySpending(monthly)));
            monthlyCaptionText.setText("From " + countTrackedExpenses(monthly) + " month buckets");
        });

        viewModel.categoryTotals().observe(owner, items -> {
            List<CategoryTotal> categories = items == null ? new ArrayList<>() : new ArrayList<>(items);
            categoryAdapter.replaceItems(toCategoryStatItems(categories));
        });
    }

    private double totalMonthlySpending(List<MonthlySpending> monthlyRows) {
        double total = 0.0;
        for (MonthlySpending item : monthlyRows) {
            total += item.getTotal();
        }
        return total;
    }

    private int countTrackedExpenses(List<MonthlySpending> monthlyRows) {
        return monthlyRows.size();
    }

    private List<StatAdapter.StatItem> toCategoryStatItems(List<CategoryTotal> source) {
        List<StatAdapter.StatItem> items = new ArrayList<>();
        for (CategoryTotal category : source) {
            items.add(new StatAdapter.StatItem(category.getCategoryName(), currency.format(category.getTotal()), category.getTotal()));
        }
        return items;
    }
}
