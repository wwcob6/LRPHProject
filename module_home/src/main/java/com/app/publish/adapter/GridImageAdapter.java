package com.app.publish.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.publish.ICallBack;
import com.app.publish.holder.ImageHolder;
import com.punuo.sys.sdk.recyclerview.BaseRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by han.chen.
 * Date on 2019-06-21.
 **/
public class GridImageAdapter extends BaseRecyclerViewAdapter<String> {
    private ICallBack mCallBack;
    private List<String> images = new ArrayList<>();
    public GridImageAdapter(Context context, ICallBack callBack) {
        super(context, new ArrayList<>());
        mCallBack = callBack;
        mData.add("add");
    }

    public void resetData(List<String> list) {
        images.clear();
        images.addAll(list);
        mData.clear();
        mData.addAll(list);
        if (list.size() < 9) {
            mData.add("add");
        }
        notifyDataSetChanged();
    }

    public void addData(String image) {
        int imageSize = images.size();
        if (imageSize < 8) {
            images.add(0, image);
            mData.add(0, image);
        } else if (imageSize == 8) {
            images.add(0, image);
            mData.add(0, image);
            mData.remove(9);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateBasicItemViewHolder(ViewGroup parent, int viewType) {
        return new ImageHolder(mContext, parent, mCallBack);
    }

    @Override
    public void onBindBasicItemView(RecyclerView.ViewHolder baseViewHolder, int position) {
        if (baseViewHolder instanceof ImageHolder) {
            ((ImageHolder) baseViewHolder).bind(getItem(position), position);
        }
    }

    @Override
    public int getBasicItemType(int position) {
        return 0;
    }

    @Override
    public int getBasicItemCount() {
        return mData == null ? 0 : mData.size();
    }

    public int size() {
        return images.size();
    }
}
