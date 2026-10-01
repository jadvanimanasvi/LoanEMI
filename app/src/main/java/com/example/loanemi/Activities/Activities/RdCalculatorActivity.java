package com.example.loanemi.Activities.Activities;

import static com.example.loanemi.Activities.utils.Util.setupEdgeToEdge;

import android.app.DatePickerDialog;
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

import com.example.loanemi.Activities.Ads.AdsHelper;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RdCalculatorActivity extends AppCompatActivity {

    // ============================================================
    // Views
    // ============================================================

    private EditText etLoanAmount;
    private EditText etInterestRate;
    private EditText etTimeInterest;
    private EditText tvLoanTerm;

    private TextView tvLoanUnit;
    private TextView tvStartDate;

    private TextView tvCurrencyCode;
    private ImageView ivCurrencyFlag;

    private LinearLayout currencySelector;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;

    private FrameLayout bannerContainer;
    private AdView bannerAdView;

    // ============================================================
    // Values
    // ============================================================

    private Calendar selectedDate;

    private String selectedCurrencyCode = "USD";
    private String selectedCurrencyCountry = "United States";

    private String selectedLoanUnit = "Months";
    private String selectedCompounding = "Monthly";

    // ============================================================
    // Currency request
    // ============================================================

    public static final int REQUEST_CURRENCY = 1001;

    // ============================================================
    // onCreate
    // ============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rd_calculator);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        setupDefaultValues();

        setupAmountFormatting();

        setupCurrencySelector();

        setupLoanUnitSelector();

        setupCompoundingSelector();

        setupStartDateSelector();

        setupButtons();

        setUpAd();
    }

    // ============================================================
    // Initialize views
    // ============================================================

    private void initViews() {

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

    // ============================================================
    // Default values
    // ============================================================

    private void setupDefaultValues() {

        // --------------------------------------------------------
        // Currency
        // --------------------------------------------------------

        selectedCurrencyCode = "USD";
        selectedCurrencyCountry = "United States";

        tvCurrencyCode.setText(selectedCurrencyCode);

        setCurrencyFlag(selectedCurrencyCode);

        selectedLoanUnit = "Months";

        tvLoanUnit.setText(selectedLoanUnit);

        // --------------------------------------------------------
        // Compounding
        // --------------------------------------------------------

        selectedCompounding = "Monthly";

        etTimeInterest.setText(selectedCompounding);

        etTimeInterest.setFocusable(false);
        etTimeInterest.setFocusableInTouchMode(false);
        etTimeInterest.setClickable(true);

        // --------------------------------------------------------
        // Date
        // --------------------------------------------------------

        selectedDate = Calendar.getInstance();

        updateStartDateText();
    }

    // ============================================================
    // Amount formatting
    // ============================================================

    private void setupAmountFormatting() {

        etLoanAmount.addTextChangedListener(new TextWatcher() {

            private boolean isFormatting = false;

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {
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

                // Remove commas
                String cleanValue = value.replace(",", "");

                // Keep only numbers and decimal point
                cleanValue = cleanValue.replaceAll(
                        "[^0-9.]",
                        ""
                );

                // Prevent multiple decimal points
                int firstDot = cleanValue.indexOf('.');

                if (firstDot >= 0) {

                    String beforeDot =
                            cleanValue.substring(0, firstDot);

                    String afterDot =
                            cleanValue.substring(firstDot + 1)
                                    .replace(".", "");

                    cleanValue =
                            beforeDot + "." + afterDot;
                }

                if (cleanValue.isEmpty()) {
                    return;
                }

                if (cleanValue.equals(".")) {
                    return;
                }

                try {

                    String integerPart;
                    String decimalPart = null;

                    if (cleanValue.contains(".")) {

                        String[] parts =
                                cleanValue.split("\\.", -1);

                        integerPart = parts[0];

                        decimalPart = parts.length > 1
                                ? parts[1]
                                : "";

                    } else {

                        integerPart = cleanValue;
                    }

                    if (integerPart.isEmpty()) {
                        integerPart = "0";
                    }

                    // Remove unnecessary leading zeros
                    integerPart =
                            integerPart.replaceFirst(
                                    "^0+(?!$)",
                                    ""
                            );

                    DecimalFormat formatter =
                            new DecimalFormat("#,##0");

                    long integerValue =
                            Long.parseLong(integerPart);

                    String formatted =
                            formatter.format(integerValue);

                    if (decimalPart != null) {

                        formatted += "." + decimalPart;
                    }

                    if (!formatted.equals(value)) {

                        isFormatting = true;

                        int oldCursor =
                                etLoanAmount.getSelectionStart();

                        if (oldCursor < 0) {
                            oldCursor = value.length();
                        }

                        int commasBefore =
                                countCommas(
                                        value.substring(
                                                0,
                                                Math.min(
                                                        oldCursor,
                                                        value.length()
                                                )
                                        )
                                );

                        etLoanAmount.setText(formatted);

                        int commasAfter =
                                countCommas(
                                        formatted.substring(
                                                0,
                                                Math.min(
                                                        oldCursor + 3,
                                                        formatted.length()
                                                )
                                        )
                                );

                        int newCursor =
                                oldCursor +
                                        (commasAfter - commasBefore);

                        newCursor =
                                Math.max(
                                        0,
                                        Math.min(
                                                newCursor,
                                                formatted.length()
                                        )
                                );

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

    // ============================================================
    // Get investment amount
    // ============================================================

    private double getInvestmentAmount() {

        String value =
                etLoanAmount
                        .getText()
                        .toString()
                        .replace(",", "")
                        .trim();

        if (value.isEmpty()) {
            return 0;
        }

        try {
            return Double.parseDouble(value);
        } catch (Exception e) {
            return 0;
        }
    }

    // ============================================================
    // Currency selector
    // ============================================================

    private void setupCurrencySelector() {

        currencySelector.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            RdCalculatorActivity.this,
                            CurrencyUnitActivity.class
                    );

            intent.putExtra(
                    CurrencyUnitActivity.EXTRA_CURRENT_CODE,
                    selectedCurrencyCode
            );

            intent.putExtra(
                    CurrencyUnitActivity.EXTRA_SELECTED_CODE,
                    selectedCurrencyCode
            );

            intent.putExtra(
                    CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE,
                    "CURRENCY"
            );

            startActivityForResult(
                    intent,
                    REQUEST_CURRENCY
            );
        });
    }

    // ============================================================
    // Currency result
    // ============================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );

        if (requestCode != REQUEST_CURRENCY) {
            return;
        }

        if (resultCode != RESULT_OK || data == null) {
            return;
        }

        String code =
                data.getStringExtra(
                        CurrencyUnitActivity.EXTRA_SELECTED_CODE
                );

        if (code != null && !code.trim().isEmpty()) {

            selectedCurrencyCode =
                    code.trim().toUpperCase(Locale.US);

            tvCurrencyCode.setText(
                    selectedCurrencyCode
            );
        }

        String country =
                data.getStringExtra(
                        CurrencyUnitActivity.EXTRA_SELECTED_COUNTRY
                );

        if (country != null && !country.trim().isEmpty()) {

            selectedCurrencyCountry =
                    country.trim();
        }

        int selectedFlag =
                data.getIntExtra(
                        CurrencyUnitActivity.EXTRA_SELECTED_FLAG,
                        0
                );

        if (selectedFlag != 0) {

            ivCurrencyFlag.setImageResource(
                    selectedFlag
            );

        } else {

            setCurrencyFlag(
                    selectedCurrencyCode
            );
        }
    }

    // ============================================================
    // Currency flag
    // ============================================================

    private void setCurrencyFlag(String code) {

        if (code == null) {
            code = "USD";
        }

        switch (code.toUpperCase(Locale.US)) {

            case "USD":
                ivCurrencyFlag.setImageResource(
                        R.drawable.icn_flagus
                );
                break;

            case "GBP":
                ivCurrencyFlag.setImageResource(
                        R.drawable.icn_flagengland
                );
                break;

            case "CNY":
                ivCurrencyFlag.setImageResource(
                        R.drawable.icn_flagchinese
                );
                break;

            case "INR":
                ivCurrencyFlag.setImageResource(
                        R.drawable.icn_flaghindi
                );
                break;

            case "VND":
                ivCurrencyFlag.setImageResource(
                        R.drawable.icn_flagvietnamese
                );
                break;

            case "THB":
                ivCurrencyFlag.setImageResource(
                        R.drawable.icn_flagthailand
                );
                break;

            case "IDR":
                ivCurrencyFlag.setImageResource(
                        R.drawable.icn_flagindonesia
                );
                break;

            default:
                ivCurrencyFlag.setImageResource(
                        R.drawable.icn_flagus
                );
                break;
        }
    }

    // ============================================================
    // RD term selector
    // ============================================================

    private void setupLoanUnitSelector() {

        View unitLayout =
                (View) tvLoanUnit.getParent();

        if (unitLayout != null) {

            unitLayout.setOnClickListener(v ->
                    showLoanUnitDialog()
            );
        }

        tvLoanUnit.setOnClickListener(v ->
                showLoanUnitDialog()
        );
    }

    private void showLoanUnitDialog() {

        final String[] units = {
                "Months",
                "Years"
        };

        int selectedIndex =
                selectedLoanUnit.equalsIgnoreCase("Years")
                        ? 1
                        : 0;

        new AlertDialog.Builder(this)
                .setTitle("Select RD Term Unit")
                .setSingleChoiceItems(
                        units,
                        selectedIndex,
                        (dialog, which) -> {

                            selectedLoanUnit =
                                    units[which];

                            tvLoanUnit.setText(
                                    selectedLoanUnit
                            );

                            dialog.dismiss();
                        }
                )
                .show();
    }

    // ============================================================
    // Total months
    // ============================================================

    private int getTotalMonths() {

        String termText =
                tvLoanTerm
                        .getText()
                        .toString()
                        .trim();

        if (termText.isEmpty()) {
            return 0;
        }

        int term;

        try {

            term =
                    Integer.parseInt(termText);

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

    // ============================================================
    // Compounding selector
    // ============================================================

    private void setupCompoundingSelector() {

        etTimeInterest.setFocusable(false);
        etTimeInterest.setFocusableInTouchMode(false);
        etTimeInterest.setClickable(true);

        etTimeInterest.setOnClickListener(v ->
                showCompoundingDialog()
        );

        View parent =
                (View) etTimeInterest.getParent();

        if (parent != null) {

            parent.setOnClickListener(v ->
                    showCompoundingDialog()
            );
        }
    }

    private void showCompoundingDialog() {

        final String[] frequencyNames = {
                "Annually",
                "Half-Yearly",
                "Quarterly",
                "Monthly",
                "Daily"
        };

        int selectedIndex = 3;

        for (int i = 0; i < frequencyNames.length; i++) {

            if (frequencyNames[i].equals(
                    selectedCompounding
            )) {

                selectedIndex = i;
                break;
            }
        }

        new AlertDialog.Builder(this)
                .setTitle("Compounding Frequency")
                .setSingleChoiceItems(
                        frequencyNames,
                        selectedIndex,
                        (dialog, which) -> {

                            selectedCompounding =
                                    frequencyNames[which];

                            etTimeInterest.setText(
                                    selectedCompounding
                            );

                            dialog.dismiss();
                        }
                )
                .show();
    }

    // ============================================================
    // Date selector
    // ============================================================

    private void setupStartDateSelector() {

        View parent =
                (View) tvStartDate.getParent();

        if (parent != null) {

            parent.setOnClickListener(v ->
                    showDatePicker()
            );
        }

        tvStartDate.setOnClickListener(v ->
                showDatePicker()
        );
    }

    private void showDatePicker() {

        Calendar currentDate =
                selectedDate != null
                        ? selectedDate
                        : Calendar.getInstance();

        DatePickerDialog datePickerDialog =
                new DatePickerDialog(
                        this,
                        (DatePicker view,
                         int year,
                         int month,
                         int dayOfMonth) -> {

                            selectedDate =
                                    Calendar.getInstance();

                            selectedDate.set(
                                    Calendar.YEAR,
                                    year
                            );

                            selectedDate.set(
                                    Calendar.MONTH,
                                    month
                            );

                            selectedDate.set(
                                    Calendar.DAY_OF_MONTH,
                                    dayOfMonth
                            );

                            updateStartDateText();
                        },
                        currentDate.get(
                                Calendar.YEAR
                        ),
                        currentDate.get(
                                Calendar.MONTH
                        ),
                        currentDate.get(
                                Calendar.DAY_OF_MONTH
                        )
                );

        datePickerDialog.show();
    }

    private void updateStartDateText() {

        if (selectedDate == null) {
            return;
        }

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                );

        tvStartDate.setText(
                dateFormat.format(
                        selectedDate.getTime()
                )
        );
    }

    // ============================================================
    // Buttons
    // ============================================================

    private void setupButtons() {

        btnReset.setOnClickListener(v ->
                resetFields()
        );

        btnCalculate.setOnClickListener(v ->
                calculateRD()
        );
    }

    // ============================================================
    // Reset
    // ============================================================

    private void resetFields() {

        etLoanAmount.setText("");
        etInterestRate.setText("");

        selectedLoanUnit = "Months";
        tvLoanUnit.setText("Months");

        selectedCompounding = "Monthly";
        etTimeInterest.setText("Monthly");

        selectedCurrencyCode = "USD";
        selectedCurrencyCountry = "United States";

        tvCurrencyCode.setText("USD");

        setCurrencyFlag("USD");

        selectedDate =
                Calendar.getInstance();

        updateStartDateText();

        etLoanAmount.requestFocus();
    }

    // ============================================================
    // Calculate RD
    // ============================================================

    private void calculateRD() {

        // --------------------------------------------------------
        // Monthly investment
        // --------------------------------------------------------

        double monthlyDeposit =
                getInvestmentAmount();

        if (monthlyDeposit <= 0) {

            etLoanAmount.setError(
                    "Enter investment amount"
            );

            etLoanAmount.requestFocus();

            return;
        }

        // --------------------------------------------------------
        // Interest rate
        // --------------------------------------------------------

        String interestText =
                etInterestRate
                        .getText()
                        .toString()
                        .trim();

        if (interestText.isEmpty()) {

            etInterestRate.setError(
                    "Enter interest rate"
            );

            etInterestRate.requestFocus();

            return;
        }

        double annualRate;

        try {

            annualRate =
                    Double.parseDouble(
                            interestText
                    );

        } catch (Exception e) {

            etInterestRate.setError(
                    "Enter valid interest rate"
            );

            etInterestRate.requestFocus();

            return;
        }

        if (annualRate < 0 ||
                annualRate > 100) {

            etInterestRate.setError(
                    "Interest rate must be between 0 and 100"
            );

            etInterestRate.requestFocus();

            return;
        }

        // --------------------------------------------------------
        // Term
        // --------------------------------------------------------

        String termText =
                tvLoanTerm
                        .getText()
                        .toString()
                        .trim();

        if (termText.isEmpty()) {

            tvLoanTerm.setError(
                    "Enter RD term"
            );

            tvLoanTerm.requestFocus();

            return;
        }

        int enteredTerm;

        try {

            enteredTerm =
                    Integer.parseInt(termText);

        } catch (Exception e) {

            tvLoanTerm.setError(
                    "Enter a valid whole number"
            );

            tvLoanTerm.requestFocus();

            return;
        }

        if (enteredTerm <= 0) {

            tvLoanTerm.setError(
                    "RD term must be greater than 0"
            );

            tvLoanTerm.requestFocus();

            return;
        }

        if (selectedLoanUnit.equalsIgnoreCase("Years")
                && enteredTerm > 50) {

            tvLoanTerm.setError(
                    "Maximum RD term is 50 years"
            );

            tvLoanTerm.requestFocus();

            return;
        }

        if (selectedLoanUnit.equalsIgnoreCase("Months")
                && enteredTerm > 600) {

            tvLoanTerm.setError(
                    "Maximum RD term is 600 months"
            );

            tvLoanTerm.requestFocus();

            return;
        }

        int totalMonths =
                getTotalMonths();

        if (totalMonths <= 0) {

            tvLoanTerm.setError(
                    "Enter valid RD term"
            );

            tvLoanTerm.requestFocus();

            return;
        }

        // --------------------------------------------------------
        // Calculate maturity
        // --------------------------------------------------------

        double maturityValue =
                calculateRDMaturity(
                        monthlyDeposit,
                        annualRate,
                        totalMonths,
                        selectedCompounding
                );

        double totalInvestment =
                monthlyDeposit * totalMonths;

        double totalInterest =
                maturityValue -
                        totalInvestment;

        if (totalInterest < 0) {
            totalInterest = 0;
        }

        // --------------------------------------------------------
        // Open result
        // --------------------------------------------------------

        Intent intent =
                new Intent(
                        RdCalculatorActivity.this,
                        RdCalculatorResultActivity.class
                );

        intent.putExtra(
                "monthly_deposit",
                monthlyDeposit
        );

        intent.putExtra(
                "interest_rate",
                annualRate
        );

        intent.putExtra(
                "loan_term",
                termText
        );

        intent.putExtra(
                "loan_term_unit",
                selectedLoanUnit
        );

        intent.putExtra(
                "total_months",
                totalMonths
        );

        intent.putExtra(
                "compounding",
                selectedCompounding
        );

        intent.putExtra(
                "start_date",
                tvStartDate.getText().toString()
        );

        intent.putExtra(
                "total_investment",
                totalInvestment
        );

        intent.putExtra(
                "total_interest",
                totalInterest
        );

        intent.putExtra(
                "maturity_value",
                maturityValue
        );

        intent.putExtra(
                "currency_code",
                selectedCurrencyCode
        );

        intent.putExtra(
                "currency_country",
                selectedCurrencyCountry
        );

        startActivity(intent);
    }

    // ============================================================
    // RD maturity calculation
    // ============================================================

    private double calculateRDMaturity(
            double monthlyDeposit,
            double annualRate,
            int months,
            String compounding) {

        if (monthlyDeposit <= 0 ||
                months <= 0) {

            return 0;
        }

        if (annualRate <= 0) {

            return monthlyDeposit * months;
        }

        int compoundsPerYear;

        switch (compounding) {

            case "Annually":
                compoundsPerYear = 1;
                break;

            case "Half-Yearly":
                compoundsPerYear = 2;
                break;

            case "Quarterly":
                compoundsPerYear = 4;
                break;

            case "Daily":
                compoundsPerYear = 365;
                break;

            case "Monthly":
            default:
                compoundsPerYear = 12;
                break;
        }

        double annualRateDecimal =
                annualRate / 100.0;

        /*
         * Convert the selected annual compounding
         * method into an equivalent monthly growth rate.
         */
        double monthlyRate =
                Math.pow(
                        1.0 +
                                annualRateDecimal /
                                        compoundsPerYear,
                        compoundsPerYear / 12.0
                ) - 1.0;

        double maturityValue = 0;

        for (int month = 0;
             month < months;
             month++) {

            int remainingMonths =
                    months - month;

            double installmentValue =
                    monthlyDeposit *
                            Math.pow(
                                    1.0 + monthlyRate,
                                    remainingMonths
                            );

            maturityValue +=
                    installmentValue;
        }

        return maturityValue;
    }

    // ============================================================
    // Banner
    // ============================================================

    private void setUpAd() {

        if (!Util.isInternetAvailable(this)
                || bannerContainer == null) {

            return;
        }

        bannerAdView =
                new AdView(this);

        if (bannerAdView.getParent() != null) {

            ((ViewGroup)
                    bannerAdView.getParent())
                    .removeView(
                            bannerAdView
                    );
        }

        bannerContainer.addView(
                bannerAdView
        );

        AdsHelper.loadAdaptiveBanner(
                bannerAdView,
                this,
                getString(
                        R.string.rd_calc_banner
                )
        );
    }

    // ============================================================
    // Resume
    // ============================================================

    @Override
    protected void onResume() {

        super.onResume();

        Util.hide(this);
    }
}