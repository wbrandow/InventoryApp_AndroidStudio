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
import android.widget.Toast;

import java.util.List;

public class DetailFragment extends Fragment {
    public static final String ITEM_NAME = "name";
    public static final String ITEM_UID = "uid";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_QUANTITY = "quantity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Button buttonSave = rootView.findViewById(R.id.button_save);
        Button buttonDelete = rootView.findViewById(R.id.button_delete);

        EditText editTextName = rootView.findViewById(R.id.edittext_detail_item_name);
        EditText editTextUid = rootView.findViewById(R.id.edittext_detail_item_uid);
        EditText editTextDescription = rootView.findViewById(R.id.edittext_detail_item_description);
        EditText editTextQuantity = rootView.findViewById(R.id.edittext_detail_item_quantity);

        // Get the clickedItem info from ListFragment
        Bundle args = getArguments();
        if (args != null) {
            String itemName = args.getString(ITEM_NAME, null);
            int itemUid = args.getInt(ITEM_UID, -1);
            String itemDescription = args.getString(ITEM_DESCRIPTION, null);
            int itemQuantity = args.getInt(ITEM_QUANTITY, -9999);

            editTextName.setText(itemName);
            editTextUid.setText(Integer.toString(itemUid));
            editTextDescription.setText(itemDescription);
            editTextQuantity.setText(Integer.toString(itemQuantity));
        }

        buttonSave.setOnClickListener(view -> {
            String toastMessage;
            String name = String.valueOf(editTextName.getText());
            String description = String.valueOf(editTextDescription.getText());
            try {
                int uid = Integer.parseInt(String.valueOf(editTextUid.getText()));
                int quantity = Integer.parseInt(String.valueOf(editTextQuantity.getText()));

                ItemService itemService = ItemService.getItemService();
                List<Item> items = itemService.getItemList();

                boolean updated = false;
                for (int i = 0; i < items.size(); i++) {
                    if (uid == items.get(i).getItemUid()) {
                        itemService.updateItem(name, uid, description, quantity);
                        updated = true;

                        // notify user of successful update
                        toastMessage = name + " successfully updated!";
                        Context context = getContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, toastMessage, duration);
                        toast.show();

                        // navigate back to list
                        Navigation.findNavController(view).navigate(R.id.list_fragment);
                    }
                }
                if (!updated) {
                    Item newItem = new Item(name, uid, description, quantity);
                    itemService.addItem(newItem);

                    // notify user of successful addition
                    toastMessage = name + " added to inventory!";
                    Context context = getContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, toastMessage, duration);
                    toast.show();

                    // navigate back to list
                    Navigation.findNavController(view).navigate(R.id.list_fragment);
                }
            } catch (NumberFormatException error) {
                String message = "onClick: Value entered into UID or Quantity not an integer.";
                Log.e(TAG, message, error);

                // Prompt user to fix error
                toastMessage = "UID and Quantity must both be integer values.";
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, toastMessage, duration);
                toast.show();
            }
        });

        buttonDelete.setOnClickListener(view -> {
            String toastMessage;

            try {
                int uid = parseInt(String.valueOf(editTextUid.getText()));

                ItemService itemService = ItemService.getItemService();
                List<Item> items = itemService.getItemList();

                boolean deleted = false;
                for(int i = 0; i < items.size(); i++) {
                    if(uid == items.get(i).getItemUid()) {
                        itemService.deleteItem(uid);
                        deleted = true;

                        // notify of successful delete
                        toastMessage = "Item with UID: " + uid + " deleted.";
                        Context context = getContext();
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, toastMessage, duration);
                        toast.show();

                        // navigate back to list
                        Navigation.findNavController(view).navigate(R.id.list_fragment);
                    }
                }
                if(!deleted) {
                    // notify user of no item exists
                    toastMessage = "Item with UID: " + uid + " not found.";
                    Context context = getContext();
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, toastMessage, duration);
                    toast.show();
                }
            }
            catch (NumberFormatException error) {
                String message = "onClick: Value entered into UID or Quantity not an integer.";
                Log.e(TAG, message, error);

                // Prompt user to fix error
                toastMessage = "UID must be an integer value.";
                Context context = getContext();
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, toastMessage, duration);
                toast.show();
            }
        });

        return rootView;
    }

}