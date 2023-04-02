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

    private Button mButtonSave;
    private Button mButtonDelete;
    private EditText mEditTextName;
    private EditText mEditTextUid;
    private EditText mEditTextDescription;
    private EditText mEditTextQuantity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        mButtonSave = rootView.findViewById(R.id.button_save);
        mButtonDelete = rootView.findViewById(R.id.button_delete);

        mEditTextName = rootView.findViewById(R.id.edittext_detail_item_name);
        mEditTextUid = rootView.findViewById(R.id.edittext_detail_item_uid);
        mEditTextDescription = rootView.findViewById(R.id.edittext_detail_item_description);
        mEditTextQuantity = rootView.findViewById(R.id.edittext_detail_item_quantity);

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

        mButtonSave.setOnClickListener(view -> {

            String toastMessage;
            String name = String.valueOf(mEditTextName.getText());
            String description = String.valueOf(mEditTextDescription.getText());
            ItemDatabase db = new ItemDatabase(getContext());

            try {
                int uid = Integer.parseInt(String.valueOf(mEditTextUid.getText()));
                int quantity = Integer.parseInt(String.valueOf(mEditTextQuantity.getText()));

                if (db.updateItem(name, uid, description, quantity)) {
                    // notify user of successful update
                    toastMessage = name + " successfully updated!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                    toast.show();
                } else {
                    long systemID = db.addItem(name, uid, description, quantity);

                    // notify user of successful addition
                    toastMessage = name + " added to inventory with id " + systemID;
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                    toast.show();
                }

                // navigate back to list
                Navigation.findNavController(view).navigate(R.id.list_fragment);
            }
            catch (NumberFormatException error){
                String message = "onClick: Value entered into UID or Quantity not an integer.";
                Log.e(TAG, message, error);

                // Prompt user to fix error
                toastMessage = "UID and Quantity must both be integer values.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                toast.show();
            }
            finally {
                db.close();
            }
        });

        mButtonDelete.setOnClickListener(view -> {

            String toastMessage;
            ItemDatabase db = new ItemDatabase(getContext());

            try {
                int uid = parseInt(String.valueOf(mEditTextUid.getText()));

                if (db.deleteItem(uid)) {
                    // notify of successful delete
                    toastMessage = "Item with UID: " + uid + " deleted.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                    toast.show();

                    // navigate back to list
                    Navigation.findNavController(view).navigate(R.id.list_fragment);
                } else {
                    // notify user of no item exists
                    toastMessage = "Item with UID: " + uid + " not found.";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                    toast.show();
                }
            }
            catch (NumberFormatException error) {
                String message = "onClick: Value entered into UID not an integer.";
                Log.e(TAG, message, error);

                // Prompt user to fix error
                toastMessage = "UID must be an integer value.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                toast.show();
            }
            finally {
                db.close();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mButtonSave.setOnClickListener(null);
        mButtonDelete.setOnClickListener(null);

        mButtonSave = null;
        mButtonDelete = null;
        mEditTextName = null;
        mEditTextUid = null;
        mEditTextDescription = null;
        mEditTextQuantity = null;
    }

}