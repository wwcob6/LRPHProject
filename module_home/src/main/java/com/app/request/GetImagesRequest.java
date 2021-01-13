package com.app.request;

import com.app.model.CloudPhoto;
import com.punuo.sys.sdk.httplib.BaseRequest;

public class GetImagesRequest extends BaseRequest<CloudPhoto> {
    public GetImagesRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/getPics");
    }
}
