package com.cs360.inventoryapp;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class DetailFragment extends Fragment {
    public static final String ITEM_NAME = "name";
    public static final String ITEM_UID = "uid";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_QUANTITY = "quantity";
    private EditText mEditTextName;
    private EditText mEditTextUid;
    private EditText mEditTextDescription;
    private EditText mEditTextQuantity;

    private Button mButtonSave;
    private Button mButtonDelete;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mButtonSave = view.findViewById(R.id.button_save);
        mButtonDelete = view.findViewById(R.id.button_delete);

        mEditTextName = view.findViewById(R.id.edittext_detail_item_name);
        mEditTextUid = view.findViewById(R.id.edittext_detail_item_uid);
        mEditTextDescription = view.findViewById(R.id.edittext_detail_item_description);
        mEditTextQuantity = view.findViewById(R.id.edittext_detail_item_quantity);

        // Get the clickedItem info from ListFragment
        Bundle args = getArguments();
        if (args != null) {
            String itemName = args.getString(ITEM_NAME, null);
            int itemUid = args.getInt(ITEM_UID, -1);
            String itemDescription = args.getString(ITEM_DESCRIPTION, null);
            int itemQuantity = args.getInt(ITEM_QUANTITY, -9999);

            mEditTextName.setText(itemName);
            mEditTextUid.setText(Integer.toString(itemUid));
            mEditTextDescription.setText(itemDescription);
            mEditTextQuantity.setText(Integer.toString(itemQuantity));
        }

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notification;
                String name = String.valueOf(mEditTextName.getText());
                String description = String.valueOf(mEditTextDescription.getText());
                try {
                    int uid = Integer.parseInt(String.valueOf(mEditTextUid.getText()));
                    int quantity = Integer.parseInt(String.valueOf(mEditTextQuantity.getText()));

                    ItemService itemService = ItemService.getItemService();
                    List<Item> items = itemService.getItemList();

                    boolean updated = false;
                    for(int i = 0; i < items.size(); i++) {
                        if(uid == items.get(i).getItemUid()) {
                            itemService.updateItem(name, uid, description, quantity);
                            updated = true;

                            // notify user of successful update
                            notification = name + " sucessfully updated!";
                            Context context = getContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, notification, duration);
                            toast.show();

                            // navigate back to list
                            Navigation.findNavController(view).navigate(R.id.list_fragment);
                        }
                    }
                    if(updated == false) {
                        Item newItem = new Item(name, uid, description, quantity);
                        itemService.addItem(newItem);

                        // notify user of successful addition
                        notification = name + " added to inventory!";
                        Context context = getContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, notification, duration);
                        toast.show();

                        // navigate back to list
                        Navigation.findNavController(view).navigate(R.id.list_fragment);
                    }
                }
                catch (NumberFormatException error) {
                    String message = "onClick: Value entered into UID or Quantity not an integer.";
                    Log.e(TAG, message, error);

                    // Prompt user to fix error
                    notification = "UID and Quantity must both be integer values.";
                    Context context = getContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, notification, duration);
                    toast.show();
                }
            }
        });

        mButtonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String notification;

                try {
                    int uid = parseInt(String.valueOf(mEditTextUid.getText()));

                    ItemService itemService = ItemService.getItemService();
                    List<Item> items = itemService.getItemList();

                    boolean deleted = false;
                    for(int i = 0; i < items.size(); i++) {
                        if(uid == items.get(i).getItemUid()) {
                            itemService.deleteItem(uid);
                            deleted = true;

                            // notify of successful delete
                            notification = "Item with UID: " + String.valueOf(uid) + " deleted.";
                            Context context = getContext();
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, notification, duration);
                            toast.show();

                            // navigate back to list
                            Navigation.findNavController(view).navigate(R.id.list_fragment);
                        }
                    }
                    if(deleted == false) {
                        // notify user of no item exists
                        notification = "Item with UID: " + String.valueOf(uid) + " not found.";
                        Context context = getContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, notification, duration);
                        toast.show();
                    }
                }
                catch (NumberFormatException error) {
                    String message = "onClick: Value entered into UID or Quantity not an integer.";
                    Log.e(TAG, message, error);

                    // Prompt user to fix error
                    notification = "UID must be an integer value.";
                    Context context = getContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, notification, duration);
                    toast.show();
                }
            }
        });

        return view;
    }

}