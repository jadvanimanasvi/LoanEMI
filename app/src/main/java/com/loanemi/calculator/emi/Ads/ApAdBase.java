package com.loanemi.calculator.emi.Ads;

public abstract class ApAdBase {
    protected StatusAd status;

    public ApAdBase(StatusAd statusAd) {
        StatusAd statusAd2 = StatusAd.AD_INIT;
        this.status = statusAd;
    }

    public ApAdBase() {
        this.status = StatusAd.AD_INIT;
    }

    public StatusAd getStatus() {
        return this.status;
    }

    public void setStatus(StatusAd statusAd) {
        this.status = statusAd;
    }

    public abstract boolean isReady();

    public boolean isNotReady() {
        return !isReady();
    }

    public boolean isLoading() {
        return this.status == StatusAd.AD_LOADING;
    }

    public boolean isLoadFail() {
        return this.status == StatusAd.AD_LOAD_FAIL;
    }
}
