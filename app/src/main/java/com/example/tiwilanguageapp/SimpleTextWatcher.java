package com.example.tiwilanguageapp;

import android.text.Editable;
import android.text.TextWatcher;

public class SimpleTextWatcher implements TextWatcher {
    private final Runnable after;
    public SimpleTextWatcher(Runnable after) { this.after = after; }
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
    @Override public void afterTextChanged(Editable s) { if (after != null) after.run(); }
}
