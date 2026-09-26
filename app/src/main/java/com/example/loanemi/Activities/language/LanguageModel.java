package com.example.loanemi.Activities.language;

public class LanguageModel {
    public int image;
    public String languageName;
    public String languageCode;
    public boolean is_Selected;


    public LanguageModel(int image, String languageName, String languageCode) {
        this.image = image;
        this.languageName = languageName;
        this.languageCode = languageCode;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public void setIs_Selected(boolean z) {
        this.is_Selected = z;
    }
}
