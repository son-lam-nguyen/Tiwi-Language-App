package com.example.tiwilanguageapp;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import android.Manifest;
import android.content.pm.PackageManager;
import androidx.annotation.RequiresPermission;
import androidx.core.content.ContextCompat;

public class AudioRecorder {
    private final Context ctx;

    private AudioRecord recorder;
    private Thread worker;
    private volatile boolean running = false;

    private RandomAccessFile wav;     // write WAV header + data
    private File outputFile;
    private long dataBytes = 0;

    // WAV params (good for voice + emulator)
    private static final int SR   = 44100;  // 16 kHz 16000
    private static final int CHAN = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENC  = AudioFormat.ENCODING_PCM_16BIT;

    public AudioRecorder(Context ctx) { this.ctx = ctx; }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public File start(String sentenceId, boolean isTeacher, String userId) throws IOException {
        stop(); // ensure clean

        String base = isTeacher ? "teacher" : "student/" + (userId == null ? "anon" : userId);
        String name = isTeacher ? (sentenceId + ".wav") : (sentenceId + "-" + IoUtils.nowStamp() + ".wav");
        outputFile  = IoUtils.appFile(ctx, base + "/" + name);

        // Choose stable source (fall back if needed)
        int[] sources = new int[] {
                MediaRecorder.AudioSource.VOICE_RECOGNITION,
                MediaRecorder.AudioSource.MIC,
                MediaRecorder.AudioSource.CAMCORDER
        };

        Exception last = null;
        for (int src : sources) {
            try {
                int min = AudioRecord.getMinBufferSize(SR, CHAN, ENC);
                int buf = Math.max(min, SR / 5 * 2); // >=100ms, 16-bit
                recorder = new AudioRecord(src, SR, CHAN, ENC, buf);
                recorder.startRecording();
                if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                    throw new IllegalStateException("Not recording");
                }

                // open WAV and write placeholder header
                wav = new RandomAccessFile(outputFile, "rw");
                writeWavHeader(wav, SR, 1, 16, 0);

                running = true;
                worker = new Thread(() -> {
                    byte[] buffer = new byte[buf];
                    while (running) {
                        int n = recorder.read(buffer, 0, buffer.length);
                        if (n > 0) {
                            try {
                                wav.write(buffer, 0, n);
                                dataBytes += n;
                            } catch (IOException ignored) { }
                        }
                    }
                }, "pcm-writer");
                worker.start();
                return outputFile;
            } catch (Exception e) {
                last = e;
                safeClose();
            }
        }
        throw new IOException("Failed to start recording", last);
    }

    public File stop() {
        running = false;
        if (worker != null) {
            try { worker.join(400); } catch (InterruptedException ignored) {}
            worker = null;
        }
        if (recorder != null) {
            try { recorder.stop(); } catch (Exception ignored) {}
            try { recorder.release(); } catch (Exception ignored) {}
            recorder = null;
        }
        if (wav != null) {
            try {
                // finalize header sizes
                finalizeWavHeader(wav, dataBytes);
                wav.close();
            } catch (Exception ignored) {
                // delete corrupt file
                if (outputFile != null) outputFile.delete();
                outputFile = null;
            }
            wav = null;
        }
        // very short/corrupt -> delete
        if (outputFile != null && dataBytes < 400) { // < ~0.01s
            outputFile.delete();
            outputFile = null;
        }
        File ret = outputFile;
        outputFile = null;
        dataBytes = 0;
        return ret;
    }

    private void safeClose() {
        try { if (recorder != null) { recorder.release(); recorder = null; } } catch (Exception ignored) {}
        try { if (wav != null) { wav.close(); wav = null; } } catch (Exception ignored) {}
        if (outputFile != null && outputFile.length() == 0) {
            // remove empty file
            outputFile.delete();
            outputFile = null;
        }
    }

    /** Minimal WAV writer */
    private static void writeWavHeader(RandomAccessFile out, int sampleRate, int channels, int bitsPerSample, long dataLen) throws IOException {
        out.seek(0);
        out.writeBytes("RIFF");
        out.writeInt(Integer.reverseBytes((int) (36 + dataLen)));
        out.writeBytes("WAVE");
        out.writeBytes("fmt ");
        out.writeInt(Integer.reverseBytes(16));                // subchunk1 size
        out.writeShort(Short.reverseBytes((short) 1));         // PCM
        out.writeShort(Short.reverseBytes((short) channels));
        out.writeInt(Integer.reverseBytes(sampleRate));
        int byteRate = sampleRate * channels * bitsPerSample / 8;
        out.writeInt(Integer.reverseBytes(byteRate));
        short blockAlign = (short) (channels * bitsPerSample / 8);
        out.writeShort(Short.reverseBytes(blockAlign));
        out.writeShort(Short.reverseBytes((short) bitsPerSample));
        out.writeBytes("data");
        out.writeInt(Integer.reverseBytes((int) dataLen));
    }

    private static void finalizeWavHeader(RandomAccessFile out, long dataLen) throws IOException {
        out.seek(4);
        out.writeInt(Integer.reverseBytes((int) (36 + dataLen)));
        out.seek(40);
        out.writeInt(Integer.reverseBytes((int) dataLen));
    }
}
