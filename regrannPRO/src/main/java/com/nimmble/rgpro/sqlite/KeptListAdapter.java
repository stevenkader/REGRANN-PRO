package com.nimmble.rgpro.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.nimmble.rgpro.activity.RegrannApp;
import com.nimmble.rgpro.model.InstaItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class KeptListAdapter extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 6;
    // Database Name
    private static final String DATABASE_NAME = ".RegrannDB-Keep";

    private static boolean checkedTables = false;
    private static KeptListAdapter mInstance = null;


//  super(context, Environment.getExternalStorageDirectory()
    //        + File.separator + FILE_DIR
    //        + File.separator + DATABASE_NAME, null, DATABASE_VERSION);

    private KeptListAdapter(Context ctx) {
        //   super(ctx, Environment.getExternalStoragePublicDirectory(
        //          Environment.DIRECTORY_DOCUMENTS) +  File.separator
        //         + DATABASE_NAME, null, DATABASE_VERSION);


        super(ctx, RegrannApp._this.getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath(), null, DATABASE_VERSION);

        try {
            Log.d("app5", Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS) + File.separator
                    + DATABASE_NAME);

            Log.d("app5", RegrannApp._this.getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath());

            File t = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS) + File.separator
                    + DATABASE_NAME);

            File t1 = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOCUMENTS) + File.separator
                    + DATABASE_NAME + "-bak");

            File newLoc = new File(RegrannApp._this.getApplicationContext().getDatabasePath(DATABASE_NAME).getAbsolutePath());


            if (t.exists() && newLoc.exists() == false) {
                Log.d("app5", "DB exists need to copy then delete");

                copy(t, newLoc);

                copy(t, t1);

                t.delete();

            }

            db = this.getWritableDatabase();
            onUpgrade(db, 4, 0);
        } catch (Exception e) {
            Log.d("app5", "Error KeptlistAdapter 46 :" + e.getMessage());
        }
    }


    public void copy(File src, File dst) throws IOException {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {

            Log.d("app5", "Copying :  " + src.toString() + "   " + dst.toString());
            Files.copy(src.toPath(), dst.toPath());
        } else {


            FileInputStream inStream = new FileInputStream(src);
            FileOutputStream outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
            inStream.close();
            outStream.close();

        }
    }


    public static KeptListAdapter getInstance(Context ctx) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (mInstance == null && ctx != null) {
            mInstance = new KeptListAdapter(ctx.getApplicationContext());
        }
        return mInstance;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        //_id INTEGER PRIMARY KEY AUTOINCREMENT
        String CREATE_BOOK_TABLE = "CREATE TABLE insta_items ( _id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT, photo TEXT, videoURL TEXT, author TEXT , videoTxt TEXT, scheduleTime LONG, isScheduled INTEGER, isNotified INTEGER)";
        // create books table
        db.execSQL(CREATE_BOOK_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (checkedTables)
            return;

        switch (oldVersion) {


            case 1:
            case 2:
            case 3:
            case 4:
                try {
                    Log.d("tag", "onUpgrade case 4");
                    db.execSQL("ALTER TABLE insta_items ADD COLUMN videoTxt TEXT  DEFAULT ''");
                    db.execSQL("ALTER TABLE insta_items ADD COLUMN  posttime INTEGER  DEFAULT 0");
                } catch (Exception e) {
                    Log.d ("tag", e.getMessage());
                }
            case 5:
                try {
                    Log.d("tag", "onUpgrade case 5  ");
                    db.execSQL("ALTER TABLE insta_items ADD COLUMN  scheduleTime LONG  DEFAULT 0");
                } catch (Exception e3) {
                }
                try {
                    db.execSQL("ALTER TABLE insta_items ADD COLUMN  isScheduled INTEGER  DEFAULT 0");
                } catch (Exception e4) {
                }
                try {
                    db.execSQL("ALTER TABLE insta_items ADD COLUMN  isNotified INTEGER  DEFAULT 0");
                } catch (Exception e5) {
                }
        }
        checkedTables = true ;
    }


    // ---------------------------------------------------------------------

    /**
     * CRUD operations (create "add", read "get", update, delete) book + get all
     * books + delete all books
     */

    // Books table name
    private static final String TABLE_ITEMS = "insta_items";

    // Books Table Columns names
    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_AUTHOR = "author";

    public static final String KEY_VIDEO = "videoURL";
    public static final String KEY_VIDEOTXT = "videoTxt";
    public static final String KEY_SCHEDULETIME = "scheduleTime";
    public static final String KEY_IS_SCHEDULED = "isScheduled";
    public static final String KEY_IS_NOTIFIED = "isNotified";

    private static final String[] COLUMNS = {KEY_ID, KEY_TITLE, KEY_PHOTO, KEY_VIDEO, KEY_AUTHOR, KEY_VIDEOTXT, KEY_SCHEDULETIME, KEY_IS_SCHEDULED, KEY_IS_NOTIFIED};
    SQLiteDatabase db;

    public void addItem(InstaItem insta_item) {

        addItem (insta_item, 0);

    }

    public long addItem(InstaItem insta_item, int i) {
        Log.d("addBook", insta_item.toString());
        // 1. get reference to writable DB
        db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, insta_item.getTitle()); // get title
        values.put(KEY_PHOTO, insta_item.getPhoto());
        values.put(KEY_AUTHOR, insta_item.getAuthor()); // get author
        values.put(KEY_VIDEO, insta_item.getVideoURL()); // get author
        values.put(KEY_SCHEDULETIME, 0);
        values.put(KEY_IS_SCHEDULED, 0);
        values.put(KEY_IS_NOTIFIED, 0);

        if (insta_item.getVideoURL().indexOf("/") != -1) {

            values.put(KEY_VIDEOTXT, "  Video  ");

        }

        // 3. insert
        long id = db.insert(TABLE_ITEMS, // table
                null, // nullColumnHack
                values); // key/value -> keys = column names/ values = column
        // values

        // 4. close
        db.close();

        return id ;
    }


    public boolean deleteAllItems() {

        SQLiteDatabase db = this.getReadableDatabase();
        int doneDelete = 0;
        doneDelete = db.delete(TABLE_ITEMS, null, null);
        return doneDelete > 0;

    }


    public InstaItem getItem(int id) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.query(TABLE_ITEMS, // a. table
                COLUMNS, // b. column names
                " _id = ?", // c. selections
                new String[]{String.valueOf(id)}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build insta_item object
        InstaItem insta_item = new InstaItem();
        insta_item.setId(Integer.parseInt(cursor != null ? cursor.getString(0) : null));
        insta_item.setTitle(cursor.getString(1));
        insta_item.setAuthor(cursor.getString(2));


        Log.d("getBook(" + id + ")", insta_item.toString());
        cursor.close();

        // 5. return insta_item
        return insta_item;
    }

    public boolean isPhotoPostedForLater(String fileName) {

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor = db.query(TABLE_ITEMS, // a. table
                COLUMNS, // b. column names
                " photo = ?", // c. selections
                new String[]{fileName}, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        // 3. if we got results get the first one
        return cursor != null;
    }


    // Get All insta_items
    public List<InstaItem> getAllItems() {
        List<InstaItem> insta_items = new LinkedList<InstaItem>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_ITEMS;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build insta_item and add it to list
        InstaItem insta_item = null;
        if (cursor.moveToFirst()) {
            do {
                insta_item = new InstaItem();
                insta_item.setId(Integer.parseInt(cursor.getString(0)));
                insta_item.setTitle(cursor.getString(1));
                insta_item.setAuthor(cursor.getString(2));
                insta_item.setAuthor(cursor.getString(2));

                // Add insta_item to insta_items
                insta_items.add(insta_item);
            } while (cursor.moveToNext());
        }

        Log.d("getAllBooks()", insta_items.toString());

        // return insta_items
        return insta_items;
    }

    public Cursor fetchAllItems() {


        try {
            SQLiteDatabase db = this.getWritableDatabase();
            Cursor mCursor = db.query(TABLE_ITEMS, new String[]{"rowid _id", KEY_TITLE, KEY_PHOTO, KEY_VIDEO, KEY_AUTHOR, KEY_VIDEOTXT, KEY_SCHEDULETIME, KEY_IS_SCHEDULED}, null, null, null, null, null, null);

            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            return mCursor;
        } catch (Exception e) {

            return null ;
        }
    }


    public int countOfScheduled() {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor mCursor = db.query(TABLE_ITEMS, new String[]{"rowid _id", KEY_TITLE, KEY_PHOTO, KEY_VIDEO, KEY_AUTHOR, KEY_VIDEOTXT, KEY_SCHEDULETIME, KEY_IS_SCHEDULED}, null, null, null, null, null, null);


        //  db.execSQL("select * FROM insta_items WHERE scheduleTime > 0 ");


        Cursor cursor = getReadableDatabase().rawQuery("select * FROM insta_items WHERE scheduleTime > 0 ", null);

        return cursor.getCount();


    }


    public ArrayList<InstaItem> fetchAllItems(Context context, ArrayList<InstaItem> list) {

        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.query(TABLE_ITEMS, new String[]{"rowid _id", KEY_TITLE, KEY_PHOTO, KEY_VIDEO, KEY_AUTHOR, KEY_VIDEOTXT, KEY_SCHEDULETIME, KEY_IS_SCHEDULED}, null, null, null, null, null, null);
        list.clear();
        String qString = "select * from " + TABLE_ITEMS;
        Cursor cursor = db.rawQuery(qString, null);
//        Toast.makeText(context, cursor.getCount() + "", Toast.LENGTH_SHORT).show();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                InstaItem instaItem = new InstaItem();
                instaItem.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                instaItem.setTitle(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_TITLE)));
                instaItem.setAuthor(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_AUTHOR)));
                instaItem.setPhoto(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_PHOTO)));
                instaItem.setVideo(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_VIDEO)));
                instaItem.setVideoURL(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_VIDEOTXT)));
                instaItem.setScheduleTime(cursor.getLong(cursor.getColumnIndex(KeptListAdapter.KEY_SCHEDULETIME)));
                instaItem.setIsScheduled(cursor.getInt(cursor.getColumnIndex(KeptListAdapter.KEY_IS_SCHEDULED)));
                instaItem.setIsNotified(cursor.getInt(cursor.getColumnIndex(KeptListAdapter.KEY_IS_NOTIFIED)));
                list.add(instaItem);
            }
            cursor.close();
        }
        this.close();
        return list;
    }

    // Updating single insta_item
    public int updateBook(InstaItem insta_item) {

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("title", insta_item.getTitle()); // get title
        values.put("author", insta_item.getAuthor()); // get author

        // 3. updating row
        int i = db.update(TABLE_ITEMS, // table
                values, // column/value
                KEY_ID + " = ?", // selections
                new String[]{String.valueOf(insta_item.getId())}); // selection
        // args

        // 4. close
        db.close();

        return i;

    }

    /**
     * // Updating single insta_item
     * public int addScheduleTime(int id, long timeMSec) {
     * <p>
     * InstaItem insta_item = getItem(id);
     * <p>
     * // 1. get reference to writable DB
     * SQLiteDatabase db = this.getWritableDatabase();
     * <p>
     * // 2. create ContentValues to add key "column"/value
     * ContentValues values = new ContentValues();
     * values.put(KEY_TITLE, insta_item.getTitle()); // get title
     * values.put(KEY_PHOTO, insta_item.getPhoto());
     * values.put(KEY_AUTHOR, insta_item.getAuthor()); // get author
     * values.put(KEY_VIDEO, insta_item.getVideoURL()); // get author
     * values.put(KEY_SCHEDULETIME, timeMSec);
     * <p>
     * if (insta_item.getVideoURL().toString().indexOf("/") != -1) {
     * <p>
     * values.put(KEY_VIDEOTXT, "  Video  ");
     * <p>
     * }
     * <p>
     * <p>
     * // 3. updating row
     * int i = db.update(TABLE_ITEMS, // table
     * values, // column/value
     * KEY_ID + " = ?", // selections
     * new String[]{String.valueOf(insta_item.getId())}); // selection
     * // args
     * <p>
     * // 4. close
     * db.close();
     * <p>
     * return i;
     * <p>
     * }
     **/


    public void remove(long id) {


        SQLiteDatabase db = this.getWritableDatabase();

        int i = db.delete(TABLE_ITEMS, // table
                KEY_ID + " = " + id, null); // selection
        db.close();


    }

    public int removeSchedule(int id) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_SCHEDULED, 0); // get title
        values.put(KEY_SCHEDULETIME, 0);
        int i = db.update(TABLE_ITEMS, // table
                values, // column/value
                KEY_ID + " = " + id, null); // selection
        db.close();
        return i;
    }

    public void getColumnNames() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor dbCursor = db.query(TABLE_ITEMS, null, null, null, null, null, null);
        String[] dbColumn = dbCursor.getColumnNames();
        for (int i = 0; i < dbColumn.length; i++) {
            Log.i("TAG", dbColumn[i]);
        }
    }


    public void updateCaption(long id, String title) {
        Log.i("TAG", "Updation ID :---->  " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, title);


        int i = db.update(TABLE_ITEMS, // table
                values, // column/value
                KEY_ID + " = " + id, null); // selection
        db.close();

        return;
    }

    public int addScheduleTime(long id, long time, String title) {
        Log.i("TAG", "Updation ID :---->  " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_SCHEDULED, 1); // get title
        values.put(KEY_SCHEDULETIME, time);
        values.put(KEY_IS_NOTIFIED, 0);
//        values.put(KEY_AUTHOR, "");
        values.put(KEY_TITLE, title);
        int i = db.update(TABLE_ITEMS, // table
                values, // column/value
                KEY_ID + " = " + id, null); // selection
        db.close();

        return i;
    }


    // Deleting single insta_item
    public void deleteItem(InstaItem insta_item) {
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. delete
        db.delete(TABLE_ITEMS, KEY_ID + " = ?", new String[]{String.valueOf(insta_item.getId())});
        // 3. close
        db.close();
        Log.d("deleteBook", insta_item.toString());

    }

    public InstaItem getNextAlert() {
        InstaItem instaItem = null;
        Calendar calendar = Calendar.getInstance();
        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.query(TABLE_ITEMS, new String[]{"rowid _id", KEY_TITLE, KEY_PHOTO, KEY_VIDEO, KEY_AUTHOR, KEY_VIDEOTXT, KEY_SCHEDULETIME, KEY_IS_SCHEDULED}, null, null, null, null, null, null);
        String qString = "select * from " + TABLE_ITEMS + " WHERE "
                + KEY_IS_SCHEDULED + " = 1 AND " + KEY_IS_NOTIFIED + " = 0 AND " + KEY_SCHEDULETIME + " > " + calendar.getTimeInMillis()
                + " ORDER BY " + KEY_SCHEDULETIME + " ASC LIMIT 1 ";
        Cursor cursor = db.rawQuery(qString, null);
        Log.i("TAG", "Next Alarm Cursor size" + cursor.getCount());
//        Toast.makeText(context, cursor.getCount() + "", Toast.LENGTH_SHORT).show();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                instaItem = new InstaItem();
                instaItem.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                instaItem.setTitle(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_TITLE)));
                instaItem.setAuthor(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_AUTHOR)));
                instaItem.setPhoto(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_PHOTO)));
                instaItem.setVideo(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_VIDEO)));
                instaItem.setVideoURL(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_VIDEOTXT)));
                instaItem.setScheduleTime(cursor.getLong(cursor.getColumnIndex(KeptListAdapter.KEY_SCHEDULETIME)));
                instaItem.setIsScheduled(cursor.getInt(cursor.getColumnIndex(KeptListAdapter.KEY_IS_SCHEDULED)));
                instaItem.setIsNotified(cursor.getInt(cursor.getColumnIndex(KeptListAdapter.KEY_IS_NOTIFIED)));

            }
            cursor.close();
        }
        this.close();
        return instaItem;
    }

    public InstaItem getAlarmDetails(int id) {
        InstaItem instaItem = null;
        Calendar calendar = Calendar.getInstance();
        SQLiteDatabase db = this.getWritableDatabase();
//        Cursor cursor = db.query(TABLE_ITEMS, new String[]{"rowid _id", KEY_TITLE, KEY_PHOTO, KEY_VIDEO, KEY_AUTHOR, KEY_VIDEOTXT, KEY_SCHEDULETIME, KEY_IS_SCHEDULED}, null, null, null, null, null, null);
        String qString = "select * from " + TABLE_ITEMS + " WHERE "
                + "_id" + " = " + id;
        Cursor cursor = db.rawQuery(qString, null);
//        Toast.makeText(context, cursor.getCount() + "", Toast.LENGTH_SHORT).show();
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                instaItem = new InstaItem();
                instaItem.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                instaItem.setTitle(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_TITLE)));
                instaItem.setAuthor(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_AUTHOR)));
                instaItem.setPhoto(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_PHOTO)));
                instaItem.setVideo(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_VIDEO)));
                instaItem.setVideoURL(cursor.getString(cursor.getColumnIndex(KeptListAdapter.KEY_VIDEOTXT)));
                instaItem.setScheduleTime(cursor.getLong(cursor.getColumnIndex(KeptListAdapter.KEY_SCHEDULETIME)));
                instaItem.setIsScheduled(cursor.getInt(cursor.getColumnIndex(KeptListAdapter.KEY_IS_SCHEDULED)));
                instaItem.setIsNotified(cursor.getInt(cursor.getColumnIndex(KeptListAdapter.KEY_IS_NOTIFIED)));
            }
            cursor.close();
        }
        this.close();
        return instaItem;
    }

    public int setNotified(int id, int Notifystatus, int ScheduleStatus) {
        Log.i("TAG", "Updation ID :---->  " + id);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_IS_NOTIFIED, Notifystatus);
        values.put(KEY_IS_SCHEDULED, ScheduleStatus);
        int i = db.update(TABLE_ITEMS, // table
                values, // column/value
                KEY_ID + " = " + id, null); // selection
        db.close();
        return i;
    }

}
