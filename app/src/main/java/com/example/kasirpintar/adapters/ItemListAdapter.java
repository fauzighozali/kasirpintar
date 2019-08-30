package com.example.kasirpintar.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ItemListViewHolder> implements ItemTouchHelperAdapter {

    MaterialDialog mMaterialDialog;
    ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
    ItemListClickListener mListener;
    Context mContext;
    ItemListViewHolder holder;
    int currentPos;
    int sum;

    public ItemListAdapter(Context mContext, ArrayList<ItemInfo> mItemInfoList, ItemListClickListener mListener) {
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
        return new ItemListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.activity_item_list_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemListViewHolder holder, int position) {
        final ItemInfo item = mItemInfoList.get(position);
        this.holder = holder;
        this.currentPos = position;

        holder.mItemName.setText(item.getItemName());
        holder.mItemStock.setText(item.getItemStock() + Constant.UNITS);

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

    public int getSum() {
        sum = 0;
        for (int i = 0; i < mItemInfoList.size(); i++) {
            sum += (mItemInfoList.get(i).getItemStock());
        }
        return sum;
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {

    }

    @Override
    public void onItemSwipe(int adapterPosition, int direction) {
        if (direction == ItemTouchHelper.END) {
            ((InventoryActivity) mContext).addMenuItem(mItemInfoList.get(adapterPosition), adapterPosition);
        } else if (direction == ItemTouchHelper.START) {
            mMaterialDialog = new MaterialDialog.Builder(mContext)
                    .title("Delete ?")
                    .content("Do you want to delete this item ?")
                    .positiveColor(mContext.getResources().getColor(R.color.colorRed))
                    .negativeColor(mContext.getResources().getColor(R.color.colorGray))
                    .negativeText("Cancel")
                    .positiveText("Delete")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            removeItem(adapterPosition);
                            mListener.itemRemoved();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            updateItem(adapterPosition);
                            dialog.dismiss();
                        }
                    })
                    .cancelable(false)
                    .show();
        }
    }

    private void removeItem(int position) {
        // Remove from Database
        DatabaseHelper.getInstance(mContext).deleteItems(mItemInfoList.get(position).getItemSku());

        // Remove from List
        mItemInfoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mItemInfoList.size());
    }

    public void updateItem(int position) {
        notifyItemChanged(position);
    }

    public class ItemListViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.layout_item_view)
        View mItemView;

        @InjectView(R.id.layout_item_content)
        View mItemContent;

        @InjectView(R.id.layout_item_name)
        TextView mItemName;

        @InjectView(R.id.layout_item_stock)
        TextView mItemStock;

        @InjectView(R.id.layout_item_image)
        ImageView mItemImage;

        public ItemListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }
    }
}
