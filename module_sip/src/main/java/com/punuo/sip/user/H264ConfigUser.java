package com.punuo.sip.user;

import com.punuo.sip.user.model.MediaData;
import com.punuo.sip.user.model.QueryResponse;

/**
 * Created by han.chen.
 * Date on 2019-09-18.
 **/
public class H264ConfigUser {

    /**
     * video width
     */
    public static int VIDEO_WIDTH = 640;

    /**
     * video height
     */
    public static int VIDEO_HEIGHT = 480;

    /**
     * video frame rate
     */
    public static int FRAME_RATE = 15;

    public static String rtpIp = "101.69.255.134";
    public static int rtpPort;
    public static byte[] magic;

    public static String resolution;

    public static void initQueryData(QueryResponse queryData) {
        resolution = queryData.resolution;
    }

    public static void initMediaData(MediaData mediaData) {
        rtpIp = mediaData.getIp();
        rtpPort = mediaData.getPort();
        magic = mediaData.getMagic();
    }

    public static byte[] getMagic() {
        return magic;
    }
}
