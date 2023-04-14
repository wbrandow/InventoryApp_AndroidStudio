package com.cs360.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class ItemDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "items.db";
    private static final int VERSION = 1;

    public ItemDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    private static final class UserTable {
        private static final String TABLE = "users";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
    }
    private static final class ItemTable {
        private static final String TABLE = "items";
        private static final String COL_ID = "_id";
        private static final String COL_USER = "user";
        private static final String COL_NAME = "name";
        private static final String COL_UID = "uid";
        private static final String COL_DESCRIPTION = "description";
        private static final String COL_QUANTITY = "quantity";
        private static final String COL_IMAGE = "image";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ItemTable.TABLE + " (" +
                ItemTable.COL_ID + " integer primary key autoincrement, " +
                ItemTable.COL_USER + " text, " +
                ItemTable.COL_NAME + " text, " +
                ItemTable.COL_UID + " integer, " +
                ItemTable.COL_DESCRIPTION + " text, " +
                ItemTable.COL_QUANTITY + " integer, " +
                ItemTable.COL_IMAGE + " blob)");

        db.execSQL("create table " + UserTable.TABLE + " (" +
                UserTable.COL_ID + " integer primary key autoincrement, " +
                UserTable.COL_USERNAME + " text, " +
                UserTable.COL_PASSWORD + " text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ItemTable.TABLE);
        db.execSQL("drop table if exists " + UserTable.TABLE);
        onCreate(db);
    }

    /***************************
     *    ItemTable methods    *
     ***************************/
    public long addItem(String user, String name, int uid, String description, int quantity, byte[] image) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_USER, user);
        values.put(ItemTable.COL_NAME, name);
        values.put(ItemTable.COL_UID, uid);
        values.put(ItemTable.COL_DESCRIPTION, description);
        values.put(ItemTable.COL_QUANTITY, quantity);
        values.put(ItemTable.COL_IMAGE, image);

        long result = db.insert(ItemTable.TABLE, null, values);

        db.close();

        return result;
    }

    public List<Item> getAllItems(String user) {
        SQLiteDatabase db = getReadableDatabase();
        List<Item> itemsList = new ArrayList<>();

        String sql = "select * from " + ItemTable.TABLE + " where user = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { user });

        Item currentItem;
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(2);
                int uid = cursor.getInt(3);
                String description = cursor.getString(4);
                int quantity = cursor.getInt(5);
                byte[] image = cursor.getBlob(6);

                currentItem = new Item(user, name, uid, description, quantity, image);
                itemsList.add(currentItem);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return itemsList;
    }

    public Item getItemByUid(String user, int uid) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + ItemTable.TABLE + " where user = ? and uid = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { user, Integer.toString(uid) });
        Item selectedItem = null;
        if (cursor.moveToFirst()) {
            do {
                long id = cursor.getLong(0);
                String itemName = cursor.getString(2);
                int itemUid = cursor.getInt(3);
                String itemDescription = cursor.getString(4);
                int itemQuantity = cursor.getInt(5);
                byte[] itemImage = cursor.getBlob(6);

                selectedItem = new Item(user, itemName, itemUid, itemDescription, itemQuantity, itemImage);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return selectedItem;
    }

    public boolean updateItem(String user, String name, int uid, String description, int quantity, byte[] image) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ItemTable.COL_USER, user);
        values.put(ItemTable.COL_NAME, name);
        values.put(ItemTable.COL_UID, uid);
        values.put(ItemTable.COL_DESCRIPTION, description);
        values.put(ItemTable.COL_QUANTITY, quantity);
        values.put(ItemTable.COL_IMAGE, image);

        int rowsUpdated = db.update(ItemTable.TABLE, values, "user = ? and uid = ?",
                new String[] { user, Integer.toString(uid) });

        db.close();

        return rowsUpdated > 0;
    }

    public boolean deleteItem(String user, int uid) {
        SQLiteDatabase db = getWritableDatabase();

        int rowsDeleted = db.delete(ItemTable.TABLE, ItemTable.COL_USER + " = ? and " + ItemTable.COL_UID + " = ?",
                new String[] { user, Integer.toString(uid) });

        db.close();

        return rowsDeleted > 0;
    }

    /***************************
     *    UserTable methods    *
     ***************************/
    public long addUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UserTable.COL_USERNAME, username);

        String hashedPassword = AuthenticationService.hashPassword(password);
        values.put(UserTable.COL_PASSWORD, hashedPassword);

        long result = db.insert(UserTable.TABLE, null, values);

        db.close();

        return result;
    }

    public List<String> getUsernames() {
        SQLiteDatabase db = getReadableDatabase();
        List<String> usernamesList = new ArrayList<>();

        String sql = "select " + UserTable.COL_USERNAME + " from " + UserTable.TABLE;
        Cursor cursor = db.rawQuery(sql, null);

        String currentUser;
        if (cursor.moveToFirst()) {
            do {
                currentUser = cursor.getString(0);

                usernamesList.add(currentUser);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return usernamesList;
    }

    public User getUser(String username) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "select * from " + UserTable.TABLE + " where username = ?";
        Cursor cursor = db.rawQuery(sql, new String[] { username });

        User selectedUser = null;
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String hashedPassword = cursor.getString(2);

                selectedUser = new User(id, username, hashedPassword);

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return selectedUser;
    }
}
