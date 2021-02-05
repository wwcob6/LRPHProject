package com.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.app.R;
import com.app.db.MyDatabaseHelper;
import com.app.model.FamilyMember;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.punuo.sys.app.home.activity.ContractManagerActivity;
import com.punuo.sys.sdk.router.HomeRouter;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 23578 on 2018/10/31.
 */

public class PhoneRecyclerViewAdapter extends RecyclerView.Adapter<PhoneRecyclerViewAdapter.MyViewHolder> {
    private final Context mContext;
    private final List<FamilyMember> mFamilyMemberList = new ArrayList<>();
    private final MyDatabaseHelper dbHelper;
    public PhoneRecyclerViewAdapter(Context mContext, MyDatabaseHelper dbHelper) {
        this.mContext = mContext;
        this.dbHelper = dbHelper;
    }

    public void addData(FamilyMember familyMember) {
        mFamilyMemberList.add(familyMember);
    }

    public void clear() {
        mFamilyMemberList.clear();
    }


    @Override
    public PhoneRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_layout, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PhoneRecyclerViewAdapter.MyViewHolder myViewHolder, final int i) {
        FamilyMember familyMember = mFamilyMemberList.get(i);
        myViewHolder.textView.setText(familyMember.getName());
        RequestOptions requestOptions = new RequestOptions().placeholder(R.drawable.default_avatar);
        Glide.with(mContext).load(familyMember.getAvatorurl()).apply(requestOptions).into(myViewHolder.mImageView);
        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build(HomeRouter.ROUTER_CONTRACT_MANAGER_ACTIVITY)
                        .withString("extra_avatorurl", familyMember.getAvatorurl())
                        .withString("extra_name", familyMember.getName())
                        .withString("extra_phonenumber", familyMember.getPhonenumber())
                        .navigation();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFamilyMemberList.size();
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

    private void setDialog(int position) {
        BottomSheetDialog dialog = new BottomSheetDialog(mContext, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.editpop, null);
        Button cancel = (Button) root.findViewById(R.id.pop_cancel);
        Button edit = (Button) root.findViewById(R.id.pop_edit);
        Button delete = (Button) root.findViewById(R.id.pop_delete);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String avatorurl = mFamilyMemberList.get(position).getAvatorurl();
                String name = mFamilyMemberList.get(position).getName();
                String phonenumber = mFamilyMemberList.get(position).getPhonenumber();
                Intent intent = new Intent(mContext, ContractManagerActivity.class);
                intent.putExtra("extra_avatorurl", avatorurl);
                intent.putExtra("extra_name", name);
                intent.putExtra("extra_phonenumber", phonenumber);
                mContext.startActivity(intent);
                dialog.dismiss();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("delete from Person where name = ?", new String[]{mFamilyMemberList.get(position).getName()});
                mFamilyMemberList.remove(position);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.setContentView(root);
        dialog.show();
    }

}
