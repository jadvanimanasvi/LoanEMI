package com.loanemi.calculator.emi.Activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.utils.Util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

public class PersonalLoanResultActivity extends AppCompatActivity {

    private ImageView btnBack;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_personal_loan_result);

        initViews();

        loadLoanResult();
    }

    private void initViews() {

        btnBack = findViewById(R.id.btnBack);

        tvLoanAmount = findViewById(R.id.tvLoanAmount);

        tvInterestRate = findViewById(R.id.tvInterestRate);

        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvStartDate = findViewById(R.id.tvStartDate);

        tvMonthlyPayment = findViewById(R.id.tvMonthlyPayment);

        tvTotalInterest = findViewById(R.id.tvTotalInterest);

        tvTotalPayment = findViewById(R.id.tvTotalPayment);

        tvPayOffDate = findViewById(R.id.tvPayOffDate);


        btnBack.setOnClickListener(v -> finish());
    }

    private void loadLoanResult() {

        double loanAmount = getIntent().getDoubleExtra("loan_amount", 0);

        double interestRate = getIntent().getDoubleExtra("interest_rate", 0);

        double loanTerm = getIntent().getDoubleExtra("loan_term", 0);

        String loanTermUnit = getIntent().getStringExtra("loan_term_unit");

        String startDate = getIntent().getStringExtra("start_date");

        String receivedCurrency = getIntent().getStringExtra("currency_code");

        if (receivedCurrency != null && !receivedCurrency.trim().isEmpty()) {

            selectedCurrencyCode = receivedCurrency.trim().toUpperCase(Locale.US);

        } else {

            selectedCurrencyCode = "USD";
        }

        int totalMonths = getIntent().getIntExtra("total_months", 0);

        if (totalMonths <= 0) {

            if (loanTermUnit != null && (loanTermUnit.equalsIgnoreCase("Year") || loanTermUnit.equalsIgnoreCase("Years"))) {

                totalMonths = (int) Math.round(loanTerm * 12);

            } else {

                totalMonths = (int) Math.round(loanTerm);
            }
        }

        double monthlyPayment = getIntent().getDoubleExtra("monthly_emi", 0);

        double totalInterest = getIntent().getDoubleExtra("total_interest", 0);

        double totalPayment = getIntent().getDoubleExtra("total_payment", 0);

        tvLoanAmount.setText(formatCurrency(loanAmount));

        tvInterestRate.setText(formatDecimal(interestRate) + "%");

        String termText;

        if (loanTermUnit != null && (loanTermUnit.equalsIgnoreCase("Year") || loanTermUnit.equalsIgnoreCase("Years"))) {

            if (loanTerm == 1) {

                termText = formatDecimal(loanTerm) + " Year";

            } else {

                termText = formatDecimal(loanTerm) + " Years";
            }

        } else {

            if (loanTerm == 1) {

                termText = formatDecimal(loanTerm) + " Month";

            } else {

                termText = formatDecimal(loanTerm) + " Months";
            }
        }

        tvLoanTerm.setText(termText);

        if (startDate != null && !startDate.isEmpty()) {

            tvStartDate.setText(startDate);

        } else {

            tvStartDate.setText("-");
        }

        String payoffDate = calculatePayoffDate(startDate, totalMonths);

        tvPayOffDate.setText(payoffDate);

        tvMonthlyPayment.setText(formatCurrency(monthlyPayment));

        tvTotalInterest.setText(formatCurrency(totalInterest));

        tvTotalPayment.setText(formatCurrency(totalPayment));
    }

    private String formatDecimal(double value) {

        if (value == (long) value) {

            return String.format(Locale.getDefault(), "%d", (long) value);
        }

        return String.format(Locale.getDefault(), "%.2f", value);
    }

    private String formatCurrency(double value) {

        long roundedValue = Math.round(value);


        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.US);

        try {

            Currency currency = Currency.getInstance(selectedCurrencyCode);

            format.setCurrency(currency);

        } catch (Exception e) {

            Currency currency = Currency.getInstance("USD");

            format.setCurrency(currency);
        }

        format.setMaximumFractionDigits(0);

        format.setMinimumFractionDigits(0);


        String number = format.format(roundedValue);


        String currencySymbol = format.getCurrency().getSymbol(Locale.US);

        if (number.startsWith(currencySymbol)) {

            number = number.substring(currencySymbol.length()).trim();
        }


        return number + currencySymbol;
    }

    private String calculatePayoffDate(String startDate, int totalMonths) {

        if (startDate == null || startDate.trim().isEmpty()) {

            return "-";
        }

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

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