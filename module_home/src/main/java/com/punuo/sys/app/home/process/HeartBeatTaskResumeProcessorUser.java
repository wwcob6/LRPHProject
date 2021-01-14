package com.punuo.sys.app.home.process;

import com.punuo.sip.user.UserHeartBeatHelper;
import com.punuo.sys.sdk.app.AbstractTaskResumeProcessor;
import com.punuo.sys.sdk.util.BaseHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public class HeartBeatTaskResumeProcessorUser extends AbstractTaskResumeProcessor {

    private final BaseHandler mBaseHandler;

    public HeartBeatTaskResumeProcessorUser(BaseHandler baseHandler) {
        mBaseHandler = baseHandler;
    }

    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void restartTask() {
        mBaseHandler.sendEmptyMessage(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
    }

    @Override
    protected void stopTask() {
        mBaseHandler.removeMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
    }

    @Override
    protected boolean isTaskStopped() {
        return !mBaseHandler.hasMessages(UserHeartBeatHelper.MSG_HEART_BEAR_VALUE);
    }

    @Override
    protected long getInternalTime() {
        return UserHeartBeatHelper.DELAY;
    }

    @Override
    protected String getTaskName() {
        return "SIP_HEART_BEAT_TASK_USER";
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }
}
