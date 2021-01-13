package com.punuo.sip.request;

import org.json.JSONException;
import org.json.JSONObject;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

public class SipControlVolumeRequest extends BaseSipRequest {
    public String mVolume;
    public SipControlVolumeRequest(String volume){
        setSipRequestType(SipRequestType.Notify);
        setHasResponse(false);
        mVolume=volume;
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("volume", mVolume);
            body.put("music_volume", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
