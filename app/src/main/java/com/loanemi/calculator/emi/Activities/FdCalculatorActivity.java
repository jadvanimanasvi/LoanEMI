package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

public class FdCalculatorActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private EditText etLoanAmount;
    private EditText etInterestRate;
    private EditText tvLoanTerm;

    private TextView tvLoanUnit;
    private TextView etTimeInterest;
    private TextView tvStartDate;

    private LinearLayout btnReset;
    private LinearLayout btnCalculate;

    private FrameLayout adContainer;
    private AdView bannerAdView;

    private LinearLayout currencySelector;
    private ImageView ivCurrencyFlag;
    private TextView tvCurrencyCode;

    private String selectedCurrencyCode = "USD";
    private String selectedCurrencyCountry = "United States";
    private String selectedCurrencySymbol = "$";

    private int selectedCurrencyFlag = R.drawable.icn_flagus;

    private static final int REQUEST_CURRENCY = 1001;

    private Calendar selectedDate;

    private String selectedLoanUnit = "Month";

    private int compoundingFrequency = 1;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final NumberFormat indianNumberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));

    private LoanHistoryManager loanHistoryManager;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fd_calculator);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        loanHistoryManager = new LoanHistoryManager(this);

        setupToolbar();

        setupDefaultValues();

        setupLoanAmountFormatting();

        setupClickListeners();

        setupCurrencySelector();

        setUpAd();
    }


    private void initViews() {

        toolbar = findViewById(R.id.toolbar);

        etLoanAmount = findViewById(R.id.etLoanAmount);

        etInterestRate = findViewById(R.id.etInterestRate);

        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvLoanUnit = findViewById(R.id.tvLoanUnit);

        etTimeInterest = findViewById(R.id.etTimeInterest);

        tvStartDate = findViewById(R.id.tvStartDate);

        btnReset = findViewById(R.id.btnReset);

        btnCalculate = findViewById(R.id.btnCalculate);

        adContainer = findViewById(R.id.bannerContainer);

        currencySelector = findViewById(R.id.currencySelector);

        ivCurrencyFlag = findViewById(R.id.ivCurrencyFlag);

        tvCurrencyCode = findViewById(R.id.tvCurrencyCode);


        if (etLoanAmount != null) {

            etLoanAmount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }


        if (etInterestRate != null) {

            etInterestRate.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }


        if (tvLoanTerm != null) {

            tvLoanTerm.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }


    private void setupToolbar() {

        if (toolbar == null) {
            return;
        }

        if (toolbar.getChildCount() > 0) {

            View firstChild = toolbar.getChildAt(0);

            if (firstChild instanceof ImageView) {

                ImageView backButton = (ImageView) firstChild;

                backButton.setOnClickListener(v -> finish());
            }
        }
    }


    private void setupDefaultValues() {

        etLoanAmount.setText("");

        etInterestRate.setText("");

        tvLoanTerm.setText("");


        selectedLoanUnit = "Month";

        tvLoanUnit.setText(selectedLoanUnit);


        compoundingFrequency = 1;

        etTimeInterest.setText("1");


        selectedDate = Calendar.getInstance();

        updateStartDateText();


        setupDefaultCurrency();
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

                    String input = editable.toString();

                    String cleanInput = input.replace(",", "").replaceAll("[^0-9.]", "");


                    if (cleanInput.isEmpty()) {

                        etLoanAmount.setText("");

                        return;
                    }


                    /*
                     * Keep decimal part if user enters one.
                     */
                    String[] parts = cleanInput.split("\\.", -1);

                    String integerPart = parts[0];

                    String decimalPart = parts.length > 1 ? parts[1] : null;


                    if (!integerPart.isEmpty()) {

                        try {

                            long value = Long.parseLong(integerPart);

                            String formatted = indianNumberFormat.format(value);


                            if (decimalPart != null) {

                                formatted = formatted + "." + decimalPart;
                            }


                            if (!formatted.equals(editable.toString())) {

                                etLoanAmount.setText(formatted);

                                etLoanAmount.setSelection(formatted.length());
                            }

                        } catch (NumberFormatException ignored) {
                        }
                    }

                } finally {

                    isFormatting = false;
                }
            }
        });
    }


    private void setupClickListeners() {

        if (tvStartDate != null) {

            tvStartDate.setOnClickListener(v -> showDatePicker());

            View startDateParent = (View) tvStartDate.getParent();

            if (startDateParent != null) {

                startDateParent.setOnClickListener(v -> showDatePicker());
            }
        }


        if (tvLoanUnit != null) {

            tvLoanUnit.setOnClickListener(v -> showLoanUnitDialog());

            View unitParent = (View) tvLoanUnit.getParent();

            if (unitParent != null) {

                unitParent.setOnClickListener(v -> showLoanUnitDialog());
            }
        }


        if (etTimeInterest != null) {

            etTimeInterest.setOnClickListener(v -> showCompoundingDialog());

            View frequencyParent = (View) etTimeInterest.getParent();

            if (frequencyParent != null) {

                frequencyParent.setOnClickListener(v -> showCompoundingDialog());
            }
        }


        if (btnReset != null) {

            btnReset.setOnClickListener(v -> resetFields());
        }


        if (btnCalculate != null) {

            btnCalculate.setOnClickListener(v -> calculateFD());
        }
    }


    private void setupCurrencySelector() {

        if (currencySelector == null) {
            return;
        }

        currencySelector.setOnClickListener(v -> openCurrencyActivity());
    }


    private void openCurrencyActivity() {

        Intent intent = new Intent(FdCalculatorActivity.this, CurrencyUnitActivity.class);


        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);


        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "FD_CALCULATOR");


        startActivityForResult(intent, REQUEST_CURRENCY);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode != REQUEST_CURRENCY || resultCode != RESULT_OK || data == null) {

            return;
        }


        String code = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE);


        String country = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_COUNTRY);


        String symbol = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_CURRENCY_ICON);


        int flag = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_FLAG, R.drawable.icn_flagus);


        /*
         * Currency code
         */
        if (code != null && !code.trim().isEmpty()) {

            selectedCurrencyCode = code.trim().toUpperCase(Locale.US);
        }


        /*
         * Currency country/name
         */
        if (country != null && !country.trim().isEmpty()) {

            selectedCurrencyCountry = country.trim();
        }


        /*
         * Currency symbol.
         *
         * CurrencyUnitActivity may return the symbol
         * as a String. If your current CurrencyUnitActivity
         * returns an integer drawable instead, use the
         * EXTRA_SELECTED_CURRENCY_ICON integer value below.
         */
        if (symbol != null && !symbol.trim().isEmpty()) {

            selectedCurrencySymbol = symbol.trim();
        }


        /*
         * Flag
         */
        selectedCurrencyFlag = flag;


        updateCurrencyUI();
    }


    private void updateCurrencyUI() {

        if (tvCurrencyCode != null) {

            tvCurrencyCode.setText(selectedCurrencyCode);
        }


        if (ivCurrencyFlag != null) {

            ivCurrencyFlag.setImageResource(selectedCurrencyFlag);
        }
    }


    private void setupDefaultCurrency() {

        selectedCurrencyCode = "USD";

        selectedCurrencyCountry = "United States";

        selectedCurrencySymbol = "$";

        selectedCurrencyFlag = R.drawable.icn_flagus;

        updateCurrencyUI();
    }


    private void showLoanUnitDialog() {

        final String[] units = {"Month", "Year"};


        int selectedPosition = selectedLoanUnit.equalsIgnoreCase("Month") ? 0 : 1;


        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.select_investment_term_unit).setSingleChoiceItems(units, selectedPosition, (dialogInterface, which) -> {

            selectedLoanUnit = units[which];

            tvLoanUnit.setText(selectedLoanUnit);

            dialogInterface.dismiss();
        }).create();


        dialog.show();
    }


    private void showCompoundingDialog() {


        String[] frequencyNames = {"1", "4", "12"};


        final int[] frequencyValues = {1, 4, 12};


        int selectedPosition = getFrequencyPosition();


        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(R.string.compounding_frequency).setSingleChoiceItems(frequencyNames, selectedPosition, (dialogInterface, which) -> {

            compoundingFrequency = frequencyValues[which];

            etTimeInterest.setText(String.valueOf(compoundingFrequency));

            dialogInterface.dismiss();
        }).setNegativeButton(R.string.cancel, null).create();


        dialog.show();
    }


    private int getFrequencyPosition() {

        switch (compoundingFrequency) {

            case 4:
                return 1;

            case 12:
                return 2;

            case 1:
            default:
                return 0;
        }
    }


    private void showDatePicker() {

        if (selectedDate == null) {

            selectedDate = Calendar.getInstance();
        }


        int year = selectedDate.get(Calendar.YEAR);

        int month = selectedDate.get(Calendar.MONTH);

        int day = selectedDate.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {

            selectedDate.set(Calendar.YEAR, selectedYear);

            selectedDate.set(Calendar.MONTH, selectedMonth);

            selectedDate.set(Calendar.DAY_OF_MONTH, selectedDay);

            updateStartDateText();
        }, year, month, day);


        datePickerDialog.show();
    }


    private void updateStartDateText() {

        if (selectedDate != null) {

            tvStartDate.setText(dateFormat.format(selectedDate.getTime()));
        }
    }


    private boolean validateInputs() {

        String amountText = etLoanAmount.getText().toString().trim().replace(",", "");


        if (amountText.isEmpty()) {

            etLoanAmount.setError("Enter investment amount");

            etLoanAmount.requestFocus();

            return false;
        }


        double amount;


        try {

            amount = Double.parseDouble(amountText);

        } catch (NumberFormatException e) {

            etLoanAmount.setError("Enter a valid amount");

            etLoanAmount.requestFocus();

            return false;
        }


        if (amount <= 0) {

            etLoanAmount.setError("Amount must be greater than 0");

            etLoanAmount.requestFocus();

            return false;
        }


        String interestText = etInterestRate.getText().toString().trim();


        if (interestText.isEmpty()) {

            etInterestRate.setError("Enter interest rate");

            etInterestRate.requestFocus();

            return false;
        }


        double interestRate;


        try {

            interestRate = Double.parseDouble(interestText);

        } catch (NumberFormatException e) {

            etInterestRate.setError("Enter a valid interest rate");

            etInterestRate.requestFocus();

            return false;
        }


        if (interestRate < 0) {

            etInterestRate.setError("Interest rate cannot be negative");

            etInterestRate.requestFocus();

            return false;
        }


        if (interestRate > 100) {

            etInterestRate.setError("Interest rate cannot exceed 100%");

            etInterestRate.requestFocus();

            return false;
        }


        String termText = tvLoanTerm.getText().toString().trim();


        if (termText.isEmpty()) {

            Toast.makeText(this, "Please enter investment term", Toast.LENGTH_SHORT).show();

            tvLoanTerm.requestFocus();

            return false;
        }


        int term;


        try {

            term = Integer.parseInt(termText);

        } catch (NumberFormatException e) {

            Toast.makeText(this, "Invalid investment term", Toast.LENGTH_SHORT).show();

            return false;
        }


        if (term <= 0) {

            Toast.makeText(this, "Investment term must be greater than 0", Toast.LENGTH_SHORT).show();

            return false;
        }


        if (selectedLoanUnit.equalsIgnoreCase("Year")) {

            if (term > 30) {

                Toast.makeText(this, "Investment term cannot exceed 30 years", Toast.LENGTH_SHORT).show();

                return false;
            }

        } else {

            if (term > 360) {

                Toast.makeText(this, "Investment term cannot exceed 360 months", Toast.LENGTH_SHORT).show();

                return false;
            }
        }


        if (compoundingFrequency <= 0) {

            Toast.makeText(this, "Please select compounding frequency", Toast.LENGTH_SHORT).show();

            return false;
        }


        return true;
    }


    private void calculateFD() {

        if (!validateInputs()) {
            return;
        }


        String amountText = etLoanAmount.getText().toString().trim().replace(",", "");


        double principal = Double.parseDouble(amountText);


        double annualRate = Double.parseDouble(etInterestRate.getText().toString().trim());


        int term = Integer.parseInt(tvLoanTerm.getText().toString().trim());


        int frequency = compoundingFrequency;


        double timeInYears;


        if (selectedLoanUnit.equalsIgnoreCase("Year")) {

            timeInYears = term;

        } else {

            timeInYears = term / 12.0;
        }


        double rateDecimal = annualRate / 100.0;


        double maturityAmount;


        if (annualRate == 0) {

            maturityAmount = principal;

        } else {

            double base = 1.0 + (rateDecimal / frequency);


            double exponent = frequency * timeInYears;


            maturityAmount = principal * Math.pow(base, exponent);
        }


        double totalInterest = maturityAmount - principal;


        if (totalInterest < 0 && totalInterest > -0.01) {

            totalInterest = 0;
        }


        Calendar maturityDate = calculateMaturityDate(selectedDate, term, selectedLoanUnit);


        String formattedPrincipal = formatCurrency(principal);


        String formattedInterest = formatCurrency(totalInterest);


        String formattedMaturity = formatCurrency(maturityAmount);


        String formattedStartDate = dateFormat.format(selectedDate.getTime());


        String formattedMaturityDate = dateFormat.format(maturityDate.getTime());


        Intent intent = new Intent(FdCalculatorActivity.this, FdCalculatorResultActivity.class);


        intent.putExtra("investment_amount", principal);


        intent.putExtra("interest_rate", annualRate);


        intent.putExtra("investment_term", term);


        intent.putExtra("investment_term_unit", selectedLoanUnit);


        intent.putExtra("compounding_frequency", frequency);


        intent.putExtra("time_in_years", timeInYears);


        intent.putExtra("total_interest", totalInterest);


        intent.putExtra("maturity_amount", maturityAmount);


        intent.putExtra("start_date", formattedStartDate);


        intent.putExtra("maturity_date", formattedMaturityDate);


        intent.putExtra("formatted_principal", formattedPrincipal);


        intent.putExtra("formatted_interest", formattedInterest);


        intent.putExtra("formatted_maturity", formattedMaturity);


        /*
         * Currency information
         */
        intent.putExtra("currency_code", selectedCurrencyCode);


        intent.putExtra("currency_country", selectedCurrencyCountry);


        intent.putExtra("currency_symbol", selectedCurrencySymbol);


        intent.putExtra("currency_flag", selectedCurrencyFlag);


        intent.putExtra("loan_type", "Fixed Deposit");


        startActivity(intent);
    }


    private Calendar calculateMaturityDate(Calendar startDate, int term, String unit) {

        Calendar result = (Calendar) startDate.clone();


        int originalDay = result.get(Calendar.DAY_OF_MONTH);


        if (unit.equalsIgnoreCase("Year")) {

            int originalMonth = result.get(Calendar.MONTH);


            result.set(Calendar.DAY_OF_MONTH, 1);


            result.add(Calendar.YEAR, term);


            result.set(Calendar.MONTH, originalMonth);


            int maxDay = result.getActualMaximum(Calendar.DAY_OF_MONTH);


            result.set(Calendar.DAY_OF_MONTH, Math.min(originalDay, maxDay));

        } else {

            result.set(Calendar.DAY_OF_MONTH, 1);


            result.add(Calendar.MONTH, term);


            int maxDay = result.getActualMaximum(Calendar.DAY_OF_MONTH);


            result.set(Calendar.DAY_OF_MONTH, Math.min(originalDay, maxDay));
        }


        return result;
    }


    private String formatCurrency(double amount) {

        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());


        formatter.setMinimumFractionDigits(2);

        formatter.setMaximumFractionDigits(2);


        return formatter.format(amount);
    }


    private void resetFields() {

        etLoanAmount.setText("");

        etInterestRate.setText("");

        tvLoanTerm.setText("");


        selectedLoanUnit = "Month";

        tvLoanUnit.setText("Month");


        compoundingFrequency = 1;

        etTimeInterest.setText("1");


        selectedDate = Calendar.getInstance();

        updateStartDateText();


        setupDefaultCurrency();


        etLoanAmount.requestFocus();


        Toast.makeText(this, "Fields reset", Toast.LENGTH_SHORT).show();
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


        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.fd_loan_banner));
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