package com.loanemi.calculator.emi.introflow;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.loanemi.calculator.emi.R;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.loanemi.calculator.emi.Ads.AdCallback;
import com.loanemi.calculator.emi.Ads.AdConfig;
import com.loanemi.calculator.emi.Ads.ApNativeAd;
import com.loanemi.calculator.emi.databinding.LoanFragmentInfo1Binding;
import com.loanemi.calculator.emi.language.LocaleHelper;
import com.loanemi.calculator.emi.utils.AppPreference;
import com.loanemi.calculator.emi.utils.Constant;
import com.loanemi.calculator.emi.utils.MyApplication;

public class LoanStartFragment_1 extends Fragment {

    private LoanFragmentInfo1Binding binding;
    private AppCompatButton cta;
    boolean isAdClicked = false;
    long adClickAt = 0L;

    private final DefaultLifecycleObserver appLifecycleObserver = new DefaultLifecycleObserver() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onStart(@NonNull LifecycleOwner owner) {
            if (!isAdded()) {
                return;
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(LocaleHelper.setLocale(context));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = LoanFragmentInfo1Binding.inflate(inflater, container, false);

        applyBtnNextBottomSpace();
        addListener();

        if (LoanStartFlowAdHelper.isFlagOn(getActivity(), AppPreference.is_first_intro_new_next_btn_show, true)) {
            NextButtonShow();
        }

        new Handler().postDelayed(this::loadFullScreenNativeFirst, 1000);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            LoanActivityTracker.setCurrentActivity(requireActivity());
        } catch (Exception ignored) {
        }
        FirstIntroShow();
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    private void applyBtnNextBottomSpace() {
        if (binding == null) {
            return;
        }
        int spaceDp = LoanStartFlowAdHelper.startFlowGap(requireContext(), AppPreference.btnNext_bottom_space, 10);
        int spacePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceDp, getResources().getDisplayMetrics());
        ViewGroup.LayoutParams layoutParams = binding.rlnext.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) layoutParams;
            params.bottomMargin = spacePx;
            binding.rlnext.setLayoutParams(params);
        }
    }

    private void addListener() {
        View.OnClickListener nextClickListener = view -> {

            ((AllScreenIntro) requireActivity()).binding.viewPager.setCurrentItem(1);
        };
        binding.btnNext.setOnClickListener(nextClickListener);
        binding.btnNextButton.setOnClickListener(nextClickListener);
    }

    private void FirstIntroShow() {
        if (!isAdded() || getActivity() == null) {
            return;
        }
        if (!LoanStartFlowAdHelper.isFlagOn(getActivity(), AppPreference.is_first_intro_native_show, true)
                || !LoanStartFlowAdHelper.isNetworkConnected(getActivity())) {
            binding.layoutAdNative.setVisibility(View.INVISIBLE);
            binding.layouinclude.shimmerContainerNativeLarge.setVisibility(View.GONE);
            return;
        }
        MyApplication.instance.FirstIntroNativeAd.observe(getActivity(), apNativeAd -> {
            if (apNativeAd == null) {
                binding.layoutAdNative.setVisibility(View.INVISIBLE);
                binding.layouinclude.shimmerContainerNativeLarge.setVisibility(View.VISIBLE);
                return;
            }
            AdConfig.getInstance().populateNativeAdView(getActivity(), apNativeAd, binding.layoutAdNative, binding.layouinclude.shimmerContainerNativeLarge, () -> {
                Constant.markLeavingForAd();
                isAdClicked = true;
                adClickAt = System.currentTimeMillis();
            }, nativeAdView -> cta = nativeAdView.findViewById(R.id.ad_call_to_action));
        });
    }

    private void handleAdClickReturn() {
        if (!isAdClicked || !isAdded()) {
            return;
        }
        if (System.currentTimeMillis() - adClickAt < 400) {
            return;
        }
        isAdClicked = false;
    }

    private void NextButtonShow() {
        binding.btnNext.setVisibility(View.GONE);
        binding.agreeContainer.setVisibility(View.VISIBLE);
        startButtonAnimation();
        binding.shine.post(this::shineAnimation);
        int spaceDp = LoanStartFlowAdHelper.startFlowGap(requireContext(), AppPreference.btnNext_bottom_space, 10);
        int spacePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, spaceDp, getResources().getDisplayMetrics());
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.rlnext.getLayoutParams();
        params.bottomMargin = spacePx;
        binding.rlnext.setLayoutParams(params);
    }

    private void startButtonAnimation() {
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
        float endX = binding.btnNextButton.getWidth();
        ObjectAnimator animator = ObjectAnimator.ofFloat(binding.shine, "translationX", startX, endX);
        animator.setDuration(1200);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public void loadFullScreenNativeFirst() {
        if (!isAdded() || !LoanStartFlowAdHelper.isFlagOn(requireActivity(), AppPreference.is_intro_full_screen_native_1_show, true)
                || !LoanStartFlowAdHelper.isNetworkConnected(requireActivity())) {
            MyApplication.instance.FullScreenFirstNativeAd.postValue(null);
            return;
        }
        int layout = LoanStartFlowAdHelper.NativeLayoutFull(requireActivity(), AppPreference.is_intro_full_screen_native_1_new_ui_show);
        LoanStartFlowAdHelper.fetchStartFlowNative(requireActivity(),
                getString(R.string.full_first_native_1),
                getString(R.string.full_first_native_2),
                layout, new AdCallback() {
                    @Override
                    public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                        super.onNativeAdLoaded(apNativeAd);
                        MyApplication.instance.FullScreenFirstNativeAd.postValue(apNativeAd);
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
                        MyApplication.instance.FullScreenFirstNativeAd.postValue(null);
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && binding != null) {
            MyApplication.instance.applyStartFlowSystemBars(requireActivity(), binding.getRoot());
        }
        handleAdClickReturn();
    }

    @Override
    public void onDestroyView() {
        try {
            ProcessLifecycleOwner.get().getLifecycle().removeObserver(appLifecycleObserver);
        } catch (Exception ignored) {
        }
        cta = null;
        binding = null;
        super.onDestroyView();
    }
}
