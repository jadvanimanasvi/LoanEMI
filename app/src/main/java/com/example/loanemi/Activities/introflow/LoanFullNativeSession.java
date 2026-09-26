package com.example.loanemi.Activities.introflow;

import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.loanemi.Activities.Ads.AdConfig;
import com.example.loanemi.Activities.Ads.ApNativeAd;
import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.Constant;
import com.example.loanemi.R;


public final class LoanFullNativeSession {

    private final AppCompatActivity activity;
    private final boolean newUi;
    private final Runnable onClose;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private View overlay;
    private CountDownTimer countDown;
    private boolean nativeAdShown;
    private boolean closeShown;
    private boolean skipStarted;
    private boolean timersStarted;
    private boolean released;

    public LoanFullNativeSession(@NonNull AppCompatActivity activity,
                                 @NonNull ViewGroup root,
                                 boolean newUi,
                                 String showEvent,
                                 String closeEvent,
                                 Runnable onClose) {
        this.activity = activity;
        this.newUi = newUi;
        this.onClose = onClose;
        overlay = LayoutInflater.from(activity).inflate(R.layout.loan_start_flow_full_native_overlay, root, false);
        root.addView(overlay, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        FrameLayout flAdNative = overlay.findViewById(R.id.flAdNative);
        View skipBar = overlay.findViewById(R.id.llSkipBar);
        if (skipBar != null) {
            skipBar.setVisibility(View.GONE);
        }
        if (flAdNative != null) {
            int shimmerLayout = newUi ? R.layout.full_screen_native_shimmer_view_new : R.layout.full_screen_native_shimmer_view;
            flAdNative.addView(LayoutInflater.from(activity).inflate(shimmerLayout, flAdNative, false));
        }
        bindClose();
    }

    public void observeAd(LiveData<ApNativeAd> liveData) {
        liveData.observe(activity, apNativeAd -> {
            if (released || apNativeAd == null || overlay == null) {
                return;
            }
            FrameLayout flAdNative = overlay.findViewById(R.id.flAdNative);
            if (flAdNative == null) {
                return;
            }
            View shimmer = flAdNative.findViewById(R.id.shimmer_container_native);
            AdConfig.getInstance().populateNativeAdView(activity, apNativeAd, flAdNative, shimmer);
            nativeAdShown = true;
            View adLabel = flAdNative.findViewById(R.id.tvAdLabel);
            if (adLabel != null) {
                adLabel.setVisibility(View.VISIBLE);
            }
            wireCta(flAdNative);
            bindClose();
            if (closeShown) {
                showClose();
            } else if (timersStarted && newUi) {
                startSkipTimer();
            }
        });
    }

    public void startTimersIfNeeded() {
        if (released || timersStarted || closeShown) {
            return;
        }
        if (newUi && nativeAdShown) {
            startSkipTimer();
        } else {
            startCloseWait();
        }
    }

    public void release() {
        released = true;
        cancelTimers();
        overlay = null;
    }

    private void startCloseWait() {
        if (closeShown || skipStarted) {
            return;
        }
        timersStarted = true;
        cancelTimers();
        long remaining = LoanStartFlowAdHelper.getCloseTimerSeconds(activity, AppPreference.full_native_close_timer, 1) * 1000L;
        if (remaining <= 0) {
            showClose();
            return;
        }
        countDown = new CountDownTimer(remaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (released) {
                    cancel();
                }
            }

            @Override
            public void onFinish() {
                if (released) {
                    return;
                }
                if (newUi && nativeAdShown) {
                    startSkipTimer();
                    return;
                }
                showClose();
            }
        };
        countDown.start();
    }

