package com.app.ui;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.punuo.sip.dev.event.StartVideoEvent;
import com.punuo.sip.dev.model.CallResponse;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipCallReplyRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * 主动双向视频第一个页面
 */

@Route(path = HomeRouter.ROUTER_VIDEO_REQUEST_ACTIVITY)
public class VideoRequestActivity extends BaseActivity {
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video_request);
        EventBus.getDefault().register(this);
        initView();
        try {
            mMediaPlayer = MediaPlayer.create(this, R.raw.videowait);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initView() {
        ImageView hangup = findViewById(R.id.iv_hangup);
        hangup.setOnClickListener(v -> {
            SipCallReplyRequest replyRequest = new SipCallReplyRequest("cancel", AccountManager.getBindDevId());
            SipUserManager.getInstance().addRequest(replyRequest);
            finish();
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StartVideoEvent event) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallResponse event) {
        if (TextUtils.equals(event.operate, "refuse")) {
            ToastUtils.showToast("对方已拒绝");
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
}
