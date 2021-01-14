package com.punuo.sip.dev;

import com.punuo.sip.dev.request.SipDevHeartBeatRequest;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 * 心跳包活工具
 **/
public class DevHeartBeatHelper {
    public static final int DELAY = 20 * 1000;
    public static final int MSG_HEART_BEAR_VALUE = 0x0002;

    public static void heartBeat() {
//        if (!AccountManager.isLoginned()) {
//            return;
//        }
        SipDevHeartBeatRequest heartBeatRequest = new SipDevHeartBeatRequest();
        SipDevManager.getInstance().addRequest(heartBeatRequest);
    }
}
