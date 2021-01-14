package com.punuo.sip.user.service;



import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.model.ResetData;
import com.punuo.sip.user.request.BaseUserSipRequest;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

@Route(path = UserServicePath.PATH_RESET)
public class ResetServiceUser extends NormalUserRequestService<ResetData> {
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
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {

    }
}
