package com.punuo.sip.user.request;

import com.punuo.sip.SipConfig;
import com.punuo.sys.sdk.account.AccountManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;

import fr.arnaudguyon.xmltojsonlib.JsonToXml;

/**
 * Created by han.chen.
 * Date on 2021/1/29.
 **/
public class SipOptionsRequest extends BaseUserSipRequest {
    public SipOptionsRequest() {
        setHasResponse(true);
        setSipRequestType(SipRequestType.Options);
        setTargetResponse("query_response");
    }

    @Override
    public NameAddress getDestNameAddress() {
        String devId = AccountManager.getBindDevId();
        devId = devId.substring(0, devId.length() - 4).concat("0160"); //设备id后4位替换成0160
        SipURL sipURL = new SipURL(devId, SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(devId, sipURL);
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("variable", "MediaInfo_Video");
            value.put("dev_type", "2");
            body.put("query", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
