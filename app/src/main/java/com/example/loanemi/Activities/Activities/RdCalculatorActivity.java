package com.example.loanemi.Activities.Activities;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loanemi.Activities.Ads.AdsHelper;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;

public class RdCalculatorActivity extends AppCompatActivity {
    private AdView bannerAdView;
    private FrameLayout adContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rd_calculator);

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
        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.rd_calc_banner));
    }
}