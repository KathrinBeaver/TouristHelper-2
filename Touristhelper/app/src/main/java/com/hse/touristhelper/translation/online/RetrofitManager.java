package com.hse.touristhelper.translation.online;

import android.util.Log;

import com.hse.touristhelper.translation.TranslationCallback;
import com.hse.touristhelper.translation.TranslationInterface;
import com.hse.touristhelper.translation.TranslationObserver;
import com.hse.touristhelper.translation.online.model.TranslateResponse;
import com.hse.touristhelper.translation.online.service.YandexService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Alex on 05.03.2016.
 */
public class RetrofitManager implements TranslationInterface, TranslationObserver {
    public static String YANDEX_TRANSLAE_API_KEY = "trnsl.1.1.20160305T114223Z.bc9e928619f89577.0da3c5e2814d7ac3c13862359425405f5500c6a6";
    public static String BASE_URL = "https://translate.yandex.net";

    private static final String TAG = RetrofitManager.class.getSimpleName();

    private static volatile RetrofitManager sSelf;
    private static Object mLock = new Object();

    private Retrofit mRetrofit;
    private YandexService mApiService;
    private List<TranslationCallback> mCallback;

    private RetrofitManager() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApiService = mRetrofit.create(YandexService.class);

        mCallback = new ArrayList<>();
    }

    public static RetrofitManager getInstance() {
        if (sSelf != null) {
            return sSelf;
        } else {
            synchronized (mLock) {
                sSelf = new RetrofitManager();
                return sSelf;
            }
        }
    }

    @Override
    public void translate(final String text, String targetLang) {
        Call<TranslateResponse> response = mApiService.translate(YANDEX_TRANSLAE_API_KEY, text, targetLang);
        response.enqueue(new Callback<TranslateResponse>() {
            @Override
            public void onResponse(Call<TranslateResponse> call, Response<TranslateResponse> response) {
                Log.e(TAG, response.message());
                String[] textAr = response.body().getText();
                if (textAr != null && textAr.length >= 1) {
                    notifyTranslationListeners(textAr[0]);
                }
            }

            @Override
            public void onFailure(Call<TranslateResponse> call, Throwable t) {
                Log.e(TAG, t.toString());
            }
        });
    }

    public void translate(String text) {
        // stub
    }

    @Override
    public void addListener(TranslationCallback callback) {
        mCallback.add(callback);
    }

    @Override
    public void removeListener(TranslationCallback callback) {
        mCallback.remove(callback);
    }

    @Override
    public void removeAllListener() {
        mCallback.clear();
    }

    @Override
    public void notifyTranslationListeners(String tr) {
        Log.e(TAG, "notifyListeners");
        for (TranslationCallback callback : mCallback) {
            Log.e(TAG, callback.toString());
            callback.onTranslationSucceed(tr);
        }
    }

}
