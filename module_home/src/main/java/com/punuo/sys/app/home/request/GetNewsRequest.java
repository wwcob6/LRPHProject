package com.punuo.sys.app.home.request;

import com.punuo.sys.app.home.model.PostNewModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class GetNewsRequest extends BaseRequest<List<PostNewModel>> {

    public GetNewsRequest() {
        setRequestPath("/posts/getNews");
        setRequestType(RequestType.GET);
    }
}
