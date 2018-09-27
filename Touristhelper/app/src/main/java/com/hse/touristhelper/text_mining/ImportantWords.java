package com.hse.touristhelper.text_mining;

import com.aliasi.lm.TokenizedLM;
import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import com.aliasi.util.ScoredObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

/**
 * Created by Alex on 15.05.2016.
 */
public class ImportantWords {
    private static int NGRAM = 1;

    private static String[] mExceptions = {"for", "about", "with", "the", "was", "is", "has", "can", "and", "in", "on",
            "since", "for", "ago", "before", "to", "past","till", "until", "off", "by", "of", "a"};
    private static List mExceptionsList = Arrays.asList(mExceptions);

    public static List<String> interestingPhrases(String text) throws IOException {
        TokenizerFactory tokenizerFactory = IndoEuropeanTokenizerFactory.INSTANCE;

        TokenizedLM backgroundModel = buildModel(tokenizerFactory, NGRAM, text);

        backgroundModel.sequenceCounter().prune(3);

        SortedSet<ScoredObject<String[]>> freq = backgroundModel.frequentTermSet(NGRAM, 10);

        ArrayList<String> strings = new ArrayList<>();
        Iterator<ScoredObject<String[]>> iterator = freq.iterator();
        while (iterator.hasNext()) {
            ScoredObject<String[]> obj = iterator.next();
            String[] word = obj.getObject();
            if (word != null && word[0].length() > 2 && !mExceptionsList.contains(word[0].toLowerCase())) {
                strings.add(word[0]);
            }
        }
        return strings;
    }

    private static TokenizedLM buildModel(TokenizerFactory tokenizerFactory,
                                          int ngram,
                                          String text)
            throws IOException {

        TokenizedLM model = new TokenizedLM(tokenizerFactory, ngram);
        model.handle(text);
        return model;
    }

    private static void report(SortedSet<ScoredObject<String[]>> nGrams) {
        for (ScoredObject<String[]> nGram : nGrams) {
            double score = nGram.score();
            String[] toks = nGram.getObject();
            report_filter(score, toks);
        }
    }

    private static void report_filter(double score, String[] toks) {
        String accum = "";
        for (int j = 0; j < toks.length; ++j) {
            if (nonCapWord(toks[j])) return;
            accum += " " + toks[j];
        }
        System.out.println("Score: " + score + " with :" + accum);
    }

    private static boolean nonCapWord(String tok) {
        if (!Character.isUpperCase(tok.charAt(0)))
            return true;
        for (int i = 1; i < tok.length(); ++i)
            if (!Character.isLowerCase(tok.charAt(i)))
                return true;
        return false;
    }
}
