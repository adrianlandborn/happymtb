package org.happymtb.unofficial.database;


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.happymtb.unofficial.fragment.KoSListFragment;
import org.happymtb.unofficial.item.KoSListItem;

public class KoSItemDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_TITLE };

    public KoSItemDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertKosItem(KoSListItem item) {
        if (item != null) {
            ContentValues values = getContentValues(item);
            return database.insert(MySQLiteHelper.TABLE_SAVED, null, values);
        }
        return -1;
    }

    public long updateKosItem(KoSListItem item) {
        if (item != null) {
            String id = String.valueOf(item.getId());
            if (checkIsDataAlreadyInDBorNot(MySQLiteHelper.COLUMN_ID, id)) {
                ContentValues values = getContentValues(item);
                return database.update(MySQLiteHelper.TABLE_SAVED, values, MySQLiteHelper.COLUMN_ID + " = " + id, null);
            }
        }
        return -1;
    }

    private ContentValues getContentValues(KoSListItem item) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_ID, item.getId());
        values.put(MySQLiteHelper.COLUMN_TITLE, item.getTitle());
        values.put(MySQLiteHelper.COLUMN_TYPE, item.getType());
        values.put(MySQLiteHelper.COLUMN_AREA, item.getArea());
        values.put(MySQLiteHelper.COLUMN_CATEGORY, item.getCategory());
        values.put(MySQLiteHelper.COLUMN_PRICE, item.getPrice().contains(KoSListFragment.NO_PRICE) ?
                null : item.getPrice());
        values.put(MySQLiteHelper.COLUMN_TIME, item.getTime());
        values.put(MySQLiteHelper.COLUMN_LINK, item.getLink());
        values.put(MySQLiteHelper.COLUMN_IMAGE_LINK, item.getImgLink());
        values.put(MySQLiteHelper.COLUMN_SOLD, item.isSold()? 1 : 0);
        return values;
    }

    public boolean deleteItem(KoSListItem item) {
        return deleteItem(item.getId());
    }

    public boolean deleteItem(long id) {
        int rowsDeleted = database.delete(MySQLiteHelper.TABLE_SAVED, MySQLiteHelper.COLUMN_ID + " = " + id, null);
        return  rowsDeleted == 1 ? true : false;}

    public boolean isItemInDatabase(long id) {
        return checkIsDataAlreadyInDBorNot(MySQLiteHelper.COLUMN_ID, String.valueOf(id));
    }

    public int setItemSold(long id, boolean isSold) {
        if (checkIsDataAlreadyInDBorNot(MySQLiteHelper.COLUMN_ID, String.valueOf(id))) {
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.COLUMN_SOLD, isSold? 1 : 0);
            return database.update(MySQLiteHelper.TABLE_SAVED, values, MySQLiteHelper.COLUMN_ID + " = " + id, null);
        } else {
            return -1;
        }
    }

    public boolean checkIsDataAlreadyInDBorNot(String coulumnName, String columnValue) {
        String Query = "Select * from " + MySQLiteHelper.TABLE_SAVED + " where " + coulumnName + " = " + columnValue;
        Cursor cursor = database.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<KoSListItem> getAllKoSItems() {
        List<KoSListItem> items = new ArrayList<KoSListItem>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_SAVED, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            KoSListItem item = getKoSItemFromCursor(cursor);
            items.add(item);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return items;
    }

    private KoSListItem getKoSItemFromCursor(Cursor cursor) {
        KoSListItem item = new KoSListItem();
        item.setId(cursor.getLong(cursor.getColumnIndex(MySQLiteHelper.COLUMN_ID)));
        item.setTitle(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TITLE)));
        item.setType(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TYPE)));
        item.setArea(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_AREA)));
        item.setCategory(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_CATEGORY)));
        item.setPrice(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_PRICE)));
        item.setTime(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_TIME)));
        item.setLink(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_LINK)));
        item.setImgLink(cursor.getString(cursor.getColumnIndex(MySQLiteHelper.COLUMN_IMAGE_LINK)));
        item.setSold(cursor.getInt(cursor.getColumnIndex(MySQLiteHelper.COLUMN_SOLD)) == 1);
        return item;
    }
}
