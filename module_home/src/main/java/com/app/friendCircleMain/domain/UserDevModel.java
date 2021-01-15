package com.app.friendCircleMain.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 林逸磊 on 2018/1/31.
 */

public class UserDevModel extends MyBaseBean{
    @SerializedName("devid")
    public List<String> devList;
}
