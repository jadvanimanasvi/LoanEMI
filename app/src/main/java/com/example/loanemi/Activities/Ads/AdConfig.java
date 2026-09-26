package com.example.loanemi.Activities.Ads;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Lifecycle;

import com.example.loanemi.Activities.introflow.LoanActivityTracker;
import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.Constant;
import com.example.loanemi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.libraries.ads.mobile.sdk.banner.AdSize;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAd;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.banner.BannerAdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback;
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest;
import com.google.android.libraries.ads.mobile.sdk.common.AdValue;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.common.VideoOptions;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.nativead.MediaView;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdEventCallback;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoader;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdLoaderCallback;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdRequest;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAdView;
import java.util.List;

public class AdConfig {

    public LoadingAdsDialog dialog;
    private static AdConfig instance;
    private AdView splashBannerAdView;
    public boolean openActivityAfterShowInterAds = false;
    private static final String TAG = "Ads";

    public static AdConfig getInstance() {
        if (instance == null) {
            instance = new AdConfig();
        }
        return instance;
    }

    public static void loadAdaptiveBanner(AdView adView, Activity activity, String adUnitId) {

        try {
            if (adView == null) {
                return;
            }

            DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

            int adWidth = (int) (metrics.widthPixels / metrics.density);

            AdSize adSize = AdSize.getInlineAdaptiveBannerAdSize(adWidth, 60);

            BannerAdRequest bannerAdRequest = new BannerAdRequest.Builder(adUnitId, adSize).build();

            adView.loadAd(bannerAdRequest, new AdLoadCallback<BannerAd>() {

                @Override
                public void onAdLoaded(@NonNull BannerAd bannerAd) {
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                }
            });

        } catch (Exception e) {
            // Exception
        }
    }

