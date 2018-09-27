package com.hse.touristhelper.ui.wizard.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.hse.touristhelper.ui.wizard.ui.OneChoiceFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Alex on 02.05.2016.
 */
public class OneChoicePage extends Page {

    protected ArrayList<String> mChoices = new ArrayList<String>();

    public OneChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return OneChoiceFragment.create(getKey());
    }

    public String getOptionAt(int position) {
        return mChoices.get(position);
    }

    public int getOptionCount() {
        return mChoices.size();
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    public OneChoicePage setChoices(String... choices) {
        mChoices.addAll(Arrays.asList(choices));
        return this;
    }

    public OneChoicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }
}
