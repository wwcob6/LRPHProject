package com.app.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.model.MessageEvent;
import com.punuo.sip.dev.H264ConfigDev;
import com.punuo.sip.dev.model.CallResponse;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipCallReplyRequest;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by maojianhui on 2018/6/27.
 */
@Route(path = HomeRouter.ROUTER_VIDEO_REPLY_ACTIVITY)
public class VideoReplyActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "VideoReplyActivity";
    private MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);  //注册
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        try {
            mMediaPlayer = MediaPlayer.create(this, R.raw.videowait);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        init();
    }

    public void init() {
        ImageView bt1 = findViewById(R.id.bt_accept);
        bt1.setOnClickListener(this);
        ImageView bt2 = findViewById(R.id.bt_refuse);
        bt2.setOnClickListener(this);
        TextView tv_videostaus= findViewById(R.id.tv_videostaus1);
        tv_videostaus.setText("对方邀请您视频通话...");
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("开始视频")) {
            Log.i(TAG, "111message is " + event.getMessage());
            // 更新界面
            finish();
            Log.d(TAG,"关闭connect");
        }
        else if(event.getMessage().equals("取消")){
            finish();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallResponse event) {
        if (TextUtils.equals(event.operate, "cancel")){
            ToastUtils.showToast("对方已取消");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.bt_accept) {
            SipCallReplyRequest replyRequest = new SipCallReplyRequest("agree", H264ConfigDev.targetDevId);
            SipUserManager.getInstance().addRequest(replyRequest);
        } else if (id == R.id.bt_refuse) {
            SipCallReplyRequest replyRequest = new SipCallReplyRequest("refuse", H264ConfigDev.targetDevId);
            SipUserManager.getInstance().addRequest(replyRequest);
        }
        finish();
    }
}
