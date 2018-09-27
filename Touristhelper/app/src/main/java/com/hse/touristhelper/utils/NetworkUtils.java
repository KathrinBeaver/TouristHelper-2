package com.hse.touristhelper.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Alex on 28.03.2016.
 */
public class NetworkUtils {
    private final static String TAG = NetworkUtils.class.getSimpleName();

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nw = cm.getActiveNetworkInfo();
        return nw != null && nw.isConnectedOrConnecting();
    }
}
