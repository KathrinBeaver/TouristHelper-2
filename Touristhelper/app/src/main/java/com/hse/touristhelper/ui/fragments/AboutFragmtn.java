package com.hse.touristhelper.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hse.touristhelper.BuildConfig;
import com.hse.touristhelper.R;
import com.hse.touristhelper.ui.FragmentsCallback;

public class AboutFragmtn extends Fragment {


    private FragmentsCallback mListener;

    public AboutFragmtn() {
        // Required empty public constructor
    }

    public static AboutFragmtn newInstance() {
        AboutFragmtn fragment = new AboutFragmtn();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_fragmtn, container, false);
        ((TextView) v.findViewById(R.id.textView6)).setText(BuildConfig.VERSION_NAME);
        ((Button) v.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.openLibsFragment();
            }
        });
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentsCallback) {
            mListener = (FragmentsCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentsCallback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
