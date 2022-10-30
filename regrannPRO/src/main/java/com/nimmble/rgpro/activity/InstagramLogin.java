package com.nimmble.rgpro.activity;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nimmble.rgpro.R;


public class InstagramLogin extends AppCompatActivity {

    Button btsubmit; // this button in your xml file
    WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_login);


        webview = findViewById(R.id.browser);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setWebChromeClient(new WebChromeClient());


        WebSettings webSettings = webview.getSettings();

        webview.setInitialScale(200);

        webSettings.setBuiltInZoomControls(true);


        webSettings.setJavaScriptEnabled(true);
     //   webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setJavaScriptEnabled(true);
        //  webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/102.0.5005.63 Safari/537.36");


        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        this.webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setDatabaseEnabled(true);
        webview.getSettings().setAllowFileAccess(true);
        // webview.getSettings().setAppCacheEnabled(true);
        webview.setWebViewClient(new WebViewClient());

        webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
       // webview.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 16_0_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.6 Mobile/15E148 Safari/604.1");

        webview.setWebViewClient(new WebViewClient() {


            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Log.e("app5", errorCode + " : " + description + " at " + failingUrl);

            }


            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("app5", "in page should override " + url);

                if (url.contains("https://www.instagram.com/accounts/onetap")) {
                    try {
                        ShareActivity._this.loadPage();

                    } catch (Exception e) {
                    }
                    RegrannApp.sendEvent("private_loginbtnclickV2");
                    RegrannApp.sendEvent("InLoginRequest_logincompleteV2");
                    Toast t = Toast.makeText(getApplicationContext(), "You are now logged in!", Toast.LENGTH_LONG);
                    t.setGravity(Gravity.CENTER, 0, 0);
                    t.show();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);

                    return true;
                }

                return false;
            }
        });


//webview.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 11; Pixel 3a) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/96.0.4664.45 Mobile Safari/537.36");
//        webview.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 12_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) CriOS/69.0.3497.105 Mobile/15E148 Safari/605.1");
        if (Build.VERSION.SDK_INT >= 21) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(webview, true);
        }


        //    if (getIntent().hasExtra("clear_data")) {
        //  clearWebviewStorage();
        //   }

        webview.loadUrl("https://www.instagram.com/accounts/login/");
    }


    public void clearWebviewStorage() {
        Context context = this.getApplicationContext();
        context.deleteDatabase("webview.db");
        context.deleteDatabase("webviewCache.db");

        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().flush();
    }

    @Override
    public void onBackPressed() {
        try {
            ShareActivity._this.loadPage();
            RegrannApp.sendEvent("private_backbutton_pressed");
        } catch (Exception e) {
            RegrannApp.sendEvent("InLoginRequest_logincompleteV2");
        }
        finish();
        super.onBackPressed();
        // your code.
    }

    public void onClickSubmit(View v) {
        try {
            ShareActivity._this.loadPage();
            RegrannApp.sendEvent("private_loginbtnclickV2");
        } catch (Exception e) {
            RegrannApp.sendEvent("InLoginRequest_logincompleteV2");
        }

        finish();

    }

    public void onClickReset(View v) {
        try {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(InstagramLogin.this);

            // set dialog message
            alertDialogBuilder.setMessage("Are you sure you want to reset? If you are logged in, you will be logged off.").setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            clearWebviewStorage();
                            webview.loadUrl("https://www.instagram.com/accounts/login/");
                            RegrannApp.sendEvent("InLogin - ClearReset");
                        }


                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();

        } catch (Exception e) {

        }

    }


}
