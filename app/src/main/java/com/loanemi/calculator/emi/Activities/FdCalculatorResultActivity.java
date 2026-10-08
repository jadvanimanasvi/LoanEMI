package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.Util;

import java.text.NumberFormat;
import java.util.Locale;

public class FdCalculatorResultActivity extends AppCompatActivity {

    private ImageView btnBack;
    private ImageView btnHome;
    private TextView tvTitle;
    private TextView tvLoanAmount;
    private TextView tvInterestRate;
    private TextView tvLoanTerm;
    private TextView tvStartDate;
    private TextView tvMonthlyPayment;
    private TextView tvTotalPayment;
    private TextView tvTotalInterest;
    private TextView tvPayOffDate;
    private FrameLayout nativeContainer;
    private double investmentAmount;
    private double interestRate;
    private double totalInterest;
    private double maturityAmount;
    private int investmentTerm;
    private int compoundingFrequency;
    private String investmentTermUnit;
    private String startDate;
    private String maturityDate;
    private String currencyCode;
    private String currencySymbol;
    private String currencyCountry;
    private int currencyFlag;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fd_calculator_result);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        getIntentData();

        setupToolbar();

        displayResult();
    }

    private void initViews() {

        // Toolbar
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
        tvTitle = findViewById(R.id.tvTitle);

        // Investment information
        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTerm = findViewById(R.id.tvLoanTerm);
        tvStartDate = findViewById(R.id.tvStartDate);

        // Result
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);

        tvTotalPayment = findViewById(R.id.tvTotalPayment);

        tvTotalInterest = findViewById(R.id.tvTotalInterest);

        tvPayOffDate = findViewById(R.id.tvPayOffDate);

        // Native ad
        nativeContainer = findViewById(R.id.nativeContainer);
    }


    private void getIntentData() {

        Intent intent = getIntent();

        investmentAmount = intent.getDoubleExtra("investment_amount", 0.0);

        interestRate = intent.getDoubleExtra("interest_rate", 0.0);

        investmentTerm = intent.getIntExtra("investment_term", 0);

        investmentTermUnit = intent.getStringExtra("investment_term_unit");

        compoundingFrequency = intent.getIntExtra("compounding_frequency", 1);

        totalInterest = intent.getDoubleExtra("total_interest", 0.0);

        maturityAmount = intent.getDoubleExtra("maturity_amount", 0.0);

        startDate = intent.getStringExtra("start_date");

        maturityDate = intent.getStringExtra("maturity_date");

        currencyCode = intent.getStringExtra("currency_code");

        currencySymbol = intent.getStringExtra("currency_symbol");

        currencyCountry = intent.getStringExtra("currency_country");

        currencyFlag = intent.getIntExtra("currency_flag", R.drawable.icn_flagus);


        if (investmentTermUnit == null || investmentTermUnit.trim().isEmpty()) {

            investmentTermUnit = "Month";
        }

        if (startDate == null || startDate.trim().isEmpty()) {

            startDate = "-";
        }

        if (maturityDate == null || maturityDate.trim().isEmpty()) {

            maturityDate = "-";
        }

        if (currencyCode == null || currencyCode.trim().isEmpty()) {

            currencyCode = "USD";
        }

        if (currencySymbol == null || currencySymbol.trim().isEmpty()) {

            currencySymbol = getCurrencySymbol(currencyCode);
        }
    }

    private void setupToolbar() {

        tvTitle.setText("FD Result");

        btnBack.setOnClickListener(v -> finish());

        btnHome.setOnClickListener(v -> {

            Intent intent = new Intent(FdCalculatorResultActivity.this, FdCalculatorActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            startActivity(intent);

            finish();
        });
    }

    private void displayResult() {


        tvLoanAmount.setText(formatCurrency(investmentAmount));

        tvInterestRate.setText(formatDecimal(interestRate) + "%");

        String termText = investmentTerm + " " + investmentTermUnit;

        tvLoanTerm.setText(termText);

        tvStartDate.setText(startDate);

        tvMonthlyPayment.setText(formatCurrency(investmentAmount));

        tvTotalPayment.setText(formatCurrency(totalInterest));

        tvTotalInterest.setText(formatCurrency(maturityAmount));

        tvPayOffDate.setText(maturityDate);
    }

    private String formatCurrency(double amount) {

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);


        return formatter.format(amount) + currencySymbol;
    }

    private String formatDecimal(double value) {

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(value);
    }


    private String getCurrencySymbol(String currencyCode) {

        if (currencyCode == null) {
            return "$";
        }

        switch (currencyCode.toUpperCase(Locale.US)) {

            case "USD": return "$";

            case "GBP": return "£";

            case "CNY": return "¥";

            case "INR": return "₹";

            case "VND": return "₫";

            case "THB": return "฿";

            case "IDR": return "Rp";

            case "EUR": return "€";

            case "JPY": return "¥";

            case "KRW": return "₩";

            case "AUD": return "A$";

            case "CAD": return "C$";

            case "SGD": return "S$";

            case "HKD": return "HK$";

            case "NZD": return "NZ$";

            case "AED": return "د.إ";

            case "SAR": return "﷼";

            case "MYR": return "RM";

            case "PHP": return "₱";

            case "BRL": return "R$";

            case "ZAR": return "R";

            case "TRY": return "₺";

            case "CHF": return "CHF";

            case "PLN": return "zł";

            case "SEK": return "kr";

            case "NOK": return "kr";

            case "DKK": return "kr";

            default: return "$";
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        Util.hide(this);
    }
}