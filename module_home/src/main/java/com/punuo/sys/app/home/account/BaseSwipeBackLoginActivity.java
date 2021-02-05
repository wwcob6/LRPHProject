package com.punuo.sys.app.home.account;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.app.R;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mob.MobSDK;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.util.RegexUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.wrapper.TokenVerifyResult;

/**
 * Created by han.chen.
 * Date on 2021/1/13.
 **/
public abstract class BaseSwipeBackLoginActivity extends BaseSwipeBackActivity {
    private int recLen = 60;
    public TextView targetView;
    private Timer mTimer;
    private TimerTask mTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobSDK.init(this, "213c5d90b2394", "793f08e685abc8a57563a8652face144");
        SMSSDK.registerEventHandler(eventHandler);
    }

    protected EventHandler eventHandler = new EventHandler() {
        @Override
        public void afterEvent(int event, int result, Object data) {
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            getBaseHandler().sendMessage(msg);
        }
    };

    public boolean checkPhoneNumber(CharSequence phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showToast(R.string.tip_phone_can_not_be_empty);
            return false;
        }
        if (!RegexUtils.checkMobile(phoneNumber)) {
            ToastUtils.showToast(R.string.tip_phone_regex_not_right);
            return false;
        }
        return true;
    }

    public boolean checkOldPassword(CharSequence oldPassword) {
        if (!TextUtils.equals(oldPassword.toString(),AccountManager.getPassword())) {
            ToastUtils.showToast(R.string.tip_password_not_same);
            return false;
        }
        return true;
    }

    public boolean checkCode(CharSequence code) {
        if (TextUtils.isEmpty(code)) { // 验证码不正确
            ToastUtils.showToast(R.string.tip_please_input_code);
            return false;
        }
        return true;
    }

    public boolean checkPassword(CharSequence pwd) {
        if (TextUtils.isEmpty(pwd) || pwd.length() < 6 || pwd.length() > 32) {
            ToastUtils.showToast(R.string.tip_please_input_6_32_password);
            return false;
        }
        return true;
    }

    public boolean checkPassWordValid(CharSequence pwd, CharSequence pwdAgain) {
        if (TextUtils.isEmpty(pwd) || pwd.length() < 6 || pwd.length() > 32 || TextUtils.isEmpty(pwdAgain) || pwdAgain.length() < 6 || pwdAgain.length() > 32) {
            ToastUtils.showToast(R.string.tip_please_input_6_32_password);
            return false;
        } else if (!TextUtils.equals(pwd.toString(), pwdAgain.toString())) {
            ToastUtils.showToast("两次密码不一致");
            return false;
        }
        return true;
    }

    public void getVerifyCode(CharSequence phoneNum) {
        if (!checkPhoneNumber(phoneNum)) {
            return;
        }
        SMSSDK.getVerificationCode("86", phoneNum.toString());

        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                getBaseHandler().post(() -> {
                    setButtonStatusOff();
                    if (recLen < 1) {
                        setButtonStatusOn();
                    }
                });
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    private void setButtonStatusOff() {
        if (targetView == null) {
            return;
        }
        targetView.setText(String.format(
                getResources().getString(R.string.count_down), recLen-- + ""));
        targetView.setClickable(false);
        targetView.setTextColor(Color.parseColor("#999999"));
        targetView.setBackgroundColor(Color.parseColor("#ffffff"));

    }

    private void setButtonStatusOn() {
        mTimer.cancel();
        if (targetView == null) {
            return;
        }
        targetView.setText("重新发送");
        targetView.setTextColor(Color.parseColor("#b1b1b3"));
        targetView.setBackgroundColor(Color.parseColor("#ffffff"));
        recLen = 60;
        targetView.setClickable(true);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        int event = msg.arg1;
        int result = msg.arg2;
        Object data = msg.obj;
        if (result == SMSSDK.RESULT_COMPLETE) {
            //成功回调
            if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交短信、语音验证码成功
                onVerifyCodeSuccess();
            } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                //获取短信验证码成功
                onGetCodeSuccess();
            } else if (event == SMSSDK.EVENT_GET_VOICE_VERIFICATION_CODE) {
                //获取语音验证码成功
            } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                //返回支持发送验证码的国家列表
            } else if (event == SMSSDK.EVENT_GET_VERIFY_TOKEN_CODE) {
                //本机验证获取token成功
                TokenVerifyResult tokenVerifyResult = (TokenVerifyResult) data;
                //SMSSDK.login(phoneNum,tokenVerifyResult);
            } else if (event == SMSSDK.EVENT_VERIFY_LOGIN) {
                //本机验证登陆成功
            }
        } else if (result == SMSSDK.RESULT_ERROR) {
            onError(data);
        } else {
            //其他失败回调
            ((Throwable) data).printStackTrace();
        }
    }

    public void onVerifyCodeSuccess() {

    }

    public void onGetCodeSuccess() {
        ToastUtils.showToast("验证码已经发送");
    }

    public void onError(Object data) {
        Throwable throwable = (Throwable) data;
        throwable.printStackTrace();
        JsonObject obj = new JsonParser().parse(throwable.getMessage()).getAsJsonObject();
        String des = obj.get("detail").getAsString();//错误描述
        int status = obj.get("status").getAsInt();//错误代码
        if (status > 0 && !TextUtils.isEmpty(des)) {
            ToastUtils.showToast(des);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
