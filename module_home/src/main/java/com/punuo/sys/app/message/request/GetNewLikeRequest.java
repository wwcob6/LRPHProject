package com.punuo.sys.app.message.request;

import com.punuo.sys.app.message.model.PostNewLikeModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class GetNewLikeRequest extends BaseRequest<PostNewLikeModel> {

    public GetNewLikeRequest() {
        setRequestPath("/posts/getNewLikes");
        setRequestType(RequestType.GET);
    }

}
