package com.app.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.punuo.sys.app.home.model.PostNewModel;
import com.punuo.sys.sdk.httplib.HttpConfig;

import org.greenrobot.eventbus.EventBus;
import org.zoolu.sip.message.Message;

import java.util.List;

/**
 * Created by maojianhui on 2019/3/21.
 */

public class SystemNotifyAdapter extends RecyclerView.Adapter<SystemNotifyAdapter.ViewHolder> {
    private final List<PostNewModel> mNotifyList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlMessage;
        ImageView messagePicture;
        TextView messageTitle;
        TextView messageInfo;
        TextView time;

        public ViewHolder(View v) {
            super(v);
            rlMessage = (RelativeLayout) v.findViewById(R.id.rl_message_info111);
            messageTitle = (TextView) v.findViewById(R.id.title111);
            messagePicture = (ImageView) v.findViewById(R.id.image111);
            messageInfo = (TextView) v.findViewById(R.id.info111);
            time = (TextView) v.findViewById(R.id.time111);
        }
    }

    public SystemNotifyAdapter(List<PostNewModel> notifyList) {
        mNotifyList = notifyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifyitem, parent, false);
        ViewHolder holder = new ViewHolder(view);
        holder.rlMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new Message("去版本更新"));
            }
        });
        return holder;
    }


    @Override
    public void onBindViewHolder(SystemNotifyAdapter.ViewHolder holder, int position) {
        holder.messageTitle.setText(mNotifyList.get(position).title);
        holder.time.setText(mNotifyList.get(position).time);
        String postType = mNotifyList.get(position).postType;
        if (postType.equals("A")) {
            holder.messageInfo.setText("查看详情");
        } else if (postType.equals("B")) {
            holder.messageInfo.setText("点击跳转");
        }

        if (mNotifyList.get(position).newId == null) {
            holder.messagePicture.setImageResource(R.drawable.akb1);
        } else {
            ImageLoader.getInstance().displayImage("http://" + HttpConfig.getHost() + ":" + HttpConfig.getPort() + "/static/news/" + mNotifyList.get(position).image + ".jpg",
                    holder.messagePicture);
        }

    }

    @Override
    public int getItemCount() {
        return mNotifyList.size();
    }


}
