/*************************************************************************************************
 *  DetailFragment.java                                                                          *
 *                                                                                               *
 *  Allows user to enter information to create new items, edit existing items, or delete items.  *
 *                                                                                               *
 *  Author: William Brandow                                                                      *
 *************************************************************************************************/
package com.cs360.inventoryapp;

import static android.content.ContentValues.TAG;
import static java.lang.Integer.parseInt;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class DetailFragment extends Fragment {
    public static final String ITEM_NAME = "name";
    public static final String ITEM_UID = "uid";
    public static final String ITEM_DESCRIPTION = "description";
    public static final String ITEM_QUANTITY = "quantity";
    public static final String ITEM_IMAGE = "image";
    private Button mButtonSave;
    private Button mButtonDelete;
    private EditText mEditTextName;
    private EditText mEditTextUid;
    private EditText mEditTextDescription;
    private EditText mEditTextQuantity;
    private ImageView mImageView;
    private Button mButtonSelectPic;

    /***************************************************************************************
     *  Inflates view.  Gets arguments passed from ListFragment to use as default values.  *
     *  Sets onClickListeners for Save, Delete, and Select Photo buttons.                  *
     *                                                                                     *
     *  Params: LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState    *
     *  Returns: View rootView                                                             *
     ***************************************************************************************/
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
        mImageView = rootView.findViewById(R.id.imageview_detail_item_pic);
        mButtonSelectPic = rootView.findViewById(R.id.button_select_pic);

        // Call the ActivityResultLauncher to request the SEND_SMS permission
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= 33) {
                requestPermissionLauncher.launch(android.Manifest.permission.READ_MEDIA_IMAGES);
            }
            else {
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }

        // Get the clickedItem info from ListFragment
        Bundle args = getArguments();
        if (args != null) {
            String itemName = args.getString(ITEM_NAME, null);
            int itemUid = args.getInt(ITEM_UID, -1);
            String itemDescription = args.getString(ITEM_DESCRIPTION, null);
            int itemQuantity = args.getInt(ITEM_QUANTITY, -9999);
            byte[] itemImage = args.getByteArray(ITEM_IMAGE);

            mEditTextName.setText(itemName);
            mEditTextUid.setText(Integer.toString(itemUid));
            mEditTextDescription.setText(itemDescription);
            mEditTextQuantity.setText(Integer.toString(itemQuantity));

            Bitmap itemImageBitmap = BitmapFactory.decodeByteArray(itemImage, 0, itemImage.length);
            mImageView.setImageBitmap(itemImageBitmap);
        }

        // Saves information to database and navigates back to ListFragment.
        mButtonSave.setOnClickListener(view -> {
            SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
            String user = sharedPreferences.getString("username", "");
            String name = String.valueOf(mEditTextName.getText());
            String description = String.valueOf(mEditTextDescription.getText());

            Bitmap bitmap = Bitmap.createBitmap(mImageView.getWidth(), mImageView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            mImageView.draw(canvas);
            // Convert the Bitmap to a byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            byte[] image = outputStream.toByteArray();

            try {
                int uid = Integer.parseInt(String.valueOf(mEditTextUid.getText()));
                int quantity = Integer.parseInt(String.valueOf(mEditTextQuantity.getText()));

                Thread thread = new Thread(() -> {
                    ItemDatabase db = new ItemDatabase(getContext());
                    if (db.updateItem(user, name, uid, description, quantity, image)) {
                        db.close();

                        requireActivity().runOnUiThread(() -> {
                            // notify user of successful update
                            String toastMessage = name + " successfully updated!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                            toast.show();
                        });
                    } else {
                        long systemID = db.addItem(user, name, uid, description, quantity, image);
                        db.close();

                        requireActivity().runOnUiThread(() -> {
                            // notify user of successful addition
                            String toastMessage = name + " added to inventory with id " + systemID;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                            toast.show();
                        });
                    }
                });
                thread.start();

                if (quantity == 0) {
                    Item outOfStockItem = new Item(user, name, uid, description, quantity, image);
                    MainActivity.sendStockNotification(requireActivity(), requireContext(), outOfStockItem);
                }

                // navigate back to list
                Navigation.findNavController(view).navigate(R.id.list_fragment);
            }
            catch (NumberFormatException error){
                String message = "onClick: Value entered into UID or Quantity not an integer.";
                Log.e(TAG, message, error);

                // Prompt user to fix error
                String toastMessage = "UID and Quantity must both be integer values.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                toast.show();
            }
        });

        //  Deletes item with matching UID from database and navigates back to ListFragment
        mButtonDelete.setOnClickListener(view -> {
            try {
                SharedPreferences sharedPreferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
                String user = sharedPreferences.getString("username", "");
                int uid = parseInt(String.valueOf(mEditTextUid.getText()));

                Thread thread = new Thread(() -> {
                    ItemDatabase db = new ItemDatabase(getContext());
                    if (db.deleteItem(user, uid)) {
                        db.close();
                        requireActivity().runOnUiThread(() -> {
                            // notify of successful delete
                            String toastMessage = "Item with UID: " + uid + " deleted.";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                            toast.show();

                            // navigate back to list
                            Navigation.findNavController(view).navigate(R.id.list_fragment);
                        });
                    } else {
                        db.close();
                        requireActivity().runOnUiThread(() -> {
                            // notify user of no item exists
                            String toastMessage = "Item with UID: " + uid + " not found.";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                            toast.show();
                        });
                    }
                });
                thread.start();
            }
            catch (NumberFormatException error) {
                String message = "onClick: Value entered into UID not an integer.";
                Log.e(TAG, message, error);

                // Prompt user to fix error
                String toastMessage = "UID must be an integer value.";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getContext(), toastMessage, duration);
                toast.show();
            }
        });

        //  Launches ActivityResultLauncher<> mGetPhoto to select photo from device
        mButtonSelectPic.setOnClickListener(v -> {

            // Call the ActivityResultLauncher to request the SEND_SMS permission
            if ((Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) ||
                    (Build.VERSION.SDK_INT < 33 && ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                mGetPhoto.launch(intent);
            }
            else {
                String message = "Permission not granted. Update permissions in phone settings";
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {

                if (isGranted) {
                    // Permission is granted
                    Toast.makeText(getContext(), "Photo permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission is not granted
                    Toast.makeText(getContext(), "Photo permission not granted", Toast.LENGTH_SHORT).show();
                }
            });

    ActivityResultLauncher<Intent> mGetPhoto = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Uri uri = result.getData().getData();
                    saveImageToInternalStorage(uri);
                }
            });

    // Launch photo selection activity
    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


    private void saveImageToInternalStorage(Uri uri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // Save the bitmap to internal storage
            // Convert the bitmap to a byte array
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            mImageView.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }
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