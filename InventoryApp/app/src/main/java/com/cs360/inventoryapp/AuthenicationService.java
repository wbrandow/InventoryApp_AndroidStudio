package com.cs360.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import org.mindrot.jbcrypt.BCrypt;
import java.security.SecureRandom;
import java.util.List;


public class AuthenicationService {
    private static final int SALT_LENGTH = 16; // set the salt length to a suitable value

    public static String hashPassword(String password) {
        String salt = generateSalt();
        String hashedPassword = BCrypt.hashpw(password, salt);
        return hashedPassword;
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return BCrypt.gensalt(12, random) + new String(salt);
    }

    public static boolean isUsernameAvailable(Context context, String username) {
        ItemDatabase db = new ItemDatabase(context);

        List<String> usernamesList = db.getUsernames();

        boolean isAvailable = !usernamesList.contains(username);

        db.close();

        return isAvailable;
    }

}


