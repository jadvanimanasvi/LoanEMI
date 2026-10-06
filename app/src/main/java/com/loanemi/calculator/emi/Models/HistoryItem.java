package com.loanemi.calculator.emi.Models;

public class HistoryItem {

    private final long id;

    private final String loanType;
    private final String date;

    private final double loanAmount;
    private final double interestRate;
    private final double loanTerm;

    private final String loanTermUnit;
    private final int totalMonths;

    private final double monthlyEmi;
    private final double totalInterest;
    private final double totalPayment;

    private final String startDate;

    private final String currencyCode;
    private final String currencySymbol;

    private final int iconRes;


    public HistoryItem(
            long id,
            String loanType,
            String date,
            double loanAmount,
            double interestRate,
            double loanTerm,
            String loanTermUnit,
            int totalMonths,
            double monthlyEmi,
            double totalInterest,
            double totalPayment,
            String startDate,
            String currencyCode,
            String currencySymbol,
            int iconRes
    ) {

        this.id = id;

        this.loanType = loanType;
        this.date = date;

        this.loanAmount = loanAmount;
        this.interestRate = interestRate;
        this.loanTerm = loanTerm;

        this.loanTermUnit = loanTermUnit;
        this.totalMonths = totalMonths;

        this.monthlyEmi = monthlyEmi;
        this.totalInterest = totalInterest;
        this.totalPayment = totalPayment;

        this.startDate = startDate;

        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;

        this.iconRes = iconRes;
    }


    public long getId() {
        return id;
    }

    public String getLoanType() {
        return loanType;
    }

    public String getDate() {
        return date;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public double getLoanTerm() {
        return loanTerm;
    }

    public String getLoanTermUnit() {
        return loanTermUnit;
    }

    public int getTotalMonths() {
        return totalMonths;
    }

    public double getMonthlyEmi() {
        return monthlyEmi;
    }

    public double getTotalInterest() {
        return totalInterest;
    }

    public double getTotalPayment() {
        return totalPayment;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public int getIconRes() {
        return iconRes;
    }


    public String getInterest() {

        if (interestRate == (long) interestRate) {
            return String.format(java.util.Locale.US, "%.0f%%", interestRate);
        }

        return String.format(java.util.Locale.US, "%.2f%%", interestRate);
    }


    public String getDuration() {

        if (loanTerm == (long) loanTerm) {
            return String.valueOf((long) loanTerm) + " " + loanTermUnit;
        }

        return String.valueOf(loanTerm) + " " + loanTermUnit;
    }


    public String getAmount() {
        return String.valueOf(loanAmount);
    }
}