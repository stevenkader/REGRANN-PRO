package com.nimmble.rgpro.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.nimmble.rgpro.R;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
//public class SettingsActivity2 extends AppCompatPreferenceActivity  implements IabBroadcastReceiver.IabBroadcastListener {

public class SettingsActivity2 extends PreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */


    static SettingsActivity2 _this;
    static boolean fromForegroundNotice = false;
    private SimpleCursorAdapter dataAdapter;

    static final String TAG = "TAG";

    private AppCompatDelegate mDelegate;

    private static SharedPreferences preferences;
    private static SharedPreferences.Editor editor;

    // Does the user have an active subscription to the infinite gas plan?
    boolean mSubscribedNoCaptionChange = false;

    // Will the subscription auto-renew?
    boolean mAutoRenewEnabled = false;

    // Tracks the currently owned infinite gas SKU, and the options in the Manage dialog
    String mInfiniteGasSku = "";
    String mFirstChoiceSku = "";
    String mSecondChoiceSku = "";

    // Used to select between purchasing gas on a monthly or yearly basis
    String mSelectedSubscriptionPeriod = "";

    // SKUs for our products: the premium upgrade (non-consumable) and gas (consumable)
    static final String SKU_PREMIUM = "premium";
    static final String SKU_GAS = "";

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // SKU for our subscription (infinite gas)

    // The helper object
    //   IabHelper mHelper;

    // Provides purchase notification while this app is running
    // IabBroadcastReceiver mBroadcastReceiver;

    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;


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
        //    if (!addPermission(permissionsList, Manifest.permission.RECEIVE_SMS))
        //       permissionsNeeded.add("Read SMS");
        //   if (!addPermission(permissionsList, android.Manifest.permission.READ_SMS))
        //      permissionsNeeded.add("Read SMS");


        if (permissionsNeeded.size() > 0) {

            //    Intent test = new Intent(this, CheckPermissions.class);
            //  startActivity(test);

            ActivityCompat.requestPermissions(_this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);


        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<String, Integer>();
            // Initial
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            //       perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
            //      perms.put(android.Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);


            if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            ) {


            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getDelegate().installViewFactory();
        getDelegate().onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);


        _this = this;

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(_this.getApplication().getApplicationContext());

        editor = preferences.edit();


        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();

        if (Build.VERSION.SDK_INT >= 23) {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    checkPermissions();
                }
            }, 1000);
        }


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getDelegate().onPostCreate(savedInstanceState);
    }

    public ActionBar getSupportActionBar() {
        return getDelegate().getSupportActionBar();
    }

    public void setSupportActionBar(Toolbar toolbar) {
        getDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getDelegate().setContentView(layoutResID);
    }

    @Override
    public void setContentView(View view) {
        getDelegate().setContentView(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().setContentView(view, params);
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getDelegate().addContentView(view, params);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDelegate().onPostResume();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getDelegate().setTitle(title);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getDelegate().onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getDelegate().onDestroy();

        // very important:
        //     if (mBroadcastReceiver != null) {
        //        unregisterReceiver(mBroadcastReceiver);
        //   }

        // very important:
        Log.d(TAG, "Destroying helper.");
        //     if (mHelper != null) {
        //  mHelper.disposeWhenFinished();
        //       mHelper = null;
        //  }
    }

    public void invalidateOptionsMenu() {
        getDelegate().invalidateOptionsMenu();
    }

    private AppCompatDelegate getDelegate() {
        if (mDelegate == null) {
            mDelegate = AppCompatDelegate.create(this, null);
        }
        return mDelegate;
    }


    private static final Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);


            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }


    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    public static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }


/**

 public void sendToInstagram() {


 try {


 Intent intent = getIntent();

 int itemID = getIntent().getExtras().getInt("itemid");
 String caption = intent.getStringExtra("caption");
 String fileName = intent.getStringExtra("fileName");
 String author = intent.getStringExtra("author");
 long scheduledTime =   intent.getLongExtra("scheduledTime", 0);
 String tempVideoName = intent.getStringExtra("videoURL");
 String tempFileFullPathName = intent.getStringExtra("fileName");



 File tempFile = new File(tempFileFullPathName);
 File tempVideoFile = null;

 // set caption to what is in the text box

 KeptListAdapter  dbHelper =  KeptListAdapter.getInstance(this);

 boolean isVideo = false;


 if (tempVideoName != null && !tempVideoName.equals(""))
 isVideo = true;


 if (isVideo)
 tempVideoFile = new File(tempVideoName);


 intent = this.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
 if (intent != null) {
 Intent shareIntent = new Intent();
 shareIntent.setAction(Intent.ACTION_SEND);
 shareIntent.setPackage("com.instagram.android");
 shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_NO_ANIMATION);
 //        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
 //     shareIntent.addFlags( Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT | Intent.FLAG_ACTIVITY_NO_ANIMATION);


 String finalCaption = Util.prepareCaption (caption, author, _this.getApplication().getApplicationContext());

 shareIntent.putExtra(Intent.EXTRA_TEXT, caption);
 ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
 ClipData clip = ClipData.newPlainText("Post caption", finalCaption);
 clipboard.setPrimaryClip(clip);

 if (isVideo) {
 shareIntent.setType("video/*");
 shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempVideoFile));
 } else {
 shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tempFile));
 shareIntent.setType("image/*");
 }

 startActivity(shareIntent);
 dbHelper.remove(itemID);//create removemethod in database class

 }
 } catch (Exception e) {
 //showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.");

 }

 }
 **/

    /**
     * @Override public void receivedBroadcast() {
     * // Received a broadcast notification that the inventory of items has changed
     * Log.d(TAG, "Received broadcast notification. Querying inventory.");
     * try {
     * mHelper.queryInventoryAsync(mGotInventoryListener);
     * } catch ( Exception e) {
     * <p>
     * complain("Error querying inventory. Another async operation in progress.");
     * }
     * }
     **/

    private final String inAppSku = "regrann_001";


