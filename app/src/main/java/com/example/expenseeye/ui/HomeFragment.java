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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String[] ACCOUNT_CHOICES = {"Cash", "Bank Account", "Credit Card"};
    private static final String[] CATEGORY_CHOICES = {"Food", "Transport", "Shopping", "Bills", "Entertainment"};

    private final List<TransactionListItem> transactions = new ArrayList<>();
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

        loadInitialTransactions();
        fab.setOnClickListener(v -> showAddTransactionDialog());

        return root;
    }

    private void loadInitialTransactions() {
        if (!transactions.isEmpty()) {
            return;
        }
        transactions.add(new TransactionListItem("test4", 4.00, "Cash", "Entertainment"));
        transactions.add(new TransactionListItem("test3", 3.00, "Credit Card", "Food"));
        transactions.add(new TransactionListItem("test2", 2.00, "Bank Account", "Food"));
        transactions.add(new TransactionListItem("test", 1.00, "Cash", "Food"));
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

        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, ACCOUNT_CHOICES);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(accountAdapter);

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, CATEGORY_CHOICES);
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

            transactions.add(0, new TransactionListItem(name, amount, account, category));
            adapter.notifyItemInserted(0);
            dialog.dismiss();
        });

        dialog.show();
    }
}
