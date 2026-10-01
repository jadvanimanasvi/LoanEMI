package com.example.loanemi.Activities.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.loanemi.Activities.Models.HistoryItem;
import com.example.loanemi.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoanHistoryManager {

    private static final String PREF_NAME = "loan_history_pref";
    private static final String KEY_HISTORY = "history";

    private final SharedPreferences preferences;


    public LoanHistoryManager(Context context) {

        preferences = context.getSharedPreferences(
                PREF_NAME,
                Context.MODE_PRIVATE
        );
    }


    // =========================================================
    // ADD FULL HISTORY
    // =========================================================

    public void addHistory(
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

        List<HistoryItem> historyList = getHistory();

        long id = System.currentTimeMillis();

        HistoryItem item = new HistoryItem(
                id,
                loanType,
                date,
                loanAmount,
                interestRate,
                loanTerm,
                loanTermUnit,
                totalMonths,
                monthlyEmi,
                totalInterest,
                totalPayment,
                startDate,
                currencyCode,
                currencySymbol,
                iconRes
        );

        // Newest history at top
        historyList.add(0, item);

        saveHistory(historyList);
    }


    // =========================================================
    // GET HISTORY
    // =========================================================

    public List<HistoryItem> getHistory() {

        List<HistoryItem> historyList = new ArrayList<>();

        String json = preferences.getString(KEY_HISTORY, null);

        if (json == null || json.isEmpty()) {
            return historyList;
        }

        try {

            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject object = jsonArray.getJSONObject(i);

                HistoryItem item = new HistoryItem(

                        object.optLong(
                                "id",
                                System.currentTimeMillis()
                        ),

                        object.optString(
                                "loanType",
                                "Personal Loan"
                        ),

                        object.optString(
                                "date",
                                ""
                        ),

                        object.optDouble(
                                "loanAmount",
                                0
                        ),

                        object.optDouble(
                                "interestRate",
                                0
                        ),

                        object.optDouble(
                                "loanTerm",
                                0
                        ),

                        object.optString(
                                "loanTermUnit",
                                "Months"
                        ),

                        object.optInt(
                                "totalMonths",
                                0
                        ),

                        object.optDouble(
                                "monthlyEmi",
                                0
                        ),

                        object.optDouble(
                                "totalInterest",
                                0
                        ),

                        object.optDouble(
                                "totalPayment",
                                0
                        ),

                        object.optString(
                                "startDate",
                                ""
                        ),

                        object.optString(
                                "currencyCode",
                                "USD"
                        ),

                        object.optString(
                                "currencySymbol",
                                "$"
                        ),

                        object.optInt(
                                "iconRes",
                                R.drawable.personal_ic
                        )
                );

                historyList.add(item);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return historyList;
    }


    // =========================================================
    // SAVE HISTORY
    // =========================================================

    private void saveHistory(List<HistoryItem> historyList) {

        JSONArray jsonArray = new JSONArray();

        try {

            for (HistoryItem item : historyList) {

                JSONObject object = new JSONObject();

                object.put(
                        "id",
                        item.getId()
                );

                object.put(
                        "loanType",
                        item.getLoanType()
                );

                object.put(
                        "date",
                        item.getDate()
                );

                object.put(
                        "loanAmount",
                        item.getLoanAmount()
                );

                object.put(
                        "interestRate",
                        item.getInterestRate()
                );

                object.put(
                        "loanTerm",
                        item.getLoanTerm()
                );

                object.put(
                        "loanTermUnit",
                        item.getLoanTermUnit()
                );

                object.put(
                        "totalMonths",
                        item.getTotalMonths()
                );

                object.put(
                        "monthlyEmi",
                        item.getMonthlyEmi()
                );

                object.put(
                        "totalInterest",
                        item.getTotalInterest()
                );

                object.put(
                        "totalPayment",
                        item.getTotalPayment()
                );

                object.put(
                        "startDate",
                        item.getStartDate()
                );

                object.put(
                        "currencyCode",
                        item.getCurrencyCode()
                );

                object.put(
                        "currencySymbol",
                        item.getCurrencySymbol()
                );

                object.put(
                        "iconRes",
                        item.getIconRes()
                );

                jsonArray.put(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        preferences.edit()
                .putString(
                        KEY_HISTORY,
                        jsonArray.toString()
                )
                .apply();
    }


    // =========================================================
    // DELETE ONE
    // =========================================================

    public void deleteHistory(long id) {

        List<HistoryItem> historyList = getHistory();

        for (int i = historyList.size() - 1; i >= 0; i--) {

            if (historyList.get(i).getId() == id) {

                historyList.remove(i);
            }
        }

        saveHistory(historyList);
    }


    // =========================================================
    // DELETE ALL
    // =========================================================

    public void deleteAllHistory() {

        preferences.edit()
                .remove(KEY_HISTORY)
                .apply();
    }


    // =========================================================
    // DELETE SELECTED
    // =========================================================

    public void deleteSelected(List<Long> selectedIds) {

        List<HistoryItem> historyList = getHistory();

        for (int i = historyList.size() - 1; i >= 0; i--) {

            if (selectedIds.contains(
                    historyList.get(i).getId()
            )) {

                historyList.remove(i);
            }
        }

        saveHistory(historyList);
    }
}