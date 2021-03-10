package com.app.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.app.R;
import com.app.R2;
import com.app.audio.AudioRecordManager;
import com.app.model.MessageEvent;
import com.app.sip.SipInfo;
import com.app.video.VideoInfo;
import com.app.video.VideoPlayThread;
import com.punuo.sip.dev.event.SuspendMonitorEvent;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipByeRequest;
import com.punuo.sip.user.request.SipDirectionControlRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author chzjy
 * Date 2016/12/19.
 * 单向视频
 */
public class VideoPlayActivity extends BaseActivity implements SurfaceHolder.Callback {
    public static final String TAG = "VideoPlayActivity";
    private SurfaceHolder surfaceHolder;
    private final Timer timer = new Timer();
    @BindView(R2.id.surfaceView)
    SurfaceView surfaceView;
    int time = 0;
    private VideoPlayThread mVideoPlayThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        ButterKnife.bind(this);
        surfaceHolder = surfaceView.getHolder();
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
        timer.schedule(task, 0, 10000);
        EventBus.getDefault().register(this);
    }

    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (VideoInfo.isrec == 0) {
                if (time == 6) {
                    getBaseHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast("长时间没有视频画面，页面关闭");
                        }
                    });

                    closeVideo();
                } else {
                    getBaseHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast("对方网络不稳定,视频画面可能延迟");
                        }
                    });
                    time++;
                }
            } else if (VideoInfo.isrec == 2) {
                VideoInfo.isrec = 0;
                time = 0;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        VideoInfo.isrec = 1;
        SipInfo.decoding = false;
        VideoInfo.rtpVideo.removeParticipant();
        VideoInfo.sendActivePacket.stopThread();
        VideoInfo.rtpVideo.endSession();
        AudioRecordManager.getInstance().stop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        closeVideo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("关闭视频")) {
            Log.i(TAG, "message is " + event.getMessage());
            closeVideo();
        } else if (event.getMessage().equals("停止浏览")) {
            closeVideo();
        }
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