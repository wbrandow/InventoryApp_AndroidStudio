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
            Navigation.findNavController(findViewById(R.id.nav_host_fragment))
                    .navigate(R.id.notification_dialog_fragment);
        }
        return true;
    }

    public static void sendStockNotification(Activity activity, Context context, Item item) {
        // FIXME: user should enter phone number
        String phoneNumber = "5555215556";
        String itemName = item.getItemName();
        String message = itemName + " is out of stock!";

        SharedPreferences sharedPreferences = activity.getPreferences(Context.MODE_PRIVATE);
        boolean notify = sharedPreferences.getBoolean("notify", false);

        if (notify) {
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