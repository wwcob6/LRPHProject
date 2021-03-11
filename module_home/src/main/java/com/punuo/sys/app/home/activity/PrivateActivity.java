package com.punuo.sys.app.home.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.router.SDKRouter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = HomeRouter.ROUTER_PRIVATE_ACTIVITY)
public class PrivateActivity extends BaseSwipeBackActivity {
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.back)
    ImageView back;
    @BindView(R2.id.re_yinsi)
    RelativeLayout reYinsi;
    @BindView(R2.id.re_xieyi)
    RelativeLayout reXieyi;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);
        ButterKnife.bind(this);
        title.setText("用户协议和隐私政策");
    }
    @OnClick({R2.id.back,R2.id.re_yinsi,R2.id.re_xieyi})
    public void onClick(View v){
        int id = v.getId();
        if (id == R.id.back) {
            scrollToFinishActivity();
        } else if (id == R.id.re_yinsi) {
            ARouter.getInstance().build(SDKRouter.ROUTER_WEB_VIEW_ACTIVITY)
                    .withString("url", "http://sip.qinqingonline.com:8000/static/protocol/lrph_privacy_protocol.html")
                    .withString("title", "隐私政策")
                    .navigation();
        } else if (id == R.id.re_xieyi) {
            ARouter.getInstance().build(SDKRouter.ROUTER_WEB_VIEW_ACTIVITY)
                    .withString("url", "http://sip.qinqingonline.com:8000/static/protocol/lrph_user_protocol.html")
                    .withString("title", "用户协议")
                    .navigation();
        }
    }
}