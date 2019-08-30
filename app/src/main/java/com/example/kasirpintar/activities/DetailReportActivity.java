package com.example.kasirpintar.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.kasirpintar.GlobalApplication;
import com.example.kasirpintar.R;
import com.example.kasirpintar.helpers.Constant;
import com.example.kasirpintar.helpers.DatabaseHelper;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class DetailReportActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    DetailReportActivity self;
    DatabaseHelper mDBHelper;

    @InjectView(R.id.activity_report_toolbar)
    Toolbar mToolbar;

    @InjectView(R.id.activity_report_background)
    ImageView mBackgroundImageView;

    @InjectView(R.id.activity_report_from_date)
    EditText mFromDateEditText;

    @InjectView(R.id.activity_report_to_date)
    EditText mToDateEditText;

    @InjectView(R.id.activity_report_product_name)
    TextView mProductName;

    @InjectView(R.id.activity_report_error_view)
    View mErrorView;

    @InjectView(R.id.activity_report_main_view)
    View mMainView;

    @InjectView(R.id.activity_report_total_stock)
    TextView mTotalStock;

    DatePickerDialog mDatePicker;

    private String fromDate, toDate;
    private String focus = null;

    @OnClick(R.id.activity_report_generate_button)
    void generateReport() {
        mMainView.setVisibility(View.VISIBLE);

        Intent mIntent = getIntent();
        int intValue = mIntent.getIntExtra("stock", 0);

        mTotalStock.setText(intValue + Constant.UNITS);

//        stock = mDBHelper.getTotalItems(sku);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ValueAnimator iAnimator = new ValueAnimator();
//                iAnimator.setObjectValues(0, sku);
//                iAnimator.setDuration(2500);
//                iAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//                    @Override
//                    public void onAnimationUpdate(ValueAnimator animation) {
//                        mTotalStock.setText((int) animation.getAnimatedValue() + Constant.UNITS);
//                    }
//                });
//                iAnimator.start();
//            }
//        }, 500);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);

        self = this;
        ButterKnife.inject(this);

        mDBHelper = DatabaseHelper.getInstance(this);

        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_detail_report);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_left);
        }

        Glide.with(this.getApplicationContext())
                .load(R.drawable.coffee_background)
                .into(mBackgroundImageView);

        Calendar now = Calendar.getInstance();
        mDatePicker = DatePickerDialog.newInstance(
                self,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        fromDate = toDate = GlobalApplication.singleton.getDate(null, null, null);

        mFromDateEditText.setText(fromDate);
        mFromDateEditText.setFocusable(false);
        mFromDateEditText.setFocusableInTouchMode(false);
        mFromDateEditText.setLongClickable(false);
        mFromDateEditText.setInputType(0);

        mToDateEditText.setText(toDate);
        mToDateEditText.setFocusable(false);
        mToDateEditText.setFocusableInTouchMode(false);
        mToDateEditText.setLongClickable(false);
        mToDateEditText.setInputType(0);

        mFromDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus = "fromDate";
                mDatePicker.show(getFragmentManager(), "DatePicker");
            }
        });

        mToDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus = "toDate";
                mDatePicker.show(getFragmentManager(), "DatePicker");
            }
        });

        mProductName.setText(getIntent().getStringExtra("name"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        if (focus.equals("fromDate")) {
            fromDate = GlobalApplication.singleton.getDate(year, monthOfYear, dayOfMonth);
        } else if (focus.equals("toDate")) {
            toDate = GlobalApplication.singleton.getDate(year, monthOfYear, dayOfMonth);
        }

        mFromDateEditText.setText(fromDate);
        mToDateEditText.setText(toDate);
    }
}
