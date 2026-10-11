package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.content.Context;
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
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.LoanHistoryManager;
import com.loanemi.calculator.emi.utils.Util;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HomeLoanActivity extends AppCompatActivity {

    private AdView bannerAdView;
    private FrameLayout adContainer;
    private EditText etLoanAmount;
    private EditText tvdownpayment;
    private TextView tvdownpaymentrate;
    private EditText etInterestRate;
    private EditText tvLoanTerm;

    private TextView tvLoanUnit;
    private TextView tvCurrencyCode;
    private TextView tvCurrencySymbol;

    private ImageView ivCurrencyFlag,ivBack;

    private LinearLayout currencySelector;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;

    private String selectedLoanUnit = "Month";

    private String selectedCurrencyCode = "USD";
    private String selectedCurrencyName = "US Dollar";
    private String selectedCurrencySymbol = "$";

    private int selectedCurrencyFlag = R.drawable.icn_flagus;

    private static final int REQUEST_CURRENCY = 1001;

    private LoanHistoryManager loanHistoryManager;

    private final NumberFormat indianNumberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home_loan);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        loanHistoryManager = new LoanHistoryManager(this);

        setupListeners();

        setupLoanTermUnit();

        setupAmountFormatting();

        setUpAd();
    }

    private void initViews() {

        ivBack = findViewById(R.id.ivBack);

        etLoanAmount = findViewById(R.id.etLoanAmount);

        tvdownpayment = findViewById(R.id.tvdownpayment);

        tvdownpaymentrate = findViewById(R.id.tvdownpaymentrate);

        etInterestRate = findViewById(R.id.etInterestRate);

        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvLoanUnit = findViewById(R.id.tvLoanUnit);

        tvCurrencyCode = findViewById(R.id.tvCurrencyCode);

        ivCurrencyFlag = findViewById(R.id.ivCurrencyFlag);

        currencySelector = findViewById(R.id.currencySelector);

        btnReset = findViewById(R.id.btnReset);

        btnCalculate = findViewById(R.id.btnCalculate);

        adContainer = findViewById(R.id.bannerContainer);

        tvCurrencySymbol = findViewById(R.id.Tvcurrency);

        updateCurrencyUI();
    }

    private void updateCurrencyUI() {

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());

        if (tvCurrencyCode != null) {
            tvCurrencyCode.setText(selectedCurrencyCode);
        }

        if (tvCurrencySymbol != null) {
            tvCurrencySymbol.setText(selectedCurrencySymbol);
        }

        if (ivCurrencyFlag != null) {
            ivCurrencyFlag.setImageResource(selectedCurrencyFlag);
        }
    }

    private boolean updatingDownPaymentFields = false;

    private void setupListeners() {
        View toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setOnClickListener(v -> finish());
        }

        if (currencySelector != null) {
            currencySelector.setOnClickListener(v -> openCurrencyActivity());
        }

        if (tvLoanUnit != null) {
            tvLoanUnit.setOnClickListener(v -> showLoanUnitDialog());
        }

        if (btnReset != null) {
            btnReset.setOnClickListener(v -> resetFields());
        }

        if (btnCalculate != null) {
            btnCalculate.setOnClickListener(v -> calculateHomeLoan());
        }

        if (tvdownpayment != null) {
            tvdownpayment.addTextChangedListener(new TextWatcher() {
                private boolean formatting;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (formatting || updatingDownPaymentFields) {
                        return;
                    }

                    String value = editable.toString();
                    String cleanValue = value.replace(",", "").trim();

                    if (!cleanValue.isEmpty()) {
                        try {
                            if (!cleanValue.contains(".")) {
                                long amount = Long.parseLong(cleanValue);
                                String formatted = indianNumberFormat.format(amount);
                                if (!formatted.equals(value)) {
                                    formatting = true;
                                    tvdownpayment.setText(formatted);
                                    tvdownpayment.setSelection(formatted.length());
                                    formatting = false;
                                }
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    updateDownPaymentRateFromAmount();
                }
            });
        }

        if (tvdownpaymentrate != null) {
            tvdownpaymentrate.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (updatingDownPaymentFields) {
                        return;
                    }

                    String rateText = editable.toString().trim();
                    String loanText = etLoanAmount == null ? "" : etLoanAmount.getText().toString().replace(",", "").trim();

                    if (rateText.isEmpty() || loanText.isEmpty()) {
                        return;
                    }

                    try {
                        double rate = Double.parseDouble(rateText);
                        double propertyAmount = Double.parseDouble(loanText);

                        if (rate < 0 || rate > 100 || propertyAmount <= 0) {
                            return;
                        }

                        double amount = propertyAmount * rate / 100.0;
                        String formatted = indianNumberFormat.format(Math.round(amount));

                        updatingDownPaymentFields = true;
                        tvdownpayment.setText(formatted);
                        tvdownpayment.setSelection(formatted.length());
                        updatingDownPaymentFields = false;
                    } catch (NumberFormatException ignored) {
                    }
                }
            });
        }
    }

    private void updateDownPaymentRateFromAmount() {
        if (updatingDownPaymentFields || etLoanAmount == null || tvdownpayment == null || tvdownpaymentrate == null) {
            return;
        }

        String loanText = etLoanAmount.getText().toString().replace(",", "").trim();
        String downText = tvdownpayment.getText().toString().replace(",", "").trim();

        if (loanText.isEmpty() || downText.isEmpty()) {
            return;
        }

        try {
            double propertyAmount = Double.parseDouble(loanText);
            double downAmount = Double.parseDouble(downText);

            if (propertyAmount <= 0 || downAmount < 0) {
                return;
            }

            double rate = downAmount / propertyAmount * 100.0;
            String rateValue = String.format(Locale.US, "%.2f", rate);
            rateValue = rateValue.replaceAll("\\.?0+$", "");

            updatingDownPaymentFields = true;
            tvdownpaymentrate.setText(rateValue);
            updatingDownPaymentFields = false;
        } catch (NumberFormatException ignored) {
        }
    }

    private void setupLoanTermUnit() {

        if (tvLoanUnit == null) {
            return;
        }

        tvLoanUnit.setText("Month");

        selectedLoanUnit = "Month";
    }

    private void showLoanUnitDialog() {

        final String[] units = {getString(R.string.month), getString(R.string.years)};

        int selectedPosition = selectedLoanUnit.equalsIgnoreCase("Month") ? 0 : 1;

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Select Loan Term Unit").setSingleChoiceItems(units, selectedPosition, (dialogInterface, which) -> {

            selectedLoanUnit = units[which];

            tvLoanUnit.setText(selectedLoanUnit);

            dialogInterface.dismiss();
        }).create();

        dialog.show();
    }

    private void setupAmountFormatting() {
        if (etLoanAmount == null) {
            return;
        }

        etLoanAmount.addTextChangedListener(new TextWatcher() {
            private boolean formatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (formatting) {
                    return;
                }

                String value = editable.toString();
                if (value.isEmpty()) {
                    if (tvdownpaymentrate != null) {
                        tvdownpaymentrate.setText("");
                    }
                    return;
                }

                String cleanValue = value.replace(",", "");
                try {
                    if (cleanValue.contains(".")) {
                        return;
                    }

                    long amount = Long.parseLong(cleanValue);
                    String formatted = indianNumberFormat.format(amount);

                    if (!formatted.equals(value)) {
                        formatting = true;
                        etLoanAmount.setText(formatted);
                        etLoanAmount.setSelection(formatted.length());
                        formatting = false;
                    }

                    updateDownPaymentRateFromAmount();
                } catch (NumberFormatException ignored) {
                }
            }
        });
    }

    private void calculateHomeLoan() {

        String loanAmountText = etLoanAmount.getText().toString().replace(",", "").trim();

        String interestText = etInterestRate.getText().toString().trim();

        String termText = tvLoanTerm.getText().toString().trim();

        if (loanAmountText.isEmpty()) {

            etLoanAmount.setError("Enter loan amount");

            etLoanAmount.requestFocus();

            return;
        }

        if (interestText.isEmpty()) {

            etInterestRate.setError("Enter interest rate");

            etInterestRate.requestFocus();

            return;
        }

        if (termText.isEmpty()) {

            tvLoanTerm.setError("Enter loan term");

            tvLoanTerm.requestFocus();

            return;
        }

        double propertyLoanAmount;

        double interestRate;

        double loanTerm;

        try {

            propertyLoanAmount = Double.parseDouble(loanAmountText);

            interestRate = Double.parseDouble(interestText);

            loanTerm = Double.parseDouble(termText);

        } catch (Exception e) {

            Toast.makeText(this, "Please enter valid values", Toast.LENGTH_SHORT).show();

            return;
        }

        if (propertyLoanAmount <= 0) {

            etLoanAmount.setError("Enter valid loan amount");

            etLoanAmount.requestFocus();

            return;
        }

        if (interestRate < 0) {

            etInterestRate.setError("Enter valid interest rate");

            etInterestRate.requestFocus();

            return;
        }

        if (interestRate > 100) {

            etInterestRate.setError("Interest rate cannot exceed 100%");

            etInterestRate.requestFocus();

            return;
        }

        if (loanTerm <= 0) {

            tvLoanTerm.setError("Enter valid loan term");

            tvLoanTerm.requestFocus();

            return;
        }

        String selectedUnit = tvLoanUnit.getText().toString();

        int totalMonths;

        if (selectedUnit.equalsIgnoreCase("Year") || selectedUnit.equalsIgnoreCase("Years")) {

            if (loanTerm > 30) {

                tvLoanTerm.setError("Loan term cannot exceed 30 years");

                Toast.makeText(this, "Loan term cannot exceed 30 years", Toast.LENGTH_SHORT).show();

                return;
            }

            totalMonths = (int) Math.round(loanTerm * 12);

        } else {

            if (loanTerm > 360) {

                tvLoanTerm.setError("Loan term cannot exceed 360 months");

                Toast.makeText(this, "Loan term cannot exceed 360 months", Toast.LENGTH_SHORT).show();

                return;
            }

            totalMonths = (int) Math.round(loanTerm);
        }

        if (totalMonths <= 0) {

            tvLoanTerm.setError("Enter valid loan term");

            return;
        }

        double downPaymentAmount = 0;

        String downAmountText = tvdownpayment.getText().toString().replace(",", "").trim();

        String downRateText = tvdownpaymentrate.getText().toString().trim();

        if (!downAmountText.isEmpty()) {

            try {

                downPaymentAmount = Double.parseDouble(downAmountText);

            } catch (Exception e) {

                tvdownpayment.setError("Enter valid down payment");

                tvdownpayment.requestFocus();

                return;
            }

        } else if (!downRateText.isEmpty()) {

            try {

                double downPaymentRate = Double.parseDouble(downRateText);

                if (downPaymentRate < 0 || downPaymentRate > 100) {

                    tvdownpaymentrate.setError("Enter percentage between 0 and 100");

                    tvdownpaymentrate.requestFocus();

                    return;
                }

                downPaymentAmount = propertyLoanAmount * downPaymentRate / 100.0;

            } catch (Exception e) {

                tvdownpaymentrate.setError("Enter valid percentage");

                tvdownpaymentrate.requestFocus();

                return;
            }
        }

        if (downPaymentAmount < 0) {

            downPaymentAmount = 0;
        }

        if (downPaymentAmount >= propertyLoanAmount) {

            Toast.makeText(this, "Down payment must be less than loan amount", Toast.LENGTH_SHORT).show();

            return;
        }

        double principal = propertyLoanAmount - downPaymentAmount;

        double monthlyInterestRate = interestRate / 12.0 / 100.0;

        double monthlyEMI;

        if (monthlyInterestRate == 0) {

            monthlyEMI = principal / totalMonths;

        } else {

            double power = Math.pow(1 + monthlyInterestRate, totalMonths);

            monthlyEMI = principal * monthlyInterestRate * power / (power - 1);
        }

        double totalPayment = monthlyEMI * totalMonths;

        double totalInterest = totalPayment - principal;

        if (totalInterest < 0 && totalInterest > -0.01) {

            totalInterest = 0;
        }

        Calendar startCalendar = Calendar.getInstance();

        Calendar payoffCalendar = (Calendar) startCalendar.clone();

        payoffCalendar.add(Calendar.MONTH, totalMonths);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String startDate = dateFormat.format(startCalendar.getTime());

        String payoffDate = dateFormat.format(payoffCalendar.getTime());

        if (loanHistoryManager == null) {

            loanHistoryManager = new LoanHistoryManager(this);
        }

        loanHistoryManager.addHistory("Home Loan", startDate, propertyLoanAmount, interestRate, loanTerm, selectedUnit, totalMonths, monthlyEMI, totalInterest, totalPayment, startDate, selectedCurrencyCode, selectedCurrencySymbol, R.drawable.home_ic);

        Intent intent = new Intent(HomeLoanActivity.this, HomeLoanResultActivity.class);

        intent.putExtra("loan_amount", propertyLoanAmount);

        intent.putExtra("down_payment", downPaymentAmount);

        intent.putExtra("principal", principal);

        intent.putExtra("interest_rate", interestRate);

        intent.putExtra("loan_term", loanTerm);

        intent.putExtra("loan_term_unit", selectedUnit);

        intent.putExtra("total_months", totalMonths);

        intent.putExtra("monthly_emi", monthlyEMI);

        intent.putExtra("total_payment", totalPayment);

        intent.putExtra("total_interest", totalInterest);

        intent.putExtra("currency_code", selectedCurrencyCode);

        intent.putExtra("currency_name", selectedCurrencyName);

        intent.putExtra("currency_symbol", selectedCurrencySymbol);

        intent.putExtra("currency_flag", selectedCurrencyFlag);

        intent.putExtra("loan_type", "Home Loan");

        intent.putExtra("start_date", startDate);

        intent.putExtra("payoff_date", payoffDate);

        intent.putExtra("start_date_millis", startCalendar.getTimeInMillis());

        intent.putExtra("payoff_date_millis", payoffCalendar.getTimeInMillis());

        startActivity(intent);
    }

    private void resetFields() {

        etLoanAmount.setText("");

        tvdownpayment.setText("");

        tvdownpaymentrate.setText("");

        etInterestRate.setText("");

        tvLoanTerm.setText("");

        selectedLoanUnit = "Month";

        if (tvLoanUnit != null) {

            tvLoanUnit.setText("Month");
        }

        selectedCurrencyCode = "USD";

        selectedCurrencyName = "US Dollar";

        selectedCurrencySymbol = "$";

        selectedCurrencyFlag = R.drawable.icn_flagus;

        updateCurrencyUI();

        etLoanAmount.requestFocus();
    }

    private void openCurrencyActivity() {

        Intent intent = new Intent(HomeLoanActivity.this, CurrencyUnitActivity.class);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "HOME_LOAN");

        startActivityForResult(intent, REQUEST_CURRENCY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != REQUEST_CURRENCY || resultCode != RESULT_OK || data == null) {

            return;
        }

        String code = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE);

        if (code != null && !code.trim().isEmpty()) {

            selectedCurrencyCode = code.trim().toUpperCase(Locale.US);
        }

        String name = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_COUNTRY);

        if (name != null && !name.trim().isEmpty()) {

            selectedCurrencyName = name.trim();
        }

        int flag = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_FLAG, R.drawable.icn_flagus);

        selectedCurrencyFlag = flag;

        selectedCurrencySymbol = getCurrencySymbol(selectedCurrencyCode);

        updateCurrencyUI();
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

    private void setUpAd() {

        if (!Util.isInternetAvailable(this) || adContainer == null) {

            return;
        }

        bannerAdView = new AdView(this);

        if (bannerAdView.getParent() != null) {

            ((ViewGroup) bannerAdView.getParent()).removeView(bannerAdView);
        }

        adContainer.addView(bannerAdView);

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.home_loan_banner));
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
