package com.hse.touristhelper.text_mining;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Alex on 15.05.2016.
 */
public class TextMiningManager {
    private static volatile TextMiningManager sSelf;

    private NER mNamedEntityRecognition;

    private static boolean isInit = false;

    public TextMiningManager(Context context) {
        mNamedEntityRecognition = new NER(context);
        isInit = true;
    }

    public static TextMiningManager getInstance(Context context) {
        if (sSelf == null) {
            sSelf = new TextMiningManager(context);
        }
        return sSelf;
    }

    public List<String> getPopularWords(String text) throws IOException {
        return ImportantWords.interestingPhrases(text);
    }

    public HashMap<Integer, HashSet<String>> getNerWords(String text) throws IOException, ClassNotFoundException {
        return mNamedEntityRecognition.confidenceChunks(text);
    }

    public Spannable simplifyText(String text) {
        StringBuilder stringBuilder = new StringBuilder();

        try {
            List<String> keywords = getPopularWords(text);
            if (keywords.size() > 0) {
                stringBuilder.append("Keywords: ");
                int i = 0;
                for (String str : keywords) {
                    if (i < keywords.size() - 1) {
                        stringBuilder.append(str).append(", ");
                    } else {
                        stringBuilder.append(str);
                    }
                    i++;
                }
                stringBuilder.append("\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Spannable spannable = new SpannableString(text);
        stringBuilder.append(text);
        try {
            HashMap<Integer, HashSet<String>> map = getNerWords(text);
            HashSet<String> people = map.get(NER.PERSON);
            HashSet<String> organizations = map.get(NER.ORGANIZATION);
            HashSet<String> locations = map.get(NER.LOCATION);

            spannable = new SpannableString(stringBuilder.toString());
            for (String str : people) {
                int start = stringBuilder.indexOf(str);
                int end = stringBuilder.indexOf(str) + str.length();
                if (start >= 0 && end > 0 && end < stringBuilder.length()) {
                    spannable.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                } //text = text.replaceAll(str, "<font color='#EE0000'>" + str + "</font>");
            }
            for (String str : organizations) {
                int start = stringBuilder.indexOf(str);
                int end = stringBuilder.indexOf(str) + str.length();
                if (start >= 0 && end > 0 && end < stringBuilder.length()) {
                    spannable.setSpan(new ForegroundColorSpan(Color.GREEN), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }
                //text = text.replaceAll(str, "<font color='#EE8888'>" + str + "</font>");
            }
            for (String str : locations) {
                int start = stringBuilder.indexOf(str);
                int end = stringBuilder.indexOf(str) + str.length();
                if (start >= 0 && end > 0 && end < stringBuilder.length()) {
                    spannable.setSpan(new ForegroundColorSpan(Color.BLUE), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                }
                //text = text.replaceAll(str, "<font color='#E01110'>" + str + "</font>");
            }
            stringBuilder.append(text);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return spannable;
    }

    public static boolean isInit() {
        return isInit;
    }
}
