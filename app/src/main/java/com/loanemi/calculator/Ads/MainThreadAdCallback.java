package com.loanemi.calculator.Ads;

import android.os.Handler;
import android.os.Looper;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd;

/**
 * Ensures all ad callbacks run on the main thread so UI / LiveData updates are safe.
 */
public final class MainThreadAdCallback extends AdCallback {

    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private final AdCallback delegate;

    private MainThreadAdCallback(AdCallback delegate) {
        this.delegate = delegate;
    }

    public static AdCallback wrap(AdCallback callback) {
        if (callback == null) {
            return null;
        }
        if (callback instanceof MainThreadAdCallback) {
            return callback;
        }
        return new MainThreadAdCallback(callback);
    }

    private void run(Runnable action) {
        if (action == null) {
            return;
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            try {
                action.run();
            } catch (Exception ignored) {
            }
        } else {
            MAIN.post(() -> {
                try {
                    action.run();
                } catch (Exception ignored) {
                }
            });
        }
    }

    @Override
    public void onAdLoaded() {
        run(delegate::onAdLoaded);
    }

    @Override
    public void onAdClicked() {
        run(delegate::onAdClicked);
    }

    @Override
    public void onAdClosed() {
        run(delegate::onAdClosed);
    }

    @Override
    public void onAdFailedToLoad(LoadAdError loadAdError) {
        run(() -> delegate.onAdFailedToLoad(loadAdError));
    }

    @Override
    public void onAdFailedToShow(FullScreenContentError adError) {
        run(() -> delegate.onAdFailedToShow(adError));
    }

    @Override
    public void onAdImpression() {
        run(delegate::onAdImpression);
    }

    @Override
    public void onNativeAdLoaded(ApNativeAd apNativeAd) {
        run(() -> delegate.onNativeAdLoaded(apNativeAd));
    }

    @Override
    public void onAdShowedFullScreenContent() {
        run(delegate::onAdShowedFullScreenContent);
    }

    @Override
    public void onAdDismissedFullScreenContent() {
        run(delegate::onAdDismissedFullScreenContent);
    }

    @Override
    public void onAdSplashReady() {
        run(delegate::onAdSplashReady);
    }

    @Override
    public void onNextAction() {
        run(delegate::onNextAction);
    }

    @Override
    public void onNoInternet() {
        run(delegate::onNoInternet);
    }

    @Override
    public void onInterstitialLoad(InterstitialAd interstitialAd) {
        run(() -> delegate.onInterstitialLoad(interstitialAd));
    }
}
