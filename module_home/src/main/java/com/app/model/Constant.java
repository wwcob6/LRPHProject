package com.app.model;

import com.app.sip.SipInfo;

/**
 * Created by acer on 2016/9/5.
 */
public class Constant {
    //HomeActivity
    public static final int HOME = 0;
    public static final int SHOP = 1;
    public static final int COMMUNITY = 2;
    public static final int MESSAGE = 3;
    public static final int PERSON = 4;

    public static final String FORMT = "http://" + SipInfo.serverIp + ":8000/xiaoyupeihu/public/index.php/";
    public static final String URL_ChPhoneNum = FORMT + "users/updateUserPhone";//改绑定账号
    public static final String URL_Bind = FORMT + "devs/bindDev";//绑定设备
    public static final String URL_joinGroup = FORMT + "groups/joinGroup";//加入群组
    public static final String URL_InquireBind = FORMT + "devs/isDevBinded";//查询是否绑定设备
    public static String groupid;
    public static String groupid1;
    public static String groupid2;
    public static String groupid3;
    public static String devid1;
    public static String devid2;
    public static String devid3;
    public static String currentfriendavatar;
    public static String currentfriendid;
    public static String appdevid1;
    public static String appdevid2;
    public static String appdevid3;

}
