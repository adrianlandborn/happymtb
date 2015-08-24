package org.happymtb.unofficial;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * Created by Adrian on 22/08/2015.
 */
public class WebViewActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setDisplayShowTitleEnabled(false);

        String url = getIntent().getExtras().getString("url");

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(url);

    }

}