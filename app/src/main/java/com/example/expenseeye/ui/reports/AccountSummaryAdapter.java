package com.example.expenseeye.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.model.reports.AccountSummary;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AccountSummaryAdapter extends RecyclerView.Adapter<AccountSummaryAdapter.AccountSummaryViewHolder> {

    public interface OnAccountClickListener {
        void onAccountClick(AccountSummary accountSummary);
    }

    private final List<AccountSummary> items = new ArrayList<>();
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CANADA);
    private final OnAccountClickListener onAccountClickListener;

    public AccountSummaryAdapter(List<AccountSummary> items, OnAccountClickListener onAccountClickListener) {
        this.items.addAll(items);
        this.onAccountClickListener = onAccountClickListener;
    }

    public void replaceItems(List<AccountSummary> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AccountSummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_account_summary, parent, false);
        return new AccountSummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AccountSummaryViewHolder holder, int position) {
        AccountSummary item = items.get(position);
        holder.accountNameText.setText(item.getAccountName());
        holder.incomeText.setText("Spent: " + currency.format(item.getTotalSpent()));
        holder.expenseText.setText("Transactions: " + item.getTransactionCount());
        holder.netText.setText("Average: " + currency.format(item.getAverageAmount()));
        holder.itemView.setOnClickListener(v -> onAccountClickListener.onAccountClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class AccountSummaryViewHolder extends RecyclerView.ViewHolder {
        final TextView accountNameText;
        final TextView incomeText;
        final TextView expenseText;
        final TextView netText;

        AccountSummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            accountNameText = itemView.findViewById(R.id.text_account_name);
            incomeText = itemView.findViewById(R.id.text_income);
            expenseText = itemView.findViewById(R.id.text_expense);
            netText = itemView.findViewById(R.id.text_net);
        }
    }
}
