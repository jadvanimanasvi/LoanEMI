package com.loanemi.calculator.Ads;

import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd;

public class AdCallback {
    public void onAdLoaded() {
    }

    public void onAdClicked() {
    }

    public void onAdClosed() {
    }

    public void onAdFailedToLoad(LoadAdError loadAdError) {
    }

    public void onAdFailedToShow(FullScreenContentError adError) {
    }

    public void onAdImpression() {
    }


    public void onAdShowedFullScreenContent() {
    }

    public void onAdDismissedFullScreenContent() {
    }
    public void onNativeAdLoaded(ApNativeAd apNativeAd) {
    }

    public void onNextAction() {
    }

    public void onInterstitialLoad(InterstitialAd interstitialAd) {
    }
    public void onAdSplashReady() {
    }
    public void onNoInternet() {
    }

}


