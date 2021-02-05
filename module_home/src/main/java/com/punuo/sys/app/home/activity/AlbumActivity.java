package com.punuo.sys.app.home.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.adapter.MyRecyclerViewAdapter;
import com.app.friendCircleMain.adapter.ImagePagerActivity;
import com.app.model.CloudPhoto;
import com.app.request.GetImagesRequest;
import com.app.video.VideoInfo;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Route(path = HomeRouter.ROUTER_ALBUM_ACTIVITY)
public class AlbumActivity extends BaseSwipeBackActivity {
    private RecyclerView mRecyclerView;
    private final List<String> images = new ArrayList<>();//图片地址
    private Context mContext;
    private MyRecyclerViewAdapter adapter;
    private final HashMap<Integer, float[]> xyMap = new HashMap<>();//所有子项的坐标

    private PopupMenu popup;
    private GetImagesRequest mGetImagesRequest;

    @Autowired(name = "month")
    String month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ARouter.getInstance().inject(this);
        mContext = this;
        initView();
        startGetImageThread();
        setEvent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }
    }

    /**
     * recyclerView item点击事件
     */
    private void setEvent() {
        adapter.setmOnItemClickListener((view, position) -> {
            Intent intent = new Intent(mContext, ImagePagerActivity.class);
            // 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) images);
            intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
            startActivity(intent);
        });
    }

    private void initView() {
        View back = findViewById(R.id.back);
        back.setOnClickListener(v -> {
            finish();
        });
        TextView title = (TextView) findViewById(R.id.title);
        title.setText("相册");
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);//定义3列的网格布局
        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(5, 3));//初始化子项距离和列数
        adapter = new MyRecyclerViewAdapter(images, mContext, glm);
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果返回值是正常的话
        if (resultCode == Activity.RESULT_OK) {
            // 验证请求码是否一至，也就是startActivityForResult的第二个参数
            switch (requestCode) {
                case 1:
                    startGetImageThread();
                    break;

                default:
                    break;
            }
        }
    }

    public void startGetImageThread() {
        if (mGetImagesRequest != null && !mGetImagesRequest.isFinished) {
            return;
        }
        mGetImagesRequest = new GetImagesRequest();
        mGetImagesRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetImagesRequest.addUrlParam("month", month);
        mGetImagesRequest.setRequestListener(new RequestListener<CloudPhoto>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(CloudPhoto result) {
                if (result == null) {
                    return;
                }
                if (result.imageList.isEmpty() || result.imageList == null) {

                } else {
                    for (int i = 0; i < result.imageList.size(); i++) {
                        images.add("http://sip.qinqingonline.com:8000/static/ftp/" + result.imageList.get(i));
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Exception e) {
                Log.e("error", "cuowu");
            }
        });
        HttpManager.addRequest(mGetImagesRequest);
    }

    public static class RecyclerViewItemDecoration extends RecyclerView.ItemDecoration {
        private final int itemSpace;//定义子项间距
        private final int itemColumnNum;//定义子项的列数

        RecyclerViewItemDecoration(int itemSpace, int itemColumnNum) {
            this.itemSpace = itemSpace;
            this.itemColumnNum = itemColumnNum;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.bottom = itemSpace;//底部留出间距
            if (parent.getChildAdapterPosition(view) % itemColumnNum == 0)//每行第一项左边不留间距，其他留出间距
            {
                outRect.left = 0;
            } else {
                outRect.left = itemSpace;
            }
        }
    }

    /**
     * 重写startActivity方法，禁用activity默认动画
     */
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏
            VideoInfo.mCamera.setDisplayOrientation(0);
        } else {
            //竖屏
            VideoInfo.mCamera.setDisplayOrientation(90);
        }
    }
}
