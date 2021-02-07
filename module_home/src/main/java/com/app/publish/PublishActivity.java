package com.app.publish;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.app.R;
import com.app.publish.adapter.PublishImageAdapter;
import com.app.request.UploadPostRequest;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.punuo.sys.app.home.friendCircle.adapter.ImagePagerActivity;
import com.punuo.sys.app.home.friendCircle.event.FriendReLoadEvent;
import com.punuo.sys.app.home.utils.FileUtils;
import com.punuo.sys.sdk.account.UserInfoManager;
import com.punuo.sys.sdk.activity.BaseSwipeBackActivity;
import com.punuo.sys.sdk.httplib.HttpManager;
import com.punuo.sys.sdk.httplib.RequestListener;
import com.punuo.sys.sdk.model.PNBaseModel;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.util.CommonUtil;
import com.punuo.sys.sdk.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Route(path = HomeRouter.ROUTER_PUBLISH_ACTIVITY)
public class PublishActivity extends BaseSwipeBackActivity {
    private RecyclerView mRecyclerView;
    private PublishImageAdapter mPublishImageAdapter;
    private EditText mEditText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_layout);
        init();
    }

    public void init() {
        mEditText = (EditText) findViewById(R.id.edit_input);

        mRecyclerView = (RecyclerView) findViewById(R.id.grid_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        mRecyclerView.setLayoutManager(layoutManager);
        mPublishImageAdapter = new PublishImageAdapter(this, new ICallBack() {
            @Override
            public void itemClick(String path, int position) {
                if (TextUtils.equals("add", path)) {
                    CommonUtil.hideKeyboard(PublishActivity.this);
                    PictureSelector.create(PublishActivity.this)
                            .openGallery(PictureMimeType.ofImage())
                            .imageSpanCount(4)
                            .selectionMode(PictureConfig.MULTIPLE)
                            .maxSelectNum(9 - mPublishImageAdapter.size())
                            .minSelectNum(1)
                            .imageFormat(PictureMimeType.JPEG)
                            .forResult(PictureConfig.CHOOSE_REQUEST);
                } else {
                    Intent intent = new Intent(PublishActivity.this, ImagePagerActivity.class);
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, (ArrayList<String>) mPublishImageAdapter.getImages());
                    intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
                    startActivity(intent);
                }
            }
        });
        mRecyclerView.setAdapter(mPublishImageAdapter);
        mPublishImageAdapter.resetData(new ArrayList<>());
        TextView publish = (TextView) findViewById(R.id.activity_selectimg_send);
        publish.setText("发表");
        publish.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String dongTai = mEditText.getText().toString();
                uploadPost(dongTai, compressBitmap(mPublishImageAdapter.getData()));
            }
        });
    }
    private List<String> compressBitmap(List<String> selectBitmap) {
        showLoadingDialog("正在压缩图片...");
        List<String> tempList = new ArrayList<>();
        for (int i = 0; i < selectBitmap.size(); i++) {
            Bitmap bitmap = FileUtils.compressBitmap(selectBitmap.get(i));
            String temp = FileUtils.saveBitmap(bitmap, String.valueOf(System.currentTimeMillis()));
            tempList.add(temp);
        }
        return tempList;
    }

    private UploadPostRequest mUploadPostRequest;

    private void uploadPost(String content, List<String> list) {
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showToast("发送的内容不能为空");
            return;
        }
        if (mUploadPostRequest != null && !mUploadPostRequest.isFinish()) {
            return;
        }
        showLoadingDialog("正在上传...");
        mUploadPostRequest = new UploadPostRequest();
        mUploadPostRequest.addEntityParam("id", UserInfoManager.getUserInfo().id);
        mUploadPostRequest.addEntityParam("content", content);
        List<File> files = new ArrayList<>();
        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                String filePath = list.get(i);
                File file = new File(filePath);
                if (file.exists()) {
                    files.add(file);
                }
            }
        }
        if (!files.isEmpty()) {
            mUploadPostRequest.addEntityParam("file[]", files);
        }
        mUploadPostRequest.setRequestListener(new RequestListener<PNBaseModel>() {
            @Override
            public void onComplete() {
                dismissLoadingDialog();
                FileUtils.deleteCircleDir();
            }

            @Override
            public void onSuccess(PNBaseModel result) {
                if (result.isSuccess()) {
                    ToastUtils.showToast("状态上传成功");
                    EventBus.getDefault().post(new FriendReLoadEvent());
                    finish();
                } else {
                    ToastUtils.showToast("状态上传失败请重试");
                }
            }

            @Override
            public void onError(Exception e) {

            }
        });
        HttpManager.addRequest(mUploadPostRequest);
    }

    public String getString(String s) {
        String path = null;
        if (s == null)
            return "";
        for (int i = s.length() - 1; i > 0; i++) {
            s.charAt(i);
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == RESULT_OK) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    for (LocalMedia localMedia : selectList) {
                        String path = localMedia.getPath();
                        mPublishImageAdapter.addData(path);
                    }
                    mPublishImageAdapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
    }
}
