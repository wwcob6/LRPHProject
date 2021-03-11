package com.punuo.sys.app.home.account;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.request.GetDevIdFromIdRequest;
import com.app.views.CleanEditText;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.event.DevLoginFailEvent;
import com.punuo.sip.dev.event.ReRegisterDevEvent;
import com.punuo.sip.dev.model.LoginResponseDev;
import com.punuo.sip.dev.request.SipDevRegisterRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.event.ReRegisterUserEvent;
import com.punuo.sip.user.event.UnauthorizedEvent;
import com.punuo.sip.user.model.LoginResponseUser;
import com.punuo.sip.user.request.SipGetUserIdRequest;
import com.punuo.sys.app.home.friendCircle.domain.UserDevModel;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.account.model.PNUserInfo;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.RegexUtils;
import com.punuo.sys.sdk.util.StatusBarUtil;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;

@Route(path = HomeRouter.ROUTER_VERIFY_CODE_LOGIN_ACTIVITY)
public class VerifyCodeLoginActivity extends BaseSwipeBackLoginActivity {
    @BindView(R2.id.num_input4)
    CleanEditText numInput4;
    @BindView(R2.id.vericode_input)
    CleanEditText vericodeinput;
    @BindView(R2.id.get_verificode)
    TextView getVerificode;
    @BindView(R2.id.password_login)
    TextView passwordLogin;
    @BindView(R2.id.btn_login2)
    TextView btnLogin2;
    @BindView(R2.id.iv_back5)
    ImageView ivBack5;
    @BindView(R2.id.newAccount_register)
    TextView newAccountRegister;

