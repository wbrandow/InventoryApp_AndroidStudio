package com.cs360.inventoryapp;

public class User {
    private int id;
    private String username;
    private String hashedPassword;

    public User(int id, String username, String hashedPassword) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
