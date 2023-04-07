package com.cs360.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

public class NotificationDialogFragment extends DialogFragment {
    private Switch mSwitchStockNotification;
    private Button mButtonDone;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_dialog, container, false);

        mSwitchStockNotification = view.findViewById(R.id.switch_stock_notification);
        mButtonDone = view.findViewById(R.id.button_done);

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean notify = sharedPreferences.getBoolean("notify", false);
        mSwitchStockNotification.setChecked(notify);

        mSwitchStockNotification.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notify", mSwitchStockNotification.isChecked());
            editor.apply();

            // Check SMS permission and notify if not granted
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "SMS permission not granted to InventoryApp!",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mButtonDone.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.popBackStack();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mSwitchStockNotification.setOnClickListener(null);
        mSwitchStockNotification = null;
    }
}