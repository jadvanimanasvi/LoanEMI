package com.loanemi.calculator.emi.Activities.Tools;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.loanemi.calculator.emi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.emi.Ads.AdsHelper;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.Util;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class WeightConverterActivity extends AppCompatActivity {

    private AdView bannerAdView;
    private FrameLayout adContainer;
    private ImageView backButton;
    private LinearLayout fromUnitSelector;
    private TextView tvFromUnit;
    private EditText etFrom;
    private TextView tvFromSymbol;
    private TextView tvFromInfo;
    private LinearLayout toUnitSelector;
    private TextView tvToUnit;
    private TextView etTo;
    private TextView tvToSymbol;
    private TextView tvToInfo;
    private FrameLayout btnSwap;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;
    private String fromUnit = "Kilogram";
    private String toUnit = "Gram";
    private final Map<String, Double> conversionFactors = new LinkedHashMap<>();
    private final Map<String, String> unitSymbols = new LinkedHashMap<>();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weight_converter);

        setupEdgeToEdge(this, R.id.main);

        initConversionUnits();
        initViews();
        setupListeners();
        updateUnitUI();
        setUpAd();
    }

    private void initConversionUnits() {

        conversionFactors.clear();
        unitSymbols.clear();

        // Metric units
        conversionFactors.put("Milligram", 0.001);
        conversionFactors.put("Gram", 1.0);
        conversionFactors.put("Kilogram", 1000.0);
        conversionFactors.put("Metric Ton", 1_000_000.0);

        // Imperial / US units
        conversionFactors.put("Ounce", 28.349523125);
        conversionFactors.put("Pound", 453.59237);
        conversionFactors.put("Stone", 6350.29318);

        // Microgram
        conversionFactors.put("Microgram", 0.000001);

        // Symbols
        unitSymbols.put("Microgram", "µg");
        unitSymbols.put("Milligram", "mg");
        unitSymbols.put("Gram", "g");
        unitSymbols.put("Kilogram", "kg");
        unitSymbols.put("Metric Ton", "t");
        unitSymbols.put("Ounce", "oz");
        unitSymbols.put("Pound", "lb");
        unitSymbols.put("Stone", "st");
    }

    private void initViews() {

        backButton = findViewById(R.id.backButton);

        fromUnitSelector = findViewById(R.id.fromUnitSelector);

        tvFromUnit = findViewById(R.id.tvFromUnit);

        etFrom = findViewById(R.id.etFrom);

        tvFromSymbol = findViewById(R.id.tvFromSymbol);

        tvFromInfo = findViewById(R.id.tvFromInfo);

        toUnitSelector = findViewById(R.id.toUnitSelector);

        tvToUnit = findViewById(R.id.tvToUnit);

        etTo = findViewById(R.id.etTo);

        tvToSymbol = findViewById(R.id.tvToSymbol);

        tvToInfo = findViewById(R.id.tvToInfo);

        btnSwap = findViewById(R.id.btnSwap);

        btnReset = findViewById(R.id.btnReset);

        btnCalculate = findViewById(R.id.btnCalculate);

        adContainer = findViewById(R.id.bannerContainer);
    }

    private void setupListeners() {

        if (backButton != null) {
            backButton.setOnClickListener(v -> finish());
        }

        if (fromUnitSelector != null) {
            fromUnitSelector.setOnClickListener(v -> showUnitDialog(true));
        }

        if (toUnitSelector != null) {
            toUnitSelector.setOnClickListener(v -> showUnitDialog(false));
        }

        if (btnSwap != null) {
            btnSwap.setOnClickListener(v -> swapUnits());
        }

        if (btnReset != null) {
            btnReset.setOnClickListener(v -> resetFields());
        }

        if (btnCalculate != null) {
            btnCalculate.setOnClickListener(v -> calculateWeight());
        }
    }

    private void showUnitDialog(boolean isFromUnit) {

        final String[] units = conversionFactors.keySet().toArray(new String[0]);

        String selectedUnit = isFromUnit ? fromUnit : toUnit;

        int selectedPosition = 0;

        for (int i = 0; i < units.length; i++) {

            if (units[i].equals(selectedUnit)) {
                selectedPosition = i;
                break;
            }
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setTitle(isFromUnit ? "Select From Unit" : "Select To Unit").setSingleChoiceItems(units, selectedPosition, (dialogInterface, which) -> {

            if (isFromUnit) {
                fromUnit = units[which];
            } else {
                toUnit = units[which];
            }

            updateUnitUI();

            dialogInterface.dismiss();
        }).setNegativeButton("Cancel", null).create();

        dialog.show();
    }

    private void updateUnitUI() {

        if (tvFromUnit != null) {
            tvFromUnit.setText(fromUnit);
        }

        if (tvToUnit != null) {
            tvToUnit.setText(toUnit);
        }

        if (tvFromSymbol != null) {
            tvFromSymbol.setText(getUnitSymbol(fromUnit));
        }

        if (tvToSymbol != null) {
            tvToSymbol.setText(getUnitSymbol(toUnit));
        }

        updateInfoTexts();
    }

    @SuppressLint("SetTextI18n")
    private void updateInfoTexts() {

        if (tvFromInfo != null) {

            double result = convertWeight(1.0, fromUnit, toUnit);

            tvFromInfo.setText("1 " + getUnitSymbol(fromUnit) + " = " + formatNumber(result) + " " + getUnitSymbol(toUnit));
        }

        if (tvToInfo != null) {

            double result = convertWeight(1.0, toUnit, fromUnit);

            tvToInfo.setText("1 " + getUnitSymbol(toUnit) + " = " + formatNumber(result) + " " + getUnitSymbol(fromUnit));
        }
    }

    private void calculateWeight() {

        if (etFrom == null || etTo == null) {
            return;
        }

        String input = etFrom.getText().toString().trim();

        if (input.isEmpty()) {

            etTo.setText("0");

            Toast.makeText(this, "Please enter a weight", Toast.LENGTH_SHORT).show();

            return;
        }

        try {

            double value = Double.parseDouble(input);

            if (Double.isNaN(value) || Double.isInfinite(value) || value < 0) {

                etTo.setText("0");

                Toast.makeText(this, "Please enter a valid weight", Toast.LENGTH_SHORT).show();

                return;
            }

            double result = convertWeight(value, fromUnit, toUnit);

            etTo.setText(formatNumber(result));

        } catch (NumberFormatException e) {

            etTo.setText("0");

            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertWeight(double value, String from, String to) {

        Double fromFactor = conversionFactors.get(from);

        Double toFactor = conversionFactors.get(to);

        if (fromFactor == null || toFactor == null || toFactor == 0) {

            return 0;
        }

        double grams = value * fromFactor;

        return grams / toFactor;
    }

    private void swapUnits() {

        if (etFrom == null || etTo == null) {
            return;
        }

        String tempUnit = fromUnit;

        fromUnit = toUnit;
        toUnit = tempUnit;

        String fromValue = etFrom.getText().toString();

        String toValue = etTo.getText().toString();

        etFrom.setText(toValue);
        etTo.setText(fromValue);

        updateUnitUI();
    }

    private void resetFields() {

        fromUnit = "Kilogram";
        toUnit = "Gram";

        if (etFrom != null) {
            etFrom.setText("0");
        }

        if (etTo != null) {
            etTo.setText("0");
        }

        updateUnitUI();

        if (etFrom != null) {

            etFrom.requestFocus();

            etFrom.setSelection(etFrom.getText().length());
        }
    }

    private String getUnitSymbol(String unit) {

        String symbol = unitSymbols.get(unit);

        if (symbol != null) {
            return symbol;
        }

        return unit;
    }

    private String formatNumber(double value) {

        if (Double.isNaN(value) || Double.isInfinite(value)) {

            return "0";
        }

        if (Math.abs(value) < 0.000000000001) {
            return "0";
        }

        return String.format(Locale.US, "%.10f", value).replaceAll("0+$", "").replaceAll("\\.$", "");
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

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.weight_convert_banner));
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

        super.onDestroy();
    }
}