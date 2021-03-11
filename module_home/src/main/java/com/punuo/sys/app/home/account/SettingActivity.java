package com.punuo.sys.app.home.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.punuo.sip.AccountUtil;
import com.punuo.sys.app.home.address.AddressManagerActivity;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.DataClearUtil;
import com.punuo.sys.sdk.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


@Route(path = HomeRouter.ROUTER_SETTING_ACTIVITY)
public class SettingActivity extends BaseSwipeBackLoginActivity {


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
    }


    @OnClick({R2.id.re_psds, R2.id.re_phonenumber, R2.id.re_personal, R2.id.re_address,
            R2.id.re_message, R2.id.re_introduction, R2.id.re_buffer, R2.id.logout, R2.id.back})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.re_phonenumber) {
            ARouter.getInstance().build(HomeRouter.ROUTER_BIND_PHONE_ACTIVITY).navigation();
        } else if (id == R.id.re_personal) {
            ARouter.getInstance().build(HomeRouter.ROUTER_USER_INFO_ACTIVITY).navigation();
        } else if (id == R.id.logout) {
            logout();
        } else if (id == R.id.re_psds) {
            ARouter.getInstance().build(HomeRouter.ROUTER_CHANGE_PASSWORD_ACTIVITY).navigation();
        } else if (id == R.id.re_address) {
            ARouter.getInstance().build(HomeRouter.ROUTER_ADDRESS_MANAGER_ACTIVITY).navigation();
        } else if (id == R.id.re_introduction) {
            ARouter.getInstance().build(HomeRouter.ROUTER_SOFTWARE_INSTRUCT_ACTIVITY).navigation();
        } else if (id == R.id.re_buffer) {//清除缓存
            DataClearUtil.cleanAllCache(this);
            ToastUtils.showToast("清除缓存成功");
            tvBuff.setText(DataClearUtil.getTotalCacheSize(this));
        } else if (id == R.id.re_message) {
            ARouter.getInstance().build(HomeRouter.ROUTER_MESSAGE_NOTIFY_ACTIVITY).navigation();
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
