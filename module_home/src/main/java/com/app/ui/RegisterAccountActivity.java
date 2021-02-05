package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.request.RegisterRequest;
import com.app.views.CleanEditText;
import com.punuo.sys.app.home.login.BaseSwipeBackLoginActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.IntentUtil;
import com.punuo.sys.sdk.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;

/**
 * 用户注册页
 */
@Route(path = HomeRouter.ROUTER_REGISTER_ACTIVITY)
public class RegisterAccountActivity extends BaseSwipeBackLoginActivity {

    @BindView(R2.id.num_input)
    CleanEditText numInput;
    @BindView(R2.id.verificode_input)
    CleanEditText verificodeInput;
    @BindView(R2.id.get_verificode)
    TextView getVerificode;
    @BindView(R2.id.password_set)
    CleanEditText passwordSet;
    @BindView(R2.id.hidepassword)
    ImageView hidepassword;
    @BindView(R2.id.btn_register)
    TextView btnRegister;
    @BindView(R2.id.linearLayout)
    LinearLayout linearLayout;
    @BindView(R2.id.goto_login)
    TextView gotoLogin;
    @BindView(R2.id.iv_back)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up1);
        ButterKnife.bind(this);
        passwordSet.setTransformationMethod(PasswordTransformationMethod.getInstance());
        targetView = getVerificode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    private RegisterRequest mRegisterRequest;

    private void commit() {
        String phoneNum = numInput.getText().toString().trim();
        String password = passwordSet.getText().toString().trim();
        String code = verificodeInput.getText().toString().trim();
        if (checkPhoneNumber(phoneNum) && checkPassword(password) && checkCode(code)) {
            showLoadingDialog();
            if (mRegisterRequest != null && !mRegisterRequest.isFinish()) {
                return;
            }
            mRegisterRequest = new RegisterRequest();
            mRegisterRequest.addUrlParam("username",phoneNum);
            mRegisterRequest.addUrlParam("password", password);
            mRegisterRequest.setRequestListener(new RequestListener<PNBaseModel>() {
                @Override
                public void onComplete() {
                    dismissLoadingDialog();
                }

                @Override
                public void onSuccess(PNBaseModel result) {
                    if (result == null || result.msg == null) {
                        return;
                    }
                    if ("注册失败".equals(result.msg) || "手机号已注册".equals(result.msg)) {
                        ToastUtils.showToast(result.msg);
                    } else {
                        ToastUtils.showToast(result.msg);
                        IntentUtil.jumpActivity(RegisterAccountActivity.this, LoginActivity.class);
                        finish();
                    }
                }

                @Override
                public void onError(Exception e) {

                }
            });
            HttpManager.addRequest(mRegisterRequest);
        }

    }

    @OnClick({R2.id.get_verificode, R2.id.hidepassword, R2.id.btn_register, R2.id.goto_login, R2.id.iv_back})
    public void onClick(View v) {
        CharSequence phoneNum = numInput.getText();
        CharSequence code = verificodeInput.getText();
        int id = v.getId();
        if (id == R.id.get_verificode) {
            getVerifyCode(phoneNum);
        } else if (id == R.id.hidepassword) {
            if (passwordSet.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                hidepassword.setImageResource(R.drawable.ic_eye);
                passwordSet.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else if (passwordSet.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                hidepassword.setImageResource(R.drawable.ic_hide);
                passwordSet.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else if (id == R.id.btn_register) {
            if (checkPhoneNumber(phoneNum) && checkCode(code)) {
                SMSSDK.submitVerificationCode("86", phoneNum.toString(), code.toString());
            }
        } else if (id == R.id.goto_login) {
            ARouter.getInstance().build(HomeRouter.ROUTER_LOGIN_ACTIVITY).navigation();
        } else if (id == R.id.iv_back) {
            scrollToFinishActivity();
        }
    }

    @Override
    public void onVerifyCodeSuccess() {
        super.onVerifyCodeSuccess();
        final String phone = numInput.getText().toString().trim();
        final String passWord = passwordSet.getText().toString().trim();
        if (checkPhoneNumber(phone) && checkPassword(passWord)) {
            commit();
        }
    }
}

