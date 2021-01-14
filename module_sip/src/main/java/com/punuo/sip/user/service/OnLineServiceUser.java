package com.punuo.sip.user.service;



import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.model.OnLineData;
import com.punuo.sip.user.request.BaseSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;


@Route(path = UserServicePath.PATH_ONLINE)
public class OnLineServiceUser extends NormalUserRequestService<OnLineData> {
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
