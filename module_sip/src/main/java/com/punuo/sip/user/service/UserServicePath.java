package com.punuo.sip.user.service;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public class UserServicePath {

    public static final String PATH_MEDIA = "/user/media";
    public static final String PATH_NOTIFY = "/user/notify";
    public static final String PATH_REGISTER = "/user/negotiate_response";
    public static final String PATH_LOGIN = "/user/login_response";
    public static final String PATH_DEV_NOTIFY = "/user/dev_notify";

    public static final String PATH_ERROR = "/user/error";
    public static final String PATH_START_VIDEO = "/user/start_video";
    public static final String PATH_WEIGHT_RESPONSE = "/user/weight";
    public static final String PATH_ONLINE="/user/is_online_response";

    public static final String  PATH_FEEDNOW_RESPONSE = "/user/feed_now_response";

    public static final String PLAN_TO_user = "/user/feed_plan_response";
    public static final String PATH_UPDATE_WEIGHT="/user/update_weight_response";
    public static final String PATH_GET_FEED_COUNT="/user/transfer_part";

    //接收设备是否已连上WiFi的消息
    public static final String PATH_WIFI_CONNECTED="/user/dev_wifi_response";
    //发送WiFi信息成功的回应
    public static final String PATH_SEND_WIFI_MESSAGE="/user/set_wifi_response";
    //重置成功的回应
    public static final String PATH_RESET="/user/dev_reset";
    //音量加减的回应
    public static final String PATH_VOLUME="/user/music_volume_response";
    //音乐播放回复
    public static final String PATH_MUSIC="/user/play_music_response";

    //未知
    public static final String PATH_XXXX="/user/session_notify";
}
