package com.punuo.sys.app.home.album;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.R2;
import com.punuo.sys.app.home.utils.FileUtils;
import com.app.model.PhotoCoverResult;
import com.app.request.GetPhotoCoverRequest;
import com.app.request.UploadPhotoRequest;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.punuo.sys.app.home.album.adapter.CloudAlbumAdapter;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.HandlerExceptionUtils;
import com.punuo.sys.sdk.util.ToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 个人中心->相册
 */
@Route(path = HomeRouter.ROUTER_CLOUD_ALBUM_ACTIVITY)
public class CloudAlbumActivity extends BaseSwipeBackActivity {
    @BindView(R2.id.rv_cloudPhoto)
    RecyclerView rvCloudPhoto;
    @BindView(R2.id.back1)
    ImageView back1;
    @BindView(R2.id.title1)
    TextView title1;
    @BindView(R2.id.add1)
    ImageView add1;
    private GetPhotoCoverRequest mGetPhotoCoverRequest;
    private CloudAlbumAdapter mCloudAlbumAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_album);
        ButterKnife.bind(this);
        getPhotoList();
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvCloudPhoto.setLayoutManager(manager);
        mCloudAlbumAdapter = new CloudAlbumAdapter(new ArrayList<>());
        rvCloudPhoto.setAdapter(mCloudAlbumAdapter);
        back1.setOnClickListener(v -> {
            finish();
        });
        add1.setOnClickListener(v -> {
            PictureSelector.create(CloudAlbumActivity.this)
                    .openGallery(PictureMimeType.ofImage())
                    .imageSpanCount(4)
                    .selectionMode(PictureConfig.MULTIPLE)
                    .maxSelectNum(9)
                    .minSelectNum(1)
                    .imageFormat(PictureMimeType.JPEG)
                    .forResult(PictureConfig.CHOOSE_REQUEST);
        });
    }

    private void getPhotoList() {
        if (mGetPhotoCoverRequest != null && !mGetPhotoCoverRequest.isFinish()) {
            return;
        }
        mGetPhotoCoverRequest = new GetPhotoCoverRequest();
        mGetPhotoCoverRequest.addUrlParam("id", UserInfoManager.getUserInfo().id);
        mGetPhotoCoverRequest.setRequestListener(new RequestListener<PhotoCoverResult>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(PhotoCoverResult result) {
                if (result == null) {
                    return;
                }
                if (result.mCloudPhotoCover != null && !result.mCloudPhotoCover.isEmpty()) {
                    mCloudAlbumAdapter.appendData(result.mCloudPhotoCover);
                }

            }

            @Override
            public void onError(Exception e) {
                HandlerExceptionUtils.handleException(e);
            }
        });
        HttpManager.addRequest(mGetPhotoCoverRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    uploadPhoto(selectList);
                }
                break;
            default:
                break;
        }
    }


    private UploadPhotoRequest mUploadPhotoRequest;

    private void uploadPhoto(List<LocalMedia> list) {
        if (mUploadPhotoRequest != null && !mUploadPhotoRequest.isFinish()) {
            return;
        }
        showLoadingDialog("正在上传...");
        mUploadPhotoRequest = new UploadPhotoRequest();
        mUploadPhotoRequest.addEntityParam("id", UserInfoManager.getUserInfo().id);
        List<File> files = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                String filePath = list.get(i).getPath();
                File file = new File(filePath);
                if (file.exists()) {
                    files.add(file);
                }
            }
        }
        if (!files.isEmpty()) {
            mUploadPhotoRequest.addEntityParam("photo[]", files);
        }
        mUploadPhotoRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {
                dismissLoadingDialog();
                FileUtils.deleteCircleDir();
            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result.isSuccess()) {
                    ToastUtils.showToast("照片上传成功");
                    getPhotoList();
                } else {
                    ToastUtils.showToast("照片上传失败请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUploadPhotoRequest);
    }
}
