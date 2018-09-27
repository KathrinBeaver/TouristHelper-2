package com.hse.touristhelper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hse.touristhelper.text_mining.TextMiningManager;
import com.hse.touristhelper.translation.online.RetrofitManager;

import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by Alex on 30.03.2016.
 */
public class App extends Application {
    public static SharedPreferences prefs;

    public static final String PREF_LAST_MODE_TITLE = "lastModeTitle";
    public static final String PREF_WIZARD_FINISH = "wizardFinished";

    private static Context sSelf;

    private TextMiningManager mMiningManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Locale.setDefault(Locale.ENGLISH);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfiguration);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        sSelf = getApplicationContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                mMiningManager = TextMiningManager.getInstance(getAppContext());
                RetrofitManager.getInstance();
            }
        }).start();

    }

    public static Context getAppContext() {
        return sSelf;
    }

    public static boolean isWizardFinished() {
        return prefs.getBoolean(PREF_WIZARD_FINISH, false);
    }

    public static void setWizardFinished(boolean finished) {
        prefs.edit().putBoolean(PREF_WIZARD_FINISH, finished).apply();
    }

    public TextMiningManager getMiningManager() {
        return mMiningManager;
    }
}
