package com.punuo.sip.request;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

public class SipResetRequest extends BaseSipRequest {
    public SipResetRequest(){
        setSipRequestType(SipRequestType.Notify);
    }
    @Override
    public String getBody(){
        JSONObject body = new JSONObject();
        try {
            body.put("dev_reset", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
