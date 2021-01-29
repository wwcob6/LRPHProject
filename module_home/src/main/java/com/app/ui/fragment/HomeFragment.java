package com.app.ui.fragment;

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
import com.app.sip.BodyFactory;
import com.app.sip.SipInfo;
import com.app.sip.SipMessageFactory;
import com.app.ui.FamilyCircleActivity;
import com.app.ui.FriendCallActivity;
import com.app.ui.VideoDial;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipIsMonitorRequest;
import com.punuo.sys.sdk.fragment.BaseFragment;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.IntentUtil;
import com.punuo.sys.sdk.util.StatusBarUtil;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

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
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.iv_camera) {
            startActivity(new Intent(getActivity(), FamilyCircleActivity.class));
        } else if (id == R.id.browse) {
            if (checkDevBind()) {
                SipInfo.single = true;
                SipIsMonitorRequest request = new SipIsMonitorRequest(true);
                SipUserManager.getInstance().addRequest(request);
            }
        } else if (id == R.id.chat) {
            IntentUtil.jumpActivity(getActivity(), FriendCallActivity.class);
        } else if (id == R.id.application) {
            ARouter.getInstance().build(HomeRouter.ROUTER_WX_MINIPROGRAM_ENTRY_ACTIVITY).navigation();
        } else if (id == R.id.video) {
            SipInfo.single = false;
            String devId1 = SipInfo.paddevId;
            //devId = devId1.substring(0, devId1.length() - 4).concat("0160");
            //设备id后4位替换成0160
            String devName1 = "pad";
            final String devType1 = "2";
            SipURL sipURL1 = new SipURL(devId1, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
            SipInfo.toDev = new NameAddress(devName1, sipURL1);
            //视频
            org.zoolu.sip.message.Message query1 =
                    SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toDev,
                            SipInfo.user_from, BodyFactory.createCallRequest("request", SipInfo.devId
                                    , SipInfo.userId));
            SipInfo.sipUser.sendMessage(query1);

            startActivity(new Intent(getActivity(), VideoDial.class));
        }
    }
}


