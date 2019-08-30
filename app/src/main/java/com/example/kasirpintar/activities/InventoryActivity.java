package com.example.kasirpintar.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.kasirpintar.R;
import com.example.kasirpintar.helpers.Constant;
import com.example.kasirpintar.helpers.DatabaseHelper;
import com.example.kasirpintar.adapters.ItemListAdapter;
import com.example.kasirpintar.helpers.SimpleItemTouchHelper;
import com.example.kasirpintar.interfaces.ItemListClickListener;
import com.example.kasirpintar.model.ItemInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class InventoryActivity extends AppCompatActivity implements ItemListClickListener {

    InventoryActivity self;
    ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
    ItemListAdapter mAdapter;
    MaterialNumberPicker numberPicker;
    DatabaseHelper mDBHelper;
    MaterialDialog mDialog;
    View mDialogView;
    EditText mItemName, mItemStock;
    ImageView mItemImage;
    boolean flagOk;

    final int REQUEST_CODE_GALLERY = 999;

    @InjectView(R.id.activity_main_toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.main_scroll_view)
    ScrollView mScrollView;

    @InjectView(R.id.main_background)
    ImageView mBackgroundImageView;

    @InjectView(R.id.main_error)
    View mErrorView;

    @InjectView(R.id.main_recycler_view)
    RecyclerView mRecyclerView;

    @InjectView(R.id.main_total_sum_view)
    TextView mTotalSumView;

    @InjectView(R.id.main_fab)
    FloatingActionButton fabButton;

    public void addMenuItem(final ItemInfo info, final int pos) {
        flagOk = false;
        String title;
        if (info == null) {
            title = "Add an Item";
        } else {
            title = "Edit an Item";
        }

        mDialog = new MaterialDialog.Builder(self)
                .title(title)
                .customView(R.layout.layout_add_menu, true)
                .positiveColor(getResources().getColor(R.color.colorTurquoise))
                .negativeColor(getResources().getColor(R.color.colorGray))
                .positiveText("Save")
                .negativeText("Cancel")
                .cancelable(false)
                .autoDismiss(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        mDialogView = dialog.getView();
                        mItemName = (EditText) mDialogView.findViewById(R.id.layout_add_menu_name);
                        mItemStock = (EditText) mDialogView.findViewById(R.id.layout_add_menu_stock);
                        mItemImage = (ImageView) mDialogView.findViewById(R.id.layout_add_menu_photo);

                        if (mItemName.getText().length() > 1) {
                            flagOk = true;
                        } else {
                            flagOk = false;
                            mItemName.setError("Required");
                        }

                        if (mItemStock.length() > 1 ) {
                            flagOk = true;
                        } else {
                            flagOk = false;
                            mItemStock.setError("Required");
                        }

                        if (mItemImage.getDrawable() != null) {
                            flagOk =true;
                        } else {
                            flagOk = false;
                            Toast.makeText(getApplicationContext(), "Image not empty!", Toast.LENGTH_SHORT).show();
                        }

                        if (flagOk) {
                            if (info == null) {
                                mDBHelper.addItems(
                                        mItemName.getText().toString(),
                                        Integer.parseInt(mItemStock.getText().toString()),
                                        imageViewToByte(mItemImage)
                                );
                            } else {
                                mDBHelper.updateItems(
                                        info.getItemSku(),
                                        mItemName.getText().toString(),
                                        Integer.parseInt(mItemStock.getText().toString()),
                                        imageViewToByte(mItemImage)
                                );
                            }
                            requestData();
                            dialog.dismiss();
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (info != null) {
                            mAdapter.updateItem(pos);
                        }
                        dialog.dismiss();
                    }
                }).build();

        mItemImage = (ImageView) mDialog.getCustomView().findViewById(R.id.layout_add_menu_photo);
        mItemImage.setOnClickListener(view -> {
            ActivityCompat.requestPermissions(
                    InventoryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_GALLERY
            );
        });

        if (info != null) {
            EditText name = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_menu_name);
            name.setText(info.getItemName() + "");

            EditText stock = (EditText) mDialog.getCustomView().findViewById(R.id.layout_add_menu_stock);
            stock.setText(info.getItemStock() + "");

            ImageView image = (ImageView) mDialog.getCustomView().findViewById(R.id.layout_add_menu_photo);
            byte[] itemImage = info.getItemImage();
            Bitmap bitmap = BitmapFactory.decodeByteArray(itemImage, 0, itemImage.length);
            image.setImageBitmap(bitmap);
        }
        mDialog.show();
    }

    private byte[] imageViewToByte(ImageView image) {
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GALLERY) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, REQUEST_CODE_GALLERY);
            }else {
                Toast.makeText(getApplicationContext(), "You don't have permission to access file location!", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                ImageView mItemPhoto = (ImageView) mDialog.getCustomView().findViewById(R.id.layout_add_menu_photo);
                mItemPhoto.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        self = this;
        ButterKnife.inject(this);

        mDBHelper = DatabaseHelper.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_inventory);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_left);
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.coffee_background)
                .into(mBackgroundImageView);

        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMenuItem(null, -1);
            }
        });

        mAdapter = new ItemListAdapter(this, mItemInfoList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);
        mScrollView.setEnabled(false);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fabButton.hide();
                }else {
                    fabButton.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelper(this, mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        requestData();
    }

    private void requestData() {
        if (mDBHelper.getItems().size() <= 0) {
//            mDBHelper.addItems("Espresso", 160);
//            mDBHelper.addItems("Regular Coffee", 160);
//            mDBHelper.addItems("Americano", 160);
//            mDBHelper.addItems("Cappuccino", 160);
//            mDBHelper.addItems("Cafe Latte", 270);
        }

        if (mDBHelper.getItems().size() <= 0) {
            mErrorView.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);

            updateSum();
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);

            updateSum();
        }

        mItemInfoList.clear();
        mItemInfoList.addAll(mDBHelper.getItems());
        mAdapter.addData(mItemInfoList);
    }

    @Override
    public void onListClick(ItemInfo item) {
        numberPicker = new MaterialNumberPicker.Builder(self)
                .minValue(1)
                .maxValue(10)
                .defaultValue(1)
                .backgroundColor(Color.TRANSPARENT)
                .separatorColor(Color.TRANSPARENT)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        new MaterialDialog.Builder(self)
                .title(item.getItemName())
                .customView(numberPicker, true)
                .positiveColor(getResources().getColor(R.color.colorTurquoise))
                .negativeColor(getResources().getColor(R.color.colorPrimaryDark))
                .positiveText("Ok")
                .negativeText("Cancel")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        MaterialDialog mMaterialDialog = new MaterialDialog.Builder(self)
                                .title("Total")
                                .customView(R.layout.layout_popup_inventory, true)
                                .autoDismiss(false)
                                .positiveColor(getResources().getColor(R.color.colorTurquoise))
                                .negativeColor(getResources().getColor(R.color.colorPrimaryDark))
                                .positiveText("Add Stock")
                                .negativeText("Sold Stock")
                                .cancelable(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        DatabaseHelper.getInstance(self).updateItems(
                                                item.getItemSku(),
                                                item.getItemName(),
                                                item.getItemStock() + numberPicker.getValue(),
                                                item.getItemImage());

                                        requestData();
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        DatabaseHelper.getInstance(self).updateItems(
                                                item.getItemSku(),
                                                item.getItemName(),
                                                item.getItemStock() - numberPicker.getValue(),
                                                item.getItemImage());

                                        requestData();
                                        dialog.dismiss();
                                    }
                                })
                                .build();

                        TextView mPopupName = (TextView) mMaterialDialog.getCustomView().findViewById(R.id.layout_popup_inventory_name);
                        mPopupName.setText(item.getItemName());

                        TextView mPopupStock = (TextView) mMaterialDialog.getCustomView().findViewById(R.id.layout_popup_inventory_stock);
                        mPopupStock.setText(numberPicker.getValue() + Constant.UNITS);

                        mMaterialDialog.show();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void itemRemoved() {
        updateSum();
    }

    private void updateSum() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mAdapter.getSum() != 0) {
                    ValueAnimator animator = new ValueAnimator();
                    animator.setObjectValues(0, mAdapter.getSum());
                    animator.setDuration(2500);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            mTotalSumView.setText((int) animation.getAnimatedValue() + Constant.UNITS);
                        }
                    });
                    animator.start();
                }else {
                    mTotalSumView.setText(0 + Constant.UNITS);
                }
            }
        }, 500);
    }
}
