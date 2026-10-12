package com.loanemi.calculator.emi.language;

import android.content.Context;
import android.content.res.Configuration;
import android.os.LocaleList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.loanemi.calculator.emi.utils.AppPreference;
import com.loanemi.calculator.emi.utils.Constant;

import java.util.Locale;

public class LocaleHelper {

    // Used in attachBaseContext of every Activity
    public static Context setLocale(Context context) {
        return wrap(context, getLanguage(context));
    }

    // ONE place to read the selected language
    @NonNull
    public static String getLanguage(Context context) {
        AppPreference pref = AppPreference.getInstance(context);
        String lang = pref.getPreferencesStr(AppPreference.SELECT_LAN, "");
        if (lang == null || lang.isEmpty()) {
            lang = pref.getString(AppPreference.SELECTED_LANGUAGE, "");
        }
        return (lang == null || lang.isEmpty()) ? Constant.EN : lang;
    }

    // ONE place to save the selected language (keeps both keys in sync)
    public static void saveLanguage(Context context, String langCode) {
        if (context == null || langCode == null || langCode.isEmpty()) return;
        AppPreference pref = AppPreference.getInstance(context);
        pref.setPreferencesStrCommit(AppPreference.SELECT_LAN, langCode);
        pref.setString(AppPreference.SELECTED_LANGUAGE, langCode);
        Locale.setDefault(toLocale(langCode));
    }

    @NonNull
    public static String normalizeLanguageTag(@Nullable String langCode) {
        if (langCode == null || langCode.isEmpty()) {
            return Constant.EN;
        }
        String normalized = langCode.trim().replace('_', '-');
        if (normalized.contains("-r")) {
            normalized = normalized.replace("-r", "-");   // en-rGB -> en-GB
        }
        return normalized;
    }

    @NonNull
    public static Locale toLocale(@Nullable String langCode) {
        return Locale.forLanguageTag(normalizeLanguageTag(langCode));
    }

    public static Context wrap(Context context, String langCode) {
        Locale locale = toLocale(langCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLocales(new LocaleList(locale));
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }

    // NEW: localized context for preview only (does NOT change global locale)
    public static Context createLocalizedContext(Context context, String langCode) {
        Locale locale = toLocale(langCode);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLocales(new LocaleList(locale));
        config.setLayoutDirection(locale);
        return context.createConfigurationContext(config);
    }
}