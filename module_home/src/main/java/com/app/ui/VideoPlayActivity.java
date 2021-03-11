package com.app.ui;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.app.audio.AudioRecordManager;
import com.app.tools.CheckFrameTask;
import com.app.video.RTPVideoReceiveImp;
import com.app.video.VideoPlayThread;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.event.SuspendMonitorEvent;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipByeRequest;
import com.punuo.sip.user.request.SipDirectionControlRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.event.FrameTimeoutEvent;
import com.punuo.sys.sdk.router.HomeRouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 监控播放页面
 */
@Route(path = HomeRouter.ROUTER_VIDEO_PLAY_ACTIVITY)
public class VideoPlayActivity extends BaseActivity implements SurfaceHolder.Callback {
    public static final String TAG = "VideoPlayActivity";
    private final Timer mTimer = new Timer();
    @BindView(R2.id.surfaceView)
    SurfaceView surfaceView;
    private VideoPlayThread mVideoPlayThread;
    private RTPVideoReceiveImp mRTPVideoReceiveImp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        ImageView left = findViewById(R.id.left);
        ImageView right = findViewById(R.id.right);
        right.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SipDirectionControlRequest request = new SipDirectionControlRequest("right");
                SipUserManager.getInstance().addRequest(request);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                SipDirectionControlRequest request = new SipDirectionControlRequest("stop");
                SipUserManager.getInstance().addRequest(request);
            }
            return true;
        });
        left.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                SipDirectionControlRequest request = new SipDirectionControlRequest("left");
                SipUserManager.getInstance().addRequest(request);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                SipDirectionControlRequest request = new SipDirectionControlRequest("stop");
                SipUserManager.getInstance().addRequest(request);
            }
            return true;
        });
        ImageView back = findViewById(R.id.back1);
        back.setOnClickListener(v -> closeVideo());
        mRTPVideoReceiveImp = new RTPVideoReceiveImp(H264ConfigUser.rtpIp, H264ConfigUser.rtpPort);
        mTimer.schedule(new CheckFrameTask(), 0, 10000);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        H264Config.frameReceived = H264Config.FRAME_UNSET;
        if (mRTPVideoReceiveImp != null) {
            mRTPVideoReceiveImp.release();
        }
        AudioRecordManager.getInstance().stop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        closeVideo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FrameTimeoutEvent event) {
        closeVideo();
    }

    /**
     * 停止浏览
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(SuspendMonitorEvent event) {
        closeVideo();
    }

    private void closeVideo() {
        SipByeRequest request = new SipByeRequest(AccountManager.getBindDevId());
        SipUserManager.getInstance().addRequest(request);
        finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mVideoPlayThread = new VideoPlayThread(holder.getSurface());
        mVideoPlayThread.startThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mVideoPlayThread.stopThread();
    }
}