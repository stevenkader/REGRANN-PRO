package com.nimmble.rgpro.activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;

//import com.google.ads.mediation.inmobi.InMobiConsent;

public class RegrannApp extends Application {
//    public class RegrannApp extends Application  {


    // The following line should be changed to include the correct property id.

    private static FirebaseAnalytics mFirebaseAnalytics;
    public static RegrannApp _this;
    public static boolean admobReady = false ;
    SharedPreferences preferences;

    //try to catch some uncaught exception
    public static boolean crashInterceptor(Thread thread, Throwable throwable) {

        if (throwable == null || thread.getId() == 1) {
            //Don't intercept the Exception of Main Thread.
            return false;
        }

        String classpath = null;
        if (throwable.getStackTrace() != null && throwable.getStackTrace().length > 0) {
            classpath = throwable.getStackTrace()[0].toString();
        }

//intercept GMS Exception
        return classpath != null
                && throwable.getMessage().contains("Results have already been set")
                && classpath.contains("com.google.android.gms");

    }

    public static void sendEvent(String cat) {
        Log.i("app5", cat) ;
        try {
            mFirebaseAnalytics.logEvent(cat, null);
        }catch (Exception e){}

    }

    public static void sendEvent(String cat, String action, String label) {

        cat = cat.replace('-', '_');
        cat = cat.replace(' ', '_');

        action = action.replace('-', '_');
        action = action.replace(' ', '_');

        label = label.replace('-', '_');
        label = label.replace(' ', '_');

        String evenTxt = cat.replaceAll("\\s+", "") + action.replaceAll("\\s+", "") + label.replaceAll("\\s+", "");

        if (evenTxt.length() > 32)
            evenTxt = evenTxt.substring(0, 31);

        Log.d("app5", evenTxt);
        mFirebaseAnalytics.logEvent(evenTxt, null);


    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


    @Override
    public void onCreate() {
        super.onCreate();


        Log.d("app5", "In Regrann App - onCreate");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstRun = preferences.getBoolean("startShowTutorial", true);


        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        _this = this;


        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {

                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("app5", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast

                        Log.d("app5", "Token :    " + token);
                    }
                });


        preferences = PreferenceManager.getDefaultSharedPreferences(_this);


        final Thread.UncaughtExceptionHandler oldHandler =
                Thread.getDefaultUncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(
                new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(
                            Thread paramThread,
                            Throwable paramThrowable
                    ) {
                        //Do your own error handling here

                        if (crashInterceptor(paramThread, paramThrowable))
                            return;


                        if (oldHandler != null)
                            oldHandler.uncaughtException(
                                    paramThread,
                                    paramThrowable
                            ); //Delegates to Android's error handling
                        else
                            System.exit(2); //Prevents the service/app from freezing
                    }
                });


    }





       public int getBannerPlacementId() {
           int bannerPlacementId = -1;
           return bannerPlacementId;
       }

}
