package com.loanemi.calculator.Activities.Tools;

import static com.loanemi.calculator.utils.Util.setupEdgeToEdge;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loanemi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.Ads.AdsHelper;
import com.loanemi.calculator.utils.Util;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;

public class LengthConverterActivity extends AppCompatActivity {

    private AdView bannerAdView;
    private FrameLayout adContainer;

    private ImageView backButton;

    private LinearLayout fromUnitSelector;
    private LinearLayout toUnitSelector;
    private FrameLayout btnSwap;
    private LinearLayout btnReset;
    private LinearLayout btnCalculate;
    private EditText etFrom;
    private TextView etTo;
    private TextView tvFromUnit;
    private TextView tvToUnit;
    private TextView tvFromSymbol;
    private TextView tvToSymbol;
    private TextView tvFromInfo;
    private TextView tvToInfo;

    private String fromUnit = "Kilometer";
    private String toUnit = "Meter";

    private final DecimalFormat decimalFormat = new DecimalFormat("0.##########");

    private final Map<String, Double> unitToMeter = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_length_converter);

        setupEdgeToEdge(this, R.id.main);

        initViews();
        initUnits();
        setupDefaultValues();
        setupListeners();
        updateUnitUI();
        setUpAd();
    }

    private void initViews() {

        backButton = findViewById(R.id.backButton);

        fromUnitSelector = findViewById(R.id.fromUnitSelector);
        toUnitSelector = findViewById(R.id.toUnitSelector);
        btnSwap = findViewById(R.id.btnSwap);
        btnReset = findViewById(R.id.btnReset);
        btnCalculate = findViewById(R.id.btnCalculate);
        etFrom = findViewById(R.id.etFrom);
        etTo = findViewById(R.id.etTo);
        tvFromUnit = findViewById(R.id.tvFromUnit);
        tvToUnit = findViewById(R.id.tvToUnit);
        tvFromSymbol = findViewById(R.id.tvFromSymbol);
        tvToSymbol = findViewById(R.id.tvToSymbol);
        tvFromInfo = findViewById(R.id.tvFromInfo);
        tvToInfo = findViewById(R.id.tvToInfo);
        adContainer = findViewById(R.id.bannerContainer);
    }

    private void initUnits() {

        unitToMeter.put("Millimeter", 0.001);
        unitToMeter.put("Centimeter", 0.01);
        unitToMeter.put("Meter", 1.0);
        unitToMeter.put("Kilometer", 1000.0);
        unitToMeter.put("Inch", 0.0254);
        unitToMeter.put("Foot", 0.3048);
        unitToMeter.put("Yard", 0.9144);
        unitToMeter.put("Mile", 1609.344);
        unitToMeter.put("Nautical Mile", 1852.0);
    }

    private void setupDefaultValues() {

        fromUnit = "Kilometer";
        toUnit = "Meter";

        etFrom.setText("0");
        etTo.setText("0");
    }

    private void setupListeners() {

        // Back
        backButton.setOnClickListener(v -> finish());

        // From unit
        fromUnitSelector.setOnClickListener(v -> showUnitDialog(true));

        // To unit
        toUnitSelector.setOnClickListener(v -> showUnitDialog(false));

        // Swap
        btnSwap.setOnClickListener(v -> swapUnits());

        // Reset
        btnReset.setOnClickListener(v -> resetFields());

        btnCalculate.setOnClickListener(v -> calculateConversion());
    }

    private void showUnitDialog(boolean isFromUnit) {

        final String[] units = unitToMeter.keySet().toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(isFromUnit ? "Select From Unit" : "Select To Unit");

        builder.setItems(units, (dialog, which) -> {

            if (isFromUnit) {
                fromUnit = units[which];
            } else {
                toUnit = units[which];
            }

            updateUnitUI();

            etTo.setText("0");
        });

        builder.show();
    }

    private void updateUnitUI() {

        tvFromUnit.setText(fromUnit);
        tvToUnit.setText(toUnit);

        tvFromSymbol.setText(getUnitSymbol(fromUnit));

        tvToSymbol.setText(getUnitSymbol(toUnit));

        tvFromInfo.setText(getInfoText(fromUnit));

        tvToInfo.setText(getInfoText(toUnit));
    }

    private void calculateConversion() {

        String input = etFrom.getText().toString().trim();

        if (input.isEmpty()) {

            etFrom.setError("Enter a value");

            Toast.makeText(this, "Please enter a value", Toast.LENGTH_SHORT).show();

            return;
        }

        double value;

        try {

            value = Double.parseDouble(input);

        } catch (NumberFormatException e) {

            etFrom.setError("Invalid number");

            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();

            return;
        }

        if (Double.isNaN(value) || Double.isInfinite(value)) {

            etFrom.setError("Invalid number");

            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show();

            return;
        }

        double result = convertLength(value, fromUnit, toUnit);

        etTo.setText(formatNumber(result));
    }

    private double convertLength(double value, String from, String to) {

        Double fromInMeter = unitToMeter.get(from);

        Double toInMeter = unitToMeter.get(to);

        if (fromInMeter == null || toInMeter == null) {
            return 0;
        }

        double valueInMeters = value * fromInMeter;

        return valueInMeters / toInMeter;
    }

    private void swapUnits() {

        String oldFromUnit = fromUnit;

        fromUnit = toUnit;
        toUnit = oldFromUnit;

        updateUnitUI();

        etTo.setText("0");
    }

    private void resetFields() {

        fromUnit = "Kilometer";
        toUnit = "Meter";

        etFrom.setText("0");
        etTo.setText("0");

        updateUnitUI();

        etFrom.requestFocus();
    }

    private String getUnitSymbol(String unit) {

        switch (unit) {

            case "Millimeter": return "mm";

            case "Centimeter": return "cm";

            case "Meter": return "m";

            case "Kilometer": return "km";

            case "Inch": return "in";

            case "Foot": return "ft";

            case "Yard": return "yd";

            case "Mile": return "mi";

            case "Nautical Mile": return "nmi";

            default: return "";
        }
    }

    private String getInfoText(String unit) {

        switch (unit) {

            case "Millimeter": return "1 millimeter = 0.001 meter";

            case "Centimeter": return "1 centimeter = 0.01 meter";

            case "Meter": return "1 meter = 100 centimeter";

            case "Kilometer": return "1 kilometer = 1000 meter";

            case "Inch": return "1 inch = 2.54 centimeter";

            case "Foot": return "1 foot = 0.3048 meter";

            case "Yard": return "1 yard = 0.9144 meter";

            case "Mile": return "1 mile = 1.609344 kilometer";

            case "Nautical Mile": return "1 nautical mile = 1.852 kilometer";

            default: return "";
        }
    }

    private String formatNumber(double value) {

        if (value == 0) {
            return "0";
        }

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

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.length_convert_banner));
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