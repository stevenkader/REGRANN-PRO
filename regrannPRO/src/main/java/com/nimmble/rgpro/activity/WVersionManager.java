package com.nimmble.rgpro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;

public class WVersionManager implements IWVersionManager {
    private static final String TAG = "WVersionManager";

    private static final int MODE_CHECK_VERSION = 100;
    private static final int MODE_ASK_FOR_RATE = 200;


    private CustomTagHandler customTagHandler;

    private final String PREF_IGNORE_VERSION_CODE = "w.ignore.version.code";
    private final String PREF_REMINDER_TIME = "w.reminder.time";

    private final Activity activity;
    private Drawable icon;
    private String title;
    private String message;
    private String updateNowLabel;
    private String remindMeLaterLabel;
    private String ignoreThisVersionLabel;
    private String updateUrl = null;
    private String versionContentUrl;
    private int reminderTimer;
    private int versionCode;
    private final AlertDialogButtonListener listener;
    private boolean mDialogCancelable = true;
    private final boolean mIsAskForRate = false;
    private String mAskForRatePositiveLabel;
    private String mAskForRateNegativeLabel;
    private int mMode = 100; // default mode

    public WVersionManager(Activity act) {
        this.activity = act;
        this.listener = new AlertDialogButtonListener();
        this.customTagHandler = new CustomTagHandler();
        updateUrl = "https://regrann.com/updatepro";
    }

    private Drawable getDefaultAppIcon() {
        return activity.getApplicationInfo().loadIcon(activity.getPackageManager());
    }

