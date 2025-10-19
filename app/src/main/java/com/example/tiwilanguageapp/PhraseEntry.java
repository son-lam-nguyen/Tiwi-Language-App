package com.example.tiwilanguageapp;

import androidx.annotation.Nullable;


public class PhraseEntry {
    public final String english;
    public final String tiwi;
    @Nullable
    public final Integer audioResId;

    public PhraseEntry(String english, String tiwi, @Nullable Integer audioResId) {
        this.english = english;
        this.tiwi = tiwi;
        this.audioResId = audioResId;
    }
}
