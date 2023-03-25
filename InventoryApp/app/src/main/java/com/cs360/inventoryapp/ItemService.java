package com.cs360.inventoryapp;

import static android.content.ContentValues.TAG;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ItemService {
    private static ItemService itemService;
    private List<Item> items = new ArrayList<>();

    private ItemService() {
    }

    public static ItemService getItemService() {
        if (itemService == null) {
            itemService = new ItemService();
        }
        return itemService;
    }

    public int getSize() {
        return items.size();
    }

    public void addItem(Item newItem) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getItemUid() == newItem.getItemUid()) {
                String message = "addItem: Item with UID, " + String.valueOf(newItem.getItemUid())
                        + ", already exists.  Choose unique UID or use updateItem().";
                Log.i(TAG, message);
                return;
            }
        }
        items.add(newItem);
    }

    public void deleteItem(int uid) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getItemUid() == uid) {
                items.remove(i);
            }
        }
    }

    public void updateItem(String name, int uid, String description, int quantity) {
        for(int i = 0; i < items.size(); i++) {
            if (items.get(i).getItemUid() == uid) {
                items.get(i).setItemName(name);
                items.get(i).setItemDescription(description);
                items.get(i).setItemQty(quantity);
            }
        }
    }

    public Item getItem(int index) {
        try {
            return items.get(index);
        }
        catch(IndexOutOfBoundsException error) {
            String message = "getItem: No item at index = " + String.valueOf(index);
            Log.e(TAG, message, error);
            return null;
        }
    }

    public Item getItemByUid(int uid) {
        for(int i = 0; i < items.size(); i++) {
            if(items.get(i).getItemUid() == uid) {
                return items.get(i);
            }
        }
        String message = "getItemByUid: No item with UID matching " + String.valueOf(uid);
        Log.i(TAG, message);
        return null;
    }

    public List<Item> getItemList() {
        return items;
    }
}

