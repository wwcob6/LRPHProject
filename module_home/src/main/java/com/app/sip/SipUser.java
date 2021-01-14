package com.app.sip;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import com.app.R;
import com.app.groupvoice.GroupInfo;
import com.app.model.Device;
import com.app.model.MessageEvent;
import com.app.model.Msg;
import com.app.model.MyFile;
import com.app.video.VideoInfo;
import com.punuo.sys.sdk.sercet.SHA1;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.zoolu.sip.address.NameAddress;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProvider;
import org.zoolu.sip.provider.Transport;
import org.zoolu.sip.provider.TransportConnId;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;



/**
 * Author chzjy
 * Date 2016/12/19.
 */

public class SipUser extends SipProvider {
    MediaPlayer music;
    private Context context;
    public static String TAG = "SipUser";
    public static String[] PROTOCOLS = {"udp"};
    //线程池
    private ExecutorService pool = Executors.newFixedThreadPool(3);
    //用户或设备上线监听
    private LoginNotifyListener loginNotifyListener;
    //密码修改监听
    private ChangePWDListener changePWDListener;
    //消息监听
    private MessageListener messageListener;
    //总监听
    private TotalListener totalListener;
    //主界面底部消息数量监听
    private BottomListener bottomListener;
    //app图标路径
    private String sdPath;
    private ClusterNotifyListener clusterNotifyListener;

//    private QinliaoUpdateListener qinliaoUpdateListener;




    public SipUser(String via_addr, int host_port, Context context) {
        super(via_addr, host_port, PROTOCOLS, null);
        this.context = context;
        sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PNS9/download/icon/";

    }

