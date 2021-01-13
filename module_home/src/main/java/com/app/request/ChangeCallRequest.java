package com.app.request;

import com.punuo.sys.sdk.httplib.BaseRequest;

public class ChangeCallRequest extends BaseRequest<String> {

    public ChangeCallRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/updateServiceNumber");
    }
}
