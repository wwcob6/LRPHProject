package com.punuo.sys.app.home.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.adapter.PhoneRecyclerViewAdapter;
import com.app.model.MessageEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.punuo.sys.app.home.db.ContractPerson;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.router.HomeRouter;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.transaction.QueryTransaction;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


public class FriendCallActivity extends BaseActivity {
    private String TAG = "FriendCallActivity";
    private RecyclerView rv;
    private Context mContext;
    private PhoneRecyclerViewAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_call);
        mContext = this;
        EventBus.getDefault().register(this);
        initView();
        getData();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if (event.getMessage().equals("addcompelete")) {
            getData();
        }
    }

    private void getData() {
        adapter.clear();
        SQLite.select()
                .from(ContractPerson.class)
                .async()
                .queryListResultCallback(new QueryTransaction.QueryResultListCallback<ContractPerson>() {
                    @Override
                    public void onListQueryResult(QueryTransaction transaction, @NonNull List<ContractPerson> tResult) {
                        adapter.addAllData(tResult);
                        adapter.notifyDataSetChanged();
                    }
                }).execute();
    }

    private void initView() {
        View back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.title);
        title.setText("通讯录");
        rv = findViewById(R.id.rv);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);//定义3列的网格布局
        rv.setLayoutManager(glm);
        rv.addItemDecoration(new FriendCallActivity.RecyclerViewItemDecoration(2, 3));//初始化子项距离和列数
        adapter = new PhoneRecyclerViewAdapter(mContext);
        rv.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add);
        fab.setOnClickListener(v -> ARouter.getInstance().build(HomeRouter.ROUTER_CONTRACT_MANAGER_ACTIVITY).navigation());
    }

    public static class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        private final int itemSpace;//定义子项间距
        private final int itemColumnNum;//定义子项的列数

        public RecyclerViewItemDecoration(int itemSpace, int itemColumnNum) {
            this.itemSpace = itemSpace;
            this.itemColumnNum = itemColumnNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = itemSpace;//底部留出间距
            if (parent.getChildLayoutPosition(view) % itemColumnNum == 0) {//每行第一项左边不留间距，其他留出间距
                outRect.left = 0;
            } else {
                outRect.left = itemSpace;
            }

        }
    }

    /**
     * 重写startActivity方法，禁用activity默认动画
     *
     * @param intent
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
