package com.example.loanemi.Activities.introflow;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.MyApplication;
import com.example.loanemi.R;
import com.example.loanemi.databinding.LoanFragmentInfo4Binding;

public final class LoanNativeAdFragment1 extends LoanLazyFragment {

    private LoanFullNativeSession nativeSession;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LoanFragmentInfo4Binding binding = LoanFragmentInfo4Binding.inflate(layoutInflater, viewGroup, false);
        binding.imgClose.setVisibility(View.INVISIBLE);
        binding.dataLyt.setVisibility(View.GONE);
        boolean newUi = LoanStartFlowAdHelper.isFlagOn(requireContext(), AppPreference.is_intro_full_screen_native_1_new_ui_show, true);
        if (newUi) {
            binding.imgSwipe.setVisibility(View.GONE);
        } else {
            Glide.with(requireActivity()).load(R.drawable.ic_swipe_left).into(binding.imgSwipe);
            binding.imgSwipe.setVisibility(View.VISIBLE);
            binding.imgSwipe.bringToFront();
        }
        nativeSession = new LoanFullNativeSession((AppCompatActivity) requireActivity(), binding.flAdNative, newUi, "onboarding_nativefull1_show", "onboarding_nativefull1_close", () -> {
            AllScreenIntro activity = (AllScreenIntro) requireActivity();
            activity.binding.viewPager.setCurrentItem(activity.start3PageIndex, true);
        });
        nativeSession.observeAd(MyApplication.instance.FullScreenFirstNativeAd);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        LoanActivityTracker.setCurrentActivity(null);
    }

    public void onResume() {
        super.onResume();
        if (nativeSession != null) {
            nativeSession.startTimersIfNeeded();
        }
    }

    @Override
    public void onDestroyView() {
        if (nativeSession != null) {
            nativeSession.release();
            nativeSession = null;
        }
        super.onDestroyView();
    }
}
