package com.example.loanemi.Activities.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_business_loan_result);

        initViews();

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

    private void setupClickListeners() {

        if (btnBack != null) {

            btnBack.setOnClickListener(v -> finish());
        }

        if (btnHome != null) {

            btnHome.setOnClickListener(v -> finishAffinity());
        }
    }

    private void showResult() {

        double loanAmount = getIntent().getDoubleExtra("loan_amount", 0);


        double interestRate = getIntent().getDoubleExtra("interest_rate", 0);

        int loanTerm = getIntent().getIntExtra("loan_term", 0);


        String loanTermUnit = getIntent().getStringExtra("loan_term_unit");


        double monthlyEMI = getIntent().getDoubleExtra("monthly_emi", 0);


        String currencyCode = getIntent().getStringExtra("currency_code");


        if (loanTermUnit == null || loanTermUnit.trim().isEmpty()) {

            loanTermUnit = "Month";
        }


        if (currencyCode == null || currencyCode.trim().isEmpty()) {

            currencyCode = "USD";
        }


        currencyCode = currencyCode.trim().toUpperCase(Locale.US);


        if (tvLoanAmount != null) {

            tvLoanAmount.setText(formatCurrencyAfter(loanAmount, currencyCode));
        }

        if (tvInterestRate != null) {

            tvInterestRate.setText(formatNumber(interestRate) + "%");
        }

        if (tvLoanTerm != null) {

            tvLoanTerm.setText(formatLoanTerm(loanTerm, loanTermUnit));
        }

        if (tvStartDate != null) {

            tvStartDate.setText("Monthly");
        }

        if (tvMonthlyPayment != null) {

            tvMonthlyPayment.setText(formatCurrencyAfter(monthlyEMI, currencyCode));
        }

        if (tvMonthlyInstallment != null) {

            tvMonthlyInstallment.setText(formatCurrencyAfter(monthlyEMI, currencyCode));
        }
    }


    private String formatCurrencyAfter(double amount, String currencyCode) {

        String symbol;


        try {

            java.util.Currency currency = java.util.Currency.getInstance(currencyCode);


            symbol = currency.getSymbol(Locale.US);

        } catch (Exception e) {

            symbol = "$";
        }


        String formatted = formatIndianCurrencyNumber(amount);


        return formatted + " " + symbol;
    }


    private String formatIndianCurrencyNumber(double amount) {

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);


        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);


        String value = formatter.format(amount);


        String[] parts = value.split("\\.");


        String integerPart = parts[0];


        String decimalPart = parts.length > 1 ? parts[1] : "00";


        String formattedInteger = formatIndianInteger(integerPart.replace(",", ""));


        return formattedInteger + "." + decimalPart;
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


        String lastThree = number.substring(number.length() - 3);


        String remaining = number.substring(0, number.length() - 3);


        StringBuilder result = new StringBuilder();


        while (remaining.length() > 2) {

            int start = remaining.length() - 2;


            result.insert(0, "," + remaining.substring(start));


            remaining = remaining.substring(0, start);
        }


        if (!remaining.isEmpty()) {

            result.insert(0, remaining);
        }

        result.append(",");

        result.append(lastThree);


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

        if (unit == null || unit.trim().isEmpty()) {

            unit = "Month";
        }

        if (unit.equalsIgnoreCase("Year") || unit.equalsIgnoreCase("Years")) {

            return loanTerm + " Year" + (loanTerm == 1 ? "" : "s");
        }

        return loanTerm + " Month" + (loanTerm == 1 ? "" : "s");
    }

    @Override
    protected void onResume() {

        super.onResume();

        Util.hide(this);
    }
}