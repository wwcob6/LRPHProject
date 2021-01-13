package com.punuo.sip.request;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

public class SipSendWiFiDataRequest extends BaseSipRequest{
    private String minput;
    private String mpwd;
    public SipSendWiFiDataRequest(String input,String pwd){
        setSipRequestType(SipRequestType.Notify);
        minput=input;
        mpwd=pwd;
    }
    @Override
    public String getBody(){
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("wifiname", minput);
            value.put("pwd",mpwd);
            body.put("set_wifi", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }

}
