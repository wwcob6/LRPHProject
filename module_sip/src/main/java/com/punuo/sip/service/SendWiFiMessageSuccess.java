package com.punuo.sip.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.model.SendWiFiResponse;
import com.punuo.sip.request.BaseSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

@Route(path=ServicePath.PATH_SEND_WIFI_MESSAGE)
public class SendWiFiMessageSuccess extends NormalRequestService<SendWiFiResponse>{

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, SendWiFiResponse result) {
        EventBus.getDefault().post(result);
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseSipRequest baseSipRequest) {

    }
}
