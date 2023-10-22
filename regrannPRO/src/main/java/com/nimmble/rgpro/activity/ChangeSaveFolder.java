package com.nimmble.rgpro.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nimmble.rgpro.R;
import com.nimmble.rgpro.util.Util;

public class ChangeSaveFolder extends AppCompatActivity {


    SharedPreferences preferences;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            preferences = PreferenceManager.getDefaultSharedPreferences(this.getApplication().getApplicationContext());


            setContentView(R.layout.activity_changesavefolder);


            Toolbar myToolbar = findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);


            Button changebtn = findViewById(R.id.changefolder);

            String currentFolder = preferences.getString("save_folder", Util.getDefaultSaveFolder());

            TextView txtFolder = findViewById(R.id.foldername);

            txtFolder.setText(currentFolder);
            changebtn.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivityForResult(intent, 44);

                }
            });


        } catch (Exception e) {
            int i = 1;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 44) {
            Uri uri = data.getData();
            Log.d("app5", uri.toString());

            Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                    DocumentsContract.getTreeDocumentId(uri));


            String newFolder = Util.getPath(this, docUri);

            Log.d("app5", Util.getPath(this, docUri));

            String folderName = Util.getPath(this, docUri);

            Log.d("app5", folderName);


            TextView txtFolder = findViewById(R.id.foldername);

            txtFolder.setText(newFolder);

            Util.setDefaultSaveFolder(newFolder);


        }
    }

}
