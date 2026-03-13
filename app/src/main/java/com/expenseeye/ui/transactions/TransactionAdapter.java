package com.expenseeye.ui.transactions;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.expenseeye.R;
import com.expenseeye.data.model.TransactionWithDetails;

import java.util.ArrayList;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    public interface OnDeleteClickListener {
        void onDelete(TransactionWithDetails transaction);
    }

    private final List<TransactionWithDetails> transactions = new ArrayList<>();
    private final OnDeleteClickListener deleteListener;

    public TransactionAdapter(OnDeleteClickListener listener) {
        this.deleteListener = listener;
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

        holder.itemView.setOnLongClickListener(v -> {

            if (deleteListener != null) {
                deleteListener.onDelete(tx);
            }

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public void updateList(List<TransactionWithDetails> newList) {

        transactions.clear();

        if (newList != null) {
            transactions.addAll(newList);
        }

        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView amount;
        TextView details;

        ViewHolder(@NonNull View itemView) {

            super(itemView);

            title = itemView.findViewById(R.id.tx_title);
            amount = itemView.findViewById(R.id.tx_amount);
            details = itemView.findViewById(R.id.tx_details);
        }
    }
}