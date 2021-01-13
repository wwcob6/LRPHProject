package com.app.request;

import com.app.model.CallModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

public class GetCallRequest extends BaseRequest<CallModel> {

        public GetCallRequest() {
            setRequestType(RequestType.GET);
            setRequestPath("/users/getServiceNumber");
        }
    }
