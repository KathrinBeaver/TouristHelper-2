package com.hse.touristhelper.translation.offline;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.hse.touristhelper.App;
import com.hse.touristhelper.translation.TranslationCallback;
import com.hse.touristhelper.translation.TranslationInterface;
import com.hse.touristhelper.translation.TranslationObserver;
import com.hse.touristhelper.utils.FileUtil;

import org.apertium.Translator;
import org.apertium.pipeline.Program;
import org.apertium.utils.IOUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import dalvik.system.DexClassLoader;

/**
 * Created by Alex on 09.03.2016.
 */
public class AppertiumManager implements TranslationInterface, TranslationObserver {

    public static final String REPO_DICT_URL = "https://apertium.svn.sourceforge.net/svnroot/apertium/builds/language-pairs";

    public final HashMap<String, String> titleToMode = new HashMap<String, String>();
    public final HashMap<String, String> modeToPackage = new HashMap<String, String>();

    private static AppertiumManager sSelf;
    private List<TranslationCallback> mCallback;
    private File packagesDir;
    private File bytecodeDir;
    private File bytecodeCacheDir;

    public final ArrayList<Runnable> observers = new ArrayList<Runnable>();

    private Handler mHandler;


    private final FilenameFilter apertiumPackageDirectoryFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.matches("apertium-[a-z][a-z][a-z]?-[a-z][a-z][a-z]?");
        }
    };

    public static AppertiumManager getInstance(Context context) {
        if (sSelf == null) {
            sSelf = new AppertiumManager(context);
        }
        return sSelf;
    }

    private AppertiumManager(Context context) {
        packagesDir = new File(context.getFilesDir(), "packages2");
        packagesDir.mkdirs();
        bytecodeDir = new File(context.getFilesDir(), "bytecode2");
        bytecodeDir.mkdirs();
        bytecodeCacheDir = new File(context.getCacheDir(), "bytecodecache2");
        bytecodeCacheDir.mkdirs();
        IOUtils.cacheDir = new File(context.getCacheDir(), "apertium-index-cache2");

        mHandler = new Handler(Looper.getMainLooper());
        mCallback = new ArrayList<>();
        rescanForPackages();
    }

    @Override
    public void translate(String text, String targetLang) {

    }

    @Override
    public void translate(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new TranslationTask().doInBackground(text);
            }
        });
    }

    private void runOnUiThread(Runnable r) {
        mHandler.post(r);
    }

    @Override
    public void addListener(TranslationCallback callback) {
        mCallback.add(callback);
    }

    @Override
    public void removeListener(TranslationCallback callback) {
        mCallback.remove(callback);
    }

    @Override
    public void removeAllListener() {
        mCallback.clear();
    }

    @Override
    public void notifyTranslationListeners(final String translation) {
        for (final TranslationCallback callback : mCallback) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onTranslationSucceed(translation);
                }
            });
        }
    }

    public String getBasedirForPackage(String pkg) {
        return packagesDir + "/" + pkg;
    }

    private void notifyObservers() {
        for (Runnable r : observers) mHandler.post(r);
    }

    public void rescanForPackages() {
        titleToMode.clear();
        modeToPackage.clear();

        String[] installedPackages = packagesDir.list(apertiumPackageDirectoryFilter);
        Log.d("", "Scanning " + packagesDir + " gave " + Arrays.asList(installedPackages));
        for (String pkg : installedPackages) {

            String basedir = packagesDir + "/" + pkg;
            try {
                Translator.setBase(basedir, getClassLoaderForPackage(pkg)); // getClassLoaderForPackage(pkg)
                for (String mode : Translator.getAvailableModes()) {
                    String title = Translator.getTitle(mode);
                    Log.d("", mode + "  " + title + "  " + basedir);
                    titleToMode.put(title, mode);
                    modeToPackage.put(mode, pkg);
                }
            } catch (Throwable ex) {
                //Perhaps the directory contained a file that wasn't a valid package...
                ex.printStackTrace();
                Log.e("", basedir, ex);
            }
        }
        notifyObservers();
    }

    public DexClassLoader getClassLoaderForPackage(String pkg) {
        return new DexClassLoader(bytecodeDir + "/" + pkg + ".jar", bytecodeCacheDir.getAbsolutePath(), null, this.getClass().getClassLoader());
    }

    public void installJar(File tmpjarfile, String pkg) throws IOException {
        File dir = new File(packagesDir, pkg);
        FileUtil.unzip(tmpjarfile.getPath(), dir.getPath(), new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                if (s.endsWith(".class")) {
                    return false;
                }
                return true;
            }
        });

        dir.setLastModified(tmpjarfile.lastModified());
        File classesDex = new File(dir, "classes.dex");
        File installedjarfile = new File(bytecodeDir, pkg + ".jar");
        if (!classesDex.exists()) {
            tmpjarfile.renameTo(installedjarfile);
        } else {
            ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(installedjarfile)));
            try {
                ZipEntry entry = new ZipEntry(classesDex.getName());
                zos.putNextEntry(entry);
                FileInputStream in = new FileInputStream(classesDex);
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    zos.write(buffer, 0, read);
                }
                in.close();

                classesDex.delete();
                zos.closeEntry();
            } finally {
                zos.close();
            }
            installedjarfile.setLastModified(tmpjarfile.lastModified());
        }
    }

    public void uninstallPackage(String pkg) {
        FileUtil.remove(new File(bytecodeDir, pkg + ".jar"));
        FileUtil.remove(new File(packagesDir, pkg));
        FileUtil.remove(new File(bytecodeCacheDir, pkg + ".dex"));
    }

    private class TranslationTask extends AsyncTask<String, Object, String> implements Translator.TranslationProgressListener {

        @Override
        protected String doInBackground(String... inputText) {
            try {
                String currentModeTitle = null;
                if (currentModeTitle == null) {
                    currentModeTitle = App.prefs.getString(App.PREF_LAST_MODE_TITLE, null);
                }
                if (titleToMode.containsKey(currentModeTitle)) {
                    currentModeTitle = null;
                }
                if (currentModeTitle == null && titleToMode.size() > 0) {
                    ArrayList<String> modeTitle = new ArrayList<String>(titleToMode.keySet());
                    Collections.sort(modeTitle);
                    currentModeTitle = modeTitle.get(0);
                }
                String input = inputText[0];

                if (currentModeTitle != null) {
                    String mode = titleToMode.get(currentModeTitle);
                    String pkg = modeToPackage.get(mode);

                    Translator.setDisplayMarks(true);
                    Translator.setBase(getBasedirForPackage(pkg), getClassLoaderForPackage(pkg));
                    Translator.setMode(mode);

                    StringWriter output = new StringWriter();
                    String format = "txt";
                    Translator.translate(new StringReader(input), output, new Program("apertium-des" + format), new Program("apertium-re" + format), this);
                    notifyTranslationListeners(output.toString());
                    return output.toString();
                } else {
                    notifyTranslationListeners(input);
                    return input;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                return "error: " + e;
            }
        }

        @Override
        public void onTranslationProgress(String s, int i, int i1) {

        }
    }

}
