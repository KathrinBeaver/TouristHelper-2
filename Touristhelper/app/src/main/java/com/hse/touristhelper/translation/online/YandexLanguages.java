package com.hse.touristhelper.translation.online;

/**
 * Created by Alex on 15.05.2016.
 */
public enum YandexLanguages {
    Azerbaijan("Azerbaijan", "az"),
    Albanian("Albanian", "sq"),
    English("English", "en"),
    Arabic("Arabic", "ar"),
    Armenian("Armenian", "hy"),
    Afrikaans("Afrikaans", "af"),
    Basque("Basque", "eu"),
    Bashkir("Bashkir", "ba"),
    Belarusian("Belarusian", "be"),
    Bulgarian("Bulgarian", "bg"),
    Bosnian("Bosnian", "bs"),
    Welsh("Welsh", "cy"),
    Hungarian("Hungarian", "hu"),
    Vietnamese("Vietnamese", "vi"),
    Haitian("Haitian (Creole)", "ht"),
    Galician("Galician", "gl"),
    Dutch("Dutch", "nl"),
    Greek("Greek", "el"),
    Georgian("Georgian", "ka"),
    Danish("Danish", "da"),
    Hebrew("Hebrew", "he"),
    Indonesian("Indonesian", "id"),
    Irish("Irish", "ga"),
    Italian("Italian", "it"),
    Icelandic("Icelandic", "is"),
    Spanish("Spanish", "es"),
    Kazakh("Kazakh", "kk"),
    Catalan("Catalan", "ca"),
    Kyrgyz("Kyrgyz", "ky"),
    Chinese("Chinese", "zh"),
    Korean("Korean", "ko"),
    Latin("Latin", "la"),
    Latvian("Latvian", "lv"),
    Lithuanian("Lithuanian", "lt"),
    Malagasy("Malagasy", "mg"),
    Malay("Malay", "ms"),
    Maltese("Maltese", "mt"),
    Macedonian("Macedonian", "mk"),
    Mongolian("Mongolian", "mn"),
    German("German", "de"),
    Norwegian("Norwegian", "no"),
    Persian("Persian", "fa"),
    Polish("Polish", "pl"),
    Portuguese("Portuguese", "pt"),
    Romanian("Romanian", "ro"),
    Russian("Russian", "ru"),
    Serbian("Serbian", "sr"),
    Slovakian("Slovakian", "sk"),
    Slovenian("Slovenian", "sl"),
    Swahili("Swahili", "sw"),
    Tajik("Tajik", "tg"),
    Thai("Thai", "th"),
    Tagalog("Tagalog", "tl"),
    Tatar("Tatar", "tt"),
    Turkish("Turkish", "tr"),
    Udmurt("Udmurt", "udm"),
    Uzbek("Uzbek", "uz"),
    Ukrainian("Ukrainian", "uk"),
    Urdu("Urdu", "ur"),
    Finish("Finish", "fi"),
    French("French", "fr"),
    Hindi("Hindi", "hi"),
    Croatian("Croatian", "hr"),
    Czech("Czech", "cs"),
    Swedish("Swedish", "sv"),
    Estonian("Estonian", "et"),
    Japanese("Japanese", "ja");


    private String title;
    private String shortTitle;

    YandexLanguages(String title, String shortTitle) {
        this.title = title;
        this.shortTitle = shortTitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    @Override
    public String toString() {
        return title;
    }
}
