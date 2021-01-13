package com.punuo.sip.service;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public class ServicePath {

    public static final String PATH_MEDIA = "/sip/media";
    public static final String PATH_NOTIFY = "/sip/notify";
    public static final String PATH_REGISTER = "/sip/negotiate_response";
    public static final String PATH_LOGIN = "/sip/login_response";
    public static final String PATH_DEV_NOTIFY = "/sip/dev_notify";

    public static final String PATH_ERROR = "/sip/error";
    public static final String PATH_START_VIDEO = "/sip/start_video";
    public static final String PATH_WEIGHT_RESPONSE = "/sip/weight";
    public static final String PATH_ONLINE="/sip/is_online_response";

    public static final String  PATH_FEEDNOW_RESPONSE = "/sip/feed_now_response";

    public static final String PLAN_TO_SIP = "/sip/feed_plan_response";
    public static final String PATH_UPDATE_WEIGHT="/sip/update_weight_response";
    public static final String PATH_GET_FEED_COUNT="/sip/transfer_part";

    //接收设备是否已连上WiFi的消息
    public static final String PATH_WIFI_CONNECTED="/sip/dev_wifi_response";
    //发送WiFi信息成功的回应
    public static final String PATH_SEND_WIFI_MESSAGE="/sip/set_wifi_response";
    //重置成功的回应
    public static final String PATH_RESET="/sip/dev_reset";
    //音量加减的回应
    public static final String PATH_VOLUME="/sip/music_volume_response";
    //音乐播放回复
    public static final String PATH_MUSIC="/sip/play_music_response";

    //未知
    public static final String PATH_XXXX="/sip/session_notify";
}
