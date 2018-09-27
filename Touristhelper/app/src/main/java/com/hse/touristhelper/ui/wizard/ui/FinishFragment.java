package com.hse.touristhelper.ui.wizard.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hse.touristhelper.R;
import com.hse.touristhelper.ui.wizard.model.FinalPage;
import com.hse.touristhelper.ui.wizard.model.Page;

import java.util.ArrayList;
import java.util.List;

public class FinishFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private FragmentPageCallBack mCallbacks;
    private List<String> mChoices;
    private String mKey;
    private Page mPage;

    public static FinishFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        FinishFragment fragment = new FinishFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public FinishFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wizard_finish, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        if (!(activity instanceof FragmentPageCallBack)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (FragmentPageCallBack) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }
}