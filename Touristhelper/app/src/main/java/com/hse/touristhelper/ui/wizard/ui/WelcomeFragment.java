package com.hse.touristhelper.ui.wizard.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hse.touristhelper.R;
import com.hse.touristhelper.ui.wizard.model.WelcomePage;
import com.hse.touristhelper.ui.wizard.model.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 02.05.2016.
 */
public class WelcomeFragment extends Fragment {

    private static final String ARG_KEY = "key";

    private FragmentPageCallBack mCallbacks;
    private List<String> mChoices;
    private String mKey;
    private Page mPage;

    public static WelcomeFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        WelcomeFragment fragment = new WelcomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public WelcomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.welcome_page, container, false);

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
