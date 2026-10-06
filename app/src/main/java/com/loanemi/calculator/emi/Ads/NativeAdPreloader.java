package com.loanemi.calculator.emi.Ads;

import android.app.Activity;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback;
import com.loanemi.calculator.emi.utils.Util;

import java.lang.ref.WeakReference;

/**
 * Preloads the exit-dialog native ad only when the user starts the back-press exit flow.
 * Avoids loading on app start for users who never open the exit dialog.
 */
public final class NativeAdPreloader {

    private static final int NATIVE_LAYOUT = 2;

    @Nullable
    private static NativeAd cachedAd;
    private static boolean loading;

    private NativeAdPreloader() {
    }

    public static void preloadIfNeeded(@NonNull Activity activity, @NonNull String adUnitId) {
        if (!Util.isInternetAvailable(activity)) {
            return;
        }
        if (cachedAd != null || loading) {
            return;
        }
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        loading = true;
        WeakReference<Activity> activityRef = new WeakReference<>(activity);

        NativeAdLoader.load(
                AdsHelper.buildNativeAdRequest(adUnitId),
                new NativeAdLoaderCallback() {
                    @Override
                    public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                        loading = false;
                        Activity host = activityRef.get();
                        if (host == null || host.isFinishing() || host.isDestroyed()) {
                            return;
                        }
                        discardCachedAd();
                        cachedAd = nativeAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        loading = false;
                    }
                }
        );
    }

    public static void show(
            @NonNull Activity activity,
            @NonNull FrameLayout adContainer,
            @NonNull String adUnitId,
            @Nullable ShimmerFrameLayout shimmer
    ) {
        if (!Util.isInternetAvailable(activity)) {
            return;
        }

        NativeAd readyAd = cachedAd;
        cachedAd = null;

        if (readyAd != null) {
            AdsHelper.bindNativeAd(activity, readyAd, adContainer, shimmer, NATIVE_LAYOUT);
            return;
        }

        AdsHelper.loadNativeAd(activity, adContainer, adUnitId, shimmer, NATIVE_LAYOUT);
    }

    public static void clear() {
        discardCachedAd();
        loading = false;
    }

    private static void discardCachedAd() {
        cachedAd = null;
    }
}
