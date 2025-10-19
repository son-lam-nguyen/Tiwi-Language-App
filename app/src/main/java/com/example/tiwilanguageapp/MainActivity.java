package com.example.tiwilanguageapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TiwiMain";

    private SentenceDoc doc;
    private boolean isTeacher = true;

    private final List<Sentence> displayed = new ArrayList<>();
    private ArrayAdapter<Sentence> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ---------- Status bar ----------
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.WHITE);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        // ---------- Get extras from HomeActivity ----------
        String categoryTitle = getIntent().getStringExtra("category_title");
        String assetFile = getIntent().getStringExtra("asset_file");
        if (assetFile == null) assetFile = "sentences.json"; // fallback
        if (categoryTitle != null) setTitle(categoryTitle);

        // ---------- Views ----------
        RadioButton rbTeacher = findViewById(R.id.rbTeacher);
        RadioButton rbStudent = findViewById(R.id.rbStudent);
        ListView listView = findViewById(R.id.listView);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        if (listView == null) {
            Toast.makeText(this, "activity_main.xml is missing a ListView with id 'listView'", Toast.LENGTH_LONG).show();
            Log.e(TAG, "Missing ListView id=listView in activity_main.xml");
            return;
        }

        // ---------- Load sentences JSON ----------
        try {
            File state = IoUtils.appFile(this, "sentences_state.json");
            IoUtils.copyAssetToFile(this, assetFile, state); // copy selected category JSON
            String json = IoUtils.readTextFile(state);

            doc = new Gson().fromJson(json, SentenceDoc.class);
            if (doc == null || doc.sentences == null) {
                doc = new SentenceDoc();
                doc.sentences = new ArrayList<>();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to load sentences", e);
            Toast.makeText(this, "Could not load " + assetFile, Toast.LENGTH_LONG).show();
            doc = new SentenceDoc();
            doc.sentences = new ArrayList<>();
        }

        // ---------- Build displayed list ----------
        displayed.clear();
        displayed.addAll(doc.sentences);

        // ---------- Adapter ----------
        adapter = new ArrayAdapter<Sentence>(this, R.layout.item_sentence_arrow, displayed) {
            @NonNull
            @Override
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                View v = (convertView != null)
                        ? convertView
                        : LayoutInflater.from(getContext()).inflate(R.layout.item_sentence_arrow, parent, false);

                TextView tvWord = v.findViewById(R.id.tvWord);
                TextView tvMeaning = v.findViewById(R.id.tvMeaning);

                Sentence s = getItem(position);
                if (s != null) {
                    if (tvWord != null) tvWord.setText(s.text == null ? "" : s.text);

                    if (tvMeaning != null) {
                        String en = (s.english == null) ? "" : s.english.trim();
                        if (en.isEmpty()) {
                            tvMeaning.setVisibility(View.GONE);
                        } else {
                            tvMeaning.setVisibility(View.VISIBLE);
                            tvMeaning.setText(en);
                        }
                    }
                }
                return v;
            }
        };
        listView.setAdapter(adapter);

        // ---------- Bottom navigation ----------
        if (bottomNav != null) {
            bottomNav.setItemIconTintList(null);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent home = new Intent(this, HomeActivity.class);
                    home.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(home);
                    return true;
                } else if (id == R.id.nav_video) {
                    startActivity(new Intent(this, VideosActivity.class));
                    return true;
                } else if (id == R.id.nav_favorites) {
                    startActivity(new Intent(this, FavoriteSentencesActivity.class));
                    return true;
                } else if (id == R.id.nav_quiz) {
                    startActivity(new Intent(this, QuizActivity.class));
                    return true;
                } else if (id == R.id.nav_phrasebook) {
                    Toast.makeText(this, "Phrasebook (coming soon)", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
        } else {
            Log.w(TAG, "No bottomNav in activity_main.xml (that’s okay).");
        }

        // ---------- Role toggle ----------
        if (rbTeacher != null) rbTeacher.setOnCheckedChangeListener((g, checked) -> { if (checked) isTeacher = true; });
        if (rbStudent != null) rbStudent.setOnCheckedChangeListener((g, checked) -> { if (checked) isTeacher = false; });

        // ---------- List click ----------
        listView.setOnItemClickListener((parent, view, pos, id) -> {
            if (pos < 0 || pos >= displayed.size()) return;
            Sentence s = displayed.get(pos);
            Intent it = new Intent(this, SentenceDetailActivity.class);
            it.putExtra("sentenceId", s.id);
            it.putExtra("sentenceText", s.text);
            it.putExtra("english", s.english);
            it.putExtra("isTeacher", isTeacher);
            startActivity(it);
        });
    }
}
