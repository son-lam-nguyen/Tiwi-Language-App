package com.example.tiwilanguageapp;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Mock donation flow:
 *  - User selects amount (or types custom) and fills card fields
 *  - Taps "Donate" -> local validation -> "Donation successful" dialog
 *  - No real payment is processed
 */
public class DonationActivity extends AppCompatActivity {

    private RadioGroup rg;
    private RadioButton rbCustom;
    private TextInputLayout tilCustom;
    private TextInputEditText etFirst, etLast, etEmail, etCustom;

    // Mock card fields
    private TextInputEditText etCardNumber, etExpiry, etCvc, etCardName;

    private TextView tvTotal;
    private MaterialButton btnDonate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Keep toolbar below status bar (no edge-to-edge on this screen)
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        getWindow().setStatusBarColor(Color.WHITE);
        new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView())
                .setAppearanceLightStatusBars(true);

        setContentView(R.layout.activity_donation);

        // Toolbar (pads for status bar height on devices with cutout)
        MaterialToolbar bar = findViewById(R.id.topBar);
        if (bar != null) {
            ViewCompat.setOnApplyWindowInsetsListener(bar, (v, insets) -> {
                Insets sb = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                v.setPadding(v.getPaddingLeft(), sb.top, v.getPaddingRight(), v.getPaddingBottom());
                return insets;
            });
            bar.setTitle("Make a Donation");
            bar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // Basic inputs
        etFirst = findViewById(R.id.etFirst);
        etLast  = findViewById(R.id.etLast);
        etEmail = findViewById(R.id.etEmail);

        // Amount controls
        rg       = findViewById(R.id.rgAmounts);
        rbCustom = findViewById(R.id.rbCustom);
        tilCustom = findViewById(R.id.tilCustom);
        etCustom  = findViewById(R.id.etCustom);

        // Card inputs (mock)
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry     = findViewById(R.id.etExpiry);
        etCvc        = findViewById(R.id.etCvc);
        etCardName   = findViewById(R.id.etCardName);

        tvTotal   = findViewById(R.id.tvTotal);
        btnDonate = findViewById(R.id.btnDonate);
        btnDonate.setText("Donate");

        // Amount listeners
        rg.setOnCheckedChangeListener((g, id) -> updateTotalAndToggle());
        rbCustom.setOnCheckedChangeListener((b, checked) -> {
            tilCustom.setEnabled(checked);
            etCustom.setEnabled(checked);
            etCustom.setFocusable(checked);
            etCustom.setFocusableInTouchMode(checked);
            etCustom.setClickable(checked);
            if (checked) etCustom.requestFocus();
            updateTotalAndToggle();
        });
        etCustom.addTextChangedListener(new SimpleTextWatcher(this::updateTotalAndToggle));

        // Initial state: no amount selected, custom disabled
        tilCustom.setEnabled(false);
        etCustom.setEnabled(false);
        etCustom.setFocusable(false);
        etCustom.setClickable(false);
        rg.clearCheck();
        updateTotalAndToggle();

        // Donate click: validate & show success popup
        btnDonate.setOnClickListener(v -> doDonate());
    }

    /** Update total text and enable/disable Donate button */
    private void updateTotalAndToggle() {
        double amt = getSelectedAmount();
        tvTotal.setText(NumberFormat.getCurrencyInstance(Locale.US).format(amt));
        boolean canDonate = amt > 0;
        btnDonate.setEnabled(canDonate);
        btnDonate.setAlpha(canDonate ? 1f : 0.5f);
    }

    /** Read the chosen/typed amount */
    private double getSelectedAmount() {
        int id = rg.getCheckedRadioButtonId();
        if (id == R.id.rb5) return 5;
        if (id == R.id.rb10) return 10;
        if (id == R.id.rb20) return 20;
        if (id == R.id.rb50) return 50;
        if (id == R.id.rb100) return 100;
        if (id == R.id.rb500) return 500;
        if (id == R.id.rb1000) return 1000;
        if (id == R.id.rbCustom) {
            String s = text(etCustom);
            if (s.isEmpty()) return 0;
            try { return Double.parseDouble(s); } catch (Exception ignored) { return 0; }
        }
        return 0;
    }

    /** Handle Donate */
    private void doDonate() {
        String first = text(etFirst);
        String last  = text(etLast);
        String email = text(etEmail);
        double amount = getSelectedAmount();

        // Basic field validation (UI demo level)
        if (TextUtils.isEmpty(first)) { etFirst.setError("Enter firstname"); etFirst.requestFocus(); return; }
        else etFirst.setError(null);

        if (TextUtils.isEmpty(last))  { etLast.setError("Enter lastname"); etLast.requestFocus(); return; }
        else etLast.setError(null);

        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            etEmail.setError("Enter a valid email"); etEmail.requestFocus(); return;
        } else etEmail.setError(null);

        if (amount <= 0) {
            Toast.makeText(this, "Please select or enter a donation amount.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mock card checks
        String number = text(etCardNumber).replaceAll("\\s", "");
        String expiry = text(etExpiry).trim();   // expect MM/YY
        String cvc    = text(etCvc).trim();
        String name   = text(etCardName).trim();

        if (number.length() < 12) { etCardNumber.setError("Enter a valid card number"); etCardNumber.requestFocus(); return; }
        else etCardNumber.setError(null);

        if (!expiry.matches("^(0[1-9]|1[0-2])\\s*/\\s*\\d{2}$")) { etExpiry.setError("Use MM/YY"); etExpiry.requestFocus(); return; }
        else etExpiry.setError(null);

        if (cvc.length() < 3 || cvc.length() > 4) { etCvc.setError("3–4 digits"); etCvc.requestFocus(); return; }
        else etCvc.setError(null);

        if (name.isEmpty()) { etCardName.setError("Enter cardholder name"); etCardName.requestFocus(); return; }
        else etCardName.setError(null);

        // Success (mock)
        String niceAmt = NumberFormat.getCurrencyInstance(Locale.US).format(amount);
        new MaterialAlertDialogBuilder(this)
                .setTitle("Thank you!")
                .setMessage("Donation successful.\nAmount: " + niceAmt)
                .setPositiveButton("Close", (d, w) -> finish())
                .show();
    }

    private static String text(TextInputEditText e) {
        return (e == null || e.getText() == null) ? "" : e.getText().toString().trim();
    }
}
