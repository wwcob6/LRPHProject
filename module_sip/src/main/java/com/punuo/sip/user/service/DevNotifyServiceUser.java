package com.punuo.sip.user.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.user.model.DevNotifyData;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2019-09-29.
 **/
@Route(path = UserServicePath.PATH_DEV_NOTIFY)
public class DevNotifyServiceUser extends NormalUserRequestService<DevNotifyData> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, DevNotifyData result) {
        if (result != null && result.mDevInfo != null) {
            String info = "devid = " + result.mDevInfo.devId + "live = " + result.mDevInfo.live;
//            ToastUtils.showToast(info);
            EventBus.getDefault().post(result);

            //TODO 根据后端返回的设备在线信息，再去做相应的UI展示
        }
    }

    @Override
    protected void onError(Exception e) {
        HandlerExceptionUtils.handleException(e);
    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {

    }
}
