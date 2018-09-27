package com.hse.touristhelper.translation.offline.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hse.touristhelper.R;
import com.hse.touristhelper.translation.offline.AppertiumManager;
import com.hse.touristhelper.utils.FileUtil;

import org.apertium.Translator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

public class SampleAppertiumInstallerFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    private AppertiumInstaller.Data d;
    private AppertiumInstaller mAppertiumInstaller;

    private ListView listView;
    private ProgressBar progressBar;
    private TextView progressTextView;
    private Button applyButton;
    private LanguagePairAdapter adapter = new LanguagePairAdapter();

    private static boolean mNeedFinishActivity = false;

    private boolean mSavedInstance;


    public SampleAppertiumInstallerFragment() {
        // Required empty public constructor
    }

    public static SampleAppertiumInstallerFragment newInstance() {
        mNeedFinishActivity = false;
        SampleAppertiumInstallerFragment fragment = new SampleAppertiumInstallerFragment();
        return fragment;
    }

    public static SampleAppertiumInstallerFragment newInstance(boolean needFinishActivity) {
        mNeedFinishActivity = needFinishActivity;
        SampleAppertiumInstallerFragment fragment = new SampleAppertiumInstallerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppertiumInstaller = new AppertiumInstaller(AppertiumManager.getInstance(getActivity()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mSavedInstance = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSavedInstance = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_installer_lang_pair, container, false);
        applyButton = (Button) v.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(this);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressTextView = (TextView) v.findViewById(R.id.progressTextView);
        listView = (ListView) v.findViewById(R.id.listView1);
        listView.setOnItemClickListener(this);
        listView.setAdapter(adapter);
        return v;
    }

    public File getCacheDir() {
        return getActivity().getCacheDir();
    }

    public void updateUI(AppertiumInstaller.Data d) {
        //setProgressBarIndeterminateVisibility(d.repoTask != null);
        progressTextView.setText(d.progressText);
        applyButton.setText(d.installTask == null ? R.string.apply : R.string.cancel);
        progressBar.setVisibility(d.installTask != null ? View.VISIBLE : View.GONE);
        progressBar.setMax(d.progressMax);
        progressBar.setProgress(d.progress);
    }

    public void finish() {
        if (mNeedFinishActivity) {
            if (getActivity() != null) {
                getActivity().finish();
                return;
            }
        }
        if (!mSavedInstance) {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            fm.popBackStackImmediate();
            fm.beginTransaction().commit();
        }
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int row, long arg3) {
        String pkg = d.packages.get(row);

        if (!d.updatablePackages.contains(pkg)) {
            if (d.installedPackages.contains(pkg)) {
                if (d.packagesToUninstall.contains(pkg)) {
                    d.packagesToUninstall.remove(pkg);
                } else {
                    d.packagesToUninstall.add(pkg);
                }
            } else {
                if (d.packagesToInstall.contains(pkg)) {
                    d.packagesToInstall.remove(pkg);
                } else {
                    d.packagesToInstall.add(pkg);
                }
            }
        } else {
            // An updateable package - there are 3 states: untouched, update, uninstall
            if (d.packagesToInstall.contains(pkg)) {
                // update -> uninstall
                d.packagesToInstall.remove(pkg);
                d.packagesToUninstall.add(pkg);
            } else {
                if (d.packagesToUninstall.contains(pkg)) {
                    // uninstall -> untouched
                    d.packagesToUninstall.remove(pkg);
                } else {
                    // untouched -> update
                    d.packagesToInstall.add(pkg);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        mAppertiumInstaller.install();
    }

    private class LanguagePairAdapter extends BaseAdapter {
        public int getCount() {
            if (d == null) {
                return 0;
            }
            return d.packages.size();
        }

        public Object getItem(int n) {
            return n;
        }

        public long getItemId(int n) {
            return n;
        }

        private boolean isChecked(String pkg) {
            if (d.installedPackages.contains(pkg) && !d.packagesToUninstall.contains(pkg)) {
                return true;
            }
            if (d.packagesToInstall.contains(pkg)) {
                return true;
            }
            return false;
        }

        @Override
        public View getView(int row, View v, ViewGroup parent) {
            if (v == null) {
                v = getActivity().getLayoutInflater().inflate(R.layout.install_elem_adapter, null);
            }
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView status = (TextView) v.findViewById(R.id.status);

            String pkg = d.packages.get(row);
            String pkgTitle = d.packageToTitle.get(pkg);
            boolean isChecked = isChecked(pkg);
            checkBox.setChecked(isChecked);

            if (d.packagesToInstall.contains(pkg)) {
                name.setText(Html.fromHtml("<html><b>" + pkgTitle + "</b></html>"));
                if (d.updatablePackages.contains(pkg)) {
                    status.setText(Html.fromHtml("<html><b>Marked to update</b></html>"));
                } else {
                    status.setText(Html.fromHtml("<html><b>Marked to install</b></html>"));
                }
            } else if (d.packagesToUninstall.contains(pkg)) {
                name.setText(Html.fromHtml("<html><b>" + pkgTitle + "</b></html>"));
                status.setText(Html.fromHtml("<html><b>Marked to uninstall</b></html>"));
            } else {
                name.setText(pkgTitle);
                String txt;
                if (d.updatedPackages.contains(pkg)) {
                    txt = "<html><i>Installed from repository</i></html>";
                } else if (d.updatablePackages.contains(pkg)) {
                    txt = "<html><i>Update available in repository</i></html>";
                } else if (d.installedPackages.contains(pkg)) {
                    if (d.repoTask != null) {
                        // During repo refresh packages are just listed in installedPackages, thus we end here during repo refresh
                        txt = "<html><i>Installed</i></html>";
                    } else {
                        txt = "<html><i>Manually installed</i></html>";
                    }
                } else {
                    txt = "<html><i>Not installed</i></html>";
                }
                status.setText(Html.fromHtml(txt));
            }
            return v;
        }
    }

    ;

    /**
     * Created by Alex on 29.03.2016.
     */
    public class AppertiumInstaller {

        private static final String REPO_URL = "https://apertium.svn.sourceforge.net/svnroot/apertium/builds/language-pairs";

        private AppertiumManager mAppertiumManager;

        public AppertiumInstaller(AppertiumManager manager) {
            mAppertiumManager = manager;

            d = new Data();
            d.cachedRepoFile = new File(getCacheDir(), new File(REPO_URL).getName());
            d.progressText = "Refreshing package list, please wait...";
            d.repoTask = new RepoAsyncTask();
            d.repoTask.d = d;
            d.fragment = SampleAppertiumInstallerFragment.this;
            d.repoTask.execute();
        }

        public void install() {
            if (d.installTask == null) {
                d.progressText = "Preparing...";
                d.installTask = new InstallRemoveAsyncTask();
                d.installTask.d = d;
                d.installTask.execute();
            } else {
                d.installTask.cancel(true);
            }
            d.fragment.updateUI(d);
        }

        private class Data {
            SampleAppertiumInstallerFragment fragment;
            ArrayList<String> packages = new ArrayList<String>();
            HashSet<String> installedPackages = new HashSet<String>();
            HashSet<String> updatablePackages = new HashSet<String>();
            HashSet<String> updatedPackages = new HashSet<String>();
            HashSet<String> packagesToInstall = new HashSet<String>();
            HashSet<String> packagesToUninstall = new HashSet<String>();
            HashMap<String, String> packageToTitle = new HashMap<String, String>();
            HashMap<String, URL> packageToURL = new HashMap<String, URL>();
            File cachedRepoFile;
            RepoAsyncTask repoTask;
            InstallRemoveAsyncTask installTask;
            String progressText;
            private int progressMax;
            private int progress;
        }

        private void initPackages(Data d, InputStream inputStream, boolean useNetwork) throws IOException {
            ArrayList<String> packages = new ArrayList<String>();
            // Get a copy of the list of installed packages, as we modify it below
            HashSet<String> installedPackages = new HashSet<String>(mAppertiumManager.modeToPackage.values());

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("\t");
                if (columns.length > 3) {
                    String pkg = columns[0];
                    packages.add(pkg);
                    URL url = new URL(columns[1]);
                    d.packageToURL.put(pkg, url);
                    Locale.setDefault(Locale.ENGLISH);
                    String modeTitle = Translator.getTitle(columns[3]);
                    d.packageToTitle.put(pkg, modeTitle);
                    if (installedPackages.contains(pkg)) {
                        installedPackages.remove(pkg);
                        d.installedPackages.add(pkg);
                        if (useNetwork) {
                            long localLastModified = new File(mAppertiumManager.getBasedirForPackage(pkg)).lastModified();
                            long onlineLastModified = url.openConnection().getLastModified();
                            if (onlineLastModified > localLastModified) {
                                d.updatablePackages.add(pkg);
                            } else {
                                d.updatedPackages.add(pkg);
                            }
                        }
                    }
                }
            }

            for (String pkg : installedPackages) {
                packages.add(pkg);
                d.installedPackages.add(pkg);
            }

            Collections.sort(packages);
            d.packages = packages;
        }


        public class InstallRemoveAsyncTask extends AsyncTask {
            Data d;

            @Override
            protected Object doInBackground(Object... arg0) {
                d.progressMax = d.packagesToInstall.size() * 100;

                int packageNo = 0;
                for (String pkg : d.packagesToInstall) {
                    if (isCancelled()) break;
                    try {
                        publishProgress(d.fragment.getString(R.string.downloading) + " " + pkg + "...");
                        URL url = d.packageToURL.get(pkg);
                        URLConnection uc = url.openConnection();
                        long lastModified = uc.getLastModified();
                        int contentLength = uc.getContentLength();
                        BufferedInputStream in = new BufferedInputStream(uc.getInputStream());
                        File tmpjarfile = new File(d.fragment.getCacheDir(), pkg + ".jar");
                        FileOutputStream fos = new FileOutputStream(tmpjarfile);
                        byte data[] = new byte[8192];
                        int count;
                        int total = 0;
                        while ((count = in.read(data, 0, 1024)) != -1) {
                            fos.write(data, 0, count);
                            total += count;
                            publishProgress(100 * packageNo + 90 * total / contentLength);
                        }
                        fos.close();
                        in.close();
                        tmpjarfile.setLastModified(lastModified);
                        publishProgress(d.fragment.getString(R.string.installing) + " " + pkg + "...");
                        mAppertiumManager.installJar(tmpjarfile, pkg);
                        tmpjarfile.delete();
                        packageNo++;
                        publishProgress(98 * packageNo);
                        d.installedPackages.add(pkg);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        return ex;
                    }
                }

                if (isCancelled()) return "";
                for (String pkg : d.packagesToUninstall) {
                    publishProgress(d.fragment.getString(R.string.deleting) + " " + pkg + "...");
                    mAppertiumManager.uninstallPackage(pkg);
                    d.installedPackages.remove(pkg);
                }
                d.packagesToInstall.clear();
                d.packagesToUninstall.clear();

                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                if (d.fragment == null) {
                    return;
                }
                Object v = values[0];
                //Log.d("", ""+v);
                if (v instanceof Integer) {
                    d.progress = (Integer) v;
                } else {
                    d.progressText = String.valueOf(v);
                }
                d.fragment.updateUI(d);
            }

            @Override
            protected void onPostExecute(Object result) {
                d.installTask = null;
                mAppertiumManager.rescanForPackages();
                if (d.fragment == null) {
                    return;
                }
                if (result != null) {
                    d.progressText = String.valueOf("" + result);
                    d.fragment.updateUI(d);
                } else {
                    d.fragment.finish();
                }
            }

            @Override
            protected void onCancelled() {
                d.installTask = null;
                d.progressText = "Cancelled";
                mAppertiumManager.rescanForPackages();
                if (d.fragment == null) {
                    return;
                }
                d.repoTask = new RepoAsyncTask();
                d.repoTask.d = d;
                d.repoTask.execute();
                d.fragment.updateUI(d);
            }
        }

        public class RepoAsyncTask extends AsyncTask {
            Data d;

            @Override
            protected Object doInBackground(Object... arg0) {
                try {
                    // First load old version of the list to display
                    if (d.cachedRepoFile.exists()) {
                        initPackages(d, new FileInputStream(d.cachedRepoFile), false);
                    } else {
                        initPackages(d, d.fragment.getResources().openRawResource(R.raw.language_pairs), false);
                    }
                    publishProgress();
                    // Then make the check over the network
                    FileUtil.copyStream(new URL(REPO_URL).openStream(), new FileOutputStream(d.cachedRepoFile));
                    initPackages(d, new FileInputStream(d.cachedRepoFile), true);
                    String STR_INSTRUCTIONS = "Check the language pairs to install and uncheck the ones to uninstall.";
                    d.progressText = STR_INSTRUCTIONS;
                } catch (IOException ex) {
                    ex.printStackTrace();
                    d.progressText = d.fragment.getString(R.string.network_error);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    d.progressText = ex.toString();
                }
                publishProgress();
                return null;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                if (d.fragment == null) {
                    return;
                }
                d.fragment.adapter.notifyDataSetChanged();
                d.fragment.updateUI(d);
            }

            @Override
            protected void onPostExecute(Object result) {
                d.repoTask = null;
                if (d.fragment == null) {
                    return;
                }
                d.fragment.updateUI(d);
            }
        }
    }
}
