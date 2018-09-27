package com.hse.touristhelper.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.hse.touristhelper.R;
import com.hse.touristhelper.ui.FragmentsCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class QrTextFragment extends Fragment implements View.OnClickListener {

    private static final String KEY = "key_qr_barcode";
    private static final String KEY_content = "key_qr_text";

    private FragmentsCallback mFragmentsCallback;

    private TextView mTextContent;

    private Button mTranslateButton;
    private Button mSimplifyButton;
    private Button mTransAndSimplButton;

    private String mText;

    public QrTextFragment() {
        // Required empty public constructor
    }

    public static QrTextFragment newInstance(Barcode barcode) {
        Bundle args = new Bundle();
        args.putParcelable(KEY, barcode);

        QrTextFragment fragment = new QrTextFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static QrTextFragment newInstance(String content) {
        Bundle args = new Bundle();
        args.putString(KEY_content, content);

        QrTextFragment fragment = new QrTextFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final Barcode mBarcode = getArguments().getParcelable(KEY);
        final String content = getArguments().getString(KEY_content);
        View v = inflater.inflate(R.layout.fragment_qr_text, container, false);
        mTextContent = (TextView) v.findViewById(R.id.content_text);
        if (mBarcode != null) {
            mText = mBarcode.rawValue;
        } else {
            mText = content;
        }
        mTextContent.setText(mText);
        mTransAndSimplButton = (Button) v.findViewById(R.id.translateAndSimplify);
        mTranslateButton = (Button) v.findViewById(R.id.translate_only);
        mSimplifyButton = (Button) v.findViewById(R.id.simplify_only);

        mTranslateButton.setOnClickListener(this);
        mTransAndSimplButton.setOnClickListener(this);
        mSimplifyButton.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick(View v) {
        if (v == mTranslateButton) {
            mFragmentsCallback.openTextToolFragment(mText, false, false);
        } else if (v == mTransAndSimplButton) {
            mFragmentsCallback.openTextToolFragment(mText, true, false);
        } else if (v == mSimplifyButton) {
            mFragmentsCallback.openTextToolFragment(mText, true, true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFragmentsCallback = (FragmentsCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentsCallback = null;
    }
}
