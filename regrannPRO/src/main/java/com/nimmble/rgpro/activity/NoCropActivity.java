package com.nimmble.rgpro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.nimmble.rgpro.util.Util;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//import com.google.android.gms.plus.PlusOneButton;

public class NoCropActivity extends Activity implements OnClickListener, OnSeekBarChangeListener {
    int buttonWidth;
    private ImageView settings, btnEmail, btndownloadphoto, btnTweet, btnFacebook, btnSMS, btnSetting, btnInviteFriends, btnInstagram, btnShare;
    private ImageView btnShareAppFB, btnShareAppTW, btnShareAppGooglePlus;
    private SeekBar seekBarOpacity;
    private String uriStr;
    ImageView previewImage;
    private Button btnSupport;

    private LinearLayout screen_ui, full_ui;

    private RelativeLayout shareLayout;
    private RelativeLayout buttonLayout;
    Drawable backgroundDrawable;
    Uri uri;
    boolean supressToast = false;
    NoCropActivity _this = this;
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
    File tempFile, tmpVideoFile;
    boolean photoReady = false;

    boolean optionHasBeenClicked = false;


    ProgressDialog pd;

    boolean isVideo = false;
    boolean isJPEG = false;

    String tempFileName;
    String tempVideoName = "temp/tmpvideo.mp4";
    File tempVideoFile;
    String tempFileFullPathName;
    String regrannPictureFolder;
    int count;
    private Context serviceCtx;
    AlertDialog rateRequestDialog;
    //  PlusOneButton mPlusOneButton = null;
    String autopost, autosave;
    String selectedImagePath = null;

    // The request code must be 0 or greater.
    private static final int PLUS_ONE_REQUEST_CODE = 0;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();

