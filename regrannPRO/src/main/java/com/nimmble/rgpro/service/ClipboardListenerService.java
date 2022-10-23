package com.nimmble.rgpro.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.FileObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.nimmble.rgpro.R;
import com.nimmble.rgpro.activity.RegrannApp;
import com.nimmble.rgpro.activity.SettingsActivityForeground;
import com.nimmble.rgpro.activity.ShareActivity;

public class ClipboardListenerService extends Service {
    private static final String TAG = "ClipboardListenerService";
    private FileObserver fileObserver;
    private String imgDirPath;
    public static final String MY_SERVICE = "com.nimmble.rgpro.service.ClipboardListenerService";
    private ClipboardManager cliboardManager;
    String currentApp = "";

    private static OnPrimaryClipChangedListener clipListener = null;
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private static ClipboardListenerService _this;

    private SharedPreferences preferences;

    // private static final String
    // CAPTURED_IMAGE_DIR_PATH="/sdcard/CapturedImages";
    // //(/Pictures/Screenshots) (ICS 4.0)
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();


        _this = this;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P)
            return ;


        //     if (preferences.getBoolean("foreground_checkbox", false))
        //         runAsForeground();


   //     ClipboardListenerService.mInterstitialAd = new InterstitialAd(_this);
   //     ClipboardListenerService.mInstagramBtnInterstitial = new InterstitialAd(_this);


    //    ClipboardListenerService.mInterstitialAd.setAdUnitId("ca-app-pub-1489694459182078/9486165149");  // LIVE
     //   ClipboardListenerService.mInstagramBtnInterstitial.setAdUnitId("ca-app-pub-1489694459182078/7378159945");   // LIVE ONE




        //  ensureServiceStaysRunning();
        Log.d("app5", "IN CREATE CLipboard service");

        //	Mint.initAndStartSession(ClipboardListenerService.this, "bf4c34e3");

        final ClipboardManager cliboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        clipListener = new OnPrimaryClipChangedListener() {

            @Override
            public void onPrimaryClipChanged() {
                try {
                    Log.d("app5", "IN CLipboard service before get data");
                    ClipData clipData = cliboardManager != null ? cliboardManager.getPrimaryClip() : null;


                    final ClipData.Item item = clipData.getItemAt(0);
                    final String text = item.coerceToText(ClipboardListenerService.this).toString();

                    if (mPreviousText.equals(text)) {
                        mPreviousText = "";

                        return;
                    } else {

                        mPreviousText = text;
                    }

                    System.out.println("***starting handler: ");

                    Handler h = new Handler(_this.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if (text.length() > 18) {
                                    Log.d("regrann", clipListener.toString());
                                    if (text.contains("instagram.com")) {


                                        Intent i;


                                        i = new Intent(_this, ShareActivity.class);


// String uri=imgDirPath+"/"+path;
                                        i.putExtra("mediaUrl", text);

                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


                                        //    i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        i.putExtra("isJPEG", "no");
                                        System.out.println("***media url " + text);


                                        startActivity(i);

                                    }
                                    mPreviousText = "";
                                }


                            } catch (Exception e) {
                            }
                        }
                    });
                } catch (Exception e) {
                }

            }
        };

        assert cliboardManager != null;
        cliboardManager.addPrimaryClipChangedListener(clipListener);
        Log.d("regrann", clipListener.toString());

    }

    private static String mPreviousText = "";

    private void ensureServiceStaysRunning() {
        // KitKat appears to have (in some cases) forgotten how to honor START_STICKY
        // and if the service is killed, it doesn't restart.  On an emulator & AOSP device, it restarts...
        // on my CM device, it does not - WTF?  So, we'll make sure it gets back
        // up and running in a minimum of 20 minutes.  We reset our timer on a handler every
        // 2 minutes...but since the handler runs on uptime vs. the alarm which is on realtime,
        // it is entirely possible that the alarm doesn't get reset.  So - we make it a noop,
        // but this will still count against the app as a wakelock when it triggers.  Oh well,
        // it should never cause a device wakeup.  We're also at SDK 19 preferred, so the alarm
        // mgr set algorithm is better on memory consumption which is good.
        //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        //{
        // A restart intent - this never changes...
        final int restartAlarmInterval = 20 * 60 * 1000;
        final int resetAlarmTimer = 60 * 1000;
        final Intent restartIntent = new Intent(this, ClipboardListenerService.class);
        restartIntent.putExtra("ALARM_RESTART_SERVICE_DIED", true);
        final AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Log.d("tag", "starting handler...");
        // Create a pending intent
        Handler restartServiceHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // Create a pending intent
                PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, restartIntent, 0);
                alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + restartAlarmInterval, pintent);
                sendEmptyMessageDelayed(0, resetAlarmTimer);
            }
        };
        restartServiceHandler.sendEmptyMessageDelayed(0, 0);
        // }
    }


    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Placeholder - Please Disable";
            String description = "Placeholder - Please Disable";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {




        preferences = PreferenceManager.getDefaultSharedPreferences(_this.getApplication()
                .getApplicationContext());

        Log.d("tag", "foregrund_check" + preferences.getBoolean("foreground_checkbox", true));


        Log.d("tag", "enable foreground");

     //   if (preferences.getBoolean("foreground_checkbox", true)) {


        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent notificationIntent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, RegrannApp._this.getPackageName())
                        .putExtra(Settings.EXTRA_CHANNEL_ID, NOTIFICATION_CHANNEL_ID);


                //  Intent notificationIntent = new Intent(this, SettingsActivityForeground.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                notificationIntent.putExtra("fromforegroundnotice", true);

                createNotificationChannel();
                Log.d("tag", "enable foreground 2");
                Notification.Builder builder = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)

                        .setContentTitle("Useless Notification")
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.icon_notification2)
                        .setCategory(Notification.CATEGORY_SERVICE)
                        .setContentText("Tap to disable. You don't need this.");


                Notification notification = builder.build();
                startForeground(2, notification);


            } else {

                Intent notificationIntent = new Intent();
                notificationIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");

//for Android 5-7
                notificationIntent.putExtra("app_package", getPackageName());
                notificationIntent.putExtra("app_uid", getApplicationInfo().uid);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                //  Intent notificationIntent = new Intent(this, SettingsActivityForeground.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                Notification notification = new Notification.Builder(this)
                        .setSmallIcon(R.drawable.icon_notification2)
                        .setContentTitle("Useless Notification")
                        .setOngoing(true)
                        .setOnlyAlertOnce(true)
                        .setContentIntent(pendingIntent)
                        .setSound(null)
                        .setContentText("Tap to block. You don't need this.").build();


                startForeground(1, notification);
            }
            //  }

        } catch (Exception e){}

        // Do your other onStartCommand stuff..
        return START_STICKY;
    }

    /**
     * @Override public void onTaskRemoved(Intent rootIntent) {
     * // TODO Auto-generated method stub
     * Intent restartService = new Intent(getApplicationContext(),
     * this.getClass());
     * restartService.setPackage(getPackageName());
     * PendingIntent restartServicePI = PendingIntent.getService(
     * getApplicationContext(), 1, restartService,
     * PendingIntent.FLAG_ONE_SHOT);
     * AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
     * alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
     * <p>
     * }
     **/

    private void runAsForeground() {
        Intent notificationIntent = new Intent(this, SettingsActivityForeground.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationIntent.putExtra("fromforegroundnotice", true);
        boolean isSchedulingActive = false;

        isSchedulingActive = preferences.getBoolean("scheduling_active", false);

        int resid = 0;
        if (isSchedulingActive) {
            resid = R.string.foreground_notification_scheduling;

        } else
            resid = R.string.foreground_notification;


        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_notification2)
                .setContentTitle("Regrann")
                .setOngoing(true)
                .setContentText(getString(resid))
                .setContentIntent(pendingIntent).build();


        startForeground(5321, notification);

    }


}
