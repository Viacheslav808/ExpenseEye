package com.example.expenseeye.ui.budget;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;
import com.example.expenseeye.model.reports.BudgetEvaluation;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {

    public interface OnBudgetActionListener {
        void onEdit(BudgetEvaluation eval);
        void onDelete(BudgetEvaluation eval);
    }

    // Status colors
    private static final int COLOR_SAFE   = Color.parseColor("#4CAF50"); // green
    private static final int COLOR_WARN   = Color.parseColor("#F59E0B"); // amber
    private static final int COLOR_DANGER = Color.parseColor("#EF4444"); // red

    private List<BudgetEvaluation> items;
    private final OnBudgetActionListener actionListener;
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.CANADA);

    public BudgetAdapter(List<BudgetEvaluation> items, OnBudgetActionListener listener) {
        this.items = items;
        this.actionListener = listener;
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

        // --- Text content ---
        holder.tvName.setText(eval.getDisplayName());
        holder.tvCategory.setText(eval.getCategoryName());
        holder.tvStatus.setText(eval.getStatusLabel());
        holder.tvSpent.setText(currency.format(eval.getSpent()) + " / " + currency.format(eval.getLimit()));
        holder.tvPercent.setText(eval.getPercentUsed() + "%");

        // Remaining line: show overage in red with minus sign
        double remaining = eval.getRemaining();
        if (remaining < 0) {
            holder.tvRemaining.setText("Over by " + currency.format(Math.abs(remaining)));
            holder.tvRemaining.setTextColor(COLOR_DANGER);
        } else {
            holder.tvRemaining.setText("Remaining: " + currency.format(remaining));
            holder.tvRemaining.setTextColor(Color.parseColor("#6B7280"));
        }

        // --- Progress bar ---
        int progress = Math.min(eval.getPercentUsed(), 100);
        holder.progressBar.setProgress(progress);

        // --- Color coding ---
        int color = eval.isOverBudget() ? COLOR_DANGER
                : eval.isNearLimit() ? COLOR_WARN
                : COLOR_SAFE;

        holder.progressBar.getProgressDrawable()
                .setColorFilter(color, PorterDuff.Mode.SRC_IN);
        holder.tvStatus.setBackgroundColor(color);
        holder.tvPercent.setTextColor(color);

        // --- Actions ---
        holder.btnEdit.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onEdit(eval);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onDelete(eval);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvStatus, tvSpent, tvPercent, tvRemaining;
        ProgressBar progressBar;
        Button btnEdit, btnDelete;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_budget_name);
            tvCategory = v.findViewById(R.id.tv_budget_category);
            tvStatus = v.findViewById(R.id.tv_budget_status);
            tvSpent = v.findViewById(R.id.tv_budget_spent);
            tvPercent = v.findViewById(R.id.tv_budget_percent);
            tvRemaining = v.findViewById(R.id.tv_budget_remaining);
            progressBar = v.findViewById(R.id.progress_budget);
            btnEdit = v.findViewById(R.id.btn_edit_budget);
            btnDelete = v.findViewById(R.id.btn_delete_budget);
        }
    }
}