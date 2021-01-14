package com.punuo.sys.app.message.request;

import com.punuo.sys.app.message.model.CountModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class GetNewLikeCountRequest extends BaseRequest<CountModel> {

    public GetNewLikeCountRequest() {
        setRequestPath("/posts/countNewLikes");
    }
}
