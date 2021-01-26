package com.punuo.sys.sdk.account.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by 林逸磊 on 2017/12/1.
 */

public class Group {
    @SerializedName("groupList")
    public List<GroupItem> mGroupItemList;
}