    public static void loadNativeAd(Activity activity, FrameLayout adContainer, String adUnitId, ShimmerFrameLayout shimmer) {

        if (shimmer != null) {
            shimmer.setVisibility(View.VISIBLE);
            shimmer.startShimmer();
        }

        NativeAd.NativeAdType adType = NativeAd.NativeAdType.NATIVE;

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(false).setCustomControlsRequested(false).build();

        NativeAdRequest adRequest = new NativeAdRequest.Builder(adUnitId, List.of(adType)).setVideoOptions(videoOptions).build();

        NativeAdLoaderCallback callback = new NativeAdLoaderCallback() {

            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                attachNativePaidListener(activity, nativeAd);
                activity.runOnUiThread(() -> {

                    @SuppressLint("InflateParams") View view = activity.getLayoutInflater().inflate(R.layout.native_medium_bottom, null);

                    NativeAdView adView = (NativeAdView) view;
                    populateNativeAdView(nativeAd, adView);

                    adContainer.removeAllViews();
                    adContainer.addView(adView);

                    if (shimmer != null) {
                        shimmer.stopShimmer();
                        shimmer.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                activity.runOnUiThread(() -> {
                    if (shimmer != null) {
                        shimmer.stopShimmer();
                        shimmer.setVisibility(View.GONE);
                    }
                });
            }
        };

        NativeAdLoader.load(adRequest, callback);
    }

    private static void attachNativePaidListener(Context context, NativeAd nativeAd) {
        if (context == null || nativeAd == null) {
            return;
        }
        try {
            nativeAd.setAdEventCallback(new NativeAdEventCallback() {
                @Override
                public void onAdPaid(@NonNull AdValue adValue) {
//                    FirebaseEventUtil.logPaidAdImpression(context, adValue, AdType.NATIVE);
                }

                @Override
                public void onAdClicked() {
                    Constant.markLeavingForAd();
//                    ActivityTracker.setCurrentString("AdClick");
                }
            });
        } catch (Exception ignored) {
        }
    }
    public void loadNativeAd(Activity activity, String highAdUnitId, String adUnitId, int nativeLayoutRes, AdCallback adCallback) {

        NativeAd.NativeAdType adType = NativeAd.NativeAdType.NATIVE;

        VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(false).setCustomControlsRequested(false).build();

        loadNativeAdWithFallback(activity, highAdUnitId, adUnitId, nativeLayoutRes, videoOptions, adType, adCallback, true);
    }

    private void loadNativeAdWithFallback(Activity activity, String highAdUnitId, String adUnitId, int nativeLayoutRes, VideoOptions videoOptions, NativeAd.NativeAdType adType, AdCallback adCallback, boolean tryHighFirst) {

        String unitToUse = tryHighFirst ? highAdUnitId : adUnitId;

        NativeAdRequest adRequest = new NativeAdRequest.Builder(unitToUse, List.of(adType)).setVideoOptions(videoOptions).build();

        NativeAdLoader.load(adRequest, new NativeAdLoaderCallback() {

            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                if (adCallback != null) {
                    adCallback.onNativeAdLoaded(new ApNativeAd(nativeLayoutRes, nativeAd));
                }
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                if (tryHighFirst && !highAdUnitId.equals(adUnitId)) {
                    // retry once with the normal (non-high) unit id
                    loadNativeAdWithFallback(activity, highAdUnitId, adUnitId, nativeLayoutRes, videoOptions, adType, adCallback, false);
                } else if (adCallback != null) {
                    adCallback.onAdFailedToLoad(loadAdError);
                }
            }
        });
    }

    private static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {

        MediaView mediaView = adView.findViewById(R.id.ad_media);
        TextView headline = adView.findViewById(R.id.ad_headline);
        TextView body = adView.findViewById(R.id.ad_body);
        Button cta = adView.findViewById(R.id.ad_call_to_action);
        ImageView icon = adView.findViewById(R.id.ad_app_icon);

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

        adView.registerNativeAd(nativeAd, mediaView);
    }

    public void populateNativeAdView(Activity activity, ApNativeAd apNativeAd, FrameLayout adContainer, View shimmer) {

        if (activity == null || apNativeAd == null || adContainer == null) {
            return;
        }

        NativeAd nativeAd = apNativeAd.getAdmobNativeAd();

        if (nativeAd == null) {
            stopNativeShimmer(shimmer);
            return;
        }

        try {

            startNativeShimmer(shimmer);

            NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(activity).inflate(apNativeAd.getLayoutCustomNative(), null);

            populateNativeAdView(nativeAd, nativeAdView);

            adContainer.removeAllViews();

            adContainer.addView(nativeAdView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

            @SuppressLint("ClickableViewAccessibility") View.OnTouchListener touchListener = (v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Constant.markLeavingForAd();
                    LoanActivityTracker.setCurrentString("AdClick");
                }
                return false;
            };

            int[] clickableIds = {R.id.ad_body, R.id.ad_app_icon, R.id.ad_headline, R.id.ad_call_to_action, R.id.ad_media};
            for (int id : clickableIds) {
                View child = nativeAdView.findViewById(id);
                if (child != null) {
                    child.setOnTouchListener(touchListener);
                }
            }

            stopNativeShimmer(shimmer);

        } catch (Exception e) {
            adContainer.removeAllViews();

            stopNativeShimmer(shimmer);
        }
    }

    public static void startNativeShimmer(View shimmerRoot) {
        if (shimmerRoot == null) {
            return;
        }
        shimmerRoot.setVisibility(View.VISIBLE);
        walkNativeShimmer(shimmerRoot, true);
    }

    public static void stopNativeShimmer(View shimmerRoot) {
        if (shimmerRoot == null) {
            return;
        }
        walkNativeShimmer(shimmerRoot, false);
        shimmerRoot.setVisibility(View.GONE);
    }

    private static void walkNativeShimmer(View view, boolean start) {
        if (view instanceof ShimmerFrameLayout) {
            ShimmerFrameLayout shimmer = (ShimmerFrameLayout) view;
            if (start) {
                shimmer.setVisibility(View.VISIBLE);
                shimmer.startShimmer();
            } else {
                shimmer.stopShimmer();
            }
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                walkNativeShimmer(group.getChildAt(i), start);
            }
        }
    }


