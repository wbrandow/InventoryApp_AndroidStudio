package com.cs360.inventoryapp;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

public class DetailFragment extends Fragment {
    public static final String ITEM_UID = "uid";
    private EditText mEditTextName;
    private EditText mEditTextUid;
    private EditText mEditTextDescription;
    private EditText mEditTextQuantity;

    private Button mButtonSave;
    private Button mButtonDelete;

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

        mEditTextName = view.findViewById(R.id.edittext_detail_item_name);
        mEditTextUid = view.findViewById(R.id.edittext_detail_item_uid);
        mEditTextDescription = view.findViewById(R.id.edittext_detail_item_description);
        mEditTextQuantity = view.findViewById(R.id.edittext_detail_item_quantity);

        mButtonSave = view.findViewById(R.id.button_save);
        mButtonDelete = view.findViewById(R.id.button_delete);

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            // FIXME: notify user of successful update and navigate back to list
                        }
                    }
                    if(updated == false) {
                        Item newItem = new Item(name, uid, description, quantity);
                        itemService.addItem(newItem);
                        // FIXME: notify user of successful add and navigate back to list
                    }
                }
                catch (NumberFormatException error) {
                    String message = "onClick: Value entered into UID or Quantity not an integer.";
                    Log.e(TAG, message, error);
                    // FIXME: Prompt user to fix error
                }
            }
        });

        // FIXME: add onClickListener for mButtonDelete

        return view;
    }

}