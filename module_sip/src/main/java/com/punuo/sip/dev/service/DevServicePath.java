package com.punuo.sip.dev.service;

/**
 * Created by han.chen.
 * Date on 2019-08-20.
 **/
public class DevServicePath {

    public static final String PATH_MEDIA = "/dev/media";
    public static final String PATH_NOTIFY = "/dev/notify";
    public static final String PATH_REGISTER = "/dev/negotiate_response";
    public static final String PATH_LOGIN = "/dev/login_response";
    public static final String PATH_DEV_NOTIFY = "/dev/dev_notify";

    public static final String PATH_ERROR = "/dev/error";
    public static final String PATH_START_VIDEO = "/dev/start_video";
    public static final String PATH_WEIGHT_RESPONSE = "/dev/weight";
    public static final String PATH_ONLINE="/dev/is_online_response";

    public static final String  PATH_FEEDNOW_RESPONSE = "/dev/feed_now_response";

    public static final String PLAN_TO_dev = "/dev/feed_plan_response";
    public static final String PATH_UPDATE_WEIGHT="/dev/update_weight_response";
    public static final String PATH_GET_FEED_COUNT="/dev/transfer_part";

    //接收设备是否已连上WiFi的消息
    public static final String PATH_WIFI_CONNECTED="/dev/dev_wifi_response";
    //发送WiFi信息成功的回应
    public static final String PATH_SEND_WIFI_MESSAGE="/dev/set_wifi_response";
    //重置成功的回应
    public static final String PATH_RESET="/dev/dev_reset";
    //音量加减的回应
    public static final String PATH_VOLUME="/dev/music_volume_response";
    //音乐播放回复
    public static final String PATH_MUSIC="/dev/play_music_response";

    //未知
    public static final String PATH_XXXX="/dev/session_notify";
}
