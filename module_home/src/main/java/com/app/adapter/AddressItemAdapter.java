package com.app.adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.model.AddressItem;
import com.punuo.sys.sdk.router.HomeRouter;
import com.punuo.sys.sdk.span.MarkerViewSpan;

import java.util.List;

/**
 * Created by maojianhui on 2019/3/21.
 */

public class AddressItemAdapter extends RecyclerView.Adapter<AddressItemAdapter.ViewHolder> {
    private List<AddressItem> mAddressList;
    private Context mContext;
    public AddressItemAdapter(Context context, List<AddressItem> addressList) {
        mContext = context;
        mAddressList = addressList;
    }

    public void appendData(List<AddressItem> address) {
        if (address != null) {
            mAddressList.clear();
            mAddressList.addAll(address);
            notifyDataSetChanged();
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.addressitem, parent, false);
        return new AddressItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressItemAdapter.ViewHolder holder, int position) {
        AddressItem addressItem = mAddressList.get(position);
        holder.userName.setText(addressItem.userName);
        holder.userPhoneNum.setText(changePhoneNum(addressItem.userPhoneNum));
        if (addressItem.isDefault()) {
            View defaultAddressView = LayoutInflater.from(mContext).inflate(R.layout.address_default_view, null);
            String prefix = "默认地址";
            String defaultAddress = prefix + addressItem.userAddress + addressItem.detailAddress;
            SpannableString sp = new SpannableString(defaultAddress);
            sp.setSpan(new MarkerViewSpan(defaultAddressView),
                    0, prefix.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            holder.userAddress.setText(sp);
        } else {
            holder.userAddress.setText(addressItem.userAddress + addressItem.detailAddress);
        }
        holder.addressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(HomeRouter.ROUTER_ADDRESS_DETAIL_ACTIVITY)
                        .withBoolean("isEdit", true)
                        .withParcelable("addressItem", addressItem)
                        .navigation();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAddressList.size();
    }

    public static String changePhoneNum(String mobile) {
        return mobile.substring(0, 3) + "****" + mobile.substring(7);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View addressView;
        RelativeLayout rlUserAddress;
        ImageView addressEdit;
        TextView userAddress;
        TextView userName;
        TextView userPhoneNum;

        public ViewHolder(View v) {
            super(v);
            addressView = v;
            rlUserAddress = v.findViewById(R.id.rl_userAddress);
            addressEdit = v.findViewById(R.id.iv_addressEdit);
            userAddress = v.findViewById(R.id.tv_userAddress);
            userName = v.findViewById(R.id.tv_userName);
            userPhoneNum = v.findViewById(R.id.tv_userPhone);

        }
    }
}
