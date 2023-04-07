package com.cs360.inventoryapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ListFragment extends Fragment {

    private FloatingActionButton mFab;
    private List<Item> mItems;
    private RecyclerView mRecyclerView;
    private DividerItemDecoration mDivider;
    private GridLayoutManager mGridLayoutManager;
    private ItemAdapter mItemAdaptor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Floating Action Button takes user to detail screen to add new item
        mFab = rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.detail_fragment));

        // FIXME: need to figure out authentication.

        ItemDatabase db = new ItemDatabase(getContext());
        mItems = db.getAllItems();
        db.close();


        // Send items to RecyclerView
        mRecyclerView = rootView.findViewById(R.id.my_recycler_view);
        mGridLayoutManager = new GridLayoutManager(getContext(),1);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mItemAdaptor = new ItemAdapter(mItems);
        mRecyclerView.setAdapter(mItemAdaptor);

        mDivider = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(mDivider);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Call the ActivityResultLauncher to request the SEND_SMS permission
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(android.Manifest.permission.SEND_SMS);
        } else {
            // Permission is already granted, send SMS message
        }
    }

    // Initialize the ActivityResultLauncher in the onViewCreated() method
    private ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Permission is granted
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("notify", true);
                    editor.apply();

                    Toast.makeText(getContext(), "SMS permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    // Permission is not granted
                    SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("notify", false);
                    editor.apply();

                    Toast.makeText(getContext(), "SMS permission not granted", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFab.setOnClickListener(null);
        mFab = null;
        mItems = null;
        mDivider = null;
        mGridLayoutManager = null;
        mItemAdaptor = null;
        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);
        mRecyclerView = null;
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        public ItemAdapter(List<Item> items) {
            mItems = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item item = mItems.get(position);
            byte[] imageData = item.getItemImage();
            Bitmap itemImageBitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
            holder.mImageViewItemPic.setImageBitmap(itemImageBitmap);
            holder.mTextViewName.setText(item.getItemName());
            holder.mTextViewUid.setText(String.valueOf(item.getItemUid()));
            holder.mTextViewDescription.setText(item.getItemDescription());
            holder.mTextViewQuantity.setText(String.valueOf(item.getItemQty()));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mImageViewItemPic;
            private TextView mTextViewName;
            private TextView mTextViewUid;
            private TextView mTextViewDescription;
            private TextView mTextViewQuantity;
            private ImageView mImageView;
            private Button mButtonDecrement;
            private Button mButtonIncrement;
            private LinearLayout mItemContents;

            public ViewHolder(View view) {
                super(view);
                mImageViewItemPic = view.findViewById(R.id.imageViewItemPic);
                mTextViewName = view.findViewById(R.id.textViewName);
                mTextViewUid = view.findViewById(R.id.textViewUid);
                mTextViewDescription = view.findViewById(R.id.textViewDescription);
                mTextViewQuantity = view.findViewById(R.id.textViewItemQuantity);
                mImageView = view.findViewById(R.id.imageview_detail_item_pic);
                mButtonDecrement = view.findViewById(R.id.buttonDecrement);
                mButtonIncrement = view.findViewById(R.id.buttonIncrement);
                mItemContents = view.findViewById(R.id.item_content);

                mButtonDecrement.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Thread thread = new Thread(() -> {
                            Item clickedItem = mItems.get(position);
                            ItemDatabase db = new ItemDatabase(getContext());
                            int uid = clickedItem.getItemUid();
                            Item item = db.getItemByUid(uid);

                            // get item info
                            String name = item.getItemName();
                            uid = item.getItemUid();
                            String description = item.getItemDescription();
                            int quantity = item.getItemQty();
                            byte[] image = item.getItemImage();

                            // decrement quantity
                            quantity -= 1;

                            if (quantity == 0) {
                                MainActivity.sendStockNotification(getActivity(), getContext(), item);
                            }

                            // update database and close it
                            db.updateItem(name, uid, description, quantity, image);
                            db.close();

                            int finalQuantity = quantity;
                            getActivity().runOnUiThread(() -> {
                                // update TextView
                                mTextViewQuantity.setText(String.valueOf(finalQuantity));
                            });
                        });
                        thread.start();
                    }
                });

                mButtonIncrement.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Thread thread = new Thread(() -> {
                            Item clickedItem = mItems.get(position);
                            ItemDatabase db = new ItemDatabase(getContext());
                            int uid = clickedItem.getItemUid();
                            Item item = db.getItemByUid(uid);

                            // get item info
                            String name = item.getItemName();
                            String description = item.getItemDescription();
                            int quantity = item.getItemQty();
                            byte[] image = item.getItemImage();

                            // decrement quantity
                            quantity += 1;

                            // update database and close it
                            db.updateItem(name, uid, description, quantity, image);
                            db.close();

                            int finalQuantity = quantity;
                            getActivity().runOnUiThread(() -> {
                                // update TextView
                                mTextViewQuantity.setText(String.valueOf(finalQuantity));
                            });
                        });
                        thread.start();
                    }
                });

                /*
                 *  When a user clicks the contents of an item pass the item's UID to the detail fragment
                 *  as an argument.
                 */
                mItemContents.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Item clickedItem = mItems.get(position);
                        ItemDatabase db = new ItemDatabase(getContext());
                        int uid = clickedItem.getItemUid();
                        Item item = db.getItemByUid(uid);

                        // close database
                        db.close();

                        // get item information
                        String itemName = item.getItemName();
                        int itemUid = item.getItemUid();
                        String itemDescription = item.getItemDescription();
                        int itemQuantity = item.getItemQty();
                        byte[] itemImage = item.getItemImage();

                        // create args to pass to DetailFragment
                        Bundle args = new Bundle();
                        args.putString(DetailFragment.ITEM_NAME, itemName);
                        args.putInt(DetailFragment.ITEM_UID, itemUid);
                        args.putString(DetailFragment.ITEM_DESCRIPTION, itemDescription);
                        args.putInt(DetailFragment.ITEM_QUANTITY, itemQuantity);
                        args.putByteArray(DetailFragment.ITEM_IMAGE, itemImage);

                        Navigation.findNavController(v).navigate(R.id.detail_fragment, args);
                    }
                });
            }
        }

        @Override
        public void onViewRecycled(@NonNull ViewHolder holder) {
            super.onViewRecycled(holder);

        }
    }
}
