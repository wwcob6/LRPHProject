package com.app.request;

import com.punuo.sys.sdk.httplib.BaseRequest;
import com.punuo.sys.sdk.model.PNBaseModel;

public class ChangeCallRequest extends BaseRequest<PNBaseModel> {

    public ChangeCallRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/updateServiceNumber");
    }
}
