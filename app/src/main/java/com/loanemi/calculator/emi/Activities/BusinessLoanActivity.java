package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BusinessLoanActivity extends AppCompatActivity {

    private static final int REQUEST_CURRENCY = 1001;

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

    private FrameLayout bannerContainer;
    private AdView bannerAdView;

    private String selectedCurrencyCode = "USD";
    private String selectedCurrencyName = "US Dollar";

    private int selectedCurrencyFlag = R.drawable.icn_flagus;
    private int selectedCurrencyIcon = R.drawable.usd_currency;

    private String selectedLoanUnit = "Month";

    private final NumberFormat indianNumberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));

    private LoanHistoryManager loanHistoryManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_business_loan);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        loanHistoryManager = new LoanHistoryManager(this);

        setupDefaultValues();
        setupLoanAmountFormatting();
        setupLoanUnit();
        setupListeners();
        setupBanner();
    }


    @Override
    protected void onResume() {
        super.onResume();

        Util.hide(this);
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

        bannerContainer = findViewById(R.id.bannerContainer);
    }


    private void setupDefaultValues() {

        selectedCurrencyCode = "USD";
        selectedCurrencyName = "US Dollar";

        selectedCurrencyFlag = R.drawable.icn_flagus;
        selectedCurrencyIcon = R.drawable.usd_currency;

        if (tvCurrencyCode != null) {
            tvCurrencyCode.setText(selectedCurrencyCode);
        }

        if (ivFromFlag != null) {
            ivFromFlag.setImageResource(selectedCurrencyFlag);
        }

        selectedLoanUnit = "Month";

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


    private void openCurrencyActivity() {

        Intent intent = new Intent(BusinessLoanActivity.this, CurrencyUnitActivity.class);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "BUSINESS_LOAN");

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


    private void setupLoanAmountFormatting() {

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


    private void setupLoanUnit() {

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


        if (totalMonths <= 0) {

            Toast.makeText(this, "Invalid loan term", Toast.LENGTH_SHORT).show();

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


        // =====================================================
        // SAVE HISTORY
        // =====================================================

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());


        String currencySymbol = getCurrencySymbol(selectedCurrencyCode);


        loanHistoryManager.addHistory(

                "Business Loan",

                currentDate,

                principal,

                annualRate,

                term,

                selectedLoanUnit,

                totalMonths,

                monthlyEMI,

                totalInterest,

                totalPayment,

                currentDate,

                selectedCurrencyCode,

                currencySymbol,

                R.drawable.business_ic);


        // =====================================================
        // OPEN RESULT
        // =====================================================

        Intent intent = new Intent(BusinessLoanActivity.this, BusinessLoanResultActivity.class);

        intent.putExtra("loan_amount", principal);

        intent.putExtra("interest_rate", annualRate);

        intent.putExtra("loan_term", term);

        intent.putExtra("loan_term_unit", selectedLoanUnit);

        intent.putExtra("total_months", totalMonths);

        intent.putExtra("monthly_emi", monthlyEMI);

        intent.putExtra("total_interest", totalInterest);

        intent.putExtra("total_payment", totalPayment);

        intent.putExtra("currency_code", selectedCurrencyCode);

        intent.putExtra("currency_name", selectedCurrencyName);

        intent.putExtra("currency_icon", selectedCurrencyIcon);

        intent.putExtra("currency_flag", selectedCurrencyFlag);

        intent.putExtra("loan_type", "Business Loan");

        startActivity(intent);
    }


    private String getCurrencySymbol(String code) {

        if (code == null) {
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
                return "¥";

            case "CNY":
                return "¥";

            case "AUD":
                return "A$";

            case "CAD":
                return "C$";

            case "SGD":
                return "S$";

            case "AED":
                return "د.إ";

            case "SAR":
                return "﷼";

            case "VND":
                return "₫";

            case "THB":
                return "฿";

            case "IDR":
                return "Rp";

            default:
                return "$";
        }
    }


    private void resetFields() {

        etLoanAmount.setText("");
        etInterestRate.setText("");
        tvLoanTerm.setText("");

        etLoanAmount.setError(null);
        etInterestRate.setError(null);
        tvLoanTerm.setError(null);

        selectedLoanUnit = "Month";

        tvLoanUnit.setText(selectedLoanUnit);

        selectedCurrencyCode = "USD";
        selectedCurrencyName = "US Dollar";

        selectedCurrencyFlag = R.drawable.icn_flagus;

        selectedCurrencyIcon = R.drawable.usd_currency;

        tvCurrencyCode.setText(selectedCurrencyCode);

        if (ivFromFlag != null) {

            ivFromFlag.setImageResource(selectedCurrencyFlag);
        }

        etLoanAmount.requestFocus();
    }


    private void setupBanner() {

        if (bannerContainer == null) {
            return;
        }

        if (!Util.isInternetAvailable(this)) {
            return;
        }

        bannerAdView = new AdView(this);

        if (bannerAdView.getParent() != null) {

            ((ViewGroup) bannerAdView.getParent()).removeView(bannerAdView);
        }

        bannerContainer.addView(bannerAdView);

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.business_loan_banner));
    }
}