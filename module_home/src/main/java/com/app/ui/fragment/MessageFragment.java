package com.app.ui.fragment;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.app.R;
import com.app.R2;
import com.app.UserInfoManager;
import com.app.http.GetPostUtil;
import com.app.model.Constant;
import com.app.model.MessageEvent;
import com.app.ui.AddlikeView;
import com.app.ui.CommentView;
import com.app.ui.message.SystemNotifyActivity;
import com.app.view.CircleImageView;
import com.punuo.sys.sdk.util.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.app.sip.SipInfo.addLikesItems;
import static com.app.sip.SipInfo.commentsItems;

/**
 * Created by maojianhui on 2018/10/18.
 */

public class MessageFragment extends Fragment {
    private static final String TAG ="MessageFragment" ;
    @BindView(R2.id.iv_huifu)
    CircleImageView ivHuifu;
    @BindView(R2.id.iv_zan)
    CircleImageView ivZan;
    @BindView(R2.id.iv_tongzhi)
    CircleImageView ivTongzhi;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.system_notify)
    TextView systemNotify;
    @BindView(R2.id.rl_systemNotify)
    RelativeLayout rlSystemNotify;
    @BindView(R2.id.camera111)
    ImageButton camera111;
    @BindView(R2.id.btnCall)
    ImageButton btnCall;
    @BindView(R2.id.iv_logo)
    ImageView ivLogo;
    @BindView(R2.id.rl_huifu)
    RelativeLayout rlHuifu;
    @BindView(R2.id.rl_dianzan)
    RelativeLayout rlDianzan;
    @BindView(R2.id.status_bar)
    View mStatusBar;
    @BindView(R2.id.count1)
    TextView count1;
    @BindView(R2.id.count2)
    TextView count2;
    private SimpleDateFormat df;
    private Timer mTimer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message1, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        mTimer=new Timer();
        setTimerTask();
        title.setText("消息");
        if(commentsItems!=0) {
            count1.setText(String.valueOf(commentsItems));
            count1.setVisibility(View.VISIBLE);
        }else{
            count1.setVisibility(View.INVISIBLE);
        }
        if(addLikesItems!=0){
            count2.setText(String.valueOf(addLikesItems));
            count2.setVisibility(View.VISIBLE);
        }else {
            count2.setVisibility(View.INVISIBLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStatusBar.setVisibility(View.VISIBLE);
            mStatusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(getActivity());
            mStatusBar.requestLayout();
        }
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("取消新评论提示")) {
            Log.i(TAG, "message is " + event.getMessage());
            handler.sendEmptyMessage(0x111);
        }else if(event.getMessage().equals("取消新点赞提示")){
            handler.sendEmptyMessage(0x222);
        }
    }

    private void setTimerTask() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message=new Message();
                message.what=1;
                handler.sendMessage(message);
            }
        },1,60*1000);
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String response = GetPostUtil.sendGet1111(Constant.URL_countNewComments,
                                    "id=" + UserInfoManager.getUserInfo().id + "&currentTime=" + df.format(new Date()) );
                            if((response!=null)&&!"".equals(response)){
                                JSONObject obj= JSONObject.parseObject(response);
                                String message=obj.getString("msg");
                                if(message.equals("success")){
                                    commentsItems=obj.getInteger("count");
                                    handler.sendEmptyMessage(0x111);
                                }
                            }
                            String response1=GetPostUtil.sendGet1111(Constant.URL_countNewLikes,
                                    "id=" + UserInfoManager.getUserInfo().id + "&currentTime=" + df.format(new Date()));
                            if((response1!=null)&&!"".equals(response1)){
                                JSONObject obj1= JSON.parseObject(response1);
                                String msg1=obj1.getString("msg");
                                if(msg1.equals("success")){
                                    addLikesItems=obj1.getInteger("count");
                                    handler.sendEmptyMessage(0x222);
                                }
                            }
                        }
                    }).start();
                    break;
                case 0x111:
                    if(commentsItems!=0){
                        count1.setText(String.valueOf(commentsItems));
                        count1.setVisibility(View.VISIBLE);
                        EventBus.getDefault().post(new MessageEvent("小红点出来吧"));
                    }else {
                        count1.setVisibility(View.INVISIBLE);
                    }
                case 0x222:
                    if(addLikesItems!=0){
                        count2.setText(String.valueOf(addLikesItems));
                        count2.setVisibility(View.VISIBLE);
                        EventBus.getDefault().post(new MessageEvent("小红点出来吧"));
                    }else {
                        count2.setVisibility(View.INVISIBLE);
                    }
                    default:
                        break;
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    @OnClick({R2.id.iv_huifu, R2.id.iv_zan, R2.id.rl_systemNotify, R2.id.rl_dianzan, R2.id.rl_huifu})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_huifu) {
            startActivity(new Intent(getActivity(), CommentView.class));
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        String response= GetPostUtil.sendGet1111(Constant.URl_getNewComments,df.format(new Date()));
//                    }
//                }).start();
        } else if (id == R.id.rl_dianzan) {
            startActivity(new Intent(getActivity(), AddlikeView.class));
        } else if (id == R.id.rl_systemNotify) {
            startActivity(new Intent(getActivity(), SystemNotifyActivity.class));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mTimer.cancel();
    }
}
