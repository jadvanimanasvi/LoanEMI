package com.example.loanemi.Activities.introflow;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.loanemi.Activities.Ads.AdCallback;
import com.example.loanemi.Activities.Ads.ApNativeAd;
import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.Constant;
import com.example.loanemi.Activities.utils.MyApplication;
import com.example.loanemi.R;
import com.example.loanemi.databinding.LoanFragmentInfo3Binding;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;

public class LoanStartFragment_3 extends Fragment {

    LoanFragmentInfo3Binding binding;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        binding = LoanFragmentInfo3Binding.inflate(getLayoutInflater());
        startButtonPulseAnimation();
        binding.shine.post(this::shineAnimation);
        preloadGetStartedNative();
        MyApplication.instance.applyStartFlowSystemBars(requireActivity(), binding.main);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle bundle) {
        LoanActivityTracker.setCurrentActivity(requireActivity());
        addListener();
    }

    private void addListener() {
        binding.btnNext.setOnClickListener(view -> {
            try {
                AllScreenIntro tutorial = (AllScreenIntro) requireActivity();
                if (tutorial.native2PageIndex >= 0) {
                    tutorial.binding.viewPager.setCurrentItem(tutorial.native2PageIndex);
                } else {
                    loadActivity();
                }
            } catch (Exception e) {
                loadActivity();
            }
        });
    }

    public void preloadGetStartedNative() {
        try {
            if (isAdded() && LoanStartFlowAdHelper.isFlagOn(requireActivity(), AppPreference.is_get_started_native_show, true) && LoanStartFlowAdHelper.isNetworkConnected(requireActivity())) {
                LoanStartFlowAdHelper.fetchStartFlowNative(requireActivity(), getString(R.string.native_get_started_1), getString(R.string.native_get_started_2), R.layout.native_large_start_flow_top, new AdCallback() {

                    @Override
                    public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                        super.onNativeAdLoaded(apNativeAd);
                        MyApplication.instance.GetStartedNativeAd.postValue(apNativeAd);
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
                        MyApplication.instance.GetStartedNativeAd.setValue(null);
                    }
                });
                return;
            }
            MyApplication.instance.GetStartedNativeAd.setValue(null);
        } catch (Exception ignored) {
        }
    }

    private void loadActivity() {
        startActivity(new Intent(requireActivity(), AllScreenIntro.class));
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

    private void shineAnimation() {
        float startX = -binding.shine.getWidth();
        float endX = binding.btnNext.getWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.shine, "translationX", startX, endX);
        animator.setDuration(1200);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
}
