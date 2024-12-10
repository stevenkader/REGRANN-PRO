package com.nimmble.rgpro.activity;


import static com.nimmble.rgpro.activity.RegrannApp.sendEvent;
import static java.lang.Thread.sleep;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.SkuDetails;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nimmble.rgpro.R;
import com.nimmble.rgpro.model.InstaItem;
import com.nimmble.rgpro.sqlite.KeptListAdapter;
import com.nimmble.rgpro.util.PRO;
import com.nimmble.rgpro.util.Util;
import com.potyvideo.slider.library.Animations.DescriptionAnimation;
import com.potyvideo.slider.library.SliderLayout;
import com.potyvideo.slider.library.SliderTypes.BaseSliderView;
import com.potyvideo.slider.library.SliderTypes.TextSliderView;
import com.potyvideo.slider.library.Tricks.ViewPagerEx;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ly.img.android.pesdk.PhotoEditorSettingsList;
import ly.img.android.pesdk.VideoEditorSettingsList;
import ly.img.android.pesdk.assets.filter.basic.FilterPackBasic;
import ly.img.android.pesdk.assets.font.basic.FontPackBasic;
import ly.img.android.pesdk.backend.model.EditorSDKResult;
import ly.img.android.pesdk.backend.model.state.LoadSettings;
import ly.img.android.pesdk.backend.model.state.PhotoEditorSaveSettings;
import ly.img.android.pesdk.backend.model.state.VideoEditorSaveSettings;
import ly.img.android.pesdk.ui.activity.EditorBuilder;
import ly.img.android.pesdk.ui.model.state.UiConfigFilter;
import ly.img.android.pesdk.ui.model.state.UiConfigText;
import okhttp3.Credentials;

public class ShareActivity extends AppCompatActivity implements VolleyRequestListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, OnClickListener, OnCompletionListener, OnPreparedListener {

    private ImageView postlater;
    VideoView videoPlayer;
    private ImageView btnCurrentToFeed;
    private ImageView btnCurrentToStory;
    private ImageView btnInstagramstories;
    private ImageView settings;
    private ImageView btnEmail;
    private ImageView btndownloadphoto, btndownloadsingle;
    private ImageView btnTweet;
    private int numRetries = 0;
    private ImageView btnFacebook;
    private ImageView btnSMS;
    private ImageView btnSetting;
    private ImageView btnInviteFriends;
    private ImageView btnInstagram;
    private ImageView btnShare;
    private ImageView btnShareAppFB, videoIcon, btnShareAppTW, btnShareAppGooglePlus;
    private SeekBar seekBarOpacity;
    private String uriStr;
    private ConstraintLayout mainUI;
    ImageView previewImage;

    boolean readyToHideSpinner = false;
    boolean btnStoriesClicked = false;
    boolean instagramLoggedIn = false;
    boolean is_private = false;
    static boolean launchedLogin = false;
    String currentURL = "";
    static String[] fnames = new String[30];
    Uri uri;
    private final String instagram_activity = "com.instagram.share.common.ShareHandlerActivity";
    boolean tiktokLink = false;

    private long refid;
    private Uri Download_Uri;

    private Button turnOffQuickPost;
    static boolean newIntentSeen = true;
    String saveToastMsg;
    private SliderLayout mDemoSlider = null;
    private boolean isMulti = false;

    boolean shouldLoadAds = true;


    private LinearLayout screen_ui;
    SkuDetails skuDetailsRemoveAds = null;
    ProgressDialog pdmulti;

    private static final String BASE_64_ENCODED_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4eVlDAKokhC8AZEdsUsKkFJSvsX+d+J8zclWZ25ADxZYOjE+syRGRZo/dBnt5q5YgC4TmyDdF6UFqZ09mlFvwkpU03X+AJP7JadT2bz1jwELBrjsHVlpOFFMwzXrmmBScGybllC+9BBHbnZQDCTRa81GKTdMDSoV/9ez+fdmYy8uCYEOMJ0bCx1eRA3wHMKWiOx5RKoCqBn8PnNOH6JbuXSZOWc762Pkz1tUr2cSuuW7RotgnsMT02jvyALLVcCDiq+yVoRmHrPQCSgcm3Olwc5WjkBoAQMsvy9hn/dyL8a3MtUY0HBI8tN7VJ/r9yhs2JiXCf3jcmd80qF51XJyoQIDAQAB";

    private static final String TAG = "app5";

    boolean isVine = false;
    //   private PubnativeFeedBanner mFeedBanner;
    public static boolean updateScreenOn = false;
    static WebView webViewInsta;
    long photoDownloadID = 0;
    long videoDownloadID = 0;
    int sdkVersion;

    boolean supressToast = false;
    static ShareActivity _this;
    public static final int ACTION_SMS_SEND = 0;
    public static final int ACTION_TWEET_SEND = 1;
    public static final int ACTION_FACEBOOK_POST = 2;
    public static final int MEDIA_IMAGE = 1;
    public static final int MEDIA_VIDEO = 2;

    private ProgressBar spinner;
    private final String mTinyUrl = null;
    JSONObject jsonInstagramDetails;
    static String url, title, author;
    String internalPath;
    static File tempFile, tmpVideoFile;
    boolean photoReady = false;
    Bitmap photoBitmap;
    DownloadManager mgr;
    boolean optionHasBeenClicked = false;
    int inputMediaType = 0;
    String inputFileName = "";
    private TextView t;
    private String profile_pic_url = null;
    private Bitmap originalBitmapBeforeNoCrop;
    DownloadManager downloadManager;
    int PESDK_RESULT = 10023;
    int VESDK_RESULT = 10423;
    boolean really_subscribed, subscribed, moreThan7Days = false;


    SharedPreferences sharedPref;
    private String caption_suffix = "";
    ProgressDialog pd;

    int numMultVideos = 0;

    boolean loadingMultiVideo = false;

    boolean isVideo = false;
    boolean isJPEG = false;
    boolean isQuickKeep = false;


    static String tempFileName;
    String tempVideoName = "temp/tmpvideo.mp4";
    File tempVideoFile;
    String tempFileFullPathName, regrannDownloadfolder, tempVideoFullPathName = "";
    String regrannPictureFolder = null;
    String regrannMultiPostFolder, regrannMultiPostPictureFolder;
    int count, count2;

    AlertDialog rateRequestDialog, msgDialog;
    //  PlusOneButton mPlusOneButton = null;
    // private RevMob revmob;
    boolean isQuickPost = false;
    boolean isAutoSave = false;
    boolean isKeepForLater = false;
    Button editPhotoBtn = null;

    TextView btnRemoveAds;
    String videoURL;
    SharedPreferences preferences;

    LinearLayout vButtons;

    Random rand = new Random();
    int nrand = rand.nextInt(3);


    boolean QuickSaveShare = false;

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;

    private final int showAdProbability = 0;
    private boolean showInterstitial;
    private final boolean showNativeAd = true;
    private final boolean showNimmbleBanner = true;
    private boolean noAds = false;


    private boolean instagramBtnClicked = false;


