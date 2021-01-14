package com.app.ui.fragment;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.view.CircleImageView;
import com.punuo.sys.app.message.badge.BadgeHelper;
import com.punuo.sys.app.message.badge.MessageBadgeCnt;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by maojianhui on 2018/10/18.
 */

public class MessageFragment extends Fragment {
    private static final String TAG = "MessageFragment";
    @BindView(R2.id.iv_huifu)
    CircleImageView ivHuifu;
    @BindView(R2.id.iv_zan)
    CircleImageView ivZan;
    @BindView(R2.id.iv_tongzhi)
    CircleImageView ivTongzhi;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.back)
    View back;
    @BindView(R2.id.system_notify)
    TextView systemNotify;
    @BindView(R2.id.rl_systemNotify)
    RelativeLayout rlSystemNotify;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mStatusBar.setVisibility(View.VISIBLE);
            mStatusBar.getLayoutParams().height = StatusBarUtil.getStatusBarHeight(getActivity());
            mStatusBar.requestLayout();
        }
        title.setText("消息");
        back.setVisibility(View.GONE);
        updateCount(BadgeHelper.messageBadgeCnt);
        EventBus.getDefault().register(this);
        return view;
    }

    private void updateCount(MessageBadgeCnt cnt) {
        if (cnt.commentCount != 0) {
            count1.setText(String.valueOf(cnt.commentCount));
            count1.setVisibility(View.VISIBLE);
        } else {
            count1.setVisibility(View.GONE);
        }
        if (cnt.likeCount != 0) {
            count2.setText(String.valueOf(cnt.likeCount));
            count2.setVisibility(View.VISIBLE);
        } else {
            count2.setVisibility(View.GONE);
        }
    }

    @OnClick({R2.id.iv_huifu, R2.id.iv_zan, R2.id.rl_systemNotify, R2.id.rl_dianzan, R2.id.rl_huifu})
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.rl_huifu) {
            ARouter.getInstance().build(HomeRouter.ROUTER_COMMENT_ACTIVITY).navigation();
        } else if (id == R.id.rl_dianzan) {
            ARouter.getInstance().build(HomeRouter.ROUTER_ADD_LIKE_ACTIVITY).navigation();
        } else if (id == R.id.rl_systemNotify) {
            ARouter.getInstance().build(HomeRouter.ROUTER_SYSTEM_NOTIFY_ACTIVITY).navigation();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageBadgeCnt event) {
        updateCount(event);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
