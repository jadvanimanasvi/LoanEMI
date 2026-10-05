package com.loanemi.calculator.Activities;

import static com.loanemi.calculator.utils.Util.setupEdgeToEdge;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loanemi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.Ads.AdsHelper;
import com.loanemi.calculator.utils.Util;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CashCalculatorActivity extends AppCompatActivity {

    private AdView bannerAdView;
    private FrameLayout adContainer;
    private String selectedCurrencyCode = "INR";
    private String selectedCurrencySymbol = "₹";
    private TextView tvCurrencyCode;
    private TextView tvCurrencyFlag,titleCurrency;
    private LinearLayout currencySelector;
    private EditText etNotes10;
    private EditText etNotes20;
    private EditText etNotes50;
    private EditText etNotes100;
    private EditText etNotes200;
    private EditText etNotes500;
    private TextView tvSymbol10;
    private TextView tvSymbol20;
    private TextView tvSymbol50;
    private TextView tvSymbol100;
    private TextView tvSymbol200;
    private TextView tvSymbol500;
    private TextView tvTotalAmount;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;
    private ImageView backButton;
    private static final long DENOMINATION_10 = 10L;
    private static final long DENOMINATION_20 = 20L;
    private static final long DENOMINATION_50 = 50L;
    private static final long DENOMINATION_100 = 100L;
    private static final long DENOMINATION_200 = 200L;
    private static final long DENOMINATION_500 = 500L;

    private final ActivityResultLauncher<Intent> currencyLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() != RESULT_OK) {
            return;
        }

        Intent data = result.getData();

        if (data == null) {
            return;
        }

        String code = data.getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE);

        if (code == null || code.trim().isEmpty()) {
            return;
        }

        selectedCurrencyCode = code.trim().toUpperCase(Locale.US);

        selectedCurrencySymbol = getCurrencySymbol(selectedCurrencyCode);

        updateCurrencyUI();

        if (hasEnteredNotes()) {
            calculateCash(false);
        } else {
            tvTotalAmount.setText(formatAmount(0));
        }
    });

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cash_calculator);

        setupEdgeToEdge(this, R.id.main);

        initViews();

        setupInputs();

        setupCurrency();

        setupListeners();

        resetCalculator();

        setUpAd();
    }

    private void initViews() {

        backButton = findViewById(R.id.backButton);

        etNotes10 = findViewById(R.id.etNotes10);
        etNotes20 = findViewById(R.id.etNotes20);
        etNotes50 = findViewById(R.id.etNotes50);
        etNotes100 = findViewById(R.id.etNotes100);
        etNotes200 = findViewById(R.id.etNotes200);
        etNotes500 = findViewById(R.id.etNotes500);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);

        btnReset = findViewById(R.id.btnReset);
        btnCalculate = findViewById(R.id.btnCalculate);

        currencySelector = findViewById(R.id.currencySelector);

        tvCurrencyCode = findViewById(R.id.tvCurrencyCode);
        tvCurrencyFlag = findViewById(R.id.tvCurrencyFlag);

        adContainer = findViewById(R.id.bannerContainer);
        titleCurrency = findViewById(R.id.titleCurrency);

        tvSymbol10 = findViewById(R.id.tvSymbol10);
        tvSymbol20 = findViewById(R.id.tvSymbol20);
        tvSymbol50 = findViewById(R.id.tvSymbol50);
        tvSymbol100 = findViewById(R.id.tvSymbol100);
        tvSymbol200 = findViewById(R.id.tvSymbol200);
        tvSymbol500 = findViewById(R.id.tvSymbol500);
    }

    private void setupInputs() {

        setupNumberField(etNotes10);
        setupNumberField(etNotes20);
        setupNumberField(etNotes50);
        setupNumberField(etNotes100);
        setupNumberField(etNotes200);
        setupNumberField(etNotes500);
    }

    private void setupNumberField(EditText editText) {

        if (editText == null) {
            return;
        }

        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
    }

    private void setupCurrency() {

        String intentCurrency = getIntent().getStringExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE);

        if (intentCurrency == null || intentCurrency.trim().isEmpty()) {

            intentCurrency = getIntent().getStringExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE);
        }

        if (intentCurrency == null || intentCurrency.trim().isEmpty()) {

            intentCurrency = "INR";
        }

        selectedCurrencyCode = intentCurrency.trim().toUpperCase(Locale.US);

        if (!isSupportedCurrency(selectedCurrencyCode)) {
            selectedCurrencyCode = "INR";
        }

        selectedCurrencySymbol = getCurrencySymbol(selectedCurrencyCode);

        updateCurrencyUI();
    }

    private boolean isSupportedCurrency(String code) {

        if (code == null) {
            return false;
        }

        switch (code.toUpperCase(Locale.US)) {

            case "GBP":
            case "USD":
            case "CNY":
            case "INR":
            case "VND":
            case "THB":
            case "IDR":
                return true;

            default:
                return false;
        }
    }

    private String getCurrencySymbol(String code) {

        if (code == null) {
            return "₹";
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

            default:
                return code;
        }
    }

    private int getCurrencyFlag(String code) {

        if (code == null) {
            return R.drawable.icn_flaghindi;
        }

        switch (code.toUpperCase(Locale.US)) {

            case "GBP":
                return R.drawable.icn_flagengland;

            case "USD":
                return R.drawable.icn_flagus;

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

            default:
                return R.drawable.icn_flaghindi;
        }
    }

    private void updateCurrencyUI() {

        if (tvCurrencyCode != null) {
            tvCurrencyCode.setText(selectedCurrencyCode);
        }

        if (tvCurrencyFlag != null) {
            tvCurrencyFlag.setText(getCurrencyEmoji(selectedCurrencyCode));
        }

        if (tvSymbol10 != null) {
            tvSymbol10.setText(selectedCurrencySymbol);
        }

        if (tvSymbol20 != null) {
            tvSymbol20.setText(selectedCurrencySymbol);
        }

        if (tvSymbol50 != null) {
            tvSymbol50.setText(selectedCurrencySymbol);
        }

        if (tvSymbol100 != null) {
            tvSymbol100.setText(selectedCurrencySymbol);
        }

        if (tvSymbol200 != null) {
            tvSymbol200.setText(selectedCurrencySymbol);
        }

        if (tvSymbol500 != null) {
            tvSymbol500.setText(selectedCurrencySymbol);
        }

        if (titleCurrency != null) {
            titleCurrency.setText(selectedCurrencySymbol);
        }
    }

    private String getCurrencyEmoji(String code) {

        if (code == null) {
            return "🇮🇳";
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

            default:
                return "🌐";
        }
    }


    private void setupListeners() {

        // Back
        if (backButton != null) {

            backButton.setOnClickListener(v -> finish());
        }

        // Currency
        if (currencySelector != null) {

            currencySelector.setOnClickListener(v -> openCurrencySelector());
        }

        // Reset
        if (btnReset != null) {

            btnReset.setOnClickListener(v -> resetCalculator());
        }

        // Calculate
        if (btnCalculate != null) {

            btnCalculate.setOnClickListener(v -> calculateCash(true));
        }
    }


    private void openCurrencySelector() {

        Intent intent = new Intent(CashCalculatorActivity.this, CurrencyUnitActivity.class);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENT_CODE, selectedCurrencyCode);

        intent.putExtra(CurrencyUnitActivity.EXTRA_SELECTED_CODE, selectedCurrencyCode);

        intent.putExtra(CurrencyUnitActivity.EXTRA_CURRENCY_SELECT_TYPE, "cash_calculator");

        currencyLauncher.launch(intent);
    }


    private void calculateCash(boolean showToast) {

        hideKeyboard();

        final long notes10 = getNoteCount(etNotes10);

        final long notes20 = getNoteCount(etNotes20);

        final long notes50 = getNoteCount(etNotes50);

        final long notes100 = getNoteCount(etNotes100);

        final long notes200 = getNoteCount(etNotes200);

        final long notes500 = getNoteCount(etNotes500);

        final long totalNotes = notes10 + notes20 + notes50 + notes100 + notes200 + notes500;

        final double totalInr = (notes10 * DENOMINATION_10) + (notes20 * DENOMINATION_20) + (notes50 * DENOMINATION_50) + (notes100 * DENOMINATION_100) + (notes200 * DENOMINATION_200) + (notes500 * DENOMINATION_500);

        if (totalNotes == 0) {

            tvTotalAmount.setText(formatAmount(0));

            if (showToast) {

                Toast.makeText(this, "Please enter at least one note.", Toast.LENGTH_SHORT).show();
            }

            return;
        }

        if ("INR".equals(selectedCurrencyCode)) {

            tvTotalAmount.setText(formatAmount(totalInr));

            if (showToast) {

                Toast.makeText(this, "Total Notes: " + formatNumber(totalNotes) + "\nTotal Amount: " + formatAmount(totalInr), Toast.LENGTH_SHORT).show();
            }

            return;
        }

        convertFromInr(totalInr, totalNotes, showToast);
    }


    private void convertFromInr(double totalInr, long totalNotes, boolean showToast) {

        if (!Util.isInternetAvailable(this)) {

            Toast.makeText(this, "Internet connection is required to convert currency.", Toast.LENGTH_SHORT).show();

            tvTotalAmount.setText(formatAmount(totalInr));

            return;
        }

        final String targetCurrency = selectedCurrencyCode;

        executor.execute(() -> {

            double exchangeRate = getExchangeRate("INR", targetCurrency);

            runOnUiThread(() -> {

                if (isFinishing() || isDestroyed()) {
                    return;
                }

                if (exchangeRate <= 0) {

                    Toast.makeText(CashCalculatorActivity.this, "Unable to get exchange rate.", Toast.LENGTH_SHORT).show();

                    tvTotalAmount.setText(formatAmount(totalInr));

                    return;
                }

                double convertedAmount = totalInr * exchangeRate;

                tvTotalAmount.setText(formatAmount(convertedAmount));

                if (showToast) {

                    Toast.makeText(CashCalculatorActivity.this, "Total Notes: " + formatNumber(totalNotes) + "\n" + formatAmount(convertedAmount), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private double getExchangeRate(String fromCurrency, String toCurrency) {

        HttpURLConnection connection = null;

        try {

            String urlString = "https://open.er-api.com/v6/latest/" + fromCurrency;

            URL url = new URL(urlString);

            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.setConnectTimeout(10000);

            connection.setReadTimeout(10000);

            connection.setRequestProperty("Accept", "application/json");

            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                return -1;
            }

            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            StringBuilder response = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null) {

                response.append(line);
            }

            reader.close();

            JSONObject json = new JSONObject(response.toString());

            String result = json.optString("result", "");

            if (!"success".equalsIgnoreCase(result)) {
                return -1;
            }

            JSONObject rates = json.optJSONObject("rates");

            if (rates == null) {
                return -1;
            }

            return rates.optDouble(toCurrency, -1);

        } catch (Exception e) {

            e.printStackTrace();

            return -1;

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    private long getNoteCount(EditText editText) {

        if (editText == null) {
            return 0;
        }

        String value = editText.getText().toString().trim();

        if (value.isEmpty()) {
            return 0;
        }

        try {

            long count = Long.parseLong(value);

            if (count < 0) {
                return 0;
            }

            return count;

        } catch (NumberFormatException e) {

            return 0;
        }
    }


    private boolean hasEnteredNotes() {

        return getNoteCount(etNotes10) > 0 || getNoteCount(etNotes20) > 0 || getNoteCount(etNotes50) > 0 || getNoteCount(etNotes100) > 0 || getNoteCount(etNotes200) > 0 || getNoteCount(etNotes500) > 0;
    }


    private String formatAmount(double amount) {

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);

        String formatted = numberFormat.format(Math.round(amount));

        if ("INR".equals(selectedCurrencyCode)) {

            return "₹ " + formatted;
        }


        return formatted + " " + selectedCurrencySymbol;
    }


    private String formatNumber(long value) {

        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(0);

        return numberFormat.format(value);
    }


    private void resetCalculator() {

        clearField(etNotes10);
        clearField(etNotes20);
        clearField(etNotes50);
        clearField(etNotes100);
        clearField(etNotes200);
        clearField(etNotes500);


        selectedCurrencyCode = "INR";

        selectedCurrencySymbol = "₹";

        updateCurrencyUI();

        if (tvTotalAmount != null) {

            tvTotalAmount.setText(formatAmount(0));
        }

        if (etNotes10 != null) {

            etNotes10.requestFocus();
        }
    }


    private void clearField(EditText editText) {

        if (editText != null) {

            editText.setText("");
        }
    }

    private void hideKeyboard() {

        View currentFocus = getCurrentFocus();

        if (currentFocus == null) {
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        if (imm != null) {

            imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
        }

        currentFocus.clearFocus();
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

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.cash_calc_banner));
    }


    @Override
    protected void onResume() {

        super.onResume();

        Util.hide(this);
    }

    @Override
    protected void onDestroy() {

        if (bannerAdView != null) {

            if (bannerAdView.getParent() != null) {

                ((ViewGroup) bannerAdView.getParent()).removeView(bannerAdView);
            }

            bannerAdView.destroy();

            bannerAdView = null;
        }

        if (executor != null) {

            executor.shutdownNow();
        }

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}
