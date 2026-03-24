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

        TextView monthlyTotalText = view.findViewById(R.id.text_monthly_total);
        TextView monthlyCaptionText = view.findViewById(R.id.text_monthly_caption);

        RecyclerView categoryList = view.findViewById(R.id.list_category_totals);
        categoryList.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryAdapter = new StatAdapter(new ArrayList<>(), item -> {});
        categoryList.setAdapter(categoryAdapter);

        RecyclerView accountList = view.findViewById(R.id.list_account_summaries);
        accountList.setLayoutManager(new LinearLayoutManager(requireContext()));
        accountAdapter = new AccountSummaryAdapter(new ArrayList<>(), item -> {});
        accountList.setAdapter(accountAdapter);

        viewModel.getMonthlySpending().observe(getViewLifecycleOwner(), items -> {
            List<MonthlySpending> monthly = items == null ? new ArrayList<>() : items;
            monthlyTotalText.setText(currency.format(totalMonthlySpending(monthly)));

            int bucketCount = monthly.size();
            monthlyCaptionText.setText(
                    bucketCount == 1
                            ? "Across 1 month bucket"
                            : "Across " + bucketCount + " month buckets"
            );
        });

        viewModel.getCategoryTotals().observe(getViewLifecycleOwner(), items -> {
            List<CategoryTotal> categories = items == null ? new ArrayList<>() : items;
            categoryAdapter.replaceItems(toCategoryStatItems(categories));
        });

        viewModel.getAccountSummaries().observe(getViewLifecycleOwner(), items -> {
            List<AccountSummary> accounts = items == null ? new ArrayList<>() : items;
            accountAdapter.replaceItems(accounts);
        });
    }

    private double totalMonthlySpending(List<MonthlySpending> monthlyRows) {
        double total = 0.0;
        for (MonthlySpending item : monthlyRows) {
            total += item.getTotal();
        }
        return total;
    }

    private List<StatAdapter.StatItem> toCategoryStatItems(List<CategoryTotal> source) {
        List<StatAdapter.StatItem> items = new ArrayList<>();
        for (CategoryTotal category : source) {
            items.add(new StatAdapter.StatItem(
                    category.getCategoryName(),
                    currency.format(category.getTotal()),
                    category.getTotal()
            ));
        }
        return items;
    }
}
