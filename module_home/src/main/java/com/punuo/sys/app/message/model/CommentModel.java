package com.punuo.sys.app.message.model;


import com.google.gson.annotations.SerializedName;

public class CommentModel {
    @SerializedName("comment")
    public String comment;
    @SerializedName("id")
    public String id;
    @SerializedName("postid")
    public String postId;
    @SerializedName("addlike_id")
    public String addLikeId;
    @SerializedName("create_time")
    public String createTime;
    @SerializedName("replyName")
    public String replyName;
    @SerializedName("avatar")
    public String avatar;
    @SerializedName("praisetype")
    public String praiseType;
    @SerializedName("pic")
    public String pic;
    @SerializedName("nickname")
    public String nickName;
}
