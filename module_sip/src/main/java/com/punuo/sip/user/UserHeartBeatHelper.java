package com.punuo.sip.user;

import com.punuo.sip.user.request.SipUserHeartBeatRequest;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 * 心跳包活工具
 **/
public class UserHeartBeatHelper {
    public static final int DELAY = 20 * 1000;
    public static final int MSG_HEART_BEAR_VALUE = 0x0001;

    public static void heartBeat() {
//        if (!AccountManager.isLoginned()) {
//            return;
//        }
        SipUserHeartBeatRequest heartBeatRequest = new SipUserHeartBeatRequest();
        SipUserManager.getInstance().addRequest(heartBeatRequest);
    }
}
