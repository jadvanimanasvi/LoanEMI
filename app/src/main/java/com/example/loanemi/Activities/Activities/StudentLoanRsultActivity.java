package com.example.loanemi.Activities.Activities;

import static com.example.loanemi.Activities.utils.Util.setupEdgeToEdge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loanemi.Activities.fragments.HomeFragment;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;

import java.text.NumberFormat;
import java.util.Locale;

public class StudentLoanRsultActivity extends AppCompatActivity {

    private TextView tvLoanAmount;
    private TextView tvInterestRate;
    private TextView tvLoanTerm;
    private TextView tvStartDate;
    private TextView tvMonthlyPayment;
    private ImageView btnBack;
    private ImageView btnHome;
    private String currencyCode = "USD";
    private String currencyCountry = "US Dollar";
    private String currencySymbol = "$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_loan_rsult);

        setupEdgeToEdge(this, R.id.main);

        initViews();
        showResult();
        setupButtons();
    }

    private void initViews() {

        tvLoanAmount = findViewById(R.id.tvLoanAmount);

        tvInterestRate = findViewById(R.id.tvInterestRate);

        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvStartDate = findViewById(R.id.tvStartDate);

        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);

        btnBack = findViewById(R.id.btnBack);

        btnHome = findViewById(R.id.btnHome);
    }

    private void showResult() {

        Intent intent = getIntent();

        double loanAmount = intent.getDoubleExtra("loan_amount", 0);

        double interestRate = intent.getDoubleExtra("interest_rate", 0);

        double loanTerm = intent.getDoubleExtra("loan_term", 0);

        String loanTermUnit = intent.getStringExtra("loan_term_unit");

        int totalMonths = intent.getIntExtra("total_months", 0);

        double monthlyPayment = intent.getDoubleExtra("monthly_payment", 0);

        String receivedCode = intent.getStringExtra("currency_code");

        if (receivedCode != null && !receivedCode.trim().isEmpty()) {

            currencyCode = receivedCode;
        }

        String receivedCountry = intent.getStringExtra("currency_country");

        if (receivedCountry != null && !receivedCountry.trim().isEmpty()) {

            currencyCountry = receivedCountry;
        }

        currencySymbol = getCurrencySymbol(currencyCode);

        tvLoanAmount.setText(formatMoney(loanAmount));

        tvInterestRate.setText(String.format(Locale.getDefault(), "%.2f%%", interestRate));

        String termText;

        if (loanTermUnit != null && loanTermUnit.equalsIgnoreCase("Years")) {

            termText = String.format(Locale.getDefault(), "%.0f Years (%d Months)", loanTerm, totalMonths);

        } else {

            termText = String.format(Locale.getDefault(), "%.0f Months", loanTerm);
        }

        tvLoanTerm.setText(termText);
        tvStartDate.setText("Monthly");

        tvMonthlyPayment.setText(formatMoney(monthlyPayment));
    }

    private String formatMoney(double amount) {

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("en", "IN"));

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(amount) + currencySymbol;
    }

    private String getCurrencySymbol(String code) {

        if (code == null) {
            return "$";
        }

        switch (code.toUpperCase(Locale.US)) {

            case "USD": return "$";

            case "GBP": return "£";

            case "CNY": return "¥";

            case "INR": return "₹";

            case "VND": return "₫";

            case "THB": return "฿";

            case "IDR": return "Rp";

            case "EUR": return "€";

            case "JPY": return "¥";

            case "AUD": return "A$";

            case "CAD": return "C$";

            case "SGD": return "S$";

            case "HKD": return "HK$";

            case "AED": return "د.إ";

            case "SAR": return "﷼";

            default: return code;
        }
    }

    private void setupButtons() {

        if (btnBack != null) {

            btnBack.setOnClickListener(v -> finish());
        }

        if (btnHome != null) {

            btnHome.setOnClickListener(v -> {

                Intent intent = new Intent(StudentLoanRsultActivity.this, HomeFragment.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                startActivity(intent);

                finish();
            });
        }
    }

    @Override
    protected void onResume() {

        super.onResume();

        Util.hide(this);
    }
}