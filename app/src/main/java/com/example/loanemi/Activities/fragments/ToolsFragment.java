package com.example.loanemi.Activities.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.loanemi.Activities.Activities.BusinessLoanActivity;
import com.example.loanemi.Activities.Activities.Tools.AgeCalculationActivity;
import com.example.loanemi.Activities.Activities.Tools.ExchangeRateActivity;
import com.example.loanemi.Activities.Activities.Tools.LengthConverterActivity;
import com.example.loanemi.Activities.Activities.Tools.SpeedConverterActivity;
import com.example.loanemi.Activities.Activities.Tools.TemperatureConverterActivity;
import com.example.loanemi.Activities.Activities.Tools.WeightConverterActivity;
import com.example.loanemi.Activities.Ads.NativeAdPreloader;
import com.example.loanemi.Activities.MainActivity;
import com.example.loanemi.Activities.introflow.LoanStartFlowAdHelper;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.facebook.shimmer.ShimmerFrameLayout;

public class ToolsFragment extends Fragment {

    private LinearLayout length, ExchangeRate, Weight, Age, speed, Temperature;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tools, container, false);

        initViews(view);
        setupAds(view);
        setupClickListeners();
        setupBackPressed();
        return view;
    }

    private void initViews(View view) {
        length = view.findViewById(R.id.length);
        ExchangeRate = view.findViewById(R.id.ExchangeRate);
        Weight = view.findViewById(R.id.Weight);
        Age = view.findViewById(R.id.Age);
        speed = view.findViewById(R.id.speed);
        Temperature = view.findViewById(R.id.Temperature);

    }

    private void setupClickListeners() {

        length.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), LengthConverterActivity.class);
            startActivity(i);
        });

        ExchangeRate.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), ExchangeRateActivity.class);
            startActivity(i);
        });

        Weight.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), WeightConverterActivity.class);
            startActivity(i);
        });

        Age.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), AgeCalculationActivity.class);
            startActivity(i);
        });

        speed.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), SpeedConverterActivity.class);
            startActivity(i);
        });

        Temperature.setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), TemperatureConverterActivity.class);
            startActivity(i);
        });

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

    private void setupBackPressed() {

        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        requireActivity()
                                .getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.fragmentContainer, new HomeFragment())
                                .commit();

                        if (requireActivity() instanceof MainActivity) {
                            ((MainActivity) requireActivity()).selectHomeTab();
                        }
                    }
                }
        );
    }



}
