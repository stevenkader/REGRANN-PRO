package com.nimmble.rgpro.activity;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class parseInstagramURL extends Service {

    private ServiceHandler serviceHandler;
    static String theURL ;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {


                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("UI thread", "I am the UI thread " );

                        final WebView webview = (WebView) new WebView(parseInstagramURL.this);
                        webview.getSettings().setJavaScriptEnabled(true);
                        webview.addJavascriptInterface(new MyJavaScriptInterface(parseInstagramURL.this), "HtmlViewer");

                        webview.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageFinished(WebView view, String url) {
                                webview.loadUrl("javascript:window.HtmlViewer.showHTML" +
                                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                            }
                        });

                        webview.loadUrl(theURL);


                    }
                });

            } catch ( Exception e) {
                // Restore interrupt status.
            //    Thread.currentThread().interrupt();
                Log.d("app5", e.getMessage());
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }


        class MyJavaScriptInterface {

            MyJavaScriptInterface(Context ctx) {
            }

            @JavascriptInterface
            public void showHTML(String html) {
                Log.d("app5", "HTML : " + html);
                Intent i;


                stopSelf() ;
            }

        }
    }


    public parseInstagramURL() {
    }


    @Override
    public void onCreate() {


        // Start up the thread running the service. Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block. We also make it
        // background priority so CPU-intensive work doesn't disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        Looper serviceLooper = thread.getLooper();
        serviceHandler = new ServiceHandler(serviceLooper);

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        theURL = intent.getStringExtra("mediaUrl");
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = serviceHandler.obtainMessage();
        msg.arg1 = startId;
        serviceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }



}
