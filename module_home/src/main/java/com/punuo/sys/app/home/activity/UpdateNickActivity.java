package com.punuo.sys.app.home.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.request.UpdateNickRequest;
import com.app.views.CleanEditText;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipListUpdateRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

@Route(path = HomeRouter.ROUTER_UPDATE_NICK_ACTIVITY)
public class UpdateNickActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_nick);
        final String nick = UserInfoManager.getUserInfo().nickname;
        final CleanEditText et_nick = (CleanEditText) this.findViewById(R.id.et_nick);
        et_nick.setText(nick);
        ImageView back = (ImageView) this.findViewById(R.id.iv_back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(HomeRouter.ROUTER_USER_INFO_ACTIVITY).navigation();
                finish();
            }
        });
        TextView tv_save = (TextView) this.findViewById(R.id.tv_save);
        tv_save.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                String newNick = et_nick.getText().toString().trim();
                if (nick.equals(newNick) || newNick.equals("") || newNick.equals("0")) {
                    return;
                }
                updateNick(newNick);
            }

        });

    }
    private UpdateNickRequest mUpdateNickRequest;
    private void updateNick(final String newNick) {
        if (mUpdateNickRequest != null && !mUpdateNickRequest.isFinish()) {
            return;
        }
        showLoadingDialog("正在更新...");
        mUpdateNickRequest = new UpdateNickRequest();
        mUpdateNickRequest.addUrlParam("userid", AccountManager.getUserId());
        mUpdateNickRequest.addUrlParam("name", newNick);
        mUpdateNickRequest.setRequestListener(new RequestListener<PNBaseModel>() {
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
                    ToastUtils.showToast("更新成功");
                    UserInfoManager.getInstance().refreshUserInfo();
                    //通知平板更新昵称
                    SipListUpdateRequest request = new SipListUpdateRequest();
                    SipUserManager.getInstance().addRequest(request);
                    finish();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUpdateNickRequest);
    }
}