// Listener that's called when we finish querying the items and subscriptions we own
/**
 IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {


 public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
 Log.d(TAG, "Query inventory finished.");

 // Have we been disposed of in the meantime? If so, quit.
 if (mHelper == null) return;

 // Is it a failure?
 if (result.isFailure()) {
 complain("Failed to query inventory: " + result);
 return;
 }

 Log.d(TAG, "Query inventory was successful.");



 // First find out which subscription is auto renewing
 Purchase subscriptionNoCaptionChange = inventory.getPurchase(SKU_INFINITE_GAS_MONTHLY);

 // The user is subscribed if either subscription exists, even if neither is auto
 // renewing
 mSubscribedNoCaptionChange = (subscriptionNoCaptionChange != null);
 Log.d(TAG, "User " + (mSubscribedNoCaptionChange ? "HAS" : "DOES NOT HAVE")
 + " infinite gas subscription.");




 }
 };
 **/


    // "Subscribe to infinite gas" button clicked. Explain to user, then start purchase
    // flow for subscription.
/**
 public void onSubscribeButtonClicked() {
 if (!mHelper.subscriptionsSupported()) {
 complain("Subscriptions not supported on your device yet. Sorry!");
 return;
 }
 String payload = "";



 mSelectedSubscriptionPeriod =   inAppSku;;


 List<String> oldSkus = null;
 if (!TextUtils.isEmpty(mInfiniteGasSku)
 && !mInfiniteGasSku.equals(mSelectedSubscriptionPeriod)) {
 // The user currently has a valid subscription, any purchase action is going to
 // replace that subscription
 oldSkus = new ArrayList<String>();
 oldSkus.add(mInfiniteGasSku);
 }

 setWaitScreen(true);
 Log.d(TAG, "Launching purchase flow for gas subscription.");
 try {
 mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, IabHelper.ITEM_TYPE_SUBS,
 oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload);
 } catch (Exception e) {
 complain("Error launching purchase flow. Another async operation in progress.");
 setWaitScreen(false);
 }
 // Reset the dialog options
 mSelectedSubscriptionPeriod = "";
 mFirstChoiceSku = "";
 mSecondChoiceSku = "";

 }
 **/

