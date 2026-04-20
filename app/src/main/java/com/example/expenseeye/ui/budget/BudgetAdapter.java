package com.example.expenseeye.ui.budget;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.data.model.Budget;
import com.example.expenseeye.model.reports.BudgetEvaluation;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {

    public interface OnDeleteListener { void onDelete(BudgetEvaluation eval); }

    private List<BudgetEvaluation> items;
    private final OnDeleteListener deleteListener;
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CANADA);

    public BudgetAdapter(List<BudgetEvaluation> items, OnDeleteListener listener) {
        this.items = items;
        this.deleteListener = listener;
    }

    public void replaceItems(List<BudgetEvaluation> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BudgetEvaluation eval = items.get(position);

        holder.tvName.setText(eval.getDisplayName());
        holder.tvCategory.setText(eval.getCategoryName());
        holder.tvStatus.setText(eval.getStatusLabel());
        holder.tvSpent.setText(currency.format(eval.getSpent()) + " / " + currency.format(eval.getLimit()));
        holder.tvRemaining.setText("Remaining: " + currency.format(eval.getRemaining()));

        int progress = (int) Math.min(eval.getUsageRatio() * 100, 100);
        holder.progressBar.setProgress(progress);

        int color = eval.isOverBudget() ? Color.RED
                : eval.getUsageRatio() >= 0.9 ? Color.parseColor("#FF9800")
                : Color.parseColor("#4CAF50");
        holder.tvStatus.setTextColor(color);

        // Long-press to delete
        holder.itemView.setOnLongClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(eval);
            return true;
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvStatus, tvSpent, tvRemaining;
        ProgressBar progressBar;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_budget_name);
            tvCategory = v.findViewById(R.id.tv_budget_category);
            tvStatus = v.findViewById(R.id.tv_budget_status);
            tvSpent = v.findViewById(R.id.tv_budget_spent);
            tvRemaining = v.findViewById(R.id.tv_budget_remaining);
            progressBar = v.findViewById(R.id.progress_budget);
        }
    }
}