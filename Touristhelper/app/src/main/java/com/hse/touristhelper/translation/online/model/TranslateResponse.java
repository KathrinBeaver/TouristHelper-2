package com.hse.touristhelper.translation.online.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Alex on 05.03.2016.
 */
public class TranslateResponse {
    @SerializedName("code")
    private int mCode;

    @SerializedName("lang")
    private String mLang;

    @SerializedName("text")
    private String[] mText;

    public TranslateResponse(int code, String lang, String[] text) {
        mCode = code;
        mLang = lang;
        mText = text;
    }

    public int getResponseCode() {
        return mCode;
    }

    public String getLang() {
        return mLang;
    }

    public String[] getText() {
        return mText;
    }
}
