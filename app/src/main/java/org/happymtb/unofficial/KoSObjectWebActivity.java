package org.happymtb.unofficial;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by Adrian on 22/08/2015.
 */
public class KoSObjectWebActivity extends AppCompatActivity {

    String url = "";

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        url = getIntent().getExtras().getString("url");

        WebView webView = findViewById(R.id.webView);
//        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                view.getContext().startActivity(intent);
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.kos_object_web_menu, menu);
//        menu.findItem(R.id.kos_object_favorite).setVisible(!mIsSaved && !mIsSold);
//        menu.findItem(R.id.kos_object_unfavorite).setVisible(mIsSaved);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        if (item.getItemId() == R.id.kos_object_share) {
            shareObject();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareObject() {
        String message = "Hej! Jag vill tipsa om en annons: " + url;
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share)));
    }

}