    public void populateNativeAdView(Activity activity2, ApNativeAd apNativeAd, FrameLayout frameLayout, View shimmerFrameLayout,
                                     Runnable onAdClicked, OnAdViewReady onAdViewReady) {
        try {
            if (apNativeAd.getAdmobNativeAd() == null && apNativeAd.getNativeView() == null) {
                stopNativeShimmer(shimmerFrameLayout);
                return;
            }
            NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(activity2).inflate(apNativeAd.getLayoutCustomNative(), null);
            stopNativeShimmer(shimmerFrameLayout);
            frameLayout.setVisibility(View.VISIBLE);

            NativeAd nativeAd = apNativeAd.getAdmobNativeAd();
            AdConfig.getInstance().populateUnifiedNativeAdView(apNativeAd.getAdmobNativeAd(), nativeAdView);
            populateNativeAdView(nativeAd, nativeAdView);
            frameLayout.removeAllViews();
            frameLayout.addView(nativeAdView);

            if (onAdViewReady != null) {
                onAdViewReady.onAdViewReady(nativeAdView);
            }

            @SuppressLint("ClickableViewAccessibility") View.OnTouchListener touchListener = (v, event) -> {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    Constant.markLeavingForAd();
                    LoanActivityTracker.setCurrentString("AdClick");
                    if (onAdClicked != null) onAdClicked.run();
                }
                return false;
            };

            int[] clickableIds = {R.id.ad_body, R.id.ad_app_icon, R.id.ad_headline, R.id.ad_call_to_action, R.id.ad_media,};

            for (int id : clickableIds) {
                View v = nativeAdView.findViewById(id);
                if (v != null) {
                    v.setOnTouchListener(touchListener);
                }
            }

        } catch (Exception unused) {
            //Exception
        }
    }

    public void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView nativeAdView) {
        MediaView mediaView = nativeAdView.findViewById(R.id.ad_media);
        TextView headline = nativeAdView.findViewById(R.id.ad_headline);
        TextView body = nativeAdView.findViewById(R.id.ad_body);
        Button cta = nativeAdView.findViewById(R.id.ad_call_to_action);
        ImageView icon = nativeAdView.findViewById(R.id.ad_app_icon);
        /*TextView price = nativeAdView.findViewById(R.id.ad_price);
        RatingBar starRating = nativeAdView.findViewById(R.id.ad_stars);
        TextView advertiser = nativeAdView.findViewById(R.id.ad_advertiser);*/

        nativeAdView.setHeadlineView(headline);
        nativeAdView.setBodyView(body);
        nativeAdView.setCallToActionView(cta);
        nativeAdView.setIconView(icon);
      /*  nativeAdView.setPriceView(price);
        nativeAdView.setStarRatingView(starRating);
        nativeAdView.setAdvertiserView(advertiser);*/

        try {
            if (headline != null) {
                headline.setText(nativeAd.getHeadline());
                headline.setVisibility(View.VISIBLE);
            }

            if (body != null) {
                if (nativeAd.getBody() == null) {
                    body.setVisibility(View.INVISIBLE);
                } else {
                    body.setText(nativeAd.getBody());
                    body.setVisibility(View.VISIBLE);
                }
            }

            if (cta != null) {
                if (nativeAd.getCallToAction() == null) {
                    cta.setVisibility(View.INVISIBLE);
                } else {
                    cta.setText(nativeAd.getCallToAction());
                    cta.setVisibility(View.VISIBLE);
                }
            }

            if (icon != null) {
                if (nativeAd.getIcon() == null) {
                    icon.setVisibility(View.GONE);
                } else {
                    icon.setImageDrawable(nativeAd.getIcon().getDrawable());
                    icon.setVisibility(View.VISIBLE);
                }
            }

           /* if (price != null) {
                if (nativeAd.getPrice() == null) {
                    price.setVisibility(View.INVISIBLE);
                } else {
                    price.setText(nativeAd.getPrice());
                    price.setVisibility(View.VISIBLE);
                }
            }

            if (starRating != null) {
                if (nativeAd.getStarRating() == null) {
                    starRating.setVisibility(View.INVISIBLE);
                } else {
                    starRating.setRating(nativeAd.getStarRating().floatValue());
                    starRating.setVisibility(View.VISIBLE);
                }
            }

            if (advertiser != null) {
                if (nativeAd.getAdvertiser() == null) {
                    advertiser.setVisibility(View.INVISIBLE);
                } else {
                    advertiser.setText(nativeAd.getAdvertiser());
                    advertiser.setVisibility(View.VISIBLE);
                }
            }*/

        } catch (Exception e) {
            Log.d("TAG@@@", "populateUnifiedNativeAdView: " + e.getLocalizedMessage());
        }
        nativeAdView.registerNativeAd(nativeAd, mediaView);
    }

    public void populateNativeAdView2(Activity activity2, ApNativeAd apNativeAd, FrameLayout frameLayout, ShimmerFrameLayout shimmerFrameLayout) {
        try {
            if (apNativeAd.getAdmobNativeAd() == null && apNativeAd.getNativeView() == null) {
                Log.d("MyDataError", "Gone:" + apNativeAd);
                if (shimmerFrameLayout != null) shimmerFrameLayout.setVisibility(View.GONE);
                return;
            }
            NativeAdView nativeAdView = (NativeAdView) LayoutInflater.from(activity2).inflate(apNativeAd.getLayoutCustomNative(), (ViewGroup) null);
            if (shimmerFrameLayout != null) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);
            }
            frameLayout.setVisibility(View.VISIBLE);
            AdConfig.getInstance().populateUnifiedNativeAdView(apNativeAd.getAdmobNativeAd(), nativeAdView);
            frameLayout.removeAllViews();

            frameLayout.addView(nativeAdView);

        } catch (Exception e) {
        }
    }

    public void loadAndShowInter(AppCompatActivity activity, String highAdUnitId, String adUnitId, long timeoutMs, boolean showDialog, long minDelayMs, AdCallback adCallback) {
        loadInterInternal(activity, highAdUnitId, adUnitId, timeoutMs, showDialog, minDelayMs, adCallback);
    }

    private void loadInterInternal(AppCompatActivity activity, String primaryAdUnitId, String fallbackAdUnitId, long timeoutMs, boolean showDialog, long minDelayMs, AdCallback adCallback) {
        try {
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }

            if (showDialog) {
                dialog = new LoadingAdsDialog(activity);
                dialog.setCancelable(false);
                dialog.show();
            }

            Handler timeoutHandler = new Handler(Looper.getMainLooper());
            final boolean[] handled = {false};

            Runnable timeoutRunnable = () -> {
                if (!handled[0]) {
                    handled[0] = true;
                    safeDismiss(activity);
                    if (adCallback != null) adCallback.onNextAction();
                }
            };
            if (timeoutMs > 0) {
                timeoutHandler.postDelayed(timeoutRunnable, timeoutMs);
            }

            InterstitialAd.load(new AdRequest.Builder(primaryAdUnitId).build(), new AdLoadCallback<InterstitialAd>() {

                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    if (handled[0]) return;
                    handled[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);

                    setInterstitialAdEventCallback(interstitialAd, adCallback);

//                            if (adCallback != null) adCallback.onAdSplashReady();

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (activity.isDestroyed() || activity.isFinishing()) {
                            safeDismiss(activity);
                            if (adCallback != null) adCallback.onAdClosed();
                            return;
                        }

                        if (activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                            interstitialAd.show(activity);
                            new Handler(Looper.getMainLooper()).postDelayed(() -> safeDismiss(activity), 500);
                        } else {
                            safeDismiss(activity);
                            if (adCallback != null) adCallback.onAdClosed();
                        }
                    }, minDelayMs);
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    if (fallbackAdUnitId != null && !fallbackAdUnitId.equals(primaryAdUnitId)) {
                        loadInterInternal(activity, fallbackAdUnitId, null, timeoutMs, false, minDelayMs, adCallback);
                        return;
                    }
                    if (handled[0]) return;
                    handled[0] = true;
                    timeoutHandler.removeCallbacks(timeoutRunnable);

                    safeDismiss(activity);
                    if (adCallback != null) adCallback.onAdFailedToLoad(adError);
                }
            });

        } catch (Exception e) {
            safeDismiss(activity);
            if (adCallback != null) adCallback.onAdClosed();
        }
    }


    private void setInterstitialAdEventCallback(InterstitialAd interstitialAd, AdCallback adCallback) {
        interstitialAd.setAdEventCallback(new InterstitialAdEventCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
                if (adCallback != null) adCallback.onAdShowedFullScreenContent();
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                if (adCallback != null) adCallback.onAdClosed();
                if (adCallback != null) adCallback.onAdDismissedFullScreenContent();
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError fullScreenContentError) {
                if (adCallback != null) adCallback.onAdFailedToShow(fullScreenContentError);
            }

            @Override
            public void onAdImpression() {
                if (adCallback != null) adCallback.onAdImpression();
            }

            @Override
            public void onAdClicked() {
                if (adCallback != null) adCallback.onAdClicked();
            }
        });
    }

    private void safeDismiss(AppCompatActivity activity) {

        if (dialog == null) {
            return;
        }

        try {

            if (activity.isFinishing() || activity.isDestroyed()) {
                dialog = null;
                return;
            }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        } catch (Exception ignored) {

        } finally {
            dialog = null;

        }
    }

    public void loadNativePriorityAlternate(Activity activity, String str, String str2, int i2, AdCallback adCallback) {
        try {

            final AdCallback adCallback2 = adCallback;
            final Activity activity2 = activity;
            final String str3 = str2;
            final int i3 = i2;
            loadNativeAdResultCallback(activity, str, i2, new AdCallback() {
                public void onAdClicked() {
                    super.onAdClicked();
                    adCallback2.onAdClicked();
                }

                public void onAdFailedToLoad(LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                    Log.e(AdConfig.TAG, "onAdFailedToLoad: loadAdNativeLanguageAlternate priority - " + loadAdError.getMessage());
                    AdConfig.this.loadNativeAdResultCallback(activity2, str3, i3, new AdCallback() {
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            Log.e(AdConfig.TAG, "onAdFailedToLoad: loadAdNativeLanguageAlternate normal - " + loadAdError.getMessage());
                            adCallback2.onAdFailedToLoad(loadAdError);
                        }

                        public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                            super.onNativeAdLoaded(apNativeAd);
                            Log.d(AdConfig.TAG, "onNativeAdLoaded: loadAdNativeLanguageAlternate normal");
                            adCallback2.onNativeAdLoaded(apNativeAd);
                        }
                    });
                }

                public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                    super.onNativeAdLoaded(apNativeAd);
                    Log.d(AdConfig.TAG, "onNativeAdLoaded: loadAdNativeLanguageAlternate priority");
                    adCallback2.onNativeAdLoaded(apNativeAd);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadNativeAdResultCallback(Activity activity, String adUnitId, final int nativeLayoutRes, final AdCallback adCallback) {

        try {
            NativeAd.NativeAdType adType = NativeAd.NativeAdType.NATIVE;

            VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(false).setCustomControlsRequested(false).build();

            NativeAdRequest adRequest = new NativeAdRequest.Builder(adUnitId, List.of(adType)).setVideoOptions(videoOptions).build();

            NativeAdLoader.load(adRequest, new NativeAdLoaderCallback() {

                @Override
                public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    if (adCallback != null) {
                        adCallback.onNativeAdLoaded(new ApNativeAd(nativeLayoutRes, nativeAd));
                    }
                }

                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    if (adCallback != null) {
                        adCallback.onAdFailedToLoad(loadAdError);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getInterstitialAds(final Context context2, String str, AdCallback adCallback) {
//        if (AppPurchase.getInstance().isPurchased(context2) || AdmobHelper.getNumClickAdsPerDay(context2, str) >= this.maxClickAds) {
//            adCallback.onInterstitialLoad((InterstitialAd) null);
//        }

        final AdCallback safeCallback = MainThreadAdCallback.wrap(adCallback);

        InterstitialAd.load(new AdRequest.Builder(str).build(), new AdLoadCallback<InterstitialAd>() {
            @Override
            public void onAdLoaded(@NonNull final InterstitialAd interstitialAd) {

                if (safeCallback != null) {
                    safeCallback.onInterstitialLoad(interstitialAd);
                }

                interstitialAd.setAdEventCallback(new InterstitialAdEventCallback() {

                    @Override
                    public void onAdShowedFullScreenContent() {
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        AppPreference.setLastImpressionInterstitialTime(context2);

                        if (safeCallback != null) {

                            if (!AdConfig.this.openActivityAfterShowInterAds) {
                                safeCallback.onNextAction();
                            }

                            safeCallback.onAdClosed();
                        }
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError fullScreenContentError) {
                        try {
                            if (AdConfig.this.dialog != null) {
                                AdConfig.this.dialog.dismiss();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Dialog dismiss error", e);
                        }

                        if (safeCallback != null) {
                            safeCallback.onAdFailedToShow(fullScreenContentError);
                            safeCallback.onNextAction();
                        }
                    }

                    @Override
                    public void onAdImpression() {
                    }

                    @Override
                    public void onAdPaid(@NonNull AdValue adValue) {
//                        FirebaseEventUtil.logPaidAdImpression(context2, adValue, AdType.INTERSTITIAL);
                    }

                    @Override
                    public void onAdClicked() {
                        Constant.markLeavingForAd();
                        LoanActivityTracker.setCurrentString("AdClick");

                        if (safeCallback != null) {
                            safeCallback.onAdClicked();
                        }
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {

                Log.e(TAG, "Next-Gen Interstitial failed to load: " + loadAdError.getMessage());

                try {
                    if (AdConfig.this.dialog != null) {
                        AdConfig.this.dialog.dismiss();
                    }
                } catch (Exception e) {
                    Log.e(AdConfig.TAG, "Dialog dismiss error", e);
                }

                if (safeCallback != null) {
                    safeCallback.onAdFailedToLoad(loadAdError);
                    safeCallback.onNextAction();
                }
            }
        });
    }

    public void loadBanner(Activity activity, String adUnitId, AdCallback adCallback) {

        final AdCallback safeCallback = MainThreadAdCallback.wrap(adCallback);

        if (activity == null) {

            if (safeCallback != null) {
                safeCallback.onAdFailedToLoad(null);
            }

            return;
        }

        if (activity.isFinishing()) {
            if (safeCallback != null) {
                safeCallback.onAdFailedToLoad(null);
            }

            return;
        }

        if (activity.isDestroyed()) {
            if (safeCallback != null) {
                safeCallback.onAdFailedToLoad(null);
            }

            return;
        }

        if (adUnitId == null || adUnitId.trim().isEmpty()) {
            if (safeCallback != null) {
                safeCallback.onAdFailedToLoad(null);
            }

            return;
        }

        FrameLayout bannerAdContainer = activity.findViewById(R.id.bannerAdContainer);

        if (bannerAdContainer == null) {
            if (safeCallback != null) {
                safeCallback.onAdFailedToLoad(null);
            }

            return;
        }

        activity.runOnUiThread(() -> {

            try {

                if (splashBannerAdView != null) {

                    try {
                        splashBannerAdView.destroy();
                    } catch (Exception e) {
                        Log.e(TAG, "Error destroying old banner", e);
                    }

                    splashBannerAdView = null;
                }

                bannerAdContainer.removeAllViews();

                splashBannerAdView = new AdView(activity);

                DisplayMetrics metrics = activity.getResources().getDisplayMetrics();

                int widthDp = (int) (metrics.widthPixels / metrics.density);

                AdSize adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, widthDp);

                BannerAdRequest request = new BannerAdRequest.Builder(adUnitId, adSize).build();

                splashBannerAdView.loadAd(request, new AdLoadCallback<BannerAd>() {

                    @Override
                    public void onAdLoaded(@NonNull BannerAd bannerAd) {

                        bannerAd.setAdEventCallback(new BannerAdEventCallback() {

                            @Override
                            public void onAdPaid(@NonNull AdValue adValue) {
//                                FirebaseEventUtil.logPaidAdImpression(activity, adValue, AdType.BANNER);
                            }

                            @Override
                            public void onAdClicked() {
                                if (safeCallback != null) {
                                    safeCallback.onAdClicked();
                                }
                            }

                            @Override
                            public void onAdImpression() {
                                if (safeCallback != null) {
                                    safeCallback.onAdImpression();
                                }
                            }
                        });
                        activity.runOnUiThread(() -> {

                            try {

                                if (activity.isFinishing()) {
                                    return;
                                }

                                if (activity.isDestroyed()) {
                                    return;
                                }

                                bannerAdContainer.removeAllViews();

                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);

                                params.gravity = Gravity.CENTER;

                                bannerAdContainer.addView(splashBannerAdView, params);

                                bannerAdContainer.setVisibility(View.VISIBLE);

                                if (safeCallback != null) {
                                    safeCallback.onAdLoaded();
                                }

                            } catch (Exception e) {
                                bannerAdContainer.removeAllViews();

                                bannerAdContainer.setVisibility(View.GONE);

                                if (safeCallback != null) {
                                    safeCallback.onAdFailedToLoad(null);
                                }
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError error) {
                        activity.runOnUiThread(() -> {

                            bannerAdContainer.removeAllViews();

                            bannerAdContainer.setVisibility(View.GONE);
                        });

                        if (safeCallback != null) {

                            safeCallback.onAdFailedToLoad(error);
                        }
                    }
                });
            } catch (Exception e) {
                bannerAdContainer.removeAllViews();

                bannerAdContainer.setVisibility(View.GONE);

                if (safeCallback != null) {
                    safeCallback.onAdFailedToLoad(null);
                }
            }
        });
    }


}