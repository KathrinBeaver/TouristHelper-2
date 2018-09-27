package com.hse.touristhelper.translation.online.model;

/**
 * Created by Alex on 05.03.2016.
 */
public enum YandexResponses {

    OK(200),
    INVALID_API_KEY(401),
    BLOCKED_API_KEY(402),
    EXCEED_NUMBER_REQUEST_PER_DAY(403),
    EXCEED_DATA_PER_DAY(404),
    TOO_BIG_TEXT(413),
    NO_WAY_TO_TRANSLATE(422),
    LANGUAGES_NOT_SUPPORTED(501);

    private int mResponseCode;

    private YandexResponses(int responseCode) {
        mResponseCode = responseCode;
    }

    public int getResponseCode() {
        return mResponseCode;
    }
}
