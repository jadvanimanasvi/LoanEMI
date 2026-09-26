package com.example.loanemi.Activities.introflow;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.MyApplication;
import com.example.loanemi.databinding.LoanFragmentInfo4Binding;


public final class LoanNativeAdFragment2 extends LoanLazyFragment {

    private LoanFullNativeSession nativeSession;

    public View onCreateView(@NonNull LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        LoanFragmentInfo4Binding inflate = LoanFragmentInfo4Binding.inflate(layoutInflater, viewGroup, false);
        inflate.imgSwipe.setVisibility(View.GONE);
        inflate.imgClose.setVisibility(View.INVISIBLE);
        inflate.dataLyt.setVisibility(View.GONE);
        LoanActivityTracker.setCurrentActivity(null);
        boolean newUi = LoanStartFlowAdHelper.isFlagOn(requireContext(), AppPreference.is_intro_full_screen_native_2_new_ui_show, true);
        nativeSession = new LoanFullNativeSession((AppCompatActivity) requireActivity(),
                inflate.flAdNative,
                newUi,
                "onboarding_nativefull2_show",
                "onboarding_nativefull2_close",
                this::openGetStarted);
        nativeSession.observeAd(MyApplication.instance.FullScreenSecondNativeAd);
        return inflate.getRoot();
    }

    private void openGetStarted() {
        startActivity(new Intent(getActivity(), GetStartActivity.class));
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
