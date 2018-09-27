package com.hse.touristhelper.ui.wizard;

import android.content.Context;

import com.hse.touristhelper.ui.wizard.model.AbstractWizardModel;
import com.hse.touristhelper.ui.wizard.model.InstructionPage;
import com.hse.touristhelper.ui.wizard.model.LibraryInstallationPage;
import com.hse.touristhelper.ui.wizard.model.WelcomePage;
import com.hse.touristhelper.ui.wizard.model.PageList;

/**
 * Created by Alex on 02.05.2016.
 */
public class SandwichModel extends AbstractWizardModel {
    public SandwichModel(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(
                new WelcomePage(this, "Hello"),
                new InstructionPage(this, "Instruction"),
                new LibraryInstallationPage(this, "Installation")
        );
    }
}