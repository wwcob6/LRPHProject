package com.app.ui;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.adapter.PhoneRecyclerViewAdapter;
import com.app.db.MyDatabaseHelper;
import com.app.model.FamilyMember;
import com.app.model.MessageEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.router.HomeRouter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class FriendCallActivity extends BaseActivity {
    private String TAG = "FriendCallActivity";
    private RecyclerView rv;
    private Context mContext;
    private PhoneRecyclerViewAdapter adapter;
    private MyDatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_call);
        mContext = this;
        EventBus.getDefault().register(this);
        dbHelper = new MyDatabaseHelper(this, "member.db", null, 2);
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
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("Person", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String phoneNumber = cursor.getString(cursor.getColumnIndex("phonenumber"));
                String avatarUrl = cursor.getString(cursor.getColumnIndex("avatorurl"));
                FamilyMember familymember = new FamilyMember(name, phoneNumber, avatarUrl);
                adapter.addData(familymember);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter.notifyDataSetChanged();
    }

    private void initView() {
        View back = findViewById(R.id.back);
        back.setOnClickListener(v -> finish());
        TextView title = findViewById(R.id.title);
        title.setText("通讯录");
        rv = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);//定义3列的网格布局
        rv.setLayoutManager(glm);
        rv.addItemDecoration(new FriendCallActivity.RecyclerViewItemDecoration(2, 3));//初始化子项距离和列数
        adapter = new PhoneRecyclerViewAdapter(mContext, dbHelper);
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
