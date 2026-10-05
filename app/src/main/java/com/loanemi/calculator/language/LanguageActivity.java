package com.loanemi.calculator.language;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.loanemi.R;
import com.example.loanemi.databinding.ActivityLanguageBinding;
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError;
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError;
import com.loanemi.calculator.Ads.AdCallback;
import com.loanemi.calculator.Ads.AdConfig;
import com.loanemi.calculator.Ads.ApNativeAd;
import com.loanemi.calculator.BaseActivity;
import com.loanemi.calculator.introflow.AllScreenIntro;
import com.loanemi.calculator.introflow.GetStartActivity;
import com.loanemi.calculator.introflow.LoanActivityTracker;
import com.loanemi.calculator.introflow.LoanStartFlowAdHelper;
import com.loanemi.calculator.utils.AppPreference;
import com.loanemi.calculator.utils.Constant;
import com.loanemi.calculator.utils.MyApplication;
import com.loanemi.calculator.utils.Util;

import java.util.ArrayList;


public class LanguageActivity extends BaseActivity {

    public int languagePosition = -1;
    ActivityLanguageBinding binding;
    boolean isLanguageSelected = false;
    boolean isLangClick = false;
    FrameLayout ivDone;
    ImageView ivBack;
    boolean fromSettings;
    ArrayList<LanguageModel> languageModels = new ArrayList<>();
    RecyclerView rvLanguage;
    AppPreference zwePreferences;
    android.widget.Button cta;
    private boolean languageNativeTwoShown;
    private boolean isDoneClicked = false;
    private boolean splashInterShown = false;
    private final Handler splashInterHandler = new Handler(Looper.getMainLooper());
    private Runnable splashInterShowRunnable;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupEdgeToEdge(binding.main);
        zwePreferences = AppPreference.Companion.getInstance(this);
        LoanActivityTracker.setCurrentActivity(this);
        MyApplication.instance.applyStartFlowSystemBars(this, findViewById(R.id.main));
        fromSettings = getIntent() != null
                && getIntent().getBooleanExtra(
                AppPreference.EXTRA_LANG_OPENED_IN_SETTINGS,
                false
        );

        isLangClick = false;
        bindViews();
        initView();
        addListener();

