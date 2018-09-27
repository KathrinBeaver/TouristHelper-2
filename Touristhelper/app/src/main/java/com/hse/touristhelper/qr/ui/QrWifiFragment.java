package com.hse.touristhelper.qr.ui;


import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.hse.touristhelper.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QrWifiFragment extends Fragment {

    private static final String KEY = "key_qr_wifi";

    public QrWifiFragment() {
        // Required empty public constructor
    }

    public static QrWifiFragment newInstance(Barcode barcode) {
        Bundle args = new Bundle();
        args.putParcelable(KEY, barcode);

        QrWifiFragment fragment = new QrWifiFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Barcode mBarcode = getArguments().getParcelable(KEY);
        View v = inflater.inflate(R.layout.fragment_qr_wifi, container, false);
        ((TextView)v.findViewById(R.id.textView)).setText("SSID:" + mBarcode.wifi.ssid);
        ((TextView)v.findViewById(R.id.textView2)).setText("Password:" + mBarcode.wifi.password);
        String type = "";
        switch (mBarcode.wifi.encryptionType) {
            case Barcode.WiFi.OPEN:
                type = "Open";
                break;
            case Barcode.WiFi.WPA:
                type = "WPA";
                break;
            case Barcode.WiFi.WEP:
                type = "WEP";
                break;
        }
        ((TextView)v.findViewById(R.id.textView3)).setText("Encryption Type:" + type);
        ((Button)v.findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WifiConfiguration wifiConfig = new WifiConfiguration();
                wifiConfig.SSID = String.format("\"%s\"", mBarcode.wifi.ssid);
                wifiConfig.preSharedKey = String.format("\"%s\"", mBarcode.wifi.password);

                WifiManager wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);

                int netId = wifiManager.addNetwork(wifiConfig);
                if (wifiManager.isWifiEnabled()) { //---wifi is turned on---
                    //---disconnect it first---
                    wifiManager.disconnect();
                } else { //---wifi is turned off---
                    //---turn on wifi---
                    wifiManager.setWifiEnabled(true);
                }
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();
            }
        });
        return v;
    }

}
