package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.emi.Ads.AdsHelper;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.LoanHistoryManager;
import com.loanemi.calculator.emi.utils.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StudentLoanActivity extends AppCompatActivity {

    private static final int REQUEST_CURRENCY = 1001;

    private AdView bannerAdView;
    private FrameLayout adContainer;

    private EditText etLoanAmount;
    private EditText etInterestRate;

    private TextView tvLoanTerm;
    private TextView tvLoanUnit;

    private LinearLayout btnCalculate;
    private LinearLayout btnReset;
    private LinearLayout currencySelector;
    private ImageView ivBack;
    private TextView tvCurrencyCode;
    private TextView tvCurrencyFlag;

    private String selectedCurrencyCode = "USD";
    private String selectedCountry = "US Dollar";

    private int selectedCurrencyIconResId = R.drawable.usd_currency;

    private int selectedFlagResId = R.drawable.icn_flagus;

    private LoanHistoryManager loanHistoryManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_student_loan);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        loanHistoryManager = new LoanHistoryManager(this);

        setupLoanAmountFormatter();

        setupCurrencySelector();

        setupLoanUnitSelector();

        setupButtons();

        setUpAd();

        updateCurrencyUI();
    }

    private void initViews() {

        ivBack = findViewById(R.id.ivBack);

        etLoanAmount = findViewById(R.id.etLoanAmount);

        etInterestRate = findViewById(R.id.etInterestRate);

        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvLoanUnit = findViewById(R.id.tvLoanUnit);

        btnCalculate = findViewById(R.id.btnCalculate);

        btnReset = findViewById(R.id.btnReset);

        adContainer = findViewById(R.id.bannerContainer);

        currencySelector = findViewById(R.id.currencySelector);

        tvCurrencyCode = findViewById(R.id.tvCurrencyCode);

        tvCurrencyFlag = findViewById(R.id.tvCurrencyFlag);
    }

    private void setupLoanAmountFormatter() {

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
            public void afterTextChanged(Editable s) {

                if (isFormatting) {
                    return;
                }

                isFormatting = true;

                String value = s.toString();

                value = value.replace(",", "");

                value = value.replaceAll("[^0-9]", "");

                if (value.isEmpty()) {

                    etLoanAmount.setText("");

                    isFormatting = false;

                    return;
                }

                try {

                    long amount = Long.parseLong(value);

                    String formatted = String.format(Locale.US, "%,d", amount);

                    etLoanAmount.setText(formatted);

                    etLoanAmount.setSelection(formatted.length());

                } catch (NumberFormatException ignored) {
                }

                isFormatting = false;
            }
        });
    }

    private void setupCurrencySelector() {

        if (currencySelector == null) {
            return;
        }

        currencySelector.setOnClickListener(v -> {

            Intent intent = new Intent(StudentLoanActivity.this, CurrencyUnitActivity.class);

            intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);

            intent.putExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE, selectedCurrencyCode);

            intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "student_loan");

            startActivityForResult(intent, REQUEST_CURRENCY);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CURRENCY) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        if (data == null) {
            return;
        }

        String code = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE);

        if (code != null && !code.trim().isEmpty()) {

            selectedCurrencyCode = code.trim().toUpperCase(Locale.US);
        }

        String country = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_COUNTRY);

        if (country != null && !country.trim().isEmpty()) {

            selectedCountry = country.trim();
        }


        selectedCurrencyIconResId = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_CURRENCY_ICON, getDefaultCurrencyIcon());


        selectedFlagResId = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_FLAG, getDefaultFlag());


        updateCurrencyUI();
    }


    private void updateCurrencyUI() {

        if (ivBack != null) {

            ivBack.setOnClickListener(v -> finish());
        }

        if (tvCurrencyCode != null) {

            tvCurrencyCode.setText(selectedCurrencyCode);
        }

        if (tvCurrencyFlag != null) {

            tvCurrencyFlag.setText(getFlagEmoji(selectedCurrencyCode));
        }
    }


    private int getDefaultCurrencyIcon() {

        switch (selectedCurrencyCode.toUpperCase(Locale.US)) {

            case "GBP":
                return R.drawable.gbp_currency;

            case "CNY":
                return R.drawable.cny_currency;

            case "INR":
                return R.drawable.inr_currency;

            case "VND":
                return R.drawable.vnd_currency;

            case "THB":
                return R.drawable.thb_currency;

            case "IDR":
                return R.drawable.idr_currency;

            case "USD":
            default:
                return R.drawable.usd_currency;
        }
    }


    private int getDefaultFlag() {

        switch (selectedCurrencyCode.toUpperCase(Locale.US)) {

            case "GBP":
                return R.drawable.icn_flagengland;

            case "CNY":
                return R.drawable.icn_flagchinese;

            case "INR":
                return R.drawable.icn_flaghindi;

            case "VND":
                return R.drawable.icn_flagvietnamese;

            case "THB":
                return R.drawable.icn_flagthailand;

            case "IDR":
                return R.drawable.icn_flagindonesia;

            case "USD":
            default:
                return R.drawable.icn_flagus;
        }
    }


    private String getFlagEmoji(String code) {

        if (code == null) {
            return "🇺🇸";
        }

        switch (code.toUpperCase(Locale.US)) {

            case "GBP":
                return "🇬🇧";

            case "USD":
                return "🇺🇸";

            case "CNY":
                return "🇨🇳";

            case "INR":
                return "🇮🇳";

            case "VND":
                return "🇻🇳";

            case "THB":
                return "🇹🇭";

            case "IDR":
                return "🇮🇩";

            case "AUD":
                return "🇦🇺";

            case "CAD":
                return "🇨🇦";

            case "EUR":
                return "🇪🇺";

            case "JPY":
                return "🇯🇵";

            case "KRW":
                return "🇰🇷";

            case "SGD":
                return "🇸🇬";

            case "MYR":
                return "🇲🇾";

            default:
                return "🇺🇸";
        }
    }

    private void setupLoanUnitSelector() {

        if (tvLoanUnit == null) {
            return;
        }

        tvLoanUnit.setOnClickListener(v -> {

            String[] units = {"Months", "Years"};

            new androidx.appcompat.app.AlertDialog.Builder(this).setTitle("Select Loan Unit").setItems(units, (dialog, which) -> {

                tvLoanUnit.setText(units[which]);
            }).show();
        });
    }

    private void setupButtons() {

        if (btnCalculate != null) {

            btnCalculate.setOnClickListener(v -> calculateStudentLoan());
        }

        if (btnReset != null) {

            btnReset.setOnClickListener(v -> resetFields());
        }
    }

    private void calculateStudentLoan() {

        String amountText = etLoanAmount.getText().toString().trim();

        String interestText = etInterestRate.getText().toString().trim();

        String termText = tvLoanTerm.getText().toString().trim();

        String unit = tvLoanUnit.getText().toString().trim();

        if (TextUtils.isEmpty(amountText)) {

            etLoanAmount.setError("Enter loan amount");

            etLoanAmount.requestFocus();

            return;
        }

        amountText = amountText.replace(",", "");

        if (TextUtils.isEmpty(interestText)) {

            etInterestRate.setError("Enter interest rate");

            etInterestRate.requestFocus();

            return;
        }

        if (TextUtils.isEmpty(termText) || termText.equalsIgnoreCase("Loan Term")) {

            Toast.makeText(this, "Enter loan term", Toast.LENGTH_SHORT).show();

            return;
        }


        double loanAmount;
        double interestRate;
        double loanTerm;


        try {

            loanAmount = Double.parseDouble(amountText);

            interestRate = Double.parseDouble(interestText);

            loanTerm = Double.parseDouble(termText);

        } catch (NumberFormatException e) {

            Toast.makeText(this, "Enter valid values", Toast.LENGTH_SHORT).show();

            return;
        }

        if (loanAmount <= 0) {

            etLoanAmount.setError("Enter valid loan amount");

            return;
        }

        if (interestRate < 0) {

            etInterestRate.setError("Enter valid interest rate");

            return;
        }


        if (interestRate > 100) {

            etInterestRate.setError("Interest rate cannot exceed 100%");

            return;
        }

        if (loanTerm <= 0) {

            Toast.makeText(this, "Loan term must be greater than 0", Toast.LENGTH_SHORT).show();

            return;
        }

        int totalMonths;


        if (unit.equalsIgnoreCase("Years")) {

            totalMonths = (int) Math.round(loanTerm * 12);

        } else {

            totalMonths = (int) Math.round(loanTerm);
        }


        if (totalMonths <= 0) {

            Toast.makeText(this, "Invalid loan term", Toast.LENGTH_SHORT).show();

            return;
        }

        if (totalMonths > 360) {

            Toast.makeText(this, "Loan term cannot exceed 30 years", Toast.LENGTH_SHORT).show();

            return;
        }

        double monthlyRate = interestRate / 12.0 / 100.0;


        double monthlyPayment;


        if (monthlyRate == 0) {

            monthlyPayment = loanAmount / totalMonths;

        } else {

            double factor = Math.pow(1 + monthlyRate, totalMonths);


            monthlyPayment = loanAmount * monthlyRate * factor / (factor - 1);
        }


        double totalPayment = monthlyPayment * totalMonths;


        double totalInterest = totalPayment - loanAmount;


        if (totalInterest < 0 && totalInterest > -0.01) {

            totalInterest = 0;
        }

        Calendar startCalendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String startDate = dateFormat.format(startCalendar.getTime());

        Calendar payoffCalendar = (Calendar) startCalendar.clone();

        payoffCalendar.add(Calendar.MONTH, totalMonths);

        String payoffDate = dateFormat.format(payoffCalendar.getTime());

        if (loanHistoryManager == null) {

            loanHistoryManager = new LoanHistoryManager(this);
        }


        loanHistoryManager.addHistory(

                "Student Loan",

                startDate,

                loanAmount,

                interestRate,

                loanTerm,

                unit,

                totalMonths,

                monthlyPayment,

                totalInterest,

                totalPayment,

                startDate,

                selectedCurrencyCode,

                getCurrencySymbol(selectedCurrencyCode),

                selectedCurrencyIconResId);

        Intent intent = new Intent(StudentLoanActivity.this, StudentLoanRsultActivity.class);

        intent.putExtra("loan_amount", loanAmount);

        intent.putExtra("interest_rate", interestRate);

        intent.putExtra("loan_term", loanTerm);

        intent.putExtra("loan_term_unit", unit);

        intent.putExtra("total_months", totalMonths);

        intent.putExtra("monthly_payment", monthlyPayment);

        intent.putExtra("total_interest", totalInterest);

        intent.putExtra("total_payment", totalPayment);

        // Currency
        intent.putExtra("currency_code", selectedCurrencyCode);

        intent.putExtra("currency_country", selectedCountry);

        intent.putExtra("currency_name", selectedCountry);

        intent.putExtra("currency_icon", selectedCurrencyIconResId);

        intent.putExtra("currency_flag", selectedFlagResId);

        intent.putExtra("currency_symbol", getCurrencySymbol(selectedCurrencyCode));

        intent.putExtra("start_date", startDate);

        intent.putExtra("payoff_date", payoffDate);

        startActivity(intent);
    }

    private String getCurrencySymbol(String code) {

        if (code == null) {
            return "$";
        }

        switch (code.toUpperCase(Locale.US)) {

            case "USD":
                return "$";

            case "GBP":
                return "£";

            case "EUR":
                return "€";

            case "INR":
                return "₹";

            case "CNY":
                return "¥";

            case "JPY":
                return "¥";

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

            case "KRW":
                return "₩";

            case "SGD":
                return "S$";

            case "MYR":
                return "RM";

            default:
                return "$";
        }
    }


    private void resetFields() {

        etLoanAmount.setText("");

        etInterestRate.setText("");

        tvLoanTerm.setText(getString(R.string.loan_term));

        tvLoanUnit.setText("Months");

        selectedCurrencyCode = "USD";

        selectedCountry = "US Dollar";

        selectedCurrencyIconResId = R.drawable.usd_currency;

        selectedFlagResId = R.drawable.icn_flagus;

        updateCurrencyUI();

        etLoanAmount.requestFocus();
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

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.student_loan_banner));
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