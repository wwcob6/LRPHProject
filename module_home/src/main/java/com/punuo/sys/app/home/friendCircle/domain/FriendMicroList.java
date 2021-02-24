package com.punuo.sys.app.home.friendCircle.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FriendMicroList {
    @SerializedName("total")
    public int total;
    @SerializedName("per_page")
    public int perPage;
    @SerializedName("current_page")
    public int currentPage;
    @SerializedName("last_page")
    public int last_page;
    @SerializedName("data")
    public List<FriendMicroListData> data;
}
