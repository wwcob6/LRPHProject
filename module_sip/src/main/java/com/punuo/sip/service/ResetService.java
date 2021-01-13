package com.punuo.sip.service;



import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.model.ResetData;
import com.punuo.sip.request.BaseSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

@Route(path = ServicePath.PATH_RESET)
public class ResetService extends NormalRequestService<ResetData>{
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, ResetData result) {
        EventBus.getDefault().post(result);
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseSipRequest baseSipRequest) {

    }
}
