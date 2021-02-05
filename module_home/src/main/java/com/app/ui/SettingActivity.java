package com.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.sip.SipInfo;
import com.app.ui.address.AddressManagerActivity;
import com.punuo.sip.AccountUtil;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.DataClearUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SettingActivity extends BaseSwipeBackActivity {


    @BindView(R2.id.re_psds)
    RelativeLayout rePsds;
    @BindView(R2.id.tv_phonenumber)
    TextView tvPhonenumber;
    @BindView(R2.id.re_phonenumber)
    RelativeLayout reInstructions;
    @BindView(R2.id.re_address)
    RelativeLayout reAddress;
    @BindView(R2.id.re_message)
    RelativeLayout reMessage;
    @BindView(R2.id.re_introduction)
    RelativeLayout reIntroduction;
    @BindView(R2.id.re_buffer)
    RelativeLayout reBuffer;
    @BindView(R2.id.logout)
    TextView logout;
    @BindView(R2.id.re_personal)
    RelativeLayout rePersonal;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.tv_buff)
    TextView tvBuff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        title.setText("设置");
        tvBuff.setText(DataClearUtil.getTotalCacheSize(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
    }


    @OnClick({R2.id.re_psds, R2.id.re_phonenumber, R2.id.re_personal, R2.id.re_address,
            R2.id.re_message, R2.id.re_introduction, R2.id.re_buffer, R2.id.logout, R2.id.back})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.re_phonenumber) {
            startActivity(new Intent(this, NumberBind.class));
        } else if (id == R.id.re_personal) {
            startActivity(new Intent(this, UserInfoActivity.class));
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.re_psds) {
            startActivity(new Intent(this, ChangePasswordActivity.class));
        } else if (id == R.id.re_address) {
            startActivity(new Intent(this, AddressManagerActivity.class));
        } else if (id == R.id.re_introduction) {
            startActivity(new Intent(this, SoftwareInstructActivity.class));
        } else if (id == R.id.re_buffer) {//清除缓存
            DataClearUtil.cleanAllCache(this);
            Toast.makeText(this, "清除缓存成功", Toast.LENGTH_SHORT).show();
            tvBuff.setText(DataClearUtil.getTotalCacheSize(this));
        } else if (id == R.id.re_message) {
            startActivity(new Intent(this, MessageNotifyActivity.class));
        } else if (id == R.id.back) {
            scrollToFinishActivity();
        }
    }

    /**
     * 退出登陆
     */
    private void logout() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("确认退出登陆?")
                .setNegativeButton("否", (dialog1, which) -> dialog1.dismiss())
                .setPositiveButton("是", (dialog12, which) -> {
                    AccountManager.setLogin(false);
                    UserInfoManager.clearUserData();
                    SipInfo.running = false;
                    AccountUtil.logout();
                    Bundle bundle = new Bundle();
                    bundle.putInt("logout", 1);
                    ARouter.getInstance().build(HomeRouter.ROUTER_HOME_ACTIVITY)
                            .with(bundle).navigation();
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
