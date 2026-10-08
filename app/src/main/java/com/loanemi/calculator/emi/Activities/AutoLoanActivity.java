package com.loanemi.calculator.emi.Activities;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;
import android.app.DatePickerDialog;
import android.content.Context;
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
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.LoanHistoryManager;
import com.loanemi.calculator.emi.utils.Util;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AutoLoanActivity extends AppCompatActivity {

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
    private String selectedCurrencyCode = "USD";
    private String selectedCurrencyName = "US Dollar";
    private int selectedCurrencyFlag = R.drawable.icn_flagus;
    private String selectedLoanUnit = "Month";
    private Calendar selectedDate;
    private static final int MAX_MONTHS = 360;
    private static final int MAX_YEARS = 30;
    private static final int REQUEST_CURRENCY = 1001;
    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private final NumberFormat indianNumberFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

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

        tvStartDate = findViewById(R.id.tvStartDate);

        tvCurrencyCode = findViewById(R.id.tvCurrencyCode);

        ivFromFlag = findViewById(R.id.ivFromFlag);

        ivBack = findViewById(R.id.ivBack);

        selectCountry = findViewById(R.id.selectCountry);

        loanUnitSelector = findViewById(R.id.loanUnitSelector);

        startDateSelector = findViewById(R.id.startDateSelector);

        btnReset = findViewById(R.id.btnReset);

        btnCalculate = findViewById(R.id.btnCalculate);
    }

    private void setupDefaultValues() {

        selectedCurrencyCode = "USD";

        selectedCurrencyName = "US Dollar";

        selectedCurrencyFlag = R.drawable.icn_flagus;

        tvCurrencyCode.setText(selectedCurrencyCode);

        ivFromFlag.setImageResource(selectedCurrencyFlag);

        selectedLoanUnit = "Month";

        tvLoanUnit.setText(selectedLoanUnit);

        selectedDate = Calendar.getInstance();

        updateStartDateText();
    }

    private void setupListeners() {

        ivBack.setOnClickListener(v -> finish());

        selectCountry.setOnClickListener(v -> openCurrencyActivity());

        loanUnitSelector.setOnClickListener(v -> showLoanUnitDialog());

        startDateSelector.setOnClickListener(v -> showDatePicker());

        btnReset.setOnClickListener(v -> resetFields());

        btnCalculate.setOnClickListener(v -> calculateLoan());
    }

    private void openCurrencyActivity() {

        Intent intent = new Intent(AutoLoanActivity.this, CurrencyUnitActivity.class);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "AUTO_LOAN");

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

        int flag = data.getIntExtra(CurrencyUnitActivity.EXTRA_SELECTED_FLAG, R.drawable.icn_flagus);

        if (code != null && !code.trim().isEmpty()) {

            selectedCurrencyCode = code.trim().toUpperCase(Locale.US);

            tvCurrencyCode.setText(selectedCurrencyCode);
        }

        if (name != null && !name.trim().isEmpty()) {

            selectedCurrencyName = name.trim();
        }

        selectedCurrencyFlag = flag;

        ivFromFlag.setImageResource(selectedCurrencyFlag);
    }

    private void showLoanUnitDialog() {

        final String[] units = {"Month", "Year"};

        int selectedPosition = selectedLoanUnit.equalsIgnoreCase("Month") ? 0 : 1;

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle("Select Loan Term Unit").setSingleChoiceItems(units, selectedPosition, (dialogInterface, which) -> {

            selectedLoanUnit = units[which];

            tvLoanUnit.setText(selectedLoanUnit);

            dialogInterface.dismiss();
        }).create();

        dialog.show();
    }

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

    private void updateStartDateText() {

        if (selectedDate == null) {
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

        tvStartDate.setText(sdf.format(selectedDate.getTime()));
    }

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

    private void calculateLoan() {

        String amountText = etLoanAmount.getText().toString().trim();

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

        double monthlyRate = annualRate / 12.0 / 100.0;

        double monthlyEMI;

        if (monthlyRate == 0) {

            monthlyEMI = principal / totalMonths;

        } else {

            double power = Math.pow(1 + monthlyRate, totalMonths);

            monthlyEMI = principal * monthlyRate * power / (power - 1);
        }

        double totalPayment = monthlyEMI * totalMonths;

        double totalInterest = totalPayment - principal;

        if (totalInterest < 0 && totalInterest > -0.01) {

            totalInterest = 0;
        }

        Calendar payoffCalendar = (Calendar) selectedDate.clone();

        payoffCalendar.add(Calendar.MONTH, totalMonths);

        String startDate = formatDate(selectedDate);

        String payoffDate = formatDate(payoffCalendar);

        String currencySymbol = getCurrencySymbol(selectedCurrencyCode);

        LoanHistoryManager historyManager = new LoanHistoryManager(AutoLoanActivity.this);

        historyManager.addHistory(

                "Auto Loan",

                startDate,

                principal,

                annualRate,

                term,

                selectedLoanUnit,

                totalMonths,

                monthlyEMI,

                totalInterest,

                totalPayment,

                startDate,

                selectedCurrencyCode,

                currencySymbol,

                R.drawable.ic_car);


        Intent intent = new Intent(AutoLoanActivity.this, AutoLoanResultActivity.class);

        intent.putExtra("loan_amount", principal);

        intent.putExtra("interest_rate", annualRate);

        intent.putExtra("loan_term", term);

        intent.putExtra("loan_term_unit", selectedLoanUnit);

        intent.putExtra("total_months", totalMonths);

        intent.putExtra("monthly_emi", monthlyEMI);

        intent.putExtra("total_interest", totalInterest);

        intent.putExtra("total_payment", totalPayment);

        intent.putExtra("start_date", startDate);

        intent.putExtra("payoff_date", payoffDate);

        intent.putExtra("currency_code", selectedCurrencyCode);

        intent.putExtra("currency_name", selectedCurrencyName);

        intent.putExtra("currency_flag", selectedCurrencyFlag);

        intent.putExtra("loan_type", "Auto Loan");


        startActivity(intent);
    }

    private String formatDate(Calendar calendar) {

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


        return sdf.format(calendar.getTime());
    }

    private String getCurrencySymbol(String currencyCode) {

        if (currencyCode == null) {
            return "$";
        }

        switch (currencyCode.toUpperCase(Locale.US)) {

            case "USD": return "$";

            case "EUR": return "€";

            case "GBP": return "£";

            case "INR": return "₹";

            case "JPY": return "¥";

            case "CNY": return "¥";

            case "AUD": return "A$";

            case "CAD": return "C$";

            case "SGD": return "S$";

            case "HKD": return "HK$";

            case "NZD": return "NZ$";

            case "CHF": return "CHF ";

            case "AED": return "د.إ ";

            case "SAR": return "﷼";

            case "THB": return "฿";

            case "VND": return "₫";

            case "IDR": return "Rp ";

            default: return "$";
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

        tvLoanUnit.setText("Month");

        selectedCurrencyCode = "USD";

        selectedCurrencyName = "US Dollar";

        selectedCurrencyFlag = R.drawable.icn_flagus;

        tvCurrencyCode.setText("USD");

        ivFromFlag.setImageResource(selectedCurrencyFlag);

        selectedDate = Calendar.getInstance();

        updateStartDateText();

        etLoanAmount.requestFocus();
    }
}