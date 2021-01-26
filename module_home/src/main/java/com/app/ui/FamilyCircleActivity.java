package com.app.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.R2;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.app.friendCircleMain.adapter.FriendCircleAdapter;
import com.app.friendCircleMain.domain.FriendMicroList;
import com.app.friendCircleMain.domain.FriendMicroListDatas;
import com.app.friendCircleMain.domain.FriendsMicro;
import com.app.friendCircleMain.event.FriendReLoadEvent;
import com.app.friendCircleMain.event.FriendRefreshEvent;
import com.app.model.Constant;
import com.app.publish.PublishedActivity;
import com.app.request.GetPostListFromGroupRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.recyclerview.CompletedFooter;
import com.punuo.sys.sdk.recyclerview.OnLoadMoreHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.app.model.Constant.devid1;

public class FamilyCircleActivity extends BaseSwipeBackActivity {
    private static final String TAG = "MicroActivity";

    @BindView(R2.id.back)
    ImageView ivBack7;
    @BindView(R2.id.add)
    ImageView add;
    @BindView(R2.id.pull_to_refresh)
    PullToRefreshRecyclerView mPullToRefreshRecyclerView;
    private int pageNum = 1;
    private FriendCircleAdapter mFriendCircleAdapter;
    private boolean hasMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_family_circle);
        ButterKnife.bind(this);

        init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    private GetPostListFromGroupRequest mGetPostListFromGroupRequest;
    private void getPostList(int page) {
        if (mGetPostListFromGroupRequest != null && !mGetPostListFromGroupRequest.isFinish()) {
            return;
        }
        boolean isFirstPage = (page == 1);
        mGetPostListFromGroupRequest = new GetPostListFromGroupRequest();
        mGetPostListFromGroupRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetPostListFromGroupRequest.addUrlParam("currentPage", page);
        mGetPostListFromGroupRequest.addUrlParam("groupid", Constant.groupid);
        mGetPostListFromGroupRequest.setRequestListener(new RequestListener<FriendsMicro>() {
            @Override
            public void onComplete() {
                if (isFirstPage) {
                    mPullToRefreshRecyclerView.onRefreshComplete();
                }
                mFriendCircleAdapter.onLoadMoreCompleted();
            }

            @Override
            public void onSuccess(FriendsMicro result) {
                if (result == null) {
                    return;
                }
                FriendMicroList friendMicroList = result.getPostList();
                if (friendMicroList == null) {
                    return;
                }
                List<FriendMicroListDatas> list = friendMicroList.data;
                if (isFirstPage) {
                    mFriendCircleAdapter.resetData(list);
                } else {
                    mFriendCircleAdapter.addAll(list);
                }
                hasMore = (friendMicroList.total - friendMicroList.per_page * friendMicroList.current_page) > 0;
                pageNum = friendMicroList.current_page + 1;
            }

            @Override
            public void onError(Exception e) {
                if (isFirstPage) {
                    mPullToRefreshRecyclerView.onRefreshComplete();
                } else {
                    mFriendCircleAdapter.onLoadMoreFailed();
                }
            }
        });
        HttpManager.addRequest(mGetPostListFromGroupRequest);
    }


    public void refresh() {
        pageNum = 1;
        getPostList(pageNum);
    }

    private void init() {
        mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                refresh();
            }
        });
        RecyclerView recyclerView = mPullToRefreshRecyclerView.getRefreshableView();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        mFriendCircleAdapter = new FriendCircleAdapter(this, new ArrayList<>());
        mFriendCircleAdapter.setOnLoadMoreHelper(new OnLoadMoreHelper() {
            @Override
            public boolean canLoadMore() {
                return hasMore;
            }

            @Override
            public void onLoadMore() {
                getPostList(pageNum);
            }
        });
        mFriendCircleAdapter.setCompletedFooterListener(new CompletedFooter.CompletedFooterListener() {
            @Override
            public boolean enableFooter() {
                return !hasMore;
            }

            @Override
            public View generateCompletedFooterView(Context context, ViewGroup parent) {
                return LayoutInflater.from(context).inflate(
                        R.layout.recycle_item_completed_foot, parent, false);
            }
        });
        recyclerView.setAdapter(mFriendCircleAdapter);
        if ((devid1 == null) || ("".equals(devid1))) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                    .setTitle("请先绑定设备")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            dialog.show();

        } else {
            refresh();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FriendRefreshEvent event) {
        mFriendCircleAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(FriendReLoadEvent event) {
        refresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R2.id.back, R2.id.add})
    public void onClock(View v) {
        int id = v.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.add) {
            startActivity(new Intent(this, PublishedActivity.class));
        }
    }

}
