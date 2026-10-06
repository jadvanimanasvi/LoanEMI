package com.loanemi.calculator.emi.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.Models.HistoryItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int count);
    }

    private final Context context;
    private final List<HistoryItem> historyList;
    private final List<Long> selectedIds = new ArrayList<>();
    private boolean selectionMode = false;
    private OnSelectionChangedListener selectionChangedListener;

    public HistoryAdapter(Context context, List<HistoryItem> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    public void setSelectionMode(boolean enabled) {

        selectionMode = enabled;

        if (!enabled) {
            selectedIds.clear();
        }

        notifyDataSetChanged();

        notifySelectionChanged();
    }

    public boolean isSelectionMode() {
        return selectionMode;
    }

    public void toggleSelection(long id) {

        if (selectedIds.contains(id)) {
            selectedIds.remove(id);
        } else {
            selectedIds.add(id);
        }

        notifyDataSetChanged();
        notifySelectionChanged();
    }

    public List<Long> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }

    public int getSelectedCount() {
        return selectedIds.size();
    }

    private void notifySelectionChanged() {

        if (selectionChangedListener != null) {
            selectionChangedListener.onSelectionChanged(selectedIds.size());
        }
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false);

        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {

        HistoryItem item = historyList.get(position);

        holder.tvLoanType.setText(item.getLoanType());
        holder.tvDate.setText(item.getDate());

        holder.tvInterest.setText(formatInterest(item.getInterestRate()));

        holder.tvDuration.setText(item.getDuration());

        holder.tvAmount.setText(formatAmount(item.getLoanAmount(), item.getCurrencySymbol()));

        // Calculator icon
        setLoanIcon(holder.ivHistoryIcon, item.getLoanType());

        // Check icon
        if (selectionMode) {

            holder.ivHistoryCheck.setVisibility(View.VISIBLE);

            if (selectedIds.contains(item.getId())) {

                holder.ivHistoryCheck.setImageResource(R.drawable.ic_check_selected);

            } else {

                holder.ivHistoryCheck.setImageResource(R.drawable.ic_check_unselected);
            }

        } else {

            holder.ivHistoryCheck.setVisibility(View.GONE);
        }

        // Item click
        holder.itemView.setOnClickListener(v -> {

            if (selectionMode) {

                toggleSelection(item.getId());

            } else {

                // Normal history item click.
                // You can open a result/detail screen here later.
            }
        });

        // Long press starts selection
        holder.itemView.setOnLongClickListener(v -> {

            if (!selectionMode) {

                selectionMode = true;

                selectedIds.clear();
                selectedIds.add(item.getId());

                notifyDataSetChanged();
                notifySelectionChanged();

                return true;
            }

            return false;
        });
    }

    private void setLoanIcon(ImageView imageView, String loanType) {

        if (loanType == null) {
            imageView.setImageResource(R.drawable.personal_ic);
            imageView.setBackgroundResource(R.drawable.bg_icon_blue);
            return;
        }

        String type = loanType.toLowerCase(Locale.US);

        if (type.contains("personal")) {

            imageView.setImageResource(R.drawable.personal_ic);
            imageView.setBackgroundResource(R.drawable.bg_icon_blue);

        } else if (type.contains("business")) {

            imageView.setImageResource(R.drawable.business_ic);
            imageView.setBackgroundResource(R.drawable.bg_icon_purple);

        } else if (type.contains("auto") || type.contains("car")) {

            imageView.setImageResource(R.drawable.ic_car);
            imageView.setBackgroundResource(R.drawable.bg_icon_pink);

        } else if (type.contains("home")) {

            imageView.setImageResource(R.drawable.home_ic);
            imageView.setBackgroundResource(R.drawable.bg_icon_orange);

        } else if (type.contains("school") || type.contains("student")) {

            imageView.setImageResource(R.drawable.school_ic);
            imageView.setBackgroundResource(R.drawable.bg_icon_teal);

        } else {

            imageView.setImageResource(R.drawable.personal_ic);
            imageView.setBackgroundResource(R.drawable.bg_icon_blue);
        }
    }
    private String formatInterest(double interest) {

        if (interest == (long) interest) {
            return String.format(Locale.US, "%.0f%%", interest);
        }

        return String.format(Locale.US, "%.2f%%", interest);
    }

    private String formatAmount(double amount, String symbol) {

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(0);

        return numberFormat.format(amount) + symbol;
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public void updateList(List<HistoryItem> newList) {

        historyList.clear();
        historyList.addAll(newList);

        selectedIds.clear();

        notifyDataSetChanged();
        notifySelectionChanged();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        ImageView ivHistoryIcon;
        ImageView ivHistoryCheck;
        TextView tvLoanType;
        TextView tvDate;
        TextView tvInterest;
        TextView tvDuration;
        TextView tvAmount;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            ivHistoryIcon = itemView.findViewById(R.id.ivHistoryIcon);

            ivHistoryCheck = itemView.findViewById(R.id.ivHistoryCheck);

            tvLoanType = itemView.findViewById(R.id.tvHistoryLoanType);

            tvDate = itemView.findViewById(R.id.tvHistoryDate);

            tvInterest = itemView.findViewById(R.id.tvHistoryInterest);

            tvDuration = itemView.findViewById(R.id.tvHistoryDuration);

            tvAmount = itemView.findViewById(R.id.tvHistoryAmount);
        }
    }
}