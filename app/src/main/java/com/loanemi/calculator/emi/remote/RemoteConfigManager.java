package com.loanemi.calculator.emi.remote;

import android.content.Context;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loanemi.calculator.emi.R;
import com.loanemi.calculator.emi.utils.Constant;
import com.loanemi.calculator.emi.utils.MyApplication;
import com.loanemi.calculator.emi.utils.PrefsUtil;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Remote Config for the start flow only (splash -> language -> intro -> Get Started).
 * Same logic as the Calculator Vault app, without the interstitial config.
 *
 * Constant.LOAN_INTRO_CONFIG must equal the parameter name in the Firebase console
 * (and the <key> in res/xml/remote_config_defaults.xml).
 */
public class RemoteConfigManager {

    private static final String TAG = "RC_DEBUG";

    // 0 = always fetch (testing). Use 3600 for release to avoid throttling.
    private static final long FETCH_INTERVAL_SECONDS = 0;

    private static RemoteConfigManager instance;
    private final FirebaseRemoteConfig remoteConfig;
    public LoanIntroConfig loanIntroConfig;

    private static final String[] START_TOGGLE_KEYS = {
            PrefsUtil.is_splash_banner_show,
            PrefsUtil.is_splash_inter_show,
            PrefsUtil.is_language_native1_show,
            PrefsUtil.is_language_native2_show,
            PrefsUtil.is_language_done_native_show,
            PrefsUtil.is_language_done_inter_show,
            PrefsUtil.is_language_done_new_native_ui_show,
            PrefsUtil.is_first_intro_native1_show,
            PrefsUtil.is_first_intro_native2_show,
            PrefsUtil.is_intro_full_screen_native_1_show,
            PrefsUtil.is_intro_full_screen_native_2_show,
            PrefsUtil.is_intro_full_screen_native_1_new_ui_show,
            PrefsUtil.is_intro_full_screen_native_2_new_ui_show,
            PrefsUtil.is_get_started_native_show,
            PrefsUtil.is_first_intro_new_next_btn_show,
            PrefsUtil.show_get_started_btn
    };

    private static final String[] START_GAP_KEYS = {
            PrefsUtil.btnNext_bottom_space,
            PrefsUtil.get_started_bottom_space,
            PrefsUtil.full_native_skip_timer,
            PrefsUtil.full_native_close_timer
    };

    private RemoteConfigManager() {
        remoteConfig = FirebaseRemoteConfig.getInstance();
    }

    public static synchronized RemoteConfigManager getInstance() {
        if (instance == null) {
            instance = new RemoteConfigManager();
        }
        return instance;
    }

    public void init() {
        FirebaseRemoteConfigSettings settings =
                new FirebaseRemoteConfigSettings.Builder()
                        .setMinimumFetchIntervalInSeconds(FETCH_INTERVAL_SECONDS)
                        .build();

        remoteConfig.setConfigSettingsAsync(settings);
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
                .addOnCompleteListener(t -> Log.d(TAG, "defaults loaded: " + t.isSuccessful()
                        + (t.getException() != null ? " err=" + t.getException() : "")));
        if (loanIntroConfig == null) {
            loanIntroConfig = new LoanIntroConfig();
        }
    }

