package com.example.loanemi.Activities.utils;

public class Constant {
    public static final String EN = "en";
    public static String TEST_DEVICE_HASHED_ID;
    public static boolean isGoOutSide = false;
    public static boolean isStartFlowInterShowing = false;
    public static boolean isLanguageChanging = false;

    public static void markLeavingForAd() {
        isGoOutSide = true;
    }
}
