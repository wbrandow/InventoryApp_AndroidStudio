package com.cs360.inventoryapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailFragment extends Fragment {
    public static final String ITEM_UID = "uid";

    private Item mItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        int itemUid = 0;

        // Get the band ID from the fragment arguments
        Bundle args = getArguments();
        if (args != null) {
            itemUid = args.getInt(ITEM_UID);
        }

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        return view;
    }
}