package com.nimmble.rgpro.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nimmble.rgpro.R;


public class LoginRequestActivity extends AppCompatActivity {

    LoginRequestActivity _this;

    Bundle _savedInstanceState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _savedInstanceState = savedInstanceState;
        _this = this;

        setContentView(R.layout.activity_request_login);


        RegrannApp.sendEvent("InLoginRequestV2");
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    /**
     * Called when the user touches the button
     */
    public void OnClickLoginBTN(View view) {
        // Do something in response to button click
        Intent myIntent = new Intent(_this, InstagramLogin.class);
        _this.startActivity(myIntent);
        RegrannApp.sendEvent("InLoginRequest_btnclickedV2");
        finish();
    }


    public void OnClickSkipBTN(View view) {
        // Do something in response to button click

        RegrannApp.sendEvent("InLoginRequest_btnSkip");
        finish();
    }

}
