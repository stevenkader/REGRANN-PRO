package com.nimmble.rgpro.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
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


    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus)

            checkForInstagramURLinClipboard();
    }

    private void checkForInstagramURLinClipboard() {

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        ClipData clipData = clipboard != null ? clipboard.getPrimaryClip() : null;

        if (clipData != null) {

            try {
                final ClipData.Item item = clipData.getItemAt(0);
                String text = item.coerceToText(KeptForLaterActivity.this).toString();


                if (text.length() > 18) {

                    ClipData clip = ClipData.newPlainText("message", "");
                    clipboard.setPrimaryClip(clip);

                    //    if (text.indexOf("ig.me") > 1 ||text.indexOf("instagram.com/tv/") > 1 || text.indexOf("instagram.com/p/") > 1) {
                    if (text.contains("instagram.com") || text.contains("youtube.com/shorts") || text.contains("fb.watch") || text.contains("tiktok") || text.contains("facebook.com") || text.contains("twitter.com") || text.contains("x.com")) {


                        Intent i;
                        i = new Intent(_this, ShareActivity.class);
                        text = text.substring(text.indexOf("https://"));

                        i.putExtra("mediaUrl", text);

                        //    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        //  i.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);


                        i.putExtra("isJPEG", "no");
                        System.out.println("***media url " + text);


                        startActivity(i);
                        //   finish();

                    }

                }


            } catch (Exception e) {
                int i = 1;
            }
        }


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


    protected void onResume() {
        super.onResume();
        // Refresh the state of the +1 button each time the activity receives
        // focus.

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
