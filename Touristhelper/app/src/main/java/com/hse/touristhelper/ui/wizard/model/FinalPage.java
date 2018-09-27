package com.hse.touristhelper.ui.wizard.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.hse.touristhelper.ui.wizard.ui.InstructionFragment;

import java.util.ArrayList;

/**
 * Created by Alex on 02.05.2016.
 */
public class FinalPage extends Page {
    protected ArrayList<String> mChoices = new ArrayList<String>();

    public FinalPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return InstructionFragment.create(getKey());
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

    public FinalPage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }


}
