package com.cs360.inventoryapp;

import android.os.Bundle;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class ListFragment extends Fragment {

    private FloatingActionButton mFab;
    private List<Item> mItems;
    private RecyclerView mRecyclerView;
    private DividerItemDecoration mDivider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // Floating Action Button takes user to detail screen to add new item
        mFab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        mFab.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.detail_fragment);
        });

        // FIXME: need to figure out authentication.
        ItemDatabase db = new ItemDatabase(getContext());
        mItems = db.getAllItems();
        db.close();

        // Send items to RecyclerView
        mRecyclerView = rootView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        mRecyclerView.setAdapter(new ItemAdapter(mItems));

        mDivider = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        mRecyclerView.addItemDecoration(mDivider);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFab = null;
        mItems = null;
        mDivider = null;
        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);
        mRecyclerView = null;
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

        public ItemAdapter(List<Item> items) {
            mItems = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Item item = mItems.get(position);
            holder.mImageViewItemPic.setImageResource(R.drawable.camera_pic);
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
            private Button mButtonDecrement;
            private Button mButtonIncrement;

            private LinearLayout mItemContents;

            public ViewHolder(View view) {
                super(view);
                mImageViewItemPic = (ImageView) view.findViewById(R.id.imageViewItemPic);
                mTextViewName = (TextView) view.findViewById(R.id.textViewName);
                mTextViewUid = (TextView) view.findViewById(R.id.textViewUid);
                mTextViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
                mTextViewQuantity = (TextView) view.findViewById(R.id.textViewItemQuantity);
                mButtonDecrement = (Button) view.findViewById(R.id.buttonDecrement);
                mButtonIncrement = (Button) view.findViewById(R.id.buttonIncrement);
                mItemContents = (LinearLayout) view.findViewById(R.id.item_content);

                mButtonDecrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Item clickedItem = mItems.get(position);
                            ItemDatabase db = new ItemDatabase(getContext());
                            int uid = clickedItem.getItemUid();
                            Item item = db.getItemByUid(uid);

                            // get item info
                            String name = item.getItemName();
                            uid = item.getItemUid();
                            String description = item.getItemDescription();
                            int quantity = item.getItemQty();

                            // decrement quantity
                            quantity -= 1;

                            // update database and close it
                            db.updateItem(name, uid, description, quantity);
                            db.close();

                            // update TextView
                            mTextViewQuantity.setText(String.valueOf(quantity));
                        }
                    }
                });

                mButtonIncrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Item clickedItem = mItems.get(position);
                            ItemDatabase db = new ItemDatabase(getContext());
                            int uid = clickedItem.getItemUid();
                            Item item = db.getItemByUid(uid);

                            // get item info
                            String name = item.getItemName();
                            uid = item.getItemUid();
                            String description = item.getItemDescription();
                            int quantity = item.getItemQty();

                            // increment quantity
                            quantity += 1;

                            // update database and close it
                            db.updateItem(name, uid, description, quantity);
                            db.close();

                            // update TextView
                            mTextViewQuantity.setText(String.valueOf(quantity));
                        }
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

                        // create args to pass to DetailFragment
                        Bundle args = new Bundle();
                        args.putString(DetailFragment.ITEM_NAME, itemName);
                        args.putInt(DetailFragment.ITEM_UID, itemUid);
                        args.putString(DetailFragment.ITEM_DESCRIPTION, itemDescription);
                        args.putInt(DetailFragment.ITEM_QUANTITY, itemQuantity);

                        Navigation.findNavController(v).navigate(R.id.detail_fragment, args);
                    }
                });
            }
        }
    }
}
