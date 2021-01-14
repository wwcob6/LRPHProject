package com.app.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.UserInfoManager;
import com.app.db.DatabaseInfo;
import com.app.db.MyDatabaseHelper;
import com.app.db.SQLiteManager;
import com.app.friendCircleMain.domain.Alldevid;
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
import com.app.sip.KeepAlive;
import com.app.sip.SipDev;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.sip.SipUser;
import com.app.tools.PermissionUtils;
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
import com.punuo.sys.sdk.util.StatusBarUtil;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static java.lang.Thread.sleep;

/**
 * 用户登陆页
 */
@Route(path = HomeRouter.ROUTER_LOGIN_ACTIVITY)
public class LoginActivity extends BaseSwipeBackLoginActivity {
    private static final String TAG = "LoginActivity";
    private String groupname;
    private String groupid;
    private String appdevid;
    private Handler handler = new Handler();

    //前一次的账号
    private String lastUserAccount;
    //网络连接失败窗口
    private AlertDialog newWorkConnectedDialog;
    //账号不存在
    private AlertDialog accountNotExistDialog;
    //登陆超时
    private AlertDialog timeOutDialog;
    protected CompositeSubscription mCompositeSubscription = new CompositeSubscription();
    public static List<Activity> activityList = new LinkedList();

    @BindView(R2.id.num_input2)
    CleanEditText numInput2;
    @BindView(R2.id.password_input)
    CleanEditText passwordInput;
    @BindView(R2.id.hidepassword)
    ImageView hidepassword;
    @BindView(R2.id.vericode_login)
    TextView vericodeLogin;
    @BindView(R2.id.password_forget)
    TextView passwordForget;
    @BindView(R2.id.btn_login1)
    TextView loginBtn;
    @BindView(R2.id.tv_register)
    TextView tvRegister;
    @BindView(R2.id.layout_root)
    RelativeLayout layoutRoot;

