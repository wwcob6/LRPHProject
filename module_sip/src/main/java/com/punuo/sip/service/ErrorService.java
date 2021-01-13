package com.punuo.sip.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.event.LoginFailEvent;
import com.punuo.sip.request.BaseSipRequest;
import com.punuo.sys.sdk.httplib.ErrorTipException;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.BaseSipResponses;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-24.
 **/
@Route(path = ServicePath.PATH_ERROR)
public class ErrorService extends NormalRequestService<String> {

    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, String result) {
        int code = msg.getStatusLine().getCode();
        if (code == 100) {
            return;
        } else if (code == 401) {
            EventBus.getDefault().post(new LoginFailEvent());
        } else if (code == 400) {
            return;
        } else {
            HandlerExceptionUtils.handleException(new ErrorTipException(BaseSipResponses.reasonOf(code)));
        }
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseSipRequest baseSipRequest) {

    }
}
