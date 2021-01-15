package com.app.friendCircleMain.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Created by 林逸磊 on 2017/12/1.
 */

public class GroupItem {
    @SerializedName("groupid")
    public String groupId;
    @SerializedName("id")
    public String id;
    @SerializedName("group_name")
    public String groupName;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("total")
    public  String total;
    @SerializedName("create_time")
    public String createTime;
}
