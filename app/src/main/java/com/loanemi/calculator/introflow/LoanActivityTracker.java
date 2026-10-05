package com.loanemi.calculator.introflow;

import android.app.Activity;

import androidx.fragment.app.Fragment;

public class LoanActivityTracker {
    private static Activity activity = null;
    private static Fragment fragment = null;
    private static String string = "";

    public static Activity getCurrentActivity() {
        return activity;
    }

    public static void setCurrentActivity(Activity activity2) {
        activity = activity2;
    }

    public static Fragment getCurrentFragment() {
        return fragment;
    }

    public static void setCurrentFragment(Fragment fragment2) {
        fragment = fragment2;
    }

    public static void setCurrentString(String str) {
        string = str;
    }
}
