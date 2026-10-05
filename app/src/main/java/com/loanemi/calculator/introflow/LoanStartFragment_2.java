package com.loanemi.calculator.introflow;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.loanemi.R;
import com.example.loanemi.databinding.LoanFragmentInfo2Binding;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.loanemi.calculator.Ads.AdCallback;
import com.loanemi.calculator.Ads.ApNativeAd;
import com.loanemi.calculator.utils.AppPreference;
import com.loanemi.calculator.utils.Constant;
import com.loanemi.calculator.utils.MyApplication;

public class LoanStartFragment_2 extends Fragment {

    Activity activity;
    LoanFragmentInfo2Binding binding;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = LoanFragmentInfo2Binding.inflate(getLayoutInflater());
        activity = getActivity();
        addListener();
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle bundle) {
        loadFullScreenNativeSecond();
        LoanActivityTracker.setCurrentActivity(requireActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addListener() {
        startButtonPulseAnimation();
        binding.shine.post(this::shineAnimation);
        binding.btnNext.setOnClickListener(view -> ((AllScreenIntro) requireActivity()).binding.viewPager.setCurrentItem(2));
    }

    private void shineAnimation() {
        float startX = -binding.shine.getWidth();
        float endX = binding.btnNext.getWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.shine, "translationX", startX, endX);
        animator.setDuration(1200);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    private void startButtonPulseAnimation() {
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(binding.agreeContainer, "scaleX", 1f, 1.05f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(binding.agreeContainer, "scaleY", 1f, 1.05f);
        scaleX.setDuration(800);
        scaleY.setDuration(800);
        scaleX.setRepeatMode(ValueAnimator.REVERSE);
        scaleY.setRepeatMode(ValueAnimator.REVERSE);
        scaleX.setRepeatCount(ValueAnimator.INFINITE);
        scaleY.setRepeatCount(ValueAnimator.INFINITE);
        scaleX.setInterpolator(new LinearInterpolator());
        scaleY.setInterpolator(new LinearInterpolator());
        scaleX.start();
        scaleY.start();

    }

    public void loadFullScreenNativeSecond() {
        if (!isAdded() || !LoanStartFlowAdHelper.isFlagOn(requireActivity(), AppPreference.is_intro_full_screen_native_2_show, true)
                || !LoanStartFlowAdHelper.isNetworkConnected(requireActivity())) {
            MyApplication.instance.FullScreenSecondNativeAd.postValue(null);
            return;
        }
        int layout = LoanStartFlowAdHelper.NativeLayoutFull(requireActivity(), AppPreference.is_intro_full_screen_native_2_new_ui_show);
        LoanStartFlowAdHelper.fetchStartFlowNative(requireActivity(),
                getString(R.string.full_second_native_1),
                getString(R.string.full_second_native_2),
                layout, new AdCallback() {
                    @Override
                    public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                        super.onNativeAdLoaded(apNativeAd);
                        MyApplication.instance.FullScreenSecondNativeAd.postValue(apNativeAd);
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        Constant.markLeavingForAd();
                        LoanActivityTracker.setCurrentString("AdClick");
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        MyApplication.instance.FullScreenSecondNativeAd.postValue(null);
                    }
                });
    }

}
