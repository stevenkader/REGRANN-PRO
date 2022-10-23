package com.nimmble.rgpro.activity;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.nimmble.rgpro.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CheckPermissions extends AppCompatActivity {

    CheckPermissions _this;

    Bundle _savedInstanceState;

    private boolean allNeededApproved = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _savedInstanceState = savedInstanceState;
        _this = this;

        setContentView(R.layout.activity_checkpermissions);




        Toolbar myToolbar =  findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);




        final Button button = findViewById(R.id.goBtn);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkPermissions(false);
                // Code here executes on main thread after user presses button
            }
        });


    }


    @Override
    public void onBackPressed() {
        if (allNeededApproved) {

            super.onBackPressed();
        } else {


            new AlertDialog.Builder(this)
                    .setMessage("Not all of the permissions have been approved. Approve them now?")
                    .setTitle("App Permissions Need Approval!")
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setCancelable(false)
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                   checkPermissions(false);


                                }
                            })


                    .create().show();

        }
    }


    @Override
    protected void onResume() {
        super.onResume();
      //  checkPermissions(false);


    }


    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    private void checkPermissions(boolean quicktest) {
        List<String> permissionsNeeded = new ArrayList<String>();

        final List<String> permissionsList = new ArrayList<String>();
        if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("Read SMS");


        if (permissionsNeeded.size() > 0) {

            //     Intent test = new Intent(this, CheckPermissions.class);
            //    startActivity(test);

            RegrannApp.sendEvent("cp_request_permissions");
            ActivityCompat.requestPermissions(this, permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);



        }


    }

    private boolean addPermission(List<String> permissionsList, String permission) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                return false ;
            }

            return true;


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<String, Integer>();
            // Initial
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            //       perms.put(Manifest.permission.RECEIVE_SMS, PackageManager.PERMISSION_GRANTED);
            //      perms.put(android.Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);


            if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

            ) {
                // All Permissions Granted
                // insertDummyContact();
                allNeededApproved = true;
                RegrannApp.sendEvent("cp_permission_granted");


                finish();

                if (getIntent().getBooleanExtra("from_sharescreen", false)) {
                    Intent i;

                    i = new Intent(RegrannApp._this, ShareActivity.class);
                    i.putExtra("from_sharescreen", true);
                    i.putExtra("mediaUrl", getIntent().getStringExtra("mediaUrl"));
                    startActivity(i);

                } else {
                    // Do something in response to button click
                    Intent myIntent = new Intent(_this, LoginRequestActivity.class);
                    _this.startActivity(myIntent);
                }


            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }





}
