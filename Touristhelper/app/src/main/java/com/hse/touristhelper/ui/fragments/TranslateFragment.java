package com.hse.touristhelper.ui.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.hse.touristhelper.App;
import com.hse.touristhelper.R;
import com.hse.touristhelper.text_mining.TextMiningManager;
import com.hse.touristhelper.translation.TranslationCallback;
import com.hse.touristhelper.translation.offline.AppertiumManager;
import com.hse.touristhelper.translation.offline.ui.SampleAppertiumInstallerFragment;
import com.hse.touristhelper.translation.online.RetrofitManager;
import com.hse.touristhelper.translation.online.YandexLanguages;
import com.hse.touristhelper.utils.NetworkUtils;

import org.apertium.Translator;
import org.apertium.pipeline.Program;
import org.apertium.utils.IOUtils;
import org.apertium.utils.Timing;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;

public class TranslateFragment extends Fragment implements View.OnClickListener {

    private final static String KEY_TRANSLATION_TEXT = "translation_text";
    private final static String KEY_MODE_TEXT = "mode_text";
    private final static String KEY_MODE_TR = "tr_text";

    private ProgressBar mOnlineTranslateProgressBar;
    private TextView mModeText;

    private Toolbar mMainToolbar;

    private Toolbar mToolabrSrc;
    private Toolbar mToolabrTrg;

    private Button mTranslate;
    private Button mInstall;
    private CardView mTrgCard;

    private EditText mEditText;
    private TextView mTransText;
    private AppertiumManager manager;
    private RetrofitManager mRetrofitManager;

    private String currentModeTitle;

    private static TranslationTask translationTask;

    private boolean mTextMiningModeEnabled;
    private boolean mTranslateModeDisabled = false;
    private String mTextToTranslate;

    private String mTargetLanguage = "en";

    public TranslateFragment() {
        // Required empty public constructor
    }

    public static TranslateFragment newInstance() {
        TranslateFragment fragment = new TranslateFragment();
        return fragment;
    }

