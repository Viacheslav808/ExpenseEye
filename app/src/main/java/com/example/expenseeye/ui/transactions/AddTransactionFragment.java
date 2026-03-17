package com.example.expenseeye.ui.transactions;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;

import com.example.expenseeye.R;
import com.example.expenseeye.data.model.Account;
import com.example.expenseeye.data.model.Category;
import com.example.expenseeye.data.model.Transaction;

import java.util.ArrayList;
import java.util.List;

public class AddTransactionFragment extends Fragment {

    private static final String TAG = "AddTransactionFragment";

    private TransactionViewModel viewModel;
    private EditText etDescription, etAmount;
    private Button btnSave;
    private Spinner spAccount;
    private Spinner spCategory;

    private List<Account> accountList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_transaction, container, false);

        etDescription = view.findViewById(R.id.et_tx_description);
        etAmount = view.findViewById(R.id.et_tx_amount);
        spAccount = view.findViewById(R.id.sp_account);
        spCategory = view.findViewById(R.id.sp_category);
        btnSave = view.findViewById(R.id.btn_save_transaction);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        setupSpinners();

        btnSave.setOnClickListener(v -> saveTransaction());

        return view;
    }

    /**
     * Populate account and category dropdowns from database
     */
    private void setupSpinners() {

        // Observe accounts from database
        viewModel.getAllAccounts().observe(getViewLifecycleOwner(), new Observer<List<Account>>() {
            @Override
            public void onChanged(List<Account> accounts) {
                if (accounts != null && !accounts.isEmpty()) {
                    accountList = accounts;
                    ArrayAdapter<Account> accountAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            accountList
                    );
                    accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spAccount.setAdapter(accountAdapter);
                }
            }
        });

        // Observe categories from database
        viewModel.getAllCategories().observe(getViewLifecycleOwner(), new Observer<List<Category>>() {
            @Override
            public void onChanged(List<Category> categories) {
                if (categories != null && !categories.isEmpty()) {
                    categoryList = categories;
                    ArrayAdapter<Category> categoryAdapter = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            categoryList
                    );
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCategory.setAdapter(categoryAdapter);
                }
            }
        });
    }

    /**
     * Save transaction to database using actual Account/Category IDs
     */
    private void saveTransaction() {

        String description = etDescription.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (TextUtils.isEmpty(description) || TextUtils.isEmpty(amountStr)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Invalid amount entered: " + amountStr, e);
            return;
        }

        // Get selected Account & Category objects
        int accountPosition = spAccount.getSelectedItemPosition();
        int categoryPosition = spCategory.getSelectedItemPosition();

        if (accountPosition < 0 || accountPosition >= accountList.size()
                || categoryPosition < 0 || categoryPosition >= categoryList.size()) {
            Toast.makeText(getContext(), "Please select valid account and category", Toast.LENGTH_SHORT).show();
            return;
        }

        Account selectedAccount = accountList.get(accountPosition);
        Category selectedCategory = categoryList.get(categoryPosition);

        long date = System.currentTimeMillis();

        Transaction transaction = new Transaction(
                amount,
                description,
                selectedAccount.getId(),
                selectedCategory.getId(),
                date
        );

        try {
            viewModel.insertTransactionAsync(transaction);

            Toast.makeText(getContext(), "Transaction added", Toast.LENGTH_SHORT).show();

            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error saving transaction", e);
            Toast.makeText(getContext(), "Failed to save transaction", Toast.LENGTH_SHORT).show();
        }
    }
}