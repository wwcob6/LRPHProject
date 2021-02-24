package com.app.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.R;
import com.app.model.MessageEvent;
import com.app.sip.SipInfo;
import com.punuo.sip.dev.model.CallResponse;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipCallReplyRequest;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.zoolu.sip.address.SipURL;


/**
 * Created by maojianhui on 2018/6/27.
 * 双向视频第一个页面
 */

public class VideoRequestActivity extends BaseActivity implements View.OnClickListener{
    private SoundPool soundPool;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private int streamId;

    private String TAG;
    String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath();
    String avaPath = SdCard + "/fanxin/Files/Camera/Images/";

    String devId = SipInfo.paddevId;
    SipURL sipURL = new SipURL(devId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_call1);

        EventBus.getDefault().register(this);
        initview();
        soundPool=new SoundPool(10, android.media.AudioManager.STREAM_MUSIC,5);
        final int sourceid=soundPool.load(this,R.raw.videowait,1);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {

                    public void onLoadComplete(
                            SoundPool soundPool,
                            int sampleId, int status) {
                        // TODO Auto-generated method stub
                        Log.d("xx", "11");
                        streamId=soundPool.play(sourceid, 1, 1, 0,
                                4, 1);
                    }
                });

        //改变状态栏颜色

    }
      public void stopSound(int id){
        soundPool.stop(id);
    }

    public void initview(){
        pref= PreferenceManager.getDefaultSharedPreferences(this);

        String vatar_temp=pref.getString("name","");
        ImageView hangup=(ImageView)findViewById(R.id.iv_hangup);
        hangup.setOnClickListener(this);
        TextView quxiao=(TextView)findViewById(R.id.quxiao);
        quxiao.setVisibility(View.VISIBLE);
        TextView tv_videostaus=(TextView)findViewById(R.id.tv_videostaus);
        tv_videostaus.setText("正在等待对方接受邀请...");
        tv_videostaus.setVisibility(View.VISIBLE);

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessage().equals("取消")) {
            Log.i(TAG, "message is " + event.getMessage());
            // 更新界面
//            stopSound(streamId);
            finish();
        }else if(event.getMessage().equals("开始视频")){
            finish();
        }
        else if(event.getMessage().equals("忙线中")) {
            AlertDialog.Builder dialog=new AlertDialog.Builder(this);
            dialog.setTitle("提示");
            dialog.setMessage("对方忙...");
            dialog.setCancelable(false);
            dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(CallResponse event) {
        if (TextUtils.equals(event.operate, "refuse")){
            ToastUtils.showToast("对方已拒绝");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        stopSound(streamId);
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v){
        if (v.getId() == R.id.iv_hangup) {
            SipCallReplyRequest replyRequest = new SipCallReplyRequest("cancel", AccountManager.getBindDevId());
            SipUserManager.getInstance().addRequest(replyRequest);
            finish();
        }

    }


}
