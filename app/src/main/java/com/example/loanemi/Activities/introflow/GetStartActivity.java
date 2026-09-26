package com.example.loanemi.Activities.introflow;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.loanemi.Activities.Ads.AdConfig;
import com.example.loanemi.Activities.BaseActivity;
import com.example.loanemi.Activities.MainActivity;
import com.example.loanemi.Activities.remote.LoanIntroConfig;
import com.example.loanemi.Activities.remote.RemoteConfigManager;
import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.MyApplication;
import com.example.loanemi.R;


public class GetStartActivity extends BaseActivity {

    AppCompatButton btnGetStart;
    AppCompatTextView tvGetStarted;
    FrameLayout layoutAdNative;
    FrameLayout linBtn;
    View shimmerContainerNativeLarge;
    AppPreference prefsUtil;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_get_start);
        View main = findViewById(R.id.main);
        layoutAdNative = findViewById(R.id.layoutAdNative);
        shimmerContainerNativeLarge = findViewById(R.id.shimmerContainerNativeLarge);
        btnGetStart = findViewById(R.id.btnGetStart);
        tvGetStarted = findViewById(R.id.tvGetStarted);
        linBtn = findViewById(R.id.linBtn);
        MyApplication.instance.applyStartFlowSystemBars(this, main);
        if (main != null) {
            setupEdgeToEdge(main);
        }
        prefsUtil = AppPreference.getInstance(this);

        if (LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_get_started_native_show, true) && LoanStartFlowAdHelper.isNetworkConnected(this)) {
            showNativeAd();
        } else {
            hideGetStartedNative();
        }

        btnGetStart.setOnClickListener(view -> onGetStartedClick());
        tvGetStarted.setOnClickListener(view -> onGetStartedClick());
        setupUiGetStarted();
        LoanStartFlowAdHelper.handleLanguageDoneNextScreen(this);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }

    private void onGetStartedClick() {
        if (prefsUtil != null) {
            prefsUtil.setBoolean(AppPreference.KEY_IS_USER, true);
        }
        LoanActivityTracker.setCurrentFragment(new LoanStartFragment_1());
        startActivity(new Intent(this, MainActivity.class).putExtra("SHOW_MESSAGE", true));
        finish();
    }

    private void showNativeAd() {
        MyApplication.instance.GetStartedNativeAd.observe(this, apNativeAd -> {
            if (isFinishing() || isDestroyed()) {
                return;
            }
            if (apNativeAd != null) {
                AdConfig.getInstance().populateNativeAdView(this, apNativeAd, layoutAdNative, shimmerContainerNativeLarge);
                return;
            }
            hideGetStartedNative();
        });
    }

    private void setupUiGetStarted() {
        int spaceDp = 10;
        boolean showButton = true;
//        LoanIntroConfig config = RemoteConfigManager.getInstance().galleryIntroConfig;
        /*if (config != null) {
            spaceDp = config.getGet_started_bottom_space();
            showButton = config.isShow_get_started_btn();
        }*/
        int spacePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceDp, getResources().getDisplayMetrics());
        if (showButton) {
            btnGetStart.setVisibility(View.VISIBLE);
            tvGetStarted.setVisibility(View.GONE);
            BottomSpaceApply(linBtn != null ? linBtn : btnGetStart, spacePx);
        } else {
            btnGetStart.setVisibility(View.GONE);
            tvGetStarted.setVisibility(View.VISIBLE);
            tvGetStarted.setAlpha(1f);
            tvGetStarted.bringToFront();
            BottomSpaceApply(linBtn != null ? linBtn : tvGetStarted, spacePx);
        }
    }

    private void BottomSpaceApply(View view, int spacePx) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutParams;
            params.bottomMargin = spacePx;
            view.setLayoutParams(params);
        }
    }

    private void hideGetStartedNative() {
        if (layoutAdNative != null) {
            layoutAdNative.setVisibility(View.INVISIBLE);
        }
        if (shimmerContainerNativeLarge != null) {
            shimmerContainerNativeLarge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        View main = findViewById(R.id.main);
        MyApplication.instance.applyStartFlowSystemBars(this, main);
        LoanStartFlowAdHelper.tryShowDeferredInterstitial();
    }
}
