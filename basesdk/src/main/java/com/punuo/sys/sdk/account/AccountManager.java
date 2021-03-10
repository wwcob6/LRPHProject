package com.punuo.sys.sdk.account;

import android.text.TextUtils;

import com.punuo.sys.sdk.account.model.Group;
import com.punuo.sys.sdk.account.model.GroupItem;
import com.punuo.sys.sdk.account.request.GetAllGroupFromUserRequest;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.util.MMKVUtil;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by han.chen.
 * Date on 2019-06-15.
 **/
public class AccountManager {
    private static String userAccount;
    private static String userId; //用户id
    private static String devId; //用户设备id
    private static String bindDevId; //绑定设备id
    private static String bindUserId; //绑定设备的userId
    private static String groupId;
    private static boolean login = false;

    public static void setBindUserId(String bindUserId) {
        AccountManager.bindUserId = bindUserId;
    }

    public static String getBindUserId() {
        return bindUserId;
    }

    public static void setBindDevId(String bindDevId) {
        AccountManager.bindDevId = bindDevId;
    }

    public static String getBindDevId() {
        return bindDevId;
    }

    public static void setGroupId(String groupId) {
        AccountManager.groupId = groupId;
    }

    public static String getGroupId() {
        return groupId;
    }

    public static boolean isLogin() {
        return login;
    }

    public static void setLogin(boolean login) {
        AccountManager.login = login;
    }

    public static void setUserAccount(CharSequence userAccount) {
        if (!TextUtils.isEmpty(userAccount)) {
            AccountManager.userAccount = userAccount.toString();
            MMKVUtil.setString("userAccount", userAccount.toString());
        }
    }

    public static String getUserAccount() {
        if (TextUtils.isEmpty(userAccount)) {
            userAccount = MMKVUtil.getString("userAccount", "");
        }
        return userAccount;
    }

    public static void setUserId(String userId) {
        AccountManager.userId = userId;
    }

    public static String getUserId() {
        return userId;
    }

    public static void setDevId(String devId) {
        AccountManager.devId = devId;
    }

    public static String getDevId() {
        return devId;
    }

    public static void setPassword(CharSequence password) {
        if (!TextUtils.isEmpty(password) && !TextUtils.equals(password, "pass")) {
            MMKVUtil.setString("password", password.toString());
        }
    }

    public static String getPassword() {
        return MMKVUtil.getString("password", "");
    }

    public static void clearAccountData() {
        userAccount = null;
        userId = null;
        devId = null;
        MMKVUtil.removeData("userAccount");
    }

    private static GetAllGroupFromUserRequest mGetAllGroupFromUserRequest;

    //获取用户绑定设备
    public static void getBindDevInfo() {
        if (mGetAllGroupFromUserRequest != null && !mGetAllGroupFromUserRequest.isFinish()) {
            return;
        }
        mGetAllGroupFromUserRequest = new GetAllGroupFromUserRequest();
        mGetAllGroupFromUserRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetAllGroupFromUserRequest.setRequestListener(new RequestListener<Group>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(Group result) {
                if (result != null) {
                    if (result.mGroupItemList != null && !result.mGroupItemList.isEmpty()) {
                        GroupItem groupItem = result.mGroupItemList.get(0);
                        AccountManager.setGroupId(groupItem.groupId);
                        AccountManager.setBindDevId(groupItem.groupName);
                        EventBus.getDefault().post(result);
                    }
                } else {
                    onError(null);
                }
            }

            @Override
            public void onError(Exception e) {
                ToastUtils.showToastShort("获取用户数据失败请重试");
            }
        });
        HttpManager.addRequest(mGetAllGroupFromUserRequest);
    }


}
