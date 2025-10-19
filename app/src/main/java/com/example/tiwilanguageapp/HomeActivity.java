package com.example.tiwilanguageapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class HomeActivity extends AppCompatActivity {

    private SentenceDoc doc;
    private final List<Sentence> displayed = new ArrayList<>();
    private ArrayAdapter<Sentence> adapter;

    // Receive result from AddSentenceActivity and refresh list if a new sentence was saved
    private final ActivityResultLauncher<Intent> addSentenceLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    doc = SentenceRepository.load(this, "sentences.json");
                    displayed.clear();
                    if (doc != null && doc.sentences != null) displayed.addAll(doc.sentences);
                    if (adapter != null) adapter.notifyDataSetChanged();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ---------- Status bar ----------
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.WHITE);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        // ---------- Drawer setup ----------
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        NavigationView navigationView = findViewById(R.id.navigationView);
        ImageButton btnHamburger = findViewById(R.id.btnHamburger);

        if (navigationView != null) {
            navigationView.setItemIconTintList(null);
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if (id == R.id.nav_donation) {
                    startActivity(new Intent(HomeActivity.this, DonationActivity.class));
                } else if (id == R.id.nav_share) {
                    Intent share = new Intent(Intent.ACTION_SEND);
                    share.setType("text/plain");
                    share.putExtra(Intent.EXTRA_SUBJECT, "Tiwi Language Practice");
                    share.putExtra(Intent.EXTRA_TEXT, "Check out the Tiwi Language Practice app!");
                    startActivity(Intent.createChooser(share, "Share via"));
                } else if (id == R.id.nav_help) {
                    startActivity(new Intent(HomeActivity.this, HelpActivity.class));
                } else if (id == R.id.nav_about) {
                    startActivity(new Intent(HomeActivity.this, AboutActivity.class));
                } else if (id == R.id.nav_favorites) {
                    startActivity(new Intent(HomeActivity.this, FavoriteSentencesActivity.class));
                }
                if (drawer != null) drawer.closeDrawer(GravityCompat.START);
                return true;
            });
            navigationView.setCheckedItem(R.id.nav_home);
        }

        if (btnHamburger != null && drawer != null) {
            btnHamburger.setOnClickListener(v -> drawer.openDrawer(GravityCompat.START));
        }

        // ---------- Views ----------
        ListView listView = findViewById(R.id.listView);
        SearchView search = findViewById(R.id.searchView);
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        Button btnLogout = findViewById(R.id.btnLogout);
        FloatingActionButton fabAddSentence = findViewById(R.id.fabAddSentence);
        ImageView ivRole = findViewById(R.id.ivRole);
        TextView tvRolePrimary = findViewById(R.id.tvRolePrimary);
        TextView tvRoleSecondary = findViewById(R.id.tvRoleSecondary);
        View roleBadge = findViewById(R.id.roleBadge);

        // ---------- Role & Teacher name ----------
        String role = getIntent().getStringExtra(RoleSelectActivity.EXTRA_ROLE);
        if (role == null) {
            role = getSharedPreferences("prefs", MODE_PRIVATE)
                    .getString("role", RoleSelectActivity.ROLE_STUDENT);
        }

        String teacherName = getIntent().getStringExtra("teacher_id");
        if (teacherName == null || teacherName.trim().isEmpty()) {
            teacherName = getSharedPreferences("prefs", MODE_PRIVATE).getString("username", null);
        }

        boolean isTeacher = RoleSelectActivity.ROLE_TEACHER.equalsIgnoreCase(role);

        if (isTeacher) {
            String greeting = (teacherName != null && !teacherName.trim().isEmpty())
                    ? "Hi, " + teacherName.trim()
                    : "Hi, Teacher";
            tvRolePrimary.setText(greeting);
            tvRoleSecondary.setText("You are logged in as Teacher");
            ivRole.setImageResource(R.drawable.ic_teacher);
            btnLogout.setVisibility(View.VISIBLE);
            fabAddSentence.setVisibility(View.VISIBLE);
        } else {
            tvRolePrimary.setText("Hi, Student");
            tvRoleSecondary.setText("You are logged in as Student");
            ivRole.setImageResource(R.drawable.ic_student);
            btnLogout.setVisibility(View.GONE);
            fabAddSentence.setVisibility(View.GONE);
        }

        // ---------- Logout button ----------
        btnLogout.setOnClickListener(v -> {
            getSharedPreferences("prefs", MODE_PRIVATE).edit().clear().apply();
            Intent intent = new Intent(HomeActivity.this, TeacherLoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // ---------- Add Sentence button (Teacher only) ----------
        fabAddSentence.setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, AddSentenceActivity.class);
            addSentenceLauncher.launch(i);
        });

        // ---------- Tint role background ----------
        int color = ContextCompat.getColor(this, isTeacher ? R.color.teal_700 : R.color.purple_700);
        ViewCompat.setBackgroundTintList(roleBadge, android.content.res.ColorStateList.valueOf(color));

        // ---------- Load sentences (persistent) ----------
        doc = SentenceRepository.load(this, "sentences.json");
        displayed.clear();
        if (doc != null && doc.sentences != null) {
            displayed.addAll(doc.sentences);
        }

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
                    tvWord.setText(s.text == null ? "" : s.text);
                    if (s.english == null || s.english.trim().isEmpty()) {
                        tvMeaning.setVisibility(View.GONE);
                    } else {
                        tvMeaning.setVisibility(View.VISIBLE);
                        tvMeaning.setText(s.english);
                    }
                }
                return v;
            }
        };
        listView.setAdapter(adapter);

        // ---------- Search ----------
        if (search != null) {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override public boolean onQueryTextSubmit(String q) { search.clearFocus(); return false; }
                @Override public boolean onQueryTextChange(String q) { filterSentences(q); return true; }
            });
        }

        // ---------- Sentence click ----------
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

        // ---------- Bottom navigation ----------
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                final int itemId = item.getItemId();
                if (itemId == R.id.nav_home) return true;
                else if (itemId == R.id.nav_video) startActivity(new Intent(this, VideosActivity.class));
                else if (itemId == R.id.nav_favorites) startActivity(new Intent(this, FavoriteSentencesActivity.class));
                else if (itemId == R.id.nav_quiz) startActivity(new Intent(this, QuizActivity.class));
                else if (itemId == R.id.nav_phrasebook) startActivity(new Intent(this, PhraseBookActivity.class));
                return true;
            });
            bottomNav.setItemIconTintList(null);
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }

    // ---------- Helpers ----------
    private void filterSentences(String query) {
        String q = (query == null) ? "" : query.trim().toLowerCase();
        List<Sentence> filtered = new ArrayList<>();
        if (doc == null || doc.sentences == null) return;

        if (q.isEmpty()) {
            filtered.addAll(doc.sentences);
        } else {
            for (Sentence s : doc.sentences) {
                String t = s.text == null ? "" : s.text.toLowerCase();
                String e = s.english == null ? "" : s.english.toLowerCase();
                if (t.contains(q) || e.contains(q)) filtered.add(s);
            }
        }

        displayed.clear();
        displayed.addAll(filtered);
        if (adapter != null) adapter.notifyDataSetChanged();
    }
}
