package com.app.request;

import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019-06-03.
 **/
public class DeleteAddressRequest extends BaseRequest<PNBaseModel> {

    public DeleteAddressRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/deleteAddress");
    }
}
