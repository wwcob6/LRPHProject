package com.punuo.sys.app.member.request;

import com.app.model.PNBaseModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/1/15.
 **/
public class JoinDevRequest extends BaseRequest<PNBaseModel> {

    public JoinDevRequest() {
        setRequestPath("/groups/joinGroup");
        setRequestType(RequestType.GET);
    }
}
