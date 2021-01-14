package com.app.http;

import android.content.Context;
import android.graphics.Color;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.app.R;
import com.punuo.sys.sdk.util.BaseHandler;
import com.punuo.sys.sdk.util.RegexUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

import cn.smssdk.SMSSDK;


public class VerifyCodeManager implements BaseHandler.MessageHandler {

	public final static int REGISTER = 1;
	public final static int RESET_PWD = 2;
	public final static int BIND_PHONE = 3;

	private Context mContext;
	private int recLen = 60;

	private BaseHandler mHandler  = new BaseHandler(this);

	private TextView targetView;
	private Timer timer;
	private TimerTask mTimerTask;
	
	public VerifyCodeManager(Context context, TextView btn) {
		this.mContext = context;
		this.targetView = btn;
	}

	public void getVerifyCode(CharSequence phoneNum) {

		if (TextUtils.isEmpty(phoneNum)) {
			ToastUtils.showToast("请输入手机号");
			return;
		} else if (phoneNum.length() < 11) {
			ToastUtils.showToast( R.string.tip_phone_regex_not_right);
			return;
		} else if (!RegexUtils.checkMobile(phoneNum)) {
			ToastUtils.showToast(R.string.tip_phone_regex_not_right);
			return;
		}else {
			SMSSDK.getVerificationCode("86", phoneNum.toString());
		}

		mTimerTask = new TimerTask() {
			@Override
			public void run() {
				mHandler.post(() -> {
					setButtonStatusOff();
					if (recLen < 1) {
						setButtonStatusOn();
					}
				});
			}
		};

		timer = new Timer();
		timer.schedule(mTimerTask, 0, 1000);

	}

	private void setButtonStatusOff() {
		targetView.setText(String.format(
				mContext.getResources().getString(R.string.count_down), recLen-- + ""));
		targetView.setClickable(false);
		targetView.setTextColor(Color.parseColor("#f3f4f8"));
		targetView.setBackgroundColor(Color.parseColor("#ffffff"));

	}

	private void setButtonStatusOn() {
		timer.cancel();
		targetView.setText("重新发送");
		targetView.setTextColor(Color.parseColor("#b1b1b3"));
		targetView.setBackgroundColor(Color.parseColor("#ffffff"));
		recLen = 60;
		targetView.setClickable(true);
	}

	@Override
	public void handleMessage(Message msg) {

	}
}
