package com.punuo.sys.sdk.router;

/**
 * Created by han.chen.
 * Date on 2021/1/12.
 **/
public class HomeRouter {
    private static final String PREFIX = "/home";

    //账号注册页面
    public static final String ROUTER_REGISTER_ACTIVITY = PREFIX + "/RegisterAccountActivity";
    //登陆页面
    public static final String ROUTER_LOGIN_ACTIVITY = PREFIX + "/LoginActivity";
    //个人中心/相册
    public static final String ROUTER_CLOUD_ALBUM_ACTIVITY = PREFIX + "/CloudAlbumActivity";
}
