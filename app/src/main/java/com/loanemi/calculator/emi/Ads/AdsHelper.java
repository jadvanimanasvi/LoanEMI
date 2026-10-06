package com.loanemi.calculator.emi.Ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.loanemi.calculator.emi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.common.VideoOptions;
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView;
import java.util.List;

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


    public static void bindNativeAd(
            Activity activity,
            NativeAd nativeAd,
            FrameLayout adContainer,
            ShimmerFrameLayout shimmer,
            int layoutType
    ) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        @SuppressLint("InflateParams") View view = activity.getLayoutInflater()
                .inflate(layoutType == 1 ? R.layout.native_medium_bottom_2
                        : R.layout.native_medium_bottom, null);

        NativeAdView adView = (NativeAdView) view;

        populateNativeAdView(nativeAd, adView);

        adContainer.removeAllViews();
        adContainer.addView(adView);

        hideNativeAdShimmer(shimmer);

    }

    private static void populateNativeAdView(
            NativeAd nativeAd,
            NativeAdView adView
    ) {

        TextView headline = adView.findViewById(R.id.ad_headline);
        TextView body = adView.findViewById(R.id.ad_body);
        Button cta = adView.findViewById(R.id.ad_call_to_action);
        ImageView icon = adView.findViewById(R.id.ad_app_icon);
        MediaView media = adView.findViewById(R.id.ad_media);

        adView.setHeadlineView(headline);
        adView.setBodyView(body);
        adView.setCallToActionView(cta);
        adView.setIconView(icon);

        if (nativeAd.getHeadline() != null) {

            headline.setText(nativeAd.getHeadline());
            headline.setVisibility(View.VISIBLE);

        } else {

            headline.setVisibility(View.GONE);
        }

        // Body
        if (nativeAd.getBody() != null) {

            body.setText(nativeAd.getBody());
            body.setVisibility(View.VISIBLE);

        } else {

            body.setVisibility(View.GONE);
        }

        // CTA
        if (nativeAd.getCallToAction() != null) {

            cta.setText(nativeAd.getCallToAction());
            cta.setVisibility(View.VISIBLE);

        } else {

            cta.setVisibility(View.GONE);
        }

        // Icon
        if (icon != null) {
            if (nativeAd.getIcon() != null) {
                icon.setVisibility(View.VISIBLE);
                icon.setImageDrawable(nativeAd.getIcon().getDrawable());
            } else {
                icon.setVisibility(View.GONE);
            }
        }


        adView.registerNativeAd(nativeAd, media);
    }

    static void hideNativeAdShimmer(ShimmerFrameLayout shimmer) {
        if (shimmer != null) {
            shimmer.stopShimmer();
            shimmer.setVisibility(View.GONE);
        }
    }

    public static void loadNativeAd(Activity activity, FrameLayout adContainer, String adUnitId, ShimmerFrameLayout shimmer, int layoutType) {
        showNativeAdShimmer(shimmer);
        NativeAdLoader.load(
                buildNativeAdRequest(adUnitId),
                createNativeAdLoaderCallback(activity, adContainer, shimmer, layoutType)
        );
    }

    static void showNativeAdShimmer(ShimmerFrameLayout shimmer) {
        if (shimmer != null) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        }
    }

    static NativeAdRequest buildNativeAdRequest(String adUnitId) {
        NativeAd.NativeAdType adType = NativeAd.NativeAdType.NATIVE;

        VideoOptions videoOptions =
                new VideoOptions.Builder()
                        .setStartMuted(false)
                        .setCustomControlsRequested(false)
                        .build();

        return new NativeAdRequest.Builder(adUnitId, List.of(adType))
                .setVideoOptions(videoOptions)
                .setMediaAspectRatio(NativeAd.NativeMediaAspectRatio.LANDSCAPE)
                .build();
    }

    static NativeAdLoaderCallback createNativeAdLoaderCallback(
            Activity activity,
            FrameLayout adContainer,
            ShimmerFrameLayout shimmer,
            int layoutType
    ) {
        return new NativeAdLoaderCallback() {

            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                activity.runOnUiThread(() -> bindNativeAd(activity, nativeAd, adContainer, shimmer, layoutType));
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                activity.runOnUiThread(() -> hideNativeAdShimmer(shimmer));
            }
        };
    }

}
