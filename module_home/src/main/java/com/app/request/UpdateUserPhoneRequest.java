package com.app.request;

import com.punuo.sys.sdk.httplib.BaseRequest;
import com.punuo.sys.sdk.model.PNBaseModel;

/**
 * Created by han.chen.
 * Date on 2021/2/5.
 **/
public class UpdateUserPhoneRequest extends BaseRequest<PNBaseModel> {

    public UpdateUserPhoneRequest() {
        setRequestPath("/users/updateUserPhone");
        setRequestType(RequestType.GET);
    }
}
