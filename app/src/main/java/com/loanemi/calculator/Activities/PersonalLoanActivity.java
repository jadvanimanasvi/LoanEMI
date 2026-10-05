package com.loanemi.calculator.Activities;

import static com.loanemi.calculator.utils.Util.setupEdgeToEdge;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.example.loanemi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.loanemi.calculator.Ads.AdsHelper;
import com.loanemi.calculator.utils.Util;

public class PersonalLoanActivity extends AppCompatActivity {
    private AdView bannerAdView;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_loan);
        setupEdgeToEdge(this, R.id.main);

        initViews();
        setUpAd();
    }

    private void initViews() {
        adContainer = findViewById(R.id.bannerContainer);
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
        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.personal_loan_banner));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.hide(this);
    }
}