package com.nimmble.rgpro.util;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.documentfile.provider.DocumentFile;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.nimmble.rgpro.R;
import com.nimmble.rgpro.activity.RegrannApp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class Util {
    public static String RootDirectoryPhoto = "/Regrann/";
    public static String RootDirectoryMultiPhoto = "/Regrann - Multi Post/";

    private static String currentTempVideoFileName;


    public static void saveImageToFolder(Uri folderUri, Bitmap bitmap, String imageName) {
        // Convert the Bitmap into an OutputStream
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bitmapData = bos.toByteArray();

        try {
            // Use DocumentFile to create a new file in the selected folder
            DocumentFile pickedDir = DocumentFile.fromTreeUri(RegrannApp._this, folderUri);
            DocumentFile newFile = pickedDir.createFile("image/png", imageName);

            // Write the image data to the new file
            OutputStream out = RegrannApp._this.getContentResolver().openOutputStream(newFile.getUri());
            if (out != null) {
                out.write(bitmapData);
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void saveVideoToFolder(Uri folderUri, Uri videoUri, String videoName) {
        // Obtain the InputStream for the source video
        InputStream in = null;
        OutputStream out = null;
        try {
            in = RegrannApp._this.getContentResolver().openInputStream(videoUri);

            // Use DocumentFile to create a new file in the selected folder
            DocumentFile pickedDir = DocumentFile.fromTreeUri(RegrannApp._this, folderUri);

            // For this example, I'm assuming an MP4 format for the video. Modify as needed.
            DocumentFile newFile = pickedDir.createFile("video/mp4", videoName);

            // Write the video data to the new file
            out = RegrannApp._this.getContentResolver().openOutputStream(newFile.getUri());

            if (in != null && out != null) {
                byte[] buffer = new byte[4096]; // Use a buffer to read and write the video stream
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Ensure streams are closed, even if an error occurs
            try {
                if (in != null) in.close();
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static String getDefaultSaveFolder() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegrannApp._this);
        String folderName = preferences.getString("save_folder", new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + Util.RootDirectoryPhoto).getAbsolutePath());
        return folderName;

    }


    public static void setDefaultSaveFolder(String folderName) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegrannApp._this);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("save_folder", folderName);

        editor.apply();


    }

    public static String getCurrentVideoFileName() {

        return currentTempVideoFileName;
    }

    public static String getTempPhotoFilePathForMulti(String fname) {
        File file = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES);
        Log.d("app5", " getTempvideofilepath : " + file + RootDirectoryPhoto + fname);
        return file + RootDirectoryMultiPhoto + fname;

    }

    static BillingClient billingClient;
    static String sku = "rgrann_sub11";  // replace with your SKU


    public static void retreivePurchase() {

        PurchasesUpdatedListener b = new PurchasesUpdatedListener() {

            @Override
            public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> list) {

            }
        };

        billingClient = BillingClient.newBuilder(RegrannApp._this).setListener(b).enablePendingPurchases().build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    checkPurchasedItem();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });
    }

    private static void checkPurchasedItem() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RegrannApp._this);
        SharedPreferences.Editor editor = preferences.edit();

        if (billingClient.isReady()) {
            billingClient.queryPurchasesAsync(BillingClient.SkuType.SUBS, (billingResult, purchasesList) -> {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchasesList != null) {
                    for (Purchase purchase : purchasesList) {
                        if (purchase.getProducts().get(0).equals(sku)) {
                            // The SKU is already purchased
                            Log.d("app5", "The SKU is already purchased");
                            editor.putBoolean("subscribed", true);
                            editor.putBoolean("really_subscribed", true);
                            editor.commit();
                            return;
                        }
                    }

                    editor.putBoolean("subscribed", false);
                    editor.putBoolean("really_subscribed", false);
                    editor.commit();
                    Log.d("app5", "NOT purchased");
                } else {
                    // Handle any error occurred
                    //   Log.e("MainActivity", "Error occurred while querying purchases");
                }


            });
        }
    }

    public static String getTempVideoFilePath(boolean isMulti) {

        if (!isMulti) {
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

        String sb = str3 +
                "";
        request.setTitle(sb);
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

        String sb = str3 +
                "";
        request.setTitle(sb);

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


    static public boolean isKeepCaption(Context ctx) {

        SharedPreferences preferences;
        preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        //  return preferences.getBoolean(IS_KEEP_CAPTION, false) ;
        return false;
    }

    public static String prepareCaption(String title, String author, String caption_suffix, Context ctx) {
        return prepareCaption(title, author, ctx, caption_suffix, false);
    }

    public static String prepareCaption(String title, String author, String caption_suffix, Context ctx, boolean isTikTok) {
        return prepareCaption(title, author, ctx, caption_suffix, isTikTok);
    }

    public static String prepareCaption(String title, String author, Context ctx, String caption_suffix, boolean isTikTok) {
        String caption = "";
        String newCaption;

        try {

            caption = title;


            Log.d("regrann", "caption 1 : " + caption);
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
            Log.d("regrann", "caption 2 : " + caption);
            // set title to caption which now may include signature

            newCaption = caption;


            //count how many hashtags
            if (title != null) {
                int count = newCaption.length() - newCaption.replace("#", "").length();


                String caption_prefix = preferences.getString("caption_prefix", "Reposted");

                if (isTikTok)
                    caption = caption_prefix + " from TikTok/@" + author + " " + newCaption;
                else

                    caption = caption_prefix + " @" + author + " " + newCaption;


                if (count > 30 && signatureactive) {
                    caption = caption_prefix + " @" + author + "  " + title;
                }


            }


            newCaption = caption + "  ";
            Log.d("regrann", "newcaption 1 : " + newCaption);


        } catch (Exception e) {
            return "";
        }


        return newCaption;
    }


    // Decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f) {
        try {
            int IMAGE_MAX_SIZE = 1000;
            Bitmap b = null;

            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            FileInputStream fis = new FileInputStream(f);
            BitmapFactory.decodeStream(fis, null, o);
            fis.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                        (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
            }

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            fis = new FileInputStream(f);
            b = BitmapFactory.decodeStream(fis, null, o2);
            fis.close();

            return b;
        } catch (OutOfMemoryError e) {
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

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
}
