package com.example.expenseeye.ui.reports;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.expenseeye.R;

import java.util.List;

public class StatAdapter extends RecyclerView.Adapter<StatAdapter.StatViewHolder> {

    public static class StatItem {
        public final String label;
        public final String value;

        public StatItem(String label, String value) {
            this.label = label;
            this.value = value;
        }
    }

    private final List<StatItem> items;

    public StatAdapter(List<StatItem> items) {
        this.items = items;
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StatViewHolder extends RecyclerView.ViewHolder {
        final TextView labelText;
        final TextView valueText;

        StatViewHolder(@NonNull View itemView) {
            super(itemView);
            labelText = itemView.findViewById(R.id.text_label);
            valueText = itemView.findViewById(R.id.text_value);
        }
    }
}
