package com.app.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.ui.AddressAddActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import static com.app.sip.SipInfo.dbHelper;
import static com.app.sip.SipInfo.farmilymemberList;


/**
 * Created by 23578 on 2018/10/31.
 */

public class PhoneRecyclerViewAdapter extends RecyclerView.Adapter<PhoneRecyclerViewAdapter.MyViewHolder> {
    private List<String> images = new ArrayList<String>();//Image资源，内容为图片的网络地址
    private Context mContext;
    private DisplayImageOptions options;//UniversalImageLoad
    private GridLayoutManager glm;
    private PhoneRecyclerViewAdapter.OnItemClickListener mOnItemClickListener;
    private PhoneRecyclerViewAdapter.OnLongItemClickListener mOnLongItemClickListener;
    Button cancle;
    Button delete;
    Button edit;
    Dialog mCameraDialog;
    PopupMenu popup;

    public PhoneRecyclerViewAdapter(List<String> images, Context mContext, DisplayImageOptions options, GridLayoutManager glm) {
        this.images = images;
        this.mContext = mContext;
        this.options = options;
        this.glm = glm;
    }

    @Override
    public PhoneRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_layout, null);//加载item布局
        PhoneRecyclerViewAdapter.MyViewHolder myViewHolder = new PhoneRecyclerViewAdapter.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final PhoneRecyclerViewAdapter.MyViewHolder myViewHolder, final int i) {
        myViewHolder.mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//设置图片充满ImageView并自动裁剪居中显示
        ViewGroup.LayoutParams parm = myViewHolder.mImageView.getLayoutParams();
        parm.height = glm.getWidth() / glm.getSpanCount()
                - 2 * myViewHolder.mImageView.getPaddingLeft() - 2 * ((ViewGroup.MarginLayoutParams) parm).leftMargin;//设置imageView宽高相同

        myViewHolder.textView.setText(farmilymemberList.get(i).getName());
        if (farmilymemberList.get(i).getAvatorurl() == null) {
            myViewHolder.mImageView.setImageResource(R.drawable.defaultavator);
        } else {
            ImageLoader.getInstance().displayImage(farmilymemberList.get(i).getAvatorurl(), myViewHolder.mImageView);
        }
        myViewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d("onlongclick", "success");
                setDialog();
                cancle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCameraDialog.dismiss();
                    }
                });
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String avatorurl = farmilymemberList.get(i).getAvatorurl();
                        String name = farmilymemberList.get(i).getName();
                        String phonenumber = farmilymemberList.get(i).getPhonenumber();
                        Intent intent = new Intent(mContext, AddressAddActivity.class);
                        intent.putExtra("extra_avatorurl", avatorurl);
                        intent.putExtra("extra_name", name);
                        intent.putExtra("extra_phonenumber", phonenumber);
                        mContext.startActivity(intent);
                        mCameraDialog.dismiss();
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        Log.d("ton", "onClick: " + farmilymemberList.get(i).getName());
                        db.execSQL("delete from Person where name = ?", new String[]{farmilymemberList.get(i).getName()});
                        farmilymemberList.remove(i);
                        notifyDataSetChanged();
                        mCameraDialog.dismiss();
                    }
                });
            }
        });
        if (mOnItemClickListener != null)//传递监听事件
        {
            myViewHolder.mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(myViewHolder.mImageView, i);
                }

            });


        }

    }

    @Override
    public int getItemCount() {
        return farmilymemberList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView textView;
        private ImageView delete;

        public MyViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iv_item);
            textView = itemView.findViewById(R.id.iv_name);
            delete = itemView.findViewById(R.id.delete);
        }
    }

    public void setmOnItemClickListener(PhoneRecyclerViewAdapter.OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setmOnLongItemClickListener(PhoneRecyclerViewAdapter.OnLongItemClickListener mOnLongItemClickListener) {
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

    private void setDialog() {
        mCameraDialog = new Dialog(mContext, R.style.BottomDialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.editpop, null);
        //初始化视图
        cancle = (Button) root.findViewById(R.id.pop_cancle);
        edit = (Button) root.findViewById(R.id.pop_edit);
        delete = (Button) root.findViewById(R.id.pop_delete);
        root.findViewById(R.id.pop_delete);
        root.findViewById(R.id.pop_edit);
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = mContext.getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();

        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }

}
