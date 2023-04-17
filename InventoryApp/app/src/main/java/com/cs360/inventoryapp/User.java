package com.cs360.inventoryapp;

public class User {
    private int id;
    private String username;
    private String hashedPassword;

    private int notifications;

    public User(int id, String username, String hashedPassword, int notifications) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.notifications = notifications;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public int getNotifications() { return notifications; }

    public String getUsername() { return username; }
}
