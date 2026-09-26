package com.example.loanemi.Activities.remote;

public class RemoteConfigManager {
    private static RemoteConfigManager instance;
    public LoanIntroConfig loanIntroConfig;

    public static RemoteConfigManager getInstance() {
        if (instance == null) {
            instance = new RemoteConfigManager();
        }
        return instance;
    }

    public LoanIntroConfig requireIntroConfig() {
        if (loanIntroConfig == null) {
            loanIntroConfig = new LoanIntroConfig();
        }
        return loanIntroConfig;
    }
}
