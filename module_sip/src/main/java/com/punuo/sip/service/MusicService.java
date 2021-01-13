package com.punuo.sip.service;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.punuo.sip.model.MusicData;
import com.punuo.sip.request.BaseSipRequest;

import org.zoolu.sip.message.Message;

@Route(path = ServicePath.PATH_MUSIC)
public class MusicService extends NormalRequestService<MusicData>{
    @Override
    protected String getBody() {
        return null;
    }

    @Override
    protected void onSuccess(Message msg, MusicData result) {

    }

    @Override
    protected void onError(Exception e) {

    }

    @Override
    public void handleTimeOut(BaseSipRequest baseSipRequest) {

    }
}
