package com.nimmble.rgpro.model;


public class InstaItem {

    private int _id, isScheduled, isNotified;
    private String title, video;
    private String photo;
    private String author;

    public int getIsNotified() {
        return isNotified;
    }

    public void setIsNotified(int isNotified) {
        this.isNotified = isNotified;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public int getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(int isScheduled) {
        this.isScheduled = isScheduled;
    }

    private String videoURL;
    private long scheduleTime;

    public void setVideoURL(String videoURL) {
        this.videoURL = videoURL;
    }

    public InstaItem() {
    }

    public InstaItem(String title, String photo, String videoURL, String author) {
        super();
        this.title = title;
        this.photo = photo;
        this.author = author;
        this.videoURL = videoURL;
    }

    public int getId() {
        return _id;
    }

    public void setId(int id) {
        this._id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(long t) {
        this.scheduleTime = t;
    }


    @Override
    public String toString() {
        return "InstaItem [id=" + _id + ", title=" + title + ", author=" + author
                + "]";
    }


}
