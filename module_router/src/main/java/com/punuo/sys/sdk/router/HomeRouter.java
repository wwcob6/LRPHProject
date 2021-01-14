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
    //设置密码页面
    public static final String ROUTER_APPLY_PASSWORD_ACTIVITY = PREFIX + "/ApplyPasswordActivity";
    //忘记密码页面
    public static final String ROUTER_FORGET_PASSWORD_ACTIVITY = PREFIX + "/ForgetPasswordActivity";
    //验证码登陆页面
    public static final String ROUTER_VERIFY_CODE_LOGIN_ACTIVITY = PREFIX + "/VerifyCodeLoginActivity";
    //个人中心/相册
    public static final String ROUTER_CLOUD_ALBUM_ACTIVITY = PREFIX + "/CloudAlbumActivity";
    //首页
    public static final String ROUTER_HOME_ACTIVITY = PREFIX + "/HomeActivity";
    //系统通知
    public static final String ROUTER_SYSTEM_NOTIFY_ACTIVITY = PREFIX + "/SystemNotifyActivity";
}
