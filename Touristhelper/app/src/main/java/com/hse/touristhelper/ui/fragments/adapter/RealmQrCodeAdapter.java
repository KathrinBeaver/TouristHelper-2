package com.hse.touristhelper.ui.fragments.adapter;

import android.content.Context;

import com.hse.touristhelper.qr.storage.QRCodeObject;

import io.realm.RealmResults;

/**
 * Created by Alex on 10.05.2016.
 */
public class RealmQrCodeAdapter extends RealmModelAdapter<QRCodeObject> {
    public RealmQrCodeAdapter(Context context, RealmResults<QRCodeObject> realmResults, boolean automaticUpdate) {
        super(context, realmResults, automaticUpdate);
    }
}
