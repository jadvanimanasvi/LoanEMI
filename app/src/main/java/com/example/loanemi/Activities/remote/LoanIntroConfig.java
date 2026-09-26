package com.example.loanemi.Activities.remote;


import com.example.loanemi.Activities.utils.AppPreference;

public class LoanIntroConfig {

    public int btnNext_bottom_space = 10;
    public int get_started_bottom_space = 10;
    public boolean show_get_started_btn = true;
    public boolean is_splash_banner_show = true;
    public boolean is_splash_inter_show = true;
    public boolean is_language_native1_show = true;
    public boolean is_language_native2_show = true;
    public boolean is_language_done_native_show = true;
    public boolean is_language_done_inter_show = true;
    public boolean is_language_done_new_native_ui_show = true;
    public boolean is_first_intro_native_show = true;
    public boolean is_intro_full_screen_native_1_show = true;
    public boolean is_intro_full_screen_native_2_show = true;
    public boolean is_intro_full_screen_native_1_new_ui_show = true;
    public boolean is_intro_full_screen_native_2_new_ui_show = true;
    public boolean is_get_started_native_show = true;
    public boolean is_first_intro_new_next_btn_show = true;
    public int full_native_skip_timer = 4;
    public int full_native_close_timer = 1;
    public double is_rev_back_to = 0.5;

    public LoanIntroConfig() {
    }

    public double getIs_rev_back_to() {
        if (is_rev_back_to > 0) {
            return is_rev_back_to;
        }
        return 0.5d;
    }

    public int getBtnNext_bottom_space() {
        return btnNext_bottom_space;
    }

    public void setBtnNext_bottom_space(int btnNext_bottom_space) {
        this.btnNext_bottom_space = btnNext_bottom_space;
    }

    public int getGet_started_bottom_space() {
        return get_started_bottom_space;
    }

    public void setGet_started_bottom_space(int get_started_bottom_space) {
        this.get_started_bottom_space = get_started_bottom_space;
    }

    public boolean isShow_get_started_btn() {
        return show_get_started_btn;
    }

    public void setShow_get_started_btn(boolean show_get_started_btn) {
        this.show_get_started_btn = show_get_started_btn;
    }

    public boolean readToggle(String key, boolean fallback) {
        if (key == null) {
            return fallback;
        }
        if (key.equals(AppPreference.is_splash_banner_show)) {
            return is_splash_banner_show;
        }
        if (key.equals(AppPreference.is_splash_inter_show)) {
            return is_splash_inter_show;
        }
        if (key.equals(AppPreference.is_language_native1_show)) {
            return is_language_native1_show;
        }
        if (key.equals(AppPreference.is_language_native2_show)) {
            return is_language_native2_show;
        }
        if (key.equals(AppPreference.is_language_done_native_show)) {
            return is_language_done_native_show;
        }
        if (key.equals(AppPreference.is_language_done_inter_show)) {
            return is_language_done_inter_show;
        }
        if (key.equals(AppPreference.is_language_done_new_native_ui_show)) {
            return is_language_done_new_native_ui_show;
        }
        if (key.equals(AppPreference.is_first_intro_native_show)) {
            return is_first_intro_native_show;
        }
        if (key.equals(AppPreference.is_intro_full_screen_native_1_show)) {
            return is_intro_full_screen_native_1_show;
        }
        if (key.equals(AppPreference.is_intro_full_screen_native_2_show)) {
            return is_intro_full_screen_native_2_show;
        }
        if (key.equals(AppPreference.is_intro_full_screen_native_1_new_ui_show)) {
            return is_intro_full_screen_native_1_new_ui_show;
        }
        if (key.equals(AppPreference.is_intro_full_screen_native_2_new_ui_show)) {
            return is_intro_full_screen_native_2_new_ui_show;
        }
        if (key.equals(AppPreference.is_get_started_native_show)) {
            return is_get_started_native_show;
        }
        if (key.equals(AppPreference.is_first_intro_new_next_btn_show)) {
            return is_first_intro_new_next_btn_show;
        }
        if (key.equals(AppPreference.show_get_started_btn)) {
            return show_get_started_btn;
        }
        return fallback;
    }

    public int readGap(String key, int fallback) {
        if (key == null) {
            return fallback;
        }
        if (key.equals(AppPreference.btnNext_bottom_space)) {
            return btnNext_bottom_space;
        }
        if (key.equals(AppPreference.get_started_bottom_space)) {
            return get_started_bottom_space;
        }
        if (key.equals(AppPreference.full_native_skip_timer)) {
            return full_native_skip_timer;
        }
        if (key.equals(AppPreference.full_native_close_timer)) {
            return full_native_close_timer;
        }
        return fallback;
    }

    public void applyToggle(String key, boolean value) {
        if (key == null) {
            return;
        }
        if (key.equals(AppPreference.is_splash_banner_show)) {
            is_splash_banner_show = value;
        } else if (key.equals(AppPreference.is_splash_inter_show)) {
            is_splash_inter_show = value;
        } else if (key.equals(AppPreference.is_language_native1_show)) {
            is_language_native1_show = value;
        } else if (key.equals(AppPreference.is_language_native2_show)) {
            is_language_native2_show = value;
        } else if (key.equals(AppPreference.is_language_done_native_show)) {
            is_language_done_native_show = value;
        } else if (key.equals(AppPreference.is_language_done_inter_show)) {
            is_language_done_inter_show = value;
        } else if (key.equals(AppPreference.is_language_done_new_native_ui_show)) {
            is_language_done_new_native_ui_show = value;
        } else if (key.equals(AppPreference.is_first_intro_native_show)) {
            is_first_intro_native_show = value;
        } else if (key.equals(AppPreference.is_intro_full_screen_native_1_show)) {
            is_intro_full_screen_native_1_show = value;
        } else if (key.equals(AppPreference.is_intro_full_screen_native_2_show)) {
            is_intro_full_screen_native_2_show = value;
        } else if (key.equals(AppPreference.is_intro_full_screen_native_1_new_ui_show)) {
            is_intro_full_screen_native_1_new_ui_show = value;
        } else if (key.equals(AppPreference.is_intro_full_screen_native_2_new_ui_show)) {
            is_intro_full_screen_native_2_new_ui_show = value;
        } else if (key.equals(AppPreference.is_get_started_native_show)) {
            is_get_started_native_show = value;
        } else if (key.equals(AppPreference.is_first_intro_new_next_btn_show)) {
            is_first_intro_new_next_btn_show = value;
        } else if (key.equals(AppPreference.show_get_started_btn)) {
            show_get_started_btn = value;
        }
    }

    public void applyGap(String key, int value) {
        if (key == null) {
            return;
        }
        if (key.equals(AppPreference.btnNext_bottom_space)) {
            btnNext_bottom_space = value;
        } else if (key.equals(AppPreference.get_started_bottom_space)) {
            get_started_bottom_space = value;
        } else if (key.equals(AppPreference.full_native_skip_timer)) {
            full_native_skip_timer = value;
        } else if (key.equals(AppPreference.full_native_close_timer)) {
            full_native_close_timer = value;
        }
    }
}
