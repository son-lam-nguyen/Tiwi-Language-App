package com.example.tiwilanguageapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    // --- UI ---
    private ImageView ivPhoto; // photo question
    private TextView tvTitle, tvQuestion, tvProgress, tvScore, tvFeedback;
    private RadioGroup rgOptions;
    private RadioButton rb1, rb2, rb3, rb4;
    private Button btnNext;

    // --- State ---
    private final List<PhotoQuestion> questions = new ArrayList<>();
    private int index = 0;
    private int score = 0;
    private int best = 0;

    private boolean checkedPhase = false; // false = Check, true = Next/Finish
    private int[] currentOrder = new int[]{0, 1, 2, 3};
    private int defaultTextColor;
    private final Random rnd = new Random();

    private SharedPreferences prefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Edge-to-edge so BottomNav hugs the bottom
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        // --- find views (ids from your layout) ---
        ivPhoto     = findViewById(R.id.ivPhoto);
        tvTitle     = findViewById(R.id.tvTitle);
        tvQuestion  = findViewById(R.id.tvQuestion);
        tvProgress  = findViewById(R.id.tvProgress);
        tvScore     = findViewById(R.id.tvScore);
        tvFeedback  = findViewById(R.id.tvFeedback);
        rgOptions   = findViewById(R.id.rgOptions);
        rb1         = findViewById(R.id.rb1);
        rb2         = findViewById(R.id.rb2);
        rb3         = findViewById(R.id.rb3);
        rb4         = findViewById(R.id.rb4);
        btnNext     = findViewById(R.id.btnNext);

        defaultTextColor = rb1.getCurrentTextColor();

        prefs = getSharedPreferences("quiz", MODE_PRIVATE);
        best = prefs.getInt("best", 0);

        seedQuestions();     // put your real images/words here
        updateUI();

        btnNext.setOnClickListener(v -> onNextOrCheck());

        // ---- Bottom Navigation (same behavior as other pages) ----
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        if (bottomNav != null) {
            bottomNav.setItemIconTintList(null);
            bottomNav.setSelectedItemId(R.id.nav_quiz);

            // keep bar flush with bottom edge (gestural nav safe area)
            ViewCompat.setOnApplyWindowInsetsListener(bottomNav, (view, insets) -> {
                int sysBottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), sysBottom);
                return insets;
            });

            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, HomeActivity.class));
                } else if (id == R.id.nav_video) {
                    startActivity(new Intent(this, VideosActivity.class));
                } else if (id == R.id.nav_favorites) {
                    startActivity(new Intent(this, FavoriteSentencesActivity.class));
                } else if (id == R.id.nav_quiz) {
                    // already here
                } else if (id == R.id.nav_phrasebook) {
                    startActivity(new Intent(this, PhraseBookActivity.class));
                }
                overridePendingTransition(0, 0);
                return true;
            });
        }

        // Also pad the ScrollView for bottom insets so content never hides
        View scroll = findViewById(R.id.scroll);
        if (scroll != null) {
            ViewCompat.setOnApplyWindowInsetsListener(scroll, (view, insets) -> {
                int bottom = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
                view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(),
                        view.getPaddingBottom() + bottom);
                return insets;
            });
        }
    }

    // ----------------- DATA -----------------
    private void seedQuestions() {
        // Place these drawables in res/drawable/
        questions.add(new PhotoQuestion(
                "What does this photo mean in Tiwi?",
                R.drawable.pirrampurra,
                new String[]{"Pirrampurra", "Ngawa", "Pwulima", "Palya"},
                0));

        questions.add(new PhotoQuestion(
                "What does this photo mean in Tiwi?",
                R.drawable.water,
                new String[]{"Ngawa", "Pirrampurra", "Maka", "Pwulima"},
                0));

        questions.add(new PhotoQuestion(
                "What does this photo mean in Tiwi?",
                R.drawable.night,
                new String[]{"Pwulima", "Palya", "Ngawa", "Jilamara"},
                0));

        questions.add(new PhotoQuestion(
                "What does this photo mean in Tiwi?",
                R.drawable.thank_you,
                new String[]{"Palya", "Pirrampurra", "Ngawa", "Kuruwala"},
                0));
    }

    // --------------- UI FLOW ----------------
    private void updateUI() {
        if (index >= questions.size()) { finishQuiz(); return; }

        PhotoQuestion q = questions.get(index);

        checkedPhase = false;
        tvFeedback.setVisibility(View.GONE);
        clearHighlights();
        setChoicesEnabled(true);

        if (ivPhoto != null) {
            if (q.imageResId != 0) ivPhoto.setImageResource(q.imageResId);
            else ivPhoto.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        tvTitle.setText("Tiwi Photo Quiz");
        tvQuestion.setText(q.prompt);

        // shuffle options
        currentOrder = new int[]{0, 1, 2, 3};
        for (int i = currentOrder.length - 1; i > 0; i--) {
            int j = rnd.nextInt(i + 1);
            int t = currentOrder[i]; currentOrder[i] = currentOrder[j]; currentOrder[j] = t;
        }

        rb1.setText(q.options[currentOrder[0]]);
        rb2.setText(q.options[currentOrder[1]]);
        rb3.setText(q.options[currentOrder[2]]);
        rb4.setText(q.options[currentOrder[3]]);
        rgOptions.clearCheck();

        tvProgress.setText("Question " + (index + 1) + "/" + questions.size());
        tvScore.setText("Score: " + score + "   |   Best: " + best);
        btnNext.setText("Check");
    }

    private void onNextOrCheck() {
        if (!checkedPhase) {
            // ---- CHECK ----
            int checkedId = rgOptions.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Please choose an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            int shownIndex = (checkedId == R.id.rb1) ? 0 :
                    (checkedId == R.id.rb2) ? 1 :
                            (checkedId == R.id.rb3) ? 2 : 3;

            PhotoQuestion q = questions.get(index);
            int originalIndex = currentOrder[shownIndex];
            boolean correct = (originalIndex == q.correctIndex);
            if (correct) score++;

            highlightResult(shownIndex, q);

            tvFeedback.setVisibility(View.VISIBLE);
            if (correct) {
                tvFeedback.setText("✅ Correct!");
                tvFeedback.setTextColor(0xFF2E7D32);
            } else {
                tvFeedback.setText("❌ Incorrect. Correct: " + q.options[q.correctIndex]);
                tvFeedback.setTextColor(0xFFC62828);
            }

            setChoicesEnabled(false);
            checkedPhase = true;
            btnNext.setText(index == questions.size() - 1 ? "Finish" : "Next");

        } else {
            // ---- NEXT ----
            index++;
            updateUI();
        }
    }

    private void finishQuiz() {
        if (score > best) {
            best = score;
            prefs.edit().putInt("best", best).apply();
        }
        new AlertDialog.Builder(this)
                .setTitle("Quiz finished")
                .setMessage("Your score: " + score + "/" + questions.size() + "\nBest: " + best)
                .setCancelable(false)
                .setPositiveButton("OK", (d, w) -> finish())
                .show();
    }

    // --------------- Helpers ----------------
    private void setChoicesEnabled(boolean enabled) {
        for (int i = 0; i < rgOptions.getChildCount(); i++) {
            rgOptions.getChildAt(i).setEnabled(enabled);
        }
    }

    private void clearHighlights() {
        rb1.setTextColor(defaultTextColor);
        rb2.setTextColor(defaultTextColor);
        rb3.setTextColor(defaultTextColor);
        rb4.setTextColor(defaultTextColor);
    }

    private void highlightResult(int shownIndex, PhotoQuestion q) {
        int correctShownIndex = 0;
        for (int i = 0; i < 4; i++) {
            if (currentOrder[i] == q.correctIndex) { correctShownIndex = i; break; }
        }
        RadioButton[] rbs = new RadioButton[]{rb1, rb2, rb3, rb4};
        rbs[correctShownIndex].setTextColor(0xFF2E7D32); // green
        if (shownIndex != correctShownIndex) {
            rbs[shownIndex].setTextColor(0xFFC62828);    // red
        }
    }

    // --- Model (photo + options) ---
    static class PhotoQuestion {
        String prompt;
        @DrawableRes int imageResId;
        String[] options;
        int correctIndex;
        PhotoQuestion(String prompt, @DrawableRes int imageResId, String[] options, int correctIndex) {
            this.prompt = prompt;
            this.imageResId = imageResId;
            this.options = options;
            this.correctIndex = correctIndex;
        }
    }
}
