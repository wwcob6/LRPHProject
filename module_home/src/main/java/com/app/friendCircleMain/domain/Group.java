package com.app.friendCircleMain.domain;

import java.util.List;

/**
 * Created by 林逸磊 on 2017/12/1.
 */

public class Group extends MyBaseBean{
    public List<GroupItem> mGroupItem;

    public List<GroupItem> getGroupItem() {
        return mGroupItem;
    }

    public void setGroupItem(List<GroupItem> groupItem) {
        this.mGroupItem = groupItem;
    }
}
