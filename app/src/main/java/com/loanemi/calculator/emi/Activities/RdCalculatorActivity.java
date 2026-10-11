package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.emi.Ads.AdsHelper;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.LoanHistoryManager;
import com.loanemi.calculator.emi.utils.Util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RdCalculatorActivity extends AppCompatActivity {

    private EditText etLoanAmount;
    private EditText etInterestRate;
    private EditText etTimeInterest;
    private EditText tvLoanTerm;

    private TextView tvLoanUnit;
    private TextView tvStartDate;
    private TextView tvCurrencyCode;

    private ImageView ivCurrencyFlag,ivBack;

    private LinearLayout currencySelector;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;

    private FrameLayout bannerContainer;
    private AdView bannerAdView;

    private Calendar selectedDate;

    private String selectedCurrencyCode = "USD";
    private String selectedCurrencyCountry = "United States";
    private String selectedCurrencySymbol = "$";

    private int selectedCurrencyFlag = R.drawable.icn_flagus;

    private String selectedLoanUnit = "Months";
    private String selectedCompounding = "Monthly";

    public static final int REQUEST_CURRENCY = 1001;

    // =========================================================
    // HISTORY MANAGER
    // =========================================================

    private LoanHistoryManager loanHistoryManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rd_calculator);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        // =====================================================
        // INITIALIZE HISTORY MANAGER
        // =====================================================

        loanHistoryManager = new LoanHistoryManager(this);

        setupDefaultValues();

        setupAmountFormatting();

        setupCurrencySelector();

        setupLoanUnitSelector();

        setupCompoundingSelector();

        setupStartDateSelector();

        setupButtons();

        setUpAd();
    }


    // =========================================================
    // INIT VIEWS
    // =========================================================

    private void initViews() {

        ivBack = findViewById(R.id.ivBack);

        etLoanAmount = findViewById(R.id.etLoanAmount);

        etInterestRate = findViewById(R.id.etInterestRate);

        etTimeInterest = findViewById(R.id.etTimeInterest);

        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvLoanUnit = findViewById(R.id.tvLoanUnit);

        tvStartDate = findViewById(R.id.tvStartDate);

        tvCurrencyCode = findViewById(R.id.tvCurrencyCode);

        ivCurrencyFlag = findViewById(R.id.ivCurrencyFlag);

        currencySelector = findViewById(R.id.currencySelector);

        btnReset = findViewById(R.id.btnReset);

        btnCalculate = findViewById(R.id.btnCalculate);

        bannerContainer = findViewById(R.id.bannerContainer);
    }


    // =========================================================
    // DEFAULT VALUES
    // =========================================================

    private void setupDefaultValues() {

        selectedCurrencyCode = "USD";

        selectedCurrencyCountry = "United States";

        selectedCurrencySymbol = "$";

        selectedCurrencyFlag = R.drawable.icn_flagus;

        updateCurrencyUI();


        selectedLoanUnit = "Months";

        tvLoanUnit.setText(selectedLoanUnit);


        selectedCompounding = "Monthly";

        etTimeInterest.setText(selectedCompounding);

        etTimeInterest.setFocusable(false);

        etTimeInterest.setFocusableInTouchMode(false);

        etTimeInterest.setClickable(true);


        selectedDate = Calendar.getInstance();

        updateStartDateText();
    }


    // =========================================================
    // CURRENCY UI
    // =========================================================

    private void updateCurrencyUI() {

        if (ivBack != null) {

            ivBack.setOnClickListener(v -> finish());
        }

        if (tvCurrencyCode != null) {

            tvCurrencyCode.setText(selectedCurrencyCode);
        }


        if (ivCurrencyFlag != null) {

            ivCurrencyFlag.setImageResource(selectedCurrencyFlag);
        }
    }


    // =========================================================
    // AMOUNT FORMATTING
    // =========================================================

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


                String value = editable.toString();


                if (value.isEmpty()) {

                    return;
                }


                String cleanValue = value.replace(",", "").replaceAll("[^0-9.]", "");


                if (cleanValue.isEmpty()) {

                    return;
                }


                int firstDot = cleanValue.indexOf('.');


                if (firstDot >= 0) {

                    String beforeDot = cleanValue.substring(0, firstDot);

                    String afterDot = cleanValue.substring(firstDot + 1).replace(".", "");

                    cleanValue = beforeDot + "." + afterDot;
                }


                if (cleanValue.equals(".")) {

                    return;
                }


                try {

                    String integerPart;

                    String decimalPart = null;


                    if (cleanValue.contains(".")) {

                        String[] parts = cleanValue.split("\\.", -1);

                        integerPart = parts[0];

                        decimalPart = parts.length > 1 ? parts[1] : "";

                    } else {

                        integerPart = cleanValue;
                    }


                    if (integerPart.isEmpty()) {

                        integerPart = "0";
                    }


                    integerPart = integerPart.replaceFirst("^0+(?!$)", "");


                    DecimalFormat formatter = new DecimalFormat("#,##0");


                    long integerValue = Long.parseLong(integerPart);


                    String formatted = formatter.format(integerValue);


                    if (decimalPart != null) {

                        formatted += "." + decimalPart;
                    }


                    if (!formatted.equals(value)) {

                        isFormatting = true;


                        int oldCursor = etLoanAmount.getSelectionStart();


                        if (oldCursor < 0) {

                            oldCursor = value.length();
                        }


                        int commasBefore = countCommas(value.substring(0, Math.min(oldCursor, value.length())));


                        etLoanAmount.setText(formatted);


                        int cursorWithoutFormatting = Math.min(oldCursor, formatted.length());


                        int commasAfter = countCommas(formatted.substring(0, Math.min(cursorWithoutFormatting, formatted.length())));


                        int newCursor = oldCursor + (commasAfter - commasBefore);


                        newCursor = Math.max(0, Math.min(newCursor, formatted.length()));


                        etLoanAmount.setSelection(newCursor);


                        isFormatting = false;
                    }

                } catch (Exception ignored) {
                }
            }
        });
    }


    private int countCommas(String value) {

        int count = 0;


        for (int i = 0; i < value.length(); i++) {

            if (value.charAt(i) == ',') {

                count++;
            }
        }


        return count;
    }


    // =========================================================
    // GET INVESTMENT AMOUNT
    // =========================================================

    private double getInvestmentAmount() {

        String value = etLoanAmount.getText().toString().replace(",", "").trim();


        if (value.isEmpty()) {

            return 0;
        }


        try {

            return Double.parseDouble(value);

        } catch (Exception e) {

            return 0;
        }
    }


    // =========================================================
    // CURRENCY SELECTOR
    // =========================================================

    private void setupCurrencySelector() {

        if (currencySelector == null) {

            return;
        }


        currencySelector.setOnClickListener(v -> {

            Intent intent = new Intent(RdCalculatorActivity.this, CurrencyUnitActivity.class);


            intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);


            intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "RD");


            startActivityForResult(intent, REQUEST_CURRENCY);
        });
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


        String country = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_COUNTRY);


        if (country != null && !country.trim().isEmpty()) {

            selectedCurrencyCountry = country.trim();
        }


        int flag = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_FLAG, 0);


        if (flag != 0) {

            selectedCurrencyFlag = flag;

        } else {

            setCurrencyFlag(selectedCurrencyCode);
        }


        selectedCurrencySymbol = getCurrencySymbol(selectedCurrencyCode);


        updateCurrencyUI();
    }


    // =========================================================
    // CURRENCY FLAG
    // =========================================================

    private void setCurrencyFlag(String code) {

        if (code == null) {

            code = "USD";
        }


        switch (code.toUpperCase(Locale.US)) {

            case "USD":

                selectedCurrencyFlag = R.drawable.icn_flagus;

                break;


            case "GBP":

                selectedCurrencyFlag = R.drawable.icn_flagengland;

                break;


            case "CNY":

                selectedCurrencyFlag = R.drawable.icn_flagchinese;

                break;


            case "INR":

                selectedCurrencyFlag = R.drawable.icn_flaghindi;

                break;


            case "VND":

                selectedCurrencyFlag = R.drawable.icn_flagvietnamese;

                break;


            case "THB":

                selectedCurrencyFlag = R.drawable.icn_flagthailand;

                break;


            case "IDR":

                selectedCurrencyFlag = R.drawable.icn_flagindonesia;

                break;


            default:

                selectedCurrencyFlag = R.drawable.icn_flagus;

                break;
        }
    }


    // =========================================================
    // CURRENCY SYMBOL
    // =========================================================

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

            default:
                return "$";
        }
    }


    // =========================================================
    // LOAN TERM SELECTOR
    // =========================================================

    private void setupLoanUnitSelector() {

        if (tvLoanUnit == null) {

            return;
        }


        View unitLayout = (View) tvLoanUnit.getParent();


        if (unitLayout != null) {

            unitLayout.setOnClickListener(v -> showLoanUnitDialog());
        }


        tvLoanUnit.setOnClickListener(v -> showLoanUnitDialog());
    }


    private void showLoanUnitDialog() {

        final String[] units = {"Months", "Years"};


        int selectedIndex = selectedLoanUnit.equalsIgnoreCase("Years") ? 1 : 0;


        new AlertDialog.Builder(this).setTitle("Select RD Term Unit").setSingleChoiceItems(units, selectedIndex, (dialog, which) -> {

            selectedLoanUnit = units[which];

            tvLoanUnit.setText(selectedLoanUnit);

            dialog.dismiss();
        }).show();
    }


    // =========================================================
    // TOTAL MONTHS
    // =========================================================

    private int getTotalMonths() {

        String termText = tvLoanTerm.getText().toString().trim();


        if (termText.isEmpty()) {

            return 0;
        }


        int term;


        try {

            term = Integer.parseInt(termText);

        } catch (Exception e) {

            return 0;
        }


        if (term <= 0) {

            return 0;
        }


        if (selectedLoanUnit.equalsIgnoreCase("Years")) {

            if (term > 50) {

                return 0;
            }


            return term * 12;
        }


        if (term > 600) {

            return 0;
        }


        return term;
    }


    // =========================================================
    // COMPOUNDING
    // =========================================================

    private void setupCompoundingSelector() {

        if (etTimeInterest == null) {

            return;
        }


        etTimeInterest.setFocusable(false);

        etTimeInterest.setFocusableInTouchMode(false);

        etTimeInterest.setClickable(true);


        etTimeInterest.setOnClickListener(v -> showCompoundingDialog());


        View parent = (View) etTimeInterest.getParent();


        if (parent != null) {

            parent.setOnClickListener(v -> showCompoundingDialog());
        }
    }


    private void showCompoundingDialog() {

        final String[] frequencyNames = {"Annually", "Quarterly", "Monthly"};


        int selectedIndex = 2;


        for (int i = 0; i < frequencyNames.length; i++) {

            if (frequencyNames[i].equals(selectedCompounding)) {

                selectedIndex = i;

                break;
            }
        }


        new AlertDialog.Builder(this).setTitle("Compounding Frequency").setSingleChoiceItems(frequencyNames, selectedIndex, (dialog, which) -> {

            selectedCompounding = frequencyNames[which];

            etTimeInterest.setText(selectedCompounding);

            dialog.dismiss();
        }).show();
    }


    // =========================================================
    // START DATE
    // =========================================================

    private void setupStartDateSelector() {

        if (tvStartDate == null) {

            return;
        }


        View parent = (View) tvStartDate.getParent();


        if (parent != null) {

            parent.setOnClickListener(v -> showDatePicker());
        }


        tvStartDate.setOnClickListener(v -> showDatePicker());
    }


    private void showDatePicker() {

        Calendar currentDate = selectedDate != null ? selectedDate : Calendar.getInstance();


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int year, int month, int dayOfMonth) -> {

            selectedDate = Calendar.getInstance();


            selectedDate.set(Calendar.YEAR, year);


            selectedDate.set(Calendar.MONTH, month);


            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            updateStartDateText();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();
    }


    private void updateStartDateText() {

        if (selectedDate == null || tvStartDate == null) {

            return;
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


        tvStartDate.setText(dateFormat.format(selectedDate.getTime()));
    }


    // =========================================================
    // BUTTONS
    // =========================================================

    private void setupButtons() {

        if (btnReset != null) {

            btnReset.setOnClickListener(v -> resetFields());
        }


        if (btnCalculate != null) {

            btnCalculate.setOnClickListener(v -> calculateRD());
        }
    }


    // =========================================================
    // RESET
    // =========================================================

    private void resetFields() {

        etLoanAmount.setText("");

        etInterestRate.setText("");

        tvLoanTerm.setText("");


        selectedLoanUnit = "Months";

        tvLoanUnit.setText("Months");


        selectedCompounding = "Monthly";

        etTimeInterest.setText("Monthly");


        selectedCurrencyCode = "USD";

        selectedCurrencyCountry = "United States";

        selectedCurrencySymbol = "$";

        selectedCurrencyFlag = R.drawable.icn_flagus;


        updateCurrencyUI();


        selectedDate = Calendar.getInstance();


        updateStartDateText();


        etLoanAmount.requestFocus();
    }


    // =========================================================
    // CALCULATE RD
    // =========================================================

    private void calculateRD() {

        double monthlyDeposit = getInvestmentAmount();


        if (monthlyDeposit <= 0) {

            etLoanAmount.setError("Enter investment amount");

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

            etInterestRate.setError("Enter valid interest rate");

            etInterestRate.requestFocus();

            return;
        }


        if (annualRate < 0 || annualRate > 100) {

            etInterestRate.setError("Interest rate must be between 0 and 100");

            etInterestRate.requestFocus();

            return;
        }


        String termText = tvLoanTerm.getText().toString().trim();


        if (termText.isEmpty()) {

            tvLoanTerm.setError("Enter RD term");

            tvLoanTerm.requestFocus();

            return;
        }


        int enteredTerm;


        try {

            enteredTerm = Integer.parseInt(termText);

        } catch (Exception e) {

            tvLoanTerm.setError("Enter a valid whole number");

            tvLoanTerm.requestFocus();

            return;
        }


        if (enteredTerm <= 0) {

            tvLoanTerm.setError("RD term must be greater than 0");

            tvLoanTerm.requestFocus();

            return;
        }


        if (selectedLoanUnit.equalsIgnoreCase("Years") && enteredTerm > 50) {

            tvLoanTerm.setError("Maximum RD term is 50 years");

            tvLoanTerm.requestFocus();

            return;
        }


        if (selectedLoanUnit.equalsIgnoreCase("Months") && enteredTerm > 600) {

            tvLoanTerm.setError("Maximum RD term is 600 months");

            tvLoanTerm.requestFocus();

            return;
        }


        int totalMonths = getTotalMonths();


        if (totalMonths <= 0) {

            tvLoanTerm.setError("Enter valid RD term");

            tvLoanTerm.requestFocus();

            return;
        }


        // =====================================================
        // CALCULATE RD
        // =====================================================

        double maturityValue = calculateRDMaturity(monthlyDeposit, annualRate, totalMonths, selectedCompounding);


        double totalInvestment = monthlyDeposit * totalMonths;


        double totalInterest = maturityValue - totalInvestment;


        if (totalInterest < 0 && totalInterest > -0.01) {

            totalInterest = 0;
        }


        if (totalInterest < 0) {

            totalInterest = 0;
        }


        String startDate = tvStartDate != null ? tvStartDate.getText().toString() : "";

        Intent intent = new Intent(RdCalculatorActivity.this, RdCalculatorResultActivity.class);


        intent.putExtra("monthly_deposit", monthlyDeposit);


        intent.putExtra("investment_amount", monthlyDeposit);


        intent.putExtra("interest_rate", annualRate);


        intent.putExtra("loan_term", enteredTerm);


        intent.putExtra("investment_term", enteredTerm);


        intent.putExtra("loan_term_unit", selectedLoanUnit);


        intent.putExtra("investment_term_unit", selectedLoanUnit);


        intent.putExtra("total_months", totalMonths);


        intent.putExtra("compounding", selectedCompounding);


        intent.putExtra("compounding_frequency", getCompoundingFrequency(selectedCompounding));


        intent.putExtra("start_date", startDate);


        intent.putExtra("total_investment", totalInvestment);


        intent.putExtra("total_interest", totalInterest);


        intent.putExtra("maturity_value", maturityValue);


        intent.putExtra("currency_code", selectedCurrencyCode);


        intent.putExtra("currency_country", selectedCurrencyCountry);


        intent.putExtra("currency_name", selectedCurrencyCountry);


        intent.putExtra("currency_symbol", selectedCurrencySymbol);


        intent.putExtra("currency_flag", selectedCurrencyFlag);


        intent.putExtra("loan_type", "Recurring Deposit");


        startActivity(intent);
    }


    // =========================================================
    // SAVE RD HISTORY
    // =========================================================

    private void saveRDHistory(double monthlyDeposit, double annualRate, int enteredTerm, String termUnit, int totalMonths, double totalInterest, double maturityValue, String startDate) {

        if (loanHistoryManager == null) {

            loanHistoryManager = new LoanHistoryManager(this);
        }


        // Current date for history item.
        SimpleDateFormat historyDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());


        String historyDate = historyDateFormat.format(Calendar.getInstance().getTime());
    }


    // =========================================================
    // COMPOUNDING FREQUENCY
    // =========================================================

    private int getCompoundingFrequency(String compounding) {

        if (compounding == null) {

            return 12;
        }


        switch (compounding) {

            case "Annually":

                return 1;


            case "Quarterly":

                return 4;


            case "Monthly":

            default:

                return 12;
        }
    }


    // =========================================================
    // RD MATURITY CALCULATION
    // =========================================================

    private double calculateRDMaturity(double monthlyDeposit, double annualRate, int months, String compounding) {

        if (monthlyDeposit <= 0 || months <= 0) {

            return 0;
        }


        if (annualRate <= 0) {

            return monthlyDeposit * months;
        }


        int compoundsPerYear = getCompoundingFrequency(compounding);


        double annualRateDecimal = annualRate / 100.0;


        double monthlyRate = Math.pow(1.0 + annualRateDecimal / compoundsPerYear, compoundsPerYear / 12.0) - 1.0;


        double maturityValue = 0;


        for (int month = 0; month < months; month++) {

            int remainingMonths = months - month;


            double installmentValue = monthlyDeposit * Math.pow(1.0 + monthlyRate, remainingMonths);


            maturityValue += installmentValue;
        }


        return maturityValue;
    }


    // =========================================================
    // BANNER AD
    // =========================================================

    private void setUpAd() {

        if (!Util.isInternetAvailable(this) || bannerContainer == null) {

            return;
        }


        bannerAdView = new AdView(this);


        if (bannerAdView.getParent() != null) {

            ((ViewGroup) bannerAdView.getParent()).removeView(bannerAdView);
        }


        bannerContainer.addView(bannerAdView);


        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.rd_calc_banner));
    }


    // =========================================================
    // RESUME
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        Util.hide(this);
    }


    // =========================================================
    // DESTROY
    // =========================================================

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