    public static TranslateFragment newInstance(String textTotranslate, boolean analysis_mode, boolean translateDisabled) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_MODE_TEXT, analysis_mode);
        bundle.putString(KEY_TRANSLATION_TEXT, textTotranslate);
        bundle.putBoolean(KEY_MODE_TR, translateDisabled);
        TranslateFragment fragment = new TranslateFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        manager.observers.add(apertiumInstallationObserver);
        apertiumInstallationObserver.run();
    }


    @Override
    public void onPause() {
        super.onResume();
        manager.observers.remove(apertiumInstallationObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment`
        View v = inflater.inflate(R.layout.fragment_translate, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mTextToTranslate = bundle.getString(KEY_TRANSLATION_TEXT);
            mTextMiningModeEnabled = bundle.getBoolean(KEY_MODE_TEXT);
            mTranslateModeDisabled = bundle.getBoolean(KEY_MODE_TR);
        }

        mTrgCard = (CardView) v.findViewById(R.id.trg_card);

        mToolabrSrc = (Toolbar) v.findViewById(R.id.src_toolbar);
        mToolabrSrc.inflateMenu(R.menu.source_card);
        mToolabrTrg = (Toolbar) v.findViewById(R.id.trg_toolbar);
        mToolabrTrg.inflateMenu(R.menu.target_card);
        mToolabrSrc.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mEditText.setText("");
                return false;
            }
        });

        mToolabrTrg.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                ClipboardManager clipboard = (ClipboardManager) App.getAppContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("qr content", mTransText.getText());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(App.getAppContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        mToolabrSrc.setTitle("Source text");
        mToolabrTrg.setTitle("Target text");

        mEditText = (EditText) v.findViewById(R.id.src_text);
        mEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTrgCard.setVisibility(View.GONE);
            }
        });
        if (mTextToTranslate != null && mTextToTranslate.length() > 0) {
            mEditText.setText(mTextToTranslate);
        }
        mTransText = (TextView) v.findViewById(R.id.trg_text);

        manager = AppertiumManager.getInstance(getActivity());

        mInstall = (Button) v.findViewById(R.id.install_button);
        mInstall.setEnabled(true);
        mInstall.setOnClickListener(this);
        mTranslate = (Button) v.findViewById(R.id.translate_button);
        mTranslate.setEnabled(true);
        mTranslate.setOnClickListener(this);
        if (mTranslateModeDisabled) {
            mInstall.setVisibility(View.GONE);
        }

        mOnlineTranslateProgressBar = (ProgressBar) v.findViewById(R.id.translate_progress);
        mRetrofitManager = RetrofitManager.getInstance();
        mModeText = (TextView) v.findViewById(R.id.text_mode);

        prepareMode();

        RetrofitManager.getInstance().addListener(new TranslationCallback() {
            @Override
            public void onTranslationSucceed(String transl) {
                textMiningExectue(transl);
            }
        });

        return v;
    }

    private void textMiningExectue(String text) {
        if (mTextMiningModeEnabled) {
            if (TextMiningManager.isInit()) {
                new TextMiningAsyncTask().execute(text);
            } else {
                Toast.makeText(getActivity(), "Text mining manager not init", Toast.LENGTH_SHORT).show();
            }
        } else {
            mTransText.setText(text);
            mOnlineTranslateProgressBar.setVisibility(View.GONE);
            mTransText.setVisibility(View.VISIBLE);
        }
    }

    private void prepareMode() {
        if (mTextMiningModeEnabled && mTranslateModeDisabled) {
            mModeText.setText("Simplify text mode");
            mTranslate.setText("Simplify");
        } else if (mTextMiningModeEnabled && !mTranslateModeDisabled) {
            mModeText.setText("Do It");
            mModeText.setText("Translate and simplify");
        } else if (!mTextMiningModeEnabled) {
            mModeText.setText("Translate");
            mModeText.setText("Translate");
        }
    }

    @Override
    public void onClick(View view) {
        boolean networkEnabled = NetworkUtils.isOnline(getActivity());
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mEditText.getApplicationWindowToken(), 0);

        if (view.equals(mTranslate)) {
            if (mEditText.getText() != null) {
                if (!mTranslateModeDisabled) {
                    mTrgCard.setVisibility(View.VISIBLE);
                    if (networkEnabled) {
                        mOnlineTranslateProgressBar.setVisibility(View.VISIBLE);
                        mTransText.setVisibility(View.GONE);
                        RetrofitManager.getInstance().translate(mEditText.getText().toString().replace(";", " "), mTargetLanguage);
                    } else {
                        if (manager.titleToMode.isEmpty()) {
                            FragmentManager fm = getActivity().getSupportFragmentManager();
                            SampleAppertiumInstallerFragment fragment = SampleAppertiumInstallerFragment.newInstance();
                            fm.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
                            return;
                        } else {
                            translatebyApertium();
                        }
                    }
                } else {
                    new TextMiningAsyncTask().execute(mEditText.getText().toString());
                }
            }
        } else if (view.equals(mInstall))

        {
            if (networkEnabled) {
                chooseYandexLanguages();
            } else {
                installApertiumDicts();
            }
        }
    }

    private void translatebyApertium() {
        try {
            if (mEditText.getText() != null) {
                String mode = manager.titleToMode.get(currentModeTitle);
                String pkg = manager.modeToPackage.get(mode);

                Translator.setDisplayMarks(true);
                Translator.setBase(manager.getBasedirForPackage(pkg), manager.getClassLoaderForPackage(pkg));
                Translator.setMode(mode);

                translationTask = new TranslationTask();
                translationTask.fragment = this;
                mTransText.setText("Preparing...");
                translationTask.execute(mEditText.getText().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void installApertiumDicts() {
        ArrayList<String> modeTitle = new ArrayList<String>(manager.titleToMode.keySet());
        Collections.sort(modeTitle);
        modeTitle.add(getString(R.string.download_languages));

        final String[] modeTitlex = modeTitle.toArray(new String[modeTitle.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_languages));
        builder.setItems(modeTitlex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                if (position == modeTitlex.length - 1) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    SampleAppertiumInstallerFragment fragment = SampleAppertiumInstallerFragment.newInstance();
                    fm.beginTransaction().addToBackStack(null).replace(R.id.content_frame, fragment).commit();
                    return;
                }
                currentModeTitle = modeTitlex[position];
                mInstall.setText(currentModeTitle);
                App.prefs.edit().putString(App.PREF_LAST_MODE_TITLE, currentModeTitle).commit();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void chooseYandexLanguages() {
        final List<YandexLanguages> modeTitle = new ArrayList<>(EnumSet.allOf(YandexLanguages.class));
        Comparator comparator1 = new Comparator<YandexLanguages>() {

            public int compare(YandexLanguages e1, YandexLanguages e2) {

                return e1.toString().compareTo(e2.toString());
            }
        };

        Collections.sort(modeTitle, comparator1);

        final String[] modeTitlex = new String[modeTitle.size()];
        for (int i = 0; i < modeTitle.size(); i++) {
            modeTitlex[i] = modeTitle.get(i).toString();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.choose_languages));
        builder.setItems(modeTitlex, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                mTargetLanguage = modeTitle.get(position).getShortTitle();
                mInstall.setText(modeTitle.get(position).getTitle());
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    Runnable apertiumInstallationObserver = new Runnable() {
        public void run() {
            if (currentModeTitle == null) {
                currentModeTitle = App.prefs.getString(App.PREF_LAST_MODE_TITLE, null);
            }
            if (manager.titleToMode.containsKey(currentModeTitle)) {
                currentModeTitle = null;
            }
            if (currentModeTitle == null && manager.titleToMode.size() > 0) {
                ArrayList<String> modeTitle = new ArrayList<String>(manager.titleToMode.keySet());
                Collections.sort(modeTitle);
                currentModeTitle = modeTitle.get(0);
            }
            if (currentModeTitle != null && !NetworkUtils.isOnline(getContext())) {
                mInstall.setText(currentModeTitle);
            } else if (NetworkUtils.isOnline(getContext())) {
                final List<YandexLanguages> modeTitle = new ArrayList<>(EnumSet.allOf(YandexLanguages.class));
                for (int i = 0; i < modeTitle.size(); i++) {
                    if (modeTitle.get(i).getShortTitle().equals(mTargetLanguage)) {
                        mInstall.setText(modeTitle.get(i).getTitle());
                    }
                }
            } else {
                mInstall.setText(R.string.choose_languages);
            }
        }
    };

    class TranslationTask extends AsyncTask<String, Object, String> implements Translator.TranslationProgressListener {
        private TranslateFragment fragment;

        @Override
        protected String doInBackground(String... inputText) {
            IOUtils.timing = new org.apertium.utils.Timing("overall");
            try {
                String input = inputText[0];
                Timing timing = new Timing("Translator.translate()");
                StringWriter output = new StringWriter();
                String format = "txt";
                Translator.translate(new StringReader(input), output, new Program("apertium-des" + format), new Program("apertium-re" + format), this);
                timing.report();
                return output.toString();
            } catch (Throwable e) {
                e.printStackTrace();
                return "error: " + e;
            } finally {
                IOUtils.timing.report();
                IOUtils.timing = null;
            }
        }

        public void onTranslationProgress(String task, int progress, int progressMax) {
            publishProgress(task, progress, progressMax);
        }

        @Override
        protected void onProgressUpdate(Object... v) {
            fragment.mTransText.setText("Translating...\n(in stage " + v[1] + " of " + v[2] + ")");
        }

        @Override
        protected void onPostExecute(String output) {
            fragment.translationTask = null;
            fragment.mTransText.setText(output);
            textMiningExectue(output);
        }
    }

    class TextMiningAsyncTask extends AsyncTask<String, String, String> {

        private Spannable spannable;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mTransText.setVisibility(View.GONE);
            mOnlineTranslateProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);
            mOnlineTranslateProgressBar.setVisibility(View.GONE);
            mTransText.setVisibility(View.VISIBLE);
            mTransText.setText(spannable, TextView.BufferType.SPANNABLE);
        }

        @Override
        protected String doInBackground(String... params) {
            spannable = TextMiningManager.getInstance(getContext()).simplifyText(params[0]);
            return "";
        }
    }
}
