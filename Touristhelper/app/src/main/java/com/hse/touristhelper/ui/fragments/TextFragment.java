package com.hse.touristhelper.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.hse.touristhelper.R;
import com.hse.touristhelper.qr.ui.QRFragment;
import com.hse.touristhelper.translation.TranslationCallback;
import com.hse.touristhelper.translation.offline.AppertiumManager;
import com.hse.touristhelper.translation.online.RetrofitManager;
import com.hse.touristhelper.utils.NetworkUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class TextFragment extends Fragment {
    private final static String TRANSLATION_KEY = "transl";

    private TextView translation;


    public TextFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_translation, container, false);
        translation = ((TextView) v.findViewById(R.id.translation));

        Bundle b = getArguments();
        Barcode barcode = b.getParcelable(QRFragment.BarcodeObject);
        if (barcode != null) {
            if (NetworkUtils.isOnline(getActivity())) {
                RetrofitManager.getInstance().addListener(new TranslationCallback() {
                    @Override
                    public void onTranslationSucceed(String transl) {
                        translation.setText(transl);
                        Log.e("Retrofit", transl);
                    }
                });
                RetrofitManager.getInstance().translate(barcode.rawValue, "en");
            } else {
                AppertiumManager manager = AppertiumManager.getInstance(getActivity());
                manager.addListener(new TranslationCallback() {
                    @Override
                    public void onTranslationSucceed(String transl) {
                        translation.setText(transl);
                    }
                });
                manager.translate(barcode.rawValue);
            }
        }
        return v;
    }

}
