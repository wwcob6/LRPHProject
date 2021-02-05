package com.app.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by maojianhui on 2019/3/20.
 */

public class AddressItem implements Parcelable {
    @SerializedName("address_id")
    public String addressId;
    @SerializedName("userAddress")
    public String userAddress;
    @SerializedName("detailAddress")
    public String detailAddress;
    @SerializedName("userName")
    public String userName;
    @SerializedName("userPhoneNum")
    public String userPhoneNum;
    @SerializedName("position")
    public int position;
    @SerializedName("isDefault")
    private int isDefault;

    protected AddressItem(Parcel in) {
        addressId = in.readString();
        userAddress = in.readString();
        detailAddress = in.readString();
        userName = in.readString();
        userPhoneNum = in.readString();
        position = in.readInt();
        isDefault = in.readInt();
    }

    public static final Creator<AddressItem> CREATOR = new Creator<AddressItem>() {
        @Override
        public AddressItem createFromParcel(Parcel in) {
            return new AddressItem(in);
        }

        @Override
        public AddressItem[] newArray(int size) {
            return new AddressItem[size];
        }
    };

    public boolean isDefault() {
        return isDefault == 1;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addressId);
        dest.writeString(userAddress);
        dest.writeString(detailAddress);
        dest.writeString(userName);
        dest.writeString(userPhoneNum);
        dest.writeInt(position);
        dest.writeInt(isDefault);
    }
}