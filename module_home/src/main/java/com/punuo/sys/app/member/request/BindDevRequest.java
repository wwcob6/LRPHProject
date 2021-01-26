package com.punuo.sys.app.member.request;

import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/1/15.
 **/
public class BindDevRequest extends BaseRequest<PNBaseModel> {

    public BindDevRequest() {
        setRequestPath("/devs/bindDev");
        setRequestType(RequestType.GET);
    }
}
