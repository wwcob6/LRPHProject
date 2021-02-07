package com.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/28 0028.
 */

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder> {

    private List<String> images = new ArrayList<String>();//Image资源，内容为图片的网络地址
    private Context mContext;
    private GridLayoutManager glm;
    private OnItemClickListener mOnItemClickListener;
    private OnLongItemClickListener mOnLongItemClickListener;

    public MyRecyclerViewAdapter(List<String> images, Context mContext, GridLayoutManager glm) {
        this.images = images;
        this.mContext = mContext;
        this.glm = glm;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_pictureitem, null);//加载item布局
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int i) {
        ViewGroup.LayoutParams parm = myViewHolder.imageView.getLayoutParams();
        parm.height = glm.getWidth() / glm.getSpanCount()
                - 2 * myViewHolder.imageView.getPaddingLeft() - 2 * ((ViewGroup.MarginLayoutParams) parm).leftMargin;
        Glide.with(mContext).load(images.get(i))
                .apply(new RequestOptions()
                        .placeholder(R.drawable.pictureloading))
                .into(myViewHolder.imageView);

        myViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(myViewHolder.imageView, i);
                }
            }

        });
        myViewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnLongItemClickListener != null) {
                    mOnLongItemClickListener.onLongClick(myViewHolder.imageView, i);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item);
        }
    }

    /**
     * 对外暴露子项点击事件监听器
     *
     * @param mOnItemClickListener
     */
    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmOnLongItemClickListener(OnLongItemClickListener mOnLongItemClickListener) {
        this.mOnLongItemClickListener = mOnLongItemClickListener;
    }

    /**
     * 子项点击接口
     */
    public interface OnItemClickListener {
        void onClick(View view, int position);

    }

    public interface OnLongItemClickListener {
        void onLongClick(View view, int position);
    }
}
