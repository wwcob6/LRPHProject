package com.punuo.sys.app.home.album.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.bumptech.glide.Glide;
import com.punuo.sys.app.home.album.model.CloudPhotoCover;
import com.punuo.sys.sdk.PnApplication;
import com.punuo.sys.sdk.httplib.HttpConfig;
import com.punuo.sys.sdk.router.HomeRouter;

import java.util.List;

public class CloudAlbumAdapter extends RecyclerView.Adapter<CloudAlbumAdapter.ViewHolder> {
    private List<CloudPhotoCover> mCloudPhotoCoverList;


    public void appendData(List<CloudPhotoCover> CloudPhotoCover) {
        if (CloudPhotoCover != null) {
            mCloudPhotoCoverList.clear();
            mCloudPhotoCoverList.addAll(CloudPhotoCover);
            notifyDataSetChanged();
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_photoDate;
        private ImageView iv_photoCover;
        private RelativeLayout rl_cloudPhoto;

        public ViewHolder(View v) {
            super(v);
            tv_photoDate = (TextView) v.findViewById(R.id.tv_photoDate);
            iv_photoCover = (ImageView) v.findViewById(R.id.iv_photoCover);
            rl_cloudPhoto = (RelativeLayout) v.findViewById(R.id.rl_cloudPhoto);
        }
    }

    public CloudAlbumAdapter(List<CloudPhotoCover> mCloudPhotoCoverList) {
        this.mCloudPhotoCoverList = mCloudPhotoCoverList;
    }

    @Override
    public CloudAlbumAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cloud_album_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CloudAlbumAdapter.ViewHolder holder, int position) {
        CloudPhotoCover cloudPhotoCover = mCloudPhotoCoverList.get(position);
        holder.tv_photoDate.setText(cloudPhotoCover.month + "月");
        Glide.with(PnApplication.getInstance()).load("http://" + HttpConfig.getHost() +
                ":8000/static/ftp/" + mCloudPhotoCoverList.get(position).pic).into(holder.iv_photoCover);
        holder.rl_cloudPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(HomeRouter.ROUTER_ALBUM_ACTIVITY)
                        .withString("month", cloudPhotoCover.month)
                        .navigation();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCloudPhotoCoverList.size();
    }
}
