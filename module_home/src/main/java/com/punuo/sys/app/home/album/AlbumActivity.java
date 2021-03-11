package com.punuo.sys.app.home.album;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.adapter.MyRecyclerViewAdapter;
import com.app.request.GetImagesRequest;
import com.punuo.sys.app.home.album.model.CloudPhoto;
import com.punuo.sys.app.home.friendCircle.adapter.ImagePagerActivity;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;

import java.util.ArrayList;
import java.util.List;


@Route(path = HomeRouter.ROUTER_ALBUM_ACTIVITY)
public class AlbumActivity extends BaseSwipeBackActivity {
    private RecyclerView mRecyclerView;
    private final List<String> images = new ArrayList<>();//图片地址
    private MyRecyclerViewAdapter adapter;
    private GetImagesRequest mGetImagesRequest;

    @Autowired(name = "month")
    String month;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        ARouter.getInstance().inject(this);
        initView();
        startGetImageThread();
        setEvent();
    }

    /**
     * recyclerView item点击事件
     */
    private void setEvent() {
        adapter.setmOnItemClickListener((view, position) -> {
            ARouter.getInstance().build(HomeRouter.ROUTER_IMAGE_PAGER_ACTIVITY)
                    .withStringArrayList(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) images)
                    .withInt(ImagePagerActivity.EXTRA_IMAGE_INDEX, position)
                    .navigation();
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
        GridLayoutManager glm = new GridLayoutManager(this, 3);//定义3列的网格布局
        mRecyclerView.setLayoutManager(glm);
        adapter = new MyRecyclerViewAdapter(images, this, glm);
        mRecyclerView.setAdapter(adapter);
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
}
