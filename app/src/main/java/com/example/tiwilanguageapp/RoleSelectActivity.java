package com.example.tiwilanguageapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.widget.TextViewCompat;

public class RoleSelectActivity extends AppCompatActivity {

    public static final String EXTRA_ROLE = "role";
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_TEACHER = "teacher";
    public static final String EXTRA_USERNAME = "username";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);

        // Initialize PasscodeManager seed (for demo teachers)
        PasscodeManager.getInstance(this).ensureSeed();

        Button btnStudent = findViewById(R.id.btnStudent);
        Button btnTeacher = findViewById(R.id.btnTeacher);

        // ==== Attach vector icons programmatically ====
        setStartIcon(btnStudent, R.drawable.ic_student);
        setStartIcon(btnTeacher, R.drawable.ic_teacher);
        // ==============================================

        // ---- Student button click ----
        btnStudent.setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)).start();

            // persist role & clear username (student doesn’t have one)
            getSharedPreferences("prefs", MODE_PRIVATE)
                    .edit()
                    .putString("role", ROLE_STUDENT)
                    .remove("username")
                    .apply();

            Intent i = new Intent(RoleSelectActivity.this, HomeActivity.class);
            i.putExtra(EXTRA_ROLE, ROLE_STUDENT);
            startActivity(i);
            finish();
        });

        // ---- Teacher button click ----
        btnTeacher.setOnClickListener(v -> {
            v.animate().scaleX(0.96f).scaleY(0.96f).setDuration(100)
                    .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100)).start();

            Intent i = new Intent(RoleSelectActivity.this, TeacherLoginActivity.class);
            startActivity(i);
        });
    }

    /**
     * Helper method to attach a white-tinted vector drawable to the start of a Button.
     */
    private void setStartIcon(Button button, int drawableRes) {
        Drawable icon = AppCompatResources.getDrawable(this, drawableRes);
        if (icon == null) return;

        icon = DrawableCompat.wrap(icon.mutate());
        TextViewCompat.setCompoundDrawableTintList(button, null);
        DrawableCompat.setTintList(icon, null);
        DrawableCompat.setTintMode(icon, null);

        TextViewCompat.setCompoundDrawablesRelativeWithIntrinsicBounds(
                button, icon, null, null, null);

        button.setCompoundDrawablePadding((int) (2 * getResources().getDisplayMetrics().density));
    }
}
