package com.example.tiwilanguageapp;

import android.graphics.Color;
import android.os.Bundle;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Match Donation and Help page style ---
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.WHITE);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_about);

        // --- Toolbar setup (identical behavior) ---
        MaterialToolbar bar = findViewById(R.id.topBar);
        if (bar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(bar, (v, insets) -> {
                Insets sb = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                v.setPadding(v.getPaddingLeft(), sb.top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
            bar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // --- App information ---
        String version = "N/A";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        TextView tvAppName = findViewById(R.id.tvAppName);
        TextView tvVersion = findViewById(R.id.tvVersion);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        TextView tvDescription = findViewById(R.id.tvDescription);

        tvAppName.setText("Tiwi Language Practice");
        tvVersion.setText("Version: " + version);
        tvAuthor.setText("Author: Tiwi Language App-2 Group");
        tvDescription.setText("This app helps teachers, students, and community members "
                + "preserve and practice the Tiwi language with recordings, practice tools, "
                + "and cultural learning resources.");
    }
}