        if (fromSettings) {

           binding.ivBack.setVisibility(View.VISIBLE);
            ivDone.setVisibility(View.VISIBLE);
            showNativeOne();
        } else {
            binding.ivBack.setVisibility(View.GONE);
            showNativeOne();
        }

//        showNativeOne();

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {
                        if (fromSettings) {
                            Constant.isLanguageChanging = false;
                        }

                        finish();
                    }
                }
        );

        // Only normal first-time flow
        if (!fromSettings
                && LoanStartFlowAdHelper.isFlagOn(
                this,
                AppPreference.is_first_intro_native_show,
                true
        )) {
            preloadIntroFirst();
        }
    }


    private void bindViews() {
        ivBack = findViewById(R.id.ivBack);
        ivDone = findViewById(R.id.ivDone);
        rvLanguage = findViewById(R.id.rvLanguage);
    }

    public void initView() {
        languageModels.clear();
        languageModels.add(new LanguageModel(R.drawable.icn_flagengland, "English", "en-rGB"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagus, "English", "en"));
        languageModels.add(new LanguageModel(R.drawable.icn_flaghindi, "Hindi", "hi"));
        languageModels.add(new LanguageModel(R.drawable.icn_bangla, "Bangla", "bn"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagbrazil, "Portuguese", "pt-rBR"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagsa, "Saudi Arabia", "ar-rSA"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagtunisia, "Tunisia", "ar-rTN"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagoman, "Oman", "ar-rOM"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagindonesia, "Indonesian", "id"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagportugal, "Portuguese", "pt-rPT"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagspain, "Spanish", "es"));
        languageModels.add(new LanguageModel(R.drawable.icn_flaghindi, "Marathi", "mr"));
        languageModels.add(new LanguageModel(R.drawable.icn_flaghindi, "Tegulu", "te"));
        languageModels.add(new LanguageModel(R.drawable.icn_flaghindi, "Tamil", "ta"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagrussian, "Russian", "ru"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagfrance, "French", "fr"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagvietnamese, "Vietnamese", "vi"));
        languageModels.add(new LanguageModel(R.drawable.icn_flaggermany, "German", "de"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagkorean, "Korean", "ko"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagjapanese, "Japanese", "ja"));
        languageModels.add(new LanguageModel(R.drawable.icn_flag_turkey, "Turkish", "tr"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagchinese, "Chinese", "zh"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagitalian, "Italian", "it"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagthailand, "Thai", "th"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagdutch, "Dutch", "nl"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagdanish, "Danish", "da"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagiris, "Irish", "ga"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagpolish, "Polish", "pl"));
        languageModels.add(new LanguageModel(R.drawable.icn_flagzulu, "Zulu", "zu"));

        int deviceLanguagePosition = -1;
        for (int i = 0; i < languageModels.size(); i++) {
            if (languageModels.get(i).getLanguageCode().equalsIgnoreCase(MyApplication.getDeviceLocale())) {
                deviceLanguagePosition = i;
                break;
            }
        }
        if (deviceLanguagePosition != -1 && deviceLanguagePosition != 1) {
            LanguageModel model = languageModels.remove(deviceLanguagePosition);
            languageModels.add(Math.min(1, languageModels.size()), model);
        }

        binding.ivDone.setVisibility(View.GONE);
        Glide.with(this).load(R.drawable.single_tap).into(binding.lottieView);
        binding.lottieView.setVisibility(View.INVISIBLE);
        rvLanguage.setLayoutManager(new GridLayoutManager(this, 2));
        rvLanguage.setAdapter(new LanguageAdapter());
        if (getIntent() != null && getIntent().getBooleanExtra(AppPreference.EXTRA_LANG_OPENED_IN_SETTINGS, false)) {
            selectSavedLanguage();
        }
    }

    public void maybeShowSplashInterstitial() {
        if (splashInterShown) {
            return;
        }
        if (getIntent() != null && getIntent().getBooleanExtra(AppPreference.EXTRA_LANG_OPENED_IN_SETTINGS, false)) {
            return;
        }
        if (!getLifecycle().getCurrentState().isAtLeast(androidx.lifecycle.Lifecycle.State.RESUMED)) {
            return;
        }
        boolean splashInterOn = LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_splash_inter_show, true);
        if (splashInterOn && LoanStartFlowAdHelper.isInterstitialLoaded()) {
            splashInterShown = true;
            LoanStartFlowAdHelper.showLoadedInterstitialWithLoading(this, new AdCallback() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    LoanActivityTracker.setCurrentString("AdClose");
                }

                @Override
                public void onAdClicked() {
                    super.onAdClicked();
                    Constant.markLeavingForAd();
                    LoanActivityTracker.setCurrentString("AdClick");
                }
            });
        } else if (!splashInterOn) {
            LoanStartFlowAdHelper.discardPendingInterstitial();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (Util.isUserAdClicked && !AppPreference.getInstance(this).getBoolean(AppPreference.KEY_IS_USER, false)) {
                Util.isUserAdClicked = false;
                languagePosition = 1;
                for (int i = 0; i < languageModels.size(); i++) {
                    languageModels.get(i).setIs_Selected(i == 1);
                }
                saveLanguage();
                loadActivity();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        LoanStartFlowAdHelper.tryShowDeferredInterstitial();
        if (splashInterShowRunnable != null) {
            splashInterHandler.removeCallbacks(splashInterShowRunnable);
        }
        splashInterShowRunnable = () -> {
            splashInterShowRunnable = null;
            if (!isFinishing() && !isDestroyed()) {
                maybeShowSplashInterstitial();
            }
        };
        binding.getRoot().post(() -> splashInterHandler.postDelayed(splashInterShowRunnable, 350));
    }

    @Override
    protected void onDestroy() {
        if (splashInterShowRunnable != null) {
            splashInterHandler.removeCallbacks(splashInterShowRunnable);
            splashInterShowRunnable = null;
        }
        super.onDestroy();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void selectSavedLanguage() {
        String saved = zwePreferences.getPreferencesStr(AppPreference.SELECT_LAN, "en");
        for (LanguageModel model : languageModels) {
            model.setIs_Selected(false);
        }
        languagePosition = -1;
        isLanguageSelected = false;
        for (int i = 0; i < languageModels.size(); i++) {
            if (languageModels.get(i).getLanguageCode().equalsIgnoreCase(saved)) {
                languagePosition = i;
                languageModels.get(i).setIs_Selected(true);
                isLanguageSelected = true;
                binding.ivDone.setVisibility(View.VISIBLE);
                break;
            }
        }
        if (rvLanguage != null && rvLanguage.getAdapter() != null) {
            rvLanguage.getAdapter().notifyDataSetChanged();
        }
    }

    private void addListener() {
        ivDone.setOnClickListener(v -> onDoneClicked());
        binding.ivBack.setOnClickListener(v -> {
            Constant.isLanguageChanging = false;
            finish();
        });
    }

    public void showNativeOne() {
        if (!LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_language_native1_show, true) || !LoanStartFlowAdHelper.isNetworkConnected(this)) {
            hideLanguageNative();
            return;
        }
        MyApplication.instance.LanguageFirstNativeAd.observe(this, apNativeAd -> {
            if (isFinishing() || languageNativeTwoShown) {
                return;
            }
            if (apNativeAd != null) {
                AdConfig.getInstance().populateNativeAdView(this, apNativeAd, binding.layoutAdNative, binding.layouinclude.shimmerContainerNativeLarge, () -> {
                    Constant.markLeavingForAd();
                    Util.isUserAdClicked = true;
                }, nativeAdView -> cta = nativeAdView.findViewById(R.id.ad_call_to_action));
            } else {
                hideLanguageNative();
            }
        });
    }

    public void showNativeTwo() {
        if (!LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_language_native2_show, true) || !LoanStartFlowAdHelper.isNetworkConnected(this)) {
            return;
        }
        languageNativeTwoShown = true;
        MyApplication.instance.LanguageSecondNativeAd.observe(this, apNativeAd -> {
            if (isFinishing()) {
                return;
            }
            if (apNativeAd != null) {
                AdConfig.getInstance().populateNativeAdView(this, apNativeAd, binding.layoutAdNative, binding.layouinclude.shimmerContainerNativeLarge, () -> {
                    Constant.markLeavingForAd();
                    Util.isUserAdClicked = true;
                }, nativeAdView -> {
                    cta = nativeAdView.findViewById(R.id.ad_call_to_action);
                    if (cta != null) {
                        int color = ContextCompat.getColor(LanguageActivity.this, R.color.cta_btn_change_color);
                        cta.setBackgroundTintList(ColorStateList.valueOf(color));
                    }
                });
            }
        });
    }

    public void onDoneClicked() {
        try {
            if (isDoneClicked) {
                return;
            }
            if (languagePosition == -1) {
                Toast.makeText(this, R.string.please_select_language, Toast.LENGTH_SHORT).show();
                return;
            }
            isDoneClicked = true;
            saveLanguage();
            /*if (getIntent() != null && getIntent().getBooleanExtra(AppPreference.EXTRA_LANG_OPENED_IN_SETTINGS, false)) {
                setResult(RESULT_OK);
                finish();
                return;
            }*/
            if (fromSettings) {
                Constant.isLanguageChanging = false;
                setResult(RESULT_OK);
                finish();
                return;
            }
            LoanStartFlowAdHelper.markLanguageDoneAdsPending();
            LoanStartFlowAdHelper.discardPendingInterstitial();
            boolean online = LoanStartFlowAdHelper.isNetworkConnected(this);

            if (online && LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_language_done_inter_show, true)) {
                LoanStartFlowAdHelper.loadInterstitialOnly(
                        getApplicationContext(),
                        getString(R.string.done_inter_language_1),
                        getString(R.string.done_inter_language_2));
            }

            if (online && LoanStartFlowAdHelper.isFlagOn(this, AppPreference.is_language_done_native_show, true)) {
                startLanguageDoneNativeLoad();
            }
            loadActivity();
        } catch (Exception e) {
            loadActivity();
        }
    }

    private void startLanguageDoneNativeLoad() {
        int selectedLayout = LoanStartFlowAdHelper.NativeLayoutFull(this, AppPreference.is_language_done_new_native_ui_show);
        LoanStartFlowAdHelper.fetchStartFlowNative(this, getString(R.string.language_done_fullscreen_native_1), getString(R.string.language_done_fullscreen_native_2), selectedLayout, new AdCallback() {
            @Override
            public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                super.onNativeAdLoaded(apNativeAd);
                MyApplication.instance.FullScreenLanguageNativeAd.postValue(apNativeAd);
            }

            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Constant.markLeavingForAd();
                LoanActivityTracker.setCurrentString("AdClick");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                MyApplication.instance.FullScreenLanguageNativeAd.postValue(null);
            }
        });
    }


    private void saveLanguage() {
        if (languagePosition < 0 || languagePosition >= languageModels.size()) {
            return;
        }
        String selectedLanguage = languageModels.get(languagePosition).getLanguageCode();
        zwePreferences.setPreferencesStrCommit(AppPreference.SELECT_LAN, selectedLanguage);
        MyApplication.setLanguage(this, selectedLanguage);
    }

    private void loadActivity() {
        if (AppPreference.getInstance(this).getBoolean(AppPreference.KEY_IS_USER, false)) {
            startActivity(new Intent(this, GetStartActivity.class).putExtra(AppPreference.EXTRA_AFTER_LANG_PICK, true));
            finish();
            return;
        }
        Intent introPager = new Intent(this, AllScreenIntro.class);
        introPager.putExtra(AppPreference.EXTRA_AFTER_LANG_PICK, true);
        introPager.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        introPager.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(introPager);
        finish();
    }

    private void hideLanguageNative() {
        binding.layoutAdNative.setVisibility(View.INVISIBLE);
        binding.layouinclude.shimmerContainerNativeLarge.setVisibility(View.GONE);
    }

    private void preloadIntroFirst() {
        LoanStartFlowAdHelper.fetchStartFlowNative(this, getString(R.string.first_onboarding_native_1), getString(R.string.first_onboarding_native_2), R.layout.native_large_start_flow_top, new AdCallback() {
            @Override
            public void onNativeAdLoaded(ApNativeAd apNativeAd) {
                super.onNativeAdLoaded(apNativeAd);
                MyApplication.instance.FirstIntroNativeAd.postValue(apNativeAd);
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                MyApplication.instance.FirstIntroNativeAd.postValue(null);
            }

            @Override
            public void onAdFailedToShow(FullScreenContentError adError) {
                super.onAdFailedToShow(adError);
                MyApplication.instance.FirstIntroNativeAd.postValue(null);
            }
        });
    }

    private void onLanguageItemSelected(int i, LanguageAdapter.ItemHolder itemHolder) {
        isLangClick = true;
        binding.ivDone.setVisibility(View.VISIBLE);
        binding.lottieView.setVisibility(View.VISIBLE);
        for (int j = 0; j < languageModels.size(); j++) {
            languageModels.get(j).setIs_Selected(j == i);
        }
        itemHolder.viewCheck.setImageResource(R.drawable.ic_language_selected);
        itemHolder.rlMainLanguage.setSelected(true);
        if (!isLanguageSelected) {
            isLanguageSelected = true;
            if (cta != null) {
                int color = ContextCompat.getColor(this, R.color.cta_btn_change_color);
                cta.setBackgroundTintList(ColorStateList.valueOf(color));
            }
            showNativeTwo();
        }
        languagePosition = i;
    }

    public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ItemHolder> {

        @NonNull
        @Override
        public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(LanguageActivity.this).inflate(R.layout.item_language, parent, false);
            return new ItemHolder(view);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull final ItemHolder itemHolder, @SuppressLint("RecyclerView") final int i) {
            itemHolder.rlMainLanguage.setVisibility(View.VISIBLE);
            itemHolder.ivCountryImage.setImageResource(languageModels.get(i).getImage());
            itemHolder.tvLanguage.setText(languageModels.get(i).getLanguageName());
            if (languagePosition == -1 && i == 1) {
                Glide.with(LanguageActivity.this).load(R.drawable.single_tap).into(itemHolder.lottieAnimationView);
                itemHolder.lottieAnimationView.setVisibility(View.VISIBLE);
            } else {
                itemHolder.lottieAnimationView.setVisibility(View.INVISIBLE);
            }
            if (languagePosition == i) {
                itemHolder.viewCheck.setImageResource(R.drawable.ic_language_selected);
                itemHolder.rlMainLanguage.setSelected(true);
            } else {
                itemHolder.viewCheck.setImageResource(R.drawable.ic_language_unselected);
                itemHolder.rlMainLanguage.setSelected(false);
            }
            itemHolder.itemView.setOnClickListener(view -> {
                onLanguageItemSelected(i, itemHolder);
                notifyDataSetChanged();
            });
            itemHolder.viewCheck.setOnClickListener(v -> {
                onLanguageItemSelected(i, itemHolder);
                notifyDataSetChanged();
            });
        }

        @Override
        public int getItemCount() {
            return languageModels.size();
        }

        public class ItemHolder extends RecyclerView.ViewHolder {
            ImageView ivCountryImage;
            ImageView viewCheck;
            ImageView lottieAnimationView;
            ConstraintLayout rlMainLanguage;
            TextView tvLanguage;

            public ItemHolder(@NonNull View view) {
                super(view);
                ivCountryImage = view.findViewById(R.id.ivCountryImage);
                viewCheck = view.findViewById(R.id.viewCheck);
                lottieAnimationView = view.findViewById(R.id.lottieView);
                rlMainLanguage = view.findViewById(R.id.rlMainLanguage);
                tvLanguage = view.findViewById(R.id.tvLanguage);
            }
        }
    }
}
