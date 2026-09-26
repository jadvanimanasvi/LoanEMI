package com.example.loanemi.Activities.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.loanemi.Activities.Activities.AutoLoanActivity;
import com.example.loanemi.Activities.Activities.BusinessLoanActivity;
import com.example.loanemi.Activities.Activities.PersonalLoanActivity;
import com.example.loanemi.Activities.Ads.NativeAdPreloader;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private RelativeLayout btnNotification;
    private TextView tvLoanSmart;
    private CardView cardLoanCalculator;
    private CardView cardBusinessLoan;
    private CardView cardHomeLoan;
    private CardView cardAutoLoan;
    private CardView cardStudentLoan;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        setupClickListeners();
        setupAds(view);
//        setupLoanSmartText();

        return view;
    }

    private void setupAds(@NonNull View view) {
        if (Util.isInternetAvailable(this.requireActivity())) {
            FrameLayout nativeLayout = view.findViewById(R.id.home_native_layout);
            ShimmerFrameLayout shimmerNative = view.findViewById(R.id.nativeShimmerLayout);

            NativeAdPreloader.show(
                    requireActivity(),
                    nativeLayout,
                    getString(R.string.home_native_medium),
                    shimmerNative
            );
        }
    }

    private void initViews(View view) {

        btnNotification = view.findViewById(R.id.btnNotification);
        cardLoanCalculator = view.findViewById(R.id.cardLoanCalculator);
        cardBusinessLoan = view.findViewById(R.id.cardBusinessLoan);
        cardHomeLoan = view.findViewById(R.id.cardHomeLoan);
        cardAutoLoan = view.findViewById(R.id.cardAutoLoan);
//        cardLoanFreedom = view.findViewById(R.id.cardLoanFreedom);
        cardStudentLoan = view.findViewById(R.id.cardStudentLoan);
    }

    private void setupClickListeners() {

        btnNotification.setOnClickListener(v -> showMessage("Notifications clicked"));

        cardLoanCalculator.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , PersonalLoanActivity.class);
            startActivity(i);
        });

        cardBusinessLoan.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , BusinessLoanActivity.class);
            startActivity(i);
        });

        cardHomeLoan.setOnClickListener(v -> openCalculator("Home Loan"));

        cardAutoLoan.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , AutoLoanActivity.class);
            startActivity(i);
        });

//        cardLoanFreedom.setOnClickListener(v -> openCalculator("Loan Freedom"));

        cardStudentLoan.setOnClickListener(v -> openCalculator("Student Loan"));
    }

    /*private void setupLoanSmartText() {

        tvLoanSmart.post(() -> {

            float height = tvLoanSmart.getHeight();

            LinearGradient gradient = new LinearGradient(0, 0, 0, height, new int[]{Color.parseColor("#1F871E"), // Yellow
                    Color.parseColor("#003A96"), // White
                    Color.parseColor("#0A58D1"), // Soft Blue
                    Color.parseColor("#0A58D1"), // Blue
                    Color.parseColor("#011530")  // Deep Blue
            }, new float[]{0.00f, 0.20f, 0.50f, 0.75f, 1.00f}, Shader.TileMode.CLAMP);

            tvLoanSmart.getPaint().setShader(gradient);
            tvLoanSmart.invalidate();
        });
    }*/


    private void openCalculator(String loanType) {
        showMessage(loanType + " selected");
    }

    private void showMessage(String message) {

        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
