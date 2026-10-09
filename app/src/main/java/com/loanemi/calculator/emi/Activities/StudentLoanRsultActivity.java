package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.loanemi.calculator.emi.Ads.NativeAdPreloader;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.fragments.HomeFragment;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.Util;

import java.text.NumberFormat;
import java.util.Locale;

public class StudentLoanRsultActivity extends AppCompatActivity {

    private TextView tvLoanAmount;
    private TextView tvInterestRate;
    private TextView tvLoanTerm;
    private TextView tvStartDate;
    private TextView tvMonthlyPayment;
    private TextView tvTotalInterest;
    private TextView tvTotalPayment;

    private ImageView btnBack;
    private ImageView btnHome;

    private String currencyCode = "USD";
    private String currencySymbol = "$";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_loan_rsult);

        setupEdgeToEdge(this, R.id.main);

        initViews();
        setUpAds();
        showResult();
        setupButtons();
    }

    private void initViews() {
        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTerm = findViewById(R.id.tvLoanTerm);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
    }

    private void setUpAds() {
        if (Util.isInternetAvailable(this)) {
            FrameLayout nativeLayout = findViewById(R.id.student_loan_native_layout);

            ShimmerFrameLayout shimmerNative = findViewById(R.id.nativeShimmerLayout);

            if (nativeLayout != null && shimmerNative != null) {
                NativeAdPreloader.show(this, nativeLayout, getString(R.string.student_loan_result_native_medium), shimmerNative);
            }
        }
    }


    private void showResult() {

        Intent intent = getIntent();

        double loanAmount = intent.getDoubleExtra("loan_amount", 0);
        double interestRate = intent.getDoubleExtra("interest_rate", 0);
        double loanTerm = intent.getDoubleExtra("loan_term", 0);

        String loanTermUnit = intent.getStringExtra("loan_term_unit");

        int totalMonths = intent.getIntExtra("total_months", 0);

        double monthlyPayment =
                intent.getDoubleExtra("monthly_payment", 0);

        double totalInterest =
                intent.getDoubleExtra("total_interest", 0);

        double totalPayment =
                intent.getDoubleExtra("total_payment", 0);

        currencyCode = intent.getStringExtra("currency_code");

        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            currencyCode = "USD";
        }

        currencyCode = currencyCode.toUpperCase(Locale.US);

        String receivedSymbol = intent.getStringExtra("currency_symbol");

        currencySymbol = receivedSymbol != null
                && !receivedSymbol.trim().isEmpty()
                ? receivedSymbol
                : getCurrencySymbol(currencyCode);

        if (totalMonths <= 0 && loanTerm > 0) {
            if ("Years".equalsIgnoreCase(loanTermUnit)) {
                totalMonths = (int) Math.round(loanTerm * 12);
            } else {
                totalMonths = (int) Math.round(loanTerm);
            }
        }

        if (loanAmount > 0 && totalMonths > 0 && interestRate >= 0) {

            double monthlyRate = interestRate / 1200.0;

            if (monthlyRate == 0) {
                monthlyPayment = loanAmount / totalMonths;
            } else {
                double factor = Math.pow(1 + monthlyRate, totalMonths);

                monthlyPayment =
                        loanAmount * monthlyRate * factor / (factor - 1);
            }

            totalPayment = monthlyPayment * totalMonths;
            totalInterest = totalPayment - loanAmount;
        }

        tvLoanAmount.setText(formatMoney(loanAmount));

        tvInterestRate.setText(
                String.format(Locale.getDefault(), "%.2f%%", interestRate)
        );

        if ("Years".equalsIgnoreCase(loanTermUnit)) {
            tvLoanTerm.setText(
                    String.format(Locale.getDefault(), "%.0f Years", loanTerm)
            );
        } else {
            tvLoanTerm.setText(
                    String.format(Locale.getDefault(), "%d Months", totalMonths)
            );
        }

        String startDate = intent.getStringExtra("start_date");

        tvStartDate.setText(
                startDate != null && !startDate.trim().isEmpty()
                        ? startDate
                        : "—"
        );

        tvMonthlyPayment.setText(formatMoney(monthlyPayment));

        if (tvTotalInterest != null) {
            tvTotalInterest.setText(formatMoney(totalInterest));
        }

        if (tvTotalPayment != null) {
            tvTotalPayment.setText(formatMoney(totalPayment));
        }
    }


    private String formatMoney(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

        if (amount == Math.floor(amount)) {
            formatter.setMinimumFractionDigits(0);
            formatter.setMaximumFractionDigits(0);
        } else {
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);
        }

        return formatter.format(amount) + currencySymbol;
    }

    private String getCurrencySymbol(String code) {
        if (code == null) {
            return "$";
        }

        switch (code.toUpperCase(Locale.US)) {
            case "USD":
                return "$";
            case "GBP":
                return "£";
            case "CNY":
            case "JPY":
                return "¥";
            case "INR":
                return "₹";
            case "VND":
                return "₫";
            case "THB":
                return "฿";
            case "IDR":
                return "Rp";
            case "EUR":
                return "€";
            case "AUD":
                return "A$";
            case "CAD":
                return "C$";
            case "SGD":
                return "S$";
            case "HKD":
                return "HK$";
            case "AED":
                return "د.إ";
            case "SAR":
                return "﷼";
            case "KRW":
                return "₩";
            case "MYR":
                return "RM";
            default:
                return code.toUpperCase(Locale.US);
        }
    }

    private void setupButtons() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> finish());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.hide(this);
    }
}