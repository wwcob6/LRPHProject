package com.punuo.sys.app.member.request;

import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.httplib.BaseRequest;

/**
 * Created by han.chen.
 * Date on 2021/1/15.
 * 判断目标设备是否已有群组
 **/
public class IsDevBindRequest extends BaseRequest<PNBaseModel> {

    public IsDevBindRequest() {
        setRequestPath("/devs/isDevBinded");
        setRequestType(RequestType.GET);
    }
}