    public TransportConnId sendMessage(Message msg) {
        return sendMessage(msg, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
    }

    public TransportConnId sendMessage(final Message msg, final String destAddr, final int destPort) {
        Log.i(TAG, "<----------send sip message---------->");
        Log.i(TAG, msg.toString());
        TransportConnId id = null;
        try {
                id = pool.submit(new Callable<TransportConnId>() {
                    public TransportConnId call() {
                        return sendMessage(msg, "udp", destAddr, destPort, 0);
                    }
                }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }catch (RejectedExecutionException e){

        }
        return id;
    }

    //结束线程池
    public void shutdown(){
        pool.shutdown();
    }
    public synchronized void onReceivedMessage(Transport transport, Message msg) {
        Log.i(TAG, "<----------received sip message---------->");
        Log.i(TAG, msg.toString());
        //sip消息来源的RemoteProt,6060为设备,6061为用户
        int port = msg.getRemotePort();
        if (port == SipInfo.SERVER_PORT_USER) {
            Log.i(TAG, "PORT = " + port);
            if (msg.isRequest()) {// 请求消息
                requestParse(msg);
            } else { // 响应消息
                int code = msg.getStatusLine().getCode();
                switch (code) {
                    case 200:
                        responseParse(msg);
                        break;
                    case 401://密码错误
                        SipInfo.loginTimeout = false;
                        SipInfo.isAccountExist = true;
                        SipInfo.passwordError = true;
                        break;
                    case 402://账号不存在
                        SipInfo.passwordError = false;
                        SipInfo.loginTimeout = false;
                        SipInfo.isAccountExist = false;
                        break;
                }

            }
        }
    }

    //请求解析
    private boolean requestParse(final Message msg) {
        String body = msg.getBody();
        if (body != null) {
            StringReader sr = new StringReader(body);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document document;
            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(is);
                Element root = document.getDocumentElement();
                final String type = root.getTagName();
                switch (type) {
                    case "dev_notify"://设备列表
                        Element devsElement = (Element) root.getElementsByTagName("devs").item(0);
                        Element loginElement = (Element) root.getElementsByTagName("login").item(0);
                        if (devsElement != null) {
                            NodeList devs = devsElement.getElementsByTagName("dev");
                            for (int i = 0; i < devs.getLength(); i++) {
                                Device device = new Device();
                                Element devElement = (Element) devs.item(i);
                                Element devIdElement = (Element) devElement.getElementsByTagName("devid").item(0);
                                Element nameElement = (Element) devElement.getElementsByTagName("name").item(0);
                                Element phoneElement = (Element) devElement.getElementsByTagName("phone").item(0);
                                Element devTypeElement = (Element) devElement.getElementsByTagName("dev_type").item(0);
                                Element liveElement = (Element) devElement.getElementsByTagName("live").item(0);
                                device.setDevId(devIdElement.getFirstChild().getNodeValue());
                                device.setName(nameElement.getFirstChild().getNodeValue());
                                device.setPhoneNum(phoneElement.getFirstChild().getNodeValue());
                                device.setDevType(devTypeElement.getFirstChild().getNodeValue());
                                if (liveElement.getFirstChild().getNodeValue().equals("1")) {
                                    device.setLive(true);
                                } else {
                                    device.setLive(false);
                                }
                                if (!device.getDevId().equals(SipInfo.devId)) {
                                    SipInfo.devList.add(device);
                                }
                            }
                            if (loginNotifyListener != null) {
                                loginNotifyListener.onDevNotify();
                            }
                            Log.i(TAG, "当前设备数：" + SipInfo.devList.size());
                            SipInfo.sipUser.sendMessage(SipMessageFactory.createResponse(msg, 200, "OK", ""));
                            return true;
                        } else {
                            Element devIdElement = (Element) loginElement.getElementsByTagName("devid").item(0);
                            Element liveElement = (Element) loginElement.getElementsByTagName("live").item(0);
                            String devid = devIdElement.getFirstChild().getNodeValue();
                            if (!devid.equals(SipInfo.devId)) {
                                Device device = new Device();
                                device.setDevId(devid);
                                int index = SipInfo.devList.indexOf(device);
                                if (index != -1) {
                                    if (liveElement.getFirstChild().getNodeValue().equals("1")) {
                                        SipInfo.devList.get(index).setLive(true);
                                    } else {
                                        SipInfo.devList.get(index).setLive(false);
                                    }
                                }
                                if (loginNotifyListener != null) {
                                    loginNotifyListener.onDevNotify();
                                }
                            }
                            return true;
                        }
                        //亲聊新成员上线
                    case"user_online":
                        Element liveElement=(Element)root.getElementsByTagName("live").item(0);
                        String islive=liveElement.getFirstChild().getNodeValue();
                        if(islive.equals("True")){
                            NodeList users=root.getElementsByTagName("login");
                            list.clear();
                            for (int i = 0; i < users.getLength(); i++) {
                                Log.i(TAG, "111");
//                                Cluster cluster = new Cluster();
                                Element logi1Element = (Element) users.item(i);
                                Element userid1Element = (Element) logi1Element.getElementsByTagName("userid").item(0);
                                list.add(userid1Element.getFirstChild().getNodeValue());
                                Log.i(TAG, "qinliao" + list.get(i));
                            }
                            qinliaoUpdateListener.stausOnUpdate();
                        }else
                            if(islive.equals("False")) {
                                NodeList users = root.getElementsByTagName("login");
                                list.clear();
                                for (int i = 0; i < users.getLength(); i++) {
                                    Log.i(TAG, "111");
//                                Cluster cluster = new Cluster();
                                    Element logi1Element = (Element) users.item(i);
                                    Element userid1Element = (Element) logi1Element.getElementsByTagName("userid").item(0);
                                    list.add(userid1Element.getFirstChild().getNodeValue());
                                    Log.i(TAG, "qinliao" + list.get(i));
                                }
                                qinliaoUpdateListener.stausOffUpdate();
                            }


                        break;
                  //服务器回复亲聊在线成员userid
                    case "cluster_users":
                            Log.i("qqq","111");
                            NodeList  clusters = root.getElementsByTagName("cluster_user");
                            Log.d(TAG, "requestParse111: " + clusters.getLength());
                            list.clear();
                            for (int i = 0; i < clusters.getLength(); i++) {
                                Log.i(TAG, "111");
                                Element clusterElement = (Element) clusters.item(i);
                                Element nameElement = (Element) clusterElement.getElementsByTagName("name").item(0);
                                list.add(nameElement.getFirstChild().getNodeValue());
                                Log.i(TAG, "qinliao" + list.get(i));
                            }
                            list.add(SipInfo.userId);

                        qinliaoUpdateListener.stausOnUpdate();
                        break;
                    case "alarm":{
                        Log.i("maomaomao","111");
                        Element useridElement = (Element) root.getElementsByTagName("userId").item(0);

                        final String userid = useridElement.getFirstChild().getNodeValue();
                        SipURL remote = new SipURL(userid, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                        SipInfo.toUser = new NameAddress(userid, remote);
                        SipInfo.sipUser.sendMessage(SipMessageFactory.createNotifyRequest(SipInfo.sipUser, SipInfo.toUser
                                , SipInfo.user_from, BodyFactory.createalarmResBody(200,String.valueOf(System.currentTimeMillis()))));
                        EventBus.getDefault().post(new MessageEvent("警报"));
                        music = MediaPlayer.create(context, R.raw.alarm);
                        music.start();
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "requestParse: ", e);
            }
        } else {
            Log.i(TAG + "requestParse", "BODY IS NULL");
            return true;
        }
        return false;
    }

    //响应解析
    private boolean responseParse(Message msg) {
        String body = msg.getBody();
        if (body != null) {
            StringReader sr = new StringReader(body);
            InputSource is = new InputSource(sr);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder;
            Document document;
            try {
                builder = factory.newDocumentBuilder();
                document = builder.parse(is);
                Element root = document.getDocumentElement();
                final String type = root.getTagName();
                Element codeElement;
                String code;
                switch (type) {
                    case "negotiate_response":/*注册第一步响应*/
                        Element seedElement = (Element) root.getElementsByTagName("seed").item(0);
                        Element userIdElement = (Element) root.getElementsByTagName("user_id").item(0);
                        if (userIdElement != null) {//如果掉线服务器会当成设备注册第一步
                            Element saltElement = (Element) root.getElementsByTagName("salt").item(0);
                            Element phoneNumElement = (Element) root.getElementsByTagName("phone_num").item(0);
                            Element realNameElement = (Element) root.getElementsByTagName("real_name").item(0);
                            SipInfo.userId = userIdElement.getFirstChild().getNodeValue();
                            //SipInfo.devId= TelephonyManager.getDeviceId():
                            SipInfo.userRealname = realNameElement.getFirstChild().getNodeValue();
                            SipURL local = new SipURL(SipInfo.userId, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                            SipInfo.user_from.setAddress(local);
                            SipInfo.userPhoneNumber = phoneNumElement.getFirstChild().getNodeValue();
                            SipInfo.seed = seedElement.getFirstChild().getNodeValue();
                            SipInfo.salt = saltElement.getFirstChild().getNodeValue();
                            Log.i(TAG, "收到用户注册第一步响应");
                            SHA1 sha1 = SHA1.getInstance();
                            if((SipInfo.passWord==null)||(SipInfo.passWord.equals(""))){
                                Message register = SipMessageFactory.createRegisterRequest(
                                        SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from,
                                        BodyFactory.createRegisterBody("pass"));
                                SipInfo.sipUser.sendMessage(register);
                            }else {
                                String password = sha1.hashData(SipInfo.salt + SipInfo.passWord);
                                password = sha1.hashData(SipInfo.seed + password);
                                Message register = SipMessageFactory.createRegisterRequest(
                                        SipInfo.sipUser, SipInfo.user_to, SipInfo.user_from,
                                        BodyFactory.createRegisterBody(password));
                                SipInfo.sipUser.sendMessage(register);
                            }


                        } else {
                            Log.e(TAG, "掉线");
                            SipInfo.userLogined = false;
                            SipURL local = new SipURL(SipInfo.REGISTER_ID, SipInfo.serverIp, SipInfo.SERVER_PORT_USER);
                            NameAddress from = new NameAddress(SipInfo.userAccount, local);
                            Message register = SipMessageFactory.createRegisterRequest(
                                    SipInfo.sipUser, SipInfo.user_to, from);
                            SipInfo.sipUser.sendMessage(register);
                        }
                        return true;
                    case "login_response":
                        if (SipInfo.userLogined) {
                            SipInfo.user_heartbeatResponse = true;
                            Log.i(TAG, "收到用户心跳回复");
                            Log.i(TAG, "用户在线!");
                        } else {
                            //获取电源锁,用于防止手机静默之后,心跳线程暂停
                            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                            GroupInfo.wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, context.getClass().getCanonicalName());
                            GroupInfo.wakeLock.acquire();
                            SipInfo.userLogined = true;
                            SipInfo.loginTimeout = false;
                            Log.i(TAG, "用户注册成功");
//                            /*请求好友列表*/
//                            SipInfo.friendCount = 0;
//                            SipInfo.friendList.clear();
//                            SipInfo.friends.clear();

//                            SipInfo.sipUser.sendMessage(SipMessageFactory.createSubscribeRequest(SipInfo.sipUser,
//                                    SipInfo.user_to, SipInfo.user_from, BodyFactory.createFriendsQueryBody(0, 0)));

                        }
                        return true;
                    case "group_bind_code":
                        codeElement = (Element) root.getElementsByTagName("code").item(0);
                        code = codeElement.getFirstChild().getNodeValue();
                        if (code.equals("200")) {
//                            Element idElement = (Element) root.getElementsByTagName("padduserid").item(0);
//                            SipInfo.paduserid = idElement.getFirstChild().getNodeValue();
//                            Element portElement=(Element)root.getElementsByTagName("cluster_id").item(0);
//                            GroupInfo.port=Integer.parseInt(portElement.getFirstChild().getNodeValue())-1+7000;
                            Log.i(TAG, "用户总数：" + SipInfo.friendCount);
                        }
                    case "port_get":
                        Element portElement = (Element) root.getElementsByTagName("port").item(0);
                        String port= portElement.getFirstChild().getNodeValue();
                        GroupInfo.port=Integer.parseInt(port);
                        Log.d(TAG,"亲聊端口号为"+GroupInfo.port);
                        break;


                    case "friends_query":
                        codeElement = (Element) root.getElementsByTagName("code").item(0);
                        code = codeElement.getFirstChild().getNodeValue();
                        if (code.equals("200")) {
                            Element numElement = (Element) root.getElementsByTagName("num").item(0);
                            SipInfo.friendCount = Integer.parseInt(numElement.getFirstChild().getNodeValue());
                            Log.i(TAG, "用户总数：" + SipInfo.friendCount);
                        }
                        return true;
                    case "dev_count"://设备数量
                        SipInfo.devCount = Integer.parseInt(root.getFirstChild().getNodeValue());
                        Log.i(TAG, "设备总数：" + SipInfo.devCount);
                        return true;
                    case "update_password_response":
                        Element resultElement = (Element) root.getElementsByTagName("result").item(0);
                        String result = resultElement.getFirstChild().getNodeValue();
                        if (result.equals("0")) {
                            if (changePWDListener != null) {
                                changePWDListener.onChangePWD(1);
                            }
                        } else {
                            if (changePWDListener != null) {
                                changePWDListener.onChangePWD(0);
                            }
                        }
                        return true;
                    case "query_response":
                        Element resolutionElement = (Element) root.getElementsByTagName("resolution").item(0);
                        VideoInfo.resultion=resolutionElement.getFirstChild().getNodeValue();
                        switch (VideoInfo.resultion) {
                            case "CIF":
                                VideoInfo.width = 352;
                                VideoInfo.height = 288;
                                VideoInfo.videoType = 2;

                                break;
                            case "QCIF_MOBILE_SOFT":
                                VideoInfo.width = 176;
                                VideoInfo.height = 144;
                                VideoInfo.videoType = 3;
                                break;
                            case "MOBILE_S6":
                                VideoInfo.width = 320;
                                VideoInfo.height = 240;
                                VideoInfo.videoType = 4;
                                break;
                            case "MOBILE_S9":
                                VideoInfo.width = 320;
                                VideoInfo.height = 240;
                                VideoInfo.videoType = 5;
                                break;
                            default:
                                break;
                        }
                        SipInfo.queryResponse = true;
                        return true;
                    case "media":
                        Element peerElement = (Element) root.getElementsByTagName("peer").item(0);
                        Element magicElement = (Element) root.getElementsByTagName("magic").item(0);
                        String peer = peerElement.getFirstChild().getNodeValue();
                        VideoInfo.rtpIp = peer.substring(0, peer.indexOf("UDP")).trim();
                        VideoInfo.rtpPort = Integer.parseInt(peer.substring(peer.indexOf("UDP") + 3).trim());
                        String magic = magicElement.getFirstChild().getNodeValue();
                        VideoInfo.magic = new byte[magic.length() / 2 + magic.length() % 2];
                        for (int i = 0; i < VideoInfo.magic.length; i++) {
                            try {
                                VideoInfo.magic[i] = (byte) (0xff & Integer.parseInt(magic.substring(i * 2, i * 2 + 2), 16));
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        SipInfo.inviteResponse = true;
                         /*
                        VideoInfo.media_info_ip = VideoInfo.rtpIp;
                        VideoInfo.media_info_port = VideoInfo.rtpPort;
                        VideoInfo.media_info_magic = VideoInfo.magic;
                        */

                        return true;
                }
            } catch (Exception e) {
                Log.e(TAG, "responseParse: ", e);
            }
        } else {
            Log.i(TAG + "responseParse", "BODY IS NULL");
            return true;
        }
        return false;
    }


    public void setLoginNotifyListener(LoginNotifyListener loginNotifyListener) {
        this.loginNotifyListener = loginNotifyListener;
    }

    public interface LoginNotifyListener {
        void onDevNotify();

        void onUserNotify();
    }

    public void setChangePWDListener(ChangePWDListener changePWDListener) {
        this.changePWDListener = changePWDListener;
    }

    public interface ChangePWDListener {
        void onChangePWD(int i);
    }


    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public interface MessageListener {
        void onReceivedMessage(Msg msg);
    }

    public interface TotalListener {
//        void onReceivedTotalNotice(Notice notice);

        void onReceivedTotalMessage(Msg msg);

        void onReceivedTotalFileshare(MyFile myfile);
    }

    public void setTotalListener(TotalListener totalListener) {
        this.totalListener = totalListener;
    }

    public interface BottomListener {
//        void onReceivedBottomNotice(Notice notice);

        void onReceivedBottomMessage(Msg msg);

        void onReceivedBottomFileshare(MyFile myfile);
    }

    public void setBottomListener(BottomListener bottomListener) {
        this.bottomListener = bottomListener;
    }
    public interface StopMonitor{
        void stopVideo();
    }

    public StopMonitor monitor=new StopMonitor() {
        @Override
        public void stopVideo() {
        }
    };
    public void setMonitor(StopMonitor monitor){
        this.monitor = monitor;
    }

//    public static String[] qinliaouserid=new String[8];
    public static List<String> list=new ArrayList<>();

    public interface ClusterNotifyListener{
        void onNotify();
    }

    //定义一个接口
    public interface QinliaoUpdateListener{
        void stausOnUpdate();
        void stausOffUpdate();
    }

    //创建接口类型变量
    public QinliaoUpdateListener qinliaoUpdateListener;


    //暴露接口设置得方法
    public void setQinliaoUpdateListener(QinliaoUpdateListener qinliaoUpdateListener){
        this.qinliaoUpdateListener=qinliaoUpdateListener;
    }

}