    public void checkVersion() {
        mMode = MODE_CHECK_VERSION;
        String versionContentUrl = getVersionContentUrl();
        if (versionContentUrl == null) {
            Log.e(TAG, "Please set versionContentUrl first");
            return;
        }

        Calendar c = Calendar.getInstance();
        long currentTimeStamp = c.getTimeInMillis();
        long reminderTimeStamp = getReminderTime();


        if ( currentTimeStamp > reminderTimeStamp) {
            // fire request to get update version content

            VersionContentRequest request = new VersionContentRequest(activity);
            request.execute(getVersionContentUrl());
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setIcon(getIcon());
        builder.setTitle(getTitle());
        builder.setMessage(Html.fromHtml(getMessage(), null, getCustomTagHandler()));

        switch (mMode) {
            case MODE_CHECK_VERSION:
                builder.setPositiveButton(getUpdateNowLabel(), listener);
             //   builder.setNeutralButton(getRemindMeLaterLabel(), listener);
                //	builder.setNegativeButton(getIgnoreThisVersionLabel(), listener);
                break;
            case MODE_ASK_FOR_RATE:
                builder.setPositiveButton(getAskForRatePositiveLabel(), listener);
                builder.setNegativeButton(getAskForRateNegativeLabel(), listener);
                break;
            default:
                return;
        }

        builder.setCancelable(isDialogCancelable());


        AlertDialog dialog = builder.create();
        if (activity != null && !activity.isFinishing()) {
            dialog.show();
        }
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getUpdateNowLabel()
     */
    @Override
    public String getUpdateNowLabel() {
        return updateNowLabel != null ? updateNowLabel : "Update now";
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setUpdateNowLabel(java.lang.String)
     */
    @Override
    public void setUpdateNowLabel(String updateNowLabel) {
        this.updateNowLabel = updateNowLabel;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getRemindMeLaterLabel()
     */
    @Override
    public String getRemindMeLaterLabel() {
        return remindMeLaterLabel != null ? remindMeLaterLabel : "Remind me later";
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setRemindMeLaterLabel(java.lang.String)
     */
    @Override
    public void setRemindMeLaterLabel(String remindMeLaterLabel) {
        this.remindMeLaterLabel = remindMeLaterLabel;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getIgnoreThisVersionLabel()
     */
    @Override
    public String getIgnoreThisVersionLabel() {
        return ignoreThisVersionLabel != null ? ignoreThisVersionLabel : "Ignore this version";
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setIgnoreThisVersionLabel(java.lang.String)
     */
    @Override
    public void setIgnoreThisVersionLabel(String ignoreThisVersionLabel) {
        this.ignoreThisVersionLabel = ignoreThisVersionLabel;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setIcon(android.graphics.drawable.Drawable)
     */
    @Override
    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setTitle(java.lang.String)
     */
    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setMessage(java.lang.String)
     */
    @Override
    public void setMessage(String message) {
        this.message = message;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getMessage()
     */
    @Override
    public String getMessage() {
        String defaultMessage = null;
        switch (mMode) {
            case MODE_CHECK_VERSION:
                defaultMessage = "What's new in this version";
                break;
            case MODE_ASK_FOR_RATE:
                defaultMessage = "Please rate us!";
                break;
        }

        return message != null ? message : defaultMessage;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getTitle()
     */
    @Override
    public String getTitle() {
        String defaultTitle = null;
        switch (mMode) {
            case MODE_CHECK_VERSION:
                defaultTitle = "New Update Available";
                break;
            case MODE_ASK_FOR_RATE:
                defaultTitle = "Rate this app";
                break;
        }
        return title != null ? title : defaultTitle;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getIcon()
     */
    @Override
    public Drawable getIcon() {
        return icon != null ? icon : getDefaultAppIcon();
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getUpdateUrl()
     */
    @Override
    public String getUpdateUrl() {
        return updateUrl != null ? updateUrl : getGooglePlayStoreUrl();
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setUpdateUrl(java.lang.String)
     */
    @Override
    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getVersionContentUrl()
     */
    @Override
    public String getVersionContentUrl() {
        return versionContentUrl;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setVersionContentUrl(java.lang.String)
     */
    @Override
    public void setVersionContentUrl(String versionContentUrl) {
        this.versionContentUrl = versionContentUrl;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setVersionContentUrl(java.lang.String)
     */
    @Override
    public int getReminderTimer() {
        return reminderTimer > 0 ? reminderTimer : (60); // default value 60 minutes
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setReminderTimer(int)
     */
    @Override
    public void setReminderTimer(int minutes) {
        if (minutes > 0) {
            reminderTimer = minutes;
        }
    }

    private void updateNow(String url) {
        if (url != null) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                activity.startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "is update url correct?" + e);
            }
        }

    }


    private void remindMeLater(int reminderTimer) {
        Calendar c = Calendar.getInstance();
        long currentTimeStamp = c.getTimeInMillis();

        c.add(Calendar.MINUTE, reminderTimer);
        long reminderTimeStamp = c.getTimeInMillis();


        setReminderTime(reminderTimeStamp);
    }

    private void setReminderTime(long reminderTimeStamp) {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putLong(PREF_REMINDER_TIME, reminderTimeStamp).commit();
    }

    private long getReminderTime() {
        return PreferenceManager.getDefaultSharedPreferences(activity).getLong(PREF_REMINDER_TIME, 0);
    }

    private void ignoreThisVersion() {
        PreferenceManager.getDefaultSharedPreferences(activity).edit().putInt(PREF_IGNORE_VERSION_CODE, versionCode).commit();
    }

    private String getGooglePlayStoreUrl() {
        String id = activity.getApplicationInfo().packageName; // current google play is using package name as id
        return "market://details?id=" + id;
    }

    private class AlertDialogButtonListener implements DialogInterface.OnClickListener {

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:
                    updateNow(getUpdateUrl());
                    break;
                case AlertDialog.BUTTON_NEUTRAL:
                    remindMeLater(getReminderTimer());
                    break;
                case AlertDialog.BUTTON_NEGATIVE:
                    ignoreThisVersion();
                    break;
            }
        }
    }

    class VersionContentRequest extends AsyncTask<String, Void, String> {
        Context context;
        int statusCode;

        public VersionContentRequest(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... uri) {



            String responseBody="";



            try {
                InputStream inputStream = null;

                URL url = new URL(uri[0]);
                URLConnection c = url.openConnection();


                    c.connect();

                BufferedReader reader = null;
                StringBuilder builder = new StringBuilder();
                try {
                    InputStreamReader i = new InputStreamReader(c.getInputStream());
                    reader = new BufferedReader(i);

                    for (String line; (line = reader.readLine()) != null; ) {

                            builder.append(line.trim());

                        // builder.append(line.trim());
                    }
                } finally {
                    if (reader != null)
                        try {
                            reader.close();
                        } catch (IOException logOrIgnore) {
                        }
                }

                 responseBody = builder.toString();






            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
            return responseBody;
        }

        @Override
        protected void onPostExecute(String result) {
            versionCode = 0;
            String content;

                try {
                    if (!result.startsWith("{")) { // for response who append with unknown char
                        result = result.substring(1);
                    }
                    // json format from server:
                    JSONObject json = (JSONObject) new JSONTokener(result).nextValue();
                    versionCode = json.optInt("version_code");
                    content = json.optString("content");

                    int currentVersionCode = getCurrentVersionCode();

                //    currentVersionCode = 0 ;

                    if (currentVersionCode < versionCode) {
                        // new versionCode will always higher than currentVersionCode
                        if (versionCode != getIgnoreVersionCode()) { // check is new versionCode is ignore version
                            // set dialog message
                            setMessage(content);

                            ShareActivity.updateScreenOn = true ;
                            // show update dialog
                            showDialog();
                        }
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "is your server response have valid json format?");
                    Log.e(TAG, e.toString());
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }

        }
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getCurrentVersionCode()
     */
    @Override
    public int getCurrentVersionCode() {
        int currentVersionCode = 0;
        PackageInfo pInfo;
        try {
            pInfo = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0);
            currentVersionCode = pInfo.versionCode;
        } catch (NameNotFoundException e) {
            // return 0
        }
        return currentVersionCode;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getIgnoreVersionCode()
     */
    @Override
    public int getIgnoreVersionCode() {
        return PreferenceManager.getDefaultSharedPreferences(activity).getInt(PREF_IGNORE_VERSION_CODE, 1);
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#getCustomTagHandler()
     */
    @Override
    public CustomTagHandler getCustomTagHandler() {
        return customTagHandler;
    }

    /* (non-Javadoc)
     * @see com.winsontan520.wversionmanagertest.IWVersionManager#setCustomTagHandler(com.winsontan520.wversionmanagertest.WVersionManager.CustomTagHandler)
     */
    @Override
    public void setCustomTagHandler(CustomTagHandler customTagHandler) {
        this.customTagHandler = customTagHandler;
    }

    public boolean isDialogCancelable() {
        return mDialogCancelable;
    }

    public void setDialogCancelable(boolean dialogCancelable) {
        mDialogCancelable = dialogCancelable;
    }

    public void askForRate() {
        mMode = MODE_ASK_FOR_RATE;
        showDialog();
    }

    public String getAskForRatePositiveLabel() {
        return mAskForRatePositiveLabel == null ? "OK" : mAskForRatePositiveLabel;
    }

    public void setAskForRatePositiveLabel(String askForRatePositiveLabel) {
        mAskForRatePositiveLabel = askForRatePositiveLabel;
    }

    public String getAskForRateNegativeLabel() {
        return mAskForRateNegativeLabel == null ? "Not now" : mAskForRateNegativeLabel;
    }

    public void setAskForRateNegativeLabel(String askForRateNegativeLabel) {
        mAskForRateNegativeLabel = askForRateNegativeLabel;
    }

}
