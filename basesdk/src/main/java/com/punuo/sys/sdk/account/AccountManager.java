package com.punuo.sys.sdk.account;

import android.text.TextUtils;

import com.punuo.sys.sdk.util.MMKVUtil;


/**
 * Created by han.chen.
 * Date on 2019-06-15.
 **/
public class AccountManager {
    private static String userAccount;
    private static String userId;
    private static String devId;

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


}
