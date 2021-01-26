package com.app.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.app.adapter.MyRecyclerViewAdapter;
import com.app.model.CloudPhoto;
import com.app.request.GetImagesRequest;
import com.app.sip.SipInfo;
import com.app.tools.AlbumBitmapCacheHelper;
import com.app.video.VideoInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.punuo.sys.sdk.activity.BaseActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AlbumActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private final List<String> images = new ArrayList<>();//图片地址
    private Context mContext;
    private DisplayImageOptions options;
    private MyRecyclerViewAdapter adapter;
    private final HashMap<Integer, float[]> xyMap = new HashMap<>();//所有子项的坐标
    private int screenWidth;//屏幕宽度
    private int screenHeight;//屏幕高度
    PopupMenu popup;
    private GetImagesRequest mGetImagesRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albumactivity);
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

    @Override
    protected void onResume() {
        super.onResume();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
    }

    /**
     * recyclerView item点击事件
     */
    private void setEvent() {
        adapter.setmOnLongItemClickListener((view, position) -> {
            Log.d("onlongclick", "success");
            popup = new PopupMenu(AlbumActivity.this, view);
            // 将R.menu.popup_menu菜单资源加载到popup菜单中
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
            // 为popup菜单的菜单项单击事件绑定事件监听器
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.deletepicture) {
                    AlbumBitmapCacheHelper.getInstance().clearCache();
                    File file = new File(images.get(position).substring(7));
                    Log.d("ssssb", position + "");
                    Log.d("ssssb", images.get(position).substring(7));
                    if (file.exists()) {
                        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{images.get(position).substring(7)});//删除系统缩略图
                        file.delete();
                        images.remove(position);
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.d("ssssb", "文件不存在");
                    }
                }
                return true;
            });
            popup.show();


        });
        adapter.setmOnItemClickListener((view, position) -> {
            Intent intent = new Intent(mContext, AlbumSecondActivity.class);
            intent.putStringArrayListExtra("urls", (ArrayList<String>) images);
            intent.putExtra("position", position);
            xyMap.clear();//每一次点击前子项坐标都不一样，所以清空子项坐标

            //子项前置判断，是否在屏幕内，不在的话获取屏幕边缘坐标
            View view0 = mRecyclerView.getChildAt(0);
            int position0 = mRecyclerView.getChildAdapterPosition(view0);
            if (position0 > 0) {
                for (int j = 0; j < position0; j++) {
                    float[] xyf = new float[]{(1 / 6.0f + (j % 3) * (1 / 3.0f)) * screenWidth, 0};//每行3张图，每张图的中心点横坐标自然是屏幕宽度的1/6,3/6,5/6
                    xyMap.put(j, xyf);
                }
            }
            //其余子项判断
            for (int i = position0; i < mRecyclerView.getAdapter().getItemCount(); i++) {
                View view1 = mRecyclerView.getChildAt(i - position0);
                if (mRecyclerView.getChildAdapterPosition(view1) == -1)//子项末尾不在屏幕部分同样赋值屏幕底部边缘
                {
                    float[] xyf = new float[]{(1 / 6.0f + (i % 3) * (1 / 3.0f)) * screenWidth, screenHeight};
                    xyMap.put(i, xyf);
                } else {
                    int[] xy = new int[2];
                    view1.getLocationOnScreen(xy);
                    float[] xyf = new float[]{xy[0] * 1.0f + view1.getWidth() / 2, xy[1] * 1.0f + view1.getHeight() / 2};
                    xyMap.put(i, xyf);
                }
            }
            intent.putExtra("xyMap", xyMap);
            startActivity(intent);
        });
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        GridLayoutManager glm = new GridLayoutManager(mContext, 3);//定义3列的网格布局
        mRecyclerView.setLayoutManager(glm);
        mRecyclerView.addItemDecoration(new RecyclerViewItemDecoration(5, 3));//初始化子项距离和列数
        options = new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.drawable.pictureloading)
                .showImageOnLoading(R.drawable.pictureloading)
                .showImageOnFail(R.drawable.pictureloading)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .displayer(new FadeInBitmapDisplayer(1))
                .build();
        adapter = new MyRecyclerViewAdapter(images, mContext, options, glm);
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
        mGetImagesRequest.addUrlParam("month", SipInfo.month);
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
