package com.punuo.sip;

import org.zoolu.sip.address.NameAddress;

/**
 * Created by han.chen.
 * Date on 2019/4/23.
 **/
public interface ISipConfig {

    String getServerIp();

    int getPort();

    NameAddress getServerAddress();

    NameAddress getUserRegisterAddress();

    NameAddress getUserNormalAddress();

    void reset();
}
