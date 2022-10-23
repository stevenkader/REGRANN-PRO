package com.nimmble.rgpro.activity;

import android.os.Parcel;
import android.os.Parcelable;

public class TiktokPost implements Parcelable {
    private String originalUrl, userName, caption, thumbnail, downloadUrl, timeStamp;

    public TiktokPost(String originalUrl, String userName, String caption, String thumbnail, String downloadLink) {
        this.originalUrl = originalUrl;
        this.userName = userName;
        this.caption = caption;
        this.thumbnail = thumbnail;
        this.downloadUrl = downloadLink;
        this.timeStamp = String.valueOf(System.currentTimeMillis());
    }

    public TiktokPost(String userName) {
        this.userName = userName;
    }

    protected TiktokPost(Parcel in) {
        originalUrl = in.readString();
        userName = in.readString();
        caption = in.readString();
        thumbnail = in.readString();
        downloadUrl = in.readString();
        timeStamp = in.readString();
    }

    public static final Creator<TiktokPost> CREATOR = new Creator<TiktokPost>() {
        @Override
        public TiktokPost createFromParcel(Parcel in) {
            return new TiktokPost(in);
        }

        @Override
        public TiktokPost[] newArray(int size) {
            return new TiktokPost[size];
        }
    };

    public String getUserName() {
        return userName == null ? "" : userName;
    }

    public String getCaption() {
        return caption;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getOriginalUrl() {
        return originalUrl;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTimeStamp() {
        return timeStamp;
    }



    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(originalUrl);
        dest.writeString(userName);
        dest.writeString(caption);
        dest.writeString(thumbnail);
        dest.writeString(downloadUrl);
        dest.writeString(timeStamp);
    }
}
