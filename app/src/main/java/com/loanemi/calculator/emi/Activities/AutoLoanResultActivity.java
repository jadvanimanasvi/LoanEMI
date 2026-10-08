package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Context;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.loanemi.calculator.emi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.loanemi.calculator.emi.Ads.NativeAdPreloader;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.Util;
import java.text.NumberFormat;
import java.util.Locale;

public class AutoLoanResultActivity extends AppCompatActivity {

    private TextView tvLoanAmount;
    private TextView tvInterestRate;
    private TextView tvLoanTerm;
    private TextView tvStartDate;
    private TextView tvPayOffDate;
    private TextView tvMonthlyPayment;
    private TextView tvTotalInterest;
    private TextView tvTotalPayment;
    private ImageView ivBack;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auto_loan_result);

        setupEdgeToEdge(this, R.id.main);

        initViews();
        setUpAds();
        loadResult();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Util.hide(this);
    }

    private void initViews() {

        ivBack = findViewById(R.id.btnBack);

        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTerm = findViewById(R.id.tvLoanTerm);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvPayOffDate = findViewById(R.id.tvPayOffDate);
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);

        ivBack.setOnClickListener(v -> finish());
    }

    private void setUpAds() {
        if (Util.isInternetAvailable(this)) {
            FrameLayout nativeLayout = findViewById(R.id.autoLoan_native_layout);
            ShimmerFrameLayout shimmerNative = findViewById(R.id.nativeShimmerLayout);

            NativeAdPreloader.show(
                    this,
                    nativeLayout,
                    getString(R.string.auto_loan_result_native_medium),
                    shimmerNative
            );
        }
    }

    private void loadResult() {

        // Get values from AutoLoanActivity
        double loanAmount = getIntent().getDoubleExtra("loan_amount", 0);

        double interestRate = getIntent().getDoubleExtra("interest_rate", 0);

        int loanTerm = getIntent().getIntExtra("loan_term", 0);

        String loanTermUnit = getIntent().getStringExtra("loan_term_unit");

        double monthlyEMI = getIntent().getDoubleExtra("monthly_emi", 0);

        double totalInterest = getIntent().getDoubleExtra("total_interest", 0);

        double totalPayment = getIntent().getDoubleExtra("total_payment", 0);

        String startDate = getIntent().getStringExtra("start_date");

        String payoffDate = getIntent().getStringExtra("payoff_date");

        String currencyCode = getIntent().getStringExtra("currency_code");

        // Default currency
        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            currencyCode = "USD";
        }

        // Default loan unit
        if (loanTermUnit == null || loanTermUnit.trim().isEmpty()) {
            loanTermUnit = "Month";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("en", "IN"));

        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);

        // Amount
        tvLoanAmount.setText(formatCurrency(loanAmount, currencyCode, formatter));

        // Interest rate
        tvInterestRate.setText(formatter.format(interestRate) + "%");

        // Loan term
        tvLoanTerm.setText(loanTerm + " " + loanTermUnit);

        // Start date
        if (startDate != null && !startDate.trim().isEmpty()) {
            tvStartDate.setText(startDate);
        }

        // Payoff date
        if (payoffDate != null && !payoffDate.trim().isEmpty()) {
            tvPayOffDate.setText(payoffDate);
        }

        // Monthly payment
        tvMonthlyPayment.setText(formatCurrency(monthlyEMI, currencyCode, formatter));

        // Total interest
        tvTotalInterest.setText(formatCurrency(totalInterest, currencyCode, formatter));

        // Total payment
        tvTotalPayment.setText(formatCurrency(totalPayment, currencyCode, formatter));
    }

    private String formatCurrency(double amount, String currencyCode, NumberFormat formatter) {

        String formattedAmount = formatter.format(amount);

        String currencySymbol = getCurrencySymbol(currencyCode);

        return formattedAmount + currencySymbol;
    }

    private String getCurrencySymbol(String currencyCode) {

        if (currencyCode == null || currencyCode.trim().isEmpty()) {

            return "$";
        }

        switch (currencyCode.toUpperCase(Locale.US)) {

            case "USD": return "$";

            case "INR": return "₹";

            case "GBP": return "£";

            case "EUR": return "€";

            case "JPY": return "¥";

            case "CNY": return "¥";

            case "KRW": return "₩";

            case "THB": return "฿";

            case "VND": return "₫";

            case "IDR": return "Rp";

            case "AUD": return "A$";

            case "CAD": return "C$";

            case "SGD": return "S$";

            case "HKD": return "HK$";

            case "NZD": return "NZ$";

            case "CHF": return "CHF";

            default: return "$";
        }
    }

}