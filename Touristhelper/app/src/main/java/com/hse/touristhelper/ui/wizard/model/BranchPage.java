package com.hse.touristhelper.ui.wizard.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.hse.touristhelper.ui.wizard.ui.WelcomeFragment;

import java.util.ArrayList;
import java.util.List;

public class BranchPage extends WelcomePage {
    private List<Branch> mBranches = new ArrayList<Branch>();

    public BranchPage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Page findByKey(String key) {
        if (getKey().equals(key)) {
            return this;
        }

        for (Branch branch : mBranches) {
            Page found = branch.childPageList.findByKey(key);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    @Override
    public void flattenCurrentPageSequence(ArrayList<Page> destination) {
        super.flattenCurrentPageSequence(destination);
        for (Branch branch : mBranches) {
            if (branch.choice.equals(mData.getString(Page.SIMPLE_DATA_KEY))) {
                branch.childPageList.flattenCurrentPageSequence(destination);
                break;
            }
        }
    }

    public BranchPage addBranch(String choice, Page... childPages) {
        PageList childPageList = new PageList(childPages);
        for (Page page : childPageList) {
            page.setParentKey(choice);
        }
        mBranches.add(new Branch(choice, childPageList));
        return this;
    }

    public BranchPage addBranch(String choice) {
        mBranches.add(new Branch(choice, new PageList()));
        return this;
    }

    @Override
    public Fragment createFragment() {
        return WelcomeFragment.create(getKey());
    }

    public String getOptionAt(int position) {
        return mBranches.get(position).choice;
    }

    public int getOptionCount() {
        return mBranches.size();
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    @Override
    public void notifyDataChanged() {
        mCallbacks.onPageTreeChanged();
        super.notifyDataChanged();
    }

    public BranchPage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    private static class Branch {
        public String choice;
        public PageList childPageList;

        private Branch(String choice, PageList childPageList) {
            this.choice = choice;
            this.childPageList = childPageList;
        }
    }
}
