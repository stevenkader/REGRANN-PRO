package com.nimmble.rgpro.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.nimmble.rgpro.R;
import com.nimmble.rgpro.sqlite.KeptListAdapter;
import com.nimmble.rgpro.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegrannMainActivity extends AppCompatActivity {

    Intent fileObserverIntent;
    SharedPreferences preferences;
    public static RegrannMainActivity _this;

    PendingIntent pendingIntent;
    private SimpleCursorAdapter dataAdapter;
    public static String caption_prefix = "Reposted";

    boolean firstRun, olderUser;

    boolean noAds;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;


    ProgressBar spinner;

    private BillingClient billingClient;
    boolean billingReady = false;
    AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        _this = this;

        try {
            //   Log.d("app5", "score " + getIntent().getStringExtra("score"));
        } catch (Exception e) {
        }
        preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication()
                .getApplicationContext());

        String country = getUserCountry(getApplicationContext());
        Log.d("app", "countr code " + country);
        SharedPreferences.Editor editor1 = preferences.edit();
        editor1.putBoolean("removeAds", true);

        Util.retreivePurchase();

        editor1.commit();


        //  PRO p = new PRO(_this);
        //   p.checkIsActiveSub();

        noAds = true;

        // public static InstaAPI instaAPI;
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
                                editor.putString("multipost_videoid", mFirebaseRemoteConfig.getString("multipost_videoid"));
                                //   editor.putString("caption_prefix", mFirebaseRemoteConfig.getString("caption_prefix"));
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


        Intent i = getIntent();
        try {
            Uri data = i.getData();
            String path = data != null ? data.getScheme() : null;
            if (path != null)
                if (path.equals("rg")) {
                    Uri uri = Uri.parse("https://instagram.com");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    intent.setPackage("com.instagram.android");
                    startActivity(intent);
                    finish();
                    return;
                }
            //add more cases if multiple links into app.
        } catch (NullPointerException e) {
            // Catch if no path data is send
        }


        //       MobileAds.initialize(getApplicationContext(), "ca-app-pub-1489694459182078~6548979145");


        //    instaAPI = new InstaAPI(this);


        if (getIntent().hasExtra("update_notice")) {
            Log.d("regrann", "UPDATE NOTICE SEEN");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("market://details?id=com.nimmble.rgpro"));
            startActivity(intent);
            finish();
            return;

        }

        if (getIntent().hasExtra("weburl")) {
            Log.d("regrann", "WEB NOTICE SEEN");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(getIntent().getStringExtra("weburl")));
            startActivity(intent);
            finish();
            return;

        }


        firstRun = preferences.getBoolean("startShowTutorial", true);
        olderUser = preferences.getBoolean("oldUser", false);


        if (!firstRun && !olderUser)  // is this an old user before this new method
        {


            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("oldUser", true);

            boolean quickpost = preferences.getBoolean("quickpost", false);
            boolean quicksave = preferences.getBoolean("quicksave", false);
            boolean quickkeep = preferences.getBoolean("quickkeep", false);
            boolean normalMode = preferences.getBoolean("normalMode", true);


            editor.putBoolean("quickpost", quickpost);
            editor.putBoolean("quicksave", quicksave);
            editor.putBoolean("quickkeep", quickkeep);
            editor.putBoolean("normalMode", normalMode);


            if (quickpost)
                editor.putString("mode_list", "1");
            if (normalMode)
                editor.putString("mode_list", "2");
            if (quicksave)
                editor.putString("mode_list", "3");
            if (quickkeep)
                editor.putString("mode_list", "4");


            editor.commit();
        }

        boolean newUser = preferences.getBoolean("firstRun", true);


        if (newUser) {


            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstRun", false);
            editor.putBoolean("foreground_checkbox", false);
            editor.commit();


        }

        boolean firstCheckForForegroundSetting = preferences.getBoolean("firstCheckForForegroundSetting", true);


        if (firstCheckForForegroundSetting) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("firstCheckForForegroundSetting", false);
            editor.putBoolean("foreground_checkbox", true);
            editor.commit();


        }


        if ((firstRun && newUser)) {

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("startShowTutorial", false);
            editor.commit();

            startActivity(new Intent(this, OnBoardingActivity.class));
            finish();
            return;
        }


        // did we come from the post later screen
        if (this.getIntent().hasExtra("show_home"))
            showMainScreen();
        else
            checkForPostLaterPhotos();


    }


    public void onClickBtnChangeLogin(View v) {

        Intent myIntent = new Intent(RegrannMainActivity.this, InstagramLogin.class);
        _this.startActivity(myIntent);

    }


    private boolean addPermission(List<String> permissionsList, String permission) {
        int i = ActivityCompat.checkSelfPermission(this, permission);

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


            startActivity(i);


        }

    }


    @Override
    protected void onResume() {
        super.onResume();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT <= 29) {
                    //Do something after 100ms
                    checkPermissions();
                }
            }
        }, 1000);


        invalidateOptionsMenu();

    }


    public void onClickBtnSupport(View v) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_this);

        // flurryAgent.logEvent("Rating Good or Bad Dialog");
        // set dialog message
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

        alertDialogBuilder.setMessage("Are you stuck?  Do you want to email support?").setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        emailSupport();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        // flurryAgent.logEvent("Good Selected");

                        dialog.cancel();
                    }
                });

        // create alert dialog
        alertDialogBuilder.create().show();

    }

    public void onClickBtnHelp(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.regrann.com/support"));
        startActivity(browserIntent);
        RegrannApp.sendEvent("rmain_help_btn");
    }

    public void onClickSettingsBtn(View v) {
        RegrannApp.sendEvent("rmain_settings_btn");
        // TODO Auto-generated method stub


        startActivity(new Intent(this, SettingsActivity2.class));

    }

    /**
     * public void queryPurchases() {
     * Runnable queryToExecute = new Runnable() {
     *
     * @Override public void run() {
     * <p>
     * try {
     * <p>
     * <p>
     * <p>
     * RegrannApp.sendEvent("query_purchases", "", "");
     * RegrannApp.sendEvent("query_purchases", "", "");
     * Purchase.PurchasesResult purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP);
     * <p>
     * if (purchasesResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
     * <p>
     * RegrannApp.sendEvent("query_purchases_ok", "", "");
     * if (purchasesResult.getPurchasesList().size() > 0) {
     * <p>
     * <p>
     * <p>
     * for (Purchase purchase : purchasesResult.getPurchasesList()) {
     * <p>
     * if (false) {
     * <p>
     * <p>
     * ConsumeParams consumeParams = ConsumeParams.newBuilder()
     * .setPurchaseToken(purchase.getPurchaseToken())
     * .build();
     * <p>
     * ConsumeResponseListener consumeResponseListener = new ConsumeResponseListener() {
     * @Override public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
     * <p>
     * }
     * };
     * <p>
     * SharedPreferences.Editor editor;
     * editor = preferences.edit();
     * <p>
     * editor.putBoolean("subscribed", false);
     * <p>
     * editor = preferences.edit();
     * editor.putBoolean("really_subscribed", false);
     * <p>
     * <p>
     * <p>
     * editor.commit();
     * <p>
     * billingClient.consumeAsync(consumeParams, consumeResponseListener);
     * }
     * <p>
     * <p>
     * if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
     * RegrannApp.sendEvent("query_purchase_foundpurchase", "", "");
     * SharedPreferences.Editor editor = preferences.edit();
     * editor.putBoolean("subscribed", true);
     * <p>
     * editor = preferences.edit();
     * editor.putBoolean("really_subscribed", true);
     * <p>
     * <p>
     * <p>
     * editor.commit();
     * <p>
     * <p>
     * <p>
     * }
     * }
     * <p>
     * }
     * <p>
     * <p>
     * }
     * } catch (Exception e) {
     * int i = 1 ;
     * }
     * }
     * <p>
     * };
     * <p>
     * try {
     * executeServiceRequest(queryToExecute);
     * } catch (Exception e) {
     * }
     * <p>
     * }
     * <p>
     * <p>
     * <p>
     * private void executeServiceRequest(Runnable runnable) {
     * if (billingReady) {
     * runnable.run();
     * }
     * }
     **/

    private void checkForPostLaterPhotos() {
        KeptListAdapter dbHelper = KeptListAdapter.getInstance(this);
        Cursor cursor = dbHelper.fetchAllItems();

        try {


            if (cursor != null && cursor.getCount() > 0) {


                startActivity(new Intent(_this, KeptForLaterActivity.class));
                firstRun = false;
                finish();

            } else {

                boolean fromForegroundNotice = getIntent().getBooleanExtra("fromforegroundnotice", false);

                //   Intent i = new Intent(this, SettingsActivity2.class);
                //  i.putExtra("fromforegroundnotice", fromForegroundNotice);
                // startActivity(i);

                //  finish();

                showMainScreen();


                /**
                 boolean viewNotice001 = preferences.getBoolean("viewNotice001", false);

                 if (viewNotice001 == false && firstRun == false) {

                 Thread thread = new Thread() {

                @Override public void run() {

                // Block this thread for 2 seconds.
                try {
                Thread.sleep(1000);
                } catch (InterruptedException e) {
                }

                // After sleep finished blocking, create a Runnable to run on the UI Thread.
                runOnUiThread(new Runnable() {
                @Override public void run() {

                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("viewNotice001", true);
                editor.commit();
                Intent intent = new Intent(_this, WebViewActivity.class);
                intent.putExtra("url", "http://regrann.com/update_news");
                intent.putExtra("title", "Latest Regrann Information");
                intent.putExtra("help", true);
                startActivity(intent);

                Toast.makeText(_this, "Important Regrann Support News For You", Toast.LENGTH_LONG);

                }
                });

                }

                };

                 // Don't forget to start the thread.
                 thread.start();


                 }
                 **/


            }
        } catch (Exception e) {


        }


    }


    int numClicks = 0;

    private void showMainScreen() {

        setContentView(R.layout.activity_mainscreen);

        //     Toolbar myToolbar = findViewById(R.id.my_toolbar);
        //   setSupportActionBar(myToolbar);


    }


    private void emailSupport() {
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


        intent.putExtra(Intent.EXTRA_SUBJECT, "Need help with Regrann PRO");


        intent.setType("message/rfc822");

        Toast.makeText(_this, "Preparing email", Toast.LENGTH_LONG).show();
        startActivity(Intent.createChooser(intent, "Select Email Sending App :"));


        RegrannApp.sendEvent("main_email_support");


    }

    public void watchYoutubeVideo(String videoId) {

        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
        appIntent.putExtra("VIDEO_ID", videoId);


        try {
            startActivity(appIntent);


            RegrannApp.sendEvent("main_ytube_multi");


        } catch (ActivityNotFoundException ex) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + videoId));
            startActivity(webIntent);
        }
    }

    public void onClickHeaderView(View view) {
        numClicks++;

        if (numClicks == 4) {


            preferences = PreferenceManager.getDefaultSharedPreferences(_this.getApplication().getApplicationContext());

            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("subscribed", true);

            editor = preferences.edit();
            editor.putBoolean("really_subscribed", true);

            editor.commit();
            Toast.makeText(_this, "The Upgrade is Complete", Toast.LENGTH_LONG).show();

            noAds = true;


            findViewById(R.id.imgPatch).setVisibility(View.VISIBLE);


        }

    }


    public void onClickHowToMulti(View view) {


        watchYoutubeVideo(preferences.getString("multipost_videoid", "gqivL9EdEmc"));

    }

    public void onClickTestPurchase(View view) {


        Intent i = new Intent(_this, RequestPaymentActivity.class);


        startActivity(i);
    }


    public void onClickOpenSettings(View view) {
        RegrannApp.sendEvent("rmain_settings_btn");
        // TODO Auto-generated method stub


        startActivity(new Intent(this, SettingsActivity2.class));

    }


    private void checkForInstagramURLinClipboard() {


        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clipData = clipboard != null ? clipboard.getPrimaryClip() : null;

        if (clipData != null) {

            try {
                final ClipData.Item item = clipData.getItemAt(0);
                String text = item.coerceToText(RegrannMainActivity.this).toString();
                ClipData clip = ClipData.newPlainText("message", "");
                clipboard.setPrimaryClip(clip);

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
                        return;

                    }

                }


            } catch (Exception e) {
            }

            // did we come from the post later screen
            if (this.getIntent().hasExtra("show_home")) showMainScreen();
            else checkForPostLaterPhotos();
        }


    }


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)

            checkForInstagramURLinClipboard();
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm != null ? tm.getSimCountryIso() : null;
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }


}
