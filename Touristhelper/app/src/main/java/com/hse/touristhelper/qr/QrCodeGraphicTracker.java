package com.hse.touristhelper.qr;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.hse.touristhelper.qr.camera.GraphicOverlay;

/**
 * Created by Alex on 14.02.2016.
 */
public class QrCodeGraphicTracker extends Tracker<Barcode> {
    private GraphicOverlay<QrCodeGraphic> mOverlay;
    private QrCodeGraphic mGraphic;

    public QrCodeGraphicTracker(GraphicOverlay<QrCodeGraphic> overlay, QrCodeGraphic graphic) {
        mOverlay = overlay;
        mGraphic = graphic;
    }

    @Override
    public void onNewItem(int id, Barcode item) {
        mGraphic.setId(id);
    }

    @Override
    public void onUpdate(Detector.Detections<Barcode> detectionResults, Barcode item) {
        mOverlay.add(mGraphic);
        mGraphic.updateItem(item);
    }

    @Override
    public void onMissing(Detector.Detections<Barcode> detectionResults) {
        mOverlay.remove(mGraphic);
    }

    @Override
    public void onDone() {
        mOverlay.remove(mGraphic);
    }
}

