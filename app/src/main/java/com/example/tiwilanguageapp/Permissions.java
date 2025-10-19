package com.example.tiwilanguageapp;

import android.app.Activity; import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat; import androidx.core.content.ContextCompat;

public class Permissions {
    public static final int REQ_REC = 1001;
    public static boolean ensureRecordAudio(Activity a){
        if(ContextCompat.checkSelfPermission(a, android.Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(a, new String[]{android.Manifest.permission.RECORD_AUDIO}, REQ_REC);
            return false;
        }
        return true;
    }
}
