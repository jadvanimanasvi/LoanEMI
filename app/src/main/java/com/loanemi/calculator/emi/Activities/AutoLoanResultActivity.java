package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Context;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
    private ImageView ivBack,btnHome;

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
        btnHome = findViewById(R.id.btnHome);

        tvLoanAmount = findViewById(R.id.tvLoanAmount);
        tvInterestRate = findViewById(R.id.tvInterestRate);
        tvLoanTerm = findViewById(R.id.tvLoanTerm);
        tvStartDate = findViewById(R.id.tvStartDate);
        tvPayOffDate = findViewById(R.id.tvPayOffDate);
        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);
        tvTotalInterest = findViewById(R.id.tvTotalInterest);
        tvTotalPayment = findViewById(R.id.tvTotalPayment);

        ivBack.setOnClickListener(v -> finish());
        btnHome.setOnClickListener(v -> finish());
    }

    private void setUpAds() {
        if (Util.isInternetAvailable(this)) {
            FrameLayout nativeLayout = findViewById(R.id.autoLoan_native_layout);

            ShimmerFrameLayout shimmerNative = findViewById(R.id.nativeShimmerLayout);

            NativeAdPreloader.show(this, nativeLayout, getString(R.string.auto_loan_result_native_medium), shimmerNative);
        }
    }

    private void loadResult() {
        double loanAmount = getIntent().getDoubleExtra("loan_amount", 0);

        double interestRate = getIntent().getDoubleExtra("interest_rate", 0);

        int loanTerm = getIntent().getIntExtra("loan_term", 0);

        String loanTermUnit = getIntent().getStringExtra("loan_term_unit");

        int totalMonths = getIntent().getIntExtra("total_months", 0);

        double monthlyEMI = getIntent().getDoubleExtra("monthly_emi", 0);

        double totalInterest = getIntent().getDoubleExtra("total_interest", 0);

        double totalPayment = getIntent().getDoubleExtra("total_payment", 0);

        String startDate = getIntent().getStringExtra("start_date");

        String payoffDate = getIntent().getStringExtra("payoff_date");

        String currencyCode = getIntent().getStringExtra("currency_code");

        if (currencyCode == null || currencyCode.trim().isEmpty()) {
            currencyCode = "USD";
        }

        if (loanTermUnit == null || loanTermUnit.trim().isEmpty()) {
            loanTermUnit = "Month";
        }

        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("en", "IN"));

        formatter.setMaximumFractionDigits(2);
        formatter.setMinimumFractionDigits(0);

        tvLoanAmount.setText(formatCurrency(loanAmount, currencyCode, formatter));

        tvInterestRate.setText(formatter.format(interestRate) + "%");

        if (totalMonths > 0) {
            if (totalMonths % 12 == 0) {
                int years = totalMonths / 12;

                tvLoanTerm.setText(years + (years == 1 ? " Year" : " Years"));
            } else {
                tvLoanTerm.setText(totalMonths + (totalMonths == 1 ? " Month" : " Months"));
            }
        } else {
            tvLoanTerm.setText(loanTerm + " " + loanTermUnit);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            tvStartDate.setText(startDate);
        } else {
            tvStartDate.setText("N/A");
        }

        if (payoffDate == null || payoffDate.trim().isEmpty()) {
            payoffDate = calculatePayoffDate(startDate, totalMonths);
        }

        if (payoffDate != null && !payoffDate.trim().isEmpty()) {
            tvPayOffDate.setText(payoffDate);
        } else {
            tvPayOffDate.setText("N/A");
        }

        tvMonthlyPayment.setText(formatCurrency(monthlyEMI, currencyCode, formatter));

        tvTotalInterest.setText(formatCurrency(totalInterest, currencyCode, formatter));

        tvTotalPayment.setText(formatCurrency(totalPayment, currencyCode, formatter));
    }

    private String calculatePayoffDate(String startDate, int totalMonths) {
        if (startDate == null || startDate.trim().isEmpty() || totalMonths <= 0) {
            return "";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        sdf.setLenient(false);

        try {
            Date date = sdf.parse(startDate);

            if (date == null) {
                return "";
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, totalMonths);

            return sdf.format(calendar.getTime());

        } catch (ParseException e) {
            return "";
        }
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
                return "Rp ";
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
                return "CHF ";
            case "AED":
                return "د.إ ";
            case "SAR":
                return "﷼";
            default:
                return "$";
        }
    }

}
