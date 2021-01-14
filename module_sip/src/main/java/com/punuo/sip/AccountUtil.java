package com.punuo.sip;

import android.text.TextUtils;

import com.punuo.sip.dev.SipDevManager;
import com.punuo.sip.dev.request.SipDevLogoutRequest;
import com.punuo.sip.user.SipUserManager;
import com.punuo.sip.user.request.SipUserLogoutRequest;
import com.punuo.sys.sdk.account.AccountManager;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class AccountUtil {

    public static void logout() {
        SipUserManager.getInstance().addRequest(new SipUserLogoutRequest());
        if (!TextUtils.isEmpty(AccountManager.getDevId())) {
            SipDevManager.getInstance().addRequest(new SipDevLogoutRequest());
        }
    }
}
