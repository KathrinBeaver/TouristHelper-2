package com.hse.touristhelper.text_mining;

import android.content.Context;

import com.aliasi.chunk.Chunk;
import com.aliasi.chunk.Chunking;
import com.aliasi.chunk.NBestChunker;
import com.aliasi.util.AbstractExternalizable;
import com.aliasi.util.ScoredObject;
import com.hse.touristhelper.utils.FileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Alex on 15.05.2016.
 */

public class NER {

    private static final String MODEL = "ne-en-news-muc6.AbstractCharLmRescoringChunker";

    public static final Integer PERSON = 1;
    public static final Integer LOCATION = 2;
    public static final Integer ORGANIZATION = 3;

    private static final String PERSON_TYPE = "PERSON";
    private static final String ORGANIZ_TYPE = "ORGANIZATION";
    private static final String LOCATION_TYPE = "LOCATION";

    private HashMap<Integer, HashSet<String>> mWords;

    private NBestChunker mChunker;

    public NER(Context context) {
        try {
            mChunker = (NBestChunker) AbstractExternalizable.readObject(FileUtil.getFileFromAssets(context, MODEL));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public HashMap<Integer, HashSet<String>> confidenceChunks(String text) throws IOException, ClassNotFoundException {
        Iterator<ScoredObject<Chunking>> it = mChunker.nBest(text.toCharArray(), 0, text.length(), 15);
        mWords = new HashMap<>();
        HashSet<String> people = new HashSet<>();
        HashSet<String> organizations = new HashSet<>();
        HashSet<String> locations = new HashSet<>();

        for (int n = 0; it.hasNext(); ++n) {
            ScoredObject so = it.next();
            double jointProb = so.score();
            Chunking chunking = (Chunking) so.getObject();
            Set<Chunk> mChunks = chunking.chunkSet();
            for (Chunk chunk : mChunks) {
                int start = chunk.start();
                int end = chunk.end();
                String phrase = text.substring(start, end);

                if (chunk.type().contains(PERSON_TYPE)) {
                    if (!organizations.contains(phrase) && !locations.contains(phrase)) {
                        people.add(phrase);
                    }
                } else if (chunk.type().contains(LOCATION_TYPE)) {
                    if (!organizations.contains(phrase) && !people.contains(phrase)) {
                        locations.add(phrase);
                    }
                } else if (chunk.type().contains(ORGANIZ_TYPE)) {
                    if (!people.contains(phrase) && !locations.contains(phrase)) {
                        organizations.add(phrase);
                    }
                }
            }
        }

        /*for (int n = 0; it.hasNext(); ++n) {
            Chunk chunk = it.next();
            double conf = Math.pow(2.0, chunk.score());
            int start = chunk.start();
            int end = chunk.end();
            String phrase = text.substring(start, end);

            if (conf > 0.7) {
                if (chunk.type().contains(PERSON_TYPE)) {
                    if (!organizations.contains(phrase) && !locations.contains(phrase)) {
                        people.add(phrase);
                    }
                } else if (chunk.type().contains(LOCATION_TYPE)) {
                    if (!organizations.contains(phrase) && !people.contains(phrase)) {
                        locations.add(phrase);
                    }
                } else if (chunk.type().contains(ORGANIZ_TYPE)) {
                    if (!people.contains(phrase) && !locations.contains(phrase)) {
                        organizations.add(phrase);
                    }
                }
            }
        }*/
        mWords.put(PERSON, people);
        mWords.put(LOCATION, locations);
        mWords.put(ORGANIZATION, organizations);

        return mWords;
    }
}
