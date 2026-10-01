package com.example.loanemi.Activities.Activities.Tools;

import static com.example.loanemi.Activities.utils.Util.setupEdgeToEdge;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loanemi.Activities.Ads.AdsHelper;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class TemperatureConverterActivity extends AppCompatActivity {

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
    private String fromUnit = "Celsius";
    private String toUnit = "Fahrenheit";
    private final Map<String, String> temperatureSymbols = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature_converter);
        setupEdgeToEdge(this, R.id.main);

        initTemperatureUnits();
        initViews();
        setupListeners();
        updateUnitUI();
        setUpAd();
    }

    private void initTemperatureUnits() {

        temperatureSymbols.clear();

        temperatureSymbols.put("Celsius", "°C");
        temperatureSymbols.put("Fahrenheit", "°F");
        temperatureSymbols.put("Kelvin", "K");
        temperatureSymbols.put("Rankine", "°R");
        temperatureSymbols.put("Réaumur", "°Ré");
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
            btnCalculate.setOnClickListener(v -> calculateTemperature());
        }
    }

    private void showUnitDialog(boolean isFromUnit) {

        final String[] units = temperatureSymbols.keySet().toArray(new String[0]);

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

    private void updateInfoTexts() {

        if (tvFromInfo != null) {

            double result = convertTemperature(1.0, fromUnit, toUnit);

            tvFromInfo.setText("1 " + getUnitSymbol(fromUnit) + " = " + formatNumber(result) + " " + getUnitSymbol(toUnit));
        }

        if (tvToInfo != null) {

            double result = convertTemperature(1.0, toUnit, fromUnit);

            tvToInfo.setText("1 " + getUnitSymbol(toUnit) + " = " + formatNumber(result) + " " + getUnitSymbol(fromUnit));
        }
    }

    private void calculateTemperature() {

        if (etFrom == null || etTo == null) {
            return;
        }

        String input = etFrom.getText().toString().trim();

        if (input.isEmpty()) {

            etTo.setText("0");

            Toast.makeText(this, "Please enter a temperature", Toast.LENGTH_SHORT).show();

            return;
        }

        try {

            double value = Double.parseDouble(input);

            if (Double.isNaN(value) || Double.isInfinite(value)) {

                etTo.setText("0");

                Toast.makeText(this, "Please enter a valid temperature", Toast.LENGTH_SHORT).show();

                return;
            }

            double result = convertTemperature(value, fromUnit, toUnit);

            etTo.setText(formatNumber(result));

        } catch (NumberFormatException e) {

            etTo.setText("0");

            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertTemperature(double value, String from, String to) {

        double celsius = toCelsius(value, from);

        return fromCelsius(celsius, to);
    }

    private double toCelsius(double value, String unit) {

        switch (unit) {

            case "Celsius": return value;

            case "Fahrenheit": return (value - 32.0) * 5.0 / 9.0;

            case "Kelvin": return value - 273.15;

            case "Rankine": return (value - 491.67) * 5.0 / 9.0;

            case "Réaumur": return value * 5.0 / 4.0;

            default: return value;
        }
    }

    private double fromCelsius(double celsius, String unit) {

        switch (unit) {

            case "Celsius": return celsius;

            case "Fahrenheit": return (celsius * 9.0 / 5.0) + 32.0;

            case "Kelvin": return celsius + 273.15;

            case "Rankine": return (celsius + 273.15) * 9.0 / 5.0;

            case "Réaumur": return celsius * 4.0 / 5.0;

            default: return celsius;
        }
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

        fromUnit = "Celsius";
        toUnit = "Fahrenheit";

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

        String symbol = temperatureSymbols.get(unit);

        if (symbol != null) {
            return symbol;
        }

        return unit;
    }

    private String formatNumber(double value) {

        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "0";
        }

        if (Math.abs(value) < 0.0000000001) {
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

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.temperature_convert_banner));
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