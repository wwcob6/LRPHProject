package com.punuo.sys.app.home.db;

import android.os.Parcel;
import android.os.Parcelable;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.UUID;

/**
 * Created by han.chen.
 * Date on 2021/2/7.
 **/
@Table(database = ContractMember.class)
public class ContractPerson extends BaseModel implements Parcelable {

    @PrimaryKey
    public UUID id;

    @Column
    public String avatarUrl;

    @Column
    public String name;

    @Column
    public String phoneNumber;

    public ContractPerson() {

    }

    protected ContractPerson(Parcel in) {
        id = (UUID) in.readSerializable();
        avatarUrl = in.readString();
        name = in.readString();
        phoneNumber = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(id);
        dest.writeString(avatarUrl);
        dest.writeString(name);
        dest.writeString(phoneNumber);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ContractPerson> CREATOR = new Creator<ContractPerson>() {
        @Override
        public ContractPerson createFromParcel(Parcel in) {
            return new ContractPerson(in);
        }

        @Override
        public ContractPerson[] newArray(int size) {
            return new ContractPerson[size];
        }
    };
}
