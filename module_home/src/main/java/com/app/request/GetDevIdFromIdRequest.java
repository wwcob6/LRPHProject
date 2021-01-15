package com.app.request;

import com.app.friendCircleMain.domain.UserDevModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetDevIdFromIdRequest extends BaseRequest<UserDevModel> {

    public GetDevIdFromIdRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/devs/getDevIdFromId");
    }
}
