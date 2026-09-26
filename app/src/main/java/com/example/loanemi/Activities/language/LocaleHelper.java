package com.example.loanemi.Activities.language;

import android.content.Context;
import android.content.res.Configuration;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.Constant;

import java.util.Locale;


public class LocaleHelper {

    public static Context setLocale(Context context) {
        String lang = AppPreference.getInstance(context).getString(AppPreference.SELECTED_LANGUAGE, Constant.EN);

        return wrap(context, lang);
    }

    @NonNull
    public static String normalizeLanguageTag(@Nullable String langCode) {
        if (langCode == null || langCode.isEmpty()) {
            return Constant.EN;
        }
        String normalized = langCode.trim().replace('_', '-');
        if (normalized.contains("-r")) {
            normalized = normalized.replace("-r", "-");
        }
        return normalized;
    }

    public static Context wrap(Context context, String langCode) {
        langCode = normalizeLanguageTag(langCode);

        Locale locale = Locale.forLanguageTag(langCode);

        Locale.setDefault(locale);

        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        return context.createConfigurationContext(config);
    }
}
