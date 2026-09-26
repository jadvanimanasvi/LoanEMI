package com.example.loanemi.Activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loanemi.Activities.Ads.NativeAdPreloader;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ToolsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        setupAds(view);

        return view;
    }

    private void setupAds(@NonNull View view) {
        if (Util.isInternetAvailable(this.requireActivity())) {
            FrameLayout nativeLayout = view.findViewById(R.id.tools_native_layout);
            ShimmerFrameLayout shimmerNative = view.findViewById(R.id.nativeShimmerLayout);

            NativeAdPreloader.show(
                    requireActivity(),
                    nativeLayout,
                    getString(R.string.tools_native_medium),
                    shimmerNative
            );
        }
    }
}
