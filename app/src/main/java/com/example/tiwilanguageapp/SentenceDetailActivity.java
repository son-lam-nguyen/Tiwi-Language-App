package com.example.tiwilanguageapp;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.media.MediaMetadataRetriever;
import com.google.gson.GsonBuilder;
import com.google.android.material.appbar.MaterialToolbar;




public class SentenceDetailActivity extends AppCompatActivity {
    private static final String TAG = "TiwiDetail";
    private static final String DEMO_USER_ID = "u123"; // demo student id

    private String sentenceId, sentenceText, english;
    private boolean isTeacher;
    // ⭐ Add these for favorites
    private FavoriteSentenceStore favStore;
    private android.view.Menu detailMenu;
    private AudioRecorder recorder;
    private AudioPlayer player;
    private File lastMyRecording;
    private final ExecutorService ioPool = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sentence_detail);

        sentenceId   = getIntent().getStringExtra("sentenceId");
        sentenceText = getIntent().getStringExtra("sentenceText");
        english      = getIntent().getStringExtra("english");
        isTeacher    = getIntent().getBooleanExtra("isTeacher", true);

        // ⭐ Add the favorites store right here
        favStore = new FavoriteSentenceStore(this);

        MaterialToolbar bar = findViewById(R.id.topAppBar);
        if (bar != null) {
            bar.setTitle(sentenceText != null ? sentenceText : "Sentence");
            setSupportActionBar(bar); // <- already added
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true); // <- ADDED
            }
        }


        TextView tvSentence     = findViewById(R.id.tvSentence);
        tvSentence.setVisibility(View.GONE);
        TextView tvGloss        = findViewById(R.id.tvGloss);
        TextView tvRole         = findViewById(R.id.tvRole);
        TextView tvStatus       = findViewById(R.id.tvStatus);
        Button   btnStart       = findViewById(R.id.btnStart);
        Button   btnStop        = findViewById(R.id.btnStop);
        Button   btnPlayTeacher = findViewById(R.id.btnPlayTeacher);
        Button   btnPlayMine    = findViewById(R.id.btnPlayMine);

        tvSentence.setText(sentenceText);
        tvGloss.setText(english == null ? "" : "English: " + english);
        tvRole.setText("Role: " + (isTeacher ? "Teacher" : "Student"));

        recorder = new AudioRecorder(this);
        player   = new AudioPlayer();

        File teacher = teacherFile();
        if (isTeacher) {
            btnStart.setText("Record");
            btnPlayMine.setVisibility(View.GONE);
        } else {
            btnStart.setText("Record");
            // preselect latest personal take so Play Mine can work immediately
            lastMyRecording = latestStudentFile(DEMO_USER_ID);
        }

        refreshButtons();

        btnStart.setOnClickListener(v -> {
            if (!Permissions.ensureRecordAudio(this)) return;
            try {
                String userId = isTeacher ? null : DEMO_USER_ID;
                File f = recorder.start(sentenceId, isTeacher, userId);
                tvStatus.setText("Recording… " + f.getName());
            } catch (Exception e) {
                tvStatus.setText("Error start: " + e.getMessage());
            }
        });

        btnStop.setOnClickListener(v -> {
            File f = recorder.stop();
            if (f != null) {
                lastMyRecording = f;
                tvStatus.setText("Saved: " + f.getAbsolutePath());
                saveRecordingToJsonAsync(isTeacher, f);
                updateLocalIndexAsync(isTeacher, f);
                refreshButtons();
            }
        });

        btnPlayTeacher.setOnClickListener(v -> {
            File tf = teacherFile();
            long size = tf.exists() ? tf.length() : 0L;

            Log.d(TAG, "PlayTeacher clicked: " + tf + " exists=" + tf.exists() + " size=" + size);
            Toast.makeText(this, tf.exists() ? "Play Teacher: found (" + size + " bytes)" : "Play Teacher: not found", Toast.LENGTH_SHORT).show();

            if (!tf.exists() || size < 800) { // gate tiny/invalid files
                tvStatus.setText("No teacher audio yet (or too short).");
                return;
            }
            tvStatus.setText("Playing teacher…");
            try {
                player.play(tf, () -> runOnUiThread(() -> tvStatus.setText("Teacher playback done.")));
            } catch (Exception e) {
                Log.e(TAG, "PlayTeacher failed", e);
                tvStatus.setText("Play Teacher failed: " + e.getMessage());
            }
        });

        btnPlayMine.setOnClickListener(v -> {
            if (lastMyRecording == null || !lastMyRecording.exists()) {
                lastMyRecording = latestStudentFile(DEMO_USER_ID); // auto-pick latest
            }
            File sf = lastMyRecording;
            long size = (sf != null && sf.exists()) ? sf.length() : 0L;

            Log.d(TAG, "PlayMine clicked: " + sf + " exists=" + (sf != null && sf.exists()) + " size=" + size);
            Toast.makeText(this, (sf != null && sf.exists()) ? "Recordings: found (" + size + " bytes)" : "Play Mine: not found", Toast.LENGTH_SHORT).show();

            if (sf == null || !sf.exists() || size < 800) {
                tvStatus.setText("Record your version first (or longer).");
                return;
            }
            tvStatus.setText("Playing my version…");
            try {
                player.play(sf, () -> runOnUiThread(() -> tvStatus.setText("My playback done.")));
            } catch (Exception e) {
                Log.e(TAG, "PlayMine failed", e);
                tvStatus.setText("Play Mine failed: " + e.getMessage());
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sentence_detail, menu);
        detailMenu = menu;
        updateFavoriteIcon(); // set correct star state at start
        return true;
    }

    @Override

    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        // Back arrow in the top app bar
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        // ⭐ Toggle favorite and show total count
        if (item.getItemId() == R.id.action_favorite) {
            if (sentenceId != null && !sentenceId.isEmpty()) {
                favStore.toggle(sentenceId);
                updateFavoriteIcon();

                int count = favStore.getAll().size(); // show how many favorites total
                Toast.makeText(
                        this,
                        (favStore.isFavorite(sentenceId) ? "Added" : "Removed") + " • Favorites total: " + count,
                        Toast.LENGTH_SHORT
                ).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateFavoriteIcon() {
        if (detailMenu == null || sentenceId == null) return;
        android.view.MenuItem it = detailMenu.findItem(R.id.action_favorite);
        if (it == null) return;
        boolean fav = favStore.isFavorite(sentenceId);
        it.setIcon(fav ? android.R.drawable.btn_star_big_on
                : android.R.drawable.btn_star_big_off);
    }


    private void refreshButtons() {
        Button btnTeacher = findViewById(R.id.btnPlayTeacher);
        boolean hasTeacher = teacherFile().exists();
        btnTeacher.setEnabled(hasTeacher);
        btnTeacher.setAlpha(hasTeacher ? 1f : 0.5f);

        if (lastMyRecording == null || !lastMyRecording.exists()) {
            lastMyRecording = latestStudentFile(DEMO_USER_ID);
        }
        Button btnMine = findViewById(R.id.btnPlayMine);
        boolean hasMine = lastMyRecording != null && lastMyRecording.exists();
        btnMine.setEnabled(hasMine);
        btnMine.setAlpha(hasMine ? 1f : 0.5f);
    }

    private File teacherFile() {
        File f = IoUtils.appFile(this, "teacher/" + sentenceId + ".m4a");
        if (!f.exists()) f = IoUtils.appFile(this, "teacher/" + sentenceId + ".wav");
        return f;
    }

    private File latestStudentFile(String userId) {
        File dir = IoUtils.appFile(this, "student/" + (userId == null ? "anon" : userId));
        File[] files = dir.listFiles((d, name) ->
                name.startsWith(sentenceId + "-") &&
                        (name.endsWith(".wav") || name.endsWith(".m4a")));
        if (files == null || files.length == 0) return null;
        java.util.Arrays.sort(files, java.util.Comparator.comparingLong(File::lastModified).reversed());
        return files[0];
    }

    private void updateLocalIndexAsync(boolean saveTeacher, File file) {
        ioPool.submit(() -> {
            try {
                File idx = IoUtils.appFile(this, "index.json");
                LocalIndex index;
                if (idx.exists()) {
                    String txt = new String(java.nio.file.Files.readAllBytes(idx.toPath()));
                    index = new Gson().fromJson(txt, LocalIndex.class);
                    if (index == null) index = new LocalIndex();
                } else {
                    index = new LocalIndex();
                }

                if (saveTeacher) {
                    index.teacherFiles.put(sentenceId, file.getAbsolutePath());
                } else {
                    index.studentFiles
                            .computeIfAbsent(sentenceId, k -> new java.util.ArrayList<>())
                            .add(file.getAbsolutePath());
                }

                String out = new Gson().toJson(index);
                java.nio.file.Files.write(idx.toPath(), out.getBytes());
                runOnUiThread(() -> Toast.makeText(this, "Saved index", Toast.LENGTH_SHORT).show());
            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "Index save error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private void saveRecordingToJsonAsync(boolean saveTeacher, File file) {
        ioPool.submit(() -> {
            try {
                // Load the writable JSON file (created by MainActivity on first run)
                File state = IoUtils.appFile(this, "sentences_state.json");
                String txt = state.exists()
                        ? IoUtils.readTextFile(state)
                        : IoUtils.readAsset(this, "sentences.json");

                SentenceDoc d = new Gson().fromJson(txt, SentenceDoc.class);
                if (d == null || d.sentences == null) return;

                // find this sentence entry
                Sentence target = null;
                for (Sentence s : d.sentences) {
                    if (sentenceId.equals(s.id)) { target = s; break; }
                }
                if (target == null) return;

                if (saveTeacher) {
                    target.teacherRecordingUrl = file.getAbsolutePath();
                } else {
                    if (target.studentRecordings == null)
                        target.studentRecordings = new java.util.ArrayList<>();

                    StudentRec rec = new StudentRec();
                    rec.userId = "u123";                           // replace with real user later
                    rec.path = file.getAbsolutePath();
                    rec.timestamp = System.currentTimeMillis();
                    rec.durationMs = getDurationMs(file);
                    target.studentRecordings.add(rec);
                }

                Gson pretty = new GsonBuilder().setPrettyPrinting().create();
                IoUtils.writeTextFile(state, pretty.toJson(d));
                runOnUiThread(() ->
                        Toast.makeText(this, "Saved in sentences_state.json", Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(this, "JSON save error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });
    }

    private long getDurationMs(File f) {
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(f.getAbsolutePath());
            String d = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            mmr.release();
            return (d == null) ? 0 : Long.parseLong(d);
        } catch (Exception e) {
            return 0;
        }
    }

    static class LocalIndex {
        java.util.Map<String, String> teacherFiles = new java.util.HashMap<>();
        java.util.Map<String, java.util.List<String>> studentFiles = new java.util.HashMap<>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) player.stop();
        if (recorder != null) recorder.stop();
    }

    @Override
    public void onRequestPermissionsResult(int code, @NonNull String[] p, @NonNull int[] r) {
        super.onRequestPermissionsResult(code, p, r);
        if (code == Permissions.REQ_REC) {
            Toast.makeText(this,
                    (r.length > 0 && r[0] == PackageManager.PERMISSION_GRANTED)
                            ? "Mic permission granted"
                            : "Mic permission required",
                    Toast.LENGTH_LONG).show();
        }
    }
}
