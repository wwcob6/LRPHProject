package com.punuo.sys.app.home.friendCircle.domain;

import com.google.gson.annotations.SerializedName;

public class FirstMicroListFriendPraise {
    @SerializedName("id")
    public String id;
    @SerializedName("sid")
    public String sid;
    @SerializedName("uid")
    public String uid;
    @SerializedName("uname")
    public String uname;
    @SerializedName("nickname")
    public String nickName;
    @SerializedName("praisetype")
    public String praiseType;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        FirstMicroListFriendPraise o = (FirstMicroListFriendPraise) obj;
        return this.nickName.equals(o.nickName);
    }
}
