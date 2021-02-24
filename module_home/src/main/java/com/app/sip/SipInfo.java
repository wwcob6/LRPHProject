package com.app.sip;

import android.net.sip.SipAudioCall;
import android.os.Handler;

import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.message.Message;

/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SipInfo {
    //心跳线程开启与否
    public static boolean running = false;
    //用户端口
    public static int SERVER_PORT_USER = 6061;
    //设备端口
    public static int SERVER_PORT_DEV = 6060;
    //用户注册获取用户ID使用
    public static String REGISTER_ID = "330100000010000190";
    //服务器ip
    public static String serverIp = "sip.qinqingonline.com";

    //验证码
    public static String code;
    //用户账号
    public static String userAccount;

    //用户密码
    public static String passWord;

    //设备id
    public static String devId;



    public static String paddevId;
    //用户id
    public static String userId;
    //用户真实姓名
    public static String userRealname;

    //网络是否连接
    public static boolean isNetworkConnected;
    //用户账号是否存在
    public static boolean isAccountExist;
    //密码错误标志
    public static boolean passwordError;
    //用户登录状态标志
    public static boolean userLogined;
    //设备登录状态标志
    public static boolean devLogined;
    //登录超时标志
    public static boolean loginTimeout;
    //设备登录超时标志
    public static boolean dev_loginTimeout;
    //sip消息From地址
    public static NameAddress user_from;

    //sip消息To地址
    public static NameAddress user_to;

    //sip消息(聊天消息)To好友地址
    public static NameAddress toUser;
    //sip消息(设备)To地址
    public static NameAddress dev_to;
    //sip消息(设备)From地址
    public static NameAddress dev_from;
    //sip消息(用户)请求视频设备地址
    public static NameAddress toDev;
    //用户sip对象
    public static SipUser sipUser;
    //设备sip对象
    public static SipDev sipDev;
    //用户IP电话号码
    public static String userPhoneNumber;
    //一次加密种子
    public static String seed;
    //二次加密种子
    public static String salt;
    //用户心跳回复
    public static boolean user_heartbeatResponse;
    //设备心跳回复
    public static boolean dev_heartbeatResponse;
    //好友数量
    public static int friendCount;
    //设备数量
    public static int devCount;
    //上一个电话对象
    public static SipAudioCall lastCall;

    public static Handler Phone = new Handler();
    //视频信息请求回复
    public static boolean queryResponse;
    //视频请求回复
    public static boolean inviteResponse;
    //视频编码状态
    public static boolean decoding = false;
    //根目录
    public static String localSdCard;

    //是否允许调用摄像头
    public static boolean flag = true;

    public static Message msg;

    public static Handler notifymedia;
    public static boolean isWaitingFeedback = false;
    public static boolean finish=false;
    public static boolean  single=false;


    public static float  bitErrorRate;

    public static String phoneType;

    public static int width;
    public static int height;

    //是否是验证码登录，在修改密码的时候作区别
    public static boolean  isVericodeLogin=false;

}
