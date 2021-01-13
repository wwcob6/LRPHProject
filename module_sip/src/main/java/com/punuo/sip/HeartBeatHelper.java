package com.punuo.sip;

import com.punuo.sip.request.SipHeartBeatRequest;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 * 心跳包活工具
 **/
public class HeartBeatHelper {
    public static final int DELAY = 20 * 1000;

    public static void heartBeat() {
//        if (!AccountManager.isLoginned()) {
//            return;
//        }
        SipHeartBeatRequest heartBeatRequest = new SipHeartBeatRequest();
        SipUserManager.getInstance().addRequest(heartBeatRequest);
    }
}
