package com.punuo.sys.app.home;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.model.Constant;
import com.app.sip.SipInfo;
import com.app.ui.VideoCallActivity;
import com.app.ui.VideoPlayActivity;
import com.app.video.RtpVideo;
import com.app.video.SendActivePacket;
import com.app.video.VideoInfo;
import com.punuo.sip.AccountUtil;
import com.punuo.sip.H264Config;
import com.punuo.sip.SipConfig;
import com.punuo.sip.dev.DevHeartBeatHelper;
import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.event.DevLoginFailEvent;
import com.punuo.sip.dev.event.MonitorEvent;
import com.punuo.sip.dev.event.ReRegisterDevEvent;
import com.punuo.sip.dev.model.LoginResponseDev;
import com.punuo.sip.dev.model.OperationData;
import com.punuo.sip.dev.request.SipDevRegisterRequest;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.UserHeartBeatHelper;
import com.punuo.sip.user.event.ReRegisterUserEvent;
import com.punuo.sip.user.event.UnauthorizedEvent;
import com.punuo.sip.user.event.UserReplaceEvent;
import com.punuo.sip.user.model.LoginResponseUser;
import com.punuo.sip.user.request.SipGetUserIdRequest;
import com.punuo.sys.app.home.process.HeartBeatTaskResumeProcessorDev;
import com.punuo.sys.app.home.process.HeartBeatTaskResumeProcessorUser;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sys.app.member.MemberFragment;
import com.punuo.sys.app.message.MessageFragment;
import com.punuo.sys.app.message.badge.BadgeHelper;
import com.punuo.sys.app.message.badge.MessageBadgeCnt;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.app.AppWakeUpEvent;
import com.punuo.sys.sdk.fragment.WebViewFragment;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.update.AutoUpdateService;
import com.punuo.sys.sdk.util.DeviceHelper;
import com.punuo.sys.sdk.util.IntentUtil;
import com.punuo.sys.sdk.util.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.net.SocketException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.app.sip.SipInfo.running;



/**
 * Author chzjy
 * Date 2016/12/19.
 * 主界面
 */
@Route(path = HomeRouter.ROUTER_HOME_ACTIVITY)
public class HomeActivity extends BaseActivity implements View.OnClickListener{
    private final String TAG = "HomeActivity";
    @BindView(R2.id.content_frame)
    FrameLayout contentFrame;
    @BindView(R2.id.newmessage_notify)
    TextView newMessageNotify;
    @BindView(R2.id.home_tab)
    View mHomeTab;
    @BindView(R2.id.shop_tab)
    View mShopTab;
    @BindView(R2.id.community_tab)
    View mCommunityTab;
    @BindView(R2.id.message_tab)
    View mMessageTab;
    @BindView(R2.id.person_tab)
    View mPersonTab;