    private boolean userLoginFailed = false;
    private boolean devLoginFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificode_login);
        ButterKnife.bind(this);
        targetView = getVerificode;
        StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, false);
        View statusBar = findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(this);
            statusBar.requestLayout();
        }
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        numInput4.setText(AccountManager.getUserAccount());
        vericodeinput.setOnEditorActionListener((v, actionId, event) -> {
            // 点击虚拟键盘的done
            if (actionId == EditorInfo.IME_ACTION_DONE
                    || actionId == EditorInfo.IME_ACTION_GO) {
                login();
            }
            return false;
        });
    }


    @OnClick({R2.id.get_verificode, R2.id.password_login, R2.id.btn_login2, R2.id.newAccount_register})
    public void onClick(View v) {
        int id = v.getId();
        String phone = numInput4.getText().toString().trim();
        if (id == R.id.get_verificode) {
            getVerifyCode(phone);
        } else if (id == R.id.password_login) {
            ARouter.getInstance().build(HomeRouter.ROUTER_LOGIN_ACTIVITY).navigation();
        } else if (id == R.id.btn_login2) {
            final String code = vericodeinput.getText().toString().trim();
            if (checkInput(phone, code)) {
                SMSSDK.submitVerificationCode("86", phone, code);
            }
        } else if (id == R.id.newAccount_register) {
            ARouter.getInstance().build(HomeRouter.ROUTER_REGISTER_ACTIVITY).navigation();
        } else if (id == R.id.iv_back5) {
            scrollToFinishActivity();
        }
    }

    private void login() {
        CharSequence userAccount = numInput4.getText();
        CharSequence code = getVerificode.getText();
        if (checkPhoneNumber(userAccount) && checkCode(code)) {
            AccountManager.setUserAccount(userAccount);
            AccountManager.setPassword("pass");
            showLoadingDialog();
            getUserId();
        }
    }
    //获取用户信息
    private void getUserInfo() {
        RequestListener<PNUserInfo> listener = new RequestListener<PNUserInfo>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PNUserInfo result) {
                if (result != null && result.isSuccess()) {
                    UserInfoManager.setUserInfo(result.userInfo);
                    //获取用户的设备Id注意不是绑定的设备Id
                    getDevIdInfo();
                } else {
                    ToastUtils.showToastShort("获取用户数据失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("获取用户数据失败请重试");
                dismissLoadingDialog();
            }
        };
        UserInfoManager.getInstance().refreshUserInfo(listener);
    }

    private GetDevIdFromIdRequest mGetDevIdFromIdRequest;

    //获取用户设备信息
    private void getDevIdInfo() {
        if (mGetDevIdFromIdRequest != null && !mGetDevIdFromIdRequest.isFinish()) {
            return;
        }
        mGetDevIdFromIdRequest = new GetDevIdFromIdRequest();
        mGetDevIdFromIdRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetDevIdFromIdRequest.setRequestListener(new RequestListener<UserDevModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(UserDevModel result) {
                if (result != null) {
                    List<String> devList = result.devList;
                    if (devList != null && !devList.isEmpty() && !TextUtils.isEmpty(devList.get(0))) {
                        //用户分配了设备id
                        AccountManager.setDevId(devList.get(0));
                        registerDev();
                    } else {
                        //用户未分配设备id
                        dismissLoadingDialog();
                        AccountManager.setLogin(true);
                        ARouter.getInstance().build(HomeRouter.ROUTER_HOME_ACTIVITY).navigation();
                        finish();
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("获取用户devid失败请重试");
                dismissLoadingDialog();
            }
        });
        HttpManager.addRequest(mGetDevIdFromIdRequest);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissLoadingDialog();
        EventBus.getDefault().unregister(this);
    }

    private boolean checkInput(String phone, String code) {
        if (TextUtils.isEmpty(phone)) { // 电话号码为空
            ToastUtils.showToast(R.string.tip_phone_can_not_be_empty);
        } else {
            if (!RegexUtils.checkMobile(phone)) { // 电话号码格式有误
                ToastUtils.showToast(R.string.tip_phone_regex_not_right);
            } else if (TextUtils.isEmpty(code)) { // 验证码不正确
                ToastUtils.showToast(R.string.tip_please_input_code);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onVerifyCodeSuccess() {
        super.onVerifyCodeSuccess();
        login();
    }

    /** sip注册相关该页面只进行sip的注册不启动心跳包 心跳包在
     * {@link com.punuo.sys.app.home.HomeActivity}
     * 上开启
     */
    /**
     * 用户注册第一步
     */
    private void getUserId() {
        SipGetUserIdRequest getUserIdRequest = new SipGetUserIdRequest();
        SipUserManager.getInstance().addRequest(getUserIdRequest);
    }

    /**
     * 设备注册第一步
     */
    private void registerDev() {
        SipDevRegisterRequest sipDevRegisterRequest = new SipDevRegisterRequest();
        SipDevManager.getInstance().addRequest(sipDevRegisterRequest);
    }

    /**
     * 用户注册成功
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final LoginResponseUser model) {
        getUserInfo();
    }

    /**
     * 设备注册成功
     *
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final LoginResponseDev model) {
        ARouter.getInstance().build(HomeRouter.ROUTER_HOME_ACTIVITY)
                .navigation();
        finish();
    }

    /**
     * 设备Sip服务重新注册事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReRegisterDevEvent event) {
        if (devLoginFailed) {
            devLoginFailed = false;
            return;
        }
        registerDev();
    }

    /**
     * 设备Sip服务注册失败事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DevLoginFailEvent event) {
        devLoginFailed = true;
    }

    /**
     * 用户Sip服务重新注册事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReRegisterUserEvent event) {
        if (userLoginFailed) {
            userLoginFailed = false;
            return;
        }
        getUserId();
    }

    /**
     * 用户Sip服务注册失败事件
     *
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UnauthorizedEvent event) {
        userLoginFailed = true;
        dismissLoadingDialog();
    }
    /** sip注册相关该页面只进行sip的注册不启动心跳包 心跳包在
     * {@link com.punuo.sys.app.home.HomeActivity}
     * 上开启
     */
}