    public void fetchAppConfig(ConfigCallback callback) {
        Log.d(TAG, "fetchAppConfig: START");

        remoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            Log.d(TAG, "fetchAppConfig: success=" + task.isSuccessful()
                    + " lastFetchStatus=" + remoteConfig.getInfo().getLastFetchStatus());
            if (!task.isSuccessful()) {
                Log.e(TAG, "fetchAppConfig: FAILED", task.getException());
            }
            Log.d(TAG, "fetchAppConfig: " + Constant.LOAN_INTRO_CONFIG + " = ["
                    + remoteConfig.getString(Constant.LOAN_INTRO_CONFIG) + "]");

            parseAllConfigs();
            logFinalFlags("after fetch");

            if (callback != null) {
                callback.onComplete();
            }
        });
    }

    private void parseAllConfigs() {
        LoanIntroConfig parsedIntro = parseConfig(Constant.LOAN_INTRO_CONFIG, LoanIntroConfig.class);
        if (parsedIntro == null) {
            Log.w(TAG, "parseAllConfigs: '" + Constant.LOAN_INTRO_CONFIG
                    + "' missing or invalid JSON, keeping previous/default values");
            parsedIntro = loanIntroConfig != null ? loanIntroConfig : new LoanIntroConfig();
        }
        overlayTopLevelStartKeys(parsedIntro);
        loanIntroConfig = parsedIntro;

        Gson gson = new Gson();

        if (loanIntroConfig != null && MyApplication.getApplication() != null) {
            PrefsUtil.saveIntroConfig(MyApplication.getApplication(), gson.toJson(loanIntroConfig));
            stampIntroIntoPrefs(MyApplication.getApplication(), loanIntroConfig);
        }
    }

    /**
     * Console may send start flags either inside the intro JSON or as top-level keys.
     */
    private void overlayTopLevelStartKeys(LoanIntroConfig target) {
        if (target == null) {
            return;
        }
        for (String key : START_TOGGLE_KEYS) {
            if (remoteConfig.getAll().containsKey(key)) {
                target.applyToggle(key, decodeRemoteToggle(key, target.readToggle(key, true)));
            }
        }
        for (String key : START_GAP_KEYS) {
            if (remoteConfig.getAll().containsKey(key)) {
                target.applyGap(key, decodeRemoteInt(key, target.readGap(key, 0)));
            }
        }
    }

    private boolean decodeRemoteToggle(String key, boolean fallback) {
        try {
            String raw = remoteConfig.getString(key);
            if (raw != null) {
                String trimmed = raw.trim();
                if (!trimmed.isEmpty()) {
                    if ("true".equalsIgnoreCase(trimmed) || "1".equals(trimmed) || "yes".equalsIgnoreCase(trimmed)) {
                        return true;
                    }
                    if ("false".equalsIgnoreCase(trimmed) || "0".equals(trimmed) || "no".equalsIgnoreCase(trimmed)) {
                        return false;
                    }
                }
            }
        } catch (Exception ignored) {
        }
        try {
            return remoteConfig.getBoolean(key);
        } catch (Exception e) {
            return fallback;
        }
    }

    private int decodeRemoteInt(String key, int fallback) {
        try {
            String raw = remoteConfig.getString(key);
            if (raw != null && !raw.trim().isEmpty()) {
                return Integer.parseInt(raw.trim());
            }
        } catch (Exception ignored) {
        }
        try {
            long asLong = remoteConfig.getLong(key);
            if (asLong != 0 || remoteConfig.getAll().containsKey(key)) {
                return (int) asLong;
            }
        } catch (Exception ignored) {
        }
        return fallback;
    }

    public void stampIntroIntoPrefs(Context context, LoanIntroConfig config) {
        if (context == null || config == null) {
            return;
        }
        PrefsUtil prefs = PrefsUtil.getInstance(context);
        if (prefs == null) {
            return;
        }
        for (String key : START_TOGGLE_KEYS) {
            prefs.setBoolean(key, config.readToggle(key, true));
        }
        for (String key : START_GAP_KEYS) {
            prefs.setInt(key, config.readGap(key, 0));
        }
    }

    public void loadCachedConfig(Context context) {
        Gson gson = new Gson();

        if (loanIntroConfig == null) {
            String data = PrefsUtil.getLoanIntroConfig(context);
            if (data != null) {
                try {
                    loanIntroConfig = gson.fromJson(data, LoanIntroConfig.class);
                } catch (Exception e) {
                    Log.e(TAG, "loadCachedConfig: bad cached JSON", e);
                }
            }
        }
        LoanIntroConfig fromRemote = parseConfig(Constant.LOAN_INTRO_CONFIG, LoanIntroConfig.class);
        if (fromRemote != null) {
            loanIntroConfig = fromRemote;
        }
        if (loanIntroConfig == null) {
            loanIntroConfig = new LoanIntroConfig();
        }
        overlayTopLevelStartKeys(loanIntroConfig);
        stampIntroIntoPrefs(context, loanIntroConfig);
        logFinalFlags("cached");
    }

    public void refreshStartFlowFlags(Context context) {
        if (context == null) {
            return;
        }
        if (loanIntroConfig == null) {
            loadCachedConfig(context);
            return;
        }
        overlayTopLevelStartKeys(loanIntroConfig);
        stampIntroIntoPrefs(context, loanIntroConfig);
    }

    public LoanIntroConfig requireIntroConfig() {
        if (loanIntroConfig == null) {
            loanIntroConfig = new LoanIntroConfig();
        }
        return loanIntroConfig;
    }

    /** Convenience: current value of a start-flow toggle (use PrefsUtil.is_... keys). */
    public boolean flag(String key, boolean fallback) {
        return requireIntroConfig().readToggle(key, fallback);
    }

    public void logFinalFlags(String stage) {
        LoanIntroConfig c = requireIntroConfig();
        for (String key : START_TOGGLE_KEYS) {
            Log.d(TAG, "[" + stage + "] FLAG " + key + " = " + c.readToggle(key, true));
        }
    }

    private <T> T parseConfig(String key, Class<T> clazz) {
        try {
            String json = remoteConfig.getString(key);
            if (json == null || json.isEmpty()) return null;

            Type type = TypeToken.getParameterized(List.class, clazz).getType();
            List<T> list = new Gson().fromJson(json, type);

            return (list != null && !list.isEmpty()) ? list.get(0) : null;

        } catch (Exception e) {
            Log.e(TAG, "parseConfig: invalid JSON for '" + key + "'", e);
            return null;
        }
    }

    public interface ConfigCallback {
        void onComplete();
    }
}