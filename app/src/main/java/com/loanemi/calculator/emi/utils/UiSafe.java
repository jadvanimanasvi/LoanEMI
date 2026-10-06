package com.loanemi.calculator.emi.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public final class UiSafe {

    private UiSafe() {
    }

    public static boolean isAlive(@Nullable Activity activity) {
        if (activity == null) {
            return false;
        }
        return !activity.isFinishing() && !activity.isDestroyed();
    }

    public static boolean isAlive(@Nullable Fragment fragment) {
        return fragment != null
                && fragment.isAdded()
                && fragment.getContext() != null
                && isAlive(fragment.getActivity());
    }

    public static boolean isContextUnavailable(@Nullable Context context) {
        if (context == null) {
            return true;
        }
        if (context instanceof Activity) {
            return !isAlive((Activity) context);
        }
        return false;
    }

    public static boolean isViewUnattached(@Nullable View view) {
        return view == null || !view.isAttachedToWindow();
    }
}
