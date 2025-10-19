package com.example.tiwilanguageapp;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;

public class SentenceRepository {
    private static final String TAG = "SentenceRepo";
    private static final String STATE_FILE = "sentences_state.json";

    /** Load sentences:
     *  - If state doesn't exist (first run), seed it from the asset.
     *  - Otherwise keep existing user-edited state.
     */
    public static SentenceDoc load(Context ctx, String assetFile) {
        try {
            if (assetFile == null || assetFile.trim().isEmpty()) assetFile = "sentences.json";

            File state = IoUtils.appFile(ctx, STATE_FILE);

            // Seed only once
            if (!state.exists() || state.length() == 0) {
                IoUtils.copyAssetToFile(ctx, assetFile, state);
            }

            String json = IoUtils.readTextFile(state);
            SentenceDoc doc = new Gson().fromJson(json, SentenceDoc.class);
            if (doc == null || doc.sentences == null) {
                doc = new SentenceDoc();
                doc.sentences = new ArrayList<>();
            }
            return doc;
        } catch (Exception e) {
            Log.e(TAG, "Failed to load sentences", e);
            SentenceDoc empty = new SentenceDoc();
            empty.sentences = new ArrayList<>();
            return empty;
        }
    }

    /** Save sentences to the persistent state file. */
    public static boolean save(Context ctx, SentenceDoc doc) {
        try {
            if (doc == null) return false;
            File state = IoUtils.appFile(ctx, STATE_FILE);
            String json = new Gson().toJson(doc);
            IoUtils.writeTextFile(state, json);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Failed to save sentences", e);
            return false;
        }
    }
}
