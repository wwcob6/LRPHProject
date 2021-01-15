package com.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.app.UserInfoManager;
import com.app.model.PNBaseModel;
import com.app.request.UnBindDevRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = HomeRouter.ROUTER_DEV_BIND_SUCCESS_ACTIVITY)
public class DevBindSuccessActivity extends BaseSwipeBackActivity {

    @BindView(R2.id.iv_bindsuccess)
    ImageView ivBindsuccess;
    @BindView(R2.id.tv_devname)
    TextView tvDevname;
    @BindView(R2.id.tv_devnumber)
    TextView tvDevnumber;
    @BindView(R2.id.bt_unbind1)
    Button btUnbind1;
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_bind_success);
        ButterKnife.bind(this);
        title.setText("绑定设备");
        tvDevnumber.setText(AccountManager.getBindDevId());
    }

    @OnClick({R2.id.bt_unbind1, R2.id.back})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            scrollToFinishActivity();
        } else if (id == R.id.bt_unbind1) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setMessage("是否解绑")
                    .setNegativeButton("否", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            unBindDev();
                        }
                    }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    private UnBindDevRequest mUnBindDevRequest;

    private void unBindDev() {
        if (mUnBindDevRequest != null && mUnBindDevRequest.isFinish()) {
            return;
        }
        showLoadingDialog();
        mUnBindDevRequest = new UnBindDevRequest();
        mUnBindDevRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mUnBindDevRequest.addUrlParam("groupid", AccountManager.getGroupId());
        mUnBindDevRequest.addUrlParam("devid", AccountManager.getBindDevId());
        mUnBindDevRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {
                dismissLoadingDialog();
            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
//                    String devId = SipInfo.paddevId;
//                    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
//                    SipInfo.toDev = new NameAddress(devName, sipURL);
//                    org.zoolu.sip.message.Message query = SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
//                            SipInfo.user_from, BodyFactory.createListUpdate("addsuccess"));
//                    SipInfo.sipUser.sendMessage(query);
                    AccountManager.setBindDevId("");
                    ToastUtils.showToastShort("解绑成功");
                    finish();
                } else {
                    onError(null);
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("解绑失败,请重试");
            }
        });
        HttpManager.addRequest(mUnBindDevRequest);
    }
}
