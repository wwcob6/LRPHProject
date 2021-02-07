package com.punuo.sys.app.home.friendCircle.domain;

import java.util.List;

public class FriendMicroList extends MyBaseBean {
    public int total;
    public int per_page;
    public int current_page;
    public int last_page;
    public List<FriendMicroListDatas> data;
}
