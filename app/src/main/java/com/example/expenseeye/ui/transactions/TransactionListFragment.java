package com.example.expenseeye.ui.transactions;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.data.model.TransactionWithDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

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

        // --- RecyclerView setup ---
        recyclerView = view.findViewById(R.id.recycler_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TransactionAdapter(tx -> {
            Log.d(TAG, "Deleting transaction: " + tx.description);

            if (viewModel != null) {

                // convert to basic Transaction for delete
                com.example.expenseeye.data.model.Transaction deleteTx =
                        new com.example.expenseeye.data.model.Transaction(
                                tx.amount,
                                tx.description,
                                0,
                                0,
                                tx.date
                        );

                deleteTx.setId(tx.id);

                viewModel.deleteTransactionAsync(deleteTx);
            }
        });

        recyclerView.setAdapter(adapter);

        // --- Progress bar ---
        progressBar = view.findViewById(R.id.progress_loading);
        progressBar.setVisibility(View.VISIBLE);

        // --- Empty state ---
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        tvEmptyState.setVisibility(View.GONE);

        // --- ViewModel ---
        viewModel = new ViewModelProvider(this).get(TransactionViewModel.class);

        viewModel.getTransactionsWithDetails().observe(getViewLifecycleOwner(), transactions -> {

            if (transactions == null || transactions.isEmpty()) {

                Log.d(TAG, "No transactions found");

                tvEmptyState.setVisibility(View.VISIBLE);

            } else {

                Log.d(TAG, "Transactions loaded: " + transactions.size());

                for (TransactionWithDetails tx : transactions) {
                    Log.d(TAG,
                            tx.description + " | $" + tx.amount +
                                    " | " + tx.accountName +
                                    " | " + tx.categoryName
                    );
                }

                tvEmptyState.setVisibility(View.GONE);
            }

            adapter.updateList(transactions);

            progressBar.setVisibility(View.GONE);
        });

        // --- FAB ---
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
}