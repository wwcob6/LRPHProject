package com.app.model;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.sdk.model.PNBaseModel;

public class CallModel extends PNBaseModel {

    @SerializedName("id")
    public int id;

    @SerializedName("devid")
    public String devid;

    @SerializedName("housekeep")
    public String housekeep;

    @SerializedName("orderfood")
    public String orderfood;

    @SerializedName("property")
    public String property;

}
