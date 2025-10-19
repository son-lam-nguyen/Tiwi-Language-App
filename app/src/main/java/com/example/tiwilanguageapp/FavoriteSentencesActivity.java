package com.example.tiwilanguageapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FavoriteSentencesActivity extends AppCompatActivity {

    private final List<Sentence> displayed = new ArrayList<>();
    private ArrayAdapter<Sentence> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_sentences);

        // ---- Status bar style (match Home/Videos) ----
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.WHITE);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        // ---- Top app bar ----
        MaterialToolbar topBar = findViewById(R.id.topBar);
        setSupportActionBar(topBar);
        topBar.setNavigationOnClickListener(v -> onBackPressed());

        // ---- List + adapter ----
        ListView list = findViewById(R.id.listFavs);
        adapter = new ArrayAdapter<Sentence>(this, R.layout.item_sentence_arrow, displayed) {
            @NonNull
            @Override
            public android.view.View getView(int position, @Nullable android.view.View convertView,
                                             @NonNull android.view.ViewGroup parent) {
                android.view.View v = (convertView != null)
                        ? convertView
                        : getLayoutInflater().inflate(R.layout.item_sentence_arrow, parent, false);

                Sentence s = getItem(position);
                TextView tvWord = v.findViewById(R.id.tvWord);
                TextView tvMeaning = v.findViewById(R.id.tvMeaning);

                if (tvWord != null)    tvWord.setText(s != null ? s.text : "");
                if (tvMeaning != null) tvMeaning.setText(s != null ? s.english : "");
                return v;
            }
        };
        list.setAdapter(adapter);

        list.setOnItemClickListener((parent, view, pos, id) -> {
            if (pos < 0 || pos >= displayed.size()) return;
            Sentence s = displayed.get(pos);
            Intent it = new Intent(this, SentenceDetailActivity.class);
            it.putExtra("sentenceId", s.id);
            it.putExtra("sentenceText", s.text);
            it.putExtra("english", s.english);
            it.putExtra("isTeacher", false);
            startActivity(it);
        });

        // ---- Bottom Navigation ----
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            bottomNav.setItemIconTintList(null);              // keep original icon colors
            bottomNav.setSelectedItemId(R.id.nav_favorites);   // mark current tab
            bottomNav.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    startActivity(new Intent(this, HomeActivity.class));
                } else if (itemId == R.id.nav_video) {
                    startActivity(new Intent(this, VideosActivity.class));
                } else if (itemId == R.id.nav_favorites) {
                    // already here
                } else if (itemId == R.id.nav_quiz) {
                    startActivity(new Intent(this, QuizActivity.class));
                } else if (itemId == R.id.nav_phrasebook) {
                    startActivity(new Intent(this, PhraseBookActivity.class));
                }
                overridePendingTransition(0, 0);
                return true;
            });
            bottomNav.setOnItemReselectedListener(item -> { /* no-op */ });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    private void loadFavorites() {
        try {
            // 1) Load persisted state or fallback to asset
            SentenceDoc doc = null;

            File state = IoUtils.appFile(this, "sentences_state.json");
            if (state.exists()) {
                String txt = IoUtils.readTextFile(state);
                doc = new com.google.gson.Gson().fromJson(txt, SentenceDoc.class);
            }
            if (doc == null || doc.sentences == null || doc.sentences.isEmpty()) {
                String txt = IoUtils.readAsset(this, "sentences.json");
                doc = new com.google.gson.Gson().fromJson(txt, SentenceDoc.class);
            }
            if (doc == null || doc.sentences == null) {
                displayed.clear();
                adapter.notifyDataSetChanged();
                Toast.makeText(this, "No sentences data found.", Toast.LENGTH_SHORT).show();
                return;
            }

            // 2) Build lookup by ID
            java.util.Map<String, Sentence> byId = new java.util.HashMap<>();
            for (Sentence s : doc.sentences) {
                if (s != null && s.id != null) byId.put(s.id, s);
            }

            // 3) Read favorites and resolve to Sentence objects
            java.util.Set<String> favIds = new FavoriteSentenceStore(this).getAll();

            displayed.clear();
            if (favIds != null && !favIds.isEmpty()) {
                for (String id : favIds) {
                    Sentence s = byId.get(id);
                    if (s != null) displayed.add(s);
                }
            }

            // 4) Update UI
            adapter.notifyDataSetChanged();
            if (displayed.isEmpty()) {
                Toast.makeText(this, "No favorites yet. Tap ⭐ on a sentence.", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            displayed.clear();
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Failed to load favorites: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
}
