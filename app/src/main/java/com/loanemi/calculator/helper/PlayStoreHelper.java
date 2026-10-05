package com.loanemi.calculator.helper;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

public final class PlayStoreHelper {

    private PlayStoreHelper() {
    }

    public static void openAppPage(@NonNull Context context) {
        String packageName = context.getPackageName();
        try {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=" + packageName)
            ));
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)
            ));
        }
    }
}
