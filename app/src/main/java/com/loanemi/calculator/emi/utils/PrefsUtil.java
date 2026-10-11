package com.loanemi.calculator.emi.utils;

import android.content.Context;
import android.content.SharedPreferences;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public class PrefsUtil {
    private final SharedPreferences prefs;
    public static PrefsUtil pref;
    private static final String NAME = "loan_emi_prefs";
    public static final Companion Companion = new Companion((DefaultConstructorMarker) null);
    public static final String CAT_ARRAYLIST = "cat_arraylist";
    public static String is_splash_banner_show = "is_splash_banner_show";
    public static String is_splash_inter_show = "is_splash_inter_show";
    public static String is_language_native1_show = "is_language_native1_show";
    public static String is_language_native2_show = "is_language_native2_show";
    public static String is_language_done_native_show = "is_language_done_native_show";
    public static String is_language_done_inter_show = "is_language_done_inter_show";
    public static String is_first_intro_native1_show = "is_first_intro_native1_show";
    public static String is_first_intro_native2_show = "is_first_intro_native2_show";
    public static String is_intro_full_screen_native_1_show = "is_intro_full_screen_native_1_show";
    public static String is_intro_full_screen_native_2_show = "is_intro_full_screen_native_2_show";
    public static String is_intro_full_screen_native_2_new_ui_show = "is_intro_full_screen_native_2_new_ui_show";
    public static String is_get_started_native_show = "is_get_started_native_show";
    public static String get_started_bottom_space = "get_started_bottom_space";
    public static String show_get_started_btn = "show_get_started_btn";
    public static String full_native_skip_timer = "full_native_skip_timer";
    public static String full_native_close_timer = "full_native_close_timer";
    public static String is_language_done_new_native_ui_show = "is_language_done_new_native_ui_show";
    public static String is_intro_full_screen_native_1_new_ui_show = "is_intro_full_screen_native_1_new_ui_show";
    public static String is_first_intro_new_next_btn_show = "is_first_intro_new_next_btn_show";
    public static String btnNext_bottom_space = "btnNext_bottom_space";
    public static final String KEY_LOAN_INTRO_CONFIG = "key_loan_intro_config";



    public PrefsUtil(Context context) {
        prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static PrefsUtil getInstance(Context context) {
        return Companion.getInstance(context);
    }

    public int getInt(String str, int i) {
        return this.prefs.getInt(str, i);
    }

    public void setInt(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return prefs.getBoolean(key, defaultValue);
    }

    public void setBoolean(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public static final class Companion {
        public Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public PrefsUtil getPref() {
            return PrefsUtil.pref;
        }

        public void setPref(PrefsUtil zWEPreferences) {
            PrefsUtil.pref = zWEPreferences;
        }

        public String getCAT_ARRAYLIST() {
            return PrefsUtil.CAT_ARRAYLIST;
        }

        @JvmStatic
        public PrefsUtil getInstance(Context context) {
            Intrinsics.checkNotNullParameter(context, "context");
            if (getPref() == null) {
                setPref(new PrefsUtil(context.getApplicationContext()));
            }
            return getPref();
        }
    }

    public static String getLoanIntroConfig(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_LOAN_INTRO_CONFIG, null);
    }

    public static void saveIntroConfig(Context context, String json) {
        SharedPreferences prefs = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LOAN_INTRO_CONFIG, json);
        editor.apply();
    }
}
