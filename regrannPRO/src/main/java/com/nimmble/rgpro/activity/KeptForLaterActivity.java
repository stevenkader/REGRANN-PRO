package com.nimmble.rgpro.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.nimmble.rgpro.R;
import com.nimmble.rgpro.sqlite.KeptListAdapter;


public class KeptForLaterActivity extends AppCompatActivity {

    private KeptListAdapter dbHelper;
    private long currentPhotoID;
    public static KeptForLaterActivity _this;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kept_list);

        dbHelper = KeptListAdapter.getInstance(this);

        _this = this;


        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        displayListView();

    }




    public void removeCurrentPhoto() {
        dbHelper.remove(currentPhotoID);//create removemethod in database class
        displayListView();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_without_removeads, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_home:


                Intent intent = new Intent(_this, RegrannMainActivity.class);
                intent.putExtra("show_home", true);
                startActivity(intent);

                return true;


            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...

                // TODO Auto-generated method stub


                startActivity(new Intent(this, SettingsActivity2.class));


                return true;

            case R.id.action_help:
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.regrann.com/support"));
                startActivity(browserIntent);


                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void displayListView() {

        Cursor cursor = dbHelper.fetchAllItems();

        // The desired columns to be bound
        final String[] columns = new String[]{KeptListAdapter.KEY_TITLE, KeptListAdapter.KEY_PHOTO, KeptListAdapter.KEY_AUTHOR, KeptListAdapter.KEY_VIDEOTXT};


        // the XML defined views which the data will be bound to
        int[] to = new int[]{R.id.keptListTitle, R.id.keptListPhoto, R.id.keptListAuthor, R.id.videoIcon};

        // create the adapter using the cursor pointing to the desired data
        // as well as the layout information
        SimpleCursorAdapter dataAdapter = new SimpleCursorAdapter(this, R.layout.kept_list_item, cursor, columns, to, 0);


        if (dataAdapter.isEmpty()) {
            finish();
            return;
        }
        ListView listView = findViewById(R.id.gridView);


        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {
                // Get the cursor, positioned to the corresponding row in the
                // result set


                Cursor cursor = (Cursor) listView.getItemAtPosition(position);


                // Get the state's capital from this row in the database.db
                String itemID = cursor.getString(cursor.getColumnIndexOrThrow("_id"));
                String title = cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_TITLE));
                String author = cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_AUTHOR));
                String photo = cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_PHOTO));
                String videoURL = cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_VIDEO));


                Intent i = new Intent(KeptForLaterActivity.this, PostFromKeptActivity.class);

                //		int theItemID = Integer.parseInt(itemID) ;

                //	dbHelper.deleteItem(dbHelper.getItem(itemID));

                i.putExtra("ID", itemID);
                i.putExtra("photo", photo);
                i.putExtra("videoURL", videoURL);
                i.putExtra("title", title);
                i.putExtra("author", author);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                //	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                i.setAction("com.jaredco.action.fromkept");

                startActivity(i);

                currentPhotoID = id;


                //	Toast.makeText(getApplicationContext(), title + "/" + photo, Toast.LENGTH_SHORT).show();

            }
        });

    }
}
