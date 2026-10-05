package com.loanemi.calculator.Ads;

import android.view.View;
import androidx.annotation.NonNull;
import com.google.android.libraries.ads.mobile.sdk.nativead.NativeAd;

public class ApNativeAd extends ApAdBase {

    private NativeAd admobNativeAd;
    private int layoutCustomNative;
    private View nativeView;

    public ApNativeAd(StatusAd statusAd) {
        super(statusAd);
    }

    public ApNativeAd(int i, View view) {
        this.layoutCustomNative = i;
        this.nativeView = view;
        this.status = StatusAd.AD_LOADED;
    }

    public ApNativeAd(int i, NativeAd nativeAd) {
        this.layoutCustomNative = i;
        this.admobNativeAd = nativeAd;
        this.status = StatusAd.AD_LOADED;
    }

    public ApNativeAd() {
    }

    public NativeAd getAdmobNativeAd() {
        return this.admobNativeAd;
    }

    public void setAdmobNativeAd(NativeAd nativeAd) {
        this.admobNativeAd = nativeAd;
        if (nativeAd != null) {
            this.status = StatusAd.AD_LOADED;
        }
    }

    public int getLayoutCustomNative() {
        return this.layoutCustomNative;
    }

    public void setLayoutCustomNative(int i) {
        this.layoutCustomNative = i;
    }

    public View getNativeView() {
        return this.nativeView;
    }

    public void setNativeView(View view) {
        this.nativeView = view;
    }

    public boolean isReady() {
        return this.nativeView != null || this.admobNativeAd != null;
    }

    @NonNull
    public String toString() {
        return "Status:" + this.status + " == nativeView:" + this.nativeView + " == admobNativeAd:" + this.admobNativeAd;
    }
}
