package com.hse.touristhelper.translation;

/**
 * Created by Alex on 05.03.2016.
 */
public interface TranslationInterface {
    void translate(String text, String targetLang);

    void translate(String text) throws Exception;
}
