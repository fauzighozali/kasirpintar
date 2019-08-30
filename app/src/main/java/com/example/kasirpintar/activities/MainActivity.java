package com.example.kasirpintar.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.kasirpintar.R;
import com.example.kasirpintar.adapters.ItemListAdapter;
import com.example.kasirpintar.adapters.MainListAdapter;
import com.example.kasirpintar.helpers.Constant;
import com.example.kasirpintar.helpers.DatabaseHelper;
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

public class MainActivity extends AppCompatActivity implements ItemListClickListener {

    MainActivity self;
    ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
    MainListAdapter mMainAdapter;
    MaterialNumberPicker numberPicker;
    DatabaseHelper mDBHelper;

    @InjectView(R.id.activity_main_toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.main_scroll_view)
    ScrollView mScrollView;

    @InjectView(R.id.main_background)
    ImageView mBackgroundImageView;

    @InjectView(R.id.main_error)
    View mErrorView;

    @InjectView(R.id.swipeContainer)
    SwipeRefreshLayout swipeContainer;

    @InjectView(R.id.main_recycler_view)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        self = this;
        ButterKnife.inject(this);

        mDBHelper = DatabaseHelper.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_menu);
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.coffee_background)
                .into(mBackgroundImageView);

        mMainAdapter = new MainListAdapter(this, mItemInfoList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMainAdapter);
        mScrollView.setEnabled(false);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeContainer.setRefreshing(false);
                    }
                }, 2500);
            }
        });

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

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
        } else {
            mErrorView.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }

        mItemInfoList.clear();
        mItemInfoList.addAll(mDBHelper.getItems());
        mMainAdapter.addData(mItemInfoList);
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
                                .positiveText("Sold")
                                .negativeText("Cancel")
                                .cancelable(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        DatabaseHelper.getInstance(self).updateItems(
                                                item.getItemSku(),
                                                item.getItemName(),
                                                item.getItemStock() - numberPicker.getValue(),
                                                item.getItemImage());
                                        Toast.makeText(getApplicationContext(), "Stock " + item.getItemName() + " : " + (item.getItemStock() - numberPicker.getValue()) + " Units", Toast.LENGTH_SHORT).show();

                                        requestData();
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_main_inventory:
                startActivity(new Intent(self, InventoryActivity.class));
                return true;
            case R.id.menu_main_report:
                startActivity(new Intent(self, ReportActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void itemRemoved() {

    }
}
