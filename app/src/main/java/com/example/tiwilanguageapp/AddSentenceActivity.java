package com.example.tiwilanguageapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AddSentenceActivity extends AppCompatActivity {

    private EditText etTiwi, etEnglish;
    private SentenceDoc doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_sentence);
        setTitle("Add Sentence");

        // Status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.WHITE);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        etTiwi = findViewById(R.id.etTiwi);
        etEnglish = findViewById(R.id.etEnglish);
        Button btnSave = findViewById(R.id.btnSave);
        Button btnCancel = findViewById(R.id.btnCancel);

        doc = SentenceRepository.load(this, "sentences.json");
        if (doc == null || doc.sentences == null) {
            doc = new SentenceDoc();
            doc.sentences = new ArrayList<>();
        }

        btnSave.setOnClickListener(v -> {
            String tiwi = etTiwi.getText().toString().trim();
            String english = etEnglish.getText().toString().trim();

            if (tiwi.isEmpty() || english.isEmpty()) {
                Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Sentence s = new Sentence();
            s.id = "id_" + System.currentTimeMillis();
            s.text = tiwi;
            s.english = english;
            doc.sentences.add(0, s);

            if (SentenceRepository.save(this, doc)) {
                setResult(RESULT_OK);
                Toast.makeText(this, "Sentence added!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to save", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> finish());

        // Bottom navigation (no overlap)
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            bottomNav.getMenu().findItem(R.id.nav_home).setChecked(true);

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                Intent intent = null;

                if (id == R.id.nav_home)
                    intent = new Intent(this, HomeActivity.class);
                else if (id == R.id.nav_video)
                    intent = new Intent(this, VideosActivity.class);
                else if (id == R.id.nav_favorites)
                    intent = new Intent(this, FavoriteSentencesActivity.class);
                else if (id == R.id.nav_quiz)
                    intent = new Intent(this, QuizActivity.class);
                else if (id == R.id.nav_phrasebook)
                    intent = new Intent(this, PhraseBookActivity.class);

                if (intent != null) {
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            });
            bottomNav.setItemIconTintList(null);
        }
    }
}
