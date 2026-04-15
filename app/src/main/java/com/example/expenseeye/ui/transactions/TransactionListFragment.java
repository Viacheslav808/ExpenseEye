package com.example.expenseeye.ui.transactions;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.data.model.Transaction;
import com.example.expenseeye.data.model.TransactionWithDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class TransactionListFragment extends Fragment {

    private static final String TAG = "TransactionListFragment";

    private TransactionViewModel viewModel;
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private ProgressBar progressBar;
    private TextView tvEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_transaction_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(new TransactionAdapter.OnTransactionActionListener() {

            @Override
            public void onDelete(TransactionWithDetails tx) {

                Log.d(TAG, "Deleting: " + tx.description);

                Transaction deleteTx = new Transaction(
                        tx.amount,
                        tx.description,
                        tx.accountId,
                        tx.categoryId,
                        tx.date
                );

                deleteTx.setId(tx.id);

                if (viewModel != null) {
                    viewModel.deleteTransactionAsync(deleteTx);
                }
            }

            @Override
            public void onEdit(TransactionWithDetails tx) {
                openEditDialog(tx);
            }
        });

        recyclerView.setAdapter(adapter);

        progressBar = view.findViewById(R.id.progress_loading);
        progressBar.setVisibility(View.VISIBLE);

        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvEmptyState.setVisibility(View.GONE);

        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        viewModel.getTransactionsWithDetails().observe(getViewLifecycleOwner(), transactions -> {

            if (transactions == null || transactions.isEmpty()) {
                tvEmptyState.setVisibility(View.VISIBLE);
            } else {
                tvEmptyState.setVisibility(View.GONE);
            }

            adapter.updateList(transactions);
            progressBar.setVisibility(View.GONE);
        });

        FloatingActionButton fab = view.findViewById(R.id.fab_add_transaction);

        fab.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_fragment_container, new AddTransactionFragment())
                        .addToBackStack(null)
                        .commit()
        );

        return view;
    }

    /**
     * =====================
     * EDIT DIALOG (FIXED UI)
     * =====================
     */
    private void openEditDialog(TransactionWithDetails tx) {

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.fragment_add_transaction, null);

        EditText etDesc = dialogView.findViewById(R.id.et_tx_description);
        EditText etAmount = dialogView.findViewById(R.id.et_tx_amount);

        // Pre-fill values
        etDesc.setText(tx.description);
        etAmount.setText(String.valueOf(tx.amount));

        // =====================
        // YOUR REQUIRED FIX (APPLIED)
        // =====================
        dialogView.setBackgroundColor(Color.WHITE);

        etDesc.setTextColor(Color.BLACK);
        etAmount.setTextColor(Color.BLACK);

        // optional but keeps UI readable
        etDesc.setHintTextColor(Color.DKGRAY);
        etAmount.setHintTextColor(Color.DKGRAY);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Edit Transaction")
                .setView(dialogView)
                .setPositiveButton("Update", null)
                .setNegativeButton("Cancel", (d, which) -> d.dismiss())
                .create();

        dialog.setOnShowListener(d -> {

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

                String desc = etDesc.getText().toString().trim();
                String amountStr = etAmount.getText().toString().trim();

                if (desc.isEmpty() || amountStr.isEmpty()) {
                    return;
                }

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (Exception e) {
                    Log.e(TAG, "Invalid amount", e);
                    return;
                }

                Transaction updated = new Transaction(
                        amount,
                        desc,
                        tx.accountId,
                        tx.categoryId,
                        tx.date
                );

                updated.setId(tx.id);

                if (viewModel != null) {
                    viewModel.updateTransactionAsync(updated);
                }

                dialog.dismiss();
            });
        });

        dialog.show();
    }
}