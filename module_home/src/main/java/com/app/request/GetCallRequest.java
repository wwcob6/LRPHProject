package com.app.request;

import com.app.model.CallModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

import java.util.List;

public class GetCallRequest extends BaseRequest<List<CallModel>> {

        public GetCallRequest() {
            setRequestType(RequestType.GET);
            setRequestPath("/users/getServiceNumber");
        }
    }
