package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.emi.Ads.AdsHelper;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.utils.LoanHistoryManager;
import com.loanemi.calculator.emi.utils.Util;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class PersonalLoanActivity extends AppCompatActivity {

    private static final int REQUEST_CURRENCY = 1001;

    private AdView bannerAdView;
    private FrameLayout adContainer;

    private EditText etLoanAmount;
    private EditText etInterestRate;
    private EditText tvLoanTerm;

    private TextView tvLoanUnit;
    private TextView tvCurrencyCode;

    private ImageView ivFromFlag;
    private ImageView ivBack;

    private LinearLayout layoutCurrency;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;

    private String selectedLoanUnit = "Month";

    private String selectedCurrencyCode = "USD";
    private String selectedCurrencyName = "US Dollar";
    private String selectedCurrencySymbol = "$";

    private int selectedCurrencyFlag = R.drawable.icn_flagus;
    private int selectedCurrencyIcon = R.drawable.usd_currency;

    private final NumberFormat indianNumberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_personal_loan);

        setupEdgeToEdge(this, R.id.main);

        initViews();
        setupDefaultValues();
        setupAmountFormatting();
        setupLoanTermUnit();
        setupListeners();
        setUpAd();
    }

    private void initViews() {

        etLoanAmount = findViewById(R.id.etLoanAmount);
        etInterestRate = findViewById(R.id.etInterestRate);
        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvLoanUnit = findViewById(R.id.tvLoanUnit);
        tvCurrencyCode = findViewById(R.id.tvCurrencyCode);

        ivFromFlag = findViewById(R.id.ivFromFlag);
        ivBack = findViewById(R.id.ivBack);

        layoutCurrency = findViewById(R.id.layoutCurrency);

        btnReset = findViewById(R.id.btnReset);
        btnCalculate = findViewById(R.id.btnCalculate);

        adContainer = findViewById(R.id.bannerContainer);
    }

    private void setupDefaultValues() {

        selectedCurrencyCode = "USD";
        selectedCurrencyName = "US Dollar";
        selectedCurrencySymbol = "$";

        selectedCurrencyFlag = R.drawable.icn_flagus;
        selectedCurrencyIcon = R.drawable.usd_currency;

        selectedLoanUnit = "Month";

        if (tvCurrencyCode != null) {
            tvCurrencyCode.setText(selectedCurrencyCode);
        }

        if (ivFromFlag != null) {
            ivFromFlag.setImageResource(selectedCurrencyFlag);
        }

        if (tvLoanUnit != null) {
            tvLoanUnit.setText(selectedLoanUnit);
        }
    }

    private void setupListeners() {

        if (ivBack != null) {
            ivBack.setOnClickListener(v -> finish());
        }

        if (layoutCurrency != null) {
            layoutCurrency.setOnClickListener(v -> openCurrencyActivity());
        }

        if (btnReset != null) {
            btnReset.setOnClickListener(v -> resetFields());
        }

        if (btnCalculate != null) {
            btnCalculate.setOnClickListener(v -> calculateLoan());
        }
    }

    private void setupAmountFormatting() {

        if (etLoanAmount == null) {
            return;
        }

        etLoanAmount.addTextChangedListener(new TextWatcher() {

            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (isFormatting) {
                    return;
                }

                isFormatting = true;

                try {

                    String cleanInput = editable.toString().replace(",", "").replaceAll("[^0-9]", "");

                    if (!cleanInput.isEmpty()) {

                        try {

                            long value = Long.parseLong(cleanInput);

                            String formatted = indianNumberFormat.format(value);

                            if (!formatted.equals(editable.toString())) {

                                etLoanAmount.setText(formatted);

                                etLoanAmount.setSelection(formatted.length());
                            }

                        } catch (NumberFormatException ignored) {
                        }

                    } else {

                        etLoanAmount.setText("");
                    }

                } finally {

                    isFormatting = false;
                }
            }
        });
    }

    private void setupLoanTermUnit() {

        if (tvLoanUnit == null) {
            return;
        }

        selectedLoanUnit = "Month";

        tvLoanUnit.setText(selectedLoanUnit);

        tvLoanUnit.setOnClickListener(v -> showLoanUnitDialog());
    }

    private void showLoanUnitDialog() {

        final String[] units = {"Month", "Year"};

        int selectedPosition = selectedLoanUnit.equalsIgnoreCase("Year") ? 1 : 0;

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Select Loan Term Unit").setSingleChoiceItems(units, selectedPosition, (dialogInterface, which) -> {

            selectedLoanUnit = units[which];

            tvLoanUnit.setText(selectedLoanUnit);

            dialogInterface.dismiss();
        }).create();

        dialog.show();
    }

    private void openCurrencyActivity() {

        Intent intent = new Intent(PersonalLoanActivity.this, CurrencyUnitActivity.class);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "PERSONAL_LOAN");

        startActivityForResult(intent, REQUEST_CURRENCY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CURRENCY || resultCode != RESULT_OK || data == null) {
            return;
        }

        String code = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE);

        String name = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_COUNTRY);

        int flag = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_FLAG, 0);

        int currencyIcon = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_CURRENCY_ICON, 0);

        if (code != null && !code.trim().isEmpty()) {

            selectedCurrencyCode = code.trim().toUpperCase(Locale.US);

            if (tvCurrencyCode != null) {
                tvCurrencyCode.setText(selectedCurrencyCode);
            }

            selectedCurrencySymbol = getCurrencySymbol(selectedCurrencyCode);
        }

        if (name != null && !name.trim().isEmpty()) {

            selectedCurrencyName = name.trim();
        }

        if (flag != 0) {

            selectedCurrencyFlag = flag;

            if (ivFromFlag != null) {
                ivFromFlag.setImageResource(selectedCurrencyFlag);
            }
        }

        if (currencyIcon != 0) {
            selectedCurrencyIcon = currencyIcon;
        }
    }

    private String getCurrencySymbol(String code) {

        if (code == null) {
            return "$";
        }

        switch (code.toUpperCase(Locale.US)) {

            case "GBP":
                return "£";

            case "USD":
                return "$";

            case "CNY":
                return "¥";

            case "INR":
                return "₹";

            case "VND":
                return "₫";

            case "THB":
                return "฿";

            case "IDR":
                return "Rp";

            case "AUD":
                return "A$";

            case "CAD":
                return "C$";

            case "EUR":
                return "€";

            case "JPY":
                return "¥";

            case "KRW":
                return "₩";

            case "SGD":
                return "S$";

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

    private void calculateLoan() {

        String amountText = etLoanAmount.getText().toString().trim().replace(",", "");

        if (amountText.isEmpty()) {

            etLoanAmount.setError("Enter loan amount");

            etLoanAmount.requestFocus();

            return;
        }

        double principal;

        try {

            principal = Double.parseDouble(amountText);

        } catch (Exception e) {

            etLoanAmount.setError("Enter a valid loan amount");

            etLoanAmount.requestFocus();

            return;
        }

        if (principal <= 0) {

            etLoanAmount.setError("Loan amount must be greater than 0");

            etLoanAmount.requestFocus();

            return;
        }

        String interestText = etInterestRate.getText().toString().trim();

        if (interestText.isEmpty()) {

            etInterestRate.setError("Enter interest rate");

            etInterestRate.requestFocus();

            return;
        }

        double annualRate;

        try {

            annualRate = Double.parseDouble(interestText);

        } catch (Exception e) {

            etInterestRate.setError("Enter a valid interest rate");

            etInterestRate.requestFocus();

            return;
        }

        if (annualRate < 0) {

            etInterestRate.setError("Interest rate cannot be negative");

            etInterestRate.requestFocus();

            return;
        }

        if (annualRate > 100) {

            etInterestRate.setError("Interest rate cannot exceed 100%");

            etInterestRate.requestFocus();

            return;
        }

        String termText = tvLoanTerm.getText().toString().trim();

        if (termText.isEmpty()) {

            tvLoanTerm.setError("Enter loan term");

            tvLoanTerm.requestFocus();

            return;
        }

        int term;

        try {

            term = Integer.parseInt(termText);

        } catch (Exception e) {

            tvLoanTerm.setError("Enter a valid loan term");

            tvLoanTerm.requestFocus();

            return;
        }

        if (term <= 0) {

            tvLoanTerm.setError("Loan term must be greater than 0");

            tvLoanTerm.requestFocus();

            return;
        }

        int totalMonths;

        if (selectedLoanUnit.equalsIgnoreCase("Year")) {

            totalMonths = term * 12;

        } else {

            totalMonths = term;
        }

        if (totalMonths > 360) {

            tvLoanTerm.setError("Loan term cannot exceed 360 months");

            Toast.makeText(this, "Loan term cannot exceed 360 months", Toast.LENGTH_SHORT).show();

            return;
        }

        double monthlyRate = annualRate / 12.0 / 100.0;

        double monthlyEMI;

        if (monthlyRate == 0) {

            monthlyEMI = principal / totalMonths;

        } else {

            double power = Math.pow(1.0 + monthlyRate, totalMonths);

            monthlyEMI = principal * monthlyRate * power / (power - 1.0);
        }

        double totalPayment = monthlyEMI * totalMonths;

        double totalInterest = totalPayment - principal;

        if (totalInterest < 0 && totalInterest > -0.01) {
            totalInterest = 0;
        }

        /*
         * =====================================================
         * HISTORY DATA
         * =====================================================
         */

        Calendar startCalendar = Calendar.getInstance();

        String startDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", startCalendar.get(Calendar.DAY_OF_MONTH), startCalendar.get(Calendar.MONTH) + 1, startCalendar.get(Calendar.YEAR));

        String historyDate = startDate;

        /*
         * =====================================================
         * SAVE HISTORY
         * =====================================================
         */

        LoanHistoryManager historyManager = new LoanHistoryManager(this);

        historyManager.addHistory("Personal Loan", historyDate, principal, annualRate, term, selectedLoanUnit, totalMonths, monthlyEMI, totalInterest, totalPayment, startDate, selectedCurrencyCode, selectedCurrencySymbol, R.drawable.personal_ic);

        /*
         * =====================================================
         * RESULT ACTIVITY
         * =====================================================
         */

        Intent intent = new Intent(PersonalLoanActivity.this, PersonalLoanResultActivity.class);

        intent.putExtra("loan_amount", principal);

        intent.putExtra("interest_rate", annualRate);

        intent.putExtra("loan_term", term);

        intent.putExtra("loan_term_unit", selectedLoanUnit);

        intent.putExtra("total_months", totalMonths);

        intent.putExtra("monthly_emi", monthlyEMI);

        intent.putExtra("total_interest", totalInterest);

        intent.putExtra("total_payment", totalPayment);

        /*
         * Currency
         */

        intent.putExtra("currency_code", selectedCurrencyCode);

        intent.putExtra("currency_name", selectedCurrencyName);

        intent.putExtra("currency_symbol", selectedCurrencySymbol);

        intent.putExtra("currency_icon", selectedCurrencyIcon);

        intent.putExtra("currency_flag", selectedCurrencyFlag);

        intent.putExtra("loan_type", "Personal Loan");

        /*
         * Dates
         */

        intent.putExtra("start_date", startDate);

        Calendar payoffCalendar = (Calendar) startCalendar.clone();

        payoffCalendar.add(Calendar.MONTH, totalMonths);

        String payoffDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", payoffCalendar.get(Calendar.DAY_OF_MONTH), payoffCalendar.get(Calendar.MONTH) + 1, payoffCalendar.get(Calendar.YEAR));

        intent.putExtra("payoff_date", payoffDate);

        intent.putExtra("start_date_millis", startCalendar.getTimeInMillis());

        intent.putExtra("payoff_date_millis", payoffCalendar.getTimeInMillis());

        startActivity(intent);
    }

    private void resetFields() {

        if (etLoanAmount != null) {
            etLoanAmount.setText("");
            etLoanAmount.setError(null);
        }

        if (etInterestRate != null) {
            etInterestRate.setText("");
            etInterestRate.setError(null);
        }

        if (tvLoanTerm != null) {
            tvLoanTerm.setText("");
            tvLoanTerm.setError(null);
        }

        selectedLoanUnit = "Month";

        if (tvLoanUnit != null) {
            tvLoanUnit.setText("Month");
        }

        /*
         * Reset currency
         */

        selectedCurrencyCode = "USD";
        selectedCurrencyName = "US Dollar";
        selectedCurrencySymbol = "$";

        selectedCurrencyFlag = R.drawable.icn_flagus;

        selectedCurrencyIcon = R.drawable.usd_currency;

        if (tvCurrencyCode != null) {
            tvCurrencyCode.setText(selectedCurrencyCode);
        }

        if (ivFromFlag != null) {
            ivFromFlag.setImageResource(selectedCurrencyFlag);
        }

        if (etLoanAmount != null) {
            etLoanAmount.requestFocus();
        }
    }

    private void setUpAd() {

        if (!Util.isInternetAvailable(this) || adContainer == null) {
            return;
        }

        bannerAdView = new AdView(this);

        if (bannerAdView.getParent() != null) {

            ((ViewGroup) bannerAdView.getParent()).removeView(bannerAdView);
        }

        adContainer.addView(bannerAdView);

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.personal_loan_banner));
    }

    @Override
    protected void onResume() {

        super.onResume();

        Util.hide(this);
    }

    @Override
    protected void onDestroy() {

        if (bannerAdView != null) {

            ViewGroup parent = (ViewGroup) bannerAdView.getParent();

            if (parent != null) {
                parent.removeView(bannerAdView);
            }

            bannerAdView.destroy();

            bannerAdView = null;
        }

        super.onDestroy();
    }
}