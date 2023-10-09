package com.nimmble.rgpro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import androidx.core.app.ShareCompat;
import androidx.core.content.FileProvider;

import com.nimmble.rgpro.R;
import com.nimmble.rgpro.util.Util;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.util.Objects;


public class PostFromKeptActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
    private ImageView settings, btnEmail, btndownloadphoto, btnTweet, btnFacebook, btnSMS, btnSetting, btnInviteFriends, btnInstagram, btnShare, btnShareAppFB, btnShareAppTW, btnShareAppGooglePlus;
    private SeekBar seekBarOpacity;
    private LinearLayout screen_ui, full_ui;
    private RelativeLayout shareLayout;
    private RelativeLayout buttonLayout;
    private Button btnSupport;

    private ProgressBar spinner;

    public static final int ACTION_SMS_SEND = 0;
    public static final int ACTION_TWEET_SEND = 1;
    public static final int ACTION_FACEBOOK_POST = 2;
    private static final int PLUS_ONE_REQUEST_CODE = 0;

    ImageView previewImage;
    Drawable backgroundDrawable;
    Uri uri;
    PostFromKeptActivity _this = this;
    JSONObject jsonInstagramDetails;
    SharedPreferences sharedPref;
    ProgressDialog pd;
    AlertDialog rateRequestDialog;


    private String uriStr;
    private final String mTinyUrl = null;
    static String url, title, author;
    File tempVideoFile, tempFile, tmpVideoFile;
    boolean isVideo = false, photoReady = false, optionHasBeenClicked = false, isJPEG = false, autopost, autosave, supressToast = false;
    String tempFileFullPathName, tempVideoName = "temp/tmpvideo.mp4", tempFileName, selectedImagePath = null, regrannPictureFolder, internalPath;
    int count, buttonWidth;

    // The request code must be 0 or greater.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File folderDst = null;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        try {
            if (action != null) {

                if (action.equals("com.jaredco.action.fromkept")) {
                    tempFileFullPathName = intent.getStringExtra("photo");
                    author = intent.getStringExtra("author");
                    title = intent.getStringExtra("title");
                    tempVideoName = intent.getStringExtra("videoURL");
                    int isScheduled = intent.getIntExtra("isScheduled", 0);
                    long scheduledTime = intent.getLongExtra("scheduledTime", 0);
                    if (tempVideoName != null && !tempVideoName.equals(""))
                        isVideo = true;


                    // Copy photo back to /temp

                    try {
                        folderDst = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + Util.RootDirectoryPhoto + "/" + new File(tempFileFullPathName).getName());


                        File src = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES) + "/regrann_postlater/" + new File(tempFileFullPathName).getName());


                        FileUtils.copyFile(src, folderDst);

                        if (isVideo) {
                            File videoDst = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + Util.RootDirectoryPhoto + "/" + new File(tempVideoName).getName());


                            File videoSrc = new File(Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_PICTURES) + "/regrann_postlater/" + new File(tempVideoName).getName());


                            FileUtils.copyFile(videoSrc, videoDst);
                        }


                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }


            photoReady = false;
            Context serviceCtx = this;
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_this.getApplication().getApplicationContext());


            tempFile = folderDst;  //new File(tempFileFullPathName);


            if (isVideo)
                tempVideoFile = new File(tempVideoName);

            photoReady = true;

            onClick(btnInstagram);
        } catch (Exception e) {
            showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.");

        }

        //  sendDirect();

    }


    protected void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time the activity receives
        // focus.
    }

    private void sendEvent(String cat, String action, String label) {
        // Get tracker.
        //	Tracker t = ((RegrannApp) PostFromKeptActivity.this.getApplication()).getTracker(TrackerName.APP_TRACKER);
        // Build and send an Event.
        //	t.send(new HitBuilders.EventBuilder().setCategory(cat).setAction(action).setLabel(label).build());

    }


    private void showErrorToast(final String error, final String displayMsg) {
        showErrorToast(error, displayMsg, false);
    }

    private void showErrorToast(final String error, final String displayMsg, final boolean doFinish) {

        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    sendEvent("Error Dialog", displayMsg, error);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PostFromKeptActivity.this);

                    // set dialog message
                    alertDialogBuilder.setMessage(displayMsg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            // if (doFinish)
                            // finish();
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


    @Override
    public void onClick(View v) {
        Uri MediaURI = null;
        try {

            if (v == btnInstagram) {

                sendEvent("button", "click", "Instagram");

                try {
                    String caption = Util.prepareCaption(title, author, _this.getApplication().getApplicationContext(), "#regrann", false);

                    // flurryAgent.logEvent("Instagram button pressed");
                    // FlurryAgent.logEvent("Click Instagram");

                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setPackage("com.instagram.android");
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    shareIntent.setClassName(
                            "com.instagram.android",
                            "com.instagram.share.handleractivity.ShareHandlerActivity");


                    shareIntent.putExtra(Intent.EXTRA_TEXT, caption);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("Post caption", caption);
                    Objects.requireNonNull(clipboard).setPrimaryClip(clip);





                    if (isVideo) {
                        shareIntent.setType("video/*");

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempVideoFile);
                        } else {
                            MediaURI = Uri.fromFile(tempVideoFile);
                        }


                    } else {
                        Log.d("app5", "tempfile :  " + tempFile.toString());


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            MediaURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", tempFile);
                        } else {
                            MediaURI = Uri.fromFile(tempFile);
                        }


                        shareIntent.setType("image/jpeg");
                    }
                    shareIntent.putExtra(Intent.EXTRA_STREAM, MediaURI);


                    String type = "image/*";

                    createInstagramIntent(type, MediaURI);

                    //  shareWithInstagramChooser(MediaURI);

                    //   startActivity(shareIntent);

                    //    finish();

                } catch (Exception e) {
                    shareWithInstagramChooser(MediaURI);
                    //     showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.");

                }
                // FlurryAgent.onEndSession(serviceCtx);
            }

        } catch (Exception e) {
            showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.");

        }

    }


    private void shareWithInstagramChooser(Uri MediaURI) {


        try {
            // flurryAgent.logEvent("Share button pressed");
            // Create the new Intent using the 'Send' action.
            Intent share = new Intent(Intent.ACTION_SEND);
            share.setPackage("com.instagram.android");
            share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);

            Intent i = ShareCompat.IntentBuilder.from(PostFromKeptActivity.this)
                    .setText("Share to")

                    .setStream(MediaURI)

                    .getIntent()
                    .setPackage("com.instagram.android");


            if (isVideo) {
                i.setType("video/*");

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
                    KeptForLaterActivity._this.removeCurrentPhoto();
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
}
