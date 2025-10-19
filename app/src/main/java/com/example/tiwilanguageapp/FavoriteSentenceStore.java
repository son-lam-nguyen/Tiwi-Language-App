package com.example.tiwilanguageapp;



import android.content.Context;
import android.content.SharedPreferences;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FavoriteSentenceStore {
    private static final String PREF = "tiwi_fav_sentences";
    private static final String KEY  = "ids";

    private final SharedPreferences sp;

    public FavoriteSentenceStore(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public boolean isFavorite(String sentenceId) {
        return getAll().contains(sentenceId);
    }

    public void toggle(String sentenceId) {
        Set<String> favs = new HashSet<>(getAll());
        if (favs.contains(sentenceId)) favs.remove(sentenceId);
        else favs.add(sentenceId);
        sp.edit().putStringSet(KEY, favs).apply();
    }

    public Set<String> getAll() {
        Set<String> s = sp.getStringSet(KEY, null);
        if (s == null) return Collections.emptySet();
        return new HashSet<>(s); // defensive copy
    }
}

