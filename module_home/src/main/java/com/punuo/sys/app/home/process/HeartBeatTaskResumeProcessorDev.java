package com.punuo.sys.app.home.process;

import com.punuo.sip.dev.DevHeartBeatHelper;
import com.punuo.sys.sdk.app.AbstractTaskResumeProcessor;
import com.punuo.sys.sdk.util.BaseHandler;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by han.chen.
 * Date on 2019-08-12.
 **/
public class HeartBeatTaskResumeProcessorDev extends AbstractTaskResumeProcessor {

    private final BaseHandler mBaseHandler;

    public HeartBeatTaskResumeProcessorDev(BaseHandler baseHandler) {
        mBaseHandler = baseHandler;
    }

    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    protected void restartTask() {
        mBaseHandler.sendEmptyMessage(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
    }

    @Override
    protected void stopTask() {
        mBaseHandler.removeMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
    }

    @Override
    protected boolean isTaskStopped() {
        return !mBaseHandler.hasMessages(DevHeartBeatHelper.MSG_HEART_BEAR_VALUE);
    }

    @Override
    protected long getInternalTime() {
        return DevHeartBeatHelper.DELAY;
    }

    @Override
    protected String getTaskName() {
        return "SIP_HEART_BEAT_TASK_DEV";
    }

    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }
}