        // Get the directory for the user's public pictures directory.
        File file2 = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + Util.RootDirectoryPhoto);

        if (!file2.mkdirs()) {
            Log.e("error", "Directory not created");
        }

        // if this is from the share menu
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_STREAM)) {
                try {
                    // Get resource path
                    Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
                    //	String filename = parseUriToFilename(uri);

                    if (uri != null) {


                        String[] projection = {MediaStore.Images.Media.DATA};
                        Cursor cursor = managedQuery(uri, projection, null, null, null);

                        if (cursor != null) {
                            // Here you will get a null pointer if cursor is null
                            // This can be if you used OI file manager for picking the media
                            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            selectedImagePath = cursor.getString(column_index);
                        } else {
                            showErrorToast("Problem", "Repost only works with photos and videos.");
                            return;

                        }


                    }
                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.toString());
                }


            }

        }

        if (selectedImagePath == null) {
            showErrorToast("Problem", "Repost only works with photos and videos.");
            return;
        }


        // is this a video ?
        if (selectedImagePath.indexOf("mp4") > 0) {

            sendEvent("autocrop", "video", "");

            try {

                sendToRegrannShareScreen(MEDIA_VIDEO);


            } catch (Exception e) {
                showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.");

            }


        } else {

            photoReady = false;

            tempFileName = "temp_regrann_" + System.currentTimeMillis() + ".jpg";
            tempVideoName = "temp_regrann_" + System.currentTimeMillis() + ".mp4";

            try {
                //   regrannPictureFolder = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + Util.RootDirectoryPhoto).getAbsolutePath();

                //  regrannPictureFolder = getAlbumStorageDir("Regrann").getAbsolutePath();

                File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + Util.RootDirectoryPhoto);


                tempFileFullPathName = file + File.separator + tempFileName;

                tempFile = new File(tempFileFullPathName);

                final Intent iv = getIntent();

                try {

                    String fileName = selectedImagePath; //
                    // String result = GET(t);
                    new HandleLocalFile().execute(fileName);

                } catch (Exception e) {
                    showErrorToast(e.getMessage(), "Sorry. There was a problem finding that photo. Please try again later.", true);

                }


            } catch (Exception e) {
                showErrorToast(e.getMessage(), "Sorry. There was a problem. Please close the app and try again.", true);

            }
        }

    }


    String getExtensionFromPath(String imagePath) {
        return imagePath.substring((imagePath.lastIndexOf(".") + 1));
    }


    private void sendToRegrannShareScreen(int mediaType) {
        try {
            Intent shareIntent = new Intent();

            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            shareIntent = new Intent(_this, ShareActivity.class);


            shareIntent.putExtra("fromNoCrop", true);


            shareIntent.putExtra("mediaType", mediaType);

            shareIntent.putExtra("fileName", selectedImagePath);



            startActivity(shareIntent);
            finish();
        } catch (Exception e) {
            showErrorToast(e.getMessage(), "Sorry. There was a problem. Please close the app and try again.", true);

        }

    }

    protected void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives
        // focus.

    }

    private void sendEvent(String cat, String action, String label) {
        // Get tracker.
        //      Tracker t = ((RegrannApp) NoCropActivity.this.getApplication()).getTracker(TrackerName.APP_TRACKER);
        // Build and send an Event.
        //     t.send(new HitBuilders.EventBuilder().setCategory(cat).setAction(action).setLabel(label).build());

    }


    private class HandleLocalFile extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {

            try {

                //    URL imageurl = new URL("file://" + urls[0]);
                Bitmap src = lessResolution(urls[0]);


                Bitmap source = generateNoCroppedSquareBitmap(src);


                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                source.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                // write the bytes in file
                FileOutputStream fo = new FileOutputStream(tempFile);
                selectedImagePath = tempFile.getPath();
                fo.write(bytes.toByteArray());

                // remember close de FileOutput
                fo.close();

                source.recycle();

            } catch (Exception e) {
                showErrorToast(e.getMessage(), "Sorry. There was a problem finding that photo. Please try again later.", true);

                return "error";
            }

            return "ok";
        }


        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            try {

                if (result.compareTo("error") != 0) {

                    try {
                        photoReady = true;

                        onClick(btnInstagram);

                    } catch (Exception e) {
                        showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.", true);

                    }

                }

                // getResponse.setText(result);
            } catch (Exception e) {
                showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.", true);

            }


        }
    }


    public static Bitmap lessResolution(String filePath) {
        try {

            int reqHeight = 640;
            int reqWidth = 640;


            BitmapFactory.Options options = new BitmapFactory.Options();

            // First decode with inJustDecodeBounds=true to check dimensions
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);

            if (options.outHeight >= 1080 || options.outWidth >= 1080) {
                reqHeight = 1080;
                reqWidth = 1080;
            }


            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;

            return BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            int i = 1;
        }
        return null;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }


    public static Bitmap generateNoCroppedSquareBitmap(Bitmap source) {


        //      Bitmap source = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());

        // may need to make square
        Bitmap background;
        Canvas canvas;
        if (source.getHeight() != source.getWidth()) {
            // create a new Bitmap with the bigger side (to get a
            // square)
            if (source.getHeight() > source.getWidth()) {
                background = Bitmap.createBitmap(source.getHeight(), source.getHeight(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(background);
                Paint paint2 = new Paint();
                canvas.drawColor(Color.WHITE);
                // draw the source image centered
                canvas.drawBitmap(source, source.getHeight() / 2 - source.getWidth() / 2, 0, new Paint());
            } else {
                background = Bitmap.createBitmap(source.getWidth(), source.getWidth(), Bitmap.Config.ARGB_8888);
                canvas = new Canvas(background);

                canvas.drawColor(Color.WHITE);
                // draw the source image centered
                canvas.drawBitmap(source, 0, source.getWidth() / 2 - source.getHeight() / 2, new Paint());
            }

            source.recycle();
            canvas.setBitmap(null);

            // update the source image
            return background;

        }
        return source;

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
        showErrorToast(error, displayMsg, false);
    }

    private void showErrorToast(final String error, final String displayMsg, final boolean doFinish) {

        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    sendEvent("Error Dialog", displayMsg, error);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NoCropActivity.this);

                    // set dialog message
                    alertDialogBuilder.setMessage(displayMsg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {


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

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public void onClick(View v) {
        try {

            if (v == btnInstagram) {

                sendEvent("autocrop", "image", "");

                sendToRegrannShareScreen(MEDIA_IMAGE);

            }

        } catch (Exception e) {
            showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.");

        }

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
