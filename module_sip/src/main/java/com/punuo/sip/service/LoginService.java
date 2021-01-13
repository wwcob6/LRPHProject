package com.punuo.sip.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.event.ReRegisterEvent;
import com.punuo.sip.model.LoginResponse;
import com.punuo.sip.request.BaseSipRequest;
import com.punuo.sip.request.SipHeartBeatRequest;
import com.punuo.sip.request.SipRegisterRequest;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-23.
 * 注册第二步Response / 心跳包Response
 **/
@Route(path = ServicePath.PATH_LOGIN)
public class LoginService extends NormalRequestService<LoginResponse> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, LoginResponse result) {
        EventBus.getDefault().post(result);
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseSipRequest baseSipRequest) {
        if (baseSipRequest instanceof SipRegisterRequest) {
            Log.d(TAG, "注册第二步超时");
        } else if (baseSipRequest instanceof SipHeartBeatRequest) {
            Log.d(TAG, "心跳包超时");
        }
        EventBus.getDefault().post(new ReRegisterEvent());
    }
}
