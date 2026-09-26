package com.example.loanemi.Activities.introflow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Lifecycle;

import com.example.loanemi.Activities.Ads.AdCallback;
import com.example.loanemi.Activities.Ads.AdConfig;
import com.example.loanemi.Activities.Ads.ApNativeAd;
import com.example.loanemi.Activities.Ads.AppStatus;
import com.example.loanemi.Activities.dialog.PrepareLoadingAdsDialog;
import com.example.loanemi.Activities.language.LanguageActivity;
import com.example.loanemi.Activities.remote.LoanIntroConfig;
import com.example.loanemi.Activities.remote.RemoteConfigManager;
import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.Constant;
import com.example.loanemi.Activities.utils.MyApplication;
import com.example.loanemi.R;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd;
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback;
import java.lang.ref.WeakReference;

public final class LoanStartFlowAdHelper {

    private static final String TAG = "StartFlowAdHelper";
    private static final long INTER_SHOW_LOADING_DELAY_MS = 800L;
    private static InterstitialAd pendingInterstitial;
    private static PrepareLoadingAdsDialog loadingDialog;
    private static WeakReference<View> loadingOverlay;
    private static boolean isLoadingInter;
    private static boolean interShowStarted;
    private static Runnable pendingInterShowRunnable;
    private static int interLoadToken;
    private static WeakReference<AppCompatActivity> deferredInterActivity;
    private static AdCallback deferredInterCallback;
    private static boolean languageDoneScreenBound;
    private static boolean nativeCloseTimerStarted;
    private static boolean nativeCloseShown;
    private static boolean interSettled;
    private static boolean nativeAdShown;
    private static boolean skipCountStarted;
    private static boolean languageDoneInterDisplayed;
    private static CountDownTimer skipCountDown;
    private static WeakReference<View> languageDoneOverlay;
    private static WeakReference<AppCompatActivity> languageDoneActivity;
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static void runOnMainThread(Runnable action) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            action.run();
        } else {
            mainHandler.post(action);
        }
    }

    private static final long INTER_SETTLE_TIMEOUT_MS = 12000L;
    private static final Runnable interSettleTimeout = LoanStartFlowAdHelper::notifyLanguageDoneInterSettled;
    private static final Runnable showCloseAfterSkip = () -> {
        View overlay = languageDoneOverlay != null ? languageDoneOverlay.get() : null;
        AppCompatActivity activity = languageDoneActivity != null ? languageDoneActivity.get() : null;
        if (overlay == null || activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }
        showNativeClose(overlay, isFlagOn(activity, AppPreference.is_language_done_new_native_ui_show, true));
    };

    private LoanStartFlowAdHelper() {
    }

    public static boolean isNetworkConnected(Context context) {
        try {
            return AppStatus.getInstance(context).isOnline(context);
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFlagOn(Context context, String key, boolean defaultValue) {
        try {
            AppPreference pref = AppPreference.getInstance(context);
            if (pref != null) {
                return pref.getBoolean(key, defaultValue);
            }
        } catch (Exception ignored) {
        }
        try {
            LoanIntroConfig intro = RemoteConfigManager.getInstance().requireIntroConfig();
            if (intro != null) {
                return intro.readToggle(key, defaultValue);
            }
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static int startFlowGap(Context context, String key, int defaultValue) {
        try {
            LoanIntroConfig intro = RemoteConfigManager.getInstance().requireIntroConfig();
            if (intro != null) {
                return intro.readGap(key, defaultValue);
            }
        } catch (Exception ignored) {
        }
        try {
            AppPreference pref = AppPreference.getInstance(context);
            if (pref != null) {
                return pref.getInt(key, defaultValue);
            }
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    public static void showSplashInterstitial(AppCompatActivity activity, String highId, String normalId, AdCallback adCallback) {
        AdConfig.getInstance().loadAndShowInter(activity, highId, normalId, 10000, false, 1000, adCallback);
    }

    public static void fetchStartFlowNative(Activity activity, String highId, String normalId, int layout, AdCallback adCallback) {
        AdConfig.getInstance().loadNativePriorityAlternate(activity, highId, normalId, layout, adCallback);
    }

    public static void fetchStartFlowNativePair(Activity activity, String highId, String normalId, int layout, AdCallback adCallback) {
        AdConfig.getInstance().loadNativeAd(activity, highId, normalId, layout, adCallback);
    }

    public static int NativeLayoutFull(Context context, String newUiFlagKey) {
        return isFlagOn(context, newUiFlagKey, true)
                ? R.layout.native_full_screen_view_new
                : R.layout.native_full_screen_view;
    }

    public static boolean isInterstitialLoaded() {
        return pendingInterstitial != null;
    }

    public static boolean isInterstitialLoading() {
        return isLoadingInter;
    }

    public static void deferInterstitialShow(@NonNull AppCompatActivity activity, AdCallback adCallback) {
        runOnMainThread(() -> deferInterstitialShowOnMain(activity, adCallback));
    }

    private static void deferInterstitialShowOnMain(@NonNull AppCompatActivity activity, AdCallback adCallback) {
        deferredInterActivity = new WeakReference<>(activity);
        deferredInterCallback = adCallback;
        if (activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
            showInterstitialLoading(activity);
            Constant.isStartFlowInterShowing = true;
        }
    }

    public static void discardPendingInterstitial() {
        runOnMainThread(() -> {
            pendingInterstitial = null;
            isLoadingInter = false;
            interLoadToken++;
            deferredInterActivity = null;
            deferredInterCallback = null;
            cancelPendingInterShow();
            dismissLoading();
        });
    }

    public static void markLanguageDoneAdsPending() {
        languageDoneScreenBound = false;
        nativeCloseTimerStarted = false;
        nativeCloseShown = false;
        interSettled = false;
        nativeAdShown = false;
        skipCountStarted = false;
        languageDoneInterDisplayed = false;
        languageDoneOverlay = null;
        languageDoneActivity = null;
        mainHandler.removeCallbacks(interSettleTimeout);
        cancelNativeTimers();
    }

    private static void cancelNativeTimers() {
        mainHandler.removeCallbacks(showCloseAfterSkip);
        if (skipCountDown != null) {
            skipCountDown.cancel();
            skipCountDown = null;
        }
        View overlay = languageDoneOverlay != null ? languageDoneOverlay.get() : null;
        if (overlay != null) {
            cancelSkipHide(findSkipCountdownView(overlay));
        }
    }

    public static void showSkipTimerText(TextView tvSkipIn, String text) {
        if (tvSkipIn == null) {
            return;
        }
        cancelSkipHide(tvSkipIn);
        tvSkipIn.setAlpha(1f);
        ViewGroup.LayoutParams lp = tvSkipIn.getLayoutParams();
        if (lp != null) {
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            tvSkipIn.setLayoutParams(lp);
        }
        tvSkipIn.setText(text);
        tvSkipIn.setVisibility(View.VISIBLE);
    }

    public static void hideSkipTimerAnimated(TextView tvSkipIn) {
        if (tvSkipIn == null || tvSkipIn.getVisibility() == View.GONE) {
            return;
        }
        cancelSkipHide(tvSkipIn);
        int startHeight = tvSkipIn.getHeight();
        if (startHeight <= 0) {
            tvSkipIn.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            startHeight = tvSkipIn.getMeasuredHeight();
        }
        if (startHeight <= 0) {
            tvSkipIn.setVisibility(View.GONE);
            return;
        }
        final int fromHeight = startHeight;
        ValueAnimator animator = ValueAnimator.ofInt(fromHeight, 0);
        animator.setDuration(220);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            int height = (int) animation.getAnimatedValue();
            ViewGroup.LayoutParams lp = tvSkipIn.getLayoutParams();
            if (lp != null) {
                lp.height = height;
                tvSkipIn.setLayoutParams(lp);
            }
            tvSkipIn.setAlpha(1f - animation.getAnimatedFraction());
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                tvSkipIn.setTag(R.id.tvSkipIn, null);
                tvSkipIn.setVisibility(View.GONE);
                tvSkipIn.setAlpha(1f);
                ViewGroup.LayoutParams lp = tvSkipIn.getLayoutParams();
                if (lp != null) {
                    lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    tvSkipIn.setLayoutParams(lp);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                tvSkipIn.setTag(R.id.tvSkipIn, null);
            }
        });
        tvSkipIn.setTag(R.id.tvSkipIn, animator);
        animator.start();
    }

    public static void cancelSkipHide(TextView tvSkipIn) {
        if (tvSkipIn == null) {
            return;
        }
        Object tag = tvSkipIn.getTag(R.id.tvSkipIn);
        if (tag instanceof ValueAnimator) {
            ((ValueAnimator) tag).cancel();
        }
        tvSkipIn.setTag(R.id.tvSkipIn, null);
    }

    public static void loadInterstitialOnly(@NonNull Context context, String highId, String normalId) {

        String TAG = "InterstitialOnly";

        Log.d(TAG, "High Ad Unit ID = " + highId);
        Log.d(TAG, "Normal Ad Unit ID = " + normalId);
        Log.d(TAG, "isLoadingInter = " + isLoadingInter);
        Log.d(TAG, "pendingInterstitial = " + (pendingInterstitial != null));

        if (isLoadingInter || pendingInterstitial != null) {
            Log.d(TAG, "LOAD SKIPPED -> Already loading OR interstitial already available");
            return;
        }

        if (!isNetworkConnected(context)) {
            Log.d(TAG, "LOAD SKIPPED -> No network connection");
            return;
        }

        if (highId == null || highId.isEmpty()) {
            Log.d(TAG, "LOAD SKIPPED -> High Ad Unit ID is null/empty");
            return;
        }

        isLoadingInter = true;

        Log.d(TAG, "Interstitial loading started");
        Log.d(TAG, "Waiting for Mobile Ads SDK to become ready...");

        MyApplication.runWhenMobileAdsReady(() -> {

            Log.d(TAG, "Mobile Ads SDK is READY");
            Log.d(TAG, "Calling loadInterstitialInternal()");

            loadInterstitialInternal(context, highId, normalId);
        });
    }


    private static void loadInterstitialInternal(
            @NonNull Context context,
            String highId,
            String normalId
    ) {

        Log.d(TAG, "High Ad Unit ID = " + highId);
        Log.d(TAG, "Normal Ad Unit ID = " + normalId);
        Log.d(TAG, "pendingInterstitial = " + (pendingInterstitial != null));

        if (pendingInterstitial != null) {
            Log.d(TAG, "LOAD INTERNAL SKIPPED -> Pending interstitial already exists");
            isLoadingInter = false;
            return;
        }

        if (!isNetworkConnected(context)) {
            Log.d(TAG, "LOAD FAILED -> No network connection");
            isLoadingInter = false;

            notifyDeferredInterFailed();
            notifyLanguageDoneInterSettled();
            return;
        }

        if (highId == null || highId.isEmpty()) {
            Log.d(TAG, "LOAD FAILED -> High Ad Unit ID is null/empty");
            isLoadingInter = false;

            notifyDeferredInterFailed();
            notifyLanguageDoneInterSettled();
            return;
        }

        isLoadingInter = true;

        final int token = ++interLoadToken;

        Log.d(TAG, "Interstitial load token = " + token);
        Log.d(TAG, "Loading HIGH priority interstitial...");
        Log.d(TAG, "HIGH Ad Unit ID = " + highId);

        AdConfig.getInstance().getInterstitialAds(
                context,
                highId,
                new AdCallback() {

                    @Override
                    public void onInterstitialLoad(InterstitialAd interstitialAd) {
                        super.onInterstitialLoad(interstitialAd);

                        Log.d(TAG, "========== HIGH INTERSTITIAL LOADED ==========");
                        Log.d(TAG, "Token = " + token);
                        Log.d(TAG, "InterstitialAd = " + (interstitialAd != null));

                        mainHandler.post(() -> {

                            Log.d(TAG, "Handling HIGH interstitial on main thread");
                            handleInterstitialLoaded(token, interstitialAd);
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);

                        Log.d(TAG, "========== HIGH INTERSTITIAL FAILED ==========");
                        Log.d(TAG, "Token = " + token);

                        if (loadAdError != null) {
                            Log.d(TAG, "Error Code = " + loadAdError.getCode());
                            Log.d(TAG, "Error Message = " + loadAdError.getMessage());
                        } else {
                            Log.d(TAG, "LoadAdError = null");
                        }

                        mainHandler.post(() -> {

                            Log.d(TAG, "Handling HIGH failure on main thread");

                            handleInterstitialLoadFailed(
                                    token,
                                    context,
                                    highId,
                                    normalId
                            );
                        });
                    }
                }
        );
    }


    private static void handleInterstitialLoaded(
            int token,
            InterstitialAd interstitialAd
    ) {

        Log.d(TAG, "========== HANDLE INTERSTITIAL LOADED ==========");
        Log.d(TAG, "Received Token = " + token);
        Log.d(TAG, "Current Token = " + interLoadToken);
        Log.d(TAG, "InterstitialAd = " + (interstitialAd != null));

        if (token != interLoadToken) {

            Log.d(TAG, "LOAD RESULT IGNORED -> Token mismatch");
            Log.d(TAG, "Received Token = " + token);
            Log.d(TAG, "Current Token = " + interLoadToken);

            return;
        }

        isLoadingInter = false;

        Log.d(TAG, "isLoadingInter = false");

        if (interstitialAd != null) {

            pendingInterstitial = interstitialAd;

            Log.d(TAG, "========== INTERSTITIAL READY ==========");
            Log.d(TAG, "pendingInterstitial = SET");
            Log.d(TAG, "Trying to show deferred interstitial...");

            tryShowDeferredInterstitial();

            Log.d(TAG, "Notifying Language Splash interstitial...");
            notifyLanguageSplashInterIfReady();

        } else {

            Log.d(TAG, "INTERSTITIAL LOAD RESULT = NULL");
            Log.d(TAG, "No pending interstitial available");

            notifyDeferredInterFailed();
            notifyLanguageDoneInterSettled();
        }
    }


    private static void handleInterstitialLoadFailed(
            int token,
            @NonNull Context context,
            String highId,
            String normalId
    ) {

        Log.d(TAG, "========== HANDLE HIGH INTERSTITIAL FAILURE ==========");
        Log.d(TAG, "Received Token = " + token);
        Log.d(TAG, "Current Token = " + interLoadToken);
        Log.d(TAG, "High Ad Unit ID = " + highId);
        Log.d(TAG, "Normal Ad Unit ID = " + normalId);

        if (token != interLoadToken) {

            Log.d(TAG, "HIGH FAILURE IGNORED -> Token mismatch");
            return;
        }

        if (normalId == null || normalId.isEmpty() || normalId.equals(highId)) {

            Log.d(TAG, "NORMAL FALLBACK NOT AVAILABLE");

            if (normalId == null || normalId.isEmpty()) {
                Log.d(TAG, "Reason -> Normal Ad Unit ID is null/empty");
            } else {
                Log.d(TAG, "Reason -> Normal Ad Unit ID is same as High Ad Unit ID");
            }

            isLoadingInter = false;

            Log.d(TAG, "isLoadingInter = false");
            Log.d(TAG, "Final Interstitial Status = FAILED");

            notifyDeferredInterFailed();
            notifyLanguageDoneInterSettled();

            return;
        }

        Log.d(TAG, "========== FALLBACK TO NORMAL INTERSTITIAL ==========");
        Log.d(TAG, "HIGH interstitial failed");
        Log.d(TAG, "Loading NORMAL Ad Unit ID = " + normalId);

        AdConfig.getInstance().getInterstitialAds(
                context,
                normalId,
                new AdCallback() {

                    @Override
                    public void onInterstitialLoad(InterstitialAd interstitialAd) {
                        super.onInterstitialLoad(interstitialAd);

                        Log.d(TAG, "========== NORMAL INTERSTITIAL LOADED ==========");
                        Log.d(TAG, "Token = " + token);
                        Log.d(TAG, "InterstitialAd = " + (interstitialAd != null));

                        mainHandler.post(() -> {

                            Log.d(TAG, "Handling NORMAL interstitial on main thread");

                            handleInterstitialLoaded(
                                    token,
                                    interstitialAd
                            );
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError2) {
                        super.onAdFailedToLoad(loadAdError2);

                        Log.d(TAG, "========== NORMAL INTERSTITIAL FAILED ==========");
                        Log.d(TAG, "Token = " + token);

                        if (loadAdError2 != null) {
                            Log.d(TAG, "Error Code = " + loadAdError2.getCode());
                            Log.d(TAG, "Error Message = " + loadAdError2.getMessage());
                        } else {
                            Log.d(TAG, "LoadAdError = null");
                        }

                        mainHandler.post(() -> {

                            if (token != interLoadToken) {

                                Log.d(TAG, "NORMAL FAILURE IGNORED -> Token mismatch");
                                Log.d(TAG, "Received Token = " + token);
                                Log.d(TAG, "Current Token = " + interLoadToken);

                                return;
                            }

                            isLoadingInter = false;

                            Log.d(TAG, "isLoadingInter = false");
                            Log.d(TAG, "========== ALL INTERSTITIAL LOADS FAILED ==========");

                            notifyDeferredInterFailed();
                            notifyLanguageDoneInterSettled();
                        });
                    }
                }
        );
    }

    private static void notifyLanguageSplashInterIfReady() {
        mainHandler.post(() -> {
            Activity activity = LoanActivityTracker.getCurrentActivity();
            if (activity instanceof LanguageActivity) {
                ((LanguageActivity) activity).maybeShowSplashInterstitial();
            }
        });
    }

    public static void showLoadedOrDefer(@NonNull AppCompatActivity activity, AdCallback adCallback) {
        if (pendingInterstitial != null) {
            showLoadedInterstitialWithLoading(activity, adCallback);
            return;
        }
        if (isLoadingInter) {
            deferredInterActivity = new WeakReference<>(activity);
            deferredInterCallback = adCallback;
            if (activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                showInterstitialLoading(activity);
                Constant.isStartFlowInterShowing = true;
            }
            return;
        }
        notifyLanguageDoneInterSettled();
    }

    public static void tryShowDeferredInterstitial() {
        runOnMainThread(LoanStartFlowAdHelper::tryShowDeferredInterstitialOnMain);
    }

    private static void tryShowDeferredInterstitialOnMain() {
        if (pendingInterstitial == null || deferredInterActivity == null) {
            return;
        }
        // Recover stuck flag from a previous failed show attempt
        if (interShowStarted && (loadingDialog == null || !loadingDialog.isShowing())) {
            interShowStarted = false;
        }
        if (interShowStarted) {
            return;
        }
        AppCompatActivity activity = deferredInterActivity.get();
        AdCallback callback = deferredInterCallback;
        deferredInterActivity = null;
        deferredInterCallback = null;
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            showLoadedInterstitialWithLoadingOnMain(activity, callback);
        } else {
            cancelPendingInterShow();
            dismissLoading();
            Constant.isStartFlowInterShowing = false;
            notifyLanguageDoneInterSettled();
        }
    }

    public static void showLoadedInterstitialWithLoading(@NonNull AppCompatActivity activity, AdCallback adCallback) {
        runOnMainThread(() -> showLoadedInterstitialWithLoadingOnMain(activity, adCallback));
    }

    private static void showLoadedInterstitialWithLoadingOnMain(@NonNull AppCompatActivity activity, AdCallback adCallback) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            notifyNext(adCallback);
            return;
        }
        if (pendingInterstitial == null) {
            notifyNext(adCallback);
            return;
        }
        if (interShowStarted) {
            return;
        }
        if (pendingInterShowRunnable != null) {
            mainHandler.removeCallbacks(pendingInterShowRunnable);
            pendingInterShowRunnable = null;
        }
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
            loadingDialog = new PrepareLoadingAdsDialog(activity);
            loadingDialog.setCancelable(false);
            loadingDialog.show();
            Constant.isStartFlowInterShowing = true;
        } catch (Exception e) {
            Log.e(TAG, "loading dialog failed", e);
        }
        interShowStarted = true;
        pendingInterShowRunnable = () -> {
            pendingInterShowRunnable = null;
            if (activity.isFinishing() || activity.isDestroyed()) {
                interShowStarted = false;
                Constant.isStartFlowInterShowing = false;
                dismissLoading();
                notifyNext(adCallback);
                return;
            }
            if (pendingInterstitial == null) {
                interShowStarted = false;
                Constant.isStartFlowInterShowing = false;
                dismissLoading();
                notifyNext(adCallback);
                return;
            }
            if (!activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.RESUMED)) {
                // Exact 120 FPS: dismiss, defer; onPostResume will show dialog 800ms again
                interShowStarted = false;
                Constant.isStartFlowInterShowing = false;
                dismissLoading();
                deferredInterActivity = new WeakReference<>(activity);
                deferredInterCallback = adCallback;
                return;
            }
            pendingInterstitial.setAdEventCallback(new InterstitialAdEventCallback() {
                @Override
                public void onAdShowedFullScreenContent() {
                    mainHandler.post(() -> {
                        if (languageDoneScreenBound) {
                            languageDoneInterDisplayed = true;
                            mainHandler.removeCallbacks(interSettleTimeout);
                        }
                    });
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    mainHandler.post(() -> {
                        interShowStarted = false;
                        Constant.isStartFlowInterShowing = false;
                        pendingInterstitial = null;
                        dismissLoading();
                        if (adCallback != null) {
                            adCallback.onAdClosed();
                            adCallback.onNextAction();
                        }
                    });
                }

                @Override
                public void onAdFailedToShowFullScreenContent(@NonNull FullScreenContentError fullScreenContentError) {
                    mainHandler.post(() -> {
                        interShowStarted = false;
                        Constant.isStartFlowInterShowing = false;
                        pendingInterstitial = null;
                        dismissLoading();
                        if (adCallback != null) {
                            adCallback.onAdFailedToShow(fullScreenContentError);
                            adCallback.onNextAction();
                        }
                    });
                }

                @Override
                public void onAdClicked() {
                    mainHandler.post(() -> {
                        Constant.markLeavingForAd();
                        LoanActivityTracker.setCurrentString("AdClick");
                        if (adCallback != null) {
                            adCallback.onAdClicked();
                        }
                    });
                }

                @Override
                public void onAdImpression() {
                }
            });
            try {
                pendingInterstitial.show(activity);
            } catch (Exception e) {
                Log.e(TAG, "interstitial show failed", e);
                interShowStarted = false;
                Constant.isStartFlowInterShowing = false;
                pendingInterstitial = null;
                dismissLoading();
                notifyNext(adCallback);
            }
        };
        mainHandler.postDelayed(pendingInterShowRunnable, INTER_SHOW_LOADING_DELAY_MS);
    }

    public static void handleLanguageDoneNextScreen(@NonNull AppCompatActivity activity) {
        if (languageDoneScreenBound) {
            tryShowDeferredInterstitial();
            return;
        }
        if (activity.getIntent() == null || !activity.getIntent().getBooleanExtra(AppPreference.EXTRA_AFTER_LANG_PICK, false)) {
            return;
        }
        languageDoneScreenBound = true;

        boolean online = isNetworkConnected(activity);
        boolean nativeOn = online && isFlagOn(activity, AppPreference.is_language_done_native_show, true);
        boolean interOn = online && isFlagOn(activity, AppPreference.is_language_done_inter_show, true);

        LangExitAdsIfMissing(activity);

        if (nativeOn) {
            attachFullNativeOverlay(activity);
            View overlay = languageDoneOverlay != null ? languageDoneOverlay.get() : null;
            if (interOn && overlay != null) {
                overlay.setVisibility(View.GONE);
            }
        }

        if (interOn) {
            mainHandler.removeCallbacks(interSettleTimeout);
            mainHandler.postDelayed(interSettleTimeout, INTER_SETTLE_TIMEOUT_MS);
            showLoadedOrDefer(activity, new AdCallback() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    LoanActivityTracker.setCurrentString("AdClose");
                }

                @Override
                public void onAdFailedToShow(FullScreenContentError adError) {
                    super.onAdFailedToShow(adError);
                }

                @Override
                public void onNextAction() {
                    super.onNextAction();
                    notifyLanguageDoneInterSettled();
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Constant.markLeavingForAd();
                    LoanActivityTracker.setCurrentString("AdClick");
                }
            });
        } else {
            discardPendingInterstitial();
            notifyLanguageDoneInterSettled();
        }
    }

    public static void LangExitAdsIfMissing(@NonNull AppCompatActivity activity) {
        boolean online = isNetworkConnected(activity);
        if (!online) {
            return;
        }
        if (isFlagOn(activity, AppPreference.is_language_done_inter_show, true)
                && pendingInterstitial == null && !isLoadingInter) {
            loadInterstitialOnly(
                    activity.getApplicationContext(),
                    activity.getString(R.string.done_inter_language_1),
                    activity.getString(R.string.done_inter_language_2)
            );
        }
        if (isFlagOn(activity, AppPreference.is_language_done_native_show, true)) {
            ApNativeAd already = MyApplication.instance.FullScreenLanguageNativeAd.getValue();
            if (already == null || !already.isReady()) {
                int layout = NativeLayoutFull(activity, AppPreference.is_language_done_new_native_ui_show);
                fetchStartFlowNative(
                        activity,
                        activity.getString(R.string.language_done_fullscreen_native_1),
                        activity.getString(R.string.language_done_fullscreen_native_2),
                        layout,
                        new AdCallback() {
                            @Override
                            public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                                super.onNativeAdLoaded(apNativeAd);
                                MyApplication.instance.FullScreenLanguageNativeAd.postValue(apNativeAd);
                            }

                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                                super.onAdFailedToLoad(loadAdError);
                                MyApplication.instance.FullScreenLanguageNativeAd.postValue(null);
                            }
                        }
                );
            }
        }
    }

    public static TextView findSkipCountdownView(View overlay) {
        if (overlay == null) {
            return null;
        }
        View adSlot = overlay.findViewById(R.id.flAdNative);
        if (adSlot != null) {
            TextView inCreative = adSlot.findViewById(R.id.tvSkipIn);
            if (inCreative != null) {
                return inCreative;
            }
        }
        return overlay.findViewById(R.id.tvSkipIn);
    }

    private static void attachFullNativeOverlay(@NonNull AppCompatActivity activity) {
        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) {
            return;
        }
        View existing = activity.findViewById(R.id.flAdNative1);
        if (existing != null) {
            languageDoneOverlay = new WeakReference<>(existing);
            languageDoneActivity = new WeakReference<>(activity);
            return;
        }
        View overlay = LayoutInflater.from(activity).inflate(R.layout.loan_start_flow_full_native_overlay, root, false);
        root.addView(overlay);
        applyOverlayStatusBarInset(overlay);
        languageDoneOverlay = new WeakReference<>(overlay);
        languageDoneActivity = new WeakReference<>(activity);
        boolean newUi = isFlagOn(activity, AppPreference.is_language_done_new_native_ui_show, true);
        FrameLayout flAdNative = overlay.findViewById(R.id.flAdNative);
        View skipBar = overlay.findViewById(R.id.llSkipBar);
        if (flAdNative != null) {
            int shimmerLayout = newUi ? R.layout.full_screen_native_shimmer_view_new : R.layout.full_screen_native_shimmer_view;
            View shimmerView = LayoutInflater.from(activity).inflate(shimmerLayout, flAdNative, false);
            flAdNative.addView(shimmerView);
        }
        if (skipBar != null) {
            skipBar.setVisibility(View.GONE);
        }
        bindOverlayClose(overlay);
        MyApplication.instance.FullScreenLanguageNativeAd.observe(activity, apNativeAd -> {
            runOnMainThread(() -> {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }
                if (apNativeAd != null && flAdNative != null) {
                    if (nativeAdShown) {
                        if (nativeCloseShown) {
                            showNativeClose(overlay, newUi);
                        } else if (interSettled && newUi) {
                            startSkipTimerAfterAdShown(activity, overlay);
                        }
                        return;
                    }

                    ShimmerFrameLayout shimmer = flAdNative.findViewById(R.id.shimmer_container_native);
                    AdConfig.getInstance().populateNativeAdView(activity, apNativeAd, flAdNative, shimmer);
                    nativeAdShown = true;
                    View adLabel = flAdNative.findViewById(R.id.tvAdLabel);
                    if (adLabel != null) {
                        adLabel.setVisibility(View.VISIBLE);
                    }
                    wireNextArrowAsCta(flAdNative);
                    bindOverlayClose(overlay);
                    if (nativeCloseShown) {
                        showNativeClose(overlay, newUi);
                    } else if (interSettled && newUi) {
                        startSkipTimerAfterAdShown(activity, overlay);
                    }
                }
            });
        });
    }

    private static void wireNextArrowAsCta(View root) {
        if (root == null) {
            return;
        }
        View arrow = root.findViewById(R.id.imgNextArrow);
        View cta = root.findViewById(R.id.ad_call_to_action);
        if (arrow != null && cta != null) {
            arrow.setVisibility(View.VISIBLE);
            arrow.setOnClickListener(v -> {
                Constant.markLeavingForAd();
                LoanActivityTracker.setCurrentString("AdClick");
                cta.performClick();
            });
        }
    }

    private static void notifyDeferredInterFailed() {
        AdCallback callback = deferredInterCallback;
        AppCompatActivity activity = deferredInterActivity != null ? deferredInterActivity.get() : null;
        deferredInterCallback = null;
        deferredInterActivity = null;
        cancelPendingInterShow();
        dismissLoading();
        Constant.isStartFlowInterShowing = false;
        if (callback == null) {
            return;
        }
        callback.onNextAction();
    }

    private static void notifyLanguageDoneInterSettled() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            mainHandler.post(LoanStartFlowAdHelper::notifyLanguageDoneInterSettled);
            return;
        }
        mainHandler.removeCallbacks(interSettleTimeout);
        if (!languageDoneScreenBound || interSettled) {
            return;
        }
        interSettled = true;
        if (!languageDoneInterDisplayed) {
            discardPendingInterstitial();
        }
        AppCompatActivity activity = languageDoneActivity != null ? languageDoneActivity.get() : null;
        View overlay = languageDoneOverlay != null ? languageDoneOverlay.get() : null;
        if (activity == null || overlay == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        overlay.setVisibility(View.VISIBLE);
        ViewCompat.requestApplyInsets(overlay);
        boolean newUi = isFlagOn(activity, AppPreference.is_language_done_new_native_ui_show, true);
        if (newUi && nativeAdShown) {
            startSkipTimerAfterAdShown(activity, overlay);
        } else {
            startShimmerOrOldCloseWait(activity, overlay, newUi);
        }
    }

    private static void bindOverlayClose(View overlay) {
        if (overlay == null) {
            return;
        }
        View.OnClickListener closeClick = v -> {
            cancelNativeTimers();
            overlay.setVisibility(View.GONE);
        };
        ImageView imgClose = overlay.findViewById(R.id.imgClose);
        if (imgClose != null) {
            imgClose.setOnClickListener(closeClick);
        }
        ImageView imgCloseNew = getOverlayCloseNew(overlay);
        if (imgCloseNew != null) {
            imgCloseNew.setOnClickListener(closeClick);
        }
    }

    private static void applyOverlayStatusBarInset(View overlay) {
        if (overlay == null) {
            return;
        }
        ViewCompat.setOnApplyWindowInsetsListener(overlay, (v, insets) -> {
            Insets status = insets.getInsets(WindowInsetsCompat.Type.statusBars());
            v.setPadding(v.getPaddingLeft(), status.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        ViewCompat.requestApplyInsets(overlay);
    }

    private static ImageView getOverlayCloseNew(View overlay) {
        if (!(overlay instanceof ViewGroup)) {
            return null;
        }
        ViewGroup group = (ViewGroup) overlay;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child.getId() == R.id.imgCloseNew && child instanceof ImageView) {
                return (ImageView) child;
            }
        }
        return null;
    }

    private static void startShimmerOrOldCloseWait(AppCompatActivity activity, View overlay, boolean newUi) {
        runOnMainThread(() -> startShimmerOrOldCloseWaitOnMain(activity, overlay, newUi));
    }

    private static void startShimmerOrOldCloseWaitOnMain(AppCompatActivity activity, View overlay, boolean newUi) {
        if (nativeCloseShown || skipCountStarted) {
            return;
        }
        nativeCloseTimerStarted = true;
        cancelNativeTimers();
        long remaining = getCloseTimerSeconds(activity, AppPreference.full_native_close_timer, 1) * 1000L;
        if (remaining <= 0) {
            showNativeClose(overlay, newUi);
            return;
        }
        bindOverlayClose(overlay);
        skipCountDown = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (activity.isFinishing() || activity.isDestroyed() || overlay.getVisibility() != View.VISIBLE) {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                if (activity.isFinishing() || activity.isDestroyed() || overlay.getVisibility() != View.VISIBLE) {
                    return;
                }
                if (newUi && nativeAdShown) {
                    startSkipTimerAfterAdShown(activity, overlay);
                    return;
                }
                showNativeClose(overlay, newUi);
            }
        };
        skipCountDown.start();
    }

    private static void startSkipTimerAfterAdShown(AppCompatActivity activity, View overlay) {
        runOnMainThread(() -> startSkipTimerAfterAdShownOnMain(activity, overlay));
    }

    private static void startSkipTimerAfterAdShownOnMain(AppCompatActivity activity, View overlay) {
        if (!interSettled || nativeCloseShown || skipCountStarted) {
            return;
        }
        skipCountStarted = true;
        nativeCloseTimerStarted = true;
        cancelNativeTimers();
        bindOverlayClose(overlay);
        int skipSeconds = getCloseTimerSeconds(activity, AppPreference.full_native_skip_timer, 4);
        if (skipSeconds <= 0) {
            mainHandler.postDelayed(showCloseAfterSkip, 1000);
            return;
        }
        updateSkipText(overlay, skipSeconds);
        skipCountDown = new CountDownTimer(skipSeconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (activity.isFinishing() || activity.isDestroyed() || overlay.getVisibility() != View.VISIBLE) {
                    cancel();
                    return;
                }
                updateSkipText(overlay, (int) Math.ceil(millisUntilFinished / 1000.0));
            }

            @Override
            public void onFinish() {
                if (activity.isFinishing() || activity.isDestroyed() || overlay.getVisibility() != View.VISIBLE) {
                    return;
                }
                updateSkipText(overlay, 0);
                mainHandler.postDelayed(showCloseAfterSkip, 1000);
            }
        };
        skipCountDown.start();
    }

    @SuppressLint("StringFormatInvalid")
    private static void updateSkipText(View overlay, int seconds) {
        if (overlay == null) {
            return;
        }
        TextView tvSkipIn = findSkipCountdownView(overlay);
        if (tvSkipIn == null) {
            return;
        }
        if (seconds > 0) {
            showSkipTimerText(tvSkipIn, MyApplication.getApplication().getString(R.string.skip_in, seconds));
        } else {
            hideSkipTimerAnimated(tvSkipIn);
        }
    }

    private static void showNativeClose(View overlay, boolean newUi) {
        if (overlay == null) {
            return;
        }
        nativeCloseShown = true;
        TextView tvSkipIn = findSkipCountdownView(overlay);
        ImageView imgCloseOld = overlay.findViewById(R.id.imgClose);
        ImageView imgCloseNew = getOverlayCloseNew(overlay);
        ImageView imgNextArrow = overlay.findViewById(R.id.imgNextArrow);
        if (tvSkipIn != null) {
            hideSkipTimerAnimated(tvSkipIn);
        }
        if (newUi) {
            if (imgNextArrow != null) {
                imgNextArrow.setVisibility(View.VISIBLE);
            }
            if (imgCloseNew != null) {
                imgCloseNew.setVisibility(View.VISIBLE);
            }
        } else if (imgCloseOld != null) {
            imgCloseOld.setVisibility(View.VISIBLE);
        }
        bindOverlayClose(overlay);
        FrameLayout flAdNative = overlay.findViewById(R.id.flAdNative);
        wireNextArrowAsCta(flAdNative != null ? flAdNative : overlay);
    }

    private static void showInterstitialLoading(@NonNull AppCompatActivity activity) {
        dismissLoading();
        ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) {
            return;
        }
        View overlay = LayoutInflater.from(activity).inflate(R.layout.loading_ads_view, root, false);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setClickable(true);
        overlay.setFocusable(true);
        overlay.setElevation(Float.MAX_VALUE);
        root.addView(overlay);
        loadingOverlay = new WeakReference<>(overlay);
    }

    private static void cancelPendingInterShow() {
        if (pendingInterShowRunnable != null) {
            mainHandler.removeCallbacks(pendingInterShowRunnable);
            pendingInterShowRunnable = null;
        }
        interShowStarted = false;
    }

    private static void dismissLoading() {
        try {
            if (loadingDialog != null && loadingDialog.isShowing()) {
                loadingDialog.dismiss();
            }
        } catch (Exception ignored) {
        }
        loadingDialog = null;
        View overlay = loadingOverlay != null ? loadingOverlay.get() : null;
        if (overlay != null && overlay.getParent() instanceof ViewGroup) {
            ((ViewGroup) overlay.getParent()).removeView(overlay);
        }
        loadingOverlay = null;
    }

    private static void notifyNext(AdCallback adCallback) {
        if (adCallback != null) {
            adCallback.onNextAction();
        }
    }

    public static int getCloseTimerSeconds(Context context, String key, int defaultSeconds) {
        int value = startFlowGap(context, key, defaultSeconds);
        if (value < 0) {
            return 0;
        }
        if (value > 10) {
            return 10;
        }
        return value;
    }
}
