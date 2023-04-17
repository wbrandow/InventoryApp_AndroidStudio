package com.cs360.inventoryapp;


public class Item {
    private String user;
    private byte[] itemImage;
    private String itemName;
    private int itemUid;
    private String itemDescription;
    private int itemQty;


    public Item(String user, String name, int uid, String description, int quantity, byte[] image){
        this.user = user;
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

    public int getItemUid() {
        return itemUid;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public int getItemQty() {
        return itemQty;
    }

    public byte[] getItemImage() { return this.itemImage; }

}
