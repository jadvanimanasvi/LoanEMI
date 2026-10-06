package com.loanemi.calculator.emi.Models;

public class CurrencyItem {

    private final String code;
    private final String name;

    // Currency symbol/icon
    private final int currencyIconResId;

    // Country flag
    private final int flagResId;

    public CurrencyItem(
            String code,
            String name,
            int currencyIconResId,
            int flagResId
    ) {
        this.code = code;
        this.name = name;
        this.currencyIconResId = currencyIconResId;
        this.flagResId = flagResId;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getCurrencyIconResId() {
        return currencyIconResId;
    }

    public int getFlagResId() {
        return flagResId;
    }
}