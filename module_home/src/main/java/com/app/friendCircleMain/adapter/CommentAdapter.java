package com.app.friendCircleMain.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.punuo.sys.app.message.model.CommentModel;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private List<CommentModel> mCommentModelList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView replyName;
        TextView replyContent;
        TextView replyTime;
        TextView replyAddlikes;
        ImageView replyAvatar;
        ImageView commenttedPicture;

        public ViewHolder(View view) {
            super(view);
            replyName = view.findViewById(R.id.reply_name);
            replyContent = view.findViewById(R.id.reply_content);
            replyTime = view.findViewById(R.id.reply_time);
            replyAddlikes = view.findViewById(R.id.reply_addlikes);
            replyAvatar = view.findViewById(R.id.reply_avatar);
            commenttedPicture = view.findViewById(R.id.commentted_picture);
        }
    }

    public CommentAdapter(List<CommentModel> commentModelList) {
        mCommentModelList = commentModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommentModel commentModel = mCommentModelList.get(position);
        holder.replyName.setText(commentModel.replyName);
        if ((commentModel.praisetype != null) && !("".equals(commentModel.praisetype))) {
            holder.replyContent.setVisibility(View.INVISIBLE);
            holder.replyAddlikes.setVisibility(View.VISIBLE);
            holder.replyAddlikes.setText(commentModel.praisetype);
        } else {
            holder.replyContent.setVisibility(View.VISIBLE);
            holder.replyAddlikes.setVisibility(View.INVISIBLE);
            holder.replyContent.setText(commentModel.comment);
        }
        holder.replyTime.setText(commentModel.createTime);
        if (commentModel.avatar == null) {
            holder.replyAvatar.setImageResource(R.drawable.defaultavator);
        } else {
            ImageLoader.getInstance().displayImage(Util.getImageUrl(commentModel.id, commentModel.avatar), holder.replyAvatar);
        }
        if (commentModel.pic == null) {
            holder.commenttedPicture.setImageResource(R.drawable.defaultavator);
        } else {
            ImageLoader.getInstance().displayImage(Util.getImageUrl(commentModel.pic), holder.commenttedPicture);
        }
    }

    @Override
    public int getItemCount() {
        return mCommentModelList.size();
    }
}