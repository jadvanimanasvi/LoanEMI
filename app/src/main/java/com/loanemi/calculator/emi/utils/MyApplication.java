package com.loanemi.calculator.emi.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.loanemi.calculator.emi.R;
import com.facebook.shimmer.BuildConfig;
import com.google.android.libraries.ads.mobile.sdk.MobileAds;
import com.google.android.libraries.ads.mobile.sdk.common.RequestConfiguration;
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig;
import com.loanemi.calculator.emi.Ads.ApNativeAd;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.remote.RemoteConfigManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MyApplication extends Application {
    public static MyApplication context;
    public static MyApplication instance;
    private static volatile boolean mobileAdsReady;
    private static final List<Runnable> mobileAdsReadyCallbacks = new ArrayList<>();
    public MutableLiveData<ApNativeAd> LanguageFirstNativeAd = new MutableLiveData<>();
    public MutableLiveData<ApNativeAd> FullScreenLanguageNativeAd = new MutableLiveData<>();
    public MutableLiveData<ApNativeAd> LanguageSecondNativeAd = new MutableLiveData<>();
    public MutableLiveData<ApNativeAd> FirstIntroNativeAd = new MutableLiveData<>();
    public MutableLiveData<ApNativeAd> FullScreenFirstNativeAd = new MutableLiveData<>();
    public MutableLiveData<ApNativeAd> FullScreenSecondNativeAd = new MutableLiveData<>();
    public MutableLiveData<ApNativeAd> GetStartedNativeAd = new MutableLiveData<>();

    public static MyApplication getApplication() {
        return context;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        instance = this;

        Constant.TEST_DEVICE_HASHED_ID = getTestDeviceId(context);

        initializeMobileAdsSdk();

        FirebaseApp.initializeApp(MyApplication.this);
        FirebaseAnalytics.getInstance(this);

        /*try {
            RemoteConfigManager.getInstance().init();
            RemoteConfigManager.getInstance().loadCachedConfig(getApplicationContext());
            if (Util.isInternetAvailable(this)) {
                RemoteConfigManager.getInstance().fetchAppConfig(() -> {
                    // intro flags live on IntroConfig / PrefsUtil
                });
            }
        } catch (Exception e) {
            //Exception
        }*/

        try {
            Log.d("RC_DEBUG", "App onCreate: init remote config");
            RemoteConfigManager.getInstance().init();
            RemoteConfigManager.getInstance().loadCachedConfig(getApplicationContext());

            boolean online = Util.isInternetAvailable(this);
            Log.d("RC_DEBUG", "App onCreate: online=" + online);
            if (online) {
                RemoteConfigManager.getInstance().fetchAppConfig(() ->
                        Log.d("RC_DEBUG", "App onCreate: fetch callback done"));
            }
        } catch (Exception e) {
            Log.e("RC_DEBUG", "App onCreate: remote config crashed", e);
        }
    }

    public static void setLanguage(Context context, String languageCode) {
        if (context == null || languageCode == null || languageCode.isEmpty()) {
            return;
        }
        Locale locale = toLocale(languageCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = new Configuration(resources.getConfiguration());
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    private static Locale toLocale(String languageCode) {
        if ("id".equalsIgnoreCase(languageCode)) {
            languageCode = "in";
        }
        if (languageCode.contains("-")) {
            String[] parts = languageCode.split("-", 2);
            return new Locale(parts[0], parts[1]);
        }
        return new Locale(languageCode);
    }

    public String getTestDeviceId(Context context) {
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return md5(androidId).toUpperCase(Locale.US);
    }

    public static String getDeviceLocale() {
        return Locale.getDefault().getLanguage();
    }

    private String md5(String s) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & b));
                while (h.length() < 2) h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Exception
        }
        return "";
    }

    private void initializeMobileAdsSdk() {
        new Thread(() -> {
            MobileAds.initialize(this, new InitializationConfig.Builder(getString(R.string.admob_app_id)).build(), initializationStatus -> {
                mobileAdsReady = true;
                List<Runnable> pending;
                synchronized (mobileAdsReadyCallbacks) {
                    pending = new ArrayList<>(mobileAdsReadyCallbacks);
                    mobileAdsReadyCallbacks.clear();
                }
                for (Runnable callback : pending) {
                    callback.run();
                }
            });

            if (BuildConfig.DEBUG) {
                MobileAds.setRequestConfiguration(new RequestConfiguration.Builder().setTestDeviceIds(List.of(Constant.TEST_DEVICE_HASHED_ID)).build());
            }
        }).start();
    }

    public void applyStartFlowSystemBars(Activity activity, View view) {
        if (activity == null) {
            return;
        }
        View target = view != null ? view : activity.getWindow().getDecorView();
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        int barColor = ContextCompat.getColor(activity, R.color.background_color);
        if (target.getBackground() instanceof ColorDrawable) {
            barColor = ((ColorDrawable) target.getBackground()).getColor();
        }
        activity.getWindow().setStatusBarColor(barColor);
        activity.getWindow().setNavigationBarColor(barColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            activity.getWindow().setStatusBarContrastEnforced(false);
            activity.getWindow().setNavigationBarContrastEnforced(false);
        }
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(activity.getWindow(), target);
        controller.show(WindowInsetsCompat.Type.statusBars());
        controller.hide(WindowInsetsCompat.Type.navigationBars());
        boolean night = (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        controller.setAppearanceLightStatusBars(!night);
        controller.setSystemBarsBehavior(WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
    }

    public static void runWhenMobileAdsReady(@NonNull Runnable action) {
        if (mobileAdsReady) {
            action.run();
            return;
        }
        synchronized (mobileAdsReadyCallbacks) {

            if (mobileAdsReady) {
                action.run();
                return;
            }
            mobileAdsReadyCallbacks.add(action);
        }
    }

    public static void applyTheme() {
        String theme = AppPreference.getInstance(context).getString(AppPreference.KEY_THEME, AppPreference.DEFAULT);
        switch (theme) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                );
                break;

            case "light":
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                );
                break;

            default: // system default
                AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                );
                break;
        }
    }

}
