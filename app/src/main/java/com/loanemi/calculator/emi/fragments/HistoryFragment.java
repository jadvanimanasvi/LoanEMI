package com.loanemi.calculator.emi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.Adapters.HistoryAdapter;
import com.loanemi.calculator.emi.Models.HistoryItem;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.LoanHistoryManager;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private RecyclerView rvHistory;
    private ImageView btnEdit;
    private FrameLayout deleteButtonContainer;
    private ImageView ivDelete;
    private TextView tvDelete;
    private HistoryAdapter historyAdapter;
    private LoanHistoryManager historyManager;

    private final List<HistoryItem> historyList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(LocaleHelper.setLocale(context));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);

        historyManager = new LoanHistoryManager(requireContext());

        setupRecyclerView();

        setupListeners();

        loadHistory();
    }

    private void initViews(View view) {

        rvHistory = view.findViewById(R.id.rvHistory);

        btnEdit = view.findViewById(R.id.btnEdit);

        deleteButtonContainer = view.findViewById(R.id.deleteButtonContainer);

        ivDelete = view.findViewById(R.id.ivDelete);

        tvDelete = view.findViewById(R.id.tvDelete);
    }

    private void setupRecyclerView() {

        rvHistory.setLayoutManager(new LinearLayoutManager(requireContext()));

        rvHistory.setHasFixedSize(false);

        historyAdapter = new HistoryAdapter(requireContext(), historyList);

        rvHistory.setAdapter(historyAdapter);

        historyAdapter.setOnSelectionChangedListener(count -> updateDeleteButton(count));
    }

    private void setupListeners() {

        btnEdit.setOnClickListener(v -> {

            if (historyList.isEmpty()) {
                return;
            }

            if (!historyAdapter.isSelectionMode()) {

                historyAdapter.setSelectionMode(true);

                btnEdit.setImageResource(R.drawable.ic_close);

            } else {

                historyAdapter.setSelectionMode(false);

                btnEdit.setImageResource(R.drawable.edit_ic);

                btnEdit.setColorFilter(ContextCompat.getColor(requireContext(), R.color.text_color));
                deleteButtonContainer.setVisibility(View.GONE);
            }
        });

        // Delete button
        deleteButtonContainer.setOnClickListener(v -> {

            List<Long> selectedIds = historyAdapter.getSelectedIds();

            if (selectedIds.isEmpty()) {
                return;
            }

            historyManager.deleteSelected(selectedIds);

            historyAdapter.setSelectionMode(false);

            btnEdit.setImageResource(R.drawable.edit_ic);

            loadHistory();

            deleteButtonContainer.setVisibility(View.GONE);
        });
    }

    private void loadHistory() {

        List<HistoryItem> savedHistory = historyManager.getHistory();

        historyList.clear();
        historyList.addAll(savedHistory);

        if (historyAdapter != null) {
            historyAdapter.updateList(savedHistory);
        }

        updateEmptyState();
    }

    private void updateDeleteButton(int count) {

        if (count > 0) {

            deleteButtonContainer.setVisibility(View.VISIBLE);

            deleteButtonContainer.setBackgroundResource(R.drawable.bg_delete_button);

            deleteButtonContainer.setClickable(true);

            deleteButtonContainer.setFocusable(true);

            tvDelete.setText("Delete (" + count + ")");

            ivDelete.setVisibility(View.VISIBLE);

        } else {

            deleteButtonContainer.setVisibility(View.GONE);
        }
    }

    private void updateEmptyState() {

        // Optional:
        // You can add an empty-history TextView/ImageView later.
    }

    @Override
    public void onResume() {

        super.onResume();

        if (historyManager != null) {
            loadHistory();
        }
    }
}