package com.nimmble.rgpro.activity;

public interface VolleyRequestListener {


    // These methods are the different events and
    // need to pass relevant arguments related to the event triggered
    void onObjectReady(String title);

    // or when data has been loaded
    void onDataLoaded(String volleyReturn, String url);

}
