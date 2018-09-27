package com.hse.touristhelper.ui.wizard.model;

import java.util.ArrayList;

/**
 * Created by Alex on 02.05.2016.
 */
public interface PageTreeNode {
    public Page findByKey(String key);

    public void flattenCurrentPageSequence(ArrayList<Page> dest);
}
