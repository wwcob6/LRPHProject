package com.app.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.app.R;
import com.app.R2;
import com.app.audio.AudioRecordManager;
import com.app.model.MessageEvent;
import com.app.sip.SipInfo;
import com.app.tools.H264VideoDecoder;
import com.app.video.H264SendingManager;
import com.app.video.VideoInfo;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.H264ConfigDev;
import com.punuo.sip.dev.event.StopVideoEvent;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipByeRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;

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
 */
public class VideoCallActivity extends BaseActivity {
    public static final String TAG = "VideoCallActivity";
//    private BufferedOutputStream outputStream;
    @BindView(R2.id.mute)
    ImageView mute;
    @BindView(R2.id.hangup)
    ImageView hangup;
    @BindView(R2.id.HF)
    ImageView HF;
    private int currVolume = 0;
    private SurfaceHolder shBack;
    private int getNum = 0;
    Timer timer = new Timer();
    private H264VideoDecoder mH264VideoDecoder;
    AlertDialog dialog;
    @BindView(R2.id.sv_back)
    SurfaceView svBack;
    @BindView(R2.id.sv_front)
    SurfaceView svFront;
    int time = 0;
    H264SendingManager sendingManager;

    public static final String BROADCAST_ACTION = "BROADCAST_ACTION";
    IntentFilter imIntentFilter;
    LocalBroadcastManager mManager;
    BroadcastReceiver mReceiver;
    private boolean isSpeakerMode = false;
    private boolean ismute=false;
//    File f = new File(Environment.getExternalStorageDirectory(), "DCIM/video_encoded1.264");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        ButterKnife.bind(this);
        //防止锁屏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        shBack = svBack.getHolder();

        //shFront.setFormat(PixelFormat.TRANSPARENT);
        svFront.setZOrderOnTop(true);
        svFront.setZOrderMediaOverlay(true);

        sendingManager = new H264SendingManager(svFront);
        sendingManager.init();
        mH264VideoDecoder = new H264VideoDecoder();

        playVideo();
        timer.schedule(task, 0, 10000);

        imIntentFilter = new IntentFilter();
        imIntentFilter.addAction(BROADCAST_ACTION);
        mManager = LocalBroadcastManager.getInstance(VideoCallActivity.this);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                VideoCallActivity.this.finish();
            }
        };

// Check if we have write PermissionUtils
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have PermissionUtils so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

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
        EventBus.getDefault().register(this);
    }
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };




    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (VideoInfo.isrec == 0) {
                if (time == 6) {
                    closeVideo();
                    time = 0;
                } else {
                    new Handler(VideoCallActivity.this.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(VideoCallActivity.this, "未收到消息!", Toast.LENGTH_SHORT).show();

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
    protected void onResume() {
        super.onResume();
        mManager.registerReceiver(mReceiver, imIntentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();
        mManager.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();

        //重置免提
//        serviceBinder.setSpeakerMode(false);
        if (dialog != null) {
            dialog.dismiss();
        }
        VideoInfo.isrec = 1;
        SipInfo.decoding = false;
        VideoInfo.rtpVideo.removeParticipant();
        VideoInfo.sendActivePacket.stopThread();
        sendingManager.deInit();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        VideoInfo.rtpVideo.endSession();
        AudioRecordManager.getInstance().stop();
        System.gc();//系统垃圾回收
    }

    private void playVideo() {
        new Thread(Video).start();
        EventBus.getDefault().post(new MessageEvent("开始视频"));
    }

    Runnable Video = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Surface surface = shBack.getSurface();
            System.out.println(surface);

            if (surface != null) {
                mH264VideoDecoder.initDecoder(surface);
                while (SipInfo.decoding) {
                    if (SipInfo.isNetworkConnected) {
                        byte[] nal = VideoInfo.nalBuffers[getNum].getReadableNalBuf();
//                        try {
//                            outputStream = new BufferedOutputStream(new FileOutputStream(f));
//                            Log.i("AvcEncoder", "outputStream initialized");
//                        } catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        try {
//                            outputStream.write(nal,0,nal.length);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
                        if (nal != null) {
                            Log.i(TAG, "nalLen:" + nal.length);

                            try {
                                //硬解码
                                mH264VideoDecoder.onFrame(nal, 0, nal.length);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        VideoInfo.nalBuffers[getNum].readLock();
                        VideoInfo.nalBuffers[getNum].cleanNalBuf();
                        getNum++;
                        if (getNum == 200) {
                            getNum = 0;
                        }
                    }
                }
            }
        }
    };

    @Override
    public void onBackPressed() {
        dialog = new AlertDialog.Builder(this)
                .setTitle("是否结束聊天?")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeVideo();
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
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
        VideoInfo.endView = true;
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
