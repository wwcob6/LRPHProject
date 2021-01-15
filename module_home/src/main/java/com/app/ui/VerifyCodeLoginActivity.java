package com.app.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.UserInfoManager;
import com.app.friendCircleMain.domain.UserDevModel;
import com.app.friendCircleMain.domain.Group;
import com.app.friendCircleMain.domain.UserFromGroup;
import com.app.friendCircleMain.domain.UserList;
import com.app.groupvoice.GroupInfo;
import com.app.model.Constant;
import com.app.model.Friend;
import com.app.model.PNUserInfo;
import com.app.request.GetAllGroupFromUserRequest;
import com.app.request.GetAllUserFromGroupRequest;
import com.app.request.GetDevIdFromIdRequest;
import com.app.sip.SipInfo;
import com.app.views.CleanEditText;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.event.DevLoginFailEvent;
import com.punuo.sip.dev.event.ReRegisterDevEvent;
import com.punuo.sip.dev.model.LoginResponseDev;
import com.punuo.sip.dev.request.SipDevRegisterRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.event.ReRegisterUserEvent;
import com.punuo.sip.user.event.UserLoginFailEvent;
import com.punuo.sip.user.model.LoginResponseUser;
import com.punuo.sip.user.request.SipGetUserIdRequest;
import com.punuo.sys.app.home.HomeActivity;
import com.punuo.sys.app.home.login.BaseSwipeBackLoginActivity;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.RegexUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.smssdk.SMSSDK;

import static com.app.model.Constant.devid1;
import static com.app.model.Constant.devid2;
import static com.app.model.Constant.devid3;

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
    private String[] groupname = new String[3];
    private String[] groupid = new String[3];
    private String[] appdevid = new String[3];

    private boolean userLoginFailed = false;
    private boolean devLoginFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificode_login);
        ButterKnife.bind(this);
        targetView = getVerificode;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        numInput4.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        getVerificode.setImeOptions(EditorInfo.IME_ACTION_NEXT);// 下一步
        getVerificode.setOnEditorActionListener((v, actionId, event) -> {
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
            startActivity(new Intent(this, LoginActivity.class));
        } else if (id == R.id.btn_login2) {
            SipInfo.passWord = null;//验证码登录没有设置密码，将密码设置为空
            final String code = vericodeinput.getText().toString().trim();
            if (checkInput(phone, code)) {
                SMSSDK.submitVerificationCode("86", phone, code);
            }
        } else if (id == R.id.newAccount_register) {
            startActivity(new Intent(this, RegisterAccountActivity.class));
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
            SipInfo.isVericodeLogin = true;
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
                    SipInfo.friends.clear();
                    getGroupInfo();
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

    private GetAllGroupFromUserRequest mGetAllGroupFromUserRequest;

    //获取组信息
    private void getGroupInfo() {
        if (mGetAllGroupFromUserRequest != null && !mGetAllGroupFromUserRequest.isFinish()) {
            return;
        }
        mGetAllGroupFromUserRequest = new GetAllGroupFromUserRequest();
        mGetAllGroupFromUserRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetAllGroupFromUserRequest.setRequestListener(new RequestListener<Group>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Group result) {
                if (result != null) {
                    if (result.mGroupItemList != null && !result.mGroupItemList.isEmpty()) {
                        //重构疑问：这个循环的意义
                        groupname = new String[]{null, null, null};
                        groupid = new String[]{null, null, null};
                        appdevid = new String[]{null, null, null};
                        for (int i = 0; i < result.mGroupItemList.size(); i++) {
                            groupname[i] = result.mGroupItemList.get(i).groupName;
                            groupid[i] = result.mGroupItemList.get(i).groupId;
                        }
                        devid1 = groupname[0];
                        devid2 = groupname[1];
                        devid3 = groupname[2];

                        Constant.groupid1 = groupid[0];
                        Constant.groupid2 = groupid[1];
                        Constant.groupid3 = groupid[2];
                        Constant.groupid = Constant.groupid1;
                        if (!TextUtils.isEmpty(Constant.groupid1)) {
                            SipInfo.paddevId = Constant.devid1;
                            getDevIdInfo();
                        } else {
                            dismissLoadingDialog();
                            startActivity(new Intent(VerifyCodeLoginActivity.this, HomeActivity.class));
                        }
                    }
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
        });
        HttpManager.addRequest(mGetAllGroupFromUserRequest);
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
                    List<String> devIdLists = result.devList;
                    if (devIdLists.isEmpty()) {
                        ToastUtils.showToast("获取设备id失败");
                        startActivity(new Intent(VerifyCodeLoginActivity.this, HomeActivity.class));
                    } else {
                        String appDevId = devIdLists.get(0);
                        Constant.appdevid1 = appdevid[0];
                        if (!TextUtils.isEmpty(appDevId)) {
                            AccountManager.setDevId(appDevId);
                            registerDev();
                        }
                        //获取组内用户
                        getAllUserFormGroup();
                    }
                } else {
                    ToastUtils.showToast("获取用户devid失败请重试");
                    dismissLoadingDialog();
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

    private GetAllUserFromGroupRequest mGetAllUserFromGroupRequest;

    //获取组内用户
    private void getAllUserFormGroup() {
        if (mGetAllUserFromGroupRequest != null && !mGetAllUserFromGroupRequest.isFinish()) {
            return;
        }
        mGetAllUserFromGroupRequest = new GetAllUserFromGroupRequest();
        mGetAllUserFromGroupRequest.addUrlParam("groupid", Constant.groupid);
        mGetAllUserFromGroupRequest.setRequestListener(new RequestListener<UserFromGroup>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(UserFromGroup result) {
                if (result != null) {
                    if (result.userList != null && !result.userList.isEmpty()) {
                        for (int i = 0; i < result.userList.size(); i++) {
                            UserList userList = result.userList.get(i);
                            Friend friend = new Friend();
                            friend.setNickName(userList.getNickname());
                            friend.setPhoneNum(userList.getName());
                            friend.setUserId(userList.getUserid());
                            friend.setId(userList.getId());
                            friend.setAvatar(userList.getAvatar());
                            SipInfo.friends.add(friend);
                        }

                        GroupInfo.groupNum = "7000";
//                        String peer = peerElement.getFirstChild().getNodeValue();
                        GroupInfo.ip = "101.69.255.134";
//                        GroupInfo.port = 7000;
                        GroupInfo.level = "1";
                        SipInfo.devName = UserInfoManager.getUserInfo().nickname;
                        dismissLoadingDialog();
                        startActivity(new Intent(VerifyCodeLoginActivity.this, HomeActivity.class));
                    } else {
                        ToastUtils.showToastShort("获取用户数据失败请重试");
                        dismissLoadingDialog();
                    }
                } else {
                    ToastUtils.showToastShort("获取用户数据失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mGetAllUserFromGroupRequest);
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
    public void onMessageEvent(UserLoginFailEvent event) {
        userLoginFailed = true;
    }
    /** sip注册相关该页面只进行sip的注册不启动心跳包 心跳包在
     * {@link com.punuo.sys.app.home.HomeActivity}
     * 上开启
     */
}