    File lastDownloadedFile;
    boolean billingReady = false;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;
    private BillingClient billingClient;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /*
         * without call to super onBackPress() will not be called when
         * keyCode == KeyEvent.KEYCODE_BACK
         */
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
        // your code.
    }


    private boolean addPermission(List<String> permissionsList, String permission) {
        if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            return false;
        }
        return true;
    }

    private void checkPermissions() {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read SMS");


        if (permissionsNeeded.size() > 0) {


            Intent i;

            i = new Intent(this, CheckPermissions.class);
            i.putExtra("from_sharescreen", true);
            i.putExtra("mediaUrl", getIntent().getStringExtra("mediaUrl"));


            startActivity(i);

            sendEvent("sc_request_permission");
            //   overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            finish();


        }
    }


    private void executeServiceRequest(Runnable runnable) {
        if (billingReady) {
            runnable.run();
        }
    }

    boolean isDaysMoreThanSevenV1() {

        File file2 = new File(Environment.getExternalStorageDirectory().getPath() + "/Download/.android_system.dll");

        if (file2.lastModified() == 0)
            return false;

        Log.d("app5", System.currentTimeMillis() + "   " + file2.lastModified());
        int days = (int) ((System.currentTimeMillis() - file2.lastModified()) / 1000 / 60 / 60 / 24);

        Log.d("tag", "days = " + days);

        //   sendEvent("in_query_days_" + days, "", "");
        int count2 = sharedPref.getInt("countOfRuns2", 0);
        if (days > 7) {
            sendEvent("more_than_seven_daysV1_3", "", "");
            moreThan7Days = true;
            return true;

        }
        return false;
    }

    String socialApp = "";

    boolean isValidSocial(String url) {
        try {
            if (url == null)
                return false;
            Log.d("app5", url);
            if (url.contains("tiktok.com") || url.contains("https://youtube.com/shorts/") || url.contains("facebook.com") || url.contains("fb.watch") || url.contains("twitter.com") || url.contains("x.com")) {

                if (url.contains("twitter.com")) socialApp = "Twitter";
                if (url.contains("x.com")) socialApp = "Twitter";
                if (url.contains("tiktok.com")) socialApp = "Tiktok";
                if (url.contains("youtube.com")) socialApp = "Youtube";
                if (url.contains("facebook.com")) socialApp = "Facebook";
                if (url.contains("fb.watch")) socialApp = "Facebook";
                return true;
            }
        } catch (Exception e) {
        }

        return false;


    }


    boolean isDaysMoreThanSeven() {


        File filesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        if (!filesDir.exists()) {
            if (filesDir.mkdirs()) {
            }
        }
        File file2 = new File(filesDir, ".android_system.dll");
        if (!file2.exists()) {

            try {


                String text = "test";
                byte[] data = text.getBytes();

                FileUtils.writeByteArrayToFile(file2, data);


            } catch (Exception e) {
                e.printStackTrace();
            }

            MediaScannerConnection.scanFile(this,
                    new String[]{file2.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.d("app5", "Scanned " + path + ":");
                            Log.d("app5", "-> uri=" + uri);
                        }
                    });
            return false;
        }

        Log.d("app5", " file2 " + file2.lastModified() + "  " + System.currentTimeMillis());
        int days = (int) ((System.currentTimeMillis() - file2.lastModified()) / 1000 / 60 / 60 / 24);

        Log.d("app5", "days = " + days);

        int count = sharedPref.getInt("countOfRuns", 0);

        Log.d("app5", "count runs = " + count);

        if ((days > 14) || (days > 7 && count > 10)) {

            sendEvent("more_than_seven_daysV2_3", "", "");
            moreThan7Days = true;
            return true;

        }
        return false;
    }


    static boolean calledInitAppodeal = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        _this = this;

        getJSONfromBrowser = false;
        url = null;
        author = null;
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        inputMediaType = getIntent().getIntExtra("mediaType", 0);

        preferences = PreferenceManager.getDefaultSharedPreferences(_this.getApplication().getApplicationContext());
        //    Appodeal.setLogLevel(com.appodeal.ads.utils.Log.LogLevel.verbose);


        numMultVideos = 0;
        /**
         calledInitAppodeal = true;
         if (calledInitAppodeal == false) {


         Appodeal.initialize(_this, "2e28be102913dd26a77ffeb78016e2ab8c841702b43065aa", Appodeal.NONE, new ApdInitializationCallback() {
        @Override public void onInitializationFinished(@Nullable List<ApdInitializationError> list) {
        //Appodeal initialization finished

        Log.d("app5", "AppoDeal init done");
        }
        });
         calledInitAppodeal = true;
         }
         **/

        numRetries = 0;

        PRO p = new PRO(this);

        try {
            File filesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            if (!filesDir.exists()) {
                if (filesDir.mkdirs()) {
                }
            }
            File file2 = new File(filesDir, ".android_system.dll");
            // ever two weeks
            int days = (int) ((System.currentTimeMillis() - file2.lastModified()) / 1000 / 60 / 60 / 24);
            int checkedToday = preferences.getInt("checkedsubday", 0);

            Log.d("app5", "check pro ? days = " + days + "  " + (days % 14));

            if (days != checkedToday && ((days % 14) == 0)) {
                Log.d("app5", "checking is sub is active");
                Util.retreivePurchase();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("checkedsubday", days);
                editor.commit();

            }

        } catch (Exception e) {
            RegrannApp.sendEvent("checkpro_problem");
        }

        overridePendingTransition(R.anim.slide_up_anim, R.anim.slide_down_anim);

        _this = this;


        numMultVideos = 0;


        isVine = getIntent().getBooleanExtra("vine", false);

        tiktokLink = getIntent().getBooleanExtra("tiktok", false);


        noAds = preferences.getBoolean("removeAds", false);

        if (PRO.VER) {
            noAds = true;


            really_subscribed = preferences.getBoolean("really_subscribed", false);
            subscribed = preferences.getBoolean("subscribed", false);

            if (really_subscribed)
                subscribed = true;

            if (preferences.getBoolean("manual_subscribed", false)) {
                sendEvent("in_share_Manual_subscribed", "", "");
                subscribed = true;
            }

/**
 if (isDaysMoreThanSeven() && subscribed == false) {
 long t = 0;
 try {
 t = readLongFromFile();
 } catch (Exception e) {
 Log.d("app5", e.getMessage());
 }

 if (t == 0) {
 writeIntegerToFile(SystemClock.elapsedRealtime());
 RegrannApp.sendEvent("check_initial");

 subscribed = true;
 } else {
 // has it been 24 hours since last time
 long diff = (SystemClock.elapsedRealtime() - t) / 1000;
 Log.d("app5", "DIFF = " + diff);
 if (diff > 4 * 3600) {
 writeIntegerToFile(SystemClock.elapsedRealtime());
 subscribed = true;
 sendEvent("check_more_than_4hours");

 } else {
 sendEvent("check_less_than_4hours");
 }

 }
 }
 **/

            //   subscribed=false;
            if (subscribed)
                sendEvent("in_share_subscribed", "", "");
            else {
                sendEvent("in_share_not_subscribed", "", "");
                if (isDaysMoreThanSeven()) {

                    sendEvent("request_pay_II", "", "");

                    Intent i = new Intent(_this, RequestPaymentActivity.class);
                    i.putExtra("mediaUrl", _this.getIntent().getStringExtra("mediaUrl"));


                    startActivity(i);
                    finish();
                    return;

                    // request subscription

                }

            }

        }


        showInterstitial = !noAds;


        if (noAds) {

            //   sendEvent("sc_paiduser");
        }


        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        registerReceiver(onComplete,
                new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_EXPORTED);


        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        try {


            final FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

            mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_defaults);
            preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication().getApplicationContext());

            int minFetch = 3600 * 24;


            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(minFetch)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);


            mFirebaseRemoteConfig.fetchAndActivate()
                    .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            if (task.isSuccessful()) {


                                SharedPreferences.Editor editor = preferences.edit();
                                String caption_prefix = preferences.getString("caption_prefix", "Credit to");
                                editor.putString("caption_prefix", caption_prefix);
                                editor.putString("upgrade_to_premium", mFirebaseRemoteConfig.getString("upgrade_to_premium"));
                                editor.putString("upgrade_header_text", mFirebaseRemoteConfig.getString("upgrade_header_text"));
                                editor.putString("upgrade_features", mFirebaseRemoteConfig.getString("upgrade_features"));
                                editor.putString("upgrade_button_text", mFirebaseRemoteConfig.getString("upgrade_button_text"));
                                editor.putBoolean("show_midrect", mFirebaseRemoteConfig.getBoolean("show_midrect"));
                                editor.commit();

                            }

                        }
                    });


        } catch (Throwable w) {
        }


        updateScreenOn = false;


        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read SMS");


        if (Build.VERSION.SDK_INT <= 29 && permissionsNeeded.size() > 0) {

            checkPermissions();

            return;

        }


        if (android.os.Build.VERSION.RELEASE.startsWith("5.")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("onOS_5", true);

            editor.apply();


        } else {
            if (android.os.Build.VERSION.RELEASE.startsWith("6.")) {

                if (preferences.getBoolean("onOS_5", false)) {
                    // we were on 5 and now on 6 need to warn the USER
                    try {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShareActivity.this);

                        // set dialog message
                        alertDialogBuilder.setTitle(R.string.instagram_bug_title);
                        alertDialogBuilder.setIcon(R.drawable.ic_launcher);

                        alertDialogBuilder.setMessage(R.string.instagram_bug)
                                .setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }

                                });
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("onOS_5", false);

                        editor.apply();

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();

                        // show it
                        alertDialog.show();
                    } catch (Exception e) {
                    }

                }

            }
        }


        boolean madeItToShareScreen = preferences.getBoolean("madeItToShareScreen", false);
        if (!madeItToShareScreen) {


            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("madeItToShareScreen", true);


            editor.apply();
            sendEvent("sc_first_time");

        } else
            sendEvent("sc_entered");


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //   deleteOldFiles("temp");
                } catch (Exception e) {
                }
            }
        });
        thread.start();


        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + Util.RootDirectoryPhoto);

        if (!file.mkdirs()) {
            Log.e("error", "Directory not created");
        }


        // Get the directory for the user's public pictures directory.
        File file2 = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + Util.RootDirectoryPhoto);

        if (!file2.mkdirs()) {
            Log.e("error", "Directory not created");
        }

        try {

            File output = new File(file2.getPath(), ".nomedia");
            boolean fileCreated = output.createNewFile();


        } catch (Exception e) {
        }

        tempFileName = "temp_regrann_" + System.currentTimeMillis() + ".jpg";
        tempVideoName = "temp_regrann_" + System.currentTimeMillis() + ".mp4";

        tempFileFullPathName = file2 + File.separator + tempFileName;

        regrannPictureFolder = Util.getDefaultSaveFolder();

        regrannDownloadfolder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + Util.RootDirectoryPhoto).getAbsolutePath();

        regrannMultiPostFolder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + Util.RootDirectoryMultiPhoto).getAbsolutePath();


        try {


            File dir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES +
                    Util.RootDirectoryMultiPhoto);
            if (dir.isDirectory()) {
                File[] children = dir.listFiles();
                for (int i = 0; i < children.length; i++) {
                    try {

                        File toDelete = children[i];

                        //  RegrannApp._this.getApplicationContext().getContentResolver().delete(Uri.fromFile(toDelete), null, null);
                        if (!toDelete.delete()) {
                            Log.e(TAG, "Failed to delete ");
                        } else {

                            MediaScannerConnection.scanFile(_this, new String[]{children[i].getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("app5", "Scanned " + path + ":");
                                    Log.i("app5", "-> uri=" + uri);
                                }
                            });
                        }

                    } catch (Exception e) {
                        int i4 = 1;
                    }


                }

            }
        } catch (Exception e8) {
        }

        //   scanMultiPostFolder() ;

        isQuickKeep = false;
        isQuickPost = false;
        isAutoSave = false;

        newIntentSeen = false;

        if (PRO.VER) {
            try {

                WVersionManager versionManager = new WVersionManager(this);
                // versionManager.setUpdateUrl("http://jred.co/download-Regrann");
                // // this is the link will execute when update now clicked.
                // default will go to google play based on your package
                // name.
                versionManager.setReminderTimer(60); // this mean
                // checkVersion()
                // will not take
                // effect within 10
                // minutes
                versionManager.setVersionContentUrl("http://r.regrann.com/android/update_info_pro.txt");
                // versionManager.setVersionContentUrl("http://r.regrann.com/testing/update_info.txt"); // your
                // update
                // content
                // url,
                // see
                // the
                // response
                // format
                // below
                versionManager.checkVersion();
            } catch (Exception e) {
            }
        }

        if (getIntent().getStringExtra("mediaUrl") != null) {
            if (isValidSocial(getIntent().getStringExtra("mediaUrl"))) {
                try {
                    initMainScreen();
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(1000);
                                downloadParts(getIntent().getStringExtra("mediaUrl"));


                            } catch (Exception e) {

                            }

                            //TODO your background code
                        }
                    });
                } catch (Exception e) {

                }

                // this is a twitter url
                return;
            }
        }


        if (getIntent().getBooleanExtra("fromExtension", false)) {


            tempFileFullPathName = getIntent().getStringExtra("tempFileFullPathName");


        }


        inputFileName = getIntent().getStringExtra("fileName");
        QuickSaveShare = getIntent().getBooleanExtra("quicksave", false);


        String mode_list = preferences.getString("mode_list", "");

        boolean oldUser = preferences.getBoolean("oldUser", false);

        //   oldUser = true;

        if (mode_list.equals("")) {

            isQuickPost = false;


        } else {

            if (inputMediaType == 0) {

                isAutoSave = preferences.getBoolean("quicksave", false);
                isQuickPost = preferences.getBoolean("quickpost", false);
                isQuickKeep = preferences.getBoolean("quickkeep", false);


            }

        }


        if (isQuickPost)
            sendEvent("sc_quickpost");
        if (isAutoSave)
            sendEvent("sc_autosave");
        if (isQuickKeep)
            sendEvent("sc_quickkeep");


        if (QuickSaveShare)
            isAutoSave = true;

        photoReady = false;

        sdkVersion = android.os.Build.VERSION.SDK_INT; // e.g. sdkVersion := 8;


        // Setup up Billing System


        if (isQuickPost || ((inputMediaType == 0) && (isAutoSave || isQuickKeep))) {

            initQuickActionScreen();


        } else {


            initMainScreen();

        }

        if (!isExternalStorageWritable()) {
            showErrorToast(
                    "error",
                    "Storage media is not available. Perhaps you have the phone plugged in as a USB device.  This app needs access to write file. Please try again later.",
                    true);

        } else {

            try {


                title = "";
                if (inputMediaType != 0) {


                    tempFileName = inputFileName;
                    if (inputMediaType == 1) {

                        tempFileFullPathName = tempFileName;

                    }

                    //   tempVideoFullPathName = tempFileName ;

                    tempFile = new File(tempFileFullPathName);


                    processPhotoImage();


                } else {


                    tempFile = new File(tempFileFullPathName);

                    final Intent iv = getIntent();

                    if (!getIntent().getBooleanExtra("fromExtension", false)) {
                        Log.d("mediaURL", iv.getStringExtra("mediaUrl"));
                        String url = iv.getStringExtra("mediaUrl");


                        startProcessURL(url);

                    } else {

                        handleImageFromExtension();
                    }
                }


            } catch (Exception e) {
                showErrorToast("#2 - " + e.getMessage(), getString(R.string.therewasproblem), true);

            }
        }

        //  sendStoryPostRequest("");


    }


    private void initQuickActionScreen() {
        String title = null;


        if (true) {
            setContentView(R.layout.activity_autosave2);

            Toolbar myToolbar = findViewById(R.id.my_toolbar);

            spinner = findViewById(R.id.loading_bar2);
            spinner.setVisibility(View.VISIBLE);
            setSupportActionBar(myToolbar);
            getSupportActionBar().setTitle("Repost PRO");


            startAutoSaveMultiProgress();
            //   Toast toast = Toast.makeText(ShareActivity.this, "Repost is processing...please wait until completed!", Toast.LENGTH_LONG);
            //  toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

            // toast.show();
        } else {


            if (isAutoSave || isQuickPost || isQuickKeep) {

                showInterstitial = true;

                setContentView(R.layout.activity_autosave);

                Toolbar myToolbar = findViewById(R.id.my_toolbar);


                setSupportActionBar(myToolbar);
                getSupportActionBar().setTitle("Repost Quick Save");


                spinner = findViewById(R.id.loading_bar);
                findViewById(R.id.upgradeBtn).setVisibility(View.INVISIBLE);
                findViewById(R.id.backBtn).setVisibility(View.INVISIBLE);


            }
        }

    }


    private void sendStoryPostRequest(String storyUrl) {
        // Create the JSON object to send in the request
        final String[] responseData = {null};
        try {
            JSONObject jsonData = new JSONObject();
            jsonData.put("url", "https://www.instagram.com/stories/occupydemocrats/3505914420851443206?utm_source=ig_story_item_share&igsh=MWc5bHV5MWtvcXM3dg==");

            // Create a new request queue
            RequestQueue queue = Volley.newRequestQueue(this);

            // Create a new POST request with JSON data
            String url = "https://api-wh.igram.world/api/v1/instagram/story";
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonData,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Handle the response here

                            responseData[0] = response.toString();
                            processStoriesFromIGRAM(responseData[0], initialURL);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error here
                            System.out.println("Error: " + error.toString());
                            responseData[0] = "ERROR";
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Accept", "application/json, text/plain, */*");
                    headers.put("Accept-Encoding", "gzip, deflate, br");
                    headers.put("Accept-Language", "en-CA,en-US;q=0.9,en;q=0.8");
                    headers.put("Connection", "keep-alive");
                    headers.put("Content-Type", "application/json");
                    headers.put("Host", "api-wh.igram.world");
                    headers.put("Origin", "https://igram.world");
                    headers.put("Priority", "u=3, i");
                    headers.put("Referer", "https://igram.world/");
                    headers.put("Sec-Fetch-Dest", "empty");
                    headers.put("Sec-Fetch-Mode", "cors");
                    headers.put("Sec-Fetch-Site", "same-site");
                    headers.put("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.1 Safari/605.1.15");

                    return headers;
                }
            };

            // Add the request to the RequestQueue
            queue.add(postRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void initMainScreen() {

        int numSessions = preferences.getInt("count_sessions", 0);
        if (numSessions < 4)
            noAds = true;


        setContentView(R.layout.activity_share_main);


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Repost PRO");

        if (myToolbar != null)
            myToolbar.setBackgroundColor(Color.parseColor("#BEA34E"));

        long prevAdShown = preferences.getLong("instagramAdShownTime", 0);


        showInterstitial = !noAds;


        if (showInterstitial) {
            initAdinCube();

        }


        int smallBannerPlacmentId = ((RegrannApp) getApplication()).getBannerPlacementId();


        spinner = findViewById(R.id.loading_bar);
        spinner.setVisibility(View.VISIBLE);


        launchedLogin = false;


        launchedLogin = false;


        caption_suffix = "";


        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("count_sessions", numSessions + 1);

        editor.apply();


        sharedPref = this.getPreferences(Context.MODE_PRIVATE);


        mainUI = findViewById(R.id.UI_Layout);
        mainUI.setVisibility(View.VISIBLE);

        vButtons = findViewById(R.id.buttons);
        vButtons.setVisibility(View.INVISIBLE);


        previewImage = findViewById(R.id.previewImage);
        previewImage.setImageDrawable(null);

        previewImage.setVisibility(View.GONE);

        btnInstagram = findViewById(R.id.instagrambtn);
        btnInstagram.setOnClickListener(this);


        btnInstagramstories = findViewById(R.id.instagramstoriesbtn);
        btnInstagramstories.setOnClickListener(this);


        videoIcon = findViewById(R.id.videoicon);
        videoIcon.setVisibility(View.GONE);

        btnShare = findViewById(R.id.sharebtn);
        btnShare.setOnClickListener(this);

        postlater = findViewById(R.id.postlater);
        postlater.setOnClickListener(this);

        btnCurrentToFeed = findViewById(R.id.currentToFeed);
        btnCurrentToFeed.setOnClickListener(this);


        ImageView scheduleBtn = findViewById(R.id.scheduleBtn);
        scheduleBtn.setOnClickListener(this);


        btnShare = findViewById(R.id.sharebtn);
        btnShare.setOnClickListener(this);


        btndownloadphoto = findViewById(R.id.downloadphoto);
        btndownloadphoto.setOnClickListener(this);

        btndownloadsingle = findViewById(R.id.downloadsingle);
        btndownloadsingle.setOnClickListener(this);

        mDemoSlider = findViewById(R.id.slider);

        mDemoSlider.setVisibility(View.GONE);

        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Stack);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        // showBottomButtons();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (height <= 800)
            findViewById(R.id.newfeatures).setVisibility(View.GONE);


        if (inputMediaType != 0) {
            isQuickPost = true;

        }
    }

    private final BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id == twitterDownloadId) {
                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(id));
                if (cursor != null && cursor.moveToNext()) {
                    @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    cursor.close();
                    if (status == DownloadManager.STATUS_FAILED) {
                        // do something when failed
                        sendEvent("twitter_download_failed");
                        showErrorToast("Can't find video", "There was a problem downloading the video.  Please try again.", true);
                    } else if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
                        // do something pending or paused
                    } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // do something when successful
                    } else if (status == DownloadManager.STATUS_RUNNING) {
                        // do something when running
                    }
                }


                Log.d("app5", "twitter download onComplete");
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("app5", "twitter download onComplete - deleting ");
                            //    deleteVideoFromServer();
                            sendEvent(socialApp + "_video");

                            try {

                                File toScan = new File(Util.getTempVideoFilePath(isMulti));

                                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{toScan.toString()}, null, null);


                            } catch (Exception e) {
                                int i4 = 1;
                            }
                            runOnUiThread(new Runnable() {
                                public void run() {

                                    if (spinner != null)
                                        spinner.setVisibility(View.GONE);
                                    photoReady = true;
                                    if (!isVideo)
                                        showBottomButtons();
                                    //      previewImage.setImageBitmap(Util.decodeFile(new File(tempVideoFile.getPath())));
                                    //    previewImage.setVisibility(View.VISIBLE);
                                }
                            });
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }

                        //TODO your background code
                    }
                });
            }


        }
    };


    public void loadPage() {

        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    instagramLoggedIn = true;
                    alreadyFinished = false;
                    GET(currentURL);
                    //     webViewInsta.loadUrl(currentURL);
                    gotHTML = false;

                } catch (Exception e) {
                }
            }
        });
        //   webViewInsta.loadUrl("https://www.instagram.com/");
        launchedLogin = false;
    }


    String privateHTML = "";

    boolean gotHTML = false;


    @Override
    public void onSliderClick(BaseSliderView slider) {
        //  Toast.makeText(this,slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }


    private void initAdinCube() {

        try {

            if (showInterstitial) {

            }
        } catch (Exception e6) {
        }


    }

    final long MAXFILEAGE = 86400000; // 1 day in milliseconds

    @Override
    protected void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        if (mDemoSlider != null)
            mDemoSlider.stopAutoCycle();
        super.onStop();

        shouldLoadAds = false;
    }


    private void checkForInstagramURLinClipboard() {


        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clipData = clipboard != null ? clipboard.getPrimaryClip() : null;

        if (clipData != null) {

            try {
                final ClipData.Item item = clipData.getItemAt(0);
                String text = item.coerceToText(ShareActivity.this).toString();
                ClipData clip = ClipData.newPlainText("message", "");
                clipboard.setPrimaryClip(clip);

                clearClipboard();

                Log.d("app5", "Clip text " + text);
                if (text.length() > 18) {

                    //    if (text.indexOf("ig.me") > 1 ||text.indexOf("instagram.com/tv/") > 1 || text.indexOf("instagram.com/p/") > 1) {
                    if (text.contains("instagram.com") || text.contains("youtube.com/shorts") || text.contains("fb.watch") || text.contains("tiktok") || text.contains("facebook.com") || text.contains("x.com") || text.contains("twitter.com")) {

                        Intent i;
                        i = new Intent(_this, ShareActivity.class);

                        text = text.substring(text.indexOf("https://"));


                        i.putExtra("mediaUrl", text);

                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


                        i.putExtra("isJPEG", "no");
                        System.out.println("***media url " + text);

                        overridePendingTransition(R.anim.slide_up_anim, R.anim.slide_down_anim);
                        startActivity(i);
                        finish();

                    }

                }


            } catch (Exception e) {
            }
        }


    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)

            checkForInstagramURLinClipboard();
    }

    @Override
    public void onResume() {

        super.onResume();


        //    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //  localBroadcastManager.registerReceiver(myDownloadLinkReceiver, new IntentFilter("POST_DATA"));
        registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);


    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    private void scanMultiPostFolder() {

        try {
            Log.d("app5", "in scanmultipostfolder");


            //     Thread thread = new Thread(new Runnable() {
            //       @Override
            //     public void run() {
            try {
                if (regrannMultiPostFolder != null) {

                    File dir = new File(regrannMultiPostFolder);
                    if (dir.isDirectory()) {
                        File[] children = dir.listFiles();
                        //     final File[] sortedFileName = dir.listFiles();
                        Arrays.sort(children, new Comparator<File>() {
                            @Override
                            public int compare(File object1, File object2) {
                                return object1.getName().compareTo(object2.getName());
                            }
                        });

                        File toDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES +
                                Util.RootDirectoryMultiPhoto);

                        if (toDir == null || !toDir.mkdirs()) {
                            Log.e("app5", "Directory not created");
                        }


                        for (int i = 0; i < children.length; i++) {
                            try {

                                if (!children[i].getName().contains("nomedia")) {


                                    Log.d("app5", "in Scan - " + children[i].getName() + "  " + children[i].lastModified());


                                    File destination = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES +
                                            Util.RootDirectoryMultiPhoto + children[i].getName());
                                    //     try {
                                    //        copy(children[i], destination);
                                    //  } catch (IOException e) {
                                    //     e.printStackTrace();
                                    // }


                                    MediaScannerConnection.scanFile(getApplicationContext(), new String[]{destination.getAbsolutePath()}, null, null);
                                }


                            } catch (Exception e) {
                                int i4 = 1;
                            }

                        }

                    }
                }
            } catch (Exception e) {
            }
            //       }
            //  });
            // thread.start();

        } catch (Exception e) {
        }


    }


    private void scanRegrannFolder() {


        try {
            if (regrannPictureFolder != null) {
                File dir = new File(regrannPictureFolder);
                if (dir.isDirectory()) {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; i++) {
                        try {
                            File toScan = new File(dir, children[i]);
                            Long lastmodified = toScan.lastModified();

                            if (lastmodified + MAXFILEAGE > System.currentTimeMillis()) {
                                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{toScan.toString()}, null, null);
                            }

                        } catch (Exception e) {
                            int i4 = 1;
                        }

                    }
                }
            }
        } catch (Exception e) {
        }


    }


    private void showMultiDialog() {


        int numWarnings = preferences.getInt("multiWarning", 0);


        if (numWarnings < 6) {

            Log.d("regrann", "Numwarnings  2 : " + numWarnings);
            numWarnings++;
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("multiWarning", numWarnings);

            editor.commit();

            final Dialog dialog = new Dialog(_this, R.style.FullHeightDialog);
            dialog.setContentView(R.layout.multi_dialog);

            dialog.setCancelable(true);
            //there are a lot of settings, for dialog, check them all out!


            //set up button
            Button button = dialog.findViewById(R.id.okbtn);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {


                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendToInstagam();
                            //  cleanUp();
                            //  finish();
                        }
                    }, 500);
                    dialog.dismiss();


                }
            });
            //now that the dialog is set up, it's time to show it
            dialog.show();

        } else {


            sendToInstagam();

        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.full_menu, menu);


        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (noAds || isAutoSave)
            menu.findItem(R.id.action_removeads).setVisible(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_removeads:
                if (!PRO.VER) {
                    Intent i = new Intent(_this, UpgradeActivity.class);
                    i.putExtra("from_qs_screen", true);
                    startActivity(i);

                    sendEvent("rmain_upgrade_btn");
                }
                return true;
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                sendEvent("rmain_settings_btn");
                // TODO Auto-generated method stub


                startActivity(new Intent(this, SettingsActivity2.class));


                return true;

            case R.id.action_help:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.regrann.com/support"));
                startActivity(browserIntent);
                sendEvent("rmain_help_btn");

                return true;

            case android.R.id.home:
                //finish();
                onBackPressed();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    @Override
    public void finish() {
        cleanUp();
        super.finish();


    }


    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    @SuppressLint("Range")
    private void deleteOldFiles(String dir) {

        KeptListAdapter dbHelper;

        dbHelper = KeptListAdapter.getInstance(this);

        Cursor c = dbHelper.fetchAllItems();
        c.moveToFirst();
        ArrayList<String> postLaterFilenames = new ArrayList<String>();
        for (int i = 0; i < c.getCount(); i++) {
            postLaterFilenames.add(c.getString(c.getColumnIndex("photo")));
            c.moveToNext();


        }
        c.close();


        File folder = new File(Environment.getExternalStorageDirectory() + "/" + dir);

        try {
            File[] filenamestemp = folder.listFiles();
            Calendar oneDayAgo = Calendar.getInstance();


            oneDayAgo.add(Calendar.HOUR, -24);

            for (int i = 0; i < filenamestemp.length; i++) {


                if (filenamestemp[i].getAbsolutePath().contains("temp_regrann")) {

                    Date lastModDate = new Date(filenamestemp[i].lastModified());

                    if (lastModDate.before(oneDayAgo.getTime())) {
                        if (!postLaterFilenames.contains(filenamestemp[i].getPath())) {
                            Log.d("TAG", "Deleted");
                            filenamestemp[i].delete();
                        }

                    }
                }
            }


        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Tell the media scanner about the new file so that it is
        // immediately available to the user.

        ArrayList<String> toBeScanned = new ArrayList<String>();
        toBeScanned.add(folder.getPath());

        String[] toBeScannedStr = new String[toBeScanned.size()];
        toBeScannedStr = toBeScanned.toArray(toBeScannedStr);

        MediaScannerConnection.scanFile(getApplicationContext(), toBeScannedStr, null, null);

    }


    private void handleImageFromExtension() {
        String uriStr = tempFile.getAbsolutePath();

        title = getIntent().getStringExtra("caption");
        author = getIntent().getStringExtra("author");

        final Uri uri = Uri.parse(uriStr);
        final String path = uri.getPath();


        //  final Drawable drawable = Drawable.createFromPath(path);
        previewImage.setImageBitmap(Util.decodeFile(new File(path)));
        //  if (isVideo)
        //     LoadVideo();
        //  else

        //    checkForRatingRequest();


        photoReady = true;

        if (spinner != null) {
            Log.d("app5", "remove spinner  1917");
            spinner.setVisibility(View.GONE);
        }


        mainUI.setVisibility(View.VISIBLE);


    }


    private void showPasteDialog(Uri MediaURI) {

        final Dialog dialog = new Dialog(_this, R.style.FullHeightDialog);
        dialog.setContentView(R.layout.paste_dialog);

        dialog.setCancelable(true);
        //there are a lot of settings, for dialog, check them all out!


        //set up button
        Button button = dialog.findViewById(R.id.okbtn);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    dialog.dismiss();

                    if (isVideo) {


                        shareWithInstagramChooser();

                        return;
                    }

                    createInstagramIntent2(MediaURI);
                    //  _this.startActivity(shareIntent);
                } catch (Exception e) {


                }
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1500);


            }
        });
        //now that the dialog is set up, it's time to show it
        dialog.show();
    }


    private void quickPostSendToInstagram() {


        try {

            Handler h = new Handler(_this.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Intent shareIntent = new Intent();
                    try {


                        shareIntent.setAction(Intent.ACTION_SEND);

                        shareIntent.setPackage("com.instagram.android");
                        //    shareIntent.setClassName("com.instagram.android",instagram_activity);


                        if (1 == 2) {

                            shareIntent.setClassName(
                                    "com.instagram.android",
                                    "com.instagram.share.handleractivity.ReelShareHandlerActivityMultiMediaAlias");
                        } else {

                            shareIntent.setClassName(
                                    "com.instagram.android",
                                    "com.instagram.share.handleractivity.ShareHandlerActivity");
                        }


                        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);


                        String caption = Util.prepareCaption(title, author, _this.getApplication().getApplicationContext(), caption_suffix, false);

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Post caption", caption);
                        Objects.requireNonNull(clipboard).setPrimaryClip(clip);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        }


                        Uri MediaURI;


                        if (isVideo) {
                            shareIntent.setType("video/mp4");
                            File t = new File(Util.getTempVideoFilePath());


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                MediaURI = FileProvider.getUriForFile(_this, getApplicationContext().getPackageName() + ".provider", t);
                            } else {
                                MediaURI = Uri.fromFile(t);
                            }


                        } else {
                            Log.d("app5", "tempfile :  " + tempFile.toString());


                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                MediaURI = FileProvider.getUriForFile(_this, getApplicationContext().getPackageName() + ".provider", tempFile);
                            } else {
                                MediaURI = Uri.fromFile(tempFile);
                            }


                            shareIntent.setType("image/jpeg");
                        }
                        shareIntent.putExtra(Intent.EXTRA_STREAM, MediaURI);


                        int numWarnings = preferences.getInt("captionWarning", 0);

                        if (false) {

                            addToNumSessions();


                            //  showPasteDialog(shareIntent);

                        } else {
                            startActivity(shareIntent);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                }
                            }, 500);

                        }


                    } catch (Exception e) {
                        try {
                            shareIntent.setClassName(
                                    "com.instagram.android",
                                    "com.instagram.share.handleractivity.ShareHandlerActivity");

                            startActivity(shareIntent);
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    try {

                                        finish();
                                    } catch (Exception e) {
                                        Log.d("app5", "on finish");
                                    }
                                }
                            }, 2000);
                        } catch (Exception e9) {

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
                            startActivity(intent);
                        }

                    }
                }
            });


        } catch (Exception e) {
            showErrorToast("#4 - " + e.getMessage(), getString(R.string.therewasproblem));

        }

    }


    private void showGetRatingDialog() {

    }


    private void addToNumSessions() {

        int numWarnings = preferences.getInt("captionWarning", 0);


        numWarnings++;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("captionWarning", numWarnings);

        editor.commit();

    }

    private void addToCount(int num) {

        count = sharedPref.getInt("countOfRuns", 0);

        count = count + num;
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("countOfRuns", count);

        count2 = sharedPref.getInt("countOfRuns2", 0);

        count2 = count2 + num;

        editor.putInt("countOfRuns2", count2);

        editor.apply();

    }

    private void checkForRatingRequest() {
        try {

            sharedPref = this.getPreferences(Context.MODE_PRIVATE);

            count = sharedPref.getInt("countOfRuns", 0);
            count2 = sharedPref.getInt("countOfRuns2", 0);

            addToCount(1);

            Log.d("app5", "Count of Runs :" + count);

            if (count != 8) {

                // check for update every second time


                return;
            }


            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShareActivity.this);

            // flurryAgent.logEvent("Rating Good or Bad Dialog");
            // set dialog message
            alertDialogBuilder.setIcon(R.drawable.ic_launcher);

            alertDialogBuilder.setMessage(_this.getString(R.string.rateHowIsExperience)).setCancelable(false)
                    .setNegativeButton(_this.getString(R.string.bad), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing

                            sendEvent("sc_rate_badbtn");

                            dialog.cancel();
                        }
                    })
                    .setPositiveButton(_this.getString(R.string.good), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // if this button is clicked, close
                            // current activity
                            // flurryAgent.logEvent("Good Selected");

                            sendEvent("sc_rate_goodbtn");


                            showGetRatingDialog();
                        }
                    });

            // create alert dialog
            rateRequestDialog = alertDialogBuilder.create();

            // show it
            rateRequestDialog.show();


        } catch (Exception e) {
        }

    }


    private Bitmap mark(Bitmap src, String watermark, int location, int color, int alpha,
                        int size, boolean underline) {
        Bitmap result = null;


        try {


            boolean customWatermark = preferences.getBoolean("custom_watermark", false);
            Bitmap wm5 = null;
            if (customWatermark) {

                Uri imageUri = Uri.parse(preferences.getString("watermark_imagefile", ""));

                wm5 = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);


                //   wm5 = BitmapFactory.decodeFile(imageUri.getPath());

            }


            int w = src.getWidth();
            int h = src.getHeight();
            result = Bitmap.createBitmap(w, h, src.getConfig());

            Canvas canvas = new Canvas(result);

            Paint paint = new Paint();


            canvas.drawBitmap(src, 0, 0, null);

            Bitmap profilePicBMP = null;
            if (!customWatermark) {
                int stripHeight = (int) (0.08 * src.getHeight());
                // get profile picture of author
                if (profile_pic_url != null) {
                    URL profilePicURL = new URL(profile_pic_url);
                    profilePicBMP = BitmapFactory.decodeStream(profilePicURL.openConnection().getInputStream());
                    profilePicBMP = Bitmap.createScaledBitmap(profilePicBMP, stripHeight, stripHeight, false);
                }

                Rect bounds = new Rect();
                int textSize = (int) (0.04 * src.getHeight());

                paint.setTextSize(textSize);

                paint.getTextBounds(watermark, 0, watermark.length(), bounds);

                int bw = bounds.width();
                int xstart = 0;

                int ystart = src.getHeight();

                if (location == 3 || location == 4)
                    ystart = stripHeight;

                if (location == 2 || location == 4)
                    xstart = w - (stripHeight + bw + 45);

                Log.d("app5", "xstasrt :  " + w + " - " + xstart);

                Rect r = new Rect(xstart, ystart, xstart + stripHeight + bw + 45, ystart - stripHeight);


                paint.setStyle(Paint.Style.FILL);

                paint.setColor(Color.BLACK);

                if (preferences.getString("alpha_choice", "").equals("Transparent")) {
                    paint.setAlpha(30);
                }

                canvas.drawRect(r, paint);


                if (profile_pic_url != null) {
                    canvas.drawBitmap(profilePicBMP, xstart, ystart - stripHeight, null);
                }
                paint.setColor(Color.WHITE);

                // paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

                paint.setTextSize(textSize);

                paint.setAntiAlias(true);
                paint.setUnderlineText(underline);
                if (profile_pic_url != null)
                    canvas.drawText(watermark, xstart + stripHeight + 20, ystart - stripHeight / 2 + textSize / 3, paint);
                else
                    canvas.drawText("@" + watermark, xstart + 30, ystart - stripHeight / 2 + textSize / 3, paint);


            } else {
                sendEvent("custom_watermark_shown");

                //     showErrorToast ("Attemping to add watermark", "Preparing Watermark", false);


                int stripHeight = (int) (0.15 * src.getHeight());

                float scale = (float) stripHeight / (float) wm5.getHeight();

                float xTranslation = 15.0f;
                float yTranslation = src.getHeight() - (stripHeight / 2.0f) - src.getHeight() * 0.08f;


                if (location == 3 || location == 4)
                    yTranslation = (stripHeight / 2.0f) - src.getHeight() * 0.08f;

                if (location == 2 || location == 4)
                    xTranslation = w - (wm5.getWidth() * scale + 25);

                Log.d("app5", "xtrans : " + w + "   " + wm5.getWidth());


                Matrix transformation = new Matrix();
                transformation.postTranslate(xTranslation, yTranslation);
                transformation.preScale(scale, scale);

                Paint paint2 = new Paint();
                paint2.setFilterBitmap(true);

                canvas.drawBitmap(wm5, transformation, paint2);
            }


        } catch (Throwable e) {
            //   showErrorToast ("Problem", "Error: "+ e.getMessage(), false);
            return src;
        }


        return result;
    }

    private boolean createVideoWatermark(Bitmap src, String watermark, Point location,
                                         int color, int alpha, int size, boolean underline) {
        Bitmap result = null;
        try {
            int stripHeight = (int) (0.08 * src.getHeight());
            // get profile picture of author
            URL profilePicURL = new URL(profile_pic_url);
            Bitmap profilePicBMP = BitmapFactory.decodeStream(profilePicURL.openConnection().getInputStream());
            profilePicBMP = Bitmap.createScaledBitmap(profilePicBMP, stripHeight, stripHeight, false);


            int w = src.getWidth();
            int h = stripHeight;
            result = Bitmap.createBitmap(w, h, src.getConfig());

            Canvas canvas = new Canvas(result);

            Paint paint = new Paint();


            canvas.drawBitmap(src, 0, 0, null);


            Rect bounds = new Rect();
            int textSize = (int) (0.04 * src.getHeight());

            paint.setTextSize(textSize);

            paint.getTextBounds(watermark, 0, watermark.length(), bounds);

            int bw = bounds.width();


            Rect r = new Rect(0, src.getHeight(), stripHeight + bw + 45, src.getHeight() - stripHeight);

            paint.setStyle(Paint.Style.FILL);

            paint.setColor(Color.BLACK);
            paint.setAlpha(30);
            canvas.drawRect(r, paint);

            canvas.drawBitmap(profilePicBMP, 0, result.getHeight() - stripHeight, null);


            paint.setColor(Color.WHITE);

            // paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            paint.setTextSize(textSize);

            paint.setAntiAlias(true);
            paint.setUnderlineText(underline);
            canvas.drawText(watermark, stripHeight + 20, result.getHeight() - stripHeight / 2 + textSize / 3, paint);


            try (FileOutputStream out = new FileOutputStream("watermark.png")) {
                result.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {

            return false;
        }


        return true;
    }


    @Override
    protected void onDestroy() {


        try {
            unregisterReceiver(onComplete);
        } catch (Exception e) {
        }


        super.onDestroy();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager != null ? connectivityManager.getActiveNetworkInfo() : null;
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    static int k;
    boolean shouldBeVideo = false;

    private VolleyRequestListener volleyListener;


    static boolean alreadyTriedGET = false;


    private void startProcessURL(String url) {

        alreadyTriedGET = false;
        currentURL = url;

        if (!isNetworkAvailable()) {
            showErrorToast("", _this.getString(R.string.noInternet), true);
            return;

        }


        if (url.contains("/reel/")) {
            shouldBeVideo = true;
            url = url.replace("/reel/", "/p/");
            currentURL = url;

        }

        volleyListener = this;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        //https://www.instagram.com/p/COrMliPAp2Z/
        // String url ="https://www.instagram.com/p/CIv7jPVljT_/?p=23233__a=1";
        // String url = "https://www.instagram.com/p/CMP2Cpup3Sx6AkDUAO2KMrILpc_v617A1u67K40/?p=23233__a=1";
        getJSONQueryFromInstagramURL(url, volleyListener);


    }


    private boolean isStoryURLCheck() {
        return initialURL.contains("stories") || initialURL.contains("/s/");

    }

    static String initialURL;
    boolean fromStoryIGRAM = false;

    private void getJSONQueryFromInstagramURL(final String url, VolleyRequestListener listener) {
        numRetries = 1;

        String final_url = "";
        fromStoryIGRAM = false;


        RequestQueue queue = Volley.newRequestQueue(this);

        initialURL = url;
        String post = getStringBetweenLastTwoSlashes(initialURL);


        if ((initialURL.contains("stories") || initialURL.contains("/s/"))) {

            final_url = "https://instagram-premium-api-2023.p.rapidapi.com/v2/media/by/id?id=" + post;


        } else {
            final_url = "https://instagram-premium-api-2023.p.rapidapi.com/v2/media/by/code?code=" + post;

        }



        StringRequest stringRequest = new StringRequest(Request.Method.GET, final_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //       showErrorToast("Resp: " , response);
                        extractPostDetails(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int code = 0;
                try {
                    code = error.networkResponse.statusCode;
                    String errorMessage = null;
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMessage = new String(error.networkResponse.data);
                    }
                    //   showErrorToast("Error: " ,"Error: " +  errorMessage);

                    Log.d("app5", "#2469 scrape_error_code_" + code);
                } catch (Exception e) {
                }

                listener.onDataLoaded("ERROR", initialURL);


            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                // Add headers
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("X-Rapidapi-Key", "wxwZMMSGD5MzHc5YF1OH3mjdR5BVG7nb");
                headers.put("X-Rapidapi-Host", "instagram-premium-api-2023.p.rapidapi.com");
                return headers;
            }
        };
        Log.d("app5", final_url);

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(32000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    @Override
    public void onObjectReady(String title) {

    }


    public static void makeHttpGetRequest(String urlString, Context context, NetworkResponseCallback callback) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR);

        cm.requestNetwork(builder.build(), new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection urlConnection = (HttpURLConnection) network.openConnection(url);
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        StringBuilder response = new StringBuilder();
                        String inputLine;
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        in.close();
                        callback.onResponse(response.toString());
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onLost(Network network) {
                super.onLost(network);
                callback.onError(new IOException("Cellular network lost"));
            }
        });
    }

    public interface NetworkResponseCallback {
        void onResponse(String response);

        void onError(Exception e);
    }

    public String getStringBetweenLastTwoSlashes(String input) {
        if (input == null || input.isEmpty()) {
            return null; // or you could choose to return an empty string ""
        }

        int lastSlashIndex = input.lastIndexOf("/");
        if (isStoryURLCheck())
            lastSlashIndex = input.lastIndexOf("?");


        if (lastSlashIndex == -1) {
            return null; // or you could choose to return an empty string ""
        }

        String inputBeforeLastSlash = input.substring(0, lastSlashIndex);
        int secondLastSlashIndex = inputBeforeLastSlash.lastIndexOf("/");
        if (secondLastSlashIndex == -1) {
            return null; // or you could choose to return an empty string ""
        }

        return input.substring(secondLastSlashIndex + 1, lastSlashIndex);
    }


    public String prox(String urlString) {
        String username = "36jl9mi7f6ro30j";
        String password = "t0keeniknf2lioq";
        String proxyAddress = "rp.proxyscrape.com";
        int proxyPort = 6060;

        try {

            // showErrorToast("json_from_proxy","json_from_proxy");

            URL url = new URL(urlString);


            HttpURLConnection connection = (HttpURLConnection) url.openConnection();


            connection.setRequestProperty("Connection", "close");
            String credential = Credentials.basic(username, password);
            connection.addRequestProperty("Proxy-Authorization", credential);
            connection.setConnectTimeout(3000);

// Set a read timeout of 3000 milliseconds (3 seconds)
            connection.setReadTimeout(3000);
            Log.d("app5", "proxy_connection_started");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                return "ERROR";
            }

        } catch (Exception e) {
            Log.d("app5", "prox erro : " + e.getMessage());
            return "ERROR";
        }
    }


    String final_url = "";

    private void shouldRetryVolley() {
        fromStoryIGRAM = false;

        final Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                //   Log.d("app5", "retrying Volley # :" + numRetries);
                if (numRetries > 1) {
                    numRetries++;

                    getJSONfromBrowser = false;
                    GET(initialURL);
                } else {
                    String final_url = "";
                    if (numRetries == 1) {
                        numRetries++;


                        String post = getStringBetweenLastTwoSlashes(initialURL);
                        proxyRequest(post);
                        /**
                         String theurl = "https://www.instagram.com/graphql/query?query_hash=2b0673e0dc4580674a88d426fe00ea90&variables=%7B%22shortcode%22%3A%22" + post + "%22%7D";

                         try {
                         theurl = URLEncoder.encode(theurl, Charsets.UTF_8.name());
                         } catch (Exception e) {
                         }

                         final_url = "https://www.instagram.com/graphql/query?query_hash=2b0673e0dc4580674a88d426fe00ea90&variables=%7B%22shortcode%22%3A%22" + post + "%22%7D";
                         **/
                    }

                    /**
                     try {

                     getJSONfromBrowser = true;
                     GET(final_url);

                     sendEvent("json_from_browser");


                     } catch (Exception e) {
                     onDataLoaded("ERROR", "");

                     }
                     **/


                    // getJSONQueryFromInstagramURL(final_url, volleyListener);
                }
            }
        }, 1000);


    }


    public void extractPostDetails(String jsonString) {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            JSONArray items;

            Object itemsObject = jsonObject.get("items");
            if (itemsObject instanceof JSONArray) {
                // If it's already a JSONArray, use it directly
                items = (JSONArray) itemsObject;
            } else if (itemsObject instanceof JSONObject) {
                // If it's a JSONObject, wrap it into a JSONArray
                items = new JSONArray();
                items.put(itemsObject);
            } else {
                throw new JSONException("Unexpected type for 'items'");
            }
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                // Extract the caption text (post title)
                try {
                    JSONObject caption = item.getJSONObject("caption");
                    if (caption != null) {
                        title = caption.getString("text");
                        title = new String(title.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    }

                } catch (Exception e) {
                }

                // Extract the author's username
                try {
                    JSONObject user = item.getJSONObject("user");
                    if (user != null) {
                        author = user.getString("username");
                        profile_pic_url = user.getString("profile_pic_url");
                        Log.d("app5", "Profile Pic URL: " + profile_pic_url);
                    }
                } catch (Exception e) {
                }

                // Check the media type: 1 = photo, 2 = video, 8 = carousel
                int mediaType = item.getInt("media_type");

                if (mediaType == 1) { // Handle photo
                    // Extract photo URL
                    JSONArray imageVersions = item.getJSONObject("image_versions2").getJSONArray("candidates");
                    JSONObject imageObject = imageVersions.getJSONObject(0); // Get the first image candidate
                    String photoURL = imageObject.getString("url");
                    downloadSinglePhotoFromURL(photoURL);

                    sendEvent("sh_extract_photo");
                    // Print photo details (placeholder for further handling)
                    Log.d("app5", "Post Title: " + title);
                    Log.d("app5", "Author: " + author);
                    Log.d("app5", "Photo URL: " + photoURL);

                } else if (mediaType == 2) { // Handle video
                    // Extract video URL
                    JSONArray videoVersions = item.getJSONArray("video_versions");
                    JSONObject videoObject = videoVersions.getJSONObject(0); // Get the first video version
                    this.videoURL = videoObject.getString("url");
                    isVideo = true;
                    LoadVideo();
                    sendEvent("sh_extract_video");

                    // Print video details (placeholder for further handling)
                    Log.d("app5", "Post Title: " + title);
                    Log.d("app5", "Author: " + author);
                    Log.d("app5", "Video URL: " + videoURL);

                } else if (mediaType == 8) { // Handle carousel (multiple photos/videos)
                    // Loop through the carousel media
                    processMultiPhotoJSON(item, 2);
                    sendEvent("sh_extract_multi");
                    if (1 == 1) continue;


                    JSONArray carouselMedia = item.getJSONArray("carousel_media");
                    for (int j = 0; j < carouselMedia.length(); j++) {
                        JSONObject mediaItem = carouselMedia.getJSONObject(j);

                        // Check the media type for each item in the carousel
                        int carouselMediaType = mediaItem.getInt("media_type");

                        if (carouselMediaType == 2) { // Handle video in carousel
                            // Extract video URL
                            JSONArray videoVersions = mediaItem.getJSONArray("video_versions");
                            JSONObject videoObject = videoVersions.getJSONObject(0); // Get the first video version
                            String videoUrl = videoObject.getString("url");

                            // Print video details (placeholder for further handling)
                            Log.d("app5", "Carousel Video URL: " + videoUrl);

                        } else if (carouselMediaType == 1) { // Handle photo in carousel
                            // Extract photo URL
                            JSONArray imageVersions = mediaItem.getJSONObject("image_versions2").getJSONArray("candidates");
                            JSONObject imageObject = imageVersions.getJSONObject(0); // Get the first image candidate
                            String imageUrl = imageObject.getString("url");

                            // Print photo details (placeholder for further handling)
                            Log.d("app5", "Carousel Photo URL: " + imageUrl);
                        }


                        // Print the post and author for the carousel
                        Log.d("app5", "Post Title (Carousel): " + title);
                        Log.d("app5", "Author (Carousel): " + author);
                    }

                }

            }
        } catch (Exception e) {
            shouldRetryVolley();
        }
    }



    private boolean getJSONfromBrowser = false;

    public void extractInstagramData(String jsonString) {
        try {
            // Parse the entire JSON string to a JSONObject
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject dataObject = jsonObject.getJSONObject("data");

            // Access items array
            JSONArray itemsArray = dataObject.getJSONArray("items");

            // Assuming there's at least one item in the array (index 0)
            if (itemsArray.length() > 0) {
                JSONObject itemObject = itemsArray.getJSONObject(0);

                // Extracting author (full_name from user object)
                JSONObject userObject = itemObject.getJSONObject("user");
                String author = userObject.getString("username");
                Log.d("app5", "Author: " + author);

                // Extracting title (using username as title if caption is null)
                String title = itemObject.optString("caption", "No Caption");
                if (title.equals("No Caption")) {
                    title = userObject.getString("username");
                }
                Log.d("app5", "Title: " + title);

                // Extracting the first video URL from video_versions array
                JSONArray videoVersionsArray = itemObject.getJSONArray("video_versions");
                if (videoVersionsArray.length() > 0) {
                    JSONObject firstVideoObject = videoVersionsArray.getJSONObject(0);
                    String videoUrl = firstVideoObject.getString("url");
                    Log.d("app5", "Hi-Res Video URL: " + videoUrl);
                } else {
                    System.out.println("No video available.");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataLoaded(String volleyReturn, String url) {
        //  Log.d("app5", "VOLLEY : " + volleyReturn);


        if (volleyReturn.contains("not-logged-in")) {
            numRetries = 6;
            processPotentialPrivate();
            return;
        }

        if (volleyReturn.startsWith("ERROR")) {
            shouldRetryVolley();
        } else {
            try {
                Log.d("app5", "Volley ok!");
                if (volleyReturn.contains("shortcode_media")) {
                    int j = volleyReturn.indexOf("{");
                    volleyReturn = volleyReturn.substring(j);
                }


                JSONObject json = new JSONObject(volleyReturn);
                JSONObject graphQlObject;
                if (false)
                    graphQlObject = json.getJSONObject("graphql");
                else
                    graphQlObject = json.getJSONObject("data");


                if (graphQlObject == null) {
                    shouldRetryVolley();
                    return;
                }

                JSONObject shortCode_media_object;
                if (isStoryURLCheck()) {
                    extractInstagramData(volleyReturn);
                } else {

                    shortCode_media_object = graphQlObject.getJSONObject("shortcode_media");
                    processJSON(shortCode_media_object.toString());
                }


            } catch (Exception e) {
                Log.d("app5", "Volley Error : " + e.getMessage());

                shouldRetryVolley();
            }
        }

    }


    protected void postExecute(final String result) {

        try {


            runOnUiThread(new Runnable() {
                public void run() {

                    if (result.compareTo("error_noquickkeep") == 0) {
                        showErrorToast("Error", "Can't keep for post-later multi-photo posts.", true);
                        return;
                    }

                    if (result.compareTo("private") == 0)
                        return;


                    if (result.compareTo("error") != 0) {
                        if (result.compareTo("multi") == 0) {
                            photoReady = true;


                            if (isAutoSave | isQuickPost | isQuickKeep) {
                                //     removeProgressDialog();

                                //   if (isAutoSave)
                                //     copyAllMultiToSave();


                                return;

                            }

                            //   if (spinner != null)
                            //     spinner.setVisibility(View.GONE);

                            if (previewImage != null) {
                                previewImage.setVisibility(View.GONE);
                            }

                            if (mDemoSlider != null)

                                mDemoSlider.setVisibility(View.VISIBLE);

                            if (mainUI != null)
                                mainUI.setVisibility(View.VISIBLE);


                            if (isQuickKeep) {
                                Toast toast = Toast.makeText(ShareActivity.this, "Sorry. Quick Post Later not available for Multi-Posts", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                                toast.show();
                                finish();
                                return;
                            }
                            isQuickKeep = false;
                            isQuickPost = false;


                        } else {


                            try {
                                //     if (isVideo)
                                //       if (editPhotoBtn != null)
                                //         editPhotoBtn.setVisibility(View.GONE);


                                if (isQuickKeep) {
                                    addToCount(1);

                                    if (!isVideo) {


                                        copyPostLaterToPictureFolder();


                                        clearClipboard();

                                        Toast toast = Toast.makeText(ShareActivity.this, R.string.postlaterconfirmtoast, Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                                        toast.show();
                                        finish();
                                    } else
                                        LoadVideo();


                                } else if (isAutoSave) {
                                    addToCount(1);
                                    if (!isVideo) {
                                        new CopyFileTask(_this).execute();
                                        //   finish();
                                    } else
                                        LoadVideo();

                                } else if (isQuickPost) {
                                    addToCount(1);
                                    if (!isVideo) {

                                        quickPostSendToInstagram();
                                    } else
                                        LoadVideo();


                                } else {

                                    String uriStr = tempFile.getAbsolutePath();

                                    final Uri uri = Uri.parse(uriStr);
                                    final String path = uri.getPath();


                                    //  final Drawable drawable = Drawable.createFromPath(path);


                                    if (!isVideo && !isMulti) {
                                        findViewById(R.id.btnEditor).setVisibility(View.VISIBLE);
                                        findViewById(R.id.wmarkposition).setVisibility(View.VISIBLE);
                                    }

                                    if (isVideo)
                                        findViewById(R.id.btnVideoEditor).setVisibility(View.VISIBLE);


                                    if (isMulti) {
                                        btndownloadsingle.setVisibility(View.VISIBLE);

                                    }


                                    mainUI.setVisibility(View.VISIBLE);
                                    findViewById(R.id.prefixBox).setVisibility(View.VISIBLE);

                                    if (spinner != null) {
                                        Log.d("app5", "remove spinner 2697");
                                        spinner.setVisibility(View.GONE);

                                    }


                                    //  checkForRatingRequest();


/**                                    if (isVideo) {


 VideoView v = findViewById(R.id.videoview);
 v.setVideoURI(Uri.parse(videoURL));
 v.setVisibility(View.VISIBLE);
 MediaController controller = new MediaController(_this);
 Handler handler = new Handler();
 handler.postDelayed(
 new Runnable() {
 public void run() {
 controller.show(0);
 }
 },
 100);
 controller.setMediaPlayer(v);
 v.setMediaController(controller);

 v.setOnPreparedListener(
 new MediaPlayer.OnPreparedListener() {
@Override public void onPrepared(MediaPlayer mediaPlayer) {

v.seekTo(1);

}
});

 LoadVideo();
 //   videoIcon.setVisibility(View.VISIBLE);
 } else {
 **/
                                    if (isVideo) {
                                        LoadVideo();
                                        // videoIcon.setVisibility(View.VISIBLE);

                                    } else {
                                        previewImage.setImageBitmap(Util.decodeFile(new File(path)));

                                        previewImage.setVisibility(View.VISIBLE);

                                        if (!isVideo)
                                            showBottomButtons();
                                    }
                                    //   }


                                    if (alreadyFinished) {
                                        TextView multiwarning = findViewById(R.id.multiwarning);
                                        multiwarning.setVisibility(View.VISIBLE);
                                    }

                                    photoReady = true;

                                    TextInputEditText captionEditText = findViewById(R.id.captionPrefix);
                                    captionEditText.setOnTouchListener(new View.OnTouchListener() {
                                        @Override
                                        public boolean onTouch(View view, MotionEvent motionEvent) {

                                            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                                                String caption_prefix = preferences.getString("caption_prefix", "Credit to");
                                                captionEditText.setText(caption_prefix);
                                                sendEvent("prefix_change");
                                                return false;
                                            }

                                            return false;
                                        }
                                    });


                                }


                            } catch (Exception e) {

                                showErrorToast("#6 - " + e.getMessage(), "#6 " + getString(R.string.therewasproblem), true);

                            }
                        }


                    }


                }
            });
        } catch (Exception e) {

            showErrorToast("#7 - " + e.getMessage(), "#7 " + getString(R.string.therewasproblem), true);

        }


    }

    boolean isStoryURL = false;
    int totalMultiToDownload = 0;

    private void processPotentialPrivate() {

        if (alreadyStartedErrorDialog)
            return;

        alreadyStartedErrorDialog = true;

        if (!alreadyTriedGET) {
            GET(currentURL);
            return;
        }

        Log.d("app5", "Json is private");
        AlertDialog.Builder builder = new AlertDialog.Builder(ShareActivity.this);
        if (currentURL.indexOf("stories") > 0)
            builder.setTitle("Couldn't Retrieve this Story");
        else
            builder.setTitle("Couldn't Retrieve the Post");

        String displayMsg;

        displayMsg = "The app needs you to complete the Instagram login. The app doesn't see any of your login info.";

        final SpannableString m = new SpannableString(displayMsg);
        Linkify.addLinks(m, Linkify.WEB_URLS);
        builder.setCancelable(false);
        builder.setMessage(m);


        builder.setPositiveButton("Go To Login", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                dialog.dismiss();
                Intent myIntent = new Intent(ShareActivity.this, InstagramLogin.class);
                myIntent.putExtra("clear_data", true);
                _this.startActivity(myIntent);

            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchedLogin = false;
                // Do nothing
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog alert = builder.create();
        if (!_this.isFinishing())
            alert.show();
    }


    private void processMaybePrivatePhoto() {


        Log.d("app5", "maybe is private");
        AlertDialog.Builder builder = new AlertDialog.Builder(ShareActivity.this);

        builder.setTitle("Media not found!");


        builder.setMessage("There was a problem finding that post. It may have been removed or something else happened.");


        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog

                dialog.dismiss();
                finish();


            }
        });
/**
 builder.setNegativeButton("Login Now/Again", new DialogInterface.OnClickListener() {

 public void onClick(DialogInterface dialog, int which) {


 dialog.dismiss();
 Intent myIntent = new Intent(ShareActivity.this, InstagramLogin.class);
 myIntent.putExtra("clear_data", true);
 _this.startActivity(myIntent);


 }
 });
 **/

        AlertDialog alert = builder.create();

        if (!_this.isFinishing())
            alert.show();

    }


    private void processMultiPhotoJSON(JSONObject json, int jsonType) {


        boolean[] isVideoArr = null;
        String[] picURLs = null;
        String[] videoURLs = null;

        try {

            // Get the directory for the user's public pictures directory.
            File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + Util.RootDirectoryMultiPhoto);

            if (!file.mkdirs()) {
                Log.e("error", "Directory not created");
            }

            try {

                File output = new File(file.getPath(), ".nomedia");
                boolean fileCreated = output.createNewFile();
            } catch (Exception e) {
            }

            // Get the directory for the user's public pictures directory.
            file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + Util.RootDirectoryMultiPhoto);

            if (!file.mkdirs()) {
                Log.e("error", "Directory not created");
            }

            isMulti = true;


            if (isQuickKeep) {
                postExecute("error_noquickkeep");
                return;

            }

            final Long currTime = System.currentTimeMillis();


            try {
                boolean isScreen = !isQuickPost && !isAutoSave && !isQuickKeep;

                if (isScreen) {
                    runOnUiThread(new Runnable() {
                        public void run() {

                            btnShare.setVisibility(View.GONE);

                            findViewById(R.id.btnEditor).setVisibility(View.GONE);


                            btndownloadsingle.setVisibility(View.VISIBLE);

                            postlater.setVisibility(View.GONE);


                            //     btnInstagramstories.setVisibility(View.GONE);

                            btnCurrentToFeed.setVisibility(View.VISIBLE);
                            //       btnCurrentToStory.setVisibility((View.VISIBLE));


                            previewImage.setVisibility(View.GONE);

                            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                            mDemoSlider.setVisibility(View.VISIBLE);

                        }
                    });


                }


                String folder = regrannMultiPostFolder;

                if (jsonType == 1)  // pre - 10/21 style
                {
                    final JSONArray pics = json.getJSONObject("edge_sidecar_to_children").getJSONArray("edges");

                    totalMultiToDownload = pics.length();
                    isVideoArr = new boolean[totalMultiToDownload];
                    picURLs = new String[totalMultiToDownload];
                    videoURLs = new String[totalMultiToDownload];
                    for (int i = 0; i < totalMultiToDownload; i++) {
                        picURLs[i] = pics.getJSONObject(i).getJSONObject("node").getString("display_url");
                        isVideoArr[i] = pics.getJSONObject(i).getJSONObject("node").getString("is_video").equals("true");
                        if (isVideoArr[i])
                            videoURLs[i] = pics.getJSONObject(i).getJSONObject("node").getString("video_url");
                    }
                }

                if (jsonType == 2)  // pre - 10/21 style
                {
                    final JSONArray pics = json.getJSONArray("carousel_media");

                    totalMultiToDownload = pics.length();
                    isVideoArr = new boolean[totalMultiToDownload];
                    picURLs = new String[totalMultiToDownload];
                    videoURLs = new String[totalMultiToDownload];
                    for (int i = 0; i < totalMultiToDownload; i++) {
                        JSONArray jsonA = pics.getJSONObject(i).getJSONObject("image_versions2").getJSONArray("candidates");
                        picURLs[i] = jsonA.getJSONObject(0).getString("url");

                        if (pics.getJSONObject(i).getString("media_type").equals("1")) // photo
                        {
                            isVideoArr[i] = false;

                        } else {
                            isVideoArr[i] = true;
                            jsonA = pics.getJSONObject(i).getJSONArray("video_versions");
                            videoURLs[i] = jsonA.getJSONObject(0).getString("url");

                        }
                    }
                }


                for (int i = 0; i < totalMultiToDownload; i++) {


                    TextSliderView sliderView = new TextSliderView(_this);


                    if (isScreen) {

                        sliderView.image(picURLs[i]);

                    }


                    String fname = "";

                    if (isVideoArr[i]) {
                        if (isScreen)
                            sliderView.description("Video #" + (i + 1));

                        fname = author + "-" + currTime + i + ".mp4";

                    } else {
                        if (isScreen)
                            sliderView.description("Photo #" + (i + 1));

                        fname = folder + File.separator + author + "-" + currTime + i + ".jpg";

                    }
                    Log.d("app5", " fname  = " + fname);
                    if (isScreen) {
                        sliderView.bundle(new Bundle());
                        sliderView.getBundle()
                                .putString("url", picURLs[i]);
                        sliderView.getBundle()
                                .putString("fname", fname);

                        sliderView.getBundle()
                                .putString("is_video", isVideoArr[i] ? "true" : "false");


                        sliderView.setScaleType(BaseSliderView.ScaleType.CenterInside);

                        runOnUiThread(new Runnable() {
                            public void run() {
                                mDemoSlider.addSlider(sliderView);
                            }
                        });
                    }


                }
            } catch (Exception e) {
                int i9 = 43;
            }


            boolean downloadingStarted = false;

            // final int index = i;
            //  final File tmpFile = new File(fname);
            try {


                String fname;

                readyToHideSpinner = false;

                String caption = Util.prepareCaption(title, author, _this.getApplication().getApplicationContext(), caption_suffix, false);


                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Post caption", caption);

                Objects.requireNonNull(clipboard).setPrimaryClip(clip);

                for (int i = totalMultiToDownload - 1; i >= 0; i--) {


                    Log.d("app5", "in loop " + i);


                    if (isVideoArr[i]) {

                        fname = author + "-" + currTime + i + ".mp4";

                    } else {

                        fname = author + "-" + currTime + i + ".jpg";

                    }
                    Log.d("app5", "Multi filename " + fname);


                    Log.d("app5", " fnames " + i + "   " + fname + "   " + currTime);


                    if (!isVideoArr[i]) {


                        downloadImage(picURLs[i], fname);
                    } else {

                        downloadingStarted = true;

                        LoadMultiVideo2(videoURLs[(i)], fname);


                    }


                }
            } catch (Exception e) {
                Log.d("app5", "exception e " + e.getMessage());
            }

            if (readyToHideSpinner || !downloadingStarted) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        Log.d("app5", "remove spinner 3032");
                        //            spinner.setVisibility(View.GONE);
                        //          showBottomButtons();

                    }
                });


            }


            readyToHideSpinner = true;

            sendEvent("sc_multiphoto");


            postExecute("multi");
            if (spinner != null)
                spinner.setVisibility(View.GONE);
            showBottomButtons();
        } catch (Exception e) {
            //  processPotentialPrivate();
            sendEvent("error_#5a");
            showErrorToast("#5a - " + e.getMessage(), getString(R.string.problemfindingvideo) + "  " + e.getMessage(), true);


            //  showErrorToast("#5a - " + e.getMessage(), "#5a - " + e.getMessage(), true);


        }
    }


    private boolean alreadyStartedErrorDialog = false;

    private void processJSON(String jsonRes) {

        //  jsonRes = "{    \"instaloader\": {        \"node_type\": \"Post\",        \"version\": \"4.10.1\"    },    \"node\": {        \"__typename\": \"GraphVideo\",        \"caption_is_edited\": false,        \"commenting_disabled_for_viewer\": false,        \"comments_disabled\": false,        \"dash_info\": {            \"is_dash_eligible\": false,            \"number_of_qualities\": 0,            \"video_dash_manifest\": null        },        \"dimensions\": {            \"height\": 1333,            \"width\": 750        },        \"display_resources\": [            {                \"config_height\": 1137,                \"config_width\": 640,                \"src\": \"https://scontent-mia3-1.cdninstagram.com/v/t51.2885-15/412576894_389958627010969_1800120334536040115_n.jpg?stp=dst-jpg_e15_p640x640&_nc_ht=scontent-mia3-1.cdninstagram.com&_nc_cat=1&_nc_ohc=FlfUlWwAFqAAX_tBHYL&edm=AP_V10EBAAAA&ccb=7-5&oh=00_AfBZ8gUiOeox0ODB0Gfp48QfpYp9hjYgABs0nLbDcEEYJw&oe=65983F93&_nc_sid=2999b8\"            },            {                \"config_height\": 1333,                \"config_width\": 750,                \"src\": \"https://scontent-mia3-1.cdninstagram.com/v/t51.2885-15/412576894_389958627010969_1800120334536040115_n.jpg?stp=dst-jpg_e15&_nc_ht=scontent-mia3-1.cdninstagram.com&_nc_cat=1&_nc_ohc=FlfUlWwAFqAAX_tBHYL&edm=AP_V10EBAAAA&ccb=7-5&oh=00_AfBecoQbZYYbmw_ZDCCx5QzCjA_EbtbHwi9DTeN8ATXNxQ&oe=65983F93&_nc_sid=2999b8\"            },            {                \"config_height\": 1920,                \"config_width\": 1080,                \"src\": \"https://scontent-mia3-1.cdninstagram.com/v/t51.2885-15/412576894_389958627010969_1800120334536040115_n.jpg?stp=dst-jpg_e15&_nc_ht=scontent-mia3-1.cdninstagram.com&_nc_cat=1&_nc_ohc=FlfUlWwAFqAAX_tBHYL&edm=AP_V10EBAAAA&ccb=7-5&oh=00_AfBecoQbZYYbmw_ZDCCx5QzCjA_EbtbHwi9DTeN8ATXNxQ&oe=65983F93&_nc_sid=2999b8\"            }        ],        \"display_url\": \"https://scontent-mia3-1.cdninstagram.com/v/t51.2885-15/412576894_389958627010969_1800120334536040115_n.jpg?stp=dst-jpg_e15&_nc_ht=scontent-mia3-1.cdninstagram.com&_nc_cat=1&_nc_ohc=FlfUlWwAFqAAX_tBHYL&edm=AP_V10EBAAAA&ccb=7-5&oh=00_AfBecoQbZYYbmw_ZDCCx5QzCjA_EbtbHwi9DTeN8ATXNxQ&oe=65983F93&_nc_sid=2999b8\",        \"edge_media_preview_like\": {            \"count\": 1649242,            \"edges\": []        },        \"edge_media_to_caption\": {            \"edges\": [                {                    \"node\": {                        \"text\": \"Indian navy training \\ud83d\\ude32\\u2764\\ufe0f\\n.\\n.\\n.\\n.\\n.\\n#viral #reels #navy #navylover #navy traning #indian navy\"                    }                }            ]        },        \"edge_media_to_comment\": {            \"count\": 2821,            \"edges\": [],            \"page_info\": {                \"end_cursor\": \"\",                \"has_next_page\": true            }        },        \"edge_media_to_sponsor_user\": {            \"edges\": []        },        \"edge_media_to_tagged_user\": {            \"edges\": []        },        \"edge_web_media_to_related_media\": {            \"edges\": []        },        \"encoding_status\": null,        \"fact_check_information\": null,        \"fact_check_overall_rating\": null,        \"gating_info\": null,        \"has_ranked_comments\": false,        \"id\": \"3264055344560953195\",        \"is_ad\": false,        \"is_published\": true,        \"is_video\": true,        \"location\": null,        \"media_preview\": \"ABgqpwxOxyilvT0/Or5t3IyzBM9s/wBeapRl/uBiqk5POPxqz5YC5PT1/wD19veup3OTTTe5XWHZIcY/Dp9RRVhCGyc8rjj6gH8etFJbBLczyW5x17f1q0o3RhfoD+fNRAbTnrir8YR+BgN6Hqf8+1XoQ210KNuoWZh2HAJ759aKuSWxXLDqcH8uKKSQOVyGWyEY/dks47dT+Pp+NRROHODgMBgA8c/h1/nW1ENsIxxlcnHc461zspO/Pf8A+vWZojVWd4xl/mGP88/40UwjkH1zn34opXK5Uf/Z\",        \"owner\": {            \"blocked_by_viewer\": false,            \"followed_by_viewer\": false,            \"full_name\": \"Indian navy \\ud83d\\udc9e Merchant Navy\",            \"has_blocked_viewer\": false,            \"id\": \"48298436318\",            \"is_private\": false,            \"is_unpublished\": false,            \"is_verified\": false,            \"profile_pic_url\": \"https://scontent-mia3-1.cdninstagram.com/v/t51.2885-19/384793336_341786778267788_8681494039250300202_n.jpg?stp=dst-jpg_s150x150&_nc_ht=scontent-mia3-1.cdninstagram.com&_nc_cat=1&_nc_ohc=VltonyPKA54AX_QpemR&edm=AP_V10EBAAAA&ccb=7-5&oh=00_AfBCulRbY7-p5w_GRLH0tTGGjBRUU-Xl73UIAUCCLdYl2A&oe=659C028F&_nc_sid=2999b8\",            \"requested_by_viewer\": false,            \"username\": \"navylovers._official\"        },        \"product_type\": \"clips\",        \"shortcode\": \"C1MQQrLxVdr\",        \"taken_at_timestamp\": 1703325786,        \"thumbnail_src\": \"https://scontent-mia3-1.cdninstagram.com/v/t51.2885-15/412576894_389958627010969_1800120334536040115_n.jpg?stp=c0.280.720.720a_dst-jpg_e15_s640x640&_nc_ht=scontent-mia3-1.cdninstagram.com&_nc_cat=1&_nc_ohc=FlfUlWwAFqAAX_tBHYL&edm=AP_V10EBAAAA&ccb=7-5&oh=00_AfAJ-_ARv4GjxQbl6VzDTsPmXP-d63jJh51HddjGUiygRQ&oe=65983F93&_nc_sid=2999b8\",        \"title\": \"\",        \"tracking_token\": \"eyJ2ZXJzaW9uIjo1LCJwYXlsb2FkIjp7ImlzX2FuYWx5dGljc190cmFja2VkIjp0cnVlLCJ1dWlkIjoiZGJhYzBkYjE1NjgxNDFjNzk4MjNhOWYyZTRkMTJlMDgzMjY0MDU1MzQ0NTYwOTUzMTk1In0sInNpZ25hdHVyZSI6IiJ9\",        \"video_duration\": 14.652,        \"video_url\": \"https://scontent-mia3-1.cdninstagram.com/v/t66.30100-16/10000000_1845722152558571_6886254468376108415_n.mp4?efg=e30&_nc_ht=scontent-mia3-1.cdninstagram.com&_nc_cat=111&_nc_ohc=G8Y-r3LrrUEAX9HoNF9&edm=AP_V10EBAAAA&ccb=7-5&oh=00_AfCvuIE3uJ3tOCjdS8n9eo41onK6EL5yR48QGC-m6ckUhQ&oe=659B5C3E&_nc_sid=2999b8\",        \"video_view_count\": 19575786,        \"viewer_can_reshare\": true,        \"viewer_has_liked\": false,        \"viewer_has_saved\": false,        \"viewer_has_saved_to_collection\": false,        \"viewer_in_photo_of_you\": false    }}";

        JSONObject json = null;
        Log.d("app5", jsonRes);

        try {


            json = new JSONObject(jsonRes);

            //  json = json.getJSONObject("node");
            try {
                author = json.getJSONObject("owner").getString("username");


                profile_pic_url = json.getJSONObject("owner").getString("profile_pic_url");
            } catch (Exception e) {
                Log.d("app5", "Exception  3197 : " + e.getMessage());
            }

            try {
                author = json.getJSONObject("owner").getString("username");


                profile_pic_url = json.getJSONObject("owner").getString("profile_pic_url");
            } catch (Exception e) {
                Log.d("app5", "Exception  3206 : " + e.getMessage());
            }


            title = "";
            try {
                JSONObject json6 = json.getJSONObject("edge_media_to_caption");
                JSONArray json7 = json6.getJSONArray("edges");
                JSONObject json8 = json7.getJSONObject(0);
                JSONObject json9 = json8.getJSONObject("node");

                title = json9.getString("text");
            } catch (Exception e) {
            }


            try {
                JSONObject json6 = json.getJSONObject("caption");
                title = json6.getString("text");
            } catch (Exception e) {
            }

            try {
                url = json.getString("display_url");

                isVideo = json.getBoolean("is_video");


                if (isVideo) {


                    videoURL = json.getString("video_url");

                }
            } catch (Exception e) {
            }


            try {

                JSONArray jsonA = json.getJSONObject("image_versions2").getJSONArray("candidates");
                url = jsonA.getJSONObject(0).getString("url");

                String media_type = json.getString("media_type");

                isVideo = false;
                if (media_type.equals("2")) {
                    isVideo = true;
                    jsonA = json.getJSONArray("video_versions");
                    videoURL = jsonA.getJSONObject(0).getString("url");
                }

                if (media_type.equals("8")) {   // Multi
                    processMultiPhotoJSON(json, 2);

                }


            } catch (Exception e) {
            }


        } catch (Exception e3) {

            processPotentialPrivate();
            // showErrorToast("Problem", "There seems to be a problem.  Please try again later.", true);
            return;

        }


        if (json != null)

            // Multi-Photo post
            if (!json.isNull("edge_sidecar_to_children")) {
                processMultiPhotoJSON(json, 1);
                return;
            }

        if (!json.isNull("carousel_media")) {
            processMultiPhotoJSON(json, 2);
            return;
        }


        downloadSinglePhotoFromURL(url);

    }


    private void copyCaptionToClipboard() {
        String caption = Util.prepareCaption(title, author, _this.getApplication().getApplicationContext(), caption_suffix, false);


        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Post caption", caption);

        Objects.requireNonNull(clipboard).setPrimaryClip(clip);

        Toast.makeText(_this, "Caption copied to clipboard. Paste where needed", Toast.LENGTH_SHORT).show();
    }

    Bitmap origBitmap;

    private void changeWatermarkPosition(int position) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here

                // previewImage.setImageBitmap(Util.decodeFile(new File(path)));

                try {
                    Bitmap bitmap = origBitmap;


                    if (position != 0) {

                        int textSize = 20;
                        if (bitmap.getHeight() > 640)
                            textSize = 50;
                        bitmap = mark(bitmap, author, position, Color.YELLOW, 180, textSize, false);
                    }


                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 99, bytes);

                    lastDownloadedFile = tempFile;

                    FileUtils.writeByteArrayToFile(tempFile, bytes.toByteArray());


                    Log.d("app5", "after compress");
                    sendEvent("sc_photo");

                    previewImage.setImageBitmap(Util.decodeFile(tempFile));

                } catch (Exception e) {

                }


            }
        });


    }

    private void downloadSinglePhotoFromURL(String url) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {

                //Background work here


                if (!isVideo) {
                    try {
                        Bitmap bitmap = null;
                        try {
                            URL imageurl = new URL(url);
                            originalBitmapBeforeNoCrop = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());
                            bitmap = originalBitmapBeforeNoCrop;
                            origBitmap = bitmap;
                        } catch (Exception e) {

                            showErrorToast("Out of memory", "Sorry not enough memory to continue", true);

                        }


                        try {
                            if (preferences.getBoolean("watermark_checkbox", false) ||
                                    preferences.getBoolean("custom_watermark", false)) {

                                int textSize = 20;
                                if (bitmap.getHeight() > 640)
                                    textSize = 50;
                                bitmap = mark(bitmap, author, 1, Color.YELLOW, 180, textSize, false);
                            }
                        } catch (Exception e99) {

                        }


                        Log.d("app5", "before compress");
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 99, bytes);

                        lastDownloadedFile = tempFile;
                        FileUtils.writeByteArrayToFile(tempFile, bytes.toByteArray());

                        Log.d("app5", "after compress");
                        sendEvent("sc_photo");


                    } catch (Exception e) {
                        sendEvent("error_#5b");
                        showErrorToast("#5b - " + e.getMessage(), getString(R.string.problemfindingvideo) + " " + e.getMessage(), true);
                        //    showErrorToast("#5b - " + e.getMessage(), "#5b - " + e.getMessage(), true);

                        return;
                    }
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        //UI Thread work here
                        postExecute("");
                    }
                });
            }
        });


    }


    private void setBottomButtonsVisible() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (vButtons != null)
                        vButtons.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                }

            }
        });
    }

    private void showBottomButtons() {

        if (!showInterstitial) {
            setBottomButtonsVisible();
        }


    }


    private class AsyncTaskDownloadMedia extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String b) {
            super.onPostExecute(b);

        }
    }


    int countImages = 0;

    private void downloadImage(String url, final String fname) {

        try {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("app5", "STARTING DOWNLOAD MULTI");
                    Util.startDownloadMulti(url, "", _this, fname, isAutoSave);
                }
            }, countImages * 200L);
            countImages++;

            //  Thread.sleep(2000) ;


        } catch (Exception e) {
            int y = 1;

        }


    }

    private void processNewInstagramURL(String html) {



        if (html.indexOf(">Log in<") > 0) {
            processPotentialPrivate();
            return;
        }

        boolean foundPhotoURL = false;
        Document doc = Jsoup.parse(html);
        try {
            Elements metaTags = doc.getElementsByTag("meta");

            try {

                for (Element metaTag : metaTags) {
                    if (metaTag.attr("property").equals("og:title") || metaTag.attr("name").equals("og:title")) {
                        title = metaTag.attr("content");

                        int s = title.indexOf('"');
                        int e = title.indexOf('"', s + 2);


                        title = title.substring(s + 1, e);
                        Log.d("app5", "Found title : " + title);


                    }

                    if (metaTag.attr("property").equals("og:image")) {
                        url = metaTag.attr("content");
                        foundPhotoURL = true;
                        Log.d("app5", "Found url : " + url);


                    }




                }
            } catch (Exception e) {
            }


            profile_pic_url = null;

            try {
                int s = html.indexOf("\"user\":{\"pk\"");
                if (s > 0) {
                    s = html.indexOf("https", s);
                    int e = html.indexOf("\"", s);

                    profile_pic_url = html.substring(s, e);
                    profile_pic_url = profile_pic_url.replace("\\/", "\\");
                    Log.d("app5", "profile pic url " + profile_pic_url);


                }


            } catch (Exception e) {
            }


            String type = null;
            String photoURL = null;

            Elements e2 = doc.getElementsByClass("_aaqy");
            if (e2.size() > 0) {

                Document doc2 = Jsoup.parse(String.valueOf(e2.get(0)));
                Elements aTag = doc2.getElementsByTag("a");
                author = aTag.attr("href");
                author = author.substring(1, author.length() - 1);
                Log.d("app5", author + "  " + e2.get(0));


            }

            Elements e = doc.getElementsByClass("_ab1d");
            if (e.size() > 0) {


                videoURL = e.attr("src");
                isVideo = true;

                Elements e3 = doc.getElementsByClass("_ab1e");
                if (e3.size() > 0) {
                    Log.d("app5", String.valueOf(e3.get(0)));

                    url = e3.attr("src");

                    downloadSinglePhotoFromURL(url);
                    return;
                }

            }


            isVideo = false;

            // Is this the not logged in style page
            // if so trim just the main section at _aa6e
            if (doc.getElementsByClass("_aa6e").size() > 0) {
                doc = Jsoup.parse(doc.getElementsByClass("_aa6e").toString());

                doc = Jsoup.parse(doc.getElementsByClass("_aatk").toString());

                //   Log.d ("app5", "new doc :  " + doc);

            }

            // is this really a video?
            if (doc.toString().indexOf("<video") > 0 && doc.toString().indexOf("<video") < doc.toString().indexOf(".jpg")) {


                Element videoElement = doc.select("video").first();

                videoURL = videoElement.attr("src");
                if (videoURL.length() > 0) {
                    isVideo = true;

                    if (videoURL.indexOf("blob") != -1) {
                        // we got a blob....no good
                        showErrorToast("Network problem", "There is a problem getting this video, try again later on a stronger Wifi signal", true);

                        return;

                    }

                    Document doc1 = Jsoup.parse(doc.toString().substring(doc.toString().indexOf("<video")));
                    Element imgE = doc1.select("img").first();

                    if (imgE != null) {


                        url = imgE.attr("src");

                        downloadSinglePhotoFromURL(url);
                        Log.d("app5", "3976");
                        RegrannApp.sendEvent("sc_video_element_found");
                        return;
                    }


                }


            }


            isVideo = false;
            Elements e4 = doc.getElementsByClass("_aagt");
            if (e4.size() > 0) {
                Log.d("app5", String.valueOf(e4.get(0)));

                url = e4.attr("src");
                prepareForSinglePhoto(url);


                return;

            } else {

                Elements e31 = doc.getElementsByClass("_aagv");
                if (e31.size() > 0) {
                    // Log the first matching element
                    Log.d("app5", String.valueOf(e31.get(0)));

                    // Try to extract the "src" attribute directly from the <img> tag
                    Element imgElement = e31.get(0).selectFirst("img");
                    if (imgElement != null) {
                        url = imgElement.attr("src");
                        Log.d("app5", "URL1 : " + url);
                    }

                    // Fallback to parsing the HTML for "srcset" if "src" is empty
                    if (url == null || url.isEmpty()) {
                        String htmlPhoto = String.valueOf(e31.get(0));
                        int t = htmlPhoto.indexOf("srcset=");
                        int end = htmlPhoto.indexOf("1080w");

                        if (t > 0 && end > t) {
                            // Extract the URL from the srcset attribute
                            url = htmlPhoto.substring(t + 8, end - 1);
                            url = url.replaceAll("&amp;", "&");
                            Log.d("app5", "URL new : " + url);
                        }
                    }

                    // If a valid URL is found, initiate the download
                    if (url != null && !url.isEmpty()) {
                        downloadSinglePhotoFromURL(url);
                        return;
                    } else {
                        Log.e("app5", "Failed to extract image URL.");
                    }
                } else {
                    Log.e("app5", "No elements found with class '_aagv'.");
                }



                isVideo = false;

                if (html.indexOf("<video") > 0) {
                    Element videoElement = doc.select("video").first();

                    videoURL = videoElement.attr("src");
                    if (videoURL.length() > 0) {
                        isVideo = true;

                        if (videoURL.indexOf("blob") != -1) {
                            // we got a blob....no good
                            showErrorToast("Network problem", "There is a problem getting this video, try again later on a stronger Wifi signal", true);

                            return;

                        }

                        Document doc1 = Jsoup.parse(html.substring(html.indexOf("<video")));
                        Element imgE = doc1.select("img").first();

                        if (imgE != null) {


                            url = imgE.attr("src");
                            Log.d("app5", "4057");
                            downloadSinglePhotoFromURL(url);
                            RegrannApp.sendEvent("sc_video_element_found");
                            return;
                        }


                    }
                }

                //   processPotentialPrivate();

                if (!isVideo && foundPhotoURL) {
                    downloadSinglePhotoFromURL(url);
                    return;
                }

                if (author == null) {
                    processPotentialPrivate();
                    return;
                }
                // if there are no JPG then check for login

                if (html.indexOf("jpg") == -1) {
                    processPotentialPrivate();
                    return;
                }


                // check for <video


                if (photoURL != null || videoURL != null)
                    return;


                processPotentialPrivate();


                return;


            }


        } catch (Exception e) {
            processPotentialPrivate();
        }

        processPotentialPrivate();

    }


    boolean loadingFinished = true;
    boolean redirect = false;
    private boolean alreadyFinished;

    WebView webview;


    private void sendDebugInfo(String html) {

        //  showErrorToast("There was a problem ", "Unable to find a video or photo at this link.", true);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShareActivity.this);

        // set dialog message
        alertDialogBuilder.setMessage("There was a problem.  Would you mind emailing the developer with details to help fix it. ")
                .setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        emailSupport(html, true);
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }

    private void emailSupport(String theText, boolean doFinish) {
        String version = "";

        try {
            PackageManager manager = _this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(
                    _this.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
        }


        String details = "APP VERSION: " + version
                + "\nANDROID OS: " + Build.VERSION.RELEASE
                + "\nMANUFACTURER : " + Build.MANUFACTURER
                + "\nMODEL : " + Build.MODEL;


        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@jaredco.com"});
        intent.putExtra(Intent.EXTRA_TEXT, "\n\n\n\n" + details);


        intent.putExtra(Intent.EXTRA_SUBJECT, "Reporting issue with Repost Pro");


        intent.setType("message/rfc822");

        Toast.makeText(_this, "Preparing email", Toast.LENGTH_LONG).show();
        startActivity(Intent.createChooser(intent, "Select Email Sending App :"));


        try {

            final Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShareActivity.this);

                    // set dialog message
                    alertDialogBuilder.setTitle("Thanks for sending support email!");
                    alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

                    alertDialogBuilder.setMessage("I appreciate you taking the time to send a support email.  I will respond ASAP.")
                            .setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    if (doFinish)
                                        finish();
                                }

                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                }
            }, 3000);


        } catch (Exception e) {
        }


        sendEvent("main_email_support");


    }


    private void prepareForSinglePhoto(String photoURL) {

        downloadSinglePhotoFromURL(photoURL);


    }


    private void prepareForSingleVideo(String tempVideoURL) {
        isVideo = true;
        videoURL = tempVideoURL;


        postExecute("");


    }


    private void processNeedToLogin() {
        try {

            if (alreadyStartedErrorDialog)
                return;

            alreadyStartedErrorDialog = true;

            if (spinner != null) {

                runOnUiThread(new Runnable() {
                    public void run() {

                        spinner.setVisibility(View.GONE);

                    }
                });
            }
            String displayMsg;

            AlertDialog.Builder builder = new AlertDialog.Builder(ShareActivity.this);

            builder.setTitle("Login Required");
            displayMsg = "Looks like you haven't completed the Instagram login within this app yet?\nIt is needed for Stories / Private / Sensitive content.\n\nOn the next screen you will be able to use the official Instagram login page.";


            builder.setCancelable(false);
            builder.setMessage(displayMsg);
            builder.setIcon(R.mipmap.ic_launcher);

            builder.setPositiveButton("Go To Login", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing but close the dialog

                    dialog.dismiss();
                    Intent myIntent = new Intent(ShareActivity.this, InstagramLogin.class);
                    myIntent.putExtra("clear_data", true);
                    _this.startActivity(myIntent);

                }
            });

            builder.setNeutralButton("Email Support",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            emailSupport("Problem: can't retrieve post.", true);
                            dialog.dismiss();
                            // finish();
                        }
                    });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    launchedLogin = false;
                    // Do nothing
                    dialog.dismiss();
                    finish();
                }
            });

            AlertDialog alert = builder.create();
            if (!_this.isFinishing())
                alert.show();

        } catch (Exception e) {
            int i = 1;
        }
    }


    private final String urlFinished = " ";

    private void processStoriesFromIGRAM(String res, String initialURL) {
        //Log.d("app5", "in processStoriesFromIGRAM for Stories ");
        isStoryURL = true;
        url = "";
        videoURL = "";
        int startPos = initialURL.indexOf("stories");
        int endPos;

        try {

            if (startPos > 1) {
                endPos = initialURL.indexOf("/", startPos + 9);
                author = initialURL.substring(startPos + 8, endPos);
            } else {
                startPos = initialURL.indexOf("/s/");
                endPos = initialURL.indexOf("/", startPos + 4);
                author = initialURL.substring(startPos + 5, endPos);
            }
        } catch (Exception e) {
        }


        try {


            JSONObject json = new JSONObject(res);
            JSONObject dataObject = json.getJSONObject("data");

            // Access items array
            JSONArray itemsArray = dataObject.getJSONArray("items");

            JSONObject jsonA = itemsArray.getJSONObject(0);

            url = jsonA.getJSONObject("image_versions2").getJSONArray("candidates").getJSONObject(0).getString("url");


            isVideo = false;

            if (jsonA.has("video_versions")) {
                JSONArray jsonB = jsonA.getJSONArray("video_versions");
                if (jsonB != null) {
                    isVideo = true;

                    videoURL = jsonB.getJSONObject(0).getString("url");
                }
            }

            if (url != "")
                downloadSinglePhotoFromURL(url);


        } catch (Exception e) {
            shouldRetryVolley();
        }

    }

    private void processHTMLforStories(String html) {
        Log.d("app5", "in processHTML for Stories ");
        isStoryURL = true;
        url = "";
        videoURL = "";

        //   if (1 == 1) {
        //      showErrorToast("#4380", "Instagram made changes.  Story reposting not available now. Still working on it.", true);
        //     return;
        // }



        if (html.indexOf("_9zm4") > 0) {
            processNeedToLogin();
            return;
        }


        try {

            sendEvent("story_attempted", "", "");
            runOnUiThread(new Runnable() {
                public void run() {
                    if (isAutoSave | isQuickKeep | isQuickPost)
                        findViewById(R.id.browser2).setVisibility(View.GONE);

                    else
                        findViewById(R.id.browser).setVisibility(View.GONE);


                }
            });


            int endPos;
            int startPos;

            startPos = trackURL.indexOf("stories");
            endPos = trackURL.indexOf("/", startPos + 9);

            author = trackURL.substring(startPos + 8, endPos);
            Log.d("app5", "author = " + author);

            isVideo = false;
            // check for video
            startPos = html.indexOf("<video");

            if (startPos > -1) {
                isVideo = true;
                startPos = html.indexOf("src=", startPos);
                endPos = html.indexOf(">", startPos);
                videoURL = html.substring((startPos + 5), endPos - 1);

                videoURL = videoURL.replaceAll("&amp;", "&");
                Log.d("app5", "VideoURL : " + videoURL);
                RegrannApp.sendEvent("sc_story_video_found");
                downloadSinglePhotoFromURL("");
                return;
            }


            Document doc1;

            if (isVideo)
                doc1 = Jsoup.parse(html.substring(html.indexOf("<video")));
            else {
                doc1 = Jsoup.parse(html);
                RegrannApp.sendEvent("sc_story_photo_found");
            }


            if (!isVideo) {
                startPos = html.indexOf("alt=\"Photo by");

                startPos = html.indexOf("src", startPos + 1);

                if (startPos > -1) {
                    endPos = html.indexOf(" ", startPos);
                    url = html.substring((startPos + 5), endPos);


                    Log.d("app5", "MediaURL : " + url);

                }
            }

            if (url.isEmpty()) {

                Element imgE = doc1.select("img").first();

                if (imgE != null) {


                    url = imgE.attr("src");
                }
            }




            if (!url.isEmpty()) {
                sendEvent("story_found", "", "");
                url = url.replaceAll("&amp;", "&");
                downloadSinglePhotoFromURL(url);
            } else {
                // showErrorToast("#3528", getString(R.string.porblemfindingphoto), true);
                processPotentialPrivate();

            }
        } catch (Exception e) {
            processPotentialPrivate();
        }

    }

    private void processHTML(String html) {

/**
 if (html.indexOf("shortcode_media") > 0 && getJSONfromBrowser) {
 onDataLoaded(html, url);
 return;
 }
 **/
        processNewInstagramURL(html);

    }


    String trackURL;
    boolean toLogin = false;

    String urlIn;

    public void GET(final String urlOrig) {
        spinner.setVisibility(View.VISIBLE);
        urlIn = urlOrig;


        alreadyTriedGET = true;
        alreadyStartedErrorDialog = false;

        class MyJavaScriptInterface {

            MyJavaScriptInterface(Context ctx) {
            }

            @JavascriptInterface
            public void showHTML(String html) {
                alreadyFinished = true;

                //  Log.d("app5", html);

                if (toLogin)
                    return;

                //   if (html.indexOf("items")> 0)
                //  {
                //      Log.d("app5", ""+html.indexOf("{"));
//                    html = html.substring(html.indexOf("{"), html.indexOf("</pre")-1);
                //              }


                if (trackURL.contains("stories")) {
                    processHTMLforStories(html);
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (isAutoSave | isQuickKeep | isQuickPost)
                                findViewById(R.id.browser2).setVisibility(View.GONE);

                            else
                                findViewById(R.id.browser).setVisibility(View.GONE);
                        }
                    });

                    processHTML(html);


                }


            }


        }


        try {

            runOnUiThread(new Runnable() {
                public void run() {


                    if (urlIn.contains("/s/") || urlIn.contains("stories")) {
                        if (isAutoSave | isQuickKeep | isQuickPost)
                            findViewById(R.id.browser2).setVisibility(View.VISIBLE);

                        else
                            findViewById(R.id.browser).setVisibility(View.VISIBLE);
                    }

                    Log.d("app5", "I am the UI thread ");


                    if (isAutoSave | isQuickKeep | isQuickPost)
                        webview = findViewById(R.id.browser2);
                    else {
                        webview = findViewById(R.id.browser);

                    }


                    webview.getSettings().setLoadWithOverviewMode(true);
                    webview.getSettings().setUseWideViewPort(true);
                    webview.getSettings().setJavaScriptEnabled(true);
                    webview.addJavascriptInterface(new MyJavaScriptInterface(ShareActivity.this), "HtmlViewer");
                    webview.getSettings().setLoadWithOverviewMode(true);


                    //   webview.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 16_0_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.6 Mobile/15E148 Safari/604.1");
                    webview.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 16_0_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/15.6 Mobile/15E148 Safari/604.1");


                    //  webview.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 18_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.1 Mobile/15E148 Safari/604.1");

                    webview.setWebViewClient(new WebViewClient() {

                        @SuppressWarnings("deprecation")
                        @Override
                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                            Log.e(TAG, errorCode + " : " + description + " at " + failingUrl);
                        }

                        @Override
                        public void onPageStarted(WebView view, String url,
                                                  android.graphics.Bitmap favicon) {
                            Log.d("app5", "in page started " + url);

                        }

                        @Override
                        public boolean shouldOverrideUrlLoading(WebView view, String url) {
                            Log.d("app5", "in page should override " + url);

                            if (url.contains("https://itunes.apple.com/app/instagram") || url.contains("instagram.com/accounts/login/")) {

                                toLogin = true;

                                Log.d("app5", "Found login page");
                                return false;

                            }

                            if (url.startsWith("https:") && !alreadyFinished) {
                                view.loadUrl(url);
                                trackURL = url;
                            }
                            return true;
                        }


                        /**
                         * Javascript-accessible callback for declaring when a page has loaded.
                         */


                        @Override
                        public void onPageFinished(WebView view, String url) {

                            Log.d("app5", url + "   " + urlFinished);
                            CookieManager.getInstance().flush();
                            Log.d("app5", "Progress : " + webview.getProgress() + " ");


                            if (toLogin) {
                                Log.d("app5", "progress 100 - " + toLogin);

                                alreadyTriedGET = true;
                                processNeedToLogin();
                                Log.d("app5", "Found login page 2");
                                return;
                            }


                            if (webview.getProgress() == 100 && !alreadyFinished) {

                                if (url.contains("instagram.com/accounts/login/")) {
                                    alreadyTriedGET = true;
                                    processPotentialPrivate();
                                    Log.d("app5", "Found login page");
                                    return;
                                }

                                // remove https://www
                                String original = urlIn;

                                if (original.contains("www."))
                                    original = original.replace("www.", "");

                                if (url.contains("www."))
                                    url = url.replace("www.", "");

                                if (original.contains("/?"))
                                    original = original.replace("/?", "?");

                                if (url.contains("/?"))
                                    url = url.replace("/?", "?");


                                Log.d("app5", "Orig - " + original);
                                Log.d("app5", "last - " + urlIn);

                                int delay = 4000;

                                final String url5 = url;


                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("app5", "Press tap to play");
                                        webview.loadUrl("javascript:let buttons = document.querySelectorAll('div[role=\"button\"]'); targetButton = Array.from(buttons).find(button => button.textContent.trim() === \"View story\");  if (targetButton) { targetButton.click(); console.log('Button clicked!'); } else { console.error('Button not found!'); }");

                                        final Handler handler1 = new Handler();
                                        handler1.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d("app5", "grab HTML  - " + url5);
                                                webview.loadUrl("javascript:window.HtmlViewer.showHTML" +
                                                        "('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");

                                            }
                                        }, 1000);
                                    }
                                }, delay);

                                alreadyFinished = true;
                                Log.d("app5", "in page finisihed " + url);


                            }


                        }
                    });


                    webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                    alreadyFinished = false;

                    webview.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Log.d("app5", "Clear cache and load url : " + urlIn);
                            webview.clearCache(true);
                            webview.loadUrl(urlIn);
                            trackURL = urlIn;
                            toLogin = false;
                            currentURL = urlIn;
                        }
                    }, 500);


                }
            });


        } catch (Exception e) {
            Log.d("app5", e.getMessage());
            showErrorToast("error 4112", "There was a problem. Please try again. 4112 - " + e.getMessage());


        }

    }


    private void clearClipboard() {
        try {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", "");
            Objects.requireNonNull(clipboard).setPrimaryClip(clip);
        } catch (Exception e) {
        }
    }

    /* 3) Handle the results */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == VESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult res = new EditorSDKResult(data);

            // This adds the result and source image to Android's gallery
            //  data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("PESDK", "Source image is located here " + res.getSourceUri());
            Log.i("PESDK", "Result image is located here " + res.getResultUri());

            // TODO: Do something with the result image

            try {

                sendEvent("sc_edit_video_complete", "", "");
                //   Uri mImageUri = res.getResultUri();


                //    previewImage.setImageBitmap(Util.decodeFile(new File(mImageUri != null ? mImageUri.getPath() : null)));


            } catch (Exception e) {
            }
        }


        if (resultCode == RESULT_OK && requestCode == PESDK_RESULT) {
            // Editor has saved an Image.
            EditorSDKResult res = new EditorSDKResult(data);

            // This adds the result and source image to Android's gallery
            //  data.notifyGallery(EditorSDKResult.UPDATE_RESULT & EditorSDKResult.UPDATE_SOURCE);

            Log.i("PESDK", "Source image is located here " + res.getSourceUri());
            Log.i("PESDK", "Result image is located here " + res.getResultUri());

            // TODO: Do something with the result image

            try {

                sendEvent("sc_edit_photo_complete", "", "");
                Uri mImageUri = res.getResultUri();


                previewImage.setImageBitmap(Util.decodeFile(new File(mImageUri != null ? mImageUri.getPath() : null)));


            } catch (Exception e) {
            }
        }


        if (resultCode == RESULT_OK) {
            /* 4) Make a case for the request code we passed to startActivityForResult() */
            if (requestCode == 1) {
                try {

                    Uri mImageUri = data.getData();


                    previewImage.setImageBitmap(Util.decodeFile(new File(mImageUri != null ? mImageUri.getPath() : null)));

                    //  final Drawable drawable = Drawable.createFromPath(mImageUri.getPath());
                    //    previewImage.setImageDrawable(drawable);


                } catch (Exception e) {
                }
            }
        }
    }


    public void onClickUpgradeButton(View v) {


        sendEvent("qs_upgrade_btn_clk", "", "");


        Intent i = new Intent(_this, UpgradeActivity.class);
        i.putExtra("from_qs_screen", true);
        startActivity(i);
        finish();


    }


    public void OnClickSaveNextTime(View v) {
        sendEvent("prefix_remember");
        SharedPreferences.Editor editor = preferences.edit();
        TextInputEditText captionEditText = findViewById(R.id.captionPrefix);
        String caption_prefix = captionEditText.getText().toString();
        editor.putString("caption_prefix", caption_prefix);
        editor.commit();
        Toast t = Toast.makeText(_this, "Caption Prefix Saved for Next Time", Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        t.show();


    }

    public void OnClickEditPhoto(View v) {
        sendEvent("sc_edit_photo", "", "");
        openEditor(Uri.fromFile(tempFile));


    }

    public void OnClickEditVideo(View v) {
        sendEvent("sc_edit_video", "", "");
        openVideoEditor(Uri.fromFile(new File(Util.getTempVideoFilePath())));


    }

    public void onClickWmarkPosition(View v) {

        RegrannApp.sendEvent("onclick_watermark");
        if (v.getId() == R.id.b_right) {

            changeWatermarkPosition(2);
        }

        if (v.getId() == R.id.b_right) {

            changeWatermarkPosition(2);
        }

        if (v.getId() == R.id.b_left) {

            changeWatermarkPosition(1);
        }

        if (v.getId() == R.id.t_left) {

            changeWatermarkPosition(3);
        }

        if (v.getId() == R.id.t_right) {

            changeWatermarkPosition(4);
        }

        if (v.getId() == R.id.nowatermark) {

            changeWatermarkPosition(0);
        }

    }

    public void onClickBackToInstagram(View v) {
        sendEvent("qs_back_to_instagram", "", "");
        finish();
    }


    private void cleanUp() {


        try {
            if (originalBitmapBeforeNoCrop != null)
                originalBitmapBeforeNoCrop.recycle();

            if (pd != null)
                pd.cancel();
        } catch (Exception e) {
        }

        try {

            removeProgressDialog();


            if (isMulti)
                scanMultiPostFolder();


            scanRegrannFolder();


            if (webViewInsta != null)
                webViewInsta.destroy();
        } catch (Exception e) {
        }
    }

    @Override
    public void onPause() {

        super.onPause();


        //    LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
        //  localBroadcastManager.unregisterReceiver(myDownloadLinkReceiver);
        unregisterReceiver(downloadCompleteReceiver);


    }


    private void processPhotoImage() {


        try {


            if (isAutoSave) {
                new CopyFileTask(_this).execute();
                //   finish();

            } else if (isQuickPost) {


                if (inputMediaType == MEDIA_IMAGE)
                    quickPostSendToInstagram();
                else {
                    isVideo = true;

                    String uriStr = tempFile.getAbsolutePath();
                    final Uri uri = Uri.parse(uriStr);
                    final String path = uri.getPath();
                    tempVideoFile = new File(path);

                    quickPostSendToInstagram();
                }


            } else {
                //  btndownloadphoto.setVisibility(View.GONE);


                String uriStr = tempFile.getAbsolutePath();
                final Uri uri = Uri.parse(uriStr);
                final String path = uri.getPath();
                if (inputMediaType == MEDIA_IMAGE) {

                    previewImage.setImageBitmap(Util.decodeFile(new File(path)));


                    //   final Drawable drawable = Drawable.createFromPath(path);
                    // previewImage.setImageDrawable(drawable);
                } else {
                    isVideo = true;

                    tempVideoFile = new File(path);

                    //    videoPlayer = (VideoView) findViewById(R.id.videoPlayer);
                    //  videoPlayer.setOnPreparedListener(_this);
                    // videoPlayer.setOnCompletionListener(_this);
                    //  videoPlayer.setKeepScreenOn(true);

//                    videoPlayer.setVideoPath(path);
                    //                  videoPlayer.setVisibility(View.VISIBLE);
                    //    previewImage.setVisibility(View.GONE);

                }


                if (spinner != null) {
                    Log.d("app5", "remove spinner 3589");

                    spinner.setVisibility(View.GONE);
                }
                photoReady = true;
            }

        } catch (Exception e) {
            showErrorToast("#9 - " + e.getMessage(), "#9 " + getString(R.string.therewasproblem));

        }

    }


    /**
     * This callback will be invoked when the file is ready to play
     */
    @Override
    public void onPrepared(MediaPlayer vp) {

        // Don't start until ready to play.  The arg of seekTo(arg) is the start point in
        // milliseconds from the beginning. Normally we would start at the beginning but,
        // for purposes of illustration, in this example we start playing 1/5 of
        // the way through the video if the player can do forward seeks on the video.

        if (videoPlayer.canSeekForward()) videoPlayer.seekTo(videoPlayer.getDuration() / 5);
        //videoPlayer.start();
        vp.setVolume(0f, 0f);
    }

    /**
     * This callback will be invoked when the file is finished playing
     */
    @Override
    public void onCompletion(MediaPlayer mp) {
        // Statements to be executed when the video finishes.
        this.finish();
    }

    /**
     * Use screen touches to toggle the video between playing and paused.
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }


    public File getVideoStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), albumName);

        if (!file.mkdirs()) {
            Log.e("error", "Directory not created");
        }

        return file;
    }

    public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);

        if (!file.mkdirs()) {
            Log.e("error", "Directory not created");
        }

        return file;
    }

    private void showErrorToast(final String error, final String displayMsg) {
        if (!updateScreenOn)
            showErrorToast(error, displayMsg, false);
    }

    private void showErrorToast(final String error, final String displayMsg,
                                final boolean doFinish) {

        runOnUiThread(new Runnable() {
            public void run() {
                try {

                    alreadyStartedErrorDialog = true;
                    try {
                        if (pd != null) {
                            if (pd.isShowing()) {
                                pd.dismiss();
                                pd = null;

                            }
                        }
                    } catch (Exception e) {
                    }


                    sleep(1000);
                    if (updateScreenOn)
                        return;

                    if (spinner != null) {
                        Log.d("app5", "remove spinner 3687");
                        spinner.setVisibility(View.GONE);
                    }

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ShareActivity.this);

                    // set dialog message
                    alertDialogBuilder.setMessage(displayMsg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (doFinish)
                                finish();
                        }

                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                } catch (Exception e) {
                }
            }
        });

    }


    private void LoadMultiVideo2(final String videoURL, final String fname) {
        String ThisUrl = videoURL;


        loadingMultiVideo = true;

        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (numMultVideos == 0)
                        startProgressDialog();


                    numMultVideos += 1;
                } catch (Exception e4) {
                }
            }
        });


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String str3 = "";
                Log.d("app5", "STARTING DOWNLOAD MULTI Video");
                Util.startDownloadMulti(videoURL, str3, _this, fname, isAutoSave);
            }
        }, countImages * 200L);
        countImages++;


        isVideo = false;
    }

    private void startAutoSaveMultiProgress() {

        if (pd != null)
            pd = ProgressDialog.show(ShareActivity.this, _this.getString(R.string.progress_dialog_msg), _this.getString(R.string.auto_saving_progress), true, true);

    }

    private void startProgressDialog() {


        if (noAds && isAutoSave) {
        } else

            runOnUiThread(new Runnable() {
                public void run() {
                    pd = ProgressDialog.show(ShareActivity.this, _this.getString(R.string.progress_dialog_msg), _this.getString(R.string.auto_saving_progress), true, true);


                }
            });


    }

    private void removeProgressDialog() {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    if (pd != null) {
                        if (pd.isShowing()) {


                            pd.dismiss();
                            pd = null;


                        }
                    }
                } catch (Exception e) {
                }

            }
        });


    }


    private void LoadVideo() {

        startProgressDialog();
        Log.d("app5", this.videoURL);

        /**
         String str3 = "";
         try {
         long DownloadId = Util.startDownload(this.videoURL, str3, _this, tempVideoName);
         } catch (Exception e) {
         Log.d("app5", e.getMessage());
         }
         **/


        FileDownloader.downloadFile(this, this.videoURL, tempVideoName, false);


    }


    private class addWatermarkToFile extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... fnames) {

            String fname = fnames[0];

            if (fname.endsWith("mp4"))
                return "";

            try {
                Bitmap bitmap = null;
                try {

                    bitmap = BitmapFactory.decodeFile(fname);

                } catch (Throwable e) {
                    showErrorToast("Out of memory", "Sorry not enough memory to continue", true);

                }

                try {
                    if (preferences.getBoolean("watermark_checkbox", false) ||
                            preferences.getBoolean("custom_watermark", false)) {

                        int textSize = 20;
                        if (bitmap.getHeight() > 640)
                            textSize = 50;
                        bitmap = mark(bitmap, author, 1, Color.YELLOW, 180, textSize, false);
                    }
                } catch (Exception e99) {

                }

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 99, bytes);


                try {

                    byte[] contents = bytes.toByteArray();
                    Log.d("app5", "in oncomplete photo : " + fname);
                    /**
                     FileOutputStream fo = new FileOutputStream(new File(fname), false);

                     fo.write(contents);
                     fo.flush();
                     // remember close de FileOutput
                     fo.close();
                     **/

                    FileUtils.writeByteArrayToFile(new File(fname), contents);

                } catch (Exception e) {
                    Log.d("app5", "in  error oncomplete photo : " + e.getMessage());
                }

            } catch (Exception e) {
            }

            return fname;


        }

        protected void onProgressUpdate(Integer... progress) {

        }


        protected void onPostExecute(String result) {


        }
    }


    private boolean allDownloadsComplete() {


        DownloadManager downloadManager = (DownloadManager)
                _this.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Query query = new DownloadManager.Query();

        query.setFilterByStatus(DownloadManager.STATUS_RUNNING);
        Cursor c = downloadManager.query(query);
        if (c.moveToFirst()) {
            c.close();
            Log.i("app5", " download still running : " + DownloadManager.STATUS_RUNNING);
            return false;
        }

        Log.d("app5", "All done downloading!");
        return true;


    }

    MediaController mediaController = null;
    int totalDownloadedAlready = 0;

    BroadcastReceiver onComplete = new BroadcastReceiver() {

        @SuppressLint("Range")
        public void onReceive(Context ctxt, Intent intent) {


            totalDownloadedAlready++;

            Log.d("app5", "In Oncomplete :  " + totalMultiToDownload + "   " + totalDownloadedAlready);


            if (isMulti) {

                if (!isVideo) {


                    String fname = "";
                    Bitmap bitmap = null;
                    Bitmap originalBitmapBeforeNoCrop;

                    Bundle extras = intent.getExtras();
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
                    Cursor c = downloadManager.query(q);


                    if (c.moveToFirst()) {
                        @SuppressLint("Range") int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            // process download
                            fname = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));

                            fname = Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_PICTURES + Util.RootDirectoryMultiPhoto + fname;
                            // get other required data by changing the constant passed to getColumnIndex
                        }
                    }


                    new addWatermarkToFile().execute(fname);


                }


                if (totalDownloadedAlready == totalMultiToDownload) {
                    if (isQuickPost) {

                        scanMultiPostFolder();


                        showMultiDialog();
                        return;
                    } else if (isAutoSave) {

                        scanRegrannFolder();


                        finish();
                        return;
                    } else {


                        runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    showBottomButtons();
                                    Log.d("app5", "all downloads complete");
                                    if (spinner != null)
                                        spinner.setVisibility(View.GONE);


                                    removeProgressDialog();


                                } catch (Exception e4) {
                                }


                            }

                        });

                    }


                }
                return;
            }

            if (isQuickKeep) {
                if (isVideo)
                    removeProgressDialog();


                clearClipboard();

                copyPostLaterToPictureFolder();


                Toast toast = Toast.makeText(ShareActivity.this, R.string.postlaterconfirmtoastvideo, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                toast.show();
                finish();
                return;

            }


            if (isVideo) {
                removeProgressDialog();
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                Cursor cursor = downloadManager.query(new DownloadManager.Query().setFilterById(id));
                if (cursor != null && cursor.moveToNext()) {
                    @SuppressLint("Range") int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                    cursor.close();
                    if (status == DownloadManager.STATUS_FAILED) {
                        // do something when failed
                        sendEvent("download_failed");
                        GET(initialURL);
                        return;
                    } else if (status == DownloadManager.STATUS_PENDING || status == DownloadManager.STATUS_PAUSED) {
                        // do something pending or paused
                    } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // do something when successful
                    } else if (status == DownloadManager.STATUS_RUNNING) {
                        // do something when running
                    }
                }


                scanRegrannFolder();


                if (isAutoSave) {
                    new CopyFileTask(_this).execute();
                    //   finish();
                } else if (isQuickPost) {
                    if (isVideo) {

                        quickPostSendToInstagram();

                    }
                } else {


                    Log.d("app5", "preparing video player");
                    videoPlayer = findViewById(R.id.videoplayer);
                    videoPlayer.setOnPreparedListener(PreparedListener);
                    videoPlayer.setKeepScreenOn(true);
                    // creating object of
                    // media controller class


                    mediaController = new MediaController(_this) {

                        @Override
                        public boolean dispatchKeyEvent(KeyEvent event) {
                            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                                super.hide();
                                ((Activity) getContext()).finish();
                                return true;
                            }
                            return super.dispatchKeyEvent(event);
                        }
                    };

                    // sets the anchor view
                    // anchor view for the videoView
                    mediaController.setAnchorView(videoPlayer);

                    // sets the media player to the videoView
                    mediaController.setMediaPlayer(videoPlayer);


                    videoPlayer.setMediaController(mediaController);
                    mediaController.setVisibility(View.VISIBLE);
                    mediaController.setEnabled(true);

                    videoPlayer.setVideoPath(Util.getTempVideoFilePath());
                    videoPlayer.setVisibility(View.VISIBLE);


                    photoReady = true;


                }


            }


        }
    };

    public void videoDownloadComplete(boolean done, boolean fromSocial) {
        removeProgressDialog();

        if (spinner != null) {
            Log.d("app5", "remove spinne 4635r");
            spinner.setVisibility(View.GONE);
        }

        if (!done) {
            sendEvent("download_failed_vid_complete_" + fromSocial);
            if (!fromSocial) {
                showErrorToast("error", "There was a problem finding this video from Instagram.  Please try another one.", true);
                //   GET(initialURL);
            }
            return;
        }


        scanRegrannFolder();


        if (isAutoSave) {
            new CopyFileTask(_this).execute();
            //   finish();
        } else if (isQuickPost) {
            if (isVideo) {

                quickPostSendToInstagram();

            }
        } else {


            Log.d("app5", "preparing video player");
            videoPlayer = findViewById(R.id.videoplayer);
            videoPlayer.setOnPreparedListener(PreparedListener);
            videoPlayer.setKeepScreenOn(true);
            // creating object of
            // media controller class


            mediaController = new MediaController(_this) {

                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        super.hide();
                        ((Activity) getContext()).finish();
                        return true;
                    }
                    return super.dispatchKeyEvent(event);
                }
            };

            // sets the anchor view
            // anchor view for the videoView
            mediaController.setAnchorView(videoPlayer);

            // sets the media player to the videoView
            mediaController.setMediaPlayer(videoPlayer);


            videoPlayer.setMediaController(mediaController);
            mediaController.setVisibility(View.VISIBLE);
            mediaController.setEnabled(true);

            videoPlayer.setVideoPath(Util.getTempVideoFilePath());
            videoPlayer.setVisibility(View.VISIBLE);


            photoReady = true;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {
                        showBottomButtons();
                    } catch (Exception e) {
                        Log.d("app5", "on finish");
                    }
                }
            }, 1000);

        }
    }

    MediaPlayer.OnPreparedListener PreparedListener = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer m) {
            try {
                if (m.isPlaying()) {
                    m.stop();
                    m.release();
                    m = new MediaPlayer();
                }
                m.setVolume(0f, 0f);
                m.setLooping(false);
                m.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void CheckDwnloadStatus(long id) {

        // TODO Auto-generated method stub
        DownloadManager.Query query = new DownloadManager.Query();

        query.setFilterById(id);
        Cursor cursor = downloadManager.query(query);
        if (cursor.moveToFirst()) {
            int columnIndex = cursor
                    .getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            int columnReason = cursor
                    .getColumnIndex(DownloadManager.COLUMN_REASON);
            int reason = cursor.getInt(columnReason);

            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    String failedReason = "";
                    switch (reason) {
                        case DownloadManager.ERROR_CANNOT_RESUME:
                            failedReason = "ERROR_CANNOT_RESUME";
                            break;
                        case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                            failedReason = "ERROR_DEVICE_NOT_FOUND";
                            break;
                        case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                            failedReason = "ERROR_FILE_ALREADY_EXISTS";
                            break;
                        case DownloadManager.ERROR_FILE_ERROR:
                            failedReason = "ERROR_FILE_ERROR";
                            break;
                        case DownloadManager.ERROR_HTTP_DATA_ERROR:
                            failedReason = "ERROR_HTTP_DATA_ERROR";
                            break;
                        case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                            failedReason = "ERROR_INSUFFICIENT_SPACE";
                            break;
                        case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                            failedReason = "ERROR_TOO_MANY_REDIRECTS";
                            break;
                        case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                            failedReason = "ERROR_UNHANDLED_HTTP_CODE";
                            break;
                        case DownloadManager.ERROR_UNKNOWN:
                            failedReason = "ERROR_UNKNOWN";
                            break;
                    }

                    //  Toast.makeText(this, "FAILED: " + failedReason,
                    //        Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_PAUSED:
                    String pausedReason = "";

                    switch (reason) {
                        case DownloadManager.PAUSED_QUEUED_FOR_WIFI:
                            pausedReason = "PAUSED_QUEUED_FOR_WIFI";
                            break;
                        case DownloadManager.PAUSED_UNKNOWN:
                            pausedReason = "PAUSED_UNKNOWN";
                            break;
                        case DownloadManager.PAUSED_WAITING_FOR_NETWORK:
                            pausedReason = "PAUSED_WAITING_FOR_NETWORK";
                            break;
                        case DownloadManager.PAUSED_WAITING_TO_RETRY:
                            pausedReason = "PAUSED_WAITING_TO_RETRY";
                            break;
                    }

                    //   Toast.makeText(this, "PAUSED: " + pausedReason,
                    //         Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_PENDING:
                    //    Toast.makeText(this, "PENDING", Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_RUNNING:
                    //   Toast.makeText(this, "RUNNING", Toast.LENGTH_LONG).show();
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:

                    //   Toast.makeText(this, "SUCCESSFUL", Toast.LENGTH_LONG).show();

                    break;
            }
        }

    }


    private void copyAllMultiToSave() {


        new LongOperation().execute("");


    }


    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {


            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            File dir = new File(regrannMultiPostFolder);
            if (dir.isDirectory()) {


                // String[] children = dir.list();
                File[] children = dir.listFiles();


                if (children != null && children.length > 1) {
                    Arrays.sort(children, new Comparator<File>() {
                        @Override
                        public int compare(File object1, File object2) {
                            return object1.getName().compareTo(object2.getName());
                        }
                    });


                    for (int i = 0; i < children.length; i++) {

                        try {

                            if (!children[i].getName().contains("nomedia")) {
                                File source = children[i];


                                File destination = new File(regrannPictureFolder + File.separator + children[i].getName());
                                try {
                                    copy(source, destination);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            //   Thread.sleep(1000) ;
                        } catch (Exception e) {
                            int i4 = 1;
                        }

                    }
                }
            }

            Log.i("app5", "done copy multi");

            scanRegrannFolder();

            if (noAds && isAutoSave) {
                // user is premium and we are in quick save mode
                removeProgressDialog();
                Toast toast = Toast.makeText(ShareActivity.this, "Saving multi-post complete.", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();
                if (spinner != null) {
                    Log.d("app5", "remove spinne 4635r");
                    spinner.setVisibility(View.GONE);
                }

                finish();
                return;
            }


            if (isAutoSave) {
                // user is premium and we are in quick save mode
                if (false) {
                } else {
                    if (spinner != null) {
                        Log.d("app5", "remove spinner");
                        spinner.setVisibility(View.GONE);
                    }
                    Toast toast = Toast.makeText(ShareActivity.this, "Saving multi-post complete.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                    toast.show();

                }


                return;

            }


            changeSaveButton();


            removeProgressDialog();
            Toast toast = Toast.makeText(ShareActivity.this, "Saving multi-post complete.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.show();


            //    Toast toast = Toast.makeText(ShareActivity.this, "Saving multi-post photos and videos.", Toast.LENGTH_LONG);
            //  toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            //  toast.show();


            int numWarnings = sharedPref.getInt("countOfRuns", 0);

            //    numWarnings = 5;

            if (numWarnings < 3) {
                addToNumSessions();
                return;
            }


            if (showInterstitial) {


            }


        }

        @Override
        protected void onPreExecute() {


            if (!isAutoSave)
                startProgressDialog();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }

    }


    boolean autoSaving = false;

    public void copy(File src, File dst) throws IOException {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Log.d("app5", "Copying :  " + src.toString() + "   " + dst.toString());
            Files.copy(src.toPath(), dst.toPath());
        } else {


            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();

        }
    }

    private class CopyFileTask extends AsyncTask<Void, Void, Boolean> {
        private final Context context;
        private String message;

        public CopyFileTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //   spinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Boolean b) {
            //  spinner.setVisibility(View.GONE);
            changeSaveButton();

            if (noAds && isAutoSave) {
                // user is premium and we are in quick save mode

                Toast toast = Toast.makeText(getBaseContext(), saveToastMsg, Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.show();

                finish();

            }


            if (isAutoSave) {
                // user is premium and we are in quick save mode


                //   findViewById(R.id.upgradeBtn).setVisibility(View.VISIBLE);
                //  findViewById(R.id.backBtn).setVisibility(View.VISIBLE);
                TextView t = findViewById(R.id.autosaveText);
                t.setText("Quick Save Done");


                int delay = 500;

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("app5", "remove spinner 4903");
                        //  spinner.setVisibility(View.GONE);


                        sendEvent("gms5_ad_needs_to_show");


                        finish();

                    }
                }, delay);
            }

        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                // Your file copy logic here

                // Note: Avoid any UI operations here, as this is the background thread
                File src;

                Long currTime = System.currentTimeMillis();


                String fname;
                Toast toast;
                String msg;
                if (isVideo) {
                    src = new File(Util.getTempVideoFilePath());
                    fname = regrannPictureFolder + File.separator + author + "_video_" + currTime + ".mp4";
                    saveToastMsg = "Video was saved in /Pictures/Regrann/Video-" + currTime + ".mp4";
                } else {


                    src = lastDownloadedFile;
                    fname = regrannPictureFolder + File.separator + author + "-" + currTime + ".jpg";
                    saveToastMsg = "Photo was saved in /Pictures/Regrann/" + author + "  -" + currTime + ".jpg";
                }


                File dst = new File(fname);

                copy(src, dst);

                Intent mediaScannerIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri fileContentUri = Uri.fromFile(dst); // With 'permFile' being the File object
                mediaScannerIntent.setData(fileContentUri);
                _this.sendBroadcast(mediaScannerIntent);


                return true; // Return true if successful
            } catch (Exception e) {
                // Handle exceptions
                return false;
            }
        }
    }


    private static String queryName(Context context, Uri uri) {
        Cursor returnCursor =
                context.getContentResolver().query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
    }

    private PhotoEditorSettingsList createPesdkSettingsList() {

        // Create a empty new SettingsList and apply the changes on this referance.
        PhotoEditorSettingsList settingsList = new PhotoEditorSettingsList(true);

        // If you include our asset Packs and you use our UI you also need to add them to the UI,
        // otherwise they are only available for the backend
        // See the specific feature sections of our guides if you want to know how to add our own Assets.

        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );


        return settingsList;
    }


    private VideoEditorSettingsList createVideoPesdkSettingsList() {

        // Create a empty new SettingsList and apply the changes on this referance.
        VideoEditorSettingsList settingsList = new VideoEditorSettingsList(true);
        settingsList.getSettingsModel(UiConfigFilter.class).setFilterList(
                FilterPackBasic.getFilterPack()
        );

        settingsList.getSettingsModel(UiConfigText.class).setFontList(
                FontPackBasic.getFontPack()
        );
        return settingsList;
    }


    private void openEditor(Uri inputImage) {

        try {
            PhotoEditorSettingsList settingsList = createPesdkSettingsList();

            // Set input image
            settingsList.getSettingsModel(LoadSettings.class).setSource(inputImage);

            settingsList.getSettingsModel(PhotoEditorSaveSettings.class).setOutputToUri(inputImage);


            new EditorBuilder(this)
                    .setSettingsList(settingsList)
                    .startActivityForResult(this, PESDK_RESULT);

            settingsList.release();
        } catch (Exception e) {
            Log.d("app5", e.getMessage());
        }
    }


    private void openVideoEditor(Uri inputVideo) {

        try {
            VideoEditorSettingsList settingsList = createVideoPesdkSettingsList();

            // Set input image
            settingsList.getSettingsModel(LoadSettings.class).setSource(inputVideo);

            settingsList.getSettingsModel(VideoEditorSaveSettings.class).setOutputToUri(inputVideo);


            new EditorBuilder(this)
                    .setSettingsList(settingsList)
                    .startActivityForResult(this, VESDK_RESULT);

            settingsList.release();
        } catch (Exception e) {
            Log.d("app5", e.getMessage());
        }
    }


    private void shareWithChooser() {


        try {
            // flurryAgent.logEvent("Share button pressed");
            // Create the new Intent using the 'Send' action.
            Intent share = new Intent(Intent.ACTION_SEND);

            share.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            String txt;

            if (inputMediaType == 0) {
                String caption;
                //count how many hashtags

                if (title != null && (!Util.isKeepCaption(_this))) {


                    share.putExtra(Intent.EXTRA_SUBJECT, "Regrann from @" + author);
                    txt = "Credit from @" + author + "  -  " + title + "  -  " + getIntent().getStringExtra("mediaUrl");

                } else {
                    share.putExtra(Intent.EXTRA_SUBJECT, "Photo share");

                    txt = "  ";
                }

                caption = txt;
                share.putExtra(Intent.EXTRA_TEXT, caption);
                //  clearClipboard();


            } else {
                String caption = "";
                if (!Util.isKeepCaption(_this))
                    caption = "@regrann no-crop:";


                share.putExtra(Intent.EXTRA_TEXT, caption);

                //   clearClipboard();

            }


            Uri MediaURI;


            if (isVideo) {

                File t = new File(Util.getTempVideoFilePath());


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", t);
                } else {
                    MediaURI = Uri.fromFile(t);
                }


            } else {
                Log.d("app5", "tempfile :  " + tempFile.toString());


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);
                } else {
                    MediaURI = Uri.fromFile(tempFile);
                }


            }

            if (isVideo) {
                share.setType("video/mp4");
                share.putExtra(Intent.EXTRA_STREAM, MediaURI);
            } else {
                share.putExtra(Intent.EXTRA_STREAM, MediaURI);

                share.setType("image/*");
            }
            // Broadcast the Intent.
            startActivity(Intent.createChooser(share, "Share to"));

            cleanUp();


            finish();
        } catch (Exception e) {
            showErrorToast(e.getMessage(), getString(R.string.therewasproblem));

        }

    }


    private boolean currentToFeedBtnPressed = false;

    @Override
    public void onClick(View v) {
        try {


            if (!photoReady)
                return;


            if (v == btndownloadphoto) {
                copyCaptionToClipboard();


                if (isMulti) {


                    copyAllMultiToSave();
                    sendEvent("sc_savebtn_multi");

                    return;
                }

                new CopyFileTask(_this).execute();


                return;

            }


            if (v == btnShare) {


                sendEvent("sc_sharebtn");


                copyCaptionToClipboard();

                shareWithChooser();

                /**
                 try {

                 // flurryAgent.logEvent("Share button pressed");
                 // Create the new Intent using the 'Send' action.
                 Intent share = new Intent(Intent.ACTION_SEND);

                 share.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                 String txt;

                 if (inputMediaType == 0) {
                 String caption;
                 //count how many hashtags

                 if (title != null && (!Util.isKeepCaption(_this))) {


                 share.putExtra(Intent.EXTRA_SUBJECT, "Regrann from @" + author);
                 txt = "@Regrann from @" + author + "  -  " + title + "  -  " + getIntent().getStringExtra("mediaUrl")
                 + "\n\nRegrann App - Repost without leaving Instagram - Download Here : http://regrann.com/download";
                 } else {
                 share.putExtra(Intent.EXTRA_SUBJECT, "Photo share");

                 txt = "  - via @Regrann app";
                 }

                 caption = txt;
                 share.putExtra(Intent.EXTRA_TEXT, caption);
                 clearClipboard();


                 } else {
                 String caption = "";
                 if (!Util.isKeepCaption(_this))
                 caption = "@regrann no-crop:";


                 share.putExtra(Intent.EXTRA_TEXT, caption);

                 clearClipboard();

                 }


                 if (isVideo) {
                 share.setType("video/*");

                 share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(Util.getTempVideoFilePath())));
                 } else {
                 share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));

                 share.setType("image/*");
                 }
                 // Broadcast the Intent.
                 startActivity(Intent.createChooser(share, "Share to"));

                 cleanUp();


                 finish();
                 } catch (Exception e) {
                 showErrorToast(e.getMessage(), getString(R.string.therewasproblem));

                 }
                 **/
                return;


            }

            if (v == btndownloadsingle) {
                currentToFeedBtnPressed = true;
                sendEvent("sc_single_download");


                runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            if (Objects.requireNonNull(mDemoSlider.getCurrentSlider().getBundle().get("is_video")).toString() == "false") {
                                //  Log.d("app5", mDemoSlider.getCurrentSlider().getBundle().get("fname").toString());
                                lastDownloadedFile = new File(mDemoSlider.getCurrentSlider().getBundle().get("fname").toString());
                                isVideo = false;

                            } else {

                                isVideo = true;
                                //  tempVideoFile = new File(mDemoSlider.getCurrentSlider().getBundle().get("fname").toString());
                                Util.setTempVideoFileName(mDemoSlider.getCurrentSlider().getBundle().get("fname").toString());

                            }


                            new CopyFileTask(_this).execute();


                        } catch (Exception e) {
                        }
                    }
                });


                return;


            }

            if (v == btnCurrentToFeed) {
                currentToFeedBtnPressed = true;
                sendEvent("sc_current_to_feed");

                if (isMulti) {

                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                if (Objects.requireNonNull(mDemoSlider.getCurrentSlider().getBundle().get("is_video")).toString() == "false") {
                                    //  Log.d("app5", mDemoSlider.getCurrentSlider().getBundle().get("fname").toString());
                                    tempFile = new File(mDemoSlider.getCurrentSlider().getBundle().get("fname").toString());
                                    isVideo = false;
                                } else {

                                    isVideo = true;
                                    //  tempVideoFile = new File(mDemoSlider.getCurrentSlider().getBundle().get("fname").toString());
                                    Util.setTempVideoFileName(mDemoSlider.getCurrentSlider().getBundle().get("fname").toString());
                                }

                                // reset isMulti to false so we after ad sendToInstagram will be called


                                sendToInstagam();


                                //   onClick(btnInstagram);

                            } catch (Exception e) {
                            }
                        }
                    });


                    return;
                }

            }


            if (v == postlater) {

                sendEvent("sc_postlaterbtn");


                if (inputMediaType != 0) {
                    title = "";
                    author = "";

                }


                copyPostLaterToPictureFolder();


                clearClipboard();

                Toast toast;
                if (isVideo) {
                    toast = Toast.makeText(ShareActivity.this, R.string.postlaterconfirmtoastvideo, Toast.LENGTH_SHORT);
                } else

                    toast = Toast.makeText(ShareActivity.this, R.string.postlaterconfirmtoast, Toast.LENGTH_SHORT);

                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                toast.show();

                //    cleanUp();

                finish();


                return;

            }


            if (v == btnInstagram || v == btnInstagramstories) {
                instagramBtnClicked = true;

                if (v == btnInstagramstories) {
                    btnStoriesClicked = true;
                    sendEvent("sc_storiesbtn");
                } else {
                    btnStoriesClicked = false;
                    sendEvent("sc_postfeedbtn");
                }

                if (isMulti) {


                    if (btnStoriesClicked)
                        sendMultiPhotosToStories();
                    else
                        showMultiDialog();


                    return;
                }


                final int numWarnings = preferences.getInt("captionWarning", 0);


                if (numWarnings < 3) {

                    addToNumSessions();
                    sendToInstagam();


                } else {


                    //    diff = 8 ;

                    sendToInstagam();


                }


            }


        } catch (Exception e) {
            showErrorToast(e.getMessage(), "#10 " + getString(R.string.therewasproblem));

        }

    }


    private void copyPostLaterToPictureFolder() {

        try {
            File videoDst = null;

            // Get the directory for the user's public pictures directory.
            File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + "/regrann_postlater");

            if (!file.mkdirs()) {
                Log.e("error", "Directory not created");
            }

            File folderDst = new File(Environment.getExternalStorageDirectory() + "/" +
                    Environment.DIRECTORY_PICTURES + "/regrann_postlater/" + new File(tempFileFullPathName).getName());


            copy(new File(tempFileFullPathName), folderDst);


            KeptListAdapter db = KeptListAdapter.getInstance(_this);

            if (isVideo) {

                videoDst = new File(Environment.getExternalStorageDirectory() + "/" +
                        Environment.DIRECTORY_PICTURES + "/regrann_postlater/" + Util.getCurrentVideoFileName());


                copy(new File(Util.getTempVideoFilePath()), videoDst);
                db.addItem(new InstaItem(title, folderDst.getAbsolutePath(), videoDst.getAbsolutePath(), author));

            } else {
                db.addItem(new InstaItem(title, folderDst.getAbsolutePath(), "", author));
            }
        } catch (Exception e) {
            Log.d("tag", e.getMessage());
        }
    }

    private void changeSaveButton() {

        try {
            btndownloadphoto.setImageResource(R.drawable.savedicon);
            btndownloadphoto.setEnabled(false);
        } catch (Exception e) {
        }


    }


    private void sendMultiPhotosToStories() {

        try {

            sendEvent("MultiToStories");
            // flurryAgent.logEvent("Instagram button pressed");
            // FlurryAgent.logEvent("Click Instagram");
            String caption = Util.prepareCaption(title, author, _this.getApplication().getApplicationContext(), caption_suffix, tiktokLink);


            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.setPackage("com.instagram.android");
            //  shareIntent.setClassName("com.instagram.android",instagram_activity);

            if (btnStoriesClicked) {
                sendEvent("ClickStoriesBtn", "", "");
                shareIntent.setClassName(
                        "com.instagram.android",
                        "com.instagram.share.handleractivity.MultiStoryShareHandlerActivity");
            }


            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);


            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Post caption", caption);
            Objects.requireNonNull(clipboard).setPrimaryClip(clip);


            ArrayList<Uri> uriList = getListOfMultiPhotos();

            if (uriList == null) {
                showErrorToast("error", "There was a problem.", true);
                return;
            }

            shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uriList);
            shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setType("*/*");


            int numWarnings = preferences.getInt("captionWarning", 0);

            Log.d("regrann", "Numwarnings : " + numWarnings);

            startActivity(Intent.createChooser(shareIntent, "Choose"));

            //startActivity(shareIntent);


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {
                        finish();
                    } catch (Exception e) {
                        Log.d("app5", "on finish");
                    }
                }
            }, 250);


        } catch (Exception e) {
            // bring user to the market to download the app.

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
            startActivity(intent);
        }

    }


    private ArrayList<Uri> getListOfMultiPhotos() {


        //     Thread thread = new Thread(new Runnable() {
        //       @Override
        //     public void run() {
        try {
            if (regrannMultiPostFolder != null) {

                File dir = new File(regrannMultiPostFolder);
                if (dir.isDirectory()) {
                    String[] children = dir.list();

                    final ArrayList<Uri> imageUris = new ArrayList<>(children.length);

                    Uri MediaURI;
                    for (int i = 0; i < children.length; i++) {
                        try {

                            if (!children[i].contains("nomedia")) {


                                File theDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES +
                                        Util.RootDirectoryMultiPhoto + children[i]);

                                Log.d("app5", theDir.toString());

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", theDir);
                                } else {
                                    MediaURI = Uri.fromFile(theDir);
                                }
                                imageUris.add(MediaURI);
                            }


                        } catch (Exception e) {
                            int i4 = 1;
                        }

                    }
                    return imageUris;

                }
            }
        } catch (Exception e) {
        }

        return null;


    }


    public boolean isClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }

    private void sendToInstagam() {
        Intent shareIntent = new Intent();
        Uri MediaURI = null;
        try {

            String caption = Util.prepareCaption(title, author, _this.getApplication().getApplicationContext(), caption_suffix, tiktokLink);


            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");
            //  shareIntent.setClassName("com.instagram.android",instagram_activity);


            if (isMulti) {


                Uri uri = Uri.parse("instagram://share");
                shareIntent = new Intent(Intent.ACTION_VIEW, uri);
                shareIntent.setPackage("com.instagram.android");

//Check that the user can open the intent, meaning they have the Instagram app installed


            } else if (btnStoriesClicked) {
                sendEvent("ClickStoriesBtn", "", "");
                shareIntent.setClassName(
                        "com.instagram.android",
                        "com.instagram.share.handleractivity.StoryShareHandlerActivity");
            } else {
                if (1 == 2) {

                    shareIntent.setClassName(
                            "com.instagram.android",
                            "com.instagram.share.handleractivity.ReelShareHandlerActivityMultiMediaAlias");


                } else {

                    shareIntent.setClassName(
                            "com.instagram.android",
                            "com.instagram.share.handleractivity.ShareHandlerActivity");
                }
            }


            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);


            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Post caption", caption);
            Objects.requireNonNull(clipboard).setPrimaryClip(clip);


            if (isMulti) {
                _this.startActivity(shareIntent);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            finish();
                        } catch (Exception e) {
                            Log.d("app5", "on finish");
                        }
                    }
                }, 2000);
                return;
            }


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }


            if (isVideo) {
                shareIntent.setType("video/mp4");
                File t = new File(Util.getTempVideoFilePath(isMulti));


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", t);
                } else {
                    MediaURI = Uri.fromFile(t);
                }


            } else {
                Log.d("app5", "tempfile :  " + tempFile.toString());


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);
                } else {
                    MediaURI = Uri.fromFile(tempFile);
                }


                shareIntent.setType("image/*");
            }
            //    shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, MediaURI);

            int numWarnings = preferences.getInt("captionWarning", 0);

            Log.d("regrann", "Numwarnings : " + numWarnings);

            if (btnStoriesClicked) {


                // Instantiate an intent
                Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");

// Set package
                intent.setPackage("com.instagram.android");

// Attach your App ID to the intent
                String appId = "773402562742917"; // This is your application's Facebook App ID
                intent.putExtra("com.instagram.platform.extra.APPLICATION_ID", appId);

// Attach your video to the intent from a URI
                Uri videoAssetUri = MediaURI;
                if (isVideo)
                    intent.setDataAndType(videoAssetUri, "video/mp4");
                else
                    intent.setDataAndType(videoAssetUri, "image/*");
                intent.putExtra(Intent.EXTRA_STREAM, videoAssetUri);

// Instantiate an activity
                Activity activity = _this;

// Grant URI permissions
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                //     List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY));

                //   for (ResolveInfo resolveInfo : resInfoList)
                // {
                String packageName = "com.instagram.android";

                activity.grantUriPermission(packageName, videoAssetUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //  }

// Verify that the activity resolves the intent and start it
                //   if (activity.getPackageManager().resolveActivity(intent, 0) != null)
                //  {
                activity.startActivityForResult(intent, 0);
                //  }

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            finish();
                        } catch (Exception e) {
                            Log.d("app5", "on finish");
                        }
                    }
                }, 2000);
                return;

            }


            if ((numWarnings < 3 && inputMediaType == 0)) {

                Log.d("regrann", "Numwarnings  2 : " + numWarnings);

                addToNumSessions();


                showPasteDialog(MediaURI);

            } else {

                if (isVideo) {


                    shareWithInstagramChooser();

                    return;
                }

                createInstagramIntent2(MediaURI);


                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            finish();
                        } catch (Exception e) {
                            Log.d("app5", "on finish");
                        }
                    }
                }, 2000);


            }

        } catch (Exception e8) {


            shareWithInstagramChooser();

        }


    }


    boolean shareChooserActive = false;

    private void shareWithInstagramChooser() {


        try {

            // Create the new Intent using the 'Send' action.
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setPackage("com.instagram.android");


            share.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            Uri MediaURI;


            if (isVideo) {

                File t = new File(Util.getTempVideoFilePath());


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", t);
                } else {
                    MediaURI = Uri.fromFile(t);
                }


            } else {
                Log.d("app5", "tempfile :  " + tempFile.toString());


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);
                } else {
                    MediaURI = Uri.fromFile(tempFile);
                }


            }

            if (isVideo) {
                share.setType("video/mp4");
                share.putExtra(Intent.EXTRA_STREAM, MediaURI);
            } else {
                share.putExtra(Intent.EXTRA_STREAM, MediaURI);

                share.setType("image/*");
            }


            // Broadcast the Intent.
            startActivity(Intent.createChooser(share, "Share to"));

            cleanUp();


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 2000);
        } catch (Exception e) {
            showErrorToast(e.getMessage(), getString(R.string.therewasproblem));

        }

    }


    private void createInstagramIntent2(Uri MediaURI) {
        Intent intent = new Intent("com.instagram.share.ADD_TO_FEED");

// Set package
        intent.setPackage("com.instagram.android");

// Attach your App ID to the intent
        String appId = "773402562742917"; // This is your application's Facebook App ID
        intent.putExtra("com.instagram.platform.extra.APPLICATION_ID", appId);

// Attach your video to the intent from a URI


        String type = "image/*";

        if (isVideo)
            type = "video/mp4";

        intent.setDataAndType(MediaURI, type);
        intent.putExtra(Intent.EXTRA_STREAM, MediaURI);

// Instantiate an activity
        Activity activity = _this;

// Grant URI permissions
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        //     List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY));

        //   for (ResolveInfo resolveInfo : resInfoList)
        // {
        String packageName = "com.instagram.android";

        activity.grantUriPermission(packageName, MediaURI, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        //  }

// Verify that the activity resolves the intent and start it
        //   if (activity.getPackageManager().resolveActivity(intent, 0) != null)
        //  {
        activity.startActivityForResult(intent, 0);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                try {

                    finish();
                } catch (Exception e) {
                    Log.d("app5", "on finish");
                }
            }
        }, 2000);
        //  }
    }


    private void shareWithInstagramChooser(Uri MediaURI) {


        try {
            // flurryAgent.logEvent("Share button pressed");
            // Create the new Intent using the 'Send' action.
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setPackage("com.instagram.android");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

            Intent i = ShareCompat.IntentBuilder.from(_this)
                    .setText("Share to")

                    .setStream(MediaURI)

                    .getIntent()
                    .setPackage("com.instagram.android");


            if (isVideo) {
                i.setType("video/mp4");

            } else {


                i.setType("image/*");
            }
            // Broadcast the Intent.


            startActivity(i);


            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //
                    finish();

                }
            }, 6000);
        } catch (Exception e) {
            showErrorToast(e.getMessage(), getString(R.string.therewasproblem));

        }

    }


    private void createInstagramIntent(String type, Uri MediaURI) {

        // Create the new Intent using the 'Send' action.
        Intent share = new Intent(Intent.ACTION_SEND);

        // Set the MIME type
        share.setType(type);

        // Create the URI from the media

        Uri uri = MediaURI;

        // Add the URI to the Intent.
        share.putExtra(Intent.EXTRA_STREAM, uri);

        // Broadcast the Intent.
        startActivity(Intent.createChooser(share, "Share to"));
    }


    /**
     * private void shareWithInstagramChooser() {
     * <p>
     * <p>
     * try {
     * // flurryAgent.logEvent("Share button pressed");
     * // Create the new Intent using the 'Send' action.
     * Intent share = new Intent(Intent.ACTION_SEND);
     * share.setPackage("com.instagram.android");
     * //   share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
     * <p>
     * <p>
     * <p>
     * Uri MediaURI;
     * <p>
     * <p>
     * if (isVideo) {
     * <p>
     * File t = new File(Util.getTempVideoFilePath());
     * <p>
     * <p>
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
     * MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", t);
     * } else {
     * MediaURI = Uri.fromFile(t);
     * }
     * <p>
     * <p>
     * } else {
     * Log.d("app5", "tempfile :  " + tempFile.toString());
     * <p>
     * <p>
     * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
     * MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);
     * } else {
     * MediaURI = Uri.fromFile(tempFile);
     * }
     * <p>
     * <p>
     * }
     * <p>
     * if (isVideo) {
     * share.setType("video/*");
     * share.putExtra(Intent.EXTRA_STREAM, MediaURI);
     * } else {
     * share.putExtra(Intent.EXTRA_STREAM, MediaURI);
     * <p>
     * share.setType("image/*");
     * }
     * // Broadcast the Intent.
     * startActivity(Intent.createChooser(share, "Share to"));
     * <p>
     * <p>
     * final Handler handler = new Handler();
     * handler.postDelayed(new Runnable() {
     *
     * @Override public void run() {
     * <p>
     * // finish();
     * <p>
     * }
     * }, 2000);
     * } catch (Exception e) {
     * showErrorToast(e.getMessage(), getString(R.string.therewasproblem));
     * <p>
     * }
     * <p>
     * }
     **/


    public static String resolveRedirect(String initialUrl) throws IOException {
        String currentUrl = initialUrl;
        HttpURLConnection connection = null;
        int redirectCount = 0;
        final int maxRedirects = 5; // Define a maximum number of redirects to follow

        while (redirectCount < maxRedirects) {
            URL url = new URL(currentUrl);
            connection = (HttpURLConnection) url.openConnection();

            // Set the HTTP request method to "HEAD" to only retrieve the headers
            connection.setRequestMethod("HEAD");

            // Follow redirects (HTTP status codes 301 and 302)
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_MOVED_PERM || responseCode == HttpURLConnection.HTTP_MOVED_TEMP) {
                String newLocation = connection.getHeaderField("Location");
                if (newLocation != null) {
                    currentUrl = newLocation;
                } else {
                    // No "Location" header found; unable to resolve further
                    break;
                }
            } else {
                // Not a redirect status code; return the current URL
                break;
            }

            redirectCount++;
        }

        if (redirectCount >= maxRedirects) {
            // Reached the maximum number of redirects; return the last URL
            return currentUrl;
        }


        // Get the final URL from the HttpURLConnection object
        if (connection != null) {
            URL finalUrl = connection.getURL();
            return finalUrl.toString();
        }


        return currentUrl;
    }


    // private void proxyRequest(String url) {

    //   String res = prox(url);
    // onDataLoaded(res, url);


    //}


    private void proxyRequest(String shortcode) {

        RequestQueue queue = Volley.newRequestQueue(this);

        String final_url = "https://pyapp.jaredco.com/rapid_pro_getjson_v2/?shortcode=" + shortcode;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, final_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        if (response.equals("ERROR"))
                            shouldRetryVolley();
                        else

                            try {
                                extractPostDetails(response);
                                /**
                                JSONObject json = new JSONObject(response);
                                 JSONObject graphQlObject = json.getJSONObject("data");
                                processJSON(graphQlObject.toString());
                                 **/
                            } catch (Exception e) {

                            }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int code = 0;
                try {
                    shouldRetryVolley();


                } catch (Exception e) {
                }


            }
        });


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(32000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    AlertDialog waitdialog;

    // TWITTER STUFF
    public void downloadParts(String url) throws IOException {
        RequestQueue queue = Volley.newRequestQueue(this);

        runOnUiThread(new Runnable() {
            public void run() {

                if (spinner != null) {
                    Log.d("app5", "remove spinne 4635r");
                    spinner.setVisibility(View.GONE);
                }

            }
        });


        runOnUiThread(new Runnable() {
            public void run() {
                pd = ProgressDialog.show(ShareActivity.this, _this.getString(R.string.progress_dialog_msg), "Looking for media.....", true, true);

                Toast toast = Toast.makeText(_this, "Looking for media...", Toast.LENGTH_LONG);

                toast.show();
            }
        });
        url = resolveRedirect(url);
        String final_url = "https://pyapp.jaredco.com/?url=" + url;

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, final_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                removeProgressDialog();
                                pd = ProgressDialog.show(ShareActivity.this, _this.getString(R.string.progress_dialog_msg), "Found - Starting download.....", true, true);

                            }
                        });
                        twitterDataLoaded(response, final_url);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int code = 0;
                try {
                    code = error.networkResponse.statusCode;

                    Log.d("app5", "twitter_Error - not found at all");
                } catch (Exception e) {
                }


            }
        });


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(32000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }

    String currentTwitterVideo;
    long twitterDownloadId = 0;

    public void deleteVideoFromServer() throws IOException {
        RequestQueue queue = Volley.newRequestQueue(this);


        String final_url = "https://pyapp.jaredco.com/delete/" + currentTwitterVideo + ".mp4";
        Log.d("app5", final_url);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, final_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                int code = 0;
                try {
                    code = error.networkResponse.statusCode;

                    Log.d("app5", "scrape_error_code_" + code);
                } catch (Exception e) {
                }


            }
        });


        stringRequest.setRetryPolicy(new DefaultRetryPolicy(32000,
                2,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
// Add the request to the RequestQueue.
        queue.add(stringRequest);


    }


    public void twitterVideoNotFound() {
        runOnUiThread(new Runnable() {
            public void run() {
                showErrorToast("Video not found", "There is no video in this " + socialApp + " post.", true);
            }
        });
    }


    public void twitterDataLoaded(String volleyReturn, String t) {

        Log.d("app5", volleyReturn);

        if (volleyReturn.equals("twitter_error")) {
            // twitter error message
            twitterDownloadId = 0;
            twitterVideoNotFound();
            Log.d("app5", "Twitter error message");

        }

        if (!volleyReturn.equals("error")) {

            currentTwitterVideo = volleyReturn;

            if (volleyReturn.equals("twitter_error"))
                return;

            String url = "https://pyapp.jaredco.com/public/" + currentTwitterVideo + ".mp4";
            try {
                Util.setTempVideoFileName(tempVideoName);
                // tempVideoFile = new File(Util.getTempVideoFilePath(false));
                title = "";
                author = "";
                isVideo = true;
                isMulti = false;
                //  tempVideoFullPathName = tempVideoFile.getPath();
                //    twitterDownloadId = Util.startDownload(url, "", this, tempVideoName);

                FileDownloader.downloadFile(this, url, tempVideoName, true);

                runOnUiThread(new Runnable() {
                    public void run() {


                        if (mainUI != null)
                            mainUI.setVisibility(View.VISIBLE);


                    }
                });


            } catch (Exception e) {
            }

        }

    }


}
