package com.app.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.app.UserInfoManager;
import com.app.adapter.CloudAlbumAdapter;
import com.app.model.CloudPhotoCover;
import com.app.model.PhotoCoverResult;
import com.app.publish.PublishedActivity1;
import com.app.request.GetPhotoCoverRequest;
import com.punuo.sys.sdk.activity.ActivityCollector;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.router.HomeRouter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 个人中心->相册
 */
@Route(path = HomeRouter.ROUTER_CLOUD_ALBUM_ACTIVITY)
public class CloudAlbumActivity extends Activity {
    @BindView(R2.id.rv_cloudPhoto)
    RecyclerView rvCloudPhoto;
    @BindView(R2.id.back1)
    ImageView back1;
    @BindView(R2.id.title1)
    TextView title1;
    @BindView(R2.id.add1)
    ImageView add1;
    private List<CloudPhotoCover> mCloudPhotoList = new ArrayList<>();
    private GetPhotoCoverRequest mGetPhotoCoverRequest;
    private CloudAlbumAdapter mCloudAlbumAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_album);
        ActivityCollector.addActivity(this);
        ButterKnife.bind(this);
        title1.setText("相册");
        RelativeLayout.LayoutParams layoutParams=(RelativeLayout.LayoutParams)title1.getLayoutParams();
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

        putData();

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvCloudPhoto.setLayoutManager(manager);
        mCloudAlbumAdapter=new CloudAlbumAdapter(new ArrayList<>());
        rvCloudPhoto.setAdapter(mCloudAlbumAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.newbackground));
        }

        back1.setOnClickListener(v->{
            ActivityCollector.removeActivity(this);
            finish();
        });
        add1.setOnClickListener(v->{
            startActivity(new Intent(this, PublishedActivity1.class));
        });
    }

    private void putData() {
        getPhotoList();
    }

    private void getPhotoList() {
        if(mGetPhotoCoverRequest!=null&&!mGetPhotoCoverRequest.isFinish()){
            return;
        }
        mGetPhotoCoverRequest=new GetPhotoCoverRequest();
        mGetPhotoCoverRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetPhotoCoverRequest.setRequestListener(new RequestListener<PhotoCoverResult>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PhotoCoverResult result) {
                if(result==null){
                    return;
                }
                if(result.mCloudPhotoCover==null||result.mCloudPhotoCover.isEmpty()){

                }else{
                    mCloudAlbumAdapter.appendData(result.mCloudPhotoCover);
                    mCloudPhotoList=result.mCloudPhotoCover;
                }

            }

            @Override
            public void onError(Exception e) {
                Log.e("Clound","获取失败");
            }
        });
        HttpManager.addRequest(mGetPhotoCoverRequest);
    }
}
