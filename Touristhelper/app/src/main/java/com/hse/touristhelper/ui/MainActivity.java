package com.hse.touristhelper.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.vision.barcode.Barcode;
import com.hse.touristhelper.R;
import com.hse.touristhelper.qr.storage.QRCodeObject;
import com.hse.touristhelper.qr.ui.CalendarEventFragment;
import com.hse.touristhelper.qr.ui.QRFragment;
import com.hse.touristhelper.qr.ui.QrWifiFragment;
import com.hse.touristhelper.translation.offline.ui.SampleAppertiumInstallerFragment;
import com.hse.touristhelper.ui.fragments.ItemFragment;
import com.hse.touristhelper.ui.fragments.QrTextFragment;
import com.hse.touristhelper.ui.fragments.TranslateFragment;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.Locale;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentsCallback {

    public static final String BROADCAST_TEXT_INTENT_NAME = "text-finished";
    public static final String BROADCAST_INTERNET_CHANGED_NAME = "internet-finished";

    Toolbar toolbar;

    private BroadcastReceiver mTranslationReceiver;
    private BroadcastReceiver mInternetConnectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
            boolean isConnected = activeNetInfo != null && activeNetInfo.isConnected();

            boolean isDisconnected = activeNetInfo == null || (!activeNetInfo.isConnectedOrConnecting());
            if (isConnected) {
                if (toolbar != null) {
                    toolbar.setTitle("Online mode");
                }
            } else if (isDisconnected) {
                if (toolbar != null) {
                    toolbar.setTitle("Offline mode");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        setUpNavigationView();
        mTranslationReceiver = new MainBroadCastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mTranslationReceiver, new IntentFilter(BROADCAST_TEXT_INTENT_NAME));
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mInternetConnectionReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mTranslationReceiver);
        unregisterReceiver(mInternetConnectionReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void setUpNavigationView() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        navigationView.getMenu().getItem(0).setChecked(true);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, new QRFragment()).commit();
    }

    private void clearBackStack() {
        FragmentManager manager = getSupportFragmentManager();
        if (manager.getBackStackEntryCount() > 0) {
            FragmentManager.BackStackEntry first = manager.getBackStackEntryAt(0);
            manager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                FragmentManager fragmentManager = getSupportFragmentManager();
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        clearBackStack();
        if (id == R.id.nav_qr) {
            fragment = new QRFragment();
        } else if (id == R.id.nav_share) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, "Tourist helper! Try it for free!");
            startActivity(Intent.createChooser(intent, "Share"));
            return true;
        } else if (id == R.id.nav_transl) {
            fragment = TranslateFragment.newInstance();
        } else if (id == R.id.nav_send) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "ayushindin@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
            return true;
        } else if (id == R.id.nav_about) {
            fragment = new LibsBuilder()
                    .supportFragment();
        } else if (id == R.id.nav_history) {
            fragment = ItemFragment.newInstance();
        } else if (id == R.id.nav_settings) {
            fragment = SampleAppertiumInstallerFragment.newInstance();
            ft.addToBackStack(null);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.content_frame, fragment).commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void openWifiQrFragment(Barcode barcode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.content_frame, QrWifiFragment.newInstance(barcode)).commit();
    }

    @Override
    public void openTextFragment(Barcode barcode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.content_frame, QrTextFragment.newInstance(barcode)).commit();
    }

    @Override
    public void openTextFragment(String barcode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.content_frame, QrTextFragment.newInstance(barcode)).commit();
    }

    @Override
    public void openQrUrlFragment(Barcode barcode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.content_frame, QrTextFragment.newInstance(barcode)).commit();
    }

    @Override
    public void openQrScannerFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.content_frame, new QRFragment()).commit();
    }

    @Override
    public void openCalendarEvent(Barcode barcode) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.content_frame, CalendarEventFragment.newInstance(barcode)).commit();
    }

    @Override
    public void openTextToolFragment(String textTotranslate, boolean textMininingMode, boolean translateDisabled) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.content_frame, TranslateFragment.newInstance(textTotranslate, textMininingMode, translateDisabled)).commit();
    }

    @Override
    public void openLibsFragment() {
    }

    private class MainBroadCastReceiver extends BroadcastReceiver {

        public MainBroadCastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            Barcode barcode = b.getParcelable(QRFragment.BarcodeObject);
            if (barcode != null) {
                handleQrCode(barcode);
            }
        }

        private void handleQrCode(Barcode barcode) {
            Realm realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            QRCodeObject object = realm.createObject(QRCodeObject.class);
            object.setTime(System.currentTimeMillis());
            object.setType(barcode.valueFormat);
            object.setContent(barcode.rawValue);
            realm.commitTransaction();
            switch (barcode.valueFormat) {
                case Barcode.TEXT:
                    openTextFragment(barcode);
                    break;
                case Barcode.URL:
                    showUrlAlertDialog(barcode);
                    break;
                case Barcode.WIFI:
                    openWifiQrFragment(barcode);
                    break;
                case Barcode.GEO:
                    showGeoAlertDialog(barcode.geoPoint.lat, barcode.geoPoint.lng);
                    break;
                case Barcode.PHONE:
                    showCallAlertDialog(barcode);
                    break;
                case Barcode.CALENDAR_EVENT:
                    openCalendarEvent(barcode);
                    break;
                case Barcode.CONTACT_INFO:
                case Barcode.EMAIL:
                case Barcode.ISBN:
                case Barcode.DRIVER_LICENSE:
                case Barcode.PRODUCT:
                case Barcode.SMS:
                default:
                    openTextFragment(barcode);
                    break;
            }
        }

        private void showUrlAlertDialog(final Barcode barcode) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Open url?")
                    .setMessage("Are you sure you want to open url? Url: " + barcode.rawValue)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String url = barcode.url.url;
                            if (url == null || url.length() == 0) {
                                url = barcode.rawValue;
                            }
                            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                url = "http://" + url;
                            }
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            MainActivity.this.startActivity(browserIntent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        private void showCallAlertDialog(final Barcode barcode) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Call")
                    .setMessage("Are you sure you want to call? Number: " + barcode.phone.number)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", barcode.phone.number, null));
                            MainActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        private void showGeoAlertDialog(final double latitude, final double longitude) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Open geo location")
                    .setMessage("Are you sure you want to open map with stored location?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            MainActivity.this.startActivity(intent);
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
