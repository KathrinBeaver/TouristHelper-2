package com.hse.touristhelper.ui.wizard.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hse.touristhelper.R;
import com.hse.touristhelper.translation.offline.ui.SampleAppertiumInstallerFragment;
import com.hse.touristhelper.ui.wizard.model.Page;

import java.util.List;

/**
 * Created by Alex on 02.05.2016.
 */
public class LibraryInstallationFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private FragmentPageCallBack mCallbacks;
    private List<String> mChoices;
    private String mKey;
    private Page mPage;

    public static LibraryInstallationFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        LibraryInstallationFragment fragment = new LibraryInstallationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public LibraryInstallationFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_wizard_libraries, container, false);
        rootView.findViewById(R.id.wizard_install_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallbacks.installLibraries();
            }
        });
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
