package com.app.model;

import com.google.gson.annotations.SerializedName;
import com.punuo.sys.app.home.album.model.CloudPhotoCover;
import com.punuo.sys.sdk.model.PNBaseModel;

import java.util.List;

public class PhotoCoverResult extends PNBaseModel {
    @SerializedName("picInfo")
    public List<CloudPhotoCover> mCloudPhotoCover;
}
