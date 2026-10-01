package com.example.loanemi.Activities.utils;

import android.content.Context;
import android.content.SharedPreferences;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public class AppPreference {
    public static final String SELECTED_LANGUAGE = "selected_language";
    public static final String SELECT_LAN = "select_lan";
    private static final String PREF_NAME = "camera_pref";
    public static final String KEY_USER_RATING = "user_rating";
    public static final String KEY_USER_RATE = "user_rate";
    public static final String KEY_THEME = "key_theme";
    public static final String DEFAULT = "default";
    public static String is_splash_inter_show = "is_splash_inter_show";
    public static String is_splash_banner_show = "is_splash_banner_show";
    public static String is_language_native1_show = "is_language_native1_show";
    public static String is_language_native2_show = "is_language_native2_show";
    public static String is_language_done_new_native_ui_show = "is_language_done_new_native_ui_show";
    public static String is_language_done_native_show = "is_language_done_native_show";
    public static String is_language_done_inter_show = "is_language_done_inter_show";
    public static String is_first_intro_native_show = "is_first_intro_native_show";
    public static String is_intro_full_screen_native_1_show = "is_intro_full_screen_native_1_show";
    public static String is_intro_full_screen_native_1_new_ui_show = "is_intro_full_screen_native_1_new_ui_show";
    public static String is_intro_full_screen_native_2_show = "is_intro_full_screen_native_2_show";
    public static String is_intro_full_screen_native_2_new_ui_show = "is_intro_full_screen_native_2_new_ui_show";
    public static String is_get_started_native_show = "is_get_started_native_show";
    public static String is_first_intro_new_next_btn_show = "is_first_intro_new_next_btn_show";
    public static String show_get_started_btn = "show_get_started_btn";
    public static String btnNext_bottom_space = "btnNext_bottom_space";
    public static String get_started_bottom_space = "get_started_bottom_space";
    public static String full_native_close_timer = "full_native_close_timer";
    public static String full_native_skip_timer = "full_native_skip_timer";
    public static final String KEY_IS_USER = "IS_USER";
    public static final String EXTRA_AFTER_LANG_PICK = "fromLanguageDone";
    public static final String EXTRA_OPEN_SPLASH_INTER = "openSplashInter";
    public static final String EXTRA_LANG_OPENED_IN_SETTINGS = "langPickedFromMenu";
    private static final String KEY_LAST_IMPRESSION_INTERSTITIAL_TIME = "KEY_LAST_IMPRESSION_INTERSTITIAL_TIME";
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final String CAT_ARRAYLIST = "cat_arraylist";

    private static AppPreference instance;
    private final SharedPreferences mPref;

    public AppPreference(Context context) {
        mPref = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }


    public static synchronized AppPreference getInstance(Context context) {
        if (instance == null) {
            instance = new AppPreference(context);
        }
        return instance;
    }

    public String getString(String key, String defaultValue) {
        return mPref.getString(key, defaultValue);
    }

    public void setString(String key, String value) {
        mPref.edit().putString(key, value).apply();
    }

    public void setBoolean(String key, boolean value) {
        mPref.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mPref.getBoolean(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return mPref.getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        mPref.edit().putInt(key, value).apply();
    }

    public static void setLastImpressionInterstitialTime(Context context) {
        context.getSharedPreferences(PREF_NAME, 0).edit().putLong(KEY_LAST_IMPRESSION_INTERSTITIAL_TIME, System.currentTimeMillis()).apply();
    }

    public final void setPreferencesStrCommit(String str, String str2) {
        this.mPref.edit().putString(str, str2).commit();
    }

    public final String getPreferencesStr(String str, String str2) {
        return this.mPref.getString(str, str2);
    }

    public static final class Companion {
        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public AppPreference getPref() {
            return AppPreference.instance;
        }

        public void setPref(AppPreference zWEPreferences) {
            AppPreference.instance = zWEPreferences;
        }

        public String getCAT_ARRAYLIST() {
            return AppPreference.CAT_ARRAYLIST;
        }

        @JvmStatic
        public AppPreference getInstance(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            if (getPref() == null) {
                setPref(new AppPreference(context.getApplicationContext()));
            }
            return getPref();
        }
    }
}
