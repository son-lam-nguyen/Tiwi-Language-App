package com.example.tiwilanguageapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasscodeManager {

    private static final String PREF = "teachers_prefs";
    private static final String SEEDED = "seeded_v3";
    private final SharedPreferences sp;

    private static PasscodeManager INSTANCE;

    public static synchronized PasscodeManager getInstance(Context ctx) {
        if (INSTANCE == null) INSTANCE = new PasscodeManager(ctx.getApplicationContext());
        return INSTANCE;
    }

    private PasscodeManager(Context ctx) {
        sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    /** Call once on app start to create example teacher accounts. */
    public void ensureSeed() {
        if (sp.getBoolean(SEEDED, false)) return;

        // Example teachers — change these!
        putOrUpdateTeacher("son", "123456");
        putOrUpdateTeacher("himalaya", "123456");
        putOrUpdateTeacher("protsan", "123456");
        putOrUpdateTeacher("bipol", "123456");
        putOrUpdateTeacher("cat", "123456");

        sp.edit().putBoolean(SEEDED, true).apply();
    }

    /** Create or update a teacher's passcode (stored as hash). */
    public void putOrUpdateTeacher(String teacherId, String passcodePlain) {
        String hash = hash(passcodePlain);
        sp.edit().putString(key(teacherId), hash).apply();
    }

    /** Verify teacherId + plain passcode. */
    public boolean verify(String teacherId, String passcodePlain) {
        String stored = sp.getString(key(teacherId), null);
        if (stored == null) return false;
        String candidate = hash(passcodePlain);
        return stored.equals(candidate);
    }

    private String key(String teacherId) {
        return "t_" + teacherId.toLowerCase().trim();
    }

    private String hash(String s) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] out = md.digest(s.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(out, Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            // Fallback: never happens on Android
            return s;
        }
    }
}
