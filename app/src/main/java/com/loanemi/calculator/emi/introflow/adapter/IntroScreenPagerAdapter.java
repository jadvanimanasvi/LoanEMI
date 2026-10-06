package com.loanemi.calculator.emi.introflow.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class IntroScreenPagerAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragments;

    public IntroScreenPagerAdapter(FragmentActivity fragmentActivity, ArrayList<Fragment> arrayList) {
        super(fragmentActivity);
        this.fragments = arrayList;
    }

    public final ArrayList<Fragment> getFragments() {
        return this.fragments;
    }

    public int getItemCount() {
        return this.fragments.size();
    }

    public Fragment createFragment(int i) {
        return this.fragments.get(i);
    }

}
