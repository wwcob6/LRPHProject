package com.punuo.sys.app.message.model;

import com.punuo.sys.sdk.model.PNBaseModel;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class PostNewCommentModel extends PNBaseModel {

    @SerializedName("comments")
    public List<CommentModel> mCommentModels;
}
