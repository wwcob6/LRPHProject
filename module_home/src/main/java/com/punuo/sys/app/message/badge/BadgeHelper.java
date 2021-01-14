package com.punuo.sys.app.message.badge;

import com.app.UserInfoManager;
import com.punuo.sys.app.message.model.CountModel;
import com.punuo.sys.app.message.request.GetNewCommentCountRequest;
import com.punuo.sys.app.message.request.GetNewLikeCountRequest;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by han.chen.
 * Date on 2021/1/14.
 **/
public class BadgeHelper {
    public static final int MSG_BADGE_VALUE = 0x0003;
    public static final int DELAY = 60 * 1000;
    public static MessageBadgeCnt messageBadgeCnt = new MessageBadgeCnt();
    public static void refreshBadge() {
        getNewLikeCount();
        getNewCommentCount();
    }

    private static void getNewLikeCount() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        GetNewLikeCountRequest likeCountRequest = new GetNewLikeCountRequest();
        likeCountRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        likeCountRequest.addUrlParam("currentTime", mDateFormat.format(new Date()));
        likeCountRequest.setRequestListener(new RequestListener<CountModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(CountModel result) {
                if (result != null && result.isSuccess()) {
                    messageBadgeCnt.likeCount = result.count;
                } else {
                    messageBadgeCnt.likeCount = 0;
                }
                EventBus.getDefault().post(messageBadgeCnt);

            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(likeCountRequest);
    }

    private static void getNewCommentCount() {
        SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        GetNewCommentCountRequest commentCountRequest = new GetNewCommentCountRequest();
        commentCountRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        commentCountRequest.addUrlParam("currentTime", mDateFormat.format(new Date()));
        commentCountRequest.setRequestListener(new RequestListener<CountModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(CountModel result) {
                if (result != null && result.isSuccess()) {
                    messageBadgeCnt.commentCount = result.count;
                } else {
                    messageBadgeCnt.commentCount = 0;
                }
                EventBus.getDefault().post(messageBadgeCnt);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(commentCountRequest);
    }
}
