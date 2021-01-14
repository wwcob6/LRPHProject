package com.punuo.sys.app.message.model;

import com.app.model.PNBaseModel;
import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class CountModel extends PNBaseModel {
    @SerializedName("count")
    public int count;
}
