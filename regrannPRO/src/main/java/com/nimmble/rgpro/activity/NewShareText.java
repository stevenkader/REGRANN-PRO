package com.nimmble.rgpro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.nimmble.rgpro.R;

public class NewShareText extends Activity {


    NewShareText _this;
    String text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _this = this;

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String action = intent.getAction();


        // if this is from the share menu
        if (Intent.ACTION_SEND.equals(action)) {
            if (extras.containsKey(Intent.EXTRA_TEXT)) {
                try {
                    text = extras.getString(Intent.EXTRA_TEXT);
                    //      text = "@avragorn a partagé la publication de @oliverpecker avec vous. Affichez-la sur https://instagram.com/p/3xjRfSNi_n/?r=1440039387";


                    // retrieve URL

                    Handler h = new Handler(_this.getMainLooper());
                    h.post(new Runnable() {
                        @Override
                        public void run() {

                            try {
                          //      if (text.contains("instagram.com")   ) {

                                if (text.contains("instagram.com") || text.contains("youtube.com/shorts") || text.contains("fb.watch") || text.contains("tiktok") || text.contains("facebook.com") || text.contains("x.com") || text.contains("twitter.com")) {
                                    text = text.substring(text.indexOf("https://"));

                                    Intent i;
                                    i = new Intent(_this, ShareActivity.class);


                                    // String uri=imgDirPath+"/"+path;
                                    i.putExtra("mediaUrl", text);
                                    i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                                    System.out.println("***media url " + text);
                                   startActivity(i);


                                   finish();

                                   overridePendingTransition(R.anim.slide_up_anim, R.anim.slide_down_anim);

                                   RegrannApp.sendEvent("NewShareTextMethod", "count", "");


                               } else {
                                   showErrorToast("Error", "Repost PRO doesn't support this share link.");


                               }


                            } catch (Exception e) {
                            }
                        }

                    });


                } catch (Exception e) {
                    Log.e(this.getClass().getName(), e.toString());
                }


            }


        }

    }


    protected void onResume() {
        super.onResume();

        // Refresh the state of the +1 button each time the activity receives
        // focus.

    }


    private void showErrorToast(final String error, final String displayMsg) {
        showErrorToast(error, displayMsg, false);
    }

    private void showErrorToast(final String error, final String displayMsg, final boolean doFinish) {

        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    RegrannApp.sendEvent("Error Dialog", displayMsg, error);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(NewShareText.this);

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


    @Override
    public void onDestroy() {
        super.onDestroy();

    }


}
