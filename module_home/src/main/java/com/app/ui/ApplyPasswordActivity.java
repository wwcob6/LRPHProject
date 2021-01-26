package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.app.request.ChangePwdRequest;
import com.app.views.CleanEditText;
import com.punuo.sys.app.home.login.BaseSwipeBackLoginActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = HomeRouter.ROUTER_APPLY_PASSWORD_ACTIVITY)
public class ApplyPasswordActivity extends BaseSwipeBackLoginActivity {
    @BindView(R2.id.newpassword_set)
    CleanEditText newpasswordSet;
    @BindView(R2.id.newpassword_confirm)
    CleanEditText newpasswordConfirm;
    @BindView(R2.id.hidepassword1)
    ImageView hidepassword1;
    @BindView(R2.id.btn_down)
    TextView btnDown;
    @BindView(R2.id.iv_back)
    ImageView ivBack4;
    @BindView(R2.id.hidepassword2)
    ImageView hidepassword2;

    @Autowired(name = "userAccount")
    String userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_new_password);
        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.image_bar));
        }
        newpasswordSet.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newpasswordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @OnClick({R2.id.hidepassword1, R2.id.hidepassword2, R2.id.btn_down, R2.id.iv_back})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.hidepassword1) {
            if (newpasswordSet.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                hidepassword1.setImageResource(R.drawable.ic_eye);
                newpasswordSet.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else if (newpasswordSet.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                hidepassword1.setImageResource(R.drawable.ic_hide);
                newpasswordSet.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else if (id == R.id.hidepassword2) {
            if (newpasswordConfirm.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                hidepassword2.setImageResource(R.drawable.ic_eye);
                newpasswordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else if (newpasswordConfirm.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                hidepassword2.setImageResource(R.drawable.ic_hide);
                newpasswordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else if (id == R.id.btn_down) {
            CharSequence password = newpasswordSet.getText();
            CharSequence again = newpasswordConfirm.getText();
            if (checkPassWordValid(password, again)) {
                changePwd(userAccount, password);
            }
        } else if (id == R.id.iv_back) {
            scrollToFinishActivity();
        }
    }

    private ChangePwdRequest mChangePwdRequest;

    private void changePwd(CharSequence telNum, CharSequence newPwd) {
        if (mChangePwdRequest != null && !mChangePwdRequest.isFinish()) {
            return;
        }
        mChangePwdRequest = new ChangePwdRequest();
        mChangePwdRequest.addUrlParam("tel_num", telNum);
        mChangePwdRequest.addUrlParam("password", newPwd);
        mChangePwdRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result.isSuccess()) {
                    ToastUtils.showToast("密码修改成功");
                    ARouter.getInstance().build(HomeRouter.ROUTER_LOGIN_ACTIVITY)
                            .navigation();
                } else {
                    if (!TextUtils.isEmpty(result.msg)) {
                        ToastUtils.showToast(result.msg);
                    }
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mChangePwdRequest);
    }
}
