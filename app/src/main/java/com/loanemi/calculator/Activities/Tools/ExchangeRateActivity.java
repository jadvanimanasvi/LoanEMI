package com.loanemi.calculator.Activities.Tools;

import static com.loanemi.calculator.utils.Util.setupEdgeToEdge;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExchangeRateActivity extends AppCompatActivity {

    private AdView bannerAdView;
    private FrameLayout adContainer;
    private ImageView backButton;
    private LinearLayout fromUnitSelector;
    private ImageView ivFromFlag;
    private TextView tvFromUnit;
    private EditText etFrom;
    private TextView tvFromSymbol;
    private TextView tvFromInfo;
    private LinearLayout toUnitSelector;
    private ImageView icFromFlag;
    private TextView tvToUnit;
    private TextView etTo;
    private TextView tvToSymbol;
    private TextView tvToInfo;
    private FrameLayout btnSwap;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;
    private String fromCurrency = "USD";
    private String toCurrency = "VND";
    private double exchangeRate = 0.0;
    private ExecutorService executorService;
    private AlertDialog internetDialog;
    private boolean isInternetDialogShowing = false;
    private final Map<String, String> currencyNames = new LinkedHashMap<>();
    private final Map<String, String> currencySymbols = new LinkedHashMap<>();
    private final Map<String, Integer> currencyFlags = new LinkedHashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_exchange_rate);

        setupEdgeToEdge(this, R.id.main);

        executorService = Executors.newSingleThreadExecutor();

        initCurrencyData();

        initViews();

        setupListeners();

        updateCurrencyUI();

        setUpAd();

        if (Util.isInternetAvailable(this)) {

            fetchExchangeRate();

        } else {

            showInternetOffDialog();
        }
    }

    private void initCurrencyData() {

        currencyNames.clear();
        currencySymbols.clear();
        currencyFlags.clear();

        // USD
        currencyNames.put("USD", "United States Dollar");

        currencySymbols.put("USD", "$");

        currencyFlags.put("USD", R.drawable.icn_flagus);

        // VND
        currencyNames.put("VND", "Vietnamese Dong");

        currencySymbols.put("VND", "₫");

        currencyFlags.put("VND", R.drawable.icn_flagvietnamese);

        // INR
        currencyNames.put("INR", "Indian Rupee");

        currencySymbols.put("INR", "₹");

        currencyFlags.put("INR", R.drawable.icn_flaghindi);

        // GBP
        currencyNames.put("GBP", "British Pound");

        currencySymbols.put("GBP", "£");

        currencyFlags.put("GBP", R.drawable.icn_flagengland);

        // CNY
        currencyNames.put("CNY", "Chinese Yuan");

        currencySymbols.put("CNY", "¥");

        currencyFlags.put("CNY", R.drawable.icn_flagchinese);

        // THB
        currencyNames.put("THB", "Thai Baht");

        currencySymbols.put("THB", "฿");

        currencyFlags.put("THB", R.drawable.icn_flagthailand);

        // IDR
        currencyNames.put("IDR", "Indonesian Rupiah");

        currencySymbols.put("IDR", "Rp");

        currencyFlags.put("IDR", R.drawable.icn_flagindonesia);

        // EUR
        currencyNames.put("EUR", "Euro");

        currencySymbols.put("EUR", "€");

        // AUD
        currencyNames.put("AUD", "Australian Dollar");

        currencySymbols.put("AUD", "A$");


        // CAD
        currencyNames.put("CAD", "Canadian Dollar");

        currencySymbols.put("CAD", "C$");


        // JPY
        currencyNames.put("JPY", "Japanese Yen");

        currencySymbols.put("JPY", "¥");


        // CHF
        currencyNames.put("CHF", "Swiss Franc");

        currencySymbols.put("CHF", "CHF");


        // SGD
        currencyNames.put("SGD", "Singapore Dollar");

        currencySymbols.put("SGD", "S$");


        // HKD
        currencyNames.put("HKD", "Hong Kong Dollar");

        currencySymbols.put("HKD", "HK$");


        // KRW
        currencyNames.put("KRW", "South Korean Won");

        currencySymbols.put("KRW", "₩");


        // NZD
        currencyNames.put("NZD", "New Zealand Dollar");

        currencySymbols.put("NZD", "NZ$");


        // ZAR
        currencyNames.put("ZAR", "South African Rand");

        currencySymbols.put("ZAR", "R");


        // BRL
        currencyNames.put("BRL", "Brazilian Real");

        currencySymbols.put("BRL", "R$");

        // MXN
        currencyNames.put("MXN", "Mexican Peso");

        currencySymbols.put("MXN", "$");

        // TRY
        currencyNames.put("TRY", "Turkish Lira");

        currencySymbols.put("TRY", "₺");
    }

    private void initViews() {

        backButton = findViewById(R.id.backButton);


        // FROM

        fromUnitSelector = findViewById(R.id.fromUnitSelector);

        ivFromFlag = findViewById(R.id.ivFromFlag);

        tvFromUnit = findViewById(R.id.tvFromUnit);

        etFrom = findViewById(R.id.etFrom);

        tvFromSymbol = findViewById(R.id.tvFromSymbol);

        tvFromInfo = findViewById(R.id.tvFromInfo);

        // TO
        toUnitSelector = findViewById(R.id.toUnitSelector);

        icFromFlag = findViewById(R.id.icFromFlag);

        tvToUnit = findViewById(R.id.tvToUnit);

        etTo = findViewById(R.id.etTo);

        tvToSymbol = findViewById(R.id.tvToSymbol);

        tvToInfo = findViewById(R.id.tvToInfo);

        // BUTTONS

        btnSwap = findViewById(R.id.btnSwap);

        btnReset = findViewById(R.id.btnReset);

        btnCalculate = findViewById(R.id.btnCalculate);
        // AD

        adContainer = findViewById(R.id.bannerContainer);
    }

    private void setupListeners() {

        // BACK

        if (backButton != null) {

            backButton.setOnClickListener(v -> finish());
        }

        // FROM CURRENCY

        if (fromUnitSelector != null) {

            fromUnitSelector.setOnClickListener(v -> showCurrencyDialog(true));
        }

        // TO CURRENCY

        if (toUnitSelector != null) {

            toUnitSelector.setOnClickListener(v -> showCurrencyDialog(false));
        }

        // SWAP

        if (btnSwap != null) {

            btnSwap.setOnClickListener(v -> swapCurrencies());
        }

        // RESET

        if (btnReset != null) {

            btnReset.setOnClickListener(v -> resetFields());
        }

        // CALCULATE

        if (btnCalculate != null) {

            btnCalculate.setOnClickListener(v -> calculateExchange());
        }
    }


    private void showInternetOffDialog() {

        if (isFinishing() || isDestroyed() || isInternetDialogShowing) {

            return;
        }

        isInternetDialogShowing = true;

        LinearLayout mainLayout = new LinearLayout(this);

        mainLayout.setOrientation(LinearLayout.VERTICAL);

        mainLayout.setGravity(android.view.Gravity.CENTER);

        mainLayout.setPadding(dpToPx(24), dpToPx(24), dpToPx(24), dpToPx(20));

        GradientDrawable background = new GradientDrawable();

        background.setColor(Color.WHITE);

        background.setCornerRadius(dpToPx(20));

        mainLayout.setBackground(background);

        ImageView icon = new ImageView(this);

        icon.setImageResource(android.R.drawable.stat_notify_error);

        icon.setColorFilter(Color.parseColor("#D32F2F"));

        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(52), dpToPx(52));

        iconParams.gravity = android.view.Gravity.CENTER_HORIZONTAL;

        mainLayout.addView(icon, iconParams);


        TextView title = new TextView(this);

        title.setText("No Internet Connection");

        title.setTextColor(Color.parseColor("#141A14"));

        title.setTextSize(20);

        title.setTypeface(null, android.graphics.Typeface.BOLD);

        title.setGravity(android.view.Gravity.CENTER);

        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        titleParams.topMargin = dpToPx(12);

        mainLayout.addView(title, titleParams);

        TextView message = new TextView(this);

        message.setText("Please check your internet connection and try again.");

        message.setTextColor(Color.parseColor("#6B7280"));

        message.setTextSize(15);

        message.setGravity(android.view.Gravity.CENTER);

        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        messageParams.topMargin = dpToPx(8);

        mainLayout.addView(message, messageParams);

        TextView tryAgain = new TextView(this);

        tryAgain.setText("Try Again");

        tryAgain.setTextColor(Color.WHITE);

        tryAgain.setTextSize(16);

        tryAgain.setTypeface(null, android.graphics.Typeface.BOLD);

        tryAgain.setGravity(android.view.Gravity.CENTER);

        tryAgain.setClickable(true);

        tryAgain.setFocusable(true);

        GradientDrawable buttonBackground = new GradientDrawable();

        buttonBackground.setColor(Color.parseColor("#7CB318"));

        buttonBackground.setCornerRadius(dpToPx(12));

        tryAgain.setBackground(buttonBackground);

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(50));

        buttonParams.topMargin = dpToPx(20);

        mainLayout.addView(tryAgain, buttonParams);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(mainLayout);

        builder.setCancelable(false);

        internetDialog = builder.create();

        if (internetDialog.getWindow() != null) {

            internetDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        tryAgain.setOnClickListener(v -> {

            if (Util.isInternetAvailable(ExchangeRateActivity.this)) {

                isInternetDialogShowing = false;

                if (internetDialog != null && internetDialog.isShowing()) {

                    internetDialog.dismiss();
                }

                internetDialog = null;

                exchangeRate = 0.0;

                fetchExchangeRate();

            } else {

                Toast.makeText(ExchangeRateActivity.this, "Internet is still unavailable", Toast.LENGTH_SHORT).show();
            }
        });

        internetDialog.setOnDismissListener(dialog -> {

            isInternetDialogShowing = false;

            internetDialog = null;
        });

        internetDialog.show();

        if (internetDialog.getWindow() != null) {

            internetDialog.getWindow().setLayout((int) (getResources().getDisplayMetrics().widthPixels * 0.88), ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private int dpToPx(int dp) {

        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void showCurrencyDialog(boolean isFromCurrency) {

        final String[] currencies = currencyNames.keySet().toArray(new String[0]);


        String selectedCurrency = isFromCurrency ? fromCurrency : toCurrency;


        int selectedPosition = 0;


        for (int i = 0; i < currencies.length; i++) {

            if (currencies[i].equals(selectedCurrency)) {

                selectedPosition = i;

                break;
            }
        }


        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(isFromCurrency ? "Select From Currency" : "Select To Currency").setSingleChoiceItems(currencies, selectedPosition, (dialogInterface, which) -> {

            if (isFromCurrency) {

                fromCurrency = currencies[which];

            } else {

                toCurrency = currencies[which];
            }


            exchangeRate = 0.0;


            updateCurrencyUI();


            dialogInterface.dismiss();


            if (Util.isInternetAvailable(ExchangeRateActivity.this)) {

                fetchExchangeRate();

            } else {

                showInternetOffDialog();
            }
        }).setNegativeButton("Cancel", null).create();


        dialog.show();
    }


    private void updateCurrencyUI() {

        // FROM

        if (tvFromUnit != null) {

            tvFromUnit.setText(fromCurrency);
        }

        if (tvFromSymbol != null) {

            tvFromSymbol.setText(getCurrencySymbol(fromCurrency));
        }

        if (ivFromFlag != null) {

            Integer flag = currencyFlags.get(fromCurrency);


            if (flag != null) {

                ivFromFlag.setImageResource(flag);
            }
        }

        if (tvToUnit != null) {

            tvToUnit.setText(toCurrency);
        }


        if (tvToSymbol != null) {

            tvToSymbol.setText(getCurrencySymbol(toCurrency));
        }

        if (icFromFlag != null) {

            Integer flag = currencyFlags.get(toCurrency);


            if (flag != null) {

                icFromFlag.setImageResource(flag);
            }
        }


        updateInfoTexts();
    }


    private void updateInfoTexts() {

        if (exchangeRate <= 0) {

            if (tvFromInfo != null) {

                tvFromInfo.setText("1 " + fromCurrency + " = Loading...");
            }

            if (tvToInfo != null) {

                tvToInfo.setText("1 " + toCurrency + " = Loading...");
            }

            return;
        }

        if (tvFromInfo != null) {

            tvFromInfo.setText("1 " + fromCurrency + " = " + formatRate(exchangeRate) + " " + toCurrency);
        }

        if (tvToInfo != null) {

            double reverseRate;


            if (exchangeRate != 0) {

                reverseRate = 1.0 / exchangeRate;

            } else {

                reverseRate = 0;
            }

            tvToInfo.setText("1 " + toCurrency + " = " + formatRate(reverseRate) + " " + fromCurrency);
        }
    }


    private void fetchExchangeRate() {

        if (!Util.isInternetAvailable(this)) {

            exchangeRate = 0.0;

            showInternetOffDialog();

            return;
        }

        if (fromCurrency.equals(toCurrency)) {

            exchangeRate = 1.0;

            updateInfoTexts();

            return;
        }

        final String from = fromCurrency;

        final String to = toCurrency;


        if (tvFromInfo != null) {

            tvFromInfo.setText("1 " + from + " = Loading...");
        }

        if (tvToInfo != null) {

            tvToInfo.setText("1 " + to + " = Loading...");
        }

        if (executorService == null || executorService.isShutdown()) {

            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.execute(() -> {

            HttpURLConnection connection = null;


            try {

                String apiUrl = "https://api.frankfurter.dev/v2/rate/" + from.toLowerCase(Locale.US) + "/" + to.toLowerCase(Locale.US);


                URL url = new URL(apiUrl);

                connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("GET");

                connection.setConnectTimeout(10000);

                connection.setReadTimeout(10000);

                connection.setRequestProperty("Accept", "application/json");

                int responseCode = connection.getResponseCode();

                InputStream inputStream;

                if (responseCode >= 200 && responseCode < 300) {

                    inputStream = connection.getInputStream();

                } else {

                    inputStream = connection.getErrorStream();
                }


                if (inputStream == null) {

                    throw new Exception("No response from server");
                }


                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));


                StringBuilder response = new StringBuilder();


                String line;


                while ((line = reader.readLine()) != null) {

                    response.append(line);
                }


                reader.close();


                if (responseCode < 200 || responseCode >= 300) {

                    throw new Exception("API error: " + responseCode);
                }

                JSONObject json = new JSONObject(response.toString());

                double rate = json.getDouble("rate");

                runOnUiThread(() -> {

                    if (!fromCurrency.equals(from) || !toCurrency.equals(to)) {

                        return;
                    }

                    exchangeRate = rate;

                    updateInfoTexts();
                });


            } catch (Exception e) {

                runOnUiThread(() -> {

                    if (!fromCurrency.equals(from) || !toCurrency.equals(to)) {

                        return;
                    }


                    exchangeRate = 0.0;


                    if (tvFromInfo != null) {

                        tvFromInfo.setText("Unable to load rate");
                    }


                    if (tvToInfo != null) {

                        tvToInfo.setText("Please check your internet connection");
                    }


                    if (!Util.isInternetAvailable(ExchangeRateActivity.this)) {

                        showInternetOffDialog();

                    } else {

                        Toast.makeText(ExchangeRateActivity.this, "Unable to fetch exchange rate", Toast.LENGTH_SHORT).show();
                    }
                });


            } finally {

                if (connection != null) {

                    connection.disconnect();
                }
            }
        });
    }

    private void calculateExchange() {

        if (!Util.isInternetAvailable(this)) {

            showInternetOffDialog();

            return;
        }


        if (etFrom == null || etTo == null) {

            return;
        }

        String input = etFrom.getText().toString().trim();


        if (input.isEmpty()) {

            etTo.setText("0");

            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();

            return;
        }

        double amount;

        try {

            amount = Double.parseDouble(input);

        } catch (NumberFormatException e) {

            etTo.setText("0");


            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();


            return;
        }

        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount < 0) {

            etTo.setText("0");

            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();

            return;
        }


        if (fromCurrency.equals(toCurrency)) {

            exchangeRate = 1.0;

            etTo.setText(formatAmount(amount));

            updateInfoTexts();

            return;
        }


        if (exchangeRate <= 0) {

            Toast.makeText(this, "Exchange rate is not available", Toast.LENGTH_SHORT).show();

            fetchExchangeRate();

            return;
        }

        double result = amount * exchangeRate;


        if (Double.isNaN(result) || Double.isInfinite(result)) {

            etTo.setText("0");

            Toast.makeText(this, "Unable to calculate result", Toast.LENGTH_SHORT).show();

            return;
        }

        etTo.setText(formatAmount(result));
    }

    private void swapCurrencies() {

        if (etFrom == null || etTo == null) {

            return;
        }

        String tempCurrency = fromCurrency;

        fromCurrency = toCurrency;

        toCurrency = tempCurrency;

        String fromValue = etFrom.getText().toString();

        String toValue = etTo.getText().toString();

        etFrom.setText(toValue);

        etTo.setText(fromValue);

        exchangeRate = 0.0;

        updateCurrencyUI();


        if (Util.isInternetAvailable(this)) {

            fetchExchangeRate();

        } else {

            showInternetOffDialog();
        }
    }

    private void resetFields() {

        fromCurrency = "USD";

        toCurrency = "VND";

        exchangeRate = 0.0;

        if (etFrom != null) {

            etFrom.setText("0");
        }

        if (etTo != null) {

            etTo.setText("0");
        }

        updateCurrencyUI();

        if (Util.isInternetAvailable(this)) {

            fetchExchangeRate();

        } else {

            showInternetOffDialog();
        }

        if (etFrom != null) {

            etFrom.requestFocus();


            etFrom.setSelection(etFrom.getText().length());
        }
    }


    private String getCurrencySymbol(String currency) {

        String symbol = currencySymbols.get(currency);


        if (symbol != null) {

            return symbol;
        }

        return currency;
    }


    private String formatRate(double value) {

        if (Double.isNaN(value) || Double.isInfinite(value)) {

            return "0";
        }


        if (value == 0) {

            return "0";
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.##########");

        return decimalFormat.format(value);
    }

    private String formatAmount(double value) {

        if (Double.isNaN(value) || Double.isInfinite(value)) {

            return "0";
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.######");

        return decimalFormat.format(value);
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

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.exchange_rate_banner));
    }


    @Override
    protected void onResume() {

        super.onResume();

        Util.hide(this);

        if (!Util.isInternetAvailable(this)) {

            showInternetOffDialog();
        }
    }


    @Override
    protected void onDestroy() {

        if (internetDialog != null && internetDialog.isShowing()) {

            internetDialog.dismiss();
        }

        internetDialog = null;

        isInternetDialogShowing = false;


        if (executorService != null) {

            executorService.shutdownNow();

            executorService = null;
        }

        if (bannerAdView != null) {

            if (bannerAdView.getParent() != null) {

                ((ViewGroup) bannerAdView.getParent()).removeView(bannerAdView);
            }


            bannerAdView.destroy();

            bannerAdView = null;
        }

        super.onDestroy();
    }
}