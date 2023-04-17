/********************************
 *  AuthenticationService.java  *
 *                              *
 *  Author: William Brandow     *
 ********************************/
package com.cs360.inventoryapp;

import android.content.Context;
import org.mindrot.jbcrypt.BCrypt;
import java.security.SecureRandom;
import java.util.List;


public class AuthenticationService {
    private static final int SALT_LENGTH = 16; // set the salt length to a suitable value

    /************************************
     *  Encrypts password using BCrypt  *
     *                                  *
     *  Params: String password          *
     *  Returns: String hashedPassword   *
     ************************************/
    public static String hashPassword(String password) {
        String salt = generateSalt();
        return BCrypt.hashpw(password, salt);
    }

    /****************************************************
     *  Verify password                                 *
     *                                                  *
     *  Params: String password, String hashedPassword  *
     *  Returns: Boolean: true if passwords match       *
     ****************************************************/
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    /****************************************
     *  Returns salt for use in encryption  *
     ****************************************/
    private static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return BCrypt.gensalt(12, random) + new String(salt);
    }

    /***********************************************************
     *  Checks database to see if username already exists.     *
     *                                                         *
     *  Params: Context context, String username               *
     *  Returns: Boolean - false if database contains username *
     ***********************************************************/
    public static boolean isUsernameAvailable(Context context, String username) {
        ItemDatabase db = new ItemDatabase(context);

        List<String> usernamesList = db.getUsernames();

        boolean isAvailable = !usernamesList.contains(username);

        db.close();

        return isAvailable;
    }

}


