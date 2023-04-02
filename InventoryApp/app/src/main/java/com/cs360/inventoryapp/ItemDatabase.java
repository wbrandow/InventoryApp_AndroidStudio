package com.cs360.inventoryapp;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ItemDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "items.db";
    private static final int VERSION = 1;

    public ItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class ItemTable {
        private static final String TABLE = "items";
        private static final String COL_ID = "_id";
        private static final String COL_NAME = "name";
        private static final String COL_UID = "uid";
        private static final String COL_DESCRIPTION = "description";
        private static final String COL_QUANTITY = "quantity";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ItemTable.TABLE + " (" +
                ItemTable.COL_ID + " integer primary key autoincrement, " +
                ItemTable.COL_NAME + " text, " +
                ItemTable.COL_UID + " integer, " +
                ItemTable.COL_DESCRIPTION + " text, " +
                ItemTable.COL_QUANTITY + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ItemTable.TABLE);
        onCreate(db);
    }

    public long addItem(String name, int uid, String description, int quantity) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_NAME, name);
        values.put(ItemTable.COL_UID, uid);
        values.put(ItemTable.COL_DESCRIPTION, description);
        values.put(ItemTable.COL_QUANTITY, quantity);

        long result = db.insert(ItemTable.TABLE, null, values);

        db.close();

        return result;
    }

    public List<Item> getAllItems() {
        SQLiteDatabase db = getReadableDatabase();
        List<Item> itemsList = new ArrayList<>();

        String sql = "select * from " + ItemTable.TABLE;
        Cursor cursor = db.rawQuery(sql, null);

        Item currentItem;
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(1);
                int uid = cursor.getInt(2);
                String description = cursor.getString(3);
                int quantity = cursor.getInt(4);

                currentItem = new Item(name, uid, description, quantity);
                itemsList.add(currentItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return itemsList;
    }

    public Item getItemByUid(int uid) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + ItemTable.TABLE + " where uid = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { Integer.toString(uid) });
        Item selectedItem = null;
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String itemName = cursor.getString(1);
                int itemUid = cursor.getInt(2);
                String itemDescription = cursor.getString(3);
                int itemQuantity = cursor.getInt(4);
                Log.d(TAG, "Item = " + id + ", " + itemName + ", " + itemUid +
                        ", " + itemDescription + ", " + itemQuantity);

                selectedItem = new Item(itemName, itemUid, itemDescription, itemQuantity);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return selectedItem;
    }

    public boolean updateItem(String name, int uid, String description, int quantity) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_NAME, name);
        values.put(ItemTable.COL_UID, uid);
        values.put(ItemTable.COL_DESCRIPTION, description);
        values.put(ItemTable.COL_QUANTITY, quantity);

        int rowsUpdated = db.update(ItemTable.TABLE, values, "uid = ?",
                new String[] { Integer.toString(uid) });

        db.close();

        return rowsUpdated > 0;
    }

    public boolean deleteItem(int uid) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(ItemTable.TABLE, ItemTable.COL_UID + " = ?",
                new String[] { Integer.toString(uid) });

        db.close();

        return rowsDeleted > 0;
    }
}
