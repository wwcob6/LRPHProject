package com.punuo.sys.app.home.friendCircle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.R2;
import com.app.request.GetPostListFromGroupRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;
import com.punuo.sys.app.home.friendCircle.adapter.FriendCircleAdapter;
import com.punuo.sys.app.home.friendCircle.domain.FriendMicroList;
import com.punuo.sys.app.home.friendCircle.domain.FriendMicroListData;
import com.punuo.sys.app.home.friendCircle.domain.FriendsMicro;
import com.punuo.sys.app.home.friendCircle.event.FriendReLoadEvent;
import com.punuo.sys.app.home.friendCircle.event.FriendRefreshEvent;
import com.punuo.sys.sdk.account.AccountManager;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.recyclerview.CompletedFooter;
import com.punuo.sys.sdk.recyclerview.OnLoadMoreHelper;
import com.punuo.sys.sdk.router.HomeRouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FamilyCircleActivity extends BaseSwipeBackActivity {
    private static final String TAG = "FamilyCircleActivity";

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
        mGetPostListFromGroupRequest.addUrlParam("groupid", AccountManager.getGroupId());
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
                FriendMicroList friendMicroList = result.postList;
                if (friendMicroList == null) {
                    return;
                }
                List<FriendMicroListData> list = friendMicroList.data;
                if (isFirstPage) {
                    mFriendCircleAdapter.resetData(list);
                } else {
                    mFriendCircleAdapter.addAll(list);
                }
                hasMore = (friendMicroList.total - friendMicroList.perPage * friendMicroList.currentPage) > 0;
                pageNum = friendMicroList.currentPage + 1;
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
        if (TextUtils.isEmpty(AccountManager.getBindDevId())) {
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
            ARouter.getInstance().build(HomeRouter.ROUTER_PUBLISH_ACTIVITY).navigation();
        }
    }

}
