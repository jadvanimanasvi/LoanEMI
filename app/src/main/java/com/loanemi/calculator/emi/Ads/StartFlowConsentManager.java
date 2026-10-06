package com.loanemi.calculator.emi.Ads;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import com.facebook.shimmer.BuildConfig;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import com.loanemi.calculator.emi.utils.Constant;

import java.util.concurrent.atomic.AtomicBoolean;

public class StartFlowConsentManager {

    public interface ConsentGateCallback {
        void onConsentResolved(boolean adsAllowed);
    }

    private final Activity activity;
    private ConsentInformation consentInformation;
    private ConsentGateCallback currentCallback;
    private final AtomicBoolean finished = new AtomicBoolean(false);
    public boolean isConsentFormShowing = false;

    public StartFlowConsentManager(Activity activity) {
        this.activity = activity;
    }

    public static boolean isAdsAllowed(Context context) {
        try {
            String purposes = context.getSharedPreferences(context.getPackageName() + "_preferences", 0)
                    .getString("IABTCF_PurposeConsents", "");
            if (purposes != null && !purposes.isEmpty()) {
                return purposes.charAt(0) == '1';
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    public void requestConsent(ConsentGateCallback callback) {
        this.currentCallback = callback;
        finished.set(false);
        try {
            ConsentRequestParameters.Builder builder = new ConsentRequestParameters.Builder()
                    .setTagForUnderAgeOfConsent(false);
            String testDeviceId = Constant.TEST_DEVICE_HASHED_ID != null ? Constant.TEST_DEVICE_HASHED_ID : "";
            if (BuildConfig.DEBUG && !testDeviceId.isEmpty()) {
                ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                        .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                        .addTestDeviceHashedId(testDeviceId)
                        .build();
                builder.setConsentDebugSettings(debugSettings);
            }
            ConsentRequestParameters params = builder.build();
            consentInformation = UserMessagingPlatform.getConsentInformation(activity);
            int existing = consentInformation.getConsentStatus();
            if (existing == ConsentInformation.ConsentStatus.OBTAINED
                    || existing == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
                finishConsent(isAdsAllowed(activity));
                consentInformation.requestConsentInfoUpdate(activity, params, () -> {
                }, formError -> {
                });
                return;
            }
            consentInformation.requestConsentInfoUpdate(activity, params,
                    this::onConsentInfoReady,
                    formError -> finishConsent(isAdsAllowed(activity)));
        } catch (Exception e) {
            finishConsent(isAdsAllowed(activity));
        }
    }

    private void onConsentInfoReady() {
        if (finished.get()) {
            return;
        }
        int status = consentInformation.getConsentStatus();
        if (status == ConsentInformation.ConsentStatus.OBTAINED
                || status == ConsentInformation.ConsentStatus.NOT_REQUIRED) {
            finishConsent(isAdsAllowed(activity));
            return;
        }
        if (status == ConsentInformation.ConsentStatus.REQUIRED) {
            showConsentForm();
        } else {
            finishConsent(isAdsAllowed(activity));
        }
    }

    private void showConsentForm() {
        if (isConsentFormShowing) {
            return;
        }
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            finishConsent(isAdsAllowed(activity));
            return;
        }
        isConsentFormShowing = true;
        UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, formError -> {
            isConsentFormShowing = false;
            finishConsent(isAdsAllowed(activity));
        });
    }

    private void finishConsent(boolean adsAllowed) {
        if (currentCallback != null && finished.compareAndSet(false, true)) {
            currentCallback.onConsentResolved(adsAllowed);
        }
    }

    public void clearCachedConsent() {
        try {
            SharedPreferences prefs = activity.getSharedPreferences(
                    activity.getPackageName() + "_preferences", Context.MODE_PRIVATE);
            prefs.edit()
                    .remove("IABTCF_PurposeConsents")
                    .remove("IABTCF_TCString")
                    .remove("IABTCF_gdprApplies")
                    .apply();
        } catch (Exception ignored) {
        }
    }
}
