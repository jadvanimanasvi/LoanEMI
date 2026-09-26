package com.example.loanemi.Activities.introflow;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.loanemi.Activities.BaseActivity;
import com.example.loanemi.Activities.introflow.adapter.IntroScreenPagerAdapter;
import com.example.loanemi.Activities.utils.AppPreference;
import com.example.loanemi.Activities.utils.MyApplication;
import com.example.loanemi.R;
import com.example.loanemi.databinding.ActivityAllScreenIntroBinding;

import java.util.ArrayList;
import kotlin.collections.CollectionsKt;

public class AllScreenIntro extends BaseActivity {

    public int fragSize = 3;
    public boolean isWithNativeAd = true;
    public ActivityAllScreenIntroBinding binding;
    int value = 0;
    int showsecond = 0;
    public int start3PageIndex = 2;
    public int native2PageIndex = -1;
    public int native1PageIndex = -1;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityAllScreenIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MyApplication.instance.applyStartFlowSystemBars(this, findViewById(R.id.main));
        setupEdgeToEdge(binding.getRoot());
        LoanActivityTracker.setCurrentActivity(this);
        showsecond = 0;
        boolean online = LoanStartFlowAdHelper.isNetworkConnected(this);
        boolean showNative1 = online && LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_intro_full_screen_native_1_show, true);
        boolean showNative2 = online && LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_intro_full_screen_native_2_show, true);
        native1PageIndex = -1;
        native2PageIndex = -1;
        start3PageIndex = 2;

        ArrayList<Fragment> fragments;
        if (showNative1 && showNative2) {
            isWithNativeAd = true;
            showsecond = 1;
            native1PageIndex = 2;
            start3PageIndex = 3;
            native2PageIndex = 4;
            fragments = CollectionsKt.arrayListOf(new LoanStartFragment_1(), new LoanStartFragment_2(), new LoanNativeAdFragment1(), new LoanStartFragment_3(), new LoanNativeAdFragment2());
            fragSize = fragments.size();
            value = 1;
        } else if (showNative1) {
            isWithNativeAd = true;
            native1PageIndex = 2;
            start3PageIndex = 3;
            fragments = CollectionsKt.arrayListOf(new LoanStartFragment_2(), new LoanStartFragment_2(), new LoanNativeAdFragment1(), new LoanStartFragment_2());
            fragSize = fragments.size();
            value = 1;
        } else if (showNative2) {
            isWithNativeAd = true;
            showsecond = 1;
            native2PageIndex = 3;
            fragments = CollectionsKt.arrayListOf(new LoanStartFragment_2(), new LoanStartFragment_2(), new LoanStartFragment_2(), new LoanNativeAdFragment2());
            fragSize = fragments.size();
            value = 2;
        } else {
            isWithNativeAd = false;
            fragments = CollectionsKt.arrayListOf(new LoanStartFragment_2(), new LoanStartFragment_2(), new LoanStartFragment_2());
            fragSize = fragments.size();
            value = 4;
            binding.viewPager.setUserInputEnabled(false);
        }

        LoanStartFlowAdHelper.handleLanguageDoneNextScreen(this);
        binding.viewPager.setAdapter(new IntroScreenPagerAdapter(this, fragments));
        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            public void onPageSelected(int i) {
                if (i == native1PageIndex) {
                    LoanActivityTracker.setCurrentFragment(new LoanNativeAdFragment1());
                    binding.viewPager.setUserInputEnabled(true);
                } else if (i == native2PageIndex) {
                    LoanActivityTracker.setCurrentFragment(new LoanNativeAdFragment2());
                    binding.viewPager.setUserInputEnabled(true);
                } else {
                    binding.viewPager.setUserInputEnabled(false);
                    if (i == 0) {
                        LoanActivityTracker.setCurrentFragment(new LoanStartFragment_1());
                        MyApplication.instance.applyStartFlowSystemBars(AllScreenIntro.this, findViewById(R.id.main));
                    } else if (i == 1) {
                        LoanActivityTracker.setCurrentFragment(new LoanStartFragment_2());
                        MyApplication.instance.applyStartFlowSystemBars(AllScreenIntro.this, findViewById(R.id.main));
                    } else if (i == start3PageIndex) {
                        LoanActivityTracker.setCurrentFragment(new LoanStartFragment_2());
                        MyApplication.instance.applyStartFlowSystemBars(AllScreenIntro.this, findViewById(R.id.main));
                    }
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            MyApplication.instance.applyStartFlowSystemBars(this, findViewById(R.id.main));
        } catch (Exception ignored) {
        }
        LoanStartFlowAdHelper.tryShowDeferredInterstitial();
    }
}
