package com.punuo.sys.app.message;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.app.home.friendCircle.adapter.CommentAdapter;
import com.app.model.MessageEvent;
import com.punuo.sys.app.message.model.PostNewCommentModel;
import com.punuo.sys.app.message.request.GetNewCommentRequest;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
@Route(path = HomeRouter.ROUTER_COMMENT_ACTIVITY)
public class CommentActivity extends BaseSwipeBackActivity {
    @BindView(R2.id.rv_comments)
    RecyclerView rvComments;
    @BindView(R2.id.tv_noNewMessage)
    TextView tvNoNewMessage;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.back)
    View back;
    private final SimpleDateFormat mDateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_view);
        ButterKnife.bind(this);
        title.setText("评论/回复");
        back.setOnClickListener(v -> {
            scrollToFinishActivity();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvComments.setLayoutManager(layoutManager);
        initComments();
    }

    private void initComments() {
        GetNewCommentRequest request = new GetNewCommentRequest();
        request.addUrlParam("id", UserInfoManager.getUserInfo().id);
        request.addUrlParam("currentTime", mDateFormat.format(new Date()));
        request.setRequestListener(new RequestListener<PostNewCommentModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PostNewCommentModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    tvNoNewMessage.setVisibility(View.INVISIBLE);
                    CommentAdapter adapter = new CommentAdapter(result.mCommentModels);
                    rvComments.setAdapter(adapter);
                    EventBus.getDefault().post(new MessageEvent("取消新评论提示"));
                } else {
                    tvNoNewMessage.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Exception e) {
                HandlerExceptionUtils.handleException(e);
            }
        });
        HttpManager.addRequest(request);
    }
}
