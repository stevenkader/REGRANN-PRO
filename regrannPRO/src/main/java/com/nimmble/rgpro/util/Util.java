package com.nimmble.rgpro.util;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.nimmble.rgpro.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Util {
    public static String RootDirectoryPhoto = "/Regrann/";
    public static String RootDirectoryMultiPhoto = "/Regrann - Multi Post/";

    private static String currentTempVideoFileName;


    public static String getCurrentVideoFileName() {

        return currentTempVideoFileName;
    }
    public static String getTempPhotoFilePathForMulti(String fname) {
        File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
        Log.d("app5", " getTempvideofilepath : " + file + RootDirectoryPhoto + fname);
        return file + RootDirectoryMultiPhoto + fname;

    }


    public static String getTempVideoFilePath(boolean isMulti) {

        if (isMulti == false) {
            return getTempVideoFilePath();
        }

        File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
        Log.d("app5", " getTempvideofilepath : " + file + RootDirectoryPhoto + currentTempVideoFileName);
        return file + RootDirectoryMultiPhoto + currentTempVideoFileName;
    }

    public static String getTempVideoFilePath() {

        File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_DOWNLOADS);
        Log.d("app5", " getTempvideofilepath : " + file + RootDirectoryPhoto + currentTempVideoFileName);
        return file + RootDirectoryPhoto + currentTempVideoFileName;
    }

    public static void setTempVideoFileName(String fname) {
        currentTempVideoFileName = fname;
        Log.d("app5", "currentTempVideoFilePath :   " + currentTempVideoFileName);

    }

    public static long startDownload(String str, String str2, Context context2, String str3) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        StringBuilder sb = new StringBuilder();
        sb.append(str3);
        sb.append("");
        request.setTitle(sb.toString());
        String str4 = Environment.DIRECTORY_DOWNLOADS;

        String sb2 = Util.RootDirectoryPhoto + str3;

        setTempVideoFileName(str3);
        request.setDestinationInExternalPublicDir(str4, sb2);
        return ((DownloadManager) context2.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
    }


    public static long startDownloadMulti(String str, String str2, Context context2, String str3, boolean isAutsave) {
        if (str == null)
            return 0;


        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
        request.setAllowedNetworkTypes(3);
        request.setAllowedOverRoaming(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

        StringBuilder sb = new StringBuilder();
        sb.append(str3);
        sb.append("");
        request.setTitle(sb.toString());

        String str4;
        String sb2;


        str4 = Environment.DIRECTORY_PICTURES;

        if (isAutsave)
            sb2 = Util.RootDirectoryPhoto + str3;
        else

            sb2 = Util.RootDirectoryMultiPhoto + str3;


        request.setDestinationInExternalPublicDir(str4, sb2);

        return ((DownloadManager) context2.getSystemService(Context.DOWNLOAD_SERVICE)).enqueue(request);
    }


    public static void showOkDialog(String title, String msg, Context ctx) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

        // set dialog message
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher);

        alertDialogBuilder.setMessage(msg)
                .setCancelable(false).setPositiveButton("Ok", null);

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }



    static public boolean isKeepCaption (Context ctx)
    {

        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
     //  return preferences.getBoolean(IS_KEEP_CAPTION, false) ;
        return false ;
    }

    public static String prepareCaption(String title, String author, String caption_suffix,  Context ctx) {
        return prepareCaption( title,  author,  ctx, caption_suffix, false);
    }

    public static String prepareCaption(String title, String author, String caption_suffix,  Context ctx, boolean isTikTok) {
        return prepareCaption( title,  author,  ctx, caption_suffix, isTikTok);
    }

    public static String prepareCaption(String title, String author, Context ctx, String caption_suffix, boolean isTikTok) {
        String caption = "";
        String newCaption;

        try {

            caption =  title ;


            Log.d("regrann","caption 1 : " + caption);
            SharedPreferences preferences;
            preferences = PreferenceManager.getDefaultSharedPreferences(ctx);

            boolean signatureactive = false;

            String sigPref = preferences.getString("signature_type_list", "");


            if (sigPref.equals("2") || sigPref.equals("3"))
                signatureactive = true;


            if (signatureactive) {
                String signatureText = preferences.getString("signature_text", "");
                caption = title + signatureText;

            }

            if (sigPref.equalsIgnoreCase("3")) { // replace signature
                caption = preferences.getString("signature_text", "");

            }
            Log.d("regrann","caption 2 : " + caption);
            // set title to caption which now may include signature

            newCaption = caption ;



                //count how many hashtags
                if (title != null) {
                    int count = newCaption.length() - newCaption.replace("#", "").length();




                    String caption_prefix = preferences.getString("caption_prefix", "Reposted");

                    if (isTikTok)
                        caption = caption_prefix + " from TikTok/@" + author + " " + newCaption;
                        else

                        caption = caption_prefix + " @" + author + " " + newCaption;





                    if (count > 30 && signatureactive)

                    {
                        caption = caption_prefix + " @" + author + "  " + title;
                    }


                }



            newCaption = caption + "  "  ;
            Log.d("regrann","newcaption 1 : " + newCaption);






        } catch (Exception e) {
            return "";
        }



        return newCaption;
    }



    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f) {
        try {
            int IMAGE_MAX_SIZE = 1000 ;
            Bitmap b = null;

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int)Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

            return b;
        } catch (OutOfMemoryError e) {} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
