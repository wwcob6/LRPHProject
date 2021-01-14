package com.punuo.sys.app.home.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class PostNewModel {
    @SerializedName("newsid")
    public String newId;
    @SerializedName("posttype")
    public String postType;
    @SerializedName("title")
    public String title;
    @SerializedName("image")
    public String image;
    @SerializedName("time")
    public String time;
}
