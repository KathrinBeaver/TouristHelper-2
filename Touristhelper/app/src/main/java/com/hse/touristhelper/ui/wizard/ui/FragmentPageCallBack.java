package com.hse.touristhelper.ui.wizard.ui;

import com.hse.touristhelper.ui.wizard.model.Page;

/**
 * Created by Alex on 02.05.2016.
 */
public interface FragmentPageCallBack {
    Page onGetPage(String key);
    void installLibraries();
}