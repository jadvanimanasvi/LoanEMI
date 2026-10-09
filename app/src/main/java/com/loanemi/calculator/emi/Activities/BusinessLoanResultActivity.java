package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.loanemi.calculator.emi.Ads.NativeAdPreloader;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.Util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class BusinessLoanResultActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView btnHome;
    private TextView tvLoanAmount;
    private TextView tvInterestRate;
    private TextView tvLoanTerm;
    private TextView tvStartDate;
    private TextView tvMonthlyInstallment;
    private TextView tvMonthlyPayment;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_loan_result);
        setupEdgeToEdge(this, R.id.main);

        initViews();
        setUpAds();
        setupClickListeners();
        showResult();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTerm = findViewById(R.id.tvLoanTerm);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvMonthlyInstallment = findViewById(R.id.tvMonthlyInstallment);
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);
    }

    private void setUpAds() {
        if (!Util.isInternetAvailable(this)) {
            return;
        }

        FrameLayout nativeLayout =
                findViewById(R.id.business_loan_native_layout);

        ShimmerFrameLayout shimmerNative =
                findViewById(R.id.nativeShimmerLayout);

        if (nativeLayout != null && shimmerNative != null) {
            NativeAdPreloader.show(
                    this,
                    nativeLayout,
                    getString(R.string.business_loan_result_native_medium),
                    shimmerNative
            );
        }
    }

    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnHome != null) {
            btnHome.setOnClickListener(v -> finish());
        }
    }

    private void showResult() {
        double loanAmount = getIntent().getDoubleExtra(
                "loan_amount",
                0
        );

        double interestRate = getIntent().getDoubleExtra(
                "interest_rate",
                0
        );

        int loanTerm = getIntent().getIntExtra(
                "loan_term",
                0
        );

        int totalMonths = getIntent().getIntExtra(
                "total_months",
                0
        );

        String loanTermUnit = getIntent().getStringExtra(
                "loan_term_unit"
        );

        double monthlyEMI = getIntent().getDoubleExtra(
                "monthly_emi",
                0
        );

        double totalPayment = getIntent().getDoubleExtra(
                "total_payment",
                0
        );

        String currencyCode = getIntent().getStringExtra(
                "currency_code"
        );

        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            currencyCode = "USD";
        }

        currencyCode = currencyCode.trim().toUpperCase(Locale.US);

        if (loanTermUnit == null || loanTermUnit.trim().isEmpty()) {
            loanTermUnit = "Month";
        }

        if (loanTerm <= 0 && totalMonths > 0) {
            if (totalMonths % 12 == 0) {
                loanTerm = totalMonths / 12;
                loanTermUnit = "Year";
            } else {
                loanTerm = totalMonths;
                loanTermUnit = "Month";
            }
        }

        if (tvLoanAmount != null) {
            tvLoanAmount.setText(
                    formatCurrencyAfter(loanAmount, currencyCode)
            );
        }

        if (tvInterestRate != null) {
            tvInterestRate.setText(formatNumber(interestRate) + "%");
        }

        if (tvLoanTerm != null) {
            if (loanTerm > 0) {
                tvLoanTerm.setText(
                        formatLoanTerm(loanTerm, loanTermUnit)
                );
            } else {
                tvLoanTerm.setText("N/A");
            }

            tvLoanTerm.setVisibility(View.VISIBLE);
        }

        if (tvStartDate != null) {
            tvStartDate.setText("Monthly");
        }

        if (tvMonthlyPayment != null) {
            if (totalPayment <= 0 && totalMonths > 0) {
                totalPayment = monthlyEMI * totalMonths;
            }

            tvMonthlyPayment.setText(
                    formatCurrencyAfter(totalPayment, currencyCode)
            );
        }
    }

    private String formatCurrencyAfter(
            double amount,
            String currencyCode
    ) {
        String symbol;

        try {
            symbol = java.util.Currency.getInstance(currencyCode)
                    .getSymbol(Locale.US);
        } catch (Exception e) {
            symbol = "$";
        }

        return formatIndianCurrencyNumber(amount) + " " + symbol;
    }

    private String formatIndianCurrencyNumber(double amount) {
        DecimalFormatSymbols symbols =
                new DecimalFormatSymbols(Locale.US);

        DecimalFormat formatter =
                new DecimalFormat("#,##0.00", symbols);

        String value = formatter.format(amount);
        String[] parts = value.split("\\.");

        String integerPart = parts[0].replace(",", "");
        String decimalPart = parts.length > 1 ? parts[1] : "00";

        return formatIndianInteger(integerPart) + "." + decimalPart;
    }

    private String formatIndianInteger(String number) {
        if (number == null || number.isEmpty()) {
            return "0";
        }

        boolean negative = number.startsWith("-");

        if (negative) {
            number = number.substring(1);
        }

        if (number.length() <= 3) {
            return negative ? "-" + number : number;
        }

        String lastThree =
                number.substring(number.length() - 3);

        String remaining =
                number.substring(0, number.length() - 3);

        StringBuilder result = new StringBuilder();

        while (remaining.length() > 2) {
            int start = remaining.length() - 2;
            result.insert(0, "," + remaining.substring(start));
            remaining = remaining.substring(0, start);
        }

        if (!remaining.isEmpty()) {
            result.insert(0, remaining);
        }

        result.append(",").append(lastThree);

        if (negative) {
            result.insert(0, "-");
        }

        return result.toString();
    }

    private String formatNumber(double value) {
        if (value == (long) value) {
            return String.valueOf((long) value);
        }

        return String.format(Locale.US, "%.2f", value);
    }

    private String formatLoanTerm(int loanTerm, String unit) {
        if (unit != null &&
                (unit.equalsIgnoreCase("Year") ||
                        unit.equalsIgnoreCase("Years"))) {
            return loanTerm + (loanTerm == 1 ? " Year" : " Years");
        }

        return loanTerm + (loanTerm == 1 ? " Month" : " Months");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.hide(this);
    }
}