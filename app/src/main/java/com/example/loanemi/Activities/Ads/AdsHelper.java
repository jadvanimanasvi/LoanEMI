package com.example.loanemi.Activities.Ads;

import android.app.Activity;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

import com.google.android.libraries.ads.mobile.sdk.banner.AdSize;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

public class AdsHelper {


    public static void loadAdaptiveBanner(AdView adView, Activity activity, String adUnitId) {

        try {

            if (adView == null) {
                return;
            }

            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

            int adWidth = (int) (metrics.widthPixels / metrics.density);

            AdSize adSize = AdSize.getInlineAdaptiveBannerAdSize(adWidth, 62);

            BannerAdRequest bannerAdRequest = new BannerAdRequest.Builder(adUnitId, adSize).build();

            adView.loadAd(bannerAdRequest,

                    new AdLoadCallback<BannerAd>() {

                        @Override
                        public void onAdLoaded(@NonNull BannerAd bannerAd) {
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                        }
                    });

        } catch (Exception e) {
            // Handle exception
        }
    }
}
