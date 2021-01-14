package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2019-09-21.
 **/
public class SipByeRequest extends BaseUserSipRequest {

    private String mDevId;

    public SipByeRequest(String devId) {
        setSipRequestType(SipRequestType.Notify);
        mDevId = devId;
        setHasResponse(false);
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            body.put("bye_m3u8", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }

    @Override
    public NameAddress getDestNameAddress() {
        String devID = mDevId.substring(0, mDevId.length() - 4).concat("0160"); //设备id后4位替换成0160
        SipURL sipURL = new SipURL(devID, SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(devID, sipURL);
    }
}
