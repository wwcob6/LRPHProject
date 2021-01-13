package com.punuo.sip.request;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 * 心跳请求
 **/
public class SipHeartBeatRequest extends BaseSipRequest {

    public SipHeartBeatRequest() {
        setSipRequestType(SipRequestType.Register);
        setTargetResponse("login_response");
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        try {
            body.put("heartbeat_request", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
