package com.app.ui;

import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyCouponActivity extends BaseActivity {

    @BindView(R2.id.iv_back1)
    ImageView ivBack1;
    @BindView(R2.id.titleset)
    TextView titleset;
    @BindView(R2.id.tv_use)
    TextView tvUse;
    @BindView(R2.id.tv_abate)
    TextView tvAbate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_coupon);
        ButterKnife.bind(this);
        init();
    }

    public void init(){
        titleset.setText("优惠券");
        TextPaint tp=titleset.getPaint();
        tp.setFakeBoldText(true);
    }


    @OnClick({R2.id.iv_back1,R2.id.tv_use,R2.id.tv_abate})
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.iv_back1) {
            finish();
        } else if (id == R.id.tv_use) {
        } else if (id == R.id.tv_abate) {
        }
    }
}
