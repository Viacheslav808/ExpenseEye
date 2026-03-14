package com.example.expenseeye.ui.reports;

import android.content.Intent;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.snackbar.Snackbar;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ReportsFragment extends Fragment {

    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CANADA);
    private List<MonthlySpending> monthly = new ArrayList<>();
    private List<CategoryTotal> categories = new ArrayList<>();
    private List<AccountSummary> accounts = new ArrayList<>();
    private StatAdapter monthlyAdapter;
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

        ReportsViewModel viewModel = new ReportsViewModel(new ReportRepo());
        monthly = viewModel.monthlySpending();
        categories = viewModel.categoryTotals();
        accounts = viewModel.accountSummaries();

        TextView monthlyTotalText = view.findViewById(R.id.text_monthly_total);
        TextView monthlyCaptionText = view.findViewById(R.id.text_monthly_caption);

        monthlyTotalText.setText(currency.format(totalMonthlySpending(monthly)));
        monthlyCaptionText.setText("Across " + monthly.size() + " months of tracked expenses");

        RecyclerView monthlyList = view.findViewById(R.id.list_monthly_spending);
        RecyclerView categoryList = view.findViewById(R.id.list_category_totals);
        RecyclerView accountList = view.findViewById(R.id.list_account_summaries);

        monthlyList.setLayoutManager(new LinearLayoutManager(requireContext()));
        categoryList.setLayoutManager(new LinearLayoutManager(requireContext()));
        accountList.setLayoutManager(new LinearLayoutManager(requireContext()));

        monthlyAdapter = new StatAdapter(toMonthlyStatItems(monthly),
                item -> showMessage(view, item.label + " spending: " + item.value));
        categoryAdapter = new StatAdapter(toCategoryStatItems(categories),
                item -> showMessage(view, item.label + " total: " + item.value));
        accountAdapter = new AccountSummaryAdapter(accounts,
                item -> showMessage(view, item.getAccountName() + " net: " + currency.format(item.getNet())));

        monthlyList.setAdapter(monthlyAdapter);
        categoryList.setAdapter(categoryAdapter);
        accountList.setAdapter(accountAdapter);

        MaterialButtonToggleGroup sortGroup = view.findViewById(R.id.group_sort);
        MaterialButton highestButton = view.findViewById(R.id.button_sort_highest);
        MaterialButton shareButton = view.findViewById(R.id.button_share_report);

        highestButton.setChecked(true);

        sortGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (!isChecked) {
                return;
            }
            boolean descending = checkedId == R.id.button_sort_highest;
            applySort(descending);
        });

        shareButton.setOnClickListener(v -> shareReport());
    }

    private double totalMonthlySpending(List<MonthlySpending> monthlyRows) {
        double total = 0.0;
        for (MonthlySpending item : monthlyRows) {
            total += item.getTotal();
        }
        return total;
    }

    private void applySort(boolean descending) {
        List<MonthlySpending> sortedMonthly = new ArrayList<>(monthly);
        List<CategoryTotal> sortedCategories = new ArrayList<>(categories);
        List<AccountSummary> sortedAccounts = new ArrayList<>(accounts);

        Comparator<MonthlySpending> monthlyComparator = Comparator.comparingDouble(MonthlySpending::getTotal);
        Comparator<CategoryTotal> categoryComparator = Comparator.comparingDouble(CategoryTotal::getTotal);
        Comparator<AccountSummary> accountComparator = Comparator.comparingDouble(AccountSummary::getNet);

        if (descending) {
            monthlyComparator = monthlyComparator.reversed();
            categoryComparator = categoryComparator.reversed();
            accountComparator = accountComparator.reversed();
        }

        Collections.sort(sortedMonthly, monthlyComparator);
        Collections.sort(sortedCategories, categoryComparator);
        Collections.sort(sortedAccounts, accountComparator);

        monthlyAdapter.replaceItems(toMonthlyStatItems(sortedMonthly));
        categoryAdapter.replaceItems(toCategoryStatItems(sortedCategories));
        accountAdapter.replaceItems(sortedAccounts);
    }

    private void showMessage(View parent, String message) {
        Snackbar.make(parent, message, Snackbar.LENGTH_SHORT).show();
    }

    private void shareReport() {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, buildShareText());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, "Share financial report");
        startActivity(shareIntent);
    }

    private String buildShareText() {
        MonthlySpending maxMonth = monthly.isEmpty() ? null : Collections.max(monthly, Comparator.comparingDouble(MonthlySpending::getTotal));
        CategoryTotal maxCategory = categories.isEmpty() ? null : Collections.max(categories, Comparator.comparingDouble(CategoryTotal::getTotal));
        AccountSummary maxAccount = accounts.isEmpty() ? null : Collections.max(accounts, Comparator.comparingDouble(AccountSummary::getNet));

        String topMonth = maxMonth == null ? "n/a" : maxMonth.getMonthLabel() + " " + currency.format(maxMonth.getTotal());
        String topCategory = maxCategory == null ? "n/a" : maxCategory.getCategoryName() + " " + currency.format(maxCategory.getTotal());
        String topAccount = maxAccount == null ? "n/a" : maxAccount.getAccountName() + " net " + currency.format(maxAccount.getNet());

        return "ExpenseEyes Financial Report\n"
                + "Total spending: " + currency.format(totalMonthlySpending(monthly)) + "\n"
                + "Top month: " + topMonth + "\n"
                + "Top category: " + topCategory + "\n"
                + "Top account: " + topAccount;
    }

    private List<StatAdapter.StatItem> toMonthlyStatItems(List<MonthlySpending> source) {
        List<StatAdapter.StatItem> items = new ArrayList<>();
        for (MonthlySpending month : source) {
            items.add(new StatAdapter.StatItem(month.getMonthLabel(), currency.format(month.getTotal()), month.getTotal()));
        }
        return items;
    }

    private List<StatAdapter.StatItem> toCategoryStatItems(List<CategoryTotal> source) {
        List<StatAdapter.StatItem> items = new ArrayList<>();
        for (CategoryTotal category : source) {
            items.add(new StatAdapter.StatItem(category.getCategoryName(), currency.format(category.getTotal()), category.getTotal()));
        }
        return items;
    }
}
