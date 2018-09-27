package com.hse.touristhelper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Alex on 07.05.2016.
 */
public class InternetConnectionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isConnected = activeNetInfo != null && activeNetInfo.isConnected();

        boolean isDisconnected = activeNetInfo == null || (activeNetInfo != null && activeNetInfo.isConnectedOrConnecting());
        if (isConnected) {
            Toast.makeText(context, "Online mode enabled", Toast.LENGTH_LONG).show();
        } else if (isDisconnected) {
            Toast.makeText(context, "Offline mode enabled", Toast.LENGTH_LONG).show();
        }
    }
}
