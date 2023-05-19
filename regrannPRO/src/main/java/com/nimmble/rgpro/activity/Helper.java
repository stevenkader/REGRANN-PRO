package com.nimmble.rgpro.activity;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nimmble.rgpro.R;

import java.io.File;
import java.util.ArrayList;

public class Helper {

    public static ArrayList<TiktokPost> getVideoHistory(Context context) {
        SharedPreferenceHelper sharedPreferenceHelper = new SharedPreferenceHelper(context);
        ArrayList<TiktokPost> toReturn;
        String savedInPrefs = sharedPreferenceHelper.getStringPreference("VIDEO_HISTORY");
        if (savedInPrefs != null) {
            toReturn = new Gson().fromJson(savedInPrefs, new TypeToken<ArrayList<TiktokPost>>() {
            }.getType());
        } else {
            toReturn = new ArrayList<>();
        }
        return toReturn;
    }

    public static void saveVideoInHistory(Context mContext, TiktokPost tiktokPost) {
        ArrayList<TiktokPost> savedList = getVideoHistory(mContext);
        savedList.add(tiktokPost);
        new SharedPreferenceHelper(mContext).setStringPreference("VIDEO_HISTORY", new Gson().toJson(savedList, new TypeToken<ArrayList<TiktokPost>>() {
        }.getType()));
    }

    private static void saveDownloadRequestId(Context context, long requestId) {
        new SharedPreferenceHelper(context).setLongPreference("DOWNLOAD_ID", requestId);
    }

    public static long getDownloadRequestId(Context context) {
        return new SharedPreferenceHelper(context).getLongPreference("DOWNLOAD_ID", -1);
    }

    public static String getFileBase(Context context) {
        return RegrannApp._this.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + context.getString(R.string.app_name);
    }

    public static File getFile(Context context, TiktokPost tiktokPost) {
        return new File(getFileBase(context) + "/" + tiktokPost.getTimeStamp() + ".mp4");
    }

    public static void downloadTiktokPost(Context context, TiktokPost tiktokPost, String videoFileName) {
        try {
            DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(tiktokPost.getDownloadUrl()));
            request.setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setTitle(context.getString(R.string.app_name))
                    .setDescription(context.getString(R.string.downloading_video))
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir("/", videoFileName);

            if (mgr != null) {
                long requestId = mgr.enqueue(request);
                Helper.saveDownloadRequestId(context, requestId);
            //    Toast.makeText(context, R.string.downloading_video, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            if (ex.getMessage() != null)
                Log.e("downloadErr", ex.getMessage());
        }
    }

    public static String getMimeType(Context context, Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

}
