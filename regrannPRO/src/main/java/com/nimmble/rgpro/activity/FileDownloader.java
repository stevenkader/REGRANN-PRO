package com.nimmble.rgpro.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.nimmble.rgpro.util.Util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {

    static String fname;
    static boolean isSocial = false;
    private static final String DOWNLOAD_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    public static void downloadFile(Context context, String url, String f, boolean fromSocial) {
        fname = f;
        Util.setTempVideoFileName(fname);
        isSocial = fromSocial;
        new DownloadFileTask(context).execute(url);
    }

    private static class DownloadFileTask extends AsyncTask<String, Integer, Boolean> {
        private final Context context;

        public DownloadFileTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d("app5", "download toast");
            Toast.makeText(context, "Downloading video...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String fileUrl = urls[0];
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000); // 15 sec timeout
                connection.setReadTimeout(30000); // 30 sec timeout
                connection.connect();
                Log.d("app5", "get File size : ");
                int fileLength = connection.getContentLength(); // Get file size
                Log.d("app5", "File size : " + fileLength);
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());

                // Define file path
                File outputDir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS + "/Regrann/");
                if (!outputDir.exists()) {
                    outputDir.mkdirs(); // Ensure directory exists
                }
                File outputFile = new File(outputDir, fname);

                // Use BufferedOutputStream for better performance
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream, 16 * 1024); // 16 KB buffer

                byte[] buffer = new byte[64 * 1024];  // Read 16 KB chunks
                int bytesRead;
                long totalRead = 0; // Track progress
                float lastP = -1;


                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    totalRead += bytesRead;
                    bufferedOutputStream.write(buffer, 0, bytesRead);

                    // Calculate progress
                    if (fileLength > 0) {
                        int progress = (int) (totalRead * 100 / fileLength);
                        float p = (totalRead * 100 / fileLength);
                        Log.d("app5", "fileLength: " + fileLength);
                        if (p > lastP) {
                            //    Log.d("app5", "Progress: " + p + "%");
                            //   if (p == 40 || p == 80)
                            //     publishProgress(progress); // Update UI
                            //    lastP = p ;
                        }
                    }
                }

                // Cleanup
                bufferedOutputStream.flush();
                bufferedOutputStream.close();
                inputStream.close();
                connection.disconnect();

                return true;
            } catch (Exception e) {
                Log.e("FileDownload", "Error downloading file", e);
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.d("app5", "Download Progress: " + values[0] + "%");
            Toast.makeText(context, "Download Progress: " + values[0] + "%", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            RegrannApp.sendEvent("filedownload_" + result);
            ShareActivity._this.videoDownloadComplete(result, isSocial);

            if (result) {
                Toast.makeText(context, "Download complete!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Download failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
