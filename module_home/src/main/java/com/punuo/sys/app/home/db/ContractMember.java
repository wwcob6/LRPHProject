package com.punuo.sys.app.home.db;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by han.chen.
 * Date on 2021/2/7.
 **/
@Database(name = ContractMember.NAME, version = ContractMember.VERSION)
public class ContractMember {
    public static final String NAME = "member";
    public static final int VERSION = 1;
}
