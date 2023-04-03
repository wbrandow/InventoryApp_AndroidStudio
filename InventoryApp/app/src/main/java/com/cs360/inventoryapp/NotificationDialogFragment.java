package com.cs360.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

public class NotificationDialogFragment extends DialogFragment {
    private Switch mSwitchStockNotification;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification_dialog, container, false);

        mSwitchStockNotification = view.findViewById(R.id.switch_stock_notification);

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        boolean notify = sharedPreferences.getBoolean("notify", false);
        mSwitchStockNotification.setChecked(notify);

        mSwitchStockNotification.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("notify", mSwitchStockNotification.isChecked());
            editor.apply();
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