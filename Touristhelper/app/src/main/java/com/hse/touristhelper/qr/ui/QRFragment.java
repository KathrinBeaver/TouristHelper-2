package com.hse.touristhelper.qr.ui;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.hse.touristhelper.R;
import com.hse.touristhelper.qr.QrCodeGraphic;
import com.hse.touristhelper.qr.QrCodeTrackerFactory;
import com.hse.touristhelper.qr.camera.GraphicOverlay;
import com.hse.touristhelper.qr.camera.SourceCamera;
import com.hse.touristhelper.qr.camera.SourcePreview;
import com.hse.touristhelper.ui.MainActivity;

import java.io.IOException;

public class QRFragment extends Fragment implements CheckBox.OnCheckedChangeListener {
    private static final String TAG = "QR-code-reader-fragment";

    private static final int RC_HANDLE_GOOGLE_SERVICES = 9001;
    private static final int RC_HANDLE_REQUEST_CAMERA_PERM = 3;

    public static final String BarcodeObject = "Barcode";

    private SourceCamera mCameraSource;
    private SourcePreview mSourcePreview;
    private GraphicOverlay<QrCodeGraphic> mGraphicOverlay;

    private GestureDetector mGesturesDetector;
    private ScaleGestureDetector mScaleGesturesDetector;

    private Switch mFocusSwitch;
    private Switch mLightSwitch;
    private String prevFocusMode;

    public QRFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onResume();
        createCamera(mLightSwitch.isChecked(), mFocusSwitch.isChecked());
        mGesturesDetector = new GestureDetector(this.getActivity(), new CaptureGestureListener());
        mScaleGesturesDetector = new ScaleGestureDetector(this.getActivity(), new ScaleListener());
        mSourcePreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean b = mScaleGesturesDetector.onTouchEvent(motionEvent);

                boolean c = mGesturesDetector.onTouchEvent(motionEvent);

                return b || c;
            }
        });
        settingLightFocus();
        startCameraSource();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mCameraSource != null) {
            mCameraSource.stop();
        }
    }

    private void startCameraSource() throws SecurityException {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                this.getActivity().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this.getActivity(), code, RC_HANDLE_GOOGLE_SERVICES);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mSourcePreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_qr, container, false);
        mLightSwitch = (Switch) v.findViewById(R.id.switchLight);
        mFocusSwitch = (Switch) v.findViewById(R.id.switchFocus);
        mLightSwitch.setOnCheckedChangeListener(this);
        mFocusSwitch.setOnCheckedChangeListener(this);
        mSourcePreview = (SourcePreview) v.findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<QrCodeGraphic>) v.findViewById(R.id.graphicOverlay);
        return v;
    }

    private void createCamera(boolean light, boolean focus) {
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(focus, light);
        } else {
            requestCameraPermission();
        }
    }

    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this.getActivity(), permissions, RC_HANDLE_REQUEST_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this.getActivity();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_REQUEST_CAMERA_PERM);
            }
        };

        // TODO
        /*Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();*/
    }

    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = this.getActivity().getApplicationContext();

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        QrCodeTrackerFactory barcodeFactory = new QrCodeTrackerFactory(mGraphicOverlay);
        barcodeDetector.setProcessor(
                new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = this.getActivity().registerReceiver(null, lowstorageFilter) != null;

            // TODO
            if (hasLowStorage) {
                //Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                // Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        SourceCamera.Builder builder = new SourceCamera.Builder(this.getActivity().getApplicationContext(), barcodeDetector)
                .setFacing(SourceCamera.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(15.0f);

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                .build();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private boolean onTap(float rawX, float rawY) {
        QrCodeGraphic graphic = mGraphicOverlay.getFirstGraphic();
        Barcode barcode = null;
        if (graphic != null) {
            barcode = graphic.getBarcode();
            if (barcode != null) {
                Intent data = new Intent(MainActivity.BROADCAST_TEXT_INTENT_NAME);
                data.putExtra(BarcodeObject, barcode);
                LocalBroadcastManager.getInstance(this.getActivity()).sendBroadcast(data);
            } else {
                Log.d(TAG, "barcode data is null");
            }
        } else {
            Log.d(TAG, "no barcode detected");
        }
        return barcode != null;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        settingLightFocus();
    }

    private void settingLightFocus() {
        if (mCameraSource != null) {
            if (mLightSwitch.isChecked()) {
                mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            } else {
                mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            }
            if (mFocusSwitch.isChecked()) {
                prevFocusMode = mCameraSource.getFocusMode();
                mCameraSource.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            } else {
                mCameraSource.setFocusMode(prevFocusMode);
            }
        }
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

}
