package com.example.loanemi.Activities.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.Network;
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
import com.example.loanemi.Activities.Activities.CashCalculatorActivity;
import com.example.loanemi.Activities.Activities.FdCalculatorActivity;
import com.example.loanemi.Activities.Activities.HomeLoanActivity;
import com.example.loanemi.Activities.Activities.PersonalLoanActivity;
import com.example.loanemi.Activities.Activities.RdCalculatorActivity;
import com.example.loanemi.Activities.Activities.StudentLoanActivity;
import com.example.loanemi.Activities.Ads.NativeAdPreloader;
import com.example.loanemi.Activities.introflow.LoanStartFlowAdHelper;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.card.MaterialCardView;

public class HomeFragment extends Fragment {

    private RelativeLayout btnNotification;
    private TextView tvLoanSmart;
    public RelativeLayout cardLoanCalculator,cardBusinessLoan,cardHomeLoan,cardAutoLoan,cardStudentLoan;
    public CardView cardCash,cardFd,cardRd;
    FrameLayout home_native_layout;
    private View native_shimmer_layout;

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


    private void initViews(View view) {

        btnNotification = view.findViewById(R.id.btnNotification);
        cardLoanCalculator = view.findViewById(R.id.personalLoan);
        cardBusinessLoan = view.findViewById(R.id.businessLoan);
        cardHomeLoan = view.findViewById(R.id.homeLoan);
        cardAutoLoan = view.findViewById(R.id.autoLoan);
//        cardLoanFreedom = view.findViewById(R.id.cardLoanFreedom);
        cardStudentLoan = view.findViewById(R.id.studentLoan);
        cardCash = view.findViewById(R.id.cardCash);
        cardFd = view.findViewById(R.id.cardFd);
        cardRd = view.findViewById(R.id.cardRd);
        setLoanCardBackgrounds();
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

        cardHomeLoan.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , HomeLoanActivity.class);
            startActivity(i);
        });

        cardAutoLoan.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , AutoLoanActivity.class);
            startActivity(i);
        });

//        cardLoanFreedom.setOnClickListener(v -> openCalculator("Loan Freedom"));

        cardStudentLoan.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , StudentLoanActivity.class);
            startActivity(i);
        });

        cardCash.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , CashCalculatorActivity.class);
            startActivity(i);
        });

        cardFd.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , FdCalculatorActivity.class);
            startActivity(i);
        });

        cardRd.setOnClickListener(v -> {
            Intent i = new Intent(requireContext() , RdCalculatorActivity.class);
            startActivity(i);
        });
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

    private void setLoanCardBackgrounds() {
        if (isDarkMode()) {
            // Dark mode images
            cardLoanCalculator.setBackgroundResource(R.drawable.loan_dark_bg);
            cardBusinessLoan.setBackgroundResource(R.drawable.business_bg_dark);
            //cardAutoLoan.setBackgroundResource(R.drawable.auto_bg_dark);
            cardHomeLoan.setBackgroundResource(R.drawable.home_loan_bg_dark);
            cardStudentLoan.setBackgroundResource(R.drawable.student_bg_dark);

        } else {
            // Light mode images
            cardLoanCalculator.setBackgroundResource(R.drawable.loan_bg);
            cardBusinessLoan.setBackgroundResource(R.drawable.business_bg);
            cardAutoLoan.setBackgroundResource(R.drawable.auto_bg);
            cardHomeLoan.setBackgroundResource(R.drawable.home_loan_bg);
            cardStudentLoan.setBackgroundResource(R.drawable.student_bg);
        }
    }

    private boolean isDarkMode() {
        int nightModeFlags = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
    private void showMessage(String message) {

        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }


}
