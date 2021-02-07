package com.punuo.sys.app.home.friendCircle.domain;

import java.util.List;

/**
 * Created by 林逸磊 on 2017/11/19.
 */

public class FirstMicroListDatasFirendimageList extends MyBaseBean{
    List<FirstMicroListFriendImage> pic_post;

    public List<FirstMicroListFriendImage> getPic_post() {
        return pic_post;
    }

    public void setPic_post(List<FirstMicroListFriendImage> pic_post) {
        this.pic_post = pic_post;
    }
}
