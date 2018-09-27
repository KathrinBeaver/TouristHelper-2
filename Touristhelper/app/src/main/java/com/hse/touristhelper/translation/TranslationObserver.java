package com.hse.touristhelper.translation;

/**
 * Created by Alex on 09.03.2016.
 */
public interface TranslationObserver {
    void addListener(TranslationCallback callback);

    void removeListener(TranslationCallback callback);

    void removeAllListener();

    void notifyTranslationListeners(String tr);
}
