package com.example.expenseeye.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private final List<TransactionListItem> items;

    public TransactionAdapter(List<TransactionListItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        TransactionListItem item = items.get(position);
        holder.name.setText(item.getName());
        holder.amount.setText(String.format(Locale.US, "$%.2f", item.getAmount()));
        holder.subtitle.setText(item.getAccount() + " - " + item.getCategory());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class TransactionViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView amount;
        private final TextView subtitle;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.transaction_name);
            amount = itemView.findViewById(R.id.transaction_amount);
            subtitle = itemView.findViewById(R.id.transaction_subtitle);
        }
    }
}
