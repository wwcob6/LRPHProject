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
import com.app.friendCircleMain.adapter.CommentAdapter;
import com.app.model.MessageEvent;
import com.punuo.sys.app.message.model.PostNewLikeModel;
import com.punuo.sys.app.message.request.GetNewLikeRequest;
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

@Route(path = HomeRouter.ROUTER_ADD_LIKE_ACTIVITY)
public class AddLikeActivity extends BaseSwipeBackActivity {

    @BindView(R2.id.rv_addlikes)
    RecyclerView rvAddlikes;
    @BindView(R2.id.tv_noAddlikes)
    TextView tvNoAddlikes;
    @BindView(R2.id.title)
    TextView title;
    @BindView(R2.id.back)
    View back;
    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addlike_view);
        ButterKnife.bind(this);
        title.setText("收到的赞");
        back.setOnClickListener(v -> {
            scrollToFinishActivity();
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddlikes.setLayoutManager(layoutManager);
        initView();
    }

    private void initView() {
        GetNewLikeRequest request = new GetNewLikeRequest();
        request.addUrlParam("id", UserInfoManager.getUserInfo().id);
        request.addUrlParam("currentTime", mDateFormat.format(new Date()));
        request.setRequestListener(new RequestListener<PostNewLikeModel>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PostNewLikeModel result) {
                if (result == null) {
                    return;
                }
                if (result.isSuccess()) {
                    tvNoAddlikes.setVisibility(View.INVISIBLE);
                    CommentAdapter adapter = new CommentAdapter(result.mCommentModels);
                    rvAddlikes.setAdapter(adapter);
                    EventBus.getDefault().post(new MessageEvent("取消新点赞提示"));
                } else {
                    tvNoAddlikes.setVisibility(View.VISIBLE);
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
