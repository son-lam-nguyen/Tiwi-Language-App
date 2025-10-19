package com.example.tiwilanguageapp;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Match Donation page status bar style
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.WHITE);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_help);

        // Toolbar identical to Donation page
        MaterialToolbar bar = findViewById(R.id.topBar);
        if (bar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(bar, (v, insets) -> {
                Insets sb = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                v.setPadding(v.getPaddingLeft(), sb.top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
            bar.setTitle("Help & Support");
            bar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // --- Button actions ---
        MaterialButton btnEmail = findViewById(R.id.btnEmail);
        MaterialButton btnCall  = findViewById(R.id.btnCallSupport);
        MaterialButton btnSms   = findViewById(R.id.btnSmsSupport);

        // Email button
        btnEmail.setOnClickListener(v -> {
            try {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.setType("message/rfc822");
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@tiwilanguageapp.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "Tiwi Language App - Help Request");
                startActivity(Intent.createChooser(email, "Send email"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Call button
        btnCall.setOnClickListener(v -> {
            Intent call = new Intent(Intent.ACTION_DIAL);
            call.setData(Uri.parse("tel:0401234567"));
            startActivity(call);
        });

        // SMS button
        btnSms.setOnClickListener(v -> {
            Intent sms = new Intent(Intent.ACTION_VIEW);
            sms.setData(Uri.parse("sms:0401234567"));
            sms.putExtra("sms_body", "Hello, I need help with the Tiwi Language App.");
            startActivity(sms);
        });
    }
}