    public static final int TAB_COUNT = 5;
    private MyFragmentManager mMyFragmentManager;
    private final View[] mTabBars = new View[TAB_COUNT];
    private HeartBeatTaskResumeProcessorDev mHeartBeatTaskResumeProcessorDev;
    private HeartBeatTaskResumeProcessorUser mHeartBeatTaskResumeProcessorUser;
    private boolean userLoginFailed = false;
    private boolean devLoginFailed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initHeartBeat();
        AccountManager.getBindDevInfo();
        init();
        mMyFragmentManager = new MyFragmentManager(this);
        switchFragment(Constant.HOME);
        StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, true);
        startUpdateService();
        if (!mBaseHandler.hasMessages(BadgeHelper.MSG_BADGE_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(BadgeHelper.MSG_BADGE_VALUE, BadgeHelper.DELAY);
        }
    }

    private void initHeartBeat() {
        mHeartBeatTaskResumeProcessorDev = new HeartBeatTaskResumeProcessorDev(mBaseHandler);
        mHeartBeatTaskResumeProcessorUser = new HeartBeatTaskResumeProcessorUser(mBaseHandler);
        mHeartBeatTaskResumeProcessorUser.onCreate();
        mHeartBeatTaskResumeProcessorDev.onCreate();
        if (!mBaseHandler.hasMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE, UserHeartBeatHelper.DELAY);
        }
        if (!mBaseHandler.hasMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE, DevHeartBeatHelper.DELAY);
        }
    }

    /**
     * 检测更新服务
     */
    private void startUpdateService() {
        Intent intent = new Intent(this, AutoUpdateService.class);
        intent.putExtra("needToast", false);
        IntentUtil.startServiceInSafeMode(this, intent);
    }

    private void init() {
        initTabBars();
        //启动语音电话服务
        startLinphone();
        //启动监听服务
//        startService(new Intent(this, NewsService.class));
    }

    private void startLinphone() {
        Log.i(TAG, "startLinphone: ");
        LinphoneHelper.getInstance().setDebug(DeviceHelper.isApkInDebug());
        LinphoneHelper.getInstance().startVoip(this);
        mBaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(UserInfoManager.getUserInfo().ipNumber)) {
                    LinphoneHelper.getInstance().register(UserInfoManager.getUserInfo().ipNumber, "123456", SipConfig.getServerIp() + ":5000");
                }
            }
        }, 500);
    }

    private void initTabBars() {
        mTabBars[0] = mHomeTab;
        mTabBars[1] = mShopTab;
        mTabBars[2] = mCommunityTab;
        mTabBars[3] = mMessageTab;
        mTabBars[4] = mPersonTab;
        for (int i = 0; i < TAB_COUNT; i++) {
            mTabBars[i].setOnClickListener(this);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        SipConfig.reset();
        releaseHeartBeat();
        mBaseHandler.removeMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        mBaseHandler.removeMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        mBaseHandler.removeMessages(BadgeHelper.MSG_BADGE_VALUE);
        SipInfo.userLogined = false;
        SipInfo.devLogined = false;
        //停止语音电话服务
        LinphoneHelper.getInstance().unRegister();
        LinphoneHelper.getInstance().stopVoip(this);
//        //关闭监听服务
//        stopService(new Intent(HomeActivity.this, NewsService.class));
        //关闭线程池
        running = false;
    }

    public void releaseHeartBeat() {
        if (mHeartBeatTaskResumeProcessorDev != null) {
            mHeartBeatTaskResumeProcessorDev.onDestroy();
        }
        if (mHeartBeatTaskResumeProcessorUser != null) {
            mHeartBeatTaskResumeProcessorUser.onDestroy();
        }
    }



    /**
     * 显示Fragment
     */
    public void switchFragment(int index) {
        changeTab(index);
        Bundle bundle = new Bundle();
        switch (index) {
            case Constant.HOME:
                mMyFragmentManager.switchFragmentWithCache(HomeFragment.class.getName(), bundle);
                break;
            case Constant.SHOP:
                //shop隐藏了，去掉了
//                mMyFragmentManager.switchFragmentWithCache(ShopFragment.class.getName(), bundle);
                break;
            case Constant.COMMUNITY:
                StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, true); //单独处理顶部状态栏颜色
                bundle.putString("url", "http://pet.qinqingonline.com:8889?user_id="+ AccountManager.getUserId());
                bundle.putBoolean("showTopBar", false);
                mMyFragmentManager.switchFragmentWithCache(WebViewFragment.class.getName(), bundle);
                break;
            case Constant.MESSAGE:
                StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, true); //单独处理顶部状态栏颜色
                mMyFragmentManager.switchFragmentWithCache(MessageFragment.class.getName(), bundle);
                break;
            case Constant.PERSON:
                StatusBarUtil.translucentStatusBar(this, Color.TRANSPARENT, true); //单独处理顶部状态栏颜色
                mMyFragmentManager.switchFragmentWithCache(MemberFragment.class.getName(), bundle);
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.home_tab) {
            switchFragment(Constant.HOME);
        } else if (id == R.id.shop_tab) {
            switchFragment(Constant.SHOP);
        } else if (id == R.id.community_tab) {
            switchFragment(Constant.COMMUNITY);
        } else if (id == R.id.message_tab) {
            switchFragment(Constant.MESSAGE);
        } else if (id == R.id.person_tab) {
            switchFragment(Constant.PERSON);
        }
    }

    private void changeTab(int type) {
        for (int i = 0; i < TAB_COUNT; ++i) {
            if (i == type) {
                mTabBars[i].setSelected(true);
            } else {
                mTabBars[i].setSelected(false);
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Intent startIntent = getIntent();
        if (startIntent != null && startIntent.getExtras() != null) {
            Bundle bundle = startIntent.getExtras();

            // logout 为 1，强制到登录页
            if (bundle.getInt("logout", 0) == 1) {
                releaseHeartBeat();
                ARouter.getInstance().build(HomeRouter.ROUTER_LOGIN_ACTIVITY).navigation();
                finish();
            }
        }
    }

    /* sip注册相关该页面启动心跳包 并且在异常断开之后重新进行sip的注册*/
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
     * 设备Sip服务重新注册事件
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReRegisterDevEvent event) {
        if (devLoginFailed) {
            devLoginFailed = false;
            return;
        }
        mBaseHandler.removeMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        registerDev();
    }

    /**
     * 设备Sip服务注册失败事件
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DevLoginFailEvent event) {
        mBaseHandler.removeMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
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
        mBaseHandler.removeMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        getUserId();
    }

    /**
     * 用户Sip服务注册失败事件
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UnauthorizedEvent event) {
        mBaseHandler.removeMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
        userLoginFailed = true;
    }

    /**
     * 用户Sip服务注册成功事件
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginResponseUser event) {
        //sip登陆注册成功 开启心跳保活
        if (!mBaseHandler.hasMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE, UserHeartBeatHelper.DELAY);
        }
    }

    /**
     * 设备Sip服务注册成功事件
     * @param event event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoginResponseDev event) {
        //sip登陆注册成功 开启心跳保活
        if (!mBaseHandler.hasMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE)) {
            mBaseHandler.sendEmptyMessageDelayed(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE, DevHeartBeatHelper.DELAY);
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case UserHeartBeatHelper.MSG_HEART_BEAR_VALUE:
                UserHeartBeatHelper.heartBeat();
                break;
            case DevHeartBeatHelper.MSG_HEART_BEAR_VALUE:
                DevHeartBeatHelper.heartBeat();
                break;
            case BadgeHelper.MSG_BADGE_VALUE:
                BadgeHelper.refreshBadge();
                break;
        }
    }
    /* sip注册相关该页面启动心跳包 并且在异常断开之后重新进行sip的注册*/

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(UserReplaceEvent event) {
        AlertDialog loginReplace = new AlertDialog.Builder(getApplicationContext())
                .setTitle("账号异地登录")
                .setMessage("请重新登录")
                .setPositiveButton("确定", null)
                .create();
        loginReplace.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        loginReplace.setCancelable(false);
        loginReplace.setCanceledOnTouchOutside(false);
        loginReplace.show();
        UserInfoManager.clearUserData();
        SipInfo.running = false;
        AccountUtil.logout();
        ARouter.getInstance().build(HomeRouter.ROUTER_LOGIN_ACTIVITY).navigation();
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageBadgeCnt event) {
        if (event.commentCount > 0 || event.likeCount > 0) {
            newMessageNotify.setVisibility(View.VISIBLE);
        } else {
            newMessageNotify.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NetworkInfo info) {
        getUserId();
    }

    /**
     * 收到设备发来的双向视频请求
     * @param data
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OperationData data) {
        ARouter.getInstance().build(HomeRouter.ROUTER_VIDEO_REPLY_ACTIVITY).navigation();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AppWakeUpEvent event) {
        startLinphone();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BadgeHelper.refreshBadge();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MonitorEvent event) {
        switch (event.monitorType) {
            case H264Config.SINGLE_MONITOR:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SipInfo.decoding = true;
                            VideoInfo.rtpVideo = new RtpVideo(H264ConfigUser.rtpIp, H264ConfigUser.rtpPort);
                            VideoInfo.sendActivePacket = new SendActivePacket();
                            VideoInfo.sendActivePacket.startThread();
                            startActivity(new Intent(HomeActivity.this, VideoPlayActivity.class));
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            case H264Config.DOUBLE_MONITOR_NEGATIVE:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SipInfo.decoding = true;
                            VideoInfo.rtpVideo = new RtpVideo(H264ConfigUser.rtpIp, H264ConfigUser.rtpPort);
                            VideoInfo.sendActivePacket = new SendActivePacket();
                            VideoInfo.sendActivePacket.startThread();
                            startActivity(new Intent(HomeActivity.this, VideoCallActivity.class));
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                break;
            case H264Config.DOUBLE_MONITOR_POSITIVE:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SipInfo.decoding = true;
                            VideoInfo.rtpVideo = new RtpVideo(H264ConfigUser.rtpIp, H264ConfigUser.rtpPort);
                            VideoInfo.sendActivePacket = new SendActivePacket();
                            VideoInfo.sendActivePacket.startThread();
                            startActivity(new Intent(HomeActivity.this, VideoCallActivity.class));
                        } catch (SocketException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
                break;
            default:
                break;
        }
    }
}
