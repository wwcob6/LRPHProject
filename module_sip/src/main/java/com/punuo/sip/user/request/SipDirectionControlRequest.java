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
 * Date on 2021/2/3.
 **/
public class SipDirectionControlRequest extends BaseUserSipRequest {
    private final String operate;
    public SipDirectionControlRequest(String operate) {
        setSipRequestType(SipRequestType.Notify);
        setHasResponse(false);
        this.operate = operate;
    }

    @Override
    public String getBody() {
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("operate", operate);
            body.put("direction_control", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }

    @Override
    public NameAddress getDestNameAddress() {
        String devId = AccountManager.getBindDevId().substring(0, AccountManager.getBindDevId().length() - 4).concat("0160");
        SipURL sipURL = new SipURL(devId, SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(devId, sipURL);
    }
}
