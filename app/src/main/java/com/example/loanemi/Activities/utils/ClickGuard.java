package com.example.loanemi.Activities.utils;

import android.os.SystemClock;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.loanemi.R;

public final class ClickGuard {

    public static final long DEFAULT_INTERVAL_MS = 500L;
    public static final long ACTION_INTERVAL_MS = 600L;

    private long lastClickTime;
    private final long intervalMs;

    public ClickGuard() {
        this(DEFAULT_INTERVAL_MS);
    }

    public ClickGuard(long intervalMs) {
        this.intervalMs = intervalMs;
    }

    public static void onClick(@Nullable View view, @NonNull Runnable action) {
        onClick(view, DEFAULT_INTERVAL_MS, action);
    }

    public static void onClick(@Nullable View view, long intervalMs, @NonNull Runnable action) {
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> {
            if (!isClickTooSoon(v, intervalMs)) {
                action.run();
            }
        });
    }

    public static void onClick(@Nullable View view, @NonNull View.OnClickListener listener) {
        onClick(view, DEFAULT_INTERVAL_MS, listener);
    }

    public static void onClick(
            @Nullable View view,
            long intervalMs,
            @NonNull View.OnClickListener listener
    ) {
        if (view == null) {
            return;
        }
        view.setOnClickListener(v -> {
            if (!isClickTooSoon(v, intervalMs)) {
                listener.onClick(v);
            }
        });
    }

    public static boolean isClickTooSoon(@Nullable View view) {
        return isClickTooSoon(view, DEFAULT_INTERVAL_MS);
    }

    public static boolean isClickTooSoon(@Nullable View view, long intervalMs) {
        if (view == null) {
            return true;
        }
        long now = SystemClock.elapsedRealtime();
        Object tag = view.getTag(R.id.click_guard_tag);
        long last = tag instanceof Long ? (Long) tag : 0L;
        if (now - last < intervalMs) {
            return true;
        }
        view.setTag(R.id.click_guard_tag, now);
        return false;
    }

    public boolean isActionTooSoon() {
        long now = SystemClock.elapsedRealtime();
        if (now - lastClickTime < intervalMs) {
            return true;
        }
        lastClickTime = now;
        return false;
    }
}
