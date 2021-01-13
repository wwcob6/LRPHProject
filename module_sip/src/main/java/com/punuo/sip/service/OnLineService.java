package com.punuo.sip.service;



import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.model.OnLineData;
import com.punuo.sip.request.BaseSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;


@Route(path = ServicePath.PATH_ONLINE)
public class OnLineService extends NormalRequestService<OnLineData>{
    @Override
    protected String getBody(){return null;}

    @Override
    protected void onSuccess(Message msg, OnLineData result) {
        if (result == null) {
            return;
        }
        EventBus.getDefault().post(result);
    }

    @Override
    protected void onError(Exception e) {

    }


    @Override
    public void handleTimeOut(BaseSipRequest baseSipRequest) {

    }
}
