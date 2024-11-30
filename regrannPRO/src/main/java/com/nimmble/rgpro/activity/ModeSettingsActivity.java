package com.nimmble.rgpro.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nimmble.rgpro.R;
import com.nimmble.rgpro.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class ModeSettingsActivity extends AppCompatActivity {

    Button runTutorialBtn, videoBtn, loginBtn, myFeedBtn;

    CheckBox cb4 = null;
    CheckBox cb5 = null;
    SharedPreferences preferences;
    Button backup;
    boolean noAds;

    @Override
    public void onResume() {
        super.onResume();



    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication().getApplicationContext());


            noAds = true;


            setContentView(R.layout.activity_modesettings);


            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);


            backup = findViewById(R.id.button2);

            final CheckBox cb0 = findViewById(R.id.normalMode);
            assert cb0 != null;
            cb0.setChecked(false);

            final CheckBox cb1 = findViewById(R.id.autoPostSetting);
            assert cb1 != null;
            cb1.setChecked(false);


            final CheckBox cb2 = findViewById(R.id.alwaysSave);
            assert cb2 != null;
            cb2.setChecked(false);
            final CheckBox cb3 = findViewById(R.id.keepForLater);
            assert cb3 != null;
            cb3.setChecked(false);

/**
 <item>Quick post</item>
 <item>Show Regrann screen</item>
 <item>Quick save</item>
 <item>Quick keep to post later</item>
 </string-array>
 <string-array name="pref_example_list_values">
 <item>1</item>
 <item>2</item>
 <item>3</item>
 <item>4</item>
 **/

            preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication().getApplicationContext());


            boolean quickpost = preferences.getBoolean("quickpost", false);
            boolean quicksave = preferences.getBoolean("quicksave", false);
            boolean quickkeep = preferences.getBoolean("quickkeep", false);
            boolean normalMode = preferences.getBoolean("normalMode", true);


            if (normalMode) {
                cb0.setChecked(true);
            }


            if (quickpost) {
                cb1.setChecked(true);
            }


            if (quicksave) {
                cb2.setChecked(true);
            }

            if (quickkeep) {
                cb3.setChecked(true);
            }


            backup.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    try {
                        final String inFileName = "/data/data/com.nimmble.rgrpo/databases/InstaItemDB";
                        File dbFile = new File(inFileName);
                        FileInputStream fis = new FileInputStream(dbFile);

                        String outFileName = Environment.getExternalStorageDirectory() + "/Documents/.InstaItemDB";

                        // Open the empty db as the output stream
                        OutputStream output = new FileOutputStream(outFileName);

                        // Transfer bytes from the inputfile to the outputfile
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            output.write(buffer, 0, length);
                        }

                        // Close the streams
                        output.flush();
                        output.close();
                        fis.close();
                    } catch (Exception e) {
                        Log.d("tag", e.getMessage());
                    }

                }
            });

            cb0.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = preferences.edit();
                    // is chkIos checked?
                    if (((CheckBox) v).isChecked()) {
                        RegrannApp.sendEvent("SettingScreen", "Normal Checkbox", "");
                        // Util.showOkDialog("Important", getString(R.string.HowToTurnOffQuickPost), ZZZSettingsActivity.this);
                        editor.putBoolean("normalMode", true);
                        editor.putBoolean("quickpost", false);
                        editor.apply();
                        editor.putBoolean("quicksave", false);
                        editor.putBoolean("quickkeep", false);
                        editor.putString("mode_list", "2");

                        editor.apply();
                        cb1.setChecked(false);
                        cb2.setChecked(false);
                        cb3.setChecked(false);

                    } else {
                        cb0.setChecked(true);
                        editor.putBoolean("quickpost", true);
                        editor.putString("mode_list", "1");
                        editor.apply();
                    }

                }
            });

            cb1.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = preferences.edit();
                    // is chkIos checked?
                    if (((CheckBox) v).isChecked()) {


                        RegrannApp.sendEvent("SettingScreen", "QuickPost Checkbox", "");
                            Util.showOkDialog("Important", getString(R.string.HowToTurnOffQuickPost), ModeSettingsActivity.this);
                            editor.putBoolean("quickpost", true);
                            editor.putBoolean("normalMode", false);
                            editor.apply();
                            editor.putBoolean("quicksave", false);
                            editor.putBoolean("quickkeep", false);
                            editor.putString("mode_list", "1");
                            editor.apply();
                            cb0.setChecked(false);
                            cb2.setChecked(false);
                            cb3.setChecked(false);

                    } else {
                        cb0.setChecked(true);
                        editor.putBoolean("quickpost", true);
                        editor.putBoolean("quickpost", false);
                        editor.apply();
                    }

                }
            });

            cb2.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = preferences.edit();
                    // is chkIos checked?

                    if (((CheckBox) v).isChecked()) {

                        RegrannApp.sendEvent("SettingScreen", "QuickSave Checkbox", "");
                            Util.showOkDialog("Important", getString(R.string.HowToTurnOffQuickSave), ModeSettingsActivity.this);

                            editor.putBoolean("quicksave", true);
                            editor.putBoolean("normalMode", false);
                            editor.putString("mode_list", "3");
                            editor.putBoolean("quickpost", false);
                            editor.putBoolean("quickkeep", false);
                            editor.apply();
                            cb1.setChecked(false);
                            cb0.setChecked(false);
                            cb3.setChecked(false);

                        //  SettingsActivity2.bindPreferenceSummaryToValue("mode_string", getString(R.string.Quick_Repost_Mode));
                    } else {
                        cb0.setChecked(true);
                        editor.putBoolean("quickpost", true);
                        editor.putBoolean("quicksave", false);
                        editor.putString("mode_list", "2");
                        editor.apply();
                    }

                }
            });
            cb3.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = preferences.edit();
                    // is chkIos checked?
                    if (((CheckBox) v).isChecked()) {
                        RegrannApp.sendEvent("SettingScreen", "PostLater Checkbox", "");
                        Util.showOkDialog("Important", getString(R.string.howToTurnOffPostLater), ModeSettingsActivity.this);

                        editor.putBoolean("quickkeep", true);
                        editor.putBoolean("normalMode", false);
                        editor.putString("mode_list", "4");
                        editor.putBoolean("quicksave", false);

                        editor.putBoolean("quickpost", false);
                        editor.apply();
                        cb0.setChecked(false);
                        cb1.setChecked(false);
                        cb2.setChecked(false);
                    } else {
                        cb0.setChecked(true);
                        editor.putString("mode_list", "2");
                        editor.putBoolean("quickpost", true);
                        editor.putBoolean("quickkeep", false);
                        editor.apply();
                    }

                }
            });


        } catch (Exception e) {
            int i = 1;
        }
    }


}
