package com.punuo.sys.app.message.request;

import com.punuo.sys.app.message.model.CountModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class GetNewCommentCountRequest extends BaseRequest<CountModel> {

    public GetNewCommentCountRequest() {
        setRequestPath("/posts/countNewComments");
    }
}
