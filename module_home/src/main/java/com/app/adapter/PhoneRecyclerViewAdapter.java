package com.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.punuo.sys.app.home.db.ContractPerson;
import com.punuo.sys.sdk.router.HomeRouter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 23578 on 2018/10/31.
 */

public class PhoneRecyclerViewAdapter extends RecyclerView.Adapter<PhoneRecyclerViewAdapter.MyViewHolder> {
    private final Context mContext;
    private final List<ContractPerson> mContractPersonList = new ArrayList<>();
    public PhoneRecyclerViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void addData(ContractPerson contractPerson) {
        mContractPersonList.add(contractPerson);
    }

    public void addAllData(List<ContractPerson> list) {
        if (list != null) {
            mContractPersonList.addAll(list);
        }
    }

    public void clear() {
        mContractPersonList.clear();
    }

    @Override
    public PhoneRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhoneRecyclerViewAdapter.MyViewHolder myViewHolder, final int i) {
        ContractPerson contractPerson = mContractPersonList.get(i);
        myViewHolder.textView.setText(contractPerson.name);
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.default_avatar);
        Glide.with(mContext).load(contractPerson.avatarUrl).apply(requestOptions).into(myViewHolder.mImageView);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(HomeRouter.ROUTER_CONTRACT_MANAGER_ACTIVITY)
                        .withParcelable("contract_person", contractPerson)
                        .navigation();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mContractPersonList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mImageView;
        private final TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_item);
            textView = itemView.findViewById(R.id.iv_name);
        }
    }

}
