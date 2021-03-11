package com.app.ui;

import android.content.Context;
import android.graphics.ImageFormat;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.app.audio.AudioRecordManager;
import com.app.audio.VoiceEncoderThread;
import com.app.tools.CheckFrameTask;
import com.app.tools.H264VideoEncoder;
import com.app.video.RTPVideoReceiveImp;
import com.app.video.VideoPlayThread;
import com.glumes.ezcamerakit.CameraKitListener;
import com.glumes.ezcamerakit.EzCamera;
import com.glumes.ezcamerakit.EzCameraKit;
import com.glumes.ezcamerakit.RequestOptions;
import com.glumes.ezcamerakit.base.AspectRatio;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.H264ConfigDev;
import com.punuo.sip.dev.event.StartVideoEvent;
import com.punuo.sip.dev.event.StopVideoEvent;
import com.punuo.sip.user.H264ConfigUser;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipByeRequest;
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

import static com.app.tools.H264VideoEncoder.YUVQueue;

/**
 * Author chzjy
 * Date 2016/12/19.
 */
@Route(path = HomeRouter.ROUTER_VIDEO_CALL_ACTIVITY)
public class VideoCallActivity extends BaseActivity {
    public static final String TAG = "VideoCallActivity";
    @BindView(R2.id.mute)
    ImageView mute;
    @BindView(R2.id.hangup)
    ImageView hangup;
    @BindView(R2.id.HF)
    ImageView HF;
    private int currVolume = 0;
    private SurfaceHolder shBack;
    private Timer mTimer = new Timer();
    @BindView(R2.id.sv_back)
    SurfaceView svBack;
    @BindView(R2.id.sv_front)
    SurfaceView svFront;
    private boolean isSpeakerMode = false;
    private boolean ismute=false;
    private EzCamera engine;
    private VideoPlayThread mVideoPlayThread;
    private VoiceEncoderThread mVoiceEncoderThread;
    private RTPVideoReceiveImp mRTPVideoReceiveImp;
    private final int previewFrameRate = 15;  //演示帧率
    private final int previewWidth = 640;     //水平像素
    private final int previewHeight = 480;    //垂直像素
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //监听视频流
        mRTPVideoReceiveImp = new RTPVideoReceiveImp(H264ConfigUser.rtpIp, H264ConfigUser.rtpPort);

        shBack = svBack.getHolder();
        shBack.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                //渲染视频
                mVideoPlayThread = new VideoPlayThread(holder.getSurface());
                mVideoPlayThread.startThread();
                EventBus.getDefault().post(new StartVideoEvent());
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (mVideoPlayThread != null) {
                    mVideoPlayThread.stopThread();
                }
            }
        });
        svFront.setZOrderOnTop(true);
        svFront.setZOrderMediaOverlay(true);
        svFront.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                startPreview(holder, previewWidth, previewHeight, previewFrameRate);
                //编码发送
                H264VideoEncoder.getInstance().initEncoder(previewWidth, previewHeight, previewFrameRate);
                H264VideoEncoder.getInstance().startEncoderThread();
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                H264VideoEncoder.getInstance().close();
                if (engine != null) {
                    engine.stopPreview();
                }
            }
        });
        mTimer.schedule(new CheckFrameTask(), 0, 10000);
        //开启音频采集线程
        mVoiceEncoderThread = new VoiceEncoderThread(H264ConfigDev.rtpIp, H264ConfigDev.rtpPort);
        mVoiceEncoderThread.startEncoding();

        mute.setOnClickListener(v->{
            if(ismute){
                mute.setImageResource(R.drawable.jingyin);
                ismute=false;
            }else{
                mute.setImageResource(R.drawable.ic_mute);
                ismute=true;
            }
        });

        hangup.setOnClickListener(v->{
            closeVideo();
        });

        HF.setOnClickListener(v -> {
            if (isSpeakerMode) {
                HF.setImageResource(R.drawable.ic_hf);
                openSpeaker();
                isSpeakerMode = false;
            } else {
                HF.setImageResource(R.drawable.ysq);
                closeSpeaker();
                isSpeakerMode = true;
            }
        });

    }

    private void startPreview(SurfaceHolder holder, int previewWidth, int previewHeight, int previewFrameRate) {
        RequestOptions requestOptions = RequestOptions
                .openBackCamera()
                .setAspectRatio(AspectRatio.of(4, 3))
                .setFrameRate(previewFrameRate)
                .size(previewWidth, previewHeight)
                .setPixelFormat(ImageFormat.YV12)
                .setListener(cameraKitListener)
                .autoFocus(true);
        engine = EzCameraKit.with(holder)
                .apply(requestOptions)
                .open();
        engine.startPreview();
    }

    private final CameraKitListener cameraKitListener = new CameraKitListener() {
        @Override
        public void onPictureTaken(byte[] data) {

        }

        @Override
        public void onCameraOpened() {

        }

        @Override
        public void onCameraClosed() {

        }

        @Override
        public void onCameraPreview() {

        }

        @Override
        public void onPreviewCallback(byte[] data) {
            putYUVData(data, data.length);
        }
    };

    private void putYUVData(byte[] data, int length) {
        if (YUVQueue.size() >= 10) {
            YUVQueue.poll();
        }
        YUVQueue.add(data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        H264Config.frameReceived = H264Config.FRAME_UNSET;
        if (mVoiceEncoderThread != null) {
            mVoiceEncoderThread.stopEncoding();
        }
        if (mRTPVideoReceiveImp != null) {
            mRTPVideoReceiveImp.release();
        }
        AudioRecordManager.getInstance().stop();
        H264Config.monitorType = H264Config.IDLE;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {

    }

    private void closeVideo() {
        SipByeRequest request;
        if (H264Config.monitorType == H264Config.DOUBLE_MONITOR_NEGATIVE) {
            request = new SipByeRequest(H264ConfigDev.targetDevId);
        } else {
            request = new SipByeRequest(AccountManager.getBindDevId());
        }
        SipUserManager.getInstance().addRequest(request);
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StopVideoEvent event) {
        closeVideo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FrameTimeoutEvent event) {
        closeVideo();
    }
    /**
     * 打开扬声器
     */
    private void openSpeaker() {
        try{
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setMode(AudioManager.ROUTE_SPEAKER);
            currVolume = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            if(!audioManager.isSpeakerphoneOn()) {
                //setSpeakerphoneOn() only work when audio mode set to MODE_IN_CALL.
                audioManager.setMode(AudioManager.MODE_IN_CALL);
                audioManager.setSpeakerphoneOn(true);
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                        audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL ),
                        AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 关闭扬声器
     */
    public void closeSpeaker() {
        try {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if(audioManager != null) {
                if(audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(false);
                    audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,currVolume,
                            AudioManager.STREAM_VOICE_CALL);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
