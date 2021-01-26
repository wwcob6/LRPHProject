package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.app.request.UpdateNotifyRequest;
import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.PreferenceUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;


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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
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
