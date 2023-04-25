package com.cs360.inventoryapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.navigation.Navigation;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.notifications) {
            SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            if (!username.isEmpty()) {
                Navigation.findNavController(findViewById(R.id.nav_host_fragment))
                        .navigate(R.id.notification_dialog_fragment);
            }
            else {
                Toast.makeText(getApplicationContext(), "Please log in first!", Toast.LENGTH_SHORT).show();
            }
        } else if (menuItem.getItemId() == R.id.logout) {
            SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("username", "");

            if (!username.isEmpty()) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", "");
                editor.apply();

                Navigation.findNavController(findViewById(R.id.nav_host_fragment))
                        .navigate(R.id.login_fragment);
            }
            else {
                Toast.makeText(getApplicationContext(), "Please log in first!", Toast.LENGTH_SHORT).show();
            }
        }
        return true;
    }

    public static void sendStockNotification(Activity activity, Context context, Item item) {
        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "");

        ItemDatabase db = new ItemDatabase(context);
        User user = db.getUser(username);
        String phoneNumber = user.getPhoneNumber();
        String itemName = item.getItemName();
        String message = itemName + " is out of stock!";

        db.close();

        if (user.getNotifications() == 1) {
            // notification preference is true
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                // SMS permission granted
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
            }

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }


    }
}