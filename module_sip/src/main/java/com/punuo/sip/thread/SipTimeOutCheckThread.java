package com.punuo.sip.thread;

import com.punuo.sip.request.BaseSipRequest;

import org.zoolu.sip.provider.TransportConnId;

/**
 * Created by han.chen.
 * Date on 2019-09-23.
 **/
public class SipTimeOutCheckThread extends Thread {
    private BaseSipRequest mSipRequest;
    private TransportConnId mTransportConnId;
    public SipTimeOutCheckThread(TransportConnId id, BaseSipRequest sipRequest) {
        mTransportConnId = id;
        mSipRequest = sipRequest;
    }
}
