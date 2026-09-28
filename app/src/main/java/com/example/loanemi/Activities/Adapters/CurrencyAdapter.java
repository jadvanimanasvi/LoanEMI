package com.example.loanemi.Activities.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loanemi.Activities.Models.CurrencyItem;
import com.example.loanemi.Activities.utils.CheckableLinearLayout;
import com.example.loanemi.R;

import java.util.List;

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder> {

    private final List<CurrencyItem> currencyList;

    private int selectedPosition;

    private final OnCurrencySelectedListener listener;

    public interface OnCurrencySelectedListener {
        void onCurrencySelected(CurrencyItem item, int position);
    }

    public CurrencyAdapter(List<CurrencyItem> currencyList, int selectedPosition, OnCurrencySelectedListener listener) {
        this.currencyList = currencyList;
        this.selectedPosition = selectedPosition;
        this.listener = listener;
    }

    // =====================================================
    // CREATE VIEW HOLDER
    // =====================================================

    @NonNull
    @Override
    public CurrencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_currency, parent, false);

        return new CurrencyViewHolder(view);
    }

    // =====================================================
    // BIND VIEW HOLDER
    // =====================================================

    @Override
    public void onBindViewHolder(@NonNull CurrencyViewHolder holder, int position) {

        CurrencyItem item = currencyList.get(position);


        holder.txtCode.setText(item.getCode());

        holder.txtName.setText(item.getName());

        holder.imgFlag.setImageResource(item.getCurrencyIconResId());


        boolean isSelected = position == selectedPosition;

        if (isSelected) {

            // Selected item background
            holder.rowRoot.setBackgroundResource(R.drawable.bg_button_outline);

            // Radio selected
            holder.imgRadio.setActivated(true);

        } else {

            // Normal item background
            holder.rowRoot.setBackgroundResource(R.drawable.bg_input_field);

            // Radio unselected
            holder.imgRadio.setActivated(false);
        }

        holder.itemView.setOnClickListener(v -> {

            int adapterPosition = holder.getBindingAdapterPosition();

            if (adapterPosition == RecyclerView.NO_POSITION) {

                return;
            }

            int oldPosition = selectedPosition;

            // New selected position
            selectedPosition = adapterPosition;

            // Refresh old item
            if (oldPosition >= 0 && oldPosition < currencyList.size()) {

                notifyItemChanged(oldPosition);
            }

            // Refresh new selected item
            notifyItemChanged(selectedPosition);

            // Callback
            if (listener != null) {

                listener.onCurrencySelected(currencyList.get(selectedPosition), selectedPosition);
            }
        });
    }

    // =====================================================
    // ITEM COUNT
    // =====================================================

    @Override
    public int getItemCount() {

        return currencyList.size();
    }

    // =====================================================
    // VIEW HOLDER
    // =====================================================

    static class CurrencyViewHolder extends RecyclerView.ViewHolder {

        CheckableLinearLayout rowRoot;

        ImageView imgFlag;
        ImageView imgRadio;

        TextView txtCode;
        TextView txtName;

        CurrencyViewHolder(@NonNull View itemView) {
            super(itemView);

            rowRoot = itemView.findViewById(R.id.rowRoot);

            imgFlag = itemView.findViewById(R.id.imgFlag);

            imgRadio = itemView.findViewById(R.id.imgRadio);

            txtCode = itemView.findViewById(R.id.txtCode);

            txtName = itemView.findViewById(R.id.txtName);
        }
    }
}