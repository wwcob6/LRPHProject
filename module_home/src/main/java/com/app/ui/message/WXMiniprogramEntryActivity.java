package com.app.ui.message;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.punuo.sys.sdk.SDKConfig;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

@Route(path = HomeRouter.ROUTER_WX_MINIPROGRAM_ENTRY_ACTIVITY)
public class WXMiniprogramEntryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin);
        IWXAPI api = WXAPIFactory.createWXAPI(this, SDKConfig.WX_APP_ID);
        WXLaunchMiniProgram.Req req = new WXLaunchMiniProgram.Req();
        req.userName = SDKConfig.MINIPROGRAM_ID; // 填小程序原始id
//        req.path = "拉起小程序页面的可带参路径";             //拉起小程序页面的可带参路径，不填默认拉起小程序首页，对于小游戏，可以只传入 query 部分，来实现传参效果，如：传入 "?foo=bar"。
        req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE;// 可选打开 开发版，体验版和正式版
        api.sendReq(req);
        finish();
    }
}