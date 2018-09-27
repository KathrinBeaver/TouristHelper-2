package com.hse.touristhelper.ui.wizard;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.hse.touristhelper.translation.offline.ui.SampleAppertiumInstallerFragment;

/**
 * Created by Alex on 14.05.2016.
 */
public class InstallLibrariestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fm = getSupportFragmentManager();
        SampleAppertiumInstallerFragment fragment = SampleAppertiumInstallerFragment.newInstance(true);
        fm.beginTransaction().addToBackStack(null).replace(android.R.id.content, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