    private void startSkipTimer() {
        if (released || closeShown || skipStarted) {
            return;
        }
        skipStarted = true;
        timersStarted = true;
        cancelTimers();
        int skipSeconds = LoanStartFlowAdHelper.getCloseTimerSeconds(activity, AppPreference.full_native_skip_timer, 4);
        if (skipSeconds <= 0) {
            handler.postDelayed(this::showClose, 1000);
            return;
        }
        updateSkipText(skipSeconds);
        countDown = new CountDownTimer(skipSeconds * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (released) {
                    cancel();
                    return;
                }
                updateSkipText((int) Math.ceil(millisUntilFinished / 1000.0));
            }

            @Override
            public void onFinish() {
                if (released) {
                    return;
                }
                updateSkipText(0);
                handler.postDelayed(() -> {
                    if (!released) {
                        showClose();
                    }
                }, 1000);
            }
        };
        countDown.start();
    }

    private void updateSkipText(int seconds) {
        if (overlay == null) {
            return;
        }
        TextView tvSkipIn = LoanStartFlowAdHelper.findSkipCountdownView(overlay);
        if (tvSkipIn == null) {
            return;
        }
        if (seconds > 0) {
            LoanStartFlowAdHelper.showSkipTimerText(tvSkipIn, activity.getString(R.string.skip_in, seconds));
        } else {
            LoanStartFlowAdHelper.hideSkipTimerAnimated(tvSkipIn);
        }
    }

    private void showClose() {
        if (overlay == null) {
            return;
        }
        closeShown = true;
        TextView tvSkipIn = LoanStartFlowAdHelper.findSkipCountdownView(overlay);
        if (tvSkipIn != null) {
            LoanStartFlowAdHelper.hideSkipTimerAnimated(tvSkipIn);
        }
        ImageView imgCloseOld = overlay.findViewById(R.id.imgClose);
        ImageView imgCloseNew = getOverlayCloseNew();
        ImageView imgNextArrow = overlay.findViewById(R.id.imgNextArrow);
        if (newUi) {
            if (imgNextArrow != null) {
                imgNextArrow.setVisibility(View.VISIBLE);
            }
            if (imgCloseNew != null) {
                imgCloseNew.setVisibility(View.VISIBLE);
            }
        } else if (imgCloseOld != null) {
            imgCloseOld.setVisibility(View.VISIBLE);
        }
        bindClose();
        FrameLayout flAdNative = overlay.findViewById(R.id.flAdNative);
        wireCta(flAdNative);
    }

    private void bindClose() {
        if (overlay == null) {
            return;
        }
        View.OnClickListener closeClick = v -> {
            cancelTimers();
            if (onClose != null) {
                onClose.run();
            }
        };
        ImageView imgClose = overlay.findViewById(R.id.imgClose);
        if (imgClose != null) {
            imgClose.setOnClickListener(closeClick);
        }
        ImageView imgCloseNew = getOverlayCloseNew();
        if (imgCloseNew != null) {
            imgCloseNew.setOnClickListener(closeClick);
        }
    }

    private ImageView getOverlayCloseNew() {
        if (!(overlay instanceof ViewGroup)) {
            return null;
        }
        ViewGroup group = (ViewGroup) overlay;
        for (int i = 0; i < group.getChildCount(); i++) {
            View child = group.getChildAt(i);
            if (child.getId() == R.id.imgCloseNew && child instanceof ImageView) {
                return (ImageView) child;
            }
        }
        return null;
    }

    private void wireCta(View root) {
        if (root == null) {
            return;
        }
        View arrow = root.findViewById(R.id.imgNextArrow);
        View cta = root.findViewById(R.id.ad_call_to_action);
        if (arrow != null && cta != null) {
            arrow.setVisibility(View.VISIBLE);
            arrow.setOnClickListener(v -> {
                Constant.markLeavingForAd();
                LoanActivityTracker.setCurrentString("AdClick");
                cta.performClick();
            });
        }
    }

    private void cancelTimers() {
        handler.removeCallbacksAndMessages(null);
        if (countDown != null) {
            countDown.cancel();
            countDown = null;
        }
        if (overlay != null) {
            LoanStartFlowAdHelper.cancelSkipHide(LoanStartFlowAdHelper.findSkipCountdownView(overlay));
        }
    }
}
