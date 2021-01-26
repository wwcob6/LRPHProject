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
 * Date on 2021/1/26.
 **/
public class SipListUpdateRequest extends BaseUserSipRequest {

    public SipListUpdateRequest() {
        setHasResponse(false);
        setSipRequestType(SipRequestType.Notify);
    }

    @Override
    public NameAddress getDestNameAddress() {
        SipURL sipURL = new SipURL(AccountManager.getBindDevId(), SipConfig.getServerIp(), SipConfig.getUserPort());
        return new NameAddress(AccountManager.getBindDevId(), sipURL);
    }

    @Override
    public String getBody(){
        JSONObject body = new JSONObject();
        JSONObject value = new JSONObject();
        try {
            value.put("addevent", "addsuccess");
            body.put("list_update", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonToXml jsonToXml = new JsonToXml.Builder(body).build();
        return jsonToXml.toFormattedString();
    }
}
