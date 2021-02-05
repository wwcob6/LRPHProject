package com.punuo.sip.dev.service;

import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.H264Config;
import com.punuo.sip.dev.H264ConfigDev;
import com.punuo.sip.dev.event.MonitorEvent;
import com.punuo.sip.dev.model.MediaData;
import com.punuo.sip.dev.request.BaseDevSipRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipQueryRequest;
import com.punuo.sys.sdk.account.AccountManager;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
@Route(path = DevServicePath.PATH_MEDIA)
public class MediaServiceDev extends NormalDevRequestService<MediaData> {
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, MediaData result) {
        H264ConfigDev.initMediaData(result);
        onResponse(msg);
        //TODO 启动视频编码
        if (H264Config.monitorType == H264Config.DOUBLE_MONITOR_NEGATIVE) {
            Log.v(TAG, "响应双向视频");
            SipQueryRequest request = new SipQueryRequest(H264ConfigDev.targetDevId);
            SipUserManager.getInstance().addRequest(request);
        } else if (H264Config.monitorType == H264Config.DOUBLE_MONITOR_POSITIVE) {
            Log.v(TAG, "主动双向视频");
            EventBus.getDefault().post(new MonitorEvent(H264Config.DOUBLE_MONITOR_POSITIVE, AccountManager.getBindDevId()));
        }

    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseDevSipRequest baseSipRequest) {

    }
}
