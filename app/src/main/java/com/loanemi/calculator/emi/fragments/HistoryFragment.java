package com.loanemi.calculator.emi.fragments;

import android.content.Context;
import android.content.Intent;
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

import com.loanemi.calculator.emi.Activities.AutoLoanResultActivity;
import com.loanemi.calculator.emi.Activities.BusinessLoanResultActivity;
import com.loanemi.calculator.emi.Activities.HomeLoanResultActivity;
import com.loanemi.calculator.emi.Activities.PersonalLoanResultActivity;
import com.loanemi.calculator.emi.Activities.StudentLoanRsultActivity;
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
    private View rootView;

    private final List<HistoryItem> historyList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(LocaleHelper.setLocale(context));
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        rootView = view;

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
        rvHistory.setLayoutManager(
                new LinearLayoutManager(requireContext()));

        rvHistory.setHasFixedSize(false);

        historyAdapter = new HistoryAdapter(
                requireContext(), historyList);

        rvHistory.setAdapter(historyAdapter);

        historyAdapter.setOnSelectionChangedListener(
                this::updateDeleteButton);

        historyAdapter.setOnHistoryClickListener(
                this::openHistoryResult);
    }

    private void setupListeners() {
        btnEdit.setOnClickListener(v -> {
            if (historyList.isEmpty()) {
                return;
            }

            if (!historyAdapter.isSelectionMode()) {
                historyAdapter.setSelectionMode(true);
                btnEdit.setImageResource(R.drawable.ic_close);
                deleteButtonContainer.setVisibility(View.VISIBLE);
                updateDeleteButton(historyAdapter.getSelectedIds().size());
            } else {
                exitSelectionMode();
            }
        });

        deleteButtonContainer.setOnClickListener(v -> {
            List<Long> selectedIds = historyAdapter.getSelectedIds();

            if (selectedIds.isEmpty()) {
                return;
            }

            historyManager.deleteSelected(selectedIds);

            exitSelectionMode();
            loadHistory();
        });
    }

    private void exitSelectionMode() {
        if (historyAdapter != null) {
            historyAdapter.setSelectionMode(false);
        }

        if (btnEdit != null) {
            btnEdit.setImageResource(R.drawable.edit_ic);
            btnEdit.setColorFilter(
                    ContextCompat.getColor(
                            requireContext(), R.color.text_color));
        }

        if (deleteButtonContainer != null) {
            deleteButtonContainer.setVisibility(View.GONE);
        }
    }

    private void loadHistory() {
        if (historyManager == null || historyAdapter == null) {
            return;
        }

        List<HistoryItem> savedHistory = historyManager.getHistory();

        historyList.clear();
        historyList.addAll(savedHistory);

        historyAdapter.updateList(new ArrayList<>(historyList));

        updateEmptyState();
    }

    private void updateDeleteButton(int count) {
        if (deleteButtonContainer == null || tvDelete == null) {
            return;
        }

        if (count > 0) {
            deleteButtonContainer.setVisibility(View.VISIBLE);
            deleteButtonContainer.setBackgroundResource(
                    R.drawable.bg_delete_button);
            deleteButtonContainer.setClickable(true);
            deleteButtonContainer.setFocusable(true);
            tvDelete.setText("Delete (" + count + ")");

            if (ivDelete != null) {
                ivDelete.setVisibility(View.VISIBLE);
            }
        } else {
            tvDelete.setText("Delete (0)");
            deleteButtonContainer.setVisibility(
                    historyAdapter != null
                            && historyAdapter.isSelectionMode()
                            ? View.VISIBLE : View.GONE);
            deleteButtonContainer.setClickable(false);
            deleteButtonContainer.setFocusable(false);
        }
    }

    private void updateEmptyState() {
        if (rvHistory == null || btnEdit == null) {
            return;
        }

        boolean isEmpty = historyList.isEmpty();

        rvHistory.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        btnEdit.setVisibility(isEmpty ? View.INVISIBLE : View.VISIBLE);

        if (isEmpty) {
            exitSelectionMode();
            btnEdit.setVisibility(View.INVISIBLE);
        }
    }

    private void openHistoryResult(HistoryItem item) {
        if (item == null || !isAdded()) {
            return;
        }

        String loanType = item.getLoanType();

        if (loanType == null) {
            return;
        }

        Intent intent;

        if (loanType.equalsIgnoreCase("Personal Loan")) {
            intent = new Intent(
                    requireContext(), PersonalLoanResultActivity.class);

        } else if (loanType.equalsIgnoreCase("Business Loan")) {
            intent = new Intent(
                    requireContext(), BusinessLoanResultActivity.class);

        } else if (loanType.equalsIgnoreCase("Auto Loan")) {
            intent = new Intent(
                    requireContext(), AutoLoanResultActivity.class);

        }else if (loanType.equalsIgnoreCase("Home Loan")) {
            intent = new Intent(
                    requireContext(), HomeLoanResultActivity.class);

        }else if (loanType.equalsIgnoreCase("Student Loan")) {
            intent = new Intent(
                    requireContext(), StudentLoanRsultActivity.class);

        } else {
            return;
        }

        intent.putExtra("loan_amount", item.getLoanAmount());
        intent.putExtra("interest_rate", item.getInterestRate());
        intent.putExtra("loan_term", item.getLoanTerm());
        intent.putExtra("loan_term_unit", item.getLoanTermUnit());
        intent.putExtra("total_months", item.getTotalMonths());
        intent.putExtra("monthly_emi", item.getMonthlyEmi());
        intent.putExtra("total_interest", item.getTotalInterest());
        intent.putExtra("total_payment", item.getTotalPayment());
        intent.putExtra("start_date", item.getStartDate());
        intent.putExtra("currency_code", item.getCurrencyCode());

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (historyManager != null && historyAdapter != null) {
            loadHistory();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        rootView = null;
        rvHistory = null;
        btnEdit = null;
        deleteButtonContainer = null;
        ivDelete = null;
        tvDelete = null;
        historyAdapter = null;
    }
}
