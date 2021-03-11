package com.punuo.sys.app.home.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.app.request.UpdateNotifyRequest;
import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.PreferenceUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


@Route(path = HomeRouter.ROUTER_MESSAGE_NOTIFY_ACTIVITY)
public class MessageNotifyActivity extends BaseSwipeBackActivity {

    @BindView(R2.id.btn_switch)
    SwitchCompat btnSwitch;
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.title)
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_notify);
        ButterKnife.bind(this);
        title.setText("新消息通知");
        boolean isNotify = PreferenceUtils.getBoolean(PnApplication.getInstance(),"is_open_notify");
        btnSwitch.setChecked(isNotify);
        initView();

    }

    private void initView() {
        btnSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateNotify(isChecked);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToFinishActivity();
            }
        });
    }

    private UpdateNotifyRequest mUpdateNotifyRequest;

    private void updateNotify(boolean isChecked) {
        if (mUpdateNotifyRequest != null && !mUpdateNotifyRequest.isFinish()) {
            return;
        }
        mUpdateNotifyRequest = new UpdateNotifyRequest();
        mUpdateNotifyRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mUpdateNotifyRequest.addUrlParam("notify", isChecked ? "1" : "2");
        mUpdateNotifyRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    PreferenceUtils.setBoolean(PnApplication.getInstance(),"is_open_notify", isChecked);
                    ToastUtils.showToast("成功");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUpdateNotifyRequest);
    }
}
