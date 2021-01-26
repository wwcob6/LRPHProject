package com.app.request;

import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-04.
 **/
public class UnBindDevRequest extends BaseRequest<PNBaseModel> {

    public UnBindDevRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/groups/leaveGroup");
    }
}
