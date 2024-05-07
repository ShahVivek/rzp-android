package com.sprigstack.cricbuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private WebView myWebView;

    class PaymentInterface{
        @JavascriptInterface
        public void success(String data){
            Log.d("PaymentSuccess", "Order ID: " + data);
        }

        @JavascriptInterface
        public void error(String data){
            Log.d("PaymentFailure", "Order ID: " + data);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myWebView=(WebView) findViewById(R.id.webview);

        assert myWebView != null;
        myWebView.addJavascriptInterface(new PaymentInterface(), "PaymentInterface");
        String html = "<html><script> var options = {callback_url: https://rzp-fe.onrender.com/',redirect: true}</script></html>";
        myWebView.loadDataWithBaseURL("https://rzp-fe.onrender.com/",html , "text/html", "utf-8", null);

        WebView.setWebContentsDebuggingEnabled(true);
        WebSettings webSettings=myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        myWebView.loadUrl("https://rzp-fe.onrender.com/");
        myWebView.setWebViewClient(new MyWebViewClient(MainActivity.this));
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