package com.example.loanemi.Activities;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.loanemi.Activities.Ads.AdCallback;
import com.example.loanemi.Activities.Ads.AdConfig;
import com.example.loanemi.Activities.Ads.ApNativeAd;
import com.example.loanemi.Activities.Ads.StartFlowConsentManager;
import com.example.loanemi.Activities.introflow.LoanActivityTracker;
import com.example.loanemi.Activities.introflow.LoanStartFlowAdHelper;
import com.example.loanemi.Activities.language.LanguageActivity;
import com.example.loanemi.Activities.language.LocaleHelper;
import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.Constant;
import com.example.loanemi.Activities.utils.MyApplication;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.example.loanemi.databinding.ActivitySplashBinding;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    // Stored in its own prefs file so it does not depend on AppPreference's API
    private static final String SPLASH_PREFS = "splash_prefs";
    private static final String KEY_SPLASH_ANIM_PLAYED = "splash_anim_played";
    public Boolean isAdClicked = false;
    public Handler mHandler = new Handler();
    Runnable r;
    ActivitySplashBinding binding;
    long elapsedTime = 0;
    private FrameLayout bannerView;
    private View bannerShimmer;
    private FrameLayout bannerAdContainer;
    private boolean splashAdsStarted = false;
    private boolean splashAlreadyLeft = false;
    private boolean splashFlowStarted = false;
    private String[] splashWaitMessages;
    private int splashMsgIndex = 0;
    private StartFlowConsentManager consentManager;
    // Lottie speed: 1f = normal, lower = slower
    private static final float FIRST_LAUNCH_SPEED = 0.5f;   // full animation: about 5.4 s
    private static final float REPEAT_LAUNCH_SPEED = 0.5f;  // repeat animation: about 4.1 s
    private final Runnable consentWaitTimeout = this::continueIfConsentStuck;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        LoanActivityTracker.setCurrentString("");
//        MyApplication.instance.applyStartFlowSystemBars(this, findViewById(R.id.main));
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), bars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        LoanActivityTracker.setCurrentActivity(this);
        bannerView = findViewById(R.id.banerView);
        bannerShimmer = findViewById(R.id.bannerShimmer);
        bannerAdContainer = findViewById(R.id.bannerAdContainer);
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (r != null) {
                    mHandler.removeCallbacks(r);
                }
                finish();
            }
        });
        setupSplashLogo();
        init();
    }

    private void setupSplashLogo() {
        if (binding == null) {
            return;
        }
        final SharedPreferences sp = getSharedPreferences(SPLASH_PREFS, MODE_PRIVATE);
        boolean animPlayed = sp.getBoolean(KEY_SPLASH_ANIM_PLAYED, false);

        binding.lottieSplash.setRepeatCount(0); // never loop

        if (animPlayed) {
            // 2nd launch onwards: icon animation first, then text animation
            binding.lottieSplash.setAnimation(R.raw.loanemi_splash_repeat_transparent);
            binding.lottieSplash.setSpeed(REPEAT_LAUNCH_SPEED);
            binding.lottieSplash.playAnimation();
        } else {
            // 1st launch: full animation
            binding.lottieSplash.setAnimation(R.raw.loanemi_splash_animated_transparent);
            binding.lottieSplash.addAnimatorListener(new Animator.AnimatorListener() {
                @Override public void onAnimationStart(Animator animation) { }
                @Override public void onAnimationEnd(Animator animation) {
                    sp.edit().putBoolean(KEY_SPLASH_ANIM_PLAYED, true).apply();
                }
                @Override public void onAnimationCancel(Animator animation) { }
                @Override public void onAnimationRepeat(Animator animation) { }
            });
            binding.lottieSplash.setSpeed(FIRST_LAUNCH_SPEED);
            binding.lottieSplash.playAnimation();
        }
    }

    private void init() {
//        RemoteConfigManager.getInstance().loadCachedConfig(getApplicationContext());
        startSplashProgress();
        startConsentThenAds();
        mHandler.postDelayed(consentWaitTimeout, 8000);
    }

    private void startSplashProgress() {
        splashMsgIndex = 0;
        if (binding != null) {
            binding.progressBar.setMax(100);
            binding.progressBar.setProgress(8);
            binding.progressBar.setIndeterminate(false);
            if (splashWaitMessages != null && splashWaitMessages.length > 0) {
                binding.tvSplashHint.setText(splashWaitMessages[0]);
            }
        }
        if (r != null) {
            mHandler.removeCallbacks(r);
        }
        elapsedTime = 800;
        r = new Runnable() {
            @Override
            public void run() {
                if (binding == null || isFinishing() || isDestroyed()) {
                    return;
                }
                if (elapsedTime < 6500) {
                    elapsedTime += 50;
                    binding.progressBar.setProgress((int) ((elapsedTime * 100) / 10000));
                    if (splashWaitMessages != null && splashWaitMessages.length > 0) {
                        int nextIndex = ((int) (elapsedTime / 2000)) % splashWaitMessages.length;
                        if (nextIndex != splashMsgIndex) {
                            splashMsgIndex = nextIndex;
                            binding.tvSplashHint.setText(splashWaitMessages[splashMsgIndex]);
                        }
                    }
                    mHandler.postDelayed(this, 50);
                } else {
                    binding.progressBar.setProgress(100);
                    moveNext();
                }
            }
        };
        mHandler.post(r);
    }

    private void startConsentThenAds() {
        consentManager = new StartFlowConsentManager(this);
        if (LoanStartFlowAdHelper.isNetworkConnected(this)) {
            consentManager.requestConsent(allowed -> startSplashFlow());
        } else {
            startSplashFlow();
        }
    }

    private void continueIfConsentStuck() {
        if (splashFlowStarted || isFinishing() || isDestroyed()) {
            return;
        }
        if (consentManager != null && consentManager.isConsentFormShowing) {
            mHandler.postDelayed(consentWaitTimeout, 2000);
            return;
        }
        startSplashFlow();
    }

    private void startSplashFlow() {
        if (splashFlowStarted || isFinishing() || isDestroyed()) {
            return;
        }
        splashFlowStarted = true;
        mHandler.removeCallbacks(consentWaitTimeout);
//        RemoteConfigManager.getInstance().loadCachedConfig(getApplicationContext());
        loadSplash();
        if (LoanStartFlowAdHelper.isNetworkConnected(this)) {
//            RemoteConfigManager.getInstance().fetchAppConfig(ignored -> runOnUiThread(this::loadSplashInterIfNeeded));
        }
    }

    private void loadSplashInterIfNeeded() {
        if (isFinishing() || isDestroyed() || splashAlreadyLeft) {
            return;
        }

//        RemoteConfigManager.getInstance().loadCachedConfig(getApplicationContext());
        if (LoanStartFlowAdHelper.isNetworkConnected(this) && LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_splash_inter_show, true) && !LoanStartFlowAdHelper.isInterstitialLoaded() && !LoanStartFlowAdHelper.isInterstitialLoading()) {
            loadSplashInterstitialInBackground();
        }
    }

    public void loadSplash() {
        if (splashAdsStarted) {
            return;
        }
        splashAdsStarted = true;
        boolean online = LoanStartFlowAdHelper.isNetworkConnected(this);
        boolean isUser = AppPreference.getInstance(this).getBoolean(AppPreference.KEY_IS_USER, false);

        if (online && LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_splash_banner_show, true)) {
            loadSplashBanner();
        } else {
            hideBannerCompletely();
        }

        if (online && LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_splash_inter_show, true)) {
            loadSplashInterstitialInBackground();
        }

        if (isUser || !online) {
            return;
        }
        if (LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_language_native1_show, true)) {
            preloadLanguageFirst();
        }
        if (LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_language_native2_show, true)) {
            preloadLanguageSecond();
        }
    }

    private void loadSplashInterstitialInBackground() {
        LoanStartFlowAdHelper.loadInterstitialOnly(this, getString(R.string.splash_inter_1), getString(R.string.splash_inter_2));
    }

    private void loadSplashBanner() {
        if (bannerView == null) {
            return;
        }
        if (!LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_splash_banner_show, true) || !LoanStartFlowAdHelper.isNetworkConnected(this)) {
            hideBannerCompletely();
            return;
        }
        showBannerShimmer();
        AdConfig.getInstance().loadBanner(this, getString(R.string.splash_banner), new AdCallback() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                showLoadedBanner();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError error) {
                super.onAdFailedToLoad(error);
                hideBannerCompletely();
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Constant.markLeavingForAd();
                isAdClicked = true;
                LoanActivityTracker.setCurrentString("AdClick");
            }
        });
    }

    private void preloadLanguageFirst() {
        LoanStartFlowAdHelper.fetchStartFlowNative(this,
                getString(R.string.language_first_native_1),
                getString(R.string.language_first_native_2),
                R.layout.native_large_start_flow_top, new AdCallback() {
                    @Override
                    public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                        super.onNativeAdLoaded(apNativeAd);
                        MyApplication.instance.LanguageFirstNativeAd.postValue(apNativeAd);
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        MyApplication.instance.LanguageFirstNativeAd.postValue(null);
                    }

                    @Override
                    public void onAdFailedToShow(FullScreenContentError adError) {
                        super.onAdFailedToShow(adError);
                        MyApplication.instance.LanguageFirstNativeAd.postValue(null);
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Constant.markLeavingForAd();
                        LoanActivityTracker.setCurrentString("AdClick1");
                    }
                });
    }

    private void preloadLanguageSecond() {
        LoanStartFlowAdHelper.fetchStartFlowNative(this, getString(R.string.language_second_native_1), getString(R.string.language_second_native_2), R.layout.native_large_start_flow_top, new AdCallback() {
            @Override
            public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                super.onNativeAdLoaded(apNativeAd);
                MyApplication.instance.LanguageSecondNativeAd.postValue(apNativeAd);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                MyApplication.instance.LanguageSecondNativeAd.postValue(null);
            }

            @Override
            public void onAdFailedToShow(FullScreenContentError adError) {
                super.onAdFailedToShow(adError);
                MyApplication.instance.LanguageSecondNativeAd.postValue(null);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Constant.markLeavingForAd();
                LoanActivityTracker.setCurrentString("AdClick1");
            }
        });
    }

    public void moveNext() {
        if (splashAlreadyLeft || isFinishing() || isDestroyed()) {
            return;
        }
        if (consentManager != null && consentManager.isConsentFormShowing) {
            mHandler.postDelayed(this::moveNext, 400);
            return;
        }
        splashAlreadyLeft = true;
        if (r != null) {
            mHandler.removeCallbacks(r);
        }
        if (binding != null) {
            binding.progressBar.setProgress(100);
        }
        boolean isUser = AppPreference.getInstance(this).getBoolean(AppPreference.KEY_IS_USER, false);
        if (isUser) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        Intent nextScreen = new Intent(this, LanguageActivity.class);
        nextScreen.putExtra(AppPreference.EXTRA_OPEN_SPLASH_INTER, LoanStartFlowAdHelper.isInterstitialLoaded());
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        nextScreen.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(nextScreen);
        finish();
    }

    private void showBannerShimmer() {
        if (bannerView != null) {
            bannerView.setVisibility(View.VISIBLE);
        }
        if (bannerAdContainer != null) {
            bannerAdContainer.removeAllViews();
            bannerAdContainer.setVisibility(View.GONE);
        }
        if (bannerShimmer != null) {
            bannerShimmer.setVisibility(View.VISIBLE);
            if (bannerShimmer instanceof ShimmerFrameLayout) {
                ((ShimmerFrameLayout) bannerShimmer).startShimmer();
            }
        }
    }

    private void showLoadedBanner() {
        if (bannerShimmer != null) {
            if (bannerShimmer instanceof ShimmerFrameLayout) {
                ((ShimmerFrameLayout) bannerShimmer).stopShimmer();
            }
            bannerShimmer.setVisibility(View.GONE);
        }
        if (bannerAdContainer != null) {
            bannerAdContainer.setVisibility(View.VISIBLE);
        }
    }

    private void hideBannerCompletely() {
        if (bannerShimmer != null) {
            if (bannerShimmer instanceof ShimmerFrameLayout) {
                ((ShimmerFrameLayout) bannerShimmer).stopShimmer();
            }
            bannerShimmer.setVisibility(View.GONE);
        }
        if (bannerAdContainer != null) {
            bannerAdContainer.removeAllViews();
            bannerAdContainer.setVisibility(View.GONE);
        }
        if (bannerView != null) {
            bannerView.setVisibility(View.GONE);
        }
    }

    public void onResume() {
        super.onResume();
        LoanActivityTracker.setCurrentActivity(this);
        Util.hide(this);
    }
}