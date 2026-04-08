package com.example.expenseeye.ui.reports;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.model.reports.AccountSummary;
import com.example.expenseeye.model.reports.CategoryTotal;
import com.example.expenseeye.model.reports.MonthlySpending;
import com.example.expenseeye.model.reports.ReportOverview;
import com.example.expenseeye.viewmodel.reports.ReportsViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CANADA);
    private StatAdapter categoryAdapter;
    private AccountSummaryAdapter accountAdapter;

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

        ReportsViewModel viewModel = new ViewModelProvider(this).get(ReportsViewModel.class);

        TextView totalSpendingText = view.findViewById(R.id.text_total_spending);
        TextView averageText = view.findViewById(R.id.text_average_spend);
        TextView countText = view.findViewById(R.id.text_transaction_count);
        TextView topCategoryText = view.findViewById(R.id.text_top_category);
        TextView topAccountText = view.findViewById(R.id.text_top_account);
        TextView peakMonthText = view.findViewById(R.id.text_peak_month);

        MonthlyTrendChartView monthlyTrendChart = view.findViewById(R.id.monthly_trend_chart);
        CategoryDonutView categoryDonutView = view.findViewById(R.id.category_donut_chart);

        RecyclerView categoryList = view.findViewById(R.id.list_category_totals);
        categoryList.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter = new StatAdapter(new ArrayList<>(), item -> {
        });
        categoryList.setAdapter(categoryAdapter);

        RecyclerView accountList = view.findViewById(R.id.list_account_summaries);
        accountList.setLayoutManager(new LinearLayoutManager(requireContext()));
        accountAdapter = new AccountSummaryAdapter(new ArrayList<>(), item -> {
        });
        accountList.setAdapter(accountAdapter);

        viewModel.getReportOverview().observe(getViewLifecycleOwner(), overview -> {
            ReportOverview safeOverview = overview == null
                    ? new ReportOverview(0.0, 0.0, 0, "No data", "No data", "No data")
                    : overview;

            totalSpendingText.setText(currency.format(safeOverview.getTotalSpending()));
            averageText.setText(currency.format(safeOverview.getAverageTransactionValue()));
            countText.setText(String.valueOf(safeOverview.getTransactionCount()));
            topCategoryText.setText(safeOverview.getTopCategory());
            topAccountText.setText(safeOverview.getTopAccount());
            peakMonthText.setText(safeOverview.getPeakMonth());
        });

        viewModel.getMonthlySpending().observe(getViewLifecycleOwner(), items -> {
            List<MonthlySpending> monthly = items == null ? new ArrayList<>() : items;
            monthlyTrendChart.setData(monthly);
        });

        viewModel.getCategoryTotals().observe(getViewLifecycleOwner(), items -> {
            List<CategoryTotal> categories = items == null ? new ArrayList<>() : items;
            categoryDonutView.setData(categories);
            categoryAdapter.replaceItems(toCategoryStatItems(categories));
        });

        viewModel.getAccountSummaries().observe(getViewLifecycleOwner(), items -> {
            List<AccountSummary> accounts = items == null ? new ArrayList<>() : items;
            accountAdapter.replaceItems(accounts);
        });
    }

    private List<StatAdapter.StatItem> toCategoryStatItems(List<CategoryTotal> source) {
        List<StatAdapter.StatItem> items = new ArrayList<>();
        double total = 0.0;
        for (CategoryTotal category : source) {
            total += category.getTotal();
        }

        for (CategoryTotal category : source) {
            int sharePercent = total <= 0
                    ? 0
                    : (int) Math.round((category.getTotal() / total) * 100d);
            items.add(new StatAdapter.StatItem(
                    category.getCategoryName(),
                    currency.format(category.getTotal()),
                    sharePercent + "% of total",
                    sharePercent
            ));
        }
        return items;
    }
}
