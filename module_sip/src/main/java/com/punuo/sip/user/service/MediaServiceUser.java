package com.punuo.sip.user.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.H264Config;
import com.punuo.sip.user.model.MediaData;
import com.punuo.sip.user.request.BaseUserSipRequest;
import com.punuo.sys.sdk.util.ToastUtils;

import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = UserServicePath.PATH_MEDIA)
public class MediaServiceUser extends NormalUserRequestService<MediaData> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, MediaData result) {
        H264Config.initMediaData(result);
        Log.v(TAG, "没错,你已经拿到视频通道的ip和port了");
    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseUserSipRequest baseUserSipRequest) {
        ToastUtils.showToast("视频请求失败，请稍后重试");
    }
}
