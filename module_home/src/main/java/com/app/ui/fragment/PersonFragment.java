package com.app.ui.fragment;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.UserInfoManager;
import com.app.Util;
import com.app.model.PNUserInfo;
import com.app.ui.CloudAlbumActivity;
import com.app.ui.FamilyCircleActivity;
import com.app.ui.PrivateActivity;
import com.app.ui.ServiceCallSet;
import com.app.ui.SettingActivity;
import com.bumptech.glide.Glide;
import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.fragment.BaseFragment;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.StatusBarUtil;
import com.punuo.sys.sdk.util.ViewUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PersonFragment extends BaseFragment implements View.OnClickListener {
    private View mView;
    private TextView tv_name;
    private TextView tv_fxid;
    private ImageView iv_avatar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_person, container, false);
        initView();
        return mView;
    }

    private void initView() {
        TextView title = mView.findViewById(R.id.title);
        title.setText("个人中心");
        RelativeLayout re_myinfo = mView.findViewById(R.id.re_myinfo);
        re_myinfo.setOnClickListener(v -> startActivity(new Intent(getActivity(), FamilyCircleActivity.class)));
        iv_avatar = mView.findViewById(R.id.iv_avatar);
        tv_name = mView.findViewById(R.id.tv_name);
        tv_fxid = mView.findViewById(R.id.tv_fxid);
        String avatar = UserInfoManager.getUserInfo().avatar;
        Glide.with(PnApplication.getInstance()).load(Util.getImageUrl(avatar)).into(iv_avatar);
        RelativeLayout re_xaingce = mView.findViewById(R.id.re_xiangce);
        RelativeLayout re_addev = mView.findViewById(R.id.re_adddev);
        RelativeLayout re_servicecall = mView.findViewById(R.id.re_servicecall);
        RelativeLayout re_settings = mView.findViewById(R.id.re_settings);
        RelativeLayout re_private = mView.findViewById(R.id.re_private);
        re_xaingce.setOnClickListener(this);
        re_addev.setOnClickListener(this);
        re_servicecall.setOnClickListener(this);
        re_settings.setOnClickListener(this);
        re_private.setOnClickListener(this);
        mView.findViewById(R.id.back).setVisibility(View.GONE); // 隐藏返回按钮

        Glide.with(PnApplication.getInstance()).load(Util.getImageUrl(UserInfoManager.getUserInfo().avatar)).into(iv_avatar);
        ViewUtil.setText(tv_name, "昵称：" + UserInfoManager.getUserInfo().nickname);
        ViewUtil.setText(tv_fxid, "手机号：" + AccountManager.getUserAccount());

        View statusBar = mView.findViewById(R.id.status_bar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBar.setVisibility(View.VISIBLE);
            statusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(getActivity());
            statusBar.requestLayout();
        }
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.re_xiangce) {
            startActivity(new Intent(getActivity(), CloudAlbumActivity.class));
        } else if (id == R.id.re_adddev) {
            ARouter.getInstance().build(HomeRouter.ROUTER_BIND_DEV_ACTIVITY).navigation();
        } else if (id == R.id.re_servicecall) {
            startActivity(new Intent(getActivity(), ServiceCallSet.class));
        } else if (id == R.id.re_settings) {
            startActivity(new Intent(getActivity(), SettingActivity.class));
        } else if (id == R.id.re_private) {
            startActivity(new Intent(getActivity(), PrivateActivity.class));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(PNUserInfo.UserInfo userInfo) {
        Glide.with(PnApplication.getInstance()).load(Util.getImageUrl(userInfo.avatar)).into(iv_avatar);
        ViewUtil.setText(tv_name, "昵称：" + userInfo.nickname);
        ViewUtil.setText(tv_fxid, "手机号：" + AccountManager.getUserAccount());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
