package com.example.loanemi.Activities.Activities;

import static com.example.loanemi.Activities.utils.Util.setupEdgeToEdge;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loanemi.Activities.Adapters.CurrencyAdapter;
import com.example.loanemi.Activities.Ads.AdsHelper;
import com.example.loanemi.Activities.Models.CurrencyItem;
import com.example.loanemi.Activities.utils.Util;
import com.example.loanemi.R;
import com.google.android.libraries.ads.mobile.sdk.banner.AdView;

import java.util.ArrayList;
import java.util.List;

public class CurrencyUnitActivity extends AppCompatActivity {

    public static final String EXTRA_SELECTED_CODE = "extra_selected_code";
    public static final String EXTRA_SELECTED_COUNTRY = "extra_selected_country";
    public static final String EXTRA_SELECTED_CURRENCY_ICON = "extra_selected_currency_icon";
    public static final String EXTRA_SELECTED_FLAG = "extra_selected_flag";
    public static final String EXTRA_CURRENT_CODE = "extra_current_code";
    public static final String EXTRA_CURRENCY_SELECT_TYPE = "currency_select_type";

    private CurrencyAdapter adapter;
    private CurrencyItem selectedItem;

    private AdView bannerAdView;
    private FrameLayout adContainer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_currency_unit);

        setupEdgeToEdge(this, R.id.main);

        List<CurrencyItem> currencies = buildCurrencyList();

        String currentCode = getIntent().getStringExtra(EXTRA_CURRENT_CODE);

        if (currentCode == null || currentCode.trim().isEmpty()) {
            currentCode = getIntent().getStringExtra(EXTRA_SELECTED_CODE);
        }

        int initialPosition = findPositionByCode(currencies, currentCode);

        if (initialPosition >= 0) {
            selectedItem = currencies.get(initialPosition);
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerCurrency);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CurrencyAdapter(currencies, initialPosition, (item, position) -> {
            selectedItem = item;
        });

        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnConfirm).setOnClickListener(v -> confirmSelection());

        adContainer = findViewById(R.id.bannerContainer);

        setUpAd();
    }

    private void setUpAd() {

        if (!Util.isInternetAvailable(this) || adContainer == null) {
            return;
        }

        bannerAdView = new AdView(this);

        if (bannerAdView.getParent() != null) {
            ((ViewGroup) bannerAdView.getParent()).removeView(bannerAdView);
        }

        adContainer.addView(bannerAdView);

        AdsHelper.loadAdaptiveBanner(bannerAdView, this, getString(R.string.currency_unit_banner));
    }

    private void confirmSelection() {

        if (selectedItem == null) {
            return;
        }

        Intent result = new Intent();

        // Currency Code
        result.putExtra(EXTRA_SELECTED_CODE, selectedItem.getCode());

        // Currency Name
        result.putExtra(EXTRA_SELECTED_COUNTRY, selectedItem.getName());

        // Currency Icon
        result.putExtra(EXTRA_SELECTED_CURRENCY_ICON, selectedItem.getCurrencyIconResId());

        // Country Flag
        result.putExtra(EXTRA_SELECTED_FLAG, selectedItem.getFlagResId());

        // Selection Type
        String selectType = getIntent().getStringExtra(EXTRA_CURRENCY_SELECT_TYPE);

        result.putExtra(EXTRA_CURRENCY_SELECT_TYPE, selectType);

        setResult(RESULT_OK, result);

        finish();
    }

    private int findPositionByCode(List<CurrencyItem> list, String code) {

        if (code == null || code.trim().isEmpty()) {
            return -1;
        }

        for (int i = 0; i < list.size(); i++) {

            CurrencyItem item = list.get(i);

            if (item.getCode().equalsIgnoreCase(code)) {
                return i;
            }
        }

        return -1;
    }

    private List<CurrencyItem> buildCurrencyList() {

        List<CurrencyItem> list = new ArrayList<>();

        list.add(new CurrencyItem("GBP", "UK Pound", R.drawable.gbp_currency, R.drawable.icn_flagengland));

        list.add(new CurrencyItem("USD", "US Dollar", R.drawable.usd_currency, R.drawable.icn_flagus));

        list.add(new CurrencyItem("CNY", "Chinese Yuan", R.drawable.cny_currency, R.drawable.icn_flagchinese));

        list.add(new CurrencyItem("INR", "Indian Rupee", R.drawable.inr_currency, R.drawable.icn_flaghindi));

        list.add(new CurrencyItem("VND", "Vietnamese Dong", R.drawable.vnd_currency, R.drawable.icn_flagvietnamese));

        list.add(new CurrencyItem("THB", "Thai Baht", R.drawable.thb_currency, R.drawable.icn_flagthailand));

        list.add(new CurrencyItem("IDR", "Indonesian Rupiah", R.drawable.idr_currency, R.drawable.icn_flagindonesia));

        return list;
    }

    @Override
    protected void onResume() {
        super.onResume();

        Util.hide(this);
    }
}