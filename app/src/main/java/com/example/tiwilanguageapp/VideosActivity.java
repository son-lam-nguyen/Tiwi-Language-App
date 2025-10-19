package com.example.tiwilanguageapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class VideosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videos);

        // ---------- Status bar (white, same as HomeActivity) ----------
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.WHITE);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        // ---------- Toolbar ----------
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ---------- RecyclerView setup ----------
        RecyclerView rv = findViewById(R.id.rvVideos);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Sample data (replace with real videos)
        List<VideoItem> data = new ArrayList<>();
        data.add(new VideoItem("IF9RD6gtlfE", "Tiwi Culture", "Example Channel"));
        data.add(new VideoItem("sWAih7ShWcY", "Tiwi Cultural Dance", "Example Channel"));
        data.add(new VideoItem("QXA3bhWd2Ro", "Tiwi Cultural Festival 2025", "Example Channel"));
        rv.setAdapter(new VideoAdapter(this, data));

        // ---------- Bottom Navigation ----------
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setItemIconTintList(null);             // keep icon colors
        bottomNav.setSelectedItemId(R.id.nav_video);     // mark this tab selected

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
            } else if (id == R.id.nav_video) {
                // Already here
            } else if (id == R.id.nav_favorites) {
                startActivity(new Intent(this, FavoriteSentencesActivity.class));
            } else if (id == R.id.nav_quiz) {
                startActivity(new Intent(this, QuizActivity.class));
            } else if (id == R.id.nav_phrasebook) {
                startActivity(new Intent(this, PhraseBookActivity.class));
            }
            overridePendingTransition(0, 0); // no animation
            return true;
        });

        bottomNav.setOnItemReselectedListener(item -> { /* No action */ });
    }
}
