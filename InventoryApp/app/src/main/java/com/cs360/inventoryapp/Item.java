package com.cs360.inventoryapp;

import java.sql.Blob;

public class Item {
    private byte[] itemImage;
    private String itemName;
    private int itemUid;
    private String itemDescription;
    private int itemQty;

    // test constructor
    public Item(){
        this.itemName = "Test Name";
        this.itemUid = 0;
        this.itemDescription = "This is a test description.";
        this.itemQty = 420;
        this.itemImage = null;
    }

    public Item(String name, int uid, String description, int quantity, byte[] image){
        this.itemName = name;
        this.itemUid = uid;
        this.itemDescription = description;
        this.itemQty = quantity;
        this.itemImage = image;
    }

    // setters and getters
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemUid() {
        return itemUid;
    }

    public void setItemUid(int itemUid) {
        this.itemUid = itemUid;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public int getItemQty() {
        return itemQty;
    }

    public void setItemQty(int itemQty) {
        this.itemQty = itemQty;
    }

    public void setItemImage(byte[] image) { this.itemImage = image; }

    public byte[] getItemImage() { return this.itemImage; }

}
