package com.loanemi.calculator.Activities.Tools;

import static com.loanemi.calculator.utils.Util.setupEdgeToEdge;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loanemi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.Ads.AdsHelper;
import com.loanemi.calculator.utils.Util;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AgeCalculationActivity extends AppCompatActivity {

    private AdView bannerAdView;
    private FrameLayout adContainer;

    private ImageView backButton;
    private ImageView ivCalendar;
    private ImageView btnClear;

    private EditText etDob;
    private View btnCalculate;

    private TextView tvAsOf;
    private TextView tvYears;
    private TextView tvMonths;
    private TextView tvDays;
    private TextView tvNextBirthday;
    private TextView tvDaysLeft;

    private final Calendar dobCalendar = Calendar.getInstance();

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_age_calculation);

        setupEdgeToEdge(this, R.id.main);

        initViews();
        setupListeners();

        setDefaultDate();
        calculateAge();

        setUpAd();
    }


    private void initViews() {

        backButton = findViewById(R.id.backButton);

        ivCalendar = findViewById(R.id.ivCalendar);
        btnClear = findViewById(R.id.btnClear);
        etDob = findViewById(R.id.etDob);
        btnCalculate = findViewById(R.id.btnCalculate);
        tvAsOf = findViewById(R.id.tvAsOf);
        tvYears = findViewById(R.id.tvYears);
        tvMonths = findViewById(R.id.tvMonths);
        tvDays = findViewById(R.id.tvDays);
        tvNextBirthday = findViewById(R.id.tvNextBirthday);
        tvDaysLeft = findViewById(R.id.tvDaysLeft);
        adContainer = findViewById(R.id.bannerContainer);
    }

    private void setupListeners() {

        backButton.setOnClickListener(v -> finish());

        ivCalendar.setOnClickListener(v -> showDatePicker());

        etDob.setOnClickListener(v -> showDatePicker());

        btnClear.setOnClickListener(v -> clearDate());

        btnCalculate.setOnClickListener(v -> calculateAge());
    }

    private void setDefaultDate() {

        dobCalendar.set(2000, Calendar.APRIL, 15);

        clearTime(dobCalendar);

        etDob.setText(dateFormat.format(dobCalendar.getTime()));
    }

    private void showDatePicker() {

        Calendar today = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {

            dobCalendar.set(year, month, dayOfMonth);

            clearTime(dobCalendar);

            etDob.setText(dateFormat.format(dobCalendar.getTime()));

            calculateAge();
        }, dobCalendar.get(Calendar.YEAR), dobCalendar.get(Calendar.MONTH), dobCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMaxDate(today.getTimeInMillis());

        datePickerDialog.show();
    }

    private void clearDate() {

        etDob.setText("");

        tvYears.setText("0");
        tvMonths.setText("0");
        tvDays.setText("0");
        tvAsOf.setText("Select your date of birth");
        tvNextBirthday.setText("--");
        tvDaysLeft.setText("--");
    }

    private void calculateAge() {

        String dobText = etDob.getText().toString().trim();

        if (dobText.isEmpty()) {

            tvYears.setText("0");
            tvMonths.setText("0");
            tvDays.setText("0");

            tvAsOf.setText("Select your date of birth");

            tvNextBirthday.setText("--");
            tvDaysLeft.setText("--");

            return;
        }

        Calendar birthDate = (Calendar) dobCalendar.clone();

        Calendar today = Calendar.getInstance();

        clearTime(birthDate);
        clearTime(today);

        if (birthDate.after(today)) {

            Toast.makeText(this, "Date of birth cannot be in the future", Toast.LENGTH_SHORT).show();

            return;
        }

        int years = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        int months = today.get(Calendar.MONTH) - birthDate.get(Calendar.MONTH);

        int days = today.get(Calendar.DAY_OF_MONTH) - birthDate.get(Calendar.DAY_OF_MONTH);


        if (days < 0) {

            months--;

            Calendar previousMonth = (Calendar) today.clone();

            previousMonth.add(Calendar.MONTH, -1);

            days += previousMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        }

        if (months < 0) {

            years--;

            months += 12;
        }

        tvYears.setText(String.valueOf(years));

        tvMonths.setText(String.valueOf(months));

        tvDays.setText(String.valueOf(days));

        String todayText = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(today.getTime());

        tvAsOf.setText("As of today (" + todayText + ")");

        calculateNextBirthday(birthDate, today);
    }

    private void calculateNextBirthday(Calendar birthDate, Calendar today) {

        Calendar nextBirthday = Calendar.getInstance();

        int birthMonth = birthDate.get(Calendar.MONTH);

        int birthDay = birthDate.get(Calendar.DAY_OF_MONTH);

        int currentYear = today.get(Calendar.YEAR);


        if (birthMonth == Calendar.FEBRUARY && birthDay == 29 && !isLeapYear(currentYear)) {

            nextBirthday.set(currentYear, Calendar.FEBRUARY, 28, 0, 0, 0);

        } else {

            nextBirthday.set(currentYear, birthMonth, birthDay, 0, 0, 0);
        }

        nextBirthday.set(Calendar.MILLISECOND, 0);


        if (nextBirthday.before(today)) {

            currentYear++;

            if (birthMonth == Calendar.FEBRUARY && birthDay == 29 && !isLeapYear(currentYear)) {

                nextBirthday.set(currentYear, Calendar.FEBRUARY, 28, 0, 0, 0);

            } else {

                nextBirthday.set(currentYear, birthMonth, birthDay, 0, 0, 0);
            }

            nextBirthday.set(Calendar.MILLISECOND, 0);
        }

        tvNextBirthday.setText(displayDateFormat.format(nextBirthday.getTime()));


        long difference = nextBirthday.getTimeInMillis() - today.getTimeInMillis();

        long daysLeft = difference / (24L * 60L * 60L * 1000L);


        if (daysLeft == 0) {

            tvDaysLeft.setText("Today");

        } else if (daysLeft == 1) {

            tvDaysLeft.setText("In 1 day");

        } else {

            tvDaysLeft.setText("In " + daysLeft + " days");
        }
    }

    private boolean isLeapYear(int year) {

        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    private void clearTime(Calendar calendar) {

        calendar.set(Calendar.HOUR_OF_DAY, 0);

        calendar.set(Calendar.MINUTE, 0);

        calendar.set(Calendar.SECOND, 0);

        calendar.set(Calendar.MILLISECOND, 0);
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

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.age_calc_banner));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Util.hide(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (bannerAdView != null) {

            if (bannerAdView.getParent() instanceof ViewGroup) {

                ((ViewGroup) bannerAdView.getParent()).removeView(bannerAdView);
            }

            bannerAdView.destroy();
            bannerAdView = null;
        }
    }
}