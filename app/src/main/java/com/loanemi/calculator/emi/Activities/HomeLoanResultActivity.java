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
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.Util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeLoanResultActivity extends AppCompatActivity {

    private TextView tvLoanAmount;
    private TextView tvInterestRate;
    private TextView tvLoanTerm;
    private TextView tvStartDate;
    private TextView tvMonthlyPayment;
    private TextView tvTotalPayment;
    private TextView tvTotalInterest;
    private TextView tvPayOffDate;
    private ImageView btnBack;
    private ImageView btnHome;
    private double loanAmount;
    private double downPayment;
    private double principal;
    private double interestRate;
    private double loanTerm;
    private int totalMonths;
    private double monthlyEMI;
    private double totalPayment;
    private double totalInterest;

    private String loanTermUnit;
    private String currencyCode;
    private String currencySymbol;

    private final NumberFormat indianNumberFormat =
            NumberFormat.getNumberInstance(new Locale("en", "IN"));

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_loan_result);

        setupEdgeToEdge(this, R.id.main);

        initViews();
        readIntentData();
        setUpAds();
        showResults();
        setupButtons();
    }

    private void initViews() {
        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTerm = findViewById(R.id.tvLoanTerm);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvPayOffDate = findViewById(R.id.tvPayOffDate);
        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);
    }

    private void setUpAds() {
        if (Util.isInternetAvailable(this)) {
            FrameLayout nativeLayout = findViewById(R.id.home_loan_native_layout);
            ShimmerFrameLayout shimmerNative = findViewById(R.id.nativeShimmerLayout);

            if (nativeLayout != null && shimmerNative != null) {
                NativeAdPreloader.show(
                        this,
                        nativeLayout,
                        getString(R.string.home_loan_result_native_medium),
                        shimmerNative
                );
            }
        }
    }

    private void readIntentData() {
        Intent intent = getIntent();

        loanAmount = intent.getDoubleExtra("loan_amount", 0);
        downPayment = intent.getDoubleExtra("down_payment", 0);
        principal = intent.getDoubleExtra("principal", loanAmount);
        interestRate = intent.getDoubleExtra("interest_rate", 0);
        loanTerm = intent.getDoubleExtra("loan_term", 0);
        totalMonths = intent.getIntExtra("total_months", 0);
        monthlyEMI = intent.getDoubleExtra("monthly_emi", 0);
        totalPayment = intent.getDoubleExtra("total_payment", 0);
        totalInterest = intent.getDoubleExtra("total_interest", 0);

        loanTermUnit = intent.getStringExtra("loan_term_unit");
        if (loanTermUnit == null) {
            loanTermUnit = "Months";
        }

        currencyCode = intent.getStringExtra("currency_code");
        if (currencyCode == null) {
            currencyCode = "USD";
        }

        currencySymbol = intent.getStringExtra("currency_symbol");
        if (currencySymbol == null || currencySymbol.trim().isEmpty()) {
            currencySymbol = getCurrencySymbol(currencyCode);
        }

        indianNumberFormat.setGroupingUsed(true);
        indianNumberFormat.setMaximumFractionDigits(0);
        indianNumberFormat.setMinimumFractionDigits(0);
    }

    private void showResults() {
        tvLoanAmount.setText(formatMoney(loanAmount));
        tvInterestRate.setText(formatRate(interestRate));
        tvLoanTerm.setText(formatLoanTerm());

        long startDateMillis = getIntent().getLongExtra(
                "start_date_millis",
                System.currentTimeMillis()
        );

        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(startDateMillis);

        tvStartDate.setText(formatDate(startCalendar));
        tvMonthlyPayment.setText(formatMoney(monthlyEMI));
        tvTotalPayment.setText(formatMoney(totalPayment));
        tvTotalInterest.setText(formatMoney(totalInterest));

        long payoffDateMillis = getIntent().getLongExtra("payoff_date_millis", 0);

        Calendar payoffCalendar = Calendar.getInstance();

        if (payoffDateMillis > 0) {
            payoffCalendar.setTimeInMillis(payoffDateMillis);
        } else {
            payoffCalendar.setTimeInMillis(startDateMillis);
            payoffCalendar.add(Calendar.MONTH, totalMonths);
        }

        tvPayOffDate.setText(formatDate(payoffCalendar));
    }

    private String formatMoney(double amount) {
        return indianNumberFormat.format(Math.round(amount)) + currencySymbol;
    }

    private String formatRate(double rate) {
        return String.format(Locale.getDefault(), "%.2f%%", rate);
    }

    private String formatLoanTerm() {
        if (loanTerm == (long) loanTerm) {
            return String.format(
                    Locale.getDefault(),
                    "%d %s",
                    (long) loanTerm,
                    loanTermUnit
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.2f %s",
                loanTerm,
                loanTermUnit
        );
    }

    private String formatDate(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(calendar.getTime());
    }

    private String getCurrencySymbol(String code) {
        if (code == null || code.trim().isEmpty()) {
            return "$";
        }

        switch (code.toUpperCase(Locale.US)) {
            case "USD":
                return "$";
            case "INR":
                return "₹";
            case "GBP":
                return "£";
            case "EUR":
                return "€";
            case "JPY":
            case "CNY":
                return "¥";
            case "KRW":
                return "₩";
            case "THB":
                return "฿";
            case "VND":
                return "₫";
            case "IDR":
                return "Rp";
            case "AUD":
                return "A$";
            case "CAD":
                return "C$";
            case "SGD":
                return "S$";
            case "HKD":
                return "HK$";
            case "NZD":
                return "NZ$";
            case "CHF":
                return "CHF";
            case "MYR":
                return "RM";
            case "AED":
                return "د.إ";
            case "SAR":
                return "﷼";
            default:
                return "$";
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
