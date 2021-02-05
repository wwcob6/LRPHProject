package com.punuo.sys.app.home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.friendCircleMain.domain.UserList;
import com.app.sip.SipInfo;
import com.punuo.sys.app.home.activity.FamilyCircleActivity;
import com.punuo.sys.app.home.activity.FriendCallActivity;
import com.app.ui.VideoDial;
import com.punuo.sys.app.linphone.LinphoneHelper;
import com.punuo.sip.H264Config;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipIsMonitorRequest;
import com.punuo.sip.user.request.SipOperationRequest;
import com.punuo.sys.sdk.fragment.BaseFragment;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.IntentUtil;
import com.punuo.sys.sdk.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends BaseFragment implements View.OnClickListener {

    private Handler handlervideo = new Handler();
    private static final String TAG = "LaoRenFragment";
    private List<UserList> userList = new ArrayList<UserList>();
    private ImageView alarm;
    private ImageView camera;
    private RelativeLayout re_background;
    private RelativeLayout re_funcation;
    private View mStatusBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.micro_list_header1, container, false);
        mStatusBar = view.findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStatusBar.setVisibility(View.VISIBLE);
            mStatusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(getActivity());
            mStatusBar.requestLayout();
        }
        init(view);
        return view;
    }

    private void init(View view) {
        re_background = view.findViewById(R.id.re_background);
        re_funcation = view.findViewById(R.id.re_funcation);
        camera = re_background.findViewById(R.id.iv_camera);
        camera.setVisibility(View.VISIBLE);
        camera.setOnClickListener(this);

        alarm = re_background.findViewById(R.id.alarm1);
        ImageView application =re_funcation.findViewById(R.id.application);
        ImageView video = re_funcation.findViewById(R.id.video);
        ImageView browse = re_funcation.findViewById(R.id.browse);
        ImageView chat = re_funcation.findViewById(R.id.chat);
        application.setOnClickListener(this);
        video.setOnClickListener(this);
        browse.setOnClickListener(this);
        chat.setOnClickListener(this);
        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinphoneHelper.getInstance().register("7001", "123456", "sip.qinqingonline.com:5000");
            }
        });
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_camera) {
            startActivity(new Intent(getActivity(), FamilyCircleActivity.class));
        } else if (id == R.id.browse) {
            if (checkDevBind()) {
                H264Config.monitorType = H264Config.SINGLE_MONITOR;
                SipInfo.single = true;
                SipIsMonitorRequest request = new SipIsMonitorRequest(true);
                SipUserManager.getInstance().addRequest(request);
            }
        } else if (id == R.id.chat) {
            IntentUtil.jumpActivity(getActivity(), FriendCallActivity.class);
        } else if (id == R.id.application) {
            ARouter.getInstance().build(HomeRouter.ROUTER_WX_MINIPROGRAM_ENTRY_ACTIVITY).navigation();
        } else if (id == R.id.video) {
            H264Config.monitorType = H264Config.DOUBLE_MONITOR_POSITIVE;
            SipInfo.single = false;
            SipOperationRequest request = new SipOperationRequest();
            SipUserManager.getInstance().addRequest(request);
            startActivity(new Intent(getActivity(), VideoDial.class));
        }
    }
}


