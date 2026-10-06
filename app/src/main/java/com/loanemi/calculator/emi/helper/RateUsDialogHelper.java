package com.loanemi.calculator.emi.helper;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.cardview.widget.CardView;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.utils.AppPreference;
import com.loanemi.calculator.emi.utils.ClickGuard;
import com.loanemi.calculator.emi.utils.UiSafe;
import com.loanemi.calculator.emi.utils.Util;

public final class RateUsDialogHelper {

    private RateUsDialogHelper() {
    }

    public static void show(@NonNull AppCompatActivity activity, @NonNull Listener listener) {
        if (!UiSafe.isAlive(activity)) {
            return;
        }

        Dialog dialog = getDialog(activity);

        AppCompatImageView star1 = dialog.findViewById(R.id.star1);
        AppCompatImageView star2 = dialog.findViewById(R.id.star2);
        AppCompatImageView star3 = dialog.findViewById(R.id.star3);
        AppCompatImageView star4 = dialog.findViewById(R.id.star4);
        AppCompatImageView star5 = dialog.findViewById(R.id.star5);
        View layoutStars = dialog.findViewById(R.id.layoutStars);
        CardView btnClose = dialog.findViewById(R.id.btnClose);
        Button btnRate = dialog.findViewById(R.id.btnRate);

        AppCompatImageView[] stars = {star1, star2, star3, star4, star5};
        final int[] rating = {0};
        final boolean[] ratingSubmitted = {false};
        final ValueAnimator[] hintAnimator = {null};

        applyStarSelection(stars, 0);
        updateRateButtonState(activity, btnRate, false);

        Runnable stopHint = () -> {
            if (hintAnimator[0] != null) {
                hintAnimator[0].cancel();
                hintAnimator[0] = null;
            }
            for (AppCompatImageView star : stars) {
                if (star != null) {
                    star.setAlpha(1f);
                    star.setScaleX(1f);
                    star.setScaleY(1f);
                }
            }
        };

        ClickGuard.onClick(btnClose, v -> dialog.dismiss());

        for (int i = 0; i < stars.length; i++) {
            int index = i;
            ClickGuard.onClick(stars[i], v -> {
                stopHint.run();
                rating[0] = index + 1;
                applyStarSelection(stars, rating[0]);
                updateRateButtonState(activity, btnRate, true);
            });
        }

        ClickGuard.onClick(btnRate, v -> {
            if (rating[0] <= 0) {
                Toast.makeText(activity, R.string.rate_select_star, Toast.LENGTH_SHORT).show();
                return;
            }
            AppPreference.getInstance(activity).setInt(AppPreference.KEY_USER_RATE, rating[0]);
            ratingSubmitted[0] = true;
            dialog.dismiss();
            if (rating[0] >= 4) {
                listener.onHighRating(rating[0]);
            } else {
                listener.onLowRating(rating[0]);
            }
        });

        dialog.setOnDismissListener(d -> {
            stopHint.run();
            if (!ratingSubmitted[0]) {
                listener.onDismissed();
            }
        });

        dialog.show();
        Util.hideUiWithDialog(dialog);
        if (layoutStars != null) {
            startStarHintAnimation(layoutStars, stars, hintAnimator);
        }
    }

    private static void updateRateButtonState(AppCompatActivity activity, @NonNull Button btnRate, boolean hasRating) {
        btnRate.setBackgroundTintList(ColorStateList.valueOf(hasRating ?
                activity.getColor(R.color.blue) : activity.getColor(R.color.light_grey)));
    }

    private static void applyStarSelection(@NonNull AppCompatImageView[] stars, int selectedRating) {
        for (int j = 0; j < stars.length; j++) {
            if (stars[j] == null) {
                continue;
            }
            stars[j].setImageResource(
                    j < selectedRating ? R.drawable.star_fill : R.drawable.star_img
            );
            stars[j].setAlpha(1f);
            stars[j].setScaleX(1f);
            stars[j].setScaleY(1f);
        }
    }

    private static void startStarHintAnimation(
            @NonNull View layoutStars,
            @NonNull AppCompatImageView[] stars,
            @NonNull ValueAnimator[] holder
    ) {
        layoutStars.post(() -> {
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.setDuration(1600L);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.setStartDelay(350L);
            animator.addUpdateListener(animation -> {
                float progress = (float) animation.getAnimatedValue();
                int focusIndex = Math.min(stars.length - 1, Math.max(0, (int) (progress * stars.length)));
                for (int j = 0; j < stars.length; j++) {
                    if (stars[j] == null) {
                        continue;
                    }
                    stars[j].setImageResource(R.drawable.star_img);
                    if (j == focusIndex) {
                        stars[j].setAlpha(1f);
                        stars[j].setScaleX(1.15f);
                        stars[j].setScaleY(1.15f);
                    } else if (j < focusIndex) {
                        stars[j].setAlpha(0.65f);
                        stars[j].setScaleX(1f);
                        stars[j].setScaleY(1f);
                    } else {
                        stars[j].setAlpha(0.35f);
                        stars[j].setScaleX(1f);
                        stars[j].setScaleY(1f);
                    }
                }
            });
            animator.start();
            holder[0] = animator;
        });
    }

    @NonNull
    private static Dialog getDialog(@NonNull AppCompatActivity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.dialog_rate_us);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.WRAP_CONTENT
            );
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;
    }

    public interface Listener {
        void onLowRating(int rating);

        void onHighRating(int rating);

        default void onDismissed() {
        }
    }
}
