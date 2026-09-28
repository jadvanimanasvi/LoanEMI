package com.example.loanemi.Activities.Activities;

import static com.example.loanemi.Activities.utils.Util.setupEdgeToEdge;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AutoLoanActivity extends AppCompatActivity {

    // =========================================================
    // VIEWS
    // =========================================================

    private EditText etLoanAmount;
    private EditText etInterestRate;
    private EditText tvLoanTerm;

    private TextView tvLoanUnit;
    private TextView tvStartDate;
    private TextView tvCurrencyCode;

    private ImageView ivFromFlag;
    private ImageView ivBack;

    private LinearLayout selectCountry;
    private LinearLayout loanUnitSelector;
    private LinearLayout startDateSelector;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;


    // =========================================================
    // CURRENCY
    // =========================================================

    private String selectedCurrencyCode = "USD";

    private String selectedCurrencyName = "US Dollar";

    /*
     * IMPORTANT:
     *
     * This stores COUNTRY FLAG only.
     *
     * Example:
     * USD -> icn_flagus
     * INR -> icn_flaghindi
     * GBP -> icn_flagengland
     */
    private int selectedCurrencyFlag = R.drawable.icn_flagus;


    // =========================================================
    // LOAN UNIT
    // =========================================================

    private String selectedLoanUnit = "Month";


    // =========================================================
    // DATE
    // =========================================================

    private Calendar selectedDate;


    // =========================================================
    // CONSTANTS
    // =========================================================

    private static final int MAX_MONTHS = 360;

    private static final int MAX_YEARS = 30;

    private static final int REQUEST_CURRENCY = 1001;

    private static final String DATE_FORMAT = "dd/MM/yyyy";


    // =========================================================
    // NUMBER FORMAT
    // =========================================================

    private final NumberFormat indianNumberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_auto_loan);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        setupDefaultValues();

        setupLoanAmountFormatting();

        setupListeners();
    }


    // =========================================================
    // ON RESUME
    // =========================================================

    @Override
    protected void onResume() {
        super.onResume();

        Util.hide(this);
    }


    // =========================================================
    // INIT VIEWS
    // =========================================================

    private void initViews() {

        etLoanAmount = findViewById(R.id.etLoanAmount);

        etInterestRate = findViewById(R.id.etInterestRate);

        tvLoanTerm = findViewById(R.id.tvLoanTerm);

        tvLoanUnit = findViewById(R.id.tvLoanUnit);

        tvStartDate = findViewById(R.id.tvStartDate);

        tvCurrencyCode = findViewById(R.id.tvCurrencyCode);

        /*
         * This ImageView displays COUNTRY FLAG.
         */
        ivFromFlag = findViewById(R.id.ivFromFlag);

        ivBack = findViewById(R.id.ivBack);

        selectCountry = findViewById(R.id.selectCountry);

        loanUnitSelector = findViewById(R.id.loanUnitSelector);

        startDateSelector = findViewById(R.id.startDateSelector);

        btnReset = findViewById(R.id.btnReset);

        btnCalculate = findViewById(R.id.btnCalculate);
    }


    // =========================================================
    // DEFAULT VALUES
    // =========================================================

    private void setupDefaultValues() {

        // -----------------------------------------------------
        // Default Currency
        // -----------------------------------------------------

        selectedCurrencyCode = "USD";

        selectedCurrencyName = "US Dollar";

        selectedCurrencyFlag = R.drawable.icn_flagus;


        // Show USD
        tvCurrencyCode.setText(selectedCurrencyCode);


        // Show US country flag
        ivFromFlag.setImageResource(selectedCurrencyFlag);


        // -----------------------------------------------------
        // Default Loan Unit
        // -----------------------------------------------------

        selectedLoanUnit = "Month";

        tvLoanUnit.setText(selectedLoanUnit);


        // -----------------------------------------------------
        // Default Date
        // -----------------------------------------------------

        selectedDate = Calendar.getInstance();

        updateStartDateText();
    }


    // =========================================================
    // LISTENERS
    // =========================================================

    private void setupListeners() {

        // -----------------------------------------------------
        // Back
        // -----------------------------------------------------

        ivBack.setOnClickListener(v -> finish());


        // -----------------------------------------------------
        // Currency Selector
        // -----------------------------------------------------

        selectCountry.setOnClickListener(v -> openCurrencyActivity());


        // -----------------------------------------------------
        // Loan Unit
        // -----------------------------------------------------

        loanUnitSelector.setOnClickListener(v -> showLoanUnitDialog());


        // -----------------------------------------------------
        // Start Date
        // -----------------------------------------------------

        startDateSelector.setOnClickListener(v -> showDatePicker());


        // -----------------------------------------------------
        // Reset
        // -----------------------------------------------------

        btnReset.setOnClickListener(v -> resetFields());


        // -----------------------------------------------------
        // Calculate
        // -----------------------------------------------------

        btnCalculate.setOnClickListener(v -> calculateLoan());
    }


    // =========================================================
    // OPEN CURRENCY ACTIVITY
    // =========================================================

    private void openCurrencyActivity() {

        Intent intent = new Intent(AutoLoanActivity.this, CurrencyUnitActivity.class);


        /*
         * Send current currency code.
         *
         * Example:
         * USD
         * INR
         * GBP
         */
        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);


        /*
         * Tell CurrencyUnitActivity that this
         * selection is coming from Auto Loan.
         */
        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "AUTO_LOAN");


        startActivityForResult(intent, REQUEST_CURRENCY);
    }


    // =========================================================
    // RECEIVE SELECTED CURRENCY
    // =========================================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);


        // -----------------------------------------------------
        // Check Result
        // -----------------------------------------------------

        if (requestCode != REQUEST_CURRENCY || resultCode != RESULT_OK || data == null) {

            return;
        }


        // -----------------------------------------------------
        // Get Currency Code
        // -----------------------------------------------------

        String code = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE);


        // -----------------------------------------------------
        // Get Currency Name
        // -----------------------------------------------------

        String name = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_COUNTRY);


        // -----------------------------------------------------
        // Get COUNTRY FLAG
        // -----------------------------------------------------

        int flag = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_FLAG, R.drawable.icn_flagus);


        // =====================================================
        // UPDATE CURRENCY CODE
        // =====================================================

        if (code != null && !code.trim().isEmpty()) {

            selectedCurrencyCode = code.trim().toUpperCase(Locale.US);


            /*
             * Example:
             *
             * USD
             * INR
             * GBP
             * CNY
             */
            tvCurrencyCode.setText(selectedCurrencyCode);
        }


        // =====================================================
        // UPDATE CURRENCY NAME
        // =====================================================

        if (name != null && !name.trim().isEmpty()) {

            selectedCurrencyName = name.trim();
        }


        // =====================================================
        // UPDATE COUNTRY FLAG
        // =====================================================

        selectedCurrencyFlag = flag;


        /*
         * IMPORTANT:
         *
         * AutoLoanActivity shows COUNTRY FLAG.
         *
         * It does NOT show the currency icon here.
         */
        ivFromFlag.setImageResource(selectedCurrencyFlag);
    }


    // =========================================================
    // LOAN UNIT DIALOG
    // =========================================================

    private void showLoanUnitDialog() {

        final String[] units = {"Month", "Year"};


        int selectedPosition = selectedLoanUnit.equals("Month") ? 0 : 1;


        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Select Loan Term Unit").setSingleChoiceItems(units, selectedPosition, (dialogInterface, which) -> {

            selectedLoanUnit = units[which];


            tvLoanUnit.setText(selectedLoanUnit);


            dialogInterface.dismiss();
        }).create();


        dialog.show();
    }


    // =========================================================
    // DATE PICKER
    // =========================================================

    private void showDatePicker() {

        Calendar calendar = selectedDate != null ? selectedDate : Calendar.getInstance();


        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

            selectedDate = Calendar.getInstance();


            selectedDate.set(Calendar.YEAR, year);


            selectedDate.set(Calendar.MONTH, month);


            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            updateStartDateText();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.show();
    }


    // =========================================================
    // UPDATE START DATE
    // =========================================================

    private void updateStartDateText() {

        if (selectedDate == null) {
            return;
        }


        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


        tvStartDate.setText(sdf.format(selectedDate.getTime()));
    }


    // =========================================================
    // LOAN AMOUNT FORMATTING
    // =========================================================

    private void setupLoanAmountFormatting() {

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

                    /*
                     * Remove commas and
                     * non-numeric characters.
                     */
                    String cleanInput = editable.toString().replace(",", "").replaceAll("[^0-9]", "");


                    if (!cleanInput.isEmpty()) {

                        try {

                            long value = Long.parseLong(cleanInput);


                            String formatted = indianNumberFormat.format(value);


                            etLoanAmount.setText(formatted);


                            etLoanAmount.setSelection(formatted.length());

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


    // =========================================================
    // CALCULATE LOAN
    // =========================================================

    private void calculateLoan() {

        // =====================================================
        // LOAN AMOUNT
        // =====================================================

        String amountText = etLoanAmount.getText().toString().trim();


        /*
         * Convert:
         *
         * 20,00,000
         *
         * into:
         *
         * 2000000
         */
        amountText = amountText.replace(",", "");


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


        // =====================================================
        // INTEREST RATE
        // =====================================================

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


        // =====================================================
        // LOAN TERM
        // =====================================================

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


        // =====================================================
        // CONVERT TERM TO MONTHS
        // =====================================================

        int totalMonths;


        if (selectedLoanUnit.equals("Year")) {

            if (term > MAX_YEARS) {

                tvLoanTerm.setError("Maximum loan term is 30 years");

                tvLoanTerm.requestFocus();

                return;
            }


            totalMonths = term * 12;

        } else {

            if (term > MAX_MONTHS) {

                tvLoanTerm.setError("Maximum loan term is 360 months");

                tvLoanTerm.requestFocus();

                return;
            }


            totalMonths = term;
        }


        // =====================================================
        // EMI CALCULATION
        // =====================================================

        double monthlyRate = annualRate / 12.0 / 100.0;


        double monthlyEMI;


        if (monthlyRate == 0) {

            monthlyEMI = principal / totalMonths;

        } else {

            double power = Math.pow(1 + monthlyRate, totalMonths);


            monthlyEMI = principal * monthlyRate * power / (power - 1);
        }


        // =====================================================
        // TOTAL PAYMENT
        // =====================================================

        double totalPayment = monthlyEMI * totalMonths;


        // =====================================================
        // TOTAL INTEREST
        // =====================================================

        double totalInterest = totalPayment - principal;


        if (totalInterest < 0 && totalInterest > -0.01) {

            totalInterest = 0;
        }


        // =====================================================
        // PAYOFF DATE
        // =====================================================

        Calendar payoffCalendar = (Calendar) selectedDate.clone();


        payoffCalendar.add(Calendar.MONTH, totalMonths);


        String startDate = formatDate(selectedDate);


        String payoffDate = formatDate(payoffCalendar);


        // =====================================================
        // OPEN RESULT ACTIVITY
        // =====================================================

        Intent intent = new Intent(AutoLoanActivity.this, AutoLoanResultActivity.class);


        // -----------------------------------------------------
        // Loan Amount
        // -----------------------------------------------------

        intent.putExtra("loan_amount", principal);


        // -----------------------------------------------------
        // Interest Rate
        // -----------------------------------------------------

        intent.putExtra("interest_rate", annualRate);


        // -----------------------------------------------------
        // Loan Term
        // -----------------------------------------------------

        intent.putExtra("loan_term", term);


        // -----------------------------------------------------
        // Loan Term Unit
        // -----------------------------------------------------

        intent.putExtra("loan_term_unit", selectedLoanUnit);


        // -----------------------------------------------------
        // Total Months
        // -----------------------------------------------------

        intent.putExtra("total_months", totalMonths);


        // -----------------------------------------------------
        // Monthly EMI
        // -----------------------------------------------------

        intent.putExtra("monthly_emi", monthlyEMI);


        // -----------------------------------------------------
        // Total Interest
        // -----------------------------------------------------

        intent.putExtra("total_interest", totalInterest);


        // -----------------------------------------------------
        // Total Payment
        // -----------------------------------------------------

        intent.putExtra("total_payment", totalPayment);


        // -----------------------------------------------------
        // Start Date
        // -----------------------------------------------------

        intent.putExtra("start_date", startDate);


        // -----------------------------------------------------
        // Payoff Date
        // -----------------------------------------------------

        intent.putExtra("payoff_date", payoffDate);


        // -----------------------------------------------------
        // Currency Code
        // -----------------------------------------------------

        intent.putExtra("currency_code", selectedCurrencyCode);


        // -----------------------------------------------------
        // Currency Name
        // -----------------------------------------------------

        intent.putExtra("currency_name", selectedCurrencyName);


        // -----------------------------------------------------
        // Country Flag
        // -----------------------------------------------------

        intent.putExtra("currency_flag", selectedCurrencyFlag);


        // -----------------------------------------------------
        // Loan Type
        // -----------------------------------------------------

        intent.putExtra("loan_type", "Auto Loan");


        startActivity(intent);
    }


    // =========================================================
    // DATE FORMAT
    // =========================================================

    private String formatDate(Calendar calendar) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


        return sdf.format(calendar.getTime());
    }


    // =========================================================
    // RESET
    // =========================================================

    private void resetFields() {

        // -----------------------------------------------------
        // Clear Inputs
        // -----------------------------------------------------

        etLoanAmount.setText("");

        etInterestRate.setText("");

        tvLoanTerm.setText("");


        // -----------------------------------------------------
        // Clear Errors
        // -----------------------------------------------------

        etLoanAmount.setError(null);

        etInterestRate.setError(null);

        tvLoanTerm.setError(null);


        // -----------------------------------------------------
        // Reset Loan Unit
        // -----------------------------------------------------

        selectedLoanUnit = "Month";

        tvLoanUnit.setText("Month");


        // -----------------------------------------------------
        // Reset Currency
        // -----------------------------------------------------

        selectedCurrencyCode = "USD";

        selectedCurrencyName = "US Dollar";


        // US COUNTRY FLAG
        selectedCurrencyFlag = R.drawable.icn_flagus;


        // Show USD
        tvCurrencyCode.setText("USD");


        // Show US FLAG
        ivFromFlag.setImageResource(selectedCurrencyFlag);


        // -----------------------------------------------------
        // Reset Date
        // -----------------------------------------------------

        selectedDate = Calendar.getInstance();


        updateStartDateText();

        etLoanAmount.requestFocus();
    }
}