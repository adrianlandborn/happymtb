package org.happymtb.unofficial.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_SAVED = "saved";

    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_AREA = "area";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_IMAGE_LINK = "image_link";
    public static final String COLUMN_SOLD = "sold";

    public static final String[] ALL_COLUMNS = { MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_TITLE, MySQLiteHelper.COLUMN_PRICE,
            MySQLiteHelper.COLUMN_AREA, MySQLiteHelper.COLUMN_CATEGORY, MySQLiteHelper.COLUMN_TIME, MySQLiteHelper.COLUMN_LINK,
            MySQLiteHelper.COLUMN_IMAGE_LINK, MySQLiteHelper.COLUMN_SOLD };

    private static final String DATABASE_NAME = "happy.db";
    private static final int DATABASE_VERSION = 5;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_SAVED + "(" + COLUMN_ID
            + " integer primary key, "
            + COLUMN_TITLE + " text, "
            + COLUMN_TYPE + " text, "
            + COLUMN_CATEGORY + " text, "
            + COLUMN_AREA + " text, "
            + COLUMN_TIME + " text, "
            + COLUMN_PRICE + " text, "
            + COLUMN_LINK + " text, "
            + COLUMN_IMAGE_LINK + " text, "
            + COLUMN_SOLD + " integer);";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SAVED);
        onCreate(db);
    }
}