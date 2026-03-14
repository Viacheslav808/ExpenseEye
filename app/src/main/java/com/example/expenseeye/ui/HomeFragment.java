package com.example.expenseeye.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.data.FinanceStore;
import com.example.expenseeye.model.finance.AccountRecord;
import com.example.expenseeye.model.finance.CategoryRecord;
import com.example.expenseeye.model.finance.TransactionRecord;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private final List<TransactionListItem> transactions = new ArrayList<>();
    private final FinanceStore financeStore = FinanceStore.getInstance();
    private TransactionAdapter adapter;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.transactions_recycler);
        FloatingActionButton fab = root.findViewById(R.id.add_transaction_fab);

        adapter = new TransactionAdapter(transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        loadTransactionsFromStore();
        fab.setOnClickListener(v -> showAddTransactionDialog());

        return root;
    }

    private void loadTransactionsFromStore() {
        List<AccountRecord> accounts = financeStore.getAccounts();
        List<CategoryRecord> categories = financeStore.getCategories();
        Map<Integer, String> accountNames = new HashMap<>();
        Map<Integer, String> categoryNames = new HashMap<>();

        for (AccountRecord account : accounts) {
            accountNames.put(account.getId(), account.getName());
        }
        for (CategoryRecord category : categories) {
            categoryNames.put(category.getId(), category.getName());
        }

        transactions.clear();
        for (TransactionRecord row : financeStore.getTransactions()) {
            if (!"expense".equalsIgnoreCase(row.getType())) {
                continue;
            }
            String title = row.getTitle().isEmpty() ? categoryNames.getOrDefault(row.getCategoryId(), "Expense") : row.getTitle();
            String accountName = accountNames.getOrDefault(row.getAccountId(), "Unknown");
            String categoryName = categoryNames.getOrDefault(row.getCategoryId(), "Unknown");
            transactions.add(new TransactionListItem(title, row.getAmount(), accountName, categoryName));
        }
        adapter.notifyDataSetChanged();
    }

    private void showAddTransactionDialog() {
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_transaction, null, false);

        EditText nameInput = dialogView.findViewById(R.id.input_name);
        EditText amountInput = dialogView.findViewById(R.id.input_amount);
        Spinner accountSpinner = dialogView.findViewById(R.id.account_spinner);
        Spinner categorySpinner = dialogView.findViewById(R.id.category_spinner);
        Button saveButton = dialogView.findViewById(R.id.save_transaction);

        List<String> accountChoices = new ArrayList<>();
        for (AccountRecord account : financeStore.getAccounts()) {
            accountChoices.add(account.getName());
        }
        List<String> categoryChoices = new ArrayList<>();
        for (CategoryRecord category : financeStore.getCategories()) {
            if (!"Salary".equalsIgnoreCase(category.getName())) {
                categoryChoices.add(category.getName());
            }
        }

        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, accountChoices);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(accountAdapter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryChoices);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        saveButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String amountText = amountInput.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(amountText)) {
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountText);
            } catch (NumberFormatException e) {
                return;
            }

            String account = (String) accountSpinner.getSelectedItem();
            String category = (String) categorySpinner.getSelectedItem();

            financeStore.addExpenseTransaction(name, amount, account, category);
            loadTransactionsFromStore();
            dialog.dismiss();
        });

        dialog.show();
    }
}
