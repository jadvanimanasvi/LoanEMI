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
import java.util.Currency;
import java.util.Locale;

public class PersonalLoanResultActivity extends AppCompatActivity {

    private ImageView btnBack, btnHome;

    private TextView tvLoanAmount;
    private TextView tvInterestRate;
    private TextView tvLoanTerm;
    private TextView tvStartDate;
    private TextView tvMonthlyPayment;
    private TextView tvTotalInterest;
    private TextView tvTotalPayment;
    private TextView tvPayOffDate;

    private String selectedCurrencyCode = "USD";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_loan_result);

        setupEdgeToEdge(this, R.id.main);

        initViews();
        setUpAds();
        loadLoanResult();
    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);
        btnHome = findViewById(R.id.btnHome);

        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTerm = findViewById(R.id.tvLoanTerm);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);
        tvPayOffDate = findViewById(R.id.tvPayOffDate);

        btnBack.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> finish());
    }

    private void setUpAds() {

        if (Util.isInternetAvailable(this)) {

            FrameLayout nativeLayout =
                    findViewById(R.id.personal_loan_native_layout);

            ShimmerFrameLayout shimmerNative =
                    findViewById(R.id.nativeShimmerLayout);

            NativeAdPreloader.show(
                    this,
                    nativeLayout,
                    getString(R.string.personal_loan_result_native_medium),
                    shimmerNative
            );
        }
    }

    private void loadLoanResult() {

        Intent intent = getIntent();

        double loanAmount =
                intent.getDoubleExtra("loan_amount", 0);

        double interestRate =
                intent.getDoubleExtra("interest_rate", 0);

        // FIX 1: Read loan_term as int, because the first Activity
        // sends it using intent.putExtra("loan_term", term).
        int loanTerm =
                intent.getIntExtra("loan_term", 0);

        String loanTermUnit =
                intent.getStringExtra("loan_term_unit");

        String startDate =
                intent.getStringExtra("start_date");

        String receivedCurrency =
                intent.getStringExtra("currency_code");

        if (receivedCurrency != null
                && !receivedCurrency.trim().isEmpty()) {

            selectedCurrencyCode =
                    receivedCurrency.trim().toUpperCase(Locale.US);

        } else {

            selectedCurrencyCode = "USD";
        }

        int totalMonths =
                intent.getIntExtra("total_months", 0);

        // FIX 2: Calculate total months if missing.
        if (totalMonths <= 0 && loanTerm > 0) {

            if ("Year".equalsIgnoreCase(loanTermUnit)
                    || "Years".equalsIgnoreCase(loanTermUnit)) {

                totalMonths = loanTerm * 12;

            } else {

                totalMonths = loanTerm;
            }
        }

        double monthlyPayment =
                intent.getDoubleExtra("monthly_emi", 0);

        double totalInterest =
                intent.getDoubleExtra("total_interest", 0);

        double totalPayment =
                intent.getDoubleExtra("total_payment", 0);

        // Loan amount
        tvLoanAmount.setText(formatCurrency(loanAmount));

        // Interest rate
        tvInterestRate.setText(formatDecimal(interestRate) + "%");

        // FIX 3: Display the original entered term with its unit.
        String unit;

        if ("Year".equalsIgnoreCase(loanTermUnit)
                || "Years".equalsIgnoreCase(loanTermUnit)) {

            unit = "Year";

        } else {

            unit = "Month";
        }

        String termText;

        if (loanTerm > 0) {

            termText = loanTerm + " " + unit;

            if (loanTerm != 1) {
                termText += "s";
            }

        } else if (totalMonths > 0) {

            // Fallback when the original loan term is unavailable.
            if (totalMonths % 12 == 0) {

                int years = totalMonths / 12;

                termText = years + (years == 1 ? " Year" : " Years");

            } else {

                termText = totalMonths
                        + (totalMonths == 1 ? " Month" : " Months");
            }

        } else {

            termText = "-";
        }

        tvLoanTerm.setText(termText);

        // Start date
        if (startDate != null && !startDate.trim().isEmpty()) {

            tvStartDate.setText(startDate);

        } else {

            tvStartDate.setText("-");
        }

        // Payoff date
        tvPayOffDate.setText(
                calculatePayoffDate(startDate, totalMonths)
        );

        // Payment details
        tvMonthlyPayment.setText(formatCurrency(monthlyPayment));
        tvTotalInterest.setText(formatCurrency(totalInterest));
        tvTotalPayment.setText(formatCurrency(totalPayment));
    }

    private String formatDecimal(double value) {

        if (value == (long) value) {

            return String.format(
                    Locale.getDefault(),
                    "%d",
                    (long) value
            );
        }

        return String.format(
                Locale.getDefault(),
                "%.2f",
                value
        );
    }

    private String formatCurrency(double value) {

        NumberFormat format =
                NumberFormat.getCurrencyInstance(Locale.US);

        Currency currency;

        try {

            currency = Currency.getInstance(selectedCurrencyCode);

        } catch (Exception e) {

            currency = Currency.getInstance("USD");
        }

        format.setCurrency(currency);
        format.setMaximumFractionDigits(0);
        format.setMinimumFractionDigits(0);

        String number = format.format(Math.round(value));

        String currencySymbol = currency.getSymbol(Locale.US);

        if (number.startsWith(currencySymbol)) {

            number = number.substring(currencySymbol.length()).trim();
        }

        return number + currencySymbol;
    }

    private String calculatePayoffDate(
            String startDate,
            int totalMonths
    ) {

        if (startDate == null || startDate.trim().isEmpty()) {
            return "-";
        }

        try {

            SimpleDateFormat sdf =
                    new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            sdf.setLenient(false);

            Calendar calendar = Calendar.getInstance();

            calendar.setTime(sdf.parse(startDate));

            calendar.add(Calendar.MONTH, totalMonths);

            return sdf.format(calendar.getTime());

        } catch (Exception e) {

            return "-";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.hide(this);
    }
}