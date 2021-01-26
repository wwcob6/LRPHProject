package com.app.request;

import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-14.
 **/
public class UpdateNotifyRequest extends BaseRequest<PNBaseModel> {
    public UpdateNotifyRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/messageNotify");
    }
}
