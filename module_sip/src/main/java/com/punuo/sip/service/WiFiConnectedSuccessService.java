package com.punuo.sip.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.model.WiFiConnectedSuccessData;
import com.punuo.sip.request.BaseSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

@Route(path = ServicePath.PATH_WIFI_CONNECTED)
public class WiFiConnectedSuccessService extends NormalRequestService<WiFiConnectedSuccessData> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, WiFiConnectedSuccessData result) {
        if (result == null) {
            return;
        }
        EventBus.getDefault().post(result);
        Log.d("wifi",result.success);
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseSipRequest baseSipRequest) {

    }
}
