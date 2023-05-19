package com.nimmble.rgpro.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.nimmble.rgpro.util.Util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {

    static String fname;
    private static final String DOWNLOAD_FOLDER = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    public static void downloadFile(Context context, String url, String f) {
        fname = f;
        Util.setTempVideoFileName(fname);
        new DownloadFileTask(context).execute(url);
    }

    private static class DownloadFileTask extends AsyncTask<String, Void, Boolean> {
        private final Context context;

        public DownloadFileTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            String fileUrl = urls[0];
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // Get the file name from the URL
                String fileName = fname;

                File outputFile = new File(DOWNLOAD_FOLDER + "/Regrann/", fileName);

                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                FileOutputStream outputStream = new FileOutputStream(outputFile);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.flush();
                outputStream.close();
                inputStream.close();

                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            ShareActivity._this.videoDownloadComplete(result);
            if (result) {
                Toast.makeText(context, "Download complete", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Download failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
