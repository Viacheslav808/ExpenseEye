package com.example.expenseeye.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;

import java.util.ArrayList;
import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.StatViewHolder> {

    public interface OnStatClickListener {
        void onStatClick(StatItem item);
    }

    public static class StatItem {
        public final String label;
        public final String value;
        public final String shareText;
        public final int sharePercent;

        public StatItem(String label, String value, String shareText, int sharePercent) {
            this.label = label;
            this.value = value;
            this.shareText = shareText;
            this.sharePercent = sharePercent;
        }
    }

    private final List<StatItem> items = new ArrayList<>();
    private final OnStatClickListener onStatClickListener;

    public StatAdapter(List<StatItem> items, OnStatClickListener onStatClickListener) {
        this.items.addAll(items);
        this.onStatClickListener = onStatClickListener;
    }

    public void replaceItems(List<StatItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report_stat, parent, false);
        return new StatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatViewHolder holder, int position) {
        StatItem item = items.get(position);
        holder.labelText.setText(item.label);
        holder.valueText.setText(item.value);
        holder.shareText.setText(item.shareText);
        holder.shareBar.setProgress(item.sharePercent);
        holder.itemView.setOnClickListener(v -> onStatClickListener.onStatClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StatViewHolder extends RecyclerView.ViewHolder {
        final TextView labelText;
        final TextView valueText;
        final TextView shareText;
        final ProgressBar shareBar;

        StatViewHolder(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.text_label);
            valueText = itemView.findViewById(R.id.text_value);
            shareText = itemView.findViewById(R.id.text_share);
            shareBar = itemView.findViewById(R.id.progress_share);
        }
    }
}
