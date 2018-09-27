package com.hse.touristhelper.translation.online.service;

import com.hse.touristhelper.translation.online.model.TranslateResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Alex on 05.03.2016.
 */
public interface YandexService {
    @GET("api/v1.5/tr.json/translate")
    Call<TranslateResponse> translate(@Query("key") String key, @Query("text") String text, @Query("lang") String lang);
}