/**
 @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
 if (mHelper == null) return;

 // Pass on the activity result to the helper for handling
 if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
 // not handled, so handle it ourselves (here's where you'd
 // perform any handling of activity results not related to in-app
 // billing...
 super.onActivityResult(requestCode, resultCode, data);
 }
 else {
 Log.d(TAG, "onActivityResult handled by IABUtil.");
 }
 }
 **/
    /**
     * // Callback for when a purchase is finished
     * IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
     * public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
     * Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);
     * <p>
     * // if we were disposed of in the meantime, quit.
     * if (mHelper == null) return;
     * <p>
     * if (result.isFailure()) {
     * complain("Error purchasing: " + result);
     * setWaitScreen(false);
     * return;
     * }
     * <p>
     * <p>
     * <p>
     * <p>
     * if (purchase.getSku().equals(SKU_INFINITE_GAS_MONTHLY)  ) {
     * // bought the infinite gas subscription
     * Log.d(TAG, "Infinite gas subscription purchased.");
     * alert("Thank you for subscribing!");
     * mSubscribedNoCaptionChange = true;
     * mAutoRenewEnabled = purchase.isAutoRenewing();
     * mInfiniteGasSku = purchase.getSku();
     * <p>
     * setWaitScreen(false);
     * }
     * }
     * };
     **/


    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        //    findViewById(R.id.screen_main).setVisibility(set ? View.GONE : View.VISIBLE);
        //  findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    void complain(String message) {
        Log.e(TAG, "**** TrivialDrive Error: " + message);
        alert("Error: " + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    void saveData() {


    }

    void loadData() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        // mTank = sp.getInt("tank", 2);
        //Log.d(TAG, "Loaded data: tank = " + String.valueOf(mTank));
    }




    public static class PrefsFragment extends PreferenceFragment {



        CheckBoxPreference checkboxPref  ;

        CheckBoxPreference cbxCreditWatermark  ;


        @Override
        public void onResume() {

            super.onResume();

            boolean quickpost = preferences.getBoolean("quickpost", false);
            boolean quicksave = preferences.getBoolean("quicksave", false);
            boolean quickkeep = preferences.getBoolean("quickkeep", false);
            boolean normalMode = preferences.getBoolean("normalMode", true);


            if (quickpost)
                editor.putString("mode_string", getResources().getString(R.string.Quick_Repost_Mode));
            if (normalMode)
                editor.putString("mode_string", getResources().getString(R.string.normal_mode));
            if (quicksave)
                editor.putString("mode_string", getResources().getString(R.string.Quick_Save_Mode));
            if (quickkeep)
                editor.putString("mode_string", getResources().getString(R.string.Quick_PostLater_Mode));

            editor.apply();

            Preference pref = findPreference("mode_string");
            pref.setSummary(preferences.getString("mode_string", ""));

           checkboxPref = (CheckBoxPreference)findPreference("custom_watermark");

           cbxCreditWatermark = (CheckBoxPreference) findPreference("watermark_checkbox");


            cbxCreditWatermark.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(final Preference preference,
                                                  final Object newValue) {
                    if ((boolean) newValue) {
                        checkboxPref.setChecked(false);
                        editor.putBoolean("custom_watermark", false);

                        cbxCreditWatermark.setChecked(true);
                        editor.putBoolean("watermark_checkbox", true);
                        editor.commit();


                    } else
                    {

                        cbxCreditWatermark.setChecked(false);
                        editor.putBoolean("watermark_checkbox", false);
                        editor.commit();

                    }
                    return true ;


                }
            });

            checkboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                public boolean onPreferenceChange(Preference preference, Object newValue) {



                    if ((Boolean) newValue) {
                        // select photo
                        cbxCreditWatermark.setChecked(false);
                        editor.putBoolean("watermark_checkbox", false);
                        editor.commit();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);


                    }


                    else
                    {

                        checkboxPref.setChecked(false);
                        editor.putBoolean("custom_watermark", false);
                        editor.commit();

                    }
                    return true ;




                }
            });


        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource

            addPreferencesFromResource(R.xml.pref_general);


            Preference button = findPreference(getString(R.string.myCoolButton));
            button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //code for what you want it to do
                    Intent i = new Intent(_this, OnBoardingActivity.class);

                    startActivity(i);

                    return true;
                }
            });


            if (!fromForegroundNotice) {
                bindPreferenceSummaryToValue(findPreference("signature_type_list"));
                bindPreferenceSummaryToValue(findPreference("signature_text"));
                bindPreferenceSummaryToValue(findPreference("mode_string"));
                bindPreferenceSummaryToValue(findPreference("alpha_choice"));
            }


        }


        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
            // super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
            if (requestCode == 1) {
                if (resultCode == RESULT_OK) {


                    editor.putString("watermark_imagefile", getRealPathFromURI_API19(_this, imageReturnedIntent.getData()));
                    editor.apply();

                    RegrannApp.sendEvent("custom_watermark_uploaded");
                    //imageview.setImageURI(selectedImage);
                } else {
                    checkboxPref.setChecked(false);
                    editor.putBoolean("custom_watermark", false);

                }
            }
        }
    }


    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                // This is for checking Main Memory
                if ("primary".equalsIgnoreCase(type)) {
                    if (split.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                        return Environment.getExternalStorageDirectory() + "/";
                    }
                    // This is for checking SD Card
                } else {
                    return "storage" + "/" + docId.replace(":", "/");
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                String fileName = getFilePath(context, uri);
                if (fileName != null) {
                    return Environment.getExternalStorageDirectory().toString() + "/Download/" + fileName;
                }

                String id = DocumentsContract.getDocumentId(uri);
                if (id.startsWith("raw:")) {
                    id = id.replaceFirst("raw:", "");
                    File file = new File(id);
                    if (file.exists())
                        return id;
                }

                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static String getFilePath(Context context, Uri uri) {

        Cursor cursor = null;
        final String[] projection = {
                MediaStore.MediaColumns.DISPLAY_NAME
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, null, null,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**

    @SuppressLint("NewApi")
    public static String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();

        return filePath;
    }
    **/

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        // ActionBar actionBar = getSupportActionBar();

        //   if (actionBar != null) {
        // Show the Up button in the action bar.
        //      actionBar.setDisplayHomeAsUpEnabled(true);
        //  }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_help:



                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.regrann.com/support"));
                startActivity(browserIntent);

                return true;


            case android.R.id.home:
                finish();
                // NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }


}
