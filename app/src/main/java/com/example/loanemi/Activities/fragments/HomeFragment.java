package com.example.loanemi.Activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loanemi.R;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private ImageView imgNotification;
    private MaterialCardView cardLoanCalculator;
    private MaterialCardView cardBusinessLoan;
    private MaterialCardView cardHomeLoan;
    private MaterialCardView cardAutoLoan;
    private MaterialCardView cardLoanFreedom;
    private MaterialCardView cardStudentLoan;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {

        imgNotification = view.findViewById(R.id.imgNotification);

        cardLoanCalculator = view.findViewById(R.id.cardLoanCalculator);
        cardBusinessLoan = view.findViewById(R.id.cardBusinessLoan);
        cardHomeLoan = view.findViewById(R.id.cardHomeLoan);
        cardAutoLoan = view.findViewById(R.id.cardAutoLoan);
        cardLoanFreedom = view.findViewById(R.id.cardLoanFreedom);
        cardStudentLoan = view.findViewById(R.id.cardStudentLoan);
    }

    private void setupClickListeners() {

        imgNotification.setOnClickListener(v -> showMessage("Notifications clicked"));

        cardLoanCalculator.setOnClickListener(v -> openCalculator("Loan Calculator"));

        cardBusinessLoan.setOnClickListener(v -> openCalculator("Business Loan"));

        cardHomeLoan.setOnClickListener(v -> openCalculator("Home Loan"));

        cardAutoLoan.setOnClickListener(v -> openCalculator("Auto Loan"));

        cardLoanFreedom.setOnClickListener(v -> openCalculator("Loan Freedom"));

        cardStudentLoan.setOnClickListener(v -> openCalculator("Student Loan"));
    }

    private void openCalculator(String loanType) {
        showMessage(loanType + " selected");
    }

    private void showMessage(String message) {

        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