    private boolean userLoginFailed = false;
    private boolean devLoginFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login1);
        ButterKnife.bind(this);
        setSwipeBackEnable(false);
        LoginActivity.activityList.add(this);
        showAboutDialog();
        setUpSplash();
        initViews();
        StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, false);
        View statusBar = findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(this);
            statusBar.requestLayout();
        }
        EventBus.getDefault().register(this);
    }

    private void setUpSplash() {
        Subscription splash = Observable.timer(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> requestPermission());
        mCompositeSubscription.add(splash);
    }

    //检查网络是否连接
    public boolean isNetworkreachable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null) {
            SipInfo.isNetworkConnected = false;
        } else {
            SipInfo.isNetworkConnected = info.getState() == NetworkInfo.State.CONNECTED;
        }
        return SipInfo.isNetworkConnected;
    }

    public void showAboutDialog(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("服务协议和隐私政策");
        title.setPadding(10,10,10,10);
        title.setGravity(Gravity.CENTER);
        title.setTextSize(20);
        title.setTextColor(getResources().getColor(R.color.common_title_bg));
        dialog.setCustomTitle(title);
        TextView content = new TextView(this);
        content.setText(Html.fromHtml(
                "\n" +"请你务必审慎阅读.充分理解“服务协议”和“隐私政策”各条款，包括但不限于:为了向你提供即时通讯、内容分享等服务，我们需要收集你的设备信息、操作日志等个人信息。你可以在“设置”中查看，变更，删除个人信息并管理你的授权\n" +
                "\n" +
                "你可阅读"+"<a href=\"http://feeder.mengshitech.com/test/userPolicy.html\">《服务协议》</a>"+"和"+"<a href=\"http://feeder.mengshitech.com/test/privacy.html\">《隐私政策》</a>"+"了解详细信息。如你同意，请点击同意开始接受我们的服务。"));
        content.setMovementMethod(LinkMovementMethod.getInstance());
        dialog.setView(content);
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("暂不使用", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        });
        dialog.show();
    }
    public void exit(){
        for(Activity act:activityList){
            act.finish();
        }
        System.exit(0);

    }
    private void initViews() {
        numInput2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        numInput2.setTransformationMethod(HideReturnsTransformationMethod
                .getInstance());
        numInput2.setText(AccountManager.getUserAccount());

        passwordInput.setImeOptions(EditorInfo.IME_ACTION_DONE);
        passwordInput.setImeOptions(EditorInfo.IME_ACTION_GO);
        passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        passwordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                        || actionId == EditorInfo.IME_ACTION_GO) {
                    clickLogin();
                }
                return false;
            }
        });
        passwordInput.setText(AccountManager.getPassword());
        SipInfo.localSdCard = Environment.getExternalStorageDirectory().getAbsolutePath() + "/faxin/";
        isNetworkreachable();
    }

    // 网络是否连接
    private Runnable networkConnectedFailed = new Runnable() {
        @Override
        public void run() {
            if (newWorkConnectedDialog == null || !newWorkConnectedDialog.isShowing()) {
                newWorkConnectedDialog = new AlertDialog.Builder(LoginActivity.this)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent mIntent = new Intent(Settings.ACTION_SETTINGS);
                                startActivity(mIntent);
                            }
                        })
                        .setTitle("当前无网络,请检查网络连接")
                        .create();
                newWorkConnectedDialog.setCancelable(false);
                newWorkConnectedDialog.setCanceledOnTouchOutside(false);
                newWorkConnectedDialog.show();
            }
        }
    };

    private void clickLogin() {
        if (SipInfo.isNetworkConnected) {
            CharSequence userAccount = numInput2.getText();
            CharSequence passWord = passwordInput.getText();
            if (checkPhoneNumber(userAccount) && checkPassword(passWord)) {
                AccountManager.setUserAccount(userAccount);
                AccountManager.setPassword(passWord);
//                beforeLogin();
                showLoadingDialog();
                getUserId();
//                new Thread(connecting).start();
            }
        } else {
            //弹出网络连接失败窗口
            handler.post(networkConnectedFailed);
        }
    }

    private void beforeLogin() {
        SipInfo.phoneType = Build.MODEL;
        Log.i("手机型号", "model" + SipInfo.phoneType);
        SipInfo.isAccountExist = true;
        SipInfo.passwordError = false;
        SipInfo.userLogined = false;
        SipInfo.loginTimeout = true;
        SipURL local = new SipURL(SipInfo.REGISTER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        SipURL remote = new SipURL(SipInfo.SERVER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
        SipInfo.user_from = new NameAddress(SipInfo.userAccount, local);
        SipInfo.user_to = new NameAddress(SipInfo.SERVER_NAME, remote);
        SipInfo.devLogined = false;
        SipInfo.dev_loginTimeout = true;

        SipURL remote_dev = new SipURL(SipInfo.SERVER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);

        SipInfo.dev_to = new NameAddress(SipInfo.SERVER_NAME, remote_dev);
    }

    Runnable connecting = new Runnable() {
        @Override
        public void run() {
            try {
                int hostPort = new Random().nextInt(5000) + 2000;
                SipInfo.sipUser = new SipUser(null, hostPort, LoginActivity.this);
                Message register = SipMessageFactory.createRegisterRequest(
                        SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from);
                SipInfo.sipUser.sendMessage(register);
                sleep(1000);
                for (int i = 0; i < 2; i++) {
                    if (!SipInfo.isAccountExist) {
                        //用户账号不存在
                        break;
                    }
                    if (SipInfo.passwordError) {
                        //密码错误
                        break;
                    }
                    if (!SipInfo.loginTimeout) {
                        //没有超时
                        break;
                    }
                    SipInfo.sipUser.sendMessage(register);
                    sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {

                if (!SipInfo.isAccountExist) {
                    dismissLoadingDialog();
                    /*账号不存在提示*/
                    handler.post(accountNotExist);
                } else if (SipInfo.passwordError) {
                    //密码错误提示
                    dismissLoadingDialog();
                    ToastUtils.showToast("账号密码错误");
                    lastUserAccount = SipInfo.userAccount;
                } else if (SipInfo.loginTimeout) {
                    dismissLoadingDialog();
                    //超时
                    handler.post(timeOut);
                } else {
                    if (SipInfo.userLogined) {
                        Log.i(TAG, "用户登录成功!");
                        //开启用户保活心跳包
                        SipInfo.keepUserAlive = new KeepAlive();
                        SipInfo.keepUserAlive.setType(0);
                        SipInfo.keepUserAlive.startThread();
                        //数据库
                        String dbPath = SipInfo.userId + ".db";
//                        deleteDatabase(dbPath);
                        MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(LoginActivity.this, dbPath, null, 1);
                        DatabaseInfo.sqLiteManager = new SQLiteManager(myDatabaseHelper);

//                        SipInfo.applist.clear();
//                        //请求服务器上的app列表
//                        SipInfo.sipUser.sendMessage(SipMessageFactory.createSubscribeRequest(SipInfo.sipUser,
//                                SipInfo.user_to, SipInfo.user_from, BodyFactory.createAppsQueryBody()));
                        //启动设备注册线程
//                        new Thread(getuserinfo).start();
                        getUserInfo();
                    }
                }
            }
        }
    };

    //获取用户信息
    private void getUserInfo() {
        RequestListener listener = new RequestListener<PNUserInfo>() {
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

    //群组获取线程
    private GetAllGroupFromUserRequest mGetAllGroupFromUserRequest;

    //获取组信息
    private void getGroupInfo() {
        if (mGetAllGroupFromUserRequest != null && !mGetAllGroupFromUserRequest.isFinish()) {
            return;
        }
        groupname = null;
        groupid = null;
        appdevid = null;
        mGetAllGroupFromUserRequest = new GetAllGroupFromUserRequest();
        mGetAllGroupFromUserRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetAllGroupFromUserRequest.setRequestListener(new RequestListener<Group>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Group result) {
                if (result != null) {
                    if (result.groupList != null && !result.groupList.isEmpty()) {
                        //重构疑问：这个循环的意义
                        for (int i = 0; i < result.groupList.size(); i++) {
                            groupname = result.groupList.get(i).getGroup_name();
                            groupid = result.groupList.get(i).getGroupid();
                        }
                        Constant.devid1 = groupname;
                        Constant.groupid1 = groupid;
                        Constant.groupid = Constant.groupid1;
                        if (!TextUtils.isEmpty(Constant.groupid1)) {
                            SipInfo.paddevId = Constant.devid1;
                            getDevIdInfo();
                        } else {
                            dismissLoadingDialog();
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        }
                    }else {
                        dismissLoadingDialog();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
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
//                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
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

    private GetDevIdFromIdRequest mGetDevIdFromIdRequest;

    //获取用户设备信息
    private void getDevIdInfo() {
        if (mGetDevIdFromIdRequest != null && !mGetDevIdFromIdRequest.isFinish()) {
            return;
        }
        mGetDevIdFromIdRequest = new GetDevIdFromIdRequest();
        mGetDevIdFromIdRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        Log.i(TAG, "getUserId"+UserInfoManager.getUserInfo().id);
        mGetDevIdFromIdRequest.setRequestListener(new RequestListener<Alldevid>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Alldevid result) {
                if (result != null) {
                    List<String> devIdLists = result.devid;
                    if (devIdLists.isEmpty()) {
//                        ToastUtils.showToast("获取设备id失败");
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    } else {
                        appdevid = devIdLists.get(0);
                        Log.i(TAG, "appdevid"+appdevid);
                        Constant.appdevid1 = appdevid;
                        //TODO 设备注册
                        if (!TextUtils.isEmpty(appdevid)) {
//                            SipInfo.devId = appdevid;
//                            Log.i(TAG, "SipInfodevid"+SipInfo.devId);
//                            SipURL local_dev = new SipURL(SipInfo.devId, SipInfo.serverIp, SipInfo.SERVER_PORT_DEV);
//                            SipInfo.dev_from = new NameAddress(SipInfo.devId, local_dev);
//                            new Thread(devConnecting).start();
                            AccountManager.setDevId(appdevid);
                            registerDev();
                        }
                        //获取组内用户
                        getAllUserFormGroup();
                    }
                } else {
//                    ToastUtils.showToast("获取用户devid失败请重试");
                    dismissLoadingDialog();
                }
            }

            @Override
            public void onError(Exception e) {
//                ToastUtils.showToastShort("获取用户devid失败请重试");
                dismissLoadingDialog();
            }
        });
        HttpManager.addRequest(mGetDevIdFromIdRequest);
    }

    //设备注册线程
    private Runnable devConnecting = new Runnable() {
        @Override
        public void run() {
            try {
                int hostPort = new Random().nextInt(5000) + 2000;
                SipInfo.sipDev = new SipDev(LoginActivity.this, null, hostPort);//无网络时在主线程操作会报异常
                Message register = SipMessageFactory.createRegisterRequest(
                        SipInfo.sipDev, SipInfo.dev_to, SipInfo.dev_from);

                for (int i = 0; i < 3; i++) {//如果没有回应,最多重发2次
                    SipInfo.sipDev.sendMessage(register);
                    sleep(2000);
                    if (!SipInfo.dev_loginTimeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (SipInfo.devLogined) {
                    Log.d(TAG, "设备注册成功!");
                    Log.d(TAG, "设备心跳包发送!");

                    //启动设备心跳线程
                    SipInfo.keepDevAlive = new KeepAlive();
                    SipInfo.keepDevAlive.setSipDev(SipInfo.sipDev);
                    SipInfo.keepDevAlive.setDev_from(SipInfo.dev_from);
                    SipInfo.keepDevAlive.setType(1);
                    SipInfo.keepDevAlive.startThread();

                } else {
                    Log.e(TAG, "设备注册失败!");
                    Looper.prepare();
                    ToastUtils.showToastShort("设备注册失败请重新登录");
                    dismissLoadingDialog();
                    Looper.loop();
                }
            }
        }
    };

    private void showDialogTip(final int errorTime) {
        if (errorTime < 2) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("密码输入错误/还有" + (2 - errorTime) + "次输入机会")
                            .setPositiveButton("确定", null)
                            .create();
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                }
            });
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                            .setTitle("由于密码输入错误过多,该账号已被冻结")
                            .setPositiveButton("确定", null)//锁账号暂未完成
                            .create();
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(false);
                    Toast.makeText(getApplicationContext(), "该账号已被冻结", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Runnable accountNotExist = new Runnable() {
        @Override
        public void run() {
            if (accountNotExistDialog == null || !accountNotExistDialog.isShowing()) {
                accountNotExistDialog = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("不存在该账号")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                accountNotExistDialog.show();
                accountNotExistDialog.setCancelable(false);
                accountNotExistDialog.setCanceledOnTouchOutside(false);
            }
        }
    };
    private Runnable timeOut = new Runnable() {
        @Override
        public void run() {
            if (timeOutDialog == null || !timeOutDialog.isShowing()) {
                timeOutDialog = new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("连接超时,请检查网络")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                timeOutDialog.show();
                timeOutDialog.setCancelable(false);
                timeOutDialog.setCanceledOnTouchOutside(false);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R2.id.num_input2, R2.id.password_input, R2.id.hidepassword,
            R2.id.vericode_login, R2.id.password_forget, R2.id.btn_login1, R2.id.tv_register})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_login1) {
            clickLogin();
        } else if (id == R.id.hidepassword) {
            if (passwordInput.getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                hidepassword.setImageResource(R.drawable.ic_eye);
                passwordInput.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else if (passwordInput.getTransformationMethod() == HideReturnsTransformationMethod.getInstance()) {
                hidepassword.setImageResource(R.drawable.ic_hide);
                passwordInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        } else if (id == R.id.vericode_login) {
            ARouter.getInstance().build(HomeRouter.ROUTER_VERIFY_CODE_LOGIN_ACTIVITY)
                    .navigation();
        } else if (id == R.id.password_forget) {
            ARouter.getInstance().build(HomeRouter.ROUTER_FORGET_PASSWORD_ACTIVITY)
                    .navigation();
        } else if (id == R.id.tv_register) {
            ARouter.getInstance().build(HomeRouter.ROUTER_REGISTER_ACTIVITY)
                    .navigation();
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.PermissionUtils.READ_EXTERNAL_STORAGE",
            "android.PermissionUtils.WRITE_EXTERNAL_STORAGE"};

    private PermissionUtils.PermissionGrant mGrant = new PermissionUtils.PermissionGrant() {
        @Override
        public void onPermissionGranted(int requestCode) {

        }

        @Override
        public void onPermissionCancel() {
            Toast.makeText(LoginActivity.this, getString(R.string.alirtc_permission), Toast.LENGTH_SHORT).show();
            finish();
        }
    };

    private void requestPermission() {
        PermissionUtils.requestMultiPermissions(this,
                new String[]{
                        PermissionUtils.PERMISSION_CAMERA,
                        PermissionUtils.PERMISSION_WRITE_EXTERNAL_STORAGE,
                        PermissionUtils.PERMISSION_RECORD_AUDIO,
                        PermissionUtils.PERMISSION_READ_EXTERNAL_STORAGE}, mGrant);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.CODE_MULTI_PERMISSION) {
            PermissionUtils.requestPermissionsResult(this, requestCode, permissions, grantResults, mGrant);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PermissionUtils.REQUEST_CODE_SETTING) {
            new Handler().postDelayed(this::requestPermission, 500);

        }
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
     * @param model
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final LoginResponseUser model) {
        getUserInfo();
    }

    /**
     * 设备注册成功
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
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DevLoginFailEvent event) {
        devLoginFailed = true;
    }

    /**
     * 用户Sip服务重新注册事件
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
