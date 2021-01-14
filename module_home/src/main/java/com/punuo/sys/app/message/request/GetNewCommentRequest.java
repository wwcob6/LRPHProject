package com.punuo.sys.app.message.request;

import com.punuo.sys.app.message.model.PostNewCommentModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class GetNewCommentRequest extends BaseRequest<PostNewCommentModel> {

    public GetNewCommentRequest() {
        setRequestPath("/posts/getNewComments");
        setRequestType(RequestType.GET);
    }

}
