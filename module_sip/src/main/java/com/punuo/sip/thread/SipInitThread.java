package com.punuo.sip.thread;

import com.punuo.sip.message.SipMessageFactory;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public class SipInitThread extends Thread {

    @Override
    public void run() {
        super.run();
        SipMessageFactory.init();
    }
}
