package com.punuo.sys.sdk.account.request;


import com.punuo.sys.sdk.account.model.Group;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2019/5/28.
 **/
public class GetAllGroupFromUserRequest extends BaseRequest<Group> {

    public GetAllGroupFromUserRequest() {
        setRequestType(RequestType.GET);
        setRequestPath("/users/getAllGroupFromUser");
    }
}
