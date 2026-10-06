package com.loanemi.calculator.emi.Activities.Tools;

import static com.loanemi.calculator.emi.utils.Util.setupEdgeToEdge;
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
import com.loanemi.calculator.emi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.emi.Ads.AdsHelper;
import com.loanemi.calculator.emi.utils.Util;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class SpeedConverterActivity extends AppCompatActivity {

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
    private String fromUnit = "Km/h";
    private String toUnit = "Km/s";
    private final Map<String, Double> conversionFactors = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_speed_converter);

        setupEdgeToEdge(this, R.id.main);

        initConversionUnits();
        initViews();
        setupListeners();
        updateUnitUI();
        setUpAd();
    }

    private void initConversionUnits() {

        conversionFactors.clear();

        // Kilometer per hour
        conversionFactors.put("Km/h", 1000.0 / 3600.0);

        // Kilometer per second
        conversionFactors.put("Km/s", 1000.0);

        // Meter per second
        conversionFactors.put("m/s", 1.0);

        // Meter per minute
        conversionFactors.put("m/min", 1.0 / 60.0);
        conversionFactors.put("mph", 1609.344 / 3600.0);
        conversionFactors.put("ft/s", 0.3048);
        conversionFactors.put("ft/min", 0.3048 / 60.0);
        conversionFactors.put("knots", 1852.0 / 3600.0);
    }

    private void initViews() {

        backButton = findViewById(R.id.backButton);

        fromUnitSelector = findViewById(R.id.fromUnitSelector);

        tvFromUnit = findViewById(R.id.tvFromUnit);

        etFrom = findViewById(R.id.etSpeedFrom);

        tvFromSymbol = findViewById(R.id.tvFromSymbol);

        tvFromInfo = findViewById(R.id.tvFromInfo);

        toUnitSelector = findViewById(R.id.toUnitSelector);

        tvToUnit = findViewById(R.id.tvToUnit);

        etTo = findViewById(R.id.etSpeedTo);

        tvToSymbol = findViewById(R.id.tvToSymbol);

        tvToInfo = findViewById(R.id.tvToInfo);

        btnSwap = findViewById(R.id.btnSwap);

        btnReset = findViewById(R.id.btnReset);

        btnCalculate = findViewById(R.id.btnCalculate);

        // Banner
        adContainer = findViewById(R.id.bannerContainer);
    }

    private void setupListeners() {

        // Back button
        if (backButton != null) {

            backButton.setOnClickListener(v -> finish());
        }

        // From unit selector
        if (fromUnitSelector != null) {

            fromUnitSelector.setOnClickListener(v -> showUnitDialog(true));
        }

        // To unit selector
        if (toUnitSelector != null) {

            toUnitSelector.setOnClickListener(v -> showUnitDialog(false));
        }

        // Swap button
        if (btnSwap != null) {

            btnSwap.setOnClickListener(v -> swapUnits());
        }

        if (btnReset != null) {

            btnReset.setOnClickListener(v -> resetFields());
        }

        if (btnCalculate != null) {

            btnCalculate.setOnClickListener(v -> calculateConversion());
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

    private void updateInfoTexts() {

        if (tvFromInfo != null) {

            double result = convertValue(1.0, fromUnit, toUnit);

            tvFromInfo.setText("1 " + getUnitSymbol(fromUnit) + " = " + formatNumber(result) + " " + getUnitSymbol(toUnit));
        }

        if (tvToInfo != null) {

            double result = convertValue(1.0, toUnit, fromUnit);

            tvToInfo.setText("1 " + getUnitSymbol(toUnit) + " = " + formatNumber(result) + " " + getUnitSymbol(fromUnit));
        }
    }

    private void calculateConversion() {

        if (etFrom == null || etTo == null) {
            return;
        }

        String input = etFrom.getText().toString().trim();

        // Empty input
        if (input.isEmpty()) {

            etTo.setText("0");

            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();

            return;
        }

        try {

            double value = Double.parseDouble(input);

            // Negative value validation
            if (value < 0) {

                etTo.setText("0");

                Toast.makeText(this, "Please enter a valid value", Toast.LENGTH_SHORT).show();

                return;
            }

            // Calculate result
            double result = convertValue(value, fromUnit, toUnit);

            // Show answer in To TextView
            etTo.setText(formatNumber(result));

        } catch (NumberFormatException e) {

            etTo.setText("0");

            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();
        }
    }

    private double convertValue(double value, String from, String to) {

        Double fromFactor = conversionFactors.get(from);

        Double toFactor = conversionFactors.get(to);

        if (fromFactor == null || toFactor == null) {

            return 0;
        }

        double metersPerSecond = value * fromFactor;

        return metersPerSecond / toFactor;
    }

    private void swapUnits() {

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

        fromUnit = "Km/h";
        toUnit = "Km/s";

        etFrom.setText("0");
        etTo.setText("0");

        updateUnitUI();

        if (etFrom != null) {

            etFrom.requestFocus();

            etFrom.setSelection(etFrom.length());
        }
    }

    private String getUnitSymbol(String unit) {

        switch (unit) {

            case "Km/h": return "km/h";

            case "Km/s": return "km/s";

            case "m/s": return "m/s";

            case "m/min": return "m/min";

            case "mph": return "mph";

            case "ft/s": return "ft/s";

            case "ft/min": return "ft/min";

            case "knots": return "kn";

            default: return unit;
        }
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
        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.speed_convert_banner));
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
