package com.app.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.request.UpdateUserPhoneRequest;
import com.app.views.CleanEditText;
import com.punuo.sip.AccountUtil;
import com.punuo.sys.app.home.login.BaseSwipeBackLoginActivity;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;

public class BindPhoneActivity extends BaseSwipeBackLoginActivity {
    @BindView(R2.id.back)
    ImageView ivBack1;
    @BindView(R2.id.title)
    TextView textView4;
    @BindView(R2.id.tv_currentnum)
    TextView tvCurrentnum;
    @BindView(R2.id.currentphone)
    TextView currentphone;
    @BindView(R2.id.tv_phone)
    CleanEditText tvPhone;
    @BindView(R2.id.verificode_input2)
    CleanEditText verificodeInput2;
    @BindView(R2.id.btn_send_verifi_code)
    TextView btnSendVerifiCode;
    @BindView(R2.id.rl_phone)
    RelativeLayout rlPhone;
    @BindView(R2.id.btn_confirm)
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_bind);
        ButterKnife.bind(this);
        targetView = btnSendVerifiCode;
        initView();
        currentphone.setText(AccountManager.getUserAccount());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    private void initView() {
        textView4.setText("修改手机号码");
        verificodeInput2.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // 点击虚拟键盘的done
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    btnConfirm.performClick();
                }
                return false;
            }
        });
    }

    private void commit() {
        String phoneNumber = tvPhone.getText().toString().trim();
        UpdateUserPhoneRequest request = new UpdateUserPhoneRequest();
        request.addUrlParam("tel_num", phoneNumber);
        request.addUrlParam("userid", AccountManager.getUserId());
        request.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    AccountManager.setLogin(false);
                    UserInfoManager.clearUserData();
                    AccountUtil.logout();
                    Bundle bundle = new Bundle();
                    bundle.putInt("logout", 1);
                    ARouter.getInstance().build(HomeRouter.ROUTER_HOME_ACTIVITY)
                            .with(bundle).navigation();
                    ToastUtils.showToastShort("更改成功, 请重新登陆");
                } else {
                    ToastUtils.showToastShort("更改失败");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(request);
    }


    @OnClick({R2.id.tv_phone, R2.id.verificode_input2, R2.id.btn_send_verifi_code,
            R2.id.rl_phone, R2.id.btn_confirm, R2.id.currentphone, R2.id.back})
    public void onClick(View v) {
        int id = v.getId();
        final CharSequence phone = tvPhone.getText();
        final CharSequence code = verificodeInput2.getText();
        if (id == R.id.btn_send_verifi_code) {
            if (checkPhoneNumber(phone)) {
                getVerifyCode(phone);
            }
        } else if (id == R.id.btn_confirm) {
            if (checkPhoneNumber(phone) && checkCode(code)) {
                SMSSDK.submitVerificationCode("86", phone.toString(), code.toString());
            }
        } else if (id == R.id.back) {
            finish();
        }
    }

    @Override
    public void onVerifyCodeSuccess() {
        super.onVerifyCodeSuccess();
        final String phone = tvPhone.getText().toString().trim();
        String code = verificodeInput2.getText().toString().trim();
        if (checkPhoneNumber(phone) && checkCode(code)) {
            commit();
        } else {
            ToastUtils.showToast("填写信息格式不正确");
        }
    }
}
