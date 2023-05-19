package com.nimmble.rgpro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.nimmble.rgpro.model.InstaItem;
import com.nimmble.rgpro.sqlite.KeptListAdapter;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

//import com.google.android.gms.analytics.HitBuilders;
//import com.google.android.gms.analytics.Tracker;
//import com.google.android.gms.plus.PlusOneButton;

public class KeepForLaterActivity extends Activity {
    int buttonWidth;
    private ImageView settings, btnEmail, btndownloadphoto, btnTweet, btnFacebook, btnSMS, btnSetting, btnInviteFriends, btnInstagram, btnShare;
    private ImageView btnShareAppFB, btnShareAppTW, btnShareAppGooglePlus;
    private SeekBar seekBarOpacity;
    private String uriStr, tempVideoFullPathName;
    ImageView previewImage;
    private Button btnSupport;

    private LinearLayout screen_ui, full_ui;

    private RelativeLayout shareLayout;
    private RelativeLayout buttonLayout;
    Drawable backgroundDrawable;
    Uri uri;
    boolean supressToast = false;
    KeepForLaterActivity _this = this;
    public static final int ACTION_SMS_SEND = 0;
    public static final int ACTION_TWEET_SEND = 1;
    public static final int ACTION_FACEBOOK_POST = 2;
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
    //PlusOneButton mPlusOneButton = null;
    boolean autopost, autosave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast toast = Toast.makeText(KeepForLaterActivity.this, "Keeping For Later", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

        toast.show();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    photoReady = false;


                    tempFileName = "temp_regrann_" + System.currentTimeMillis() + ".jpg";
                    tempVideoName = "/temp/temp_regrann_" + System.currentTimeMillis() + ".mp4";
                    regrannPictureFolder = getAlbumStorageDir("Regrann").getAbsolutePath();

                    // Get the directory for the user's public pictures directory.
                    File file = new File(RegrannApp._this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "regrann-keepforlater");

                    if (!file.mkdirs()) {
                        Log.e("error", "Directory not created");
                    }
                    tempFileFullPathName = file + File.separator + tempFileName;
                    tempVideoFullPathName = file + File.separator + tempVideoName;

                    tempFile = new File(tempFileFullPathName);

                    try {

                        Log.d("mediaURL", getIntent().getStringExtra("mediaUrl"));
                        String t = "http://api.instagram.com/oembed?url=" + getIntent().getStringExtra("mediaUrl");
                        // String result = GET (t);
                        new HttpAsyncTask().execute(t);

                    } catch (Exception e) {
                        showErrorToast(e.getMessage(), "Sorry. There was a problem finding that photo. Please try again later.", true);

                    }

                } catch (Exception e) {
                }
            }
        });
        thread.start();


    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    protected void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives
        // focus.

    }

    private void sendEvent(String cat, String action, String label) {
        // Get tracker.
        //	Tracker t = ((RegrannApp) KeepForLaterActivity.this.getApplication()).getTracker(TrackerName.APP_TRACKER);
        // Build and send an Event.
        //	t.send(new HitBuilders.EventBuilder().setCategory(cat).setAction(action).setLabel(label).build());

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            String jsonRes = GET(urls[0]);

            if (jsonRes.compareTo("No Media Match") == 0) {
                showErrorToast("error", "That photo may be from a private account so it is not possible to Regrann it at this time.", true);

                return "error";
            }
            try {
                JSONObject json = new JSONObject(jsonRes);
                url = null;
                try {
                    if (json.getString("type").equals("video"))

                        url = json.getString("thumbnail_url");
                    else

                        url = json.getString("thumbnail_url");
                } catch (Exception e1) {

                    String thumbURL = null;

                    try {
                        String copiedURL = getIntent().getStringExtra("mediaUrl");
                        if (copiedURL.startsWith("http://"))
                            copiedURL = "https" + copiedURL.substring(4);

                        URLConnection conn = new URL(copiedURL).openConnection();

                        // open the stream and put it into BufferedReader
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        String inputLine;
                        boolean done = false;

                        while (thumbURL == null && (inputLine = br.readLine()) != null && !done) {
                            // System.out.println(inputLine);
                            if (inputLine.indexOf("<body") > 1)
                                done = true;

                            if (inputLine.indexOf("<meta property=\"og:image\"") > 1) {

                                int startPos = inputLine.indexOf("http");
                                int endPos = inputLine.indexOf("jpg");
                                if (startPos > 1 && endPos > 1) {

                                    thumbURL = inputLine.substring(startPos, endPos + 3);
                                    Log.d("VIDEO URL ", thumbURL);
                                }

                            }

                            Log.d("file Line", inputLine);

                        }
                        br.close();
                        url = thumbURL;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                title = json.getString("title");
                author = json.getString("author_name");

                URL imageurl = new URL(url);
                Bitmap bitmap = BitmapFactory.decodeStream(imageurl.openConnection().getInputStream());

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                // write the bytes in file
                FileOutputStream fo = new FileOutputStream(tempFile);
                fo.write(bytes.toByteArray());

                // remember close de FileOutput
                fo.close();

                bitmap.recycle();

            } catch (Exception e) {
                showErrorToast(e.getMessage(), "Sorry. There was a problem finding that photo. Please try again later.", true);

                return "error";
            }

            return jsonRes;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            try {

                if (result.compareTo("error") != 0) {

                    try {


                        KeptListAdapter db =     KeptListAdapter.getInstance(_this);

                        /**
                         * CRUD Operations
                         * */
                        // add Books
                        // "title TEXT, "+ "photo TEXT, "+ "author
                        db.addItem(new InstaItem(title, tempFileFullPathName, tempVideoFullPathName, author));

                        finish();


                    } catch (Exception e) {
                        showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.", true);

                    }

                }

                // etResponse.setText(result);
            } catch (Exception e) {
                showErrorToast(e.getMessage(), "Sorry. There was a problem. Please try again later.", true);

            }

        }

        public String GET(String urlIn) {
            InputStream inputStream = null;
            // String result = "";
            try {

                // given a url open a connection
                URLConnection c = new URL(urlIn).openConnection();

                // set the connection timeout to 5 seconds and the read timeout
                // to 10 seconds
                c.setConnectTimeout(5000);
                c.setReadTimeout(10000);

                BufferedReader reader = null;
                StringBuilder builder = new StringBuilder();
                try {
                    InputStreamReader i = new InputStreamReader(c.getInputStream());
                    reader = new BufferedReader(i);
                    for (String line; (line = reader.readLine()) != null; ) {
                        builder.append(line.trim());
                    }
                } finally {
                    if (reader != null)
                        try {
                            reader.close();
                        } catch (IOException logOrIgnore) {
                        }
                }

                return builder.toString();

            } catch (Exception e) {

                showErrorToast(e.getMessage(), "Sorry. There was a problem finding that photo. Please try again later.", true);
                return "";

            }

        }
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
                    spinner.setVisibility(View.GONE);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(KeepForLaterActivity.this);

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

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... urls) {
            try {

                URL url;
                String videoURL = null;

                try {
                    String copiedURL = getIntent().getStringExtra("mediaUrl");
                    if (copiedURL.startsWith("http://"))
                        copiedURL = "https" + copiedURL.substring(4);

                    url = new URL(copiedURL); // enter your url here which to
                    // download

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    // open the stream and put it into BufferedReader
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String inputLine;
                    boolean done = false;
                    Log.d("In video reading ", "Start reading  for Video ");
                    while (videoURL == null && (inputLine = br.readLine()) != null && !done) {
                        // System.out.println(inputLine);
                        if (inputLine.indexOf("<body") > 1)
                            done = true;

                        if (inputLine.indexOf("<meta property=\"og:video\"") > 1) {

                            int startPos = inputLine.indexOf("http");
                            int endPos = inputLine.indexOf("mp4");
                            if (startPos > 1 && endPos > 1) {

                                videoURL = inputLine.substring(startPos, endPos + 3);
                                Log.d("VIDEO URL ", videoURL);
                            }

                        }

                        Log.d("file Line", inputLine);

                    }
                    br.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("In video reading ", " done Checking for Video ");

                return videoURL;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(final String videoURL) {
            try {

                // is this a video
                if (videoURL == null) {
                    try {

                        // not a video ... save photo instead

                        String t = "http://api.instagram.com/oembed?url=" + getIntent().getStringExtra("mediaUrl");
                        // String result = GET (t);
                        new HttpAsyncTask().execute(t);

                    } catch (Exception e) {
                        showErrorToast(e.getMessage(), "Sorry. There was a problem finding that photo. Please try again later.", true);

                    }

                    return;
                }

                AsyncTask<Void, Void, Void> videoLoader = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            final int TIMEOUT_CONNECTION = 5000;// 5sec
                            final int TIMEOUT_SOCKET = 30000;// 30sec

                            URL url = null;
                            try {
                                url = new URL(videoURL);
                            } catch (MalformedURLException e3) {
                                // TODO Auto-generated catch block
                                e3.printStackTrace();
                            }
                            long startTime = System.currentTimeMillis();
                            Log.i("info", "image download beginning: " + videoURL);

                            // Open a connection to that URL.
                            URLConnection ucon = null;
                            try {
                                ucon = url != null ? url.openConnection() : null;
                            } catch (IOException e2) {
                                // TODO Auto-generated catch block
                                showErrorToast(e2.getMessage(), "Sorry. There was a problem finding that video. Please try again later.", true);

                                e2.printStackTrace();
                            }

                            // this timeout affects how long it takes for
                            // the
                            // app to
                            // realize
                            // there's a connection problem
                            ucon.setReadTimeout(TIMEOUT_CONNECTION);
                            ucon.setConnectTimeout(TIMEOUT_SOCKET);

                            // Define InputStreams to read from the
                            // URLConnection.
                            // uses 3KB download buffer
                            InputStream is = null;
                            try {
                                is = ucon.getInputStream();
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            BufferedInputStream inStream = new BufferedInputStream(is, 1024 * 5);
                            FileOutputStream outStream = null;
                            try {
                                outStream = new FileOutputStream(Environment.getExternalStorageDirectory() + tempVideoName);

                            } catch (FileNotFoundException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            byte[] buff = new byte[5 * 1024];

                            // Read bytes (and store them) until there is
                            // nothing
                            // more to
                            // read(-1)
                            int len;
                            try {
                                while ((len = inStream.read(buff)) != -1) {
                                    outStream.write(buff, 0, len);
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                showErrorToast(e.getMessage(), "Sorry. There was a problem finding that video. Please try again later.", true);

                                e.printStackTrace();
                            }

                            // clean up
                            try {
                                outStream.flush();
                                outStream.close();
                                inStream.close();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            tempVideoFile = new File(Environment.getExternalStorageDirectory() + tempVideoName);
                            isVideo = true;

                        } catch (Exception e) {
                            showErrorToast(e.getMessage(), "Sorry. There was a problem finding that video. Please try again later.", true);
                            isVideo = false;
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        try {


                        } catch (Exception e) {
                        }

                    }
                };

                // runOnUiThread(new Runnable() {
                // public void run() {

                try {

                    // pd = ProgressDialog.show(QuickSaveActivity.this,
                    // "Please Wait...", "Downloading Video!", true, true);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // }
                // });

                videoLoader.execute((Void[]) null);

            } catch (Exception e) {
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
