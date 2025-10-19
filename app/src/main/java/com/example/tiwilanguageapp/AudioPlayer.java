// AudioPlayer.java
package com.example.tiwilanguageapp;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class AudioPlayer {
    private MediaPlayer mp;

    public void play(File file, Runnable onDone) throws IOException {
        stop();

        if (file == null || !file.exists() || file.length() < 600) {
            throw new IOException("Audio file missing or too small: " +
                    (file == null ? "null" : file.getAbsolutePath()));
        }

        mp = new MediaPlayer();
        mp.setAudioAttributes(new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build());

        FileInputStream fis = new FileInputStream(file);
        mp.setDataSource(fis.getFD());
        fis.close();

        mp.setOnCompletionListener(m -> {
            stop();
            if (onDone != null) onDone.run();
        });
        mp.setOnErrorListener((m, what, extra) -> { stop(); return true; });

        mp.prepare();
        mp.start();
    }

    public void stop() {
        if (mp != null) {
            try { mp.stop(); } catch (Exception ignored) {}
            try { mp.release(); } catch (Exception ignored) {}
            mp = null;
        }
    }
}
