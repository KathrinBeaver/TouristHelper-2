package com.hse.touristhelper.ui;

import com.google.android.gms.vision.barcode.Barcode;

/**
 * Created by Alex on 07.05.2016.
 */
public interface FragmentsCallback {
    void openWifiQrFragment(Barcode barcode);

    void openTextFragment(Barcode barcode);

    void openTextFragment(String barcode);

    void openQrUrlFragment(Barcode barcode);

    void openQrScannerFragment();

    void openCalendarEvent(Barcode barcode);

    void openTextToolFragment(String textTotranslate, boolean textMininingMode, boolean translateDisabled);

    void openLibsFragment();
}
