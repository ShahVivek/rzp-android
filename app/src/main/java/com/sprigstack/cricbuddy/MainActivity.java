package com.sprigstack.cricbuddy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWebView=(WebView) findViewById(R.id.webview);

        assert myWebView != null;
        WebView.setWebContentsDebuggingEnabled(true);
        WebSettings webSettings=myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
//        myWebView.loadUrl("https://rzp-fe.onrender.com/");
        myWebView.loadUrl("https://dev.cricbuddy.in/home?platform=android");

        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("mobile-booking")) {
                    // Open new activity for Razorpay payment
                    Intent intent = new Intent(MainActivity.this, RazorpayActivity.class);
                    intent.putExtra("paymentUrl", url);
//                    startActivity(intent);
                    startActivityForResult(intent, 1);
                    return true; // URL handled
                }
                return false; // Let the WebView handle the URL normally
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                // Get URL from intent data
                String url = data.getStringExtra("url");
                Log.d("url success=====", url);
                myWebView.loadUrl(url);
            } else {
                // Handle payment failure or cancellation
                myWebView.loadUrl("https://example.com/failure");
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(myWebView.canGoBack()) {
            myWebView.goBack();
        } else{
            super.onBackPressed();
        }
    }
}