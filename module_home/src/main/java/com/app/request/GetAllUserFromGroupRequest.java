package com.app.request;

import com.punuo.sys.app.home.friendCircle.domain.UserFromGroup;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetAllUserFromGroupRequest extends BaseRequest<UserFromGroup> {
    public GetAllUserFromGroupRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/groups/getAllUserFromGroup");
    }
}
