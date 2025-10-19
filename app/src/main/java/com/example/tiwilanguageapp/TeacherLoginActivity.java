package com.example.tiwilanguageapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherLoginActivity extends AppCompatActivity {

    private EditText etTeacherId, etPasscode;
    private PasscodeManager passcodeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        etTeacherId = findViewById(R.id.etTeacherId);
        etPasscode = findViewById(R.id.etPasscode);
        Button btnLogin = findViewById(R.id.btnLogin);

        passcodeManager = PasscodeManager.getInstance(this);

        btnLogin.setOnClickListener(v -> doLogin());
    }

    private void doLogin() {
        String teacherId = etTeacherId.getText().toString().trim();
        String passcode = etPasscode.getText().toString().trim();

        if (TextUtils.isEmpty(teacherId) || TextUtils.isEmpty(passcode)) {
            Toast.makeText(this, "Please enter Teacher ID and Passcode", Toast.LENGTH_SHORT).show();
            return;
        }

        if (passcodeManager.verify(teacherId, passcode)) {
            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

            // Persist role + username so HomeActivity can read it even without extras
            getSharedPreferences("prefs", MODE_PRIVATE)
                    .edit()
                    .putString("role", RoleSelectActivity.ROLE_TEACHER) // "teacher"
                    .putString("username", teacherId)                   // use the typed teacher ID as name
                    .apply();

            // Navigate to Home and also pass fresh extras
            Intent i = new Intent(TeacherLoginActivity.this, HomeActivity.class);
            i.putExtra(RoleSelectActivity.EXTRA_ROLE, RoleSelectActivity.ROLE_TEACHER); // "teacher"
            i.putExtra("teacher_id", teacherId); // keep your existing extra
            // (Optional) also pass a generic "username" extra if you like:
            // i.putExtra(RoleSelectActivity.EXTRA_USERNAME, teacherId);
            startActivity(i);

            finishAffinity(); // optional: prevent back to login
        } else {
            Toast.makeText(this, "Invalid ID or passcode", Toast.LENGTH_SHORT).show();
        }
    }


}
