package com.hse.touristhelper.qr;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.hse.touristhelper.qr.camera.GraphicOverlay;

/**
 * Created by Alex on 14.02.2016.
 */
public class QrCodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay<QrCodeGraphic> mGraphicOverlay;

    public QrCodeTrackerFactory(GraphicOverlay<QrCodeGraphic> barcodeGraphicOverlay) {
        mGraphicOverlay = barcodeGraphicOverlay;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        QrCodeGraphic graphic = new QrCodeGraphic(mGraphicOverlay);
        return new QrCodeGraphicTracker(mGraphicOverlay, graphic);
    }

}
