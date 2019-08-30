package com.example.kasirpintar.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.kasirpintar.R;
import com.example.kasirpintar.activities.InventoryActivity;
import com.example.kasirpintar.helpers.Constant;
import com.example.kasirpintar.helpers.DatabaseHelper;
import com.example.kasirpintar.interfaces.ItemListClickListener;
import com.example.kasirpintar.interfaces.ItemTouchHelperAdapter;
import com.example.kasirpintar.model.ItemInfo;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainListAdapter extends RecyclerView.Adapter<MainListAdapter.ItemListViewHolder> implements ItemTouchHelperAdapter {

    ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
    ItemListClickListener mListener;
    Context mContext;
    ItemListViewHolder holder;
    int currentPos;

    public MainListAdapter(Context mContext, ArrayList<ItemInfo> mItemInfoList, ItemListClickListener mListener) {
        this.mContext = mContext;
        this.mItemInfoList = mItemInfoList;
        this.mListener = mListener;
    }

    public void addData(final ArrayList<ItemInfo> mItemList) {
        this.mItemInfoList = mItemList;
        notifyDataSetChanged();
    }

    @Override
    public ItemListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_main_list_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemListViewHolder holder, int position) {
        final ItemInfo item = mItemInfoList.get(position);
        this.holder = holder;
        this.currentPos = position;

        holder.mItemName.setText(item.getItemName());

        byte[] itemImage = item.getItemImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(itemImage, 0, itemImage.length);
        holder.mItemImage.setImageBitmap(bitmap);

        holder.mItemView.setOnClickListener(v->{
            mListener.onListClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return mItemInfoList.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemSwipe(int adapterPosition, int direction) {

    }

    public class ItemListViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.layout_item_view)
        View mItemView;

        @InjectView(R.id.layout_item_content)
        View mItemContent;

        @InjectView(R.id.layout_item_name)
        TextView mItemName;

        @InjectView(R.id.layout_item_image)
        ImageView mItemImage;

        public ItemListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
