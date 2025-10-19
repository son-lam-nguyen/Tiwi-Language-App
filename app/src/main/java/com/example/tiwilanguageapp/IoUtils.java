package com.example.tiwilanguageapp;

import android.content.Context;
import java.io.*; import java.nio.charset.StandardCharsets; import java.time.Instant;

public class IoUtils {
    public static String readAsset(Context ctx, String name) throws IOException {
        try (InputStream in = ctx.getAssets().open(name); ByteArrayOutputStream out = new ByteArrayOutputStream()){
            byte[] buf=new byte[4096]; int n; while((n=in.read(buf))>=0) out.write(buf,0,n);
            return out.toString(StandardCharsets.UTF_8.name());
        }
    }
    public static String nowStamp(){ return Instant.now().toString().replaceAll("[:.]", "-"); }
    public static File appFile(Context ctx, String relative){ File f=new File(ctx.getFilesDir(), relative); File p=f.getParentFile(); if(p!=null)p.mkdirs(); return f; }
    public static String toLocalUrl(File f){ return "local://" + f.getAbsolutePath(); }

    public static void copyAssetToFile(Context ctx, String assetName, File dst) throws IOException {
        dst.getParentFile().mkdirs();
        try (InputStream in = ctx.getAssets().open(assetName);
             OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192]; int n;
            while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        }
    }
    public static String readTextFile(File f) throws IOException {
        return new String(java.nio.file.Files.readAllBytes(f.toPath()));
    }
    public static void writeTextFile(File f, String s) throws IOException {
        f.getParentFile().mkdirs();
        java.nio.file.Files.write(f.toPath(), s.getBytes());
    }
}
