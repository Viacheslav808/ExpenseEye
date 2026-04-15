package com.example.expenseeye.ui.transactions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.data.model.TransactionWithDetails;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    /**
     * Single responsibility listener:
     * Adapter only sends actions, UI logic stays in Fragment
     */
    public interface OnTransactionActionListener {
        void onDelete(TransactionWithDetails transaction);
        void onEdit(TransactionWithDetails transaction);
    }

    private final List<TransactionWithDetails> transactions = new ArrayList<>();
    private final OnTransactionActionListener listener;

    public TransactionAdapter(OnTransactionActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TransactionWithDetails tx = transactions.get(position);

        holder.title.setText(tx.description);
        holder.amount.setText(String.format("$%.2f", tx.amount));
        holder.details.setText(tx.accountName + " • " + tx.categoryName);

        // DELETE ACTION
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(tx);
            }
        });

        // =========================
        // EDIT ACTION
        // =========================
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(tx);
            }
        });

        // Optional UX: long press also edits
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onEdit(tx);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    /**
     * Updates RecyclerView data
     */
    public void updateList(List<TransactionWithDetails> newList) {
        transactions.clear();
        if (newList != null) {
            transactions.addAll(newList);
        }
        notifyDataSetChanged();
    }

    // VIEW HOLDER
    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView amount;
        TextView details;
        View btnEdit;
        View btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tx_title);
            amount = itemView.findViewById(R.id.tx_amount);
            details = itemView.findViewById(R.id.tx_details);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}