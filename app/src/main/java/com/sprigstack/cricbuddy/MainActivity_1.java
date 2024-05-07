package com.sprigstack.cricbuddy;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;
import android.webkit.PermissionRequest;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.os.Build;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.webkit.ValueCallback;
import android.widget.RelativeLayout;

public class MainActivity_1 extends AppCompatActivity {
    private WebView myWebView;
    Context context = this;
    private Handler handler = new Handler();

    private Activity activity;

    private static final String TAG = MainActivity.class.getSimpleName();
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
        webSettings.setSaveFormData(true);
        webSettings.setSupportZoom(false);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        webSettings.setBuiltInZoomControls(false);
        webSettings.setDisplayZoomControls(false);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.9999.99 Mobile Safari/537.36");
        myWebView.loadUrl("https://rzp-fe.onrender.com/");
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Log.d("WebViewURL", "Loading URL: " + request.getUrl());
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        myWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView webViewPop = new WebView(context);
//                webViewPop.addJavascriptInterface(new PaymentInterface(), "PaymentInterface");
//                String html = "<html><script> var options = {callback_url: https://rzp-fe.onrender.com/',redirect: true}</script></html>";
//                webViewPop.loadDataWithBaseURL("https://rzp-fe.onrender.com/",html , "text/html", "utf-8", null);

                WebSettings webViewPopSettings = webViewPop.getSettings();
                webViewPopSettings.setJavaScriptEnabled(true);
//                webViewPop.setWebContentsDebuggingEnabled(true);
//                webViewPopSettings.setSupportMultipleWindows(true);
//                webViewPopSettings.setJavaScriptCanOpenWindowsAutomatically(true);
//                webViewPopSettings.setDomStorageEnabled(true);
//                webViewPopSettings.setDatabaseEnabled(true);
//                webViewPopSettings.setAllowFileAccess(true);
//                webViewPopSettings.setSaveFormData(true);
//                webViewPopSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
//                webViewPopSettings.setPluginState(WebSettings.PluginState.ON);
//                webViewPop.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//                webViewPopSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.9999.99 Mobile Safari/537.36");
//                webViewPopSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int screenHeight = displayMetrics.heightPixels;
                int topHeight = (int) (screenHeight * 0.7);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        topHeight
                );
                layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                RelativeLayout parentLayout = findViewById(R.id.parentId);
                parentLayout.addView(webViewPop, layoutParams);
                webViewPop.setVisibility(View.VISIBLE);
                webViewPop.setBackgroundColor(Color.TRANSPARENT);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(webViewPop);
                resultMsg.sendToTarget();
                webViewPop.setWebViewClient(new WebViewClient() {

                    private void hideWebView() {
                        webViewPop.setVisibility(View.INVISIBLE);
                        parentLayout.removeView(webViewPop);
                    }
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        String parsedUrl = request.getUrl().toString();
                        Log.d("parsedUrl=====", parsedUrl);
                        if (!parsedUrl.startsWith("https") || !parsedUrl.startsWith("http")) {
                            try {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(request.getUrl().toString()));
                                startActivityForResult(i, 2001);
                            } catch (ActivityNotFoundException ignored) {

                            }
                            hideWebView();
                            return true;
                        }
                        webViewPop.setBackgroundColor(Color.WHITE);
                        if (parsedUrl.contains("api.razorpay.com") && parsedUrl.contains("callback")) {
                            if (parsedUrl.contains("status=failed") || parsedUrl.contains("status=authorized")) {
                                hideWebView();
                            }
                        }
                        return super.shouldOverrideUrlLoading(view, request);
                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.d("Child WebViewURL", "onPageFinished: " + url);
                        if (url.contains("api.razorpay.com") && url.contains("callback")) {
                            handler.postDelayed(() -> hideWebView(), 2000);
                        }
                        super.onPageFinished(view, url);
                    }
                });
                return true;
            }

        });
    }

    @Override
    protected void onDestroy() {
        // Remove pending callbacks to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onBackPressed(){
        if(myWebView.canGoBack()) {
            myWebView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }
}