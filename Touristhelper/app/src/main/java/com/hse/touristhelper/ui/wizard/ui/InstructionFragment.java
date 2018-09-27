package com.hse.touristhelper.ui.wizard.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hse.touristhelper.R;
import com.hse.touristhelper.ui.wizard.model.InstructionPage;
import com.hse.touristhelper.ui.wizard.model.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 02.05.2016.
 */
public class InstructionFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private FragmentPageCallBack mCallbacks;
    private List<String> mChoices;
    private String mKey;
    private Page mPage;

    public static InstructionFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        InstructionFragment fragment = new InstructionFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public InstructionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        InstructionPage fixedChoicePage = (InstructionPage) mPage;
        mChoices = new ArrayList<String>();
        for (int i = 0; i < fixedChoicePage.getOptionCount(); i++) {
            mChoices.add(fixedChoicePage.getOptionAt(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wizard_isntruction, container, false);
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
