/*
 *  User.java
 *
 *  Stores
 */

package com.cs360.inventoryapp;

public class User {
    private int id;
    private String username;
    private String hashedPassword;

    private int notifications;

    private String phoneNumber;

    public User(int id, String username, String hashedPassword, int notifications, String phoneNumber) {
        this.id = id;
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.notifications = notifications;
        this.phoneNumber = phoneNumber;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public int getNotifications() { return notifications; }

    public String getUsername() { return username; }

    public String getPhoneNumber() { return phoneNumber; }
}
