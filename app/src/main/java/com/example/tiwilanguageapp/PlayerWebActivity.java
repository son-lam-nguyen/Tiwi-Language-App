package com.example.tiwilanguageapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class PlayerWebActivity extends AppCompatActivity {
    public static final String EXTRA_VIDEO_ID = "video_id";

    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_web);

        // Optional action bar back arrow + title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Playing video");
        }

        String id = getIntent().getStringExtra(EXTRA_VIDEO_ID);
        if (id == null) id = "";

        webView = findViewById(R.id.web);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);          // required for YouTube embed
        s.setDomStorageEnabled(true);          // some embeds rely on this
        s.setMediaPlaybackRequiresUserGesture(false);

        // Keep navigation inside the WebView
        webView.setWebViewClient(new WebViewClient());

        // Simple HTML embed
        String html =
                "<html><body style='margin:0;padding:0;background:#000;'>"
                        + "<iframe width='100%' height='100%' "
                        + "src='https://www.youtube.com/embed/" + id + "?autoplay=1' "
                        + "frameborder='0' allow='accelerometer; autoplay; clipboard-write; "
                        + "encrypted-media; gyroscope; picture-in-picture; web-share' "
                        + "allowfullscreen></iframe>"
                        + "</body></html>";

        webView.loadDataWithBaseURL(
                "https://www.youtube.com", // base URL avoids mixed content nags
                html,
                "text/html",
                "utf-8",
                null
        );
    }

    // Back arrow in the action bar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            handleBack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        handleBack();
        super.onBackPressed();
    }

    private void handleBack() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            // Either of the following is fine:
            // getOnBackPressedDispatcher().onBackPressed();
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
