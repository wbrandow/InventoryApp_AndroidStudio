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

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);

        // FIXME: get list from database. probably do this in login fragment
        ItemService itemService = ItemService.getItemService();
        itemService.addItem(new Item());
        List<Item> items = itemService.getItemList();


        // Send bands to RecyclerView
        RecyclerView recyclerView = rootView.findViewById(R.id.my_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        recyclerView.setAdapter(new ItemAdapter(items));

        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(divider);

        return rootView;
    }
    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        private List<Item> mItems;

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
            public ImageView mImageViewItemPic;
            public TextView mTextViewName;
            public TextView mTextViewUid;
            public TextView mTextViewDescription;
            public TextView mTextViewQuantity;
            public Button mButtonDecrement;
            public Button mButtonIncrement;

            public LinearLayout mItemContents;

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

                            int itemQty = clickedItem.getItemQty();
                            itemQty -= 1;
                            clickedItem.setItemQty(itemQty);
                            mTextViewQuantity.setText(String.valueOf(itemQty));
                        }
                    }
                });

                mButtonIncrement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Item clickedItem = mItems.get(position);

                            int itemQty = clickedItem.getItemQty();
                            itemQty += 1;
                            clickedItem.setItemQty(itemQty);
                            mTextViewQuantity.setText(String.valueOf(itemQty));
                        }
                    }
                });

                /*
                 *  When a user clicks the contents of an item pass the item's UID to the detail fragment
                 *  as an argument.
                 */
                mItemContents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            Item clickedItem = mItems.get(position);

                            int itemUid = clickedItem.getItemUid();
                            Bundle args = new Bundle();
                            args.putInt(DetailFragment.ITEM_UID, itemUid);
                            Navigation.findNavController(view).navigate(R.id.detail_fragment, args);
                        }
                    }
                });
            }
        }
    }
}