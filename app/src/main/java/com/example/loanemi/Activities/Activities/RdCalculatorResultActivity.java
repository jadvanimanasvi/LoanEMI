package com.example.loanemi.Activities.Activities;

import static com.example.loanemi.Activities.utils.Util.setupEdgeToEdge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;

import java.text.DecimalFormat;
import java.util.Locale;

public class RdCalculatorResultActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView btnHome;
    private TextView tvLoanAmount;
    private TextView tvInterestRate;
    private TextView tvLoanTerm;
    private TextView tvStartDate;
    private TextView tvMonthlyPayment;
    private TextView tvTotalPayment;
    private TextView tvTotalInterest;
    private TextView tvPayOffDate;
    private TextView labelLoanAmount;
    private TextView labelInterestRate;
    private TextView labelLoanTerm;
    private TextView labelStartDate;
    private TextView labelMonthly;
    private TextView labelTotalPayment;
    private TextView labelTotalInterest;
    private TextView labelPayOff;
    private TextView tvLoanInformationTitle;
    private TextView tvResultTitle;
    private String currencyCode = "USD";
    private String currencySymbol = "$";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rd_calculator_result);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        readIntentData();

        setupButtons();
    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);

        btnHome = findViewById(R.id.btnHome);

        tvLoanAmount = findViewById(R.id.tvLoanAmount);

        tvInterestRate = findViewById(R.id.tvInterestRate);

        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvStartDate = findViewById(R.id.tvStartDate);

        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);

        tvTotalPayment = findViewById(R.id.tvTotalPayment);

        tvTotalInterest = findViewById(R.id.tvTotalInterest);

        tvPayOffDate = findViewById(R.id.tvPayOffDate);

        labelLoanAmount = findViewById(R.id.labelLoanAmount);

        labelInterestRate = findViewById(R.id.labelInterestRate);

        labelLoanTerm = findViewById(R.id.labelLoanTerm);

        labelStartDate = findViewById(R.id.labelStartDate);

        labelMonthly = findViewById(R.id.labelMonthly);

        labelTotalPayment = findViewById(R.id.labelTotalPayment);

        labelTotalInterest = findViewById(R.id.labelTotalInterest);

        labelPayOff = findViewById(R.id.labelPayOff);

        tvLoanInformationTitle = findViewById(R.id.tvLoanInformationTitle);

        tvResultTitle = findViewById(R.id.tvResultTitle);
    }

    private void readIntentData() {

        Intent intent = getIntent();

        double monthlyDeposit = intent.getDoubleExtra("monthly_deposit", 0);

        double interestRate = intent.getDoubleExtra("interest_rate", 0);

        String loanTerm = intent.getStringExtra("loan_term");

        String loanTermUnit = intent.getStringExtra("loan_term_unit");

        int totalMonths = intent.getIntExtra("total_months", 0);

        String compounding = intent.getStringExtra("compounding");

        String startDate = intent.getStringExtra("start_date");

        double totalInvestment = intent.getDoubleExtra("total_investment", 0);

        double totalInterest = intent.getDoubleExtra("total_interest", 0);

        double maturityValue = intent.getDoubleExtra("maturity_value", 0);


        currencyCode = intent.getStringExtra("currency_code");

        if (currencyCode == null || currencyCode.trim().isEmpty()) {

            currencyCode = "USD";
        }

        currencyCode = currencyCode.trim().toUpperCase(Locale.US);

        currencySymbol = getCurrencySymbol(currencyCode);


        tvLoanInformationTitle.setText("RD Information");

        tvResultTitle.setText("Result After Calculation");

        labelLoanAmount.setText("Monthly Investment");

        labelInterestRate.setText("Interest Rate");

        labelLoanTerm.setText("RD Term");

        labelStartDate.setText("Start Date");

        labelMonthly.setText("Total Investment");

        labelTotalPayment.setText("Total Interest");

        labelTotalInterest.setText("Maturity Value");

        labelPayOff.setText("Compounding");


        tvLoanAmount.setText(formatCurrency(monthlyDeposit));

        tvInterestRate.setText(formatPercent(interestRate));

        String termDisplay;

        if (loanTerm != null && !loanTerm.trim().isEmpty()) {

            termDisplay = loanTerm.trim();

        } else {

            termDisplay = String.valueOf(totalMonths);
        }

        if (loanTermUnit != null && !loanTermUnit.trim().isEmpty()) {

            termDisplay += " " + loanTermUnit;
        }

        tvLoanTerm.setText(termDisplay);

        if (startDate != null && !startDate.trim().isEmpty()) {

            tvStartDate.setText(startDate);

        } else {

            tvStartDate.setText("-");
        }


        tvMonthlyPayment.setText(formatCurrency(totalInvestment));

        tvTotalPayment.setText(formatCurrency(totalInterest));

        tvTotalInterest.setText(formatCurrency(maturityValue));

        if (compounding != null && !compounding.trim().isEmpty()) {

            tvPayOffDate.setText(compounding);

        } else {

            tvPayOffDate.setText("-");
        }
    }


    private String formatCurrency(double amount) {

        DecimalFormat decimalFormat = new DecimalFormat("#,##0.##");


        return decimalFormat.format(amount) + currencySymbol;
    }

    private String formatPercent(double value) {

        DecimalFormat decimalFormat = new DecimalFormat("0.##");

        return decimalFormat.format(value) + "%";
    }

    private String getCurrencySymbol(String code) {

        if (code == null) {
            return "$";
        }

        switch (code.toUpperCase(Locale.US)) {

            case "USD":
                return "$";

            case "EUR":
                return "€";

            case "GBP":
                return "£";

            case "INR":
                return "₹";

            case "JPY":
                return "¥";

            case "CNY":
                return "¥";

            case "KRW":
                return "₩";

            case "AUD":
                return "A$";

            case "CAD":
                return "C$";

            case "NZD":
                return "NZ$";

            case "SGD":
                return "S$";

            case "HKD":
                return "HK$";

            case "CHF":
                return "CHF";

            case "THB":
                return "฿";

            case "VND":
                return "₫";

            case "IDR":
                return "Rp";

            case "MYR":
                return "RM";

            case "PHP":
                return "₱";

            case "AED":
                return "د.إ";

            case "SAR":
                return "﷼";

            case "QAR":
                return "﷼";

            case "KWD":
                return "د.ك";

            case "BHD":
                return ".د.ب";

            case "ZAR":
                return "R";

            case "RUB":
                return "₽";

            case "TRY":
                return "₺";

            case "BRL":
                return "R$";

            case "MXN":
                return "MX$";

            default:
                return "$";
        }
    }

    private void setupButtons() {

        btnBack.setOnClickListener(v -> {

            finish();
        });

        btnHome.setOnClickListener(v -> {

            finish();
        });
    }

    @Override
    protected void onResume() {

        super.onResume();

        Util.hide(this);
    }
}