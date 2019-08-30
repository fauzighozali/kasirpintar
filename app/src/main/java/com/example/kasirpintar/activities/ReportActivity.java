package com.example.kasirpintar.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.example.kasirpintar.R;
import com.example.kasirpintar.adapters.MainListAdapter;
import com.example.kasirpintar.adapters.ReportListAdapter;
import com.example.kasirpintar.helpers.Constant;
import com.example.kasirpintar.helpers.DatabaseHelper;
import com.example.kasirpintar.interfaces.ItemListClickListener;
import com.example.kasirpintar.model.ItemInfo;

import java.util.ArrayList;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class ReportActivity extends AppCompatActivity implements ItemListClickListener {

    ReportActivity self;
    ArrayList<ItemInfo> mItemInfoList = new ArrayList<>();
    ReportListAdapter mMainAdapter;
    DatabaseHelper mDBHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        self = this;
        ButterKnife.inject(this);

        mDBHelper = DatabaseHelper.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_report);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_left);
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.coffee_background)
                .into(mBackgroundImageView);

        mMainAdapter = new ReportListAdapter(this, mItemInfoList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mMainAdapter);
        mScrollView.setEnabled(false);

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
        Intent intent = new Intent(self, DetailReportActivity.class);
        intent.putExtra("sku", item.getItemSku());
        intent.putExtra("name", item.getItemName());
        intent.putExtra("stock", item.getItemStock());
        startActivity(intent);
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

    }
}
