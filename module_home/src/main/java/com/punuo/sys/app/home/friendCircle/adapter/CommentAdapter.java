package com.punuo.sys.app.home.friendCircle.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.R;
import com.app.Util;
import com.bumptech.glide.Glide;
import com.punuo.sys.app.home.friendCircle.PraiseConst;
import com.punuo.sys.app.message.model.CommentModel;
import com.punuo.sys.sdk.PnApplication;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private final List<CommentModel> mCommentModelList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView replyName;
        TextView replyContent;
        TextView replyTime;
        TextView replyAddLikes;
        ImageView replyAvatar;
        ImageView commentPicture;

        public ViewHolder(View view) {
            super(view);
            replyName = view.findViewById(R.id.reply_name);
            replyContent = view.findViewById(R.id.reply_content);
            replyTime = view.findViewById(R.id.reply_time);
            replyAddLikes = view.findViewById(R.id.reply_add_likes);
            replyAvatar = view.findViewById(R.id.reply_avatar);
            commentPicture = view.findViewById(R.id.comment_picture);
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
        if ((commentModel.praiseType != null) && !("".equals(commentModel.praiseType))) {
            holder.replyContent.setVisibility(View.INVISIBLE);
            holder.replyAddLikes.setVisibility(View.VISIBLE);
            holder.replyAddLikes.setText(getPraiseText(commentModel.praiseType));
        } else {
            holder.replyContent.setVisibility(View.VISIBLE);
            holder.replyAddLikes.setVisibility(View.INVISIBLE);
            holder.replyContent.setText(commentModel.comment);
        }
        holder.replyTime.setText(commentModel.createTime);
        if (commentModel.avatar == null) {
            holder.replyAvatar.setImageResource(R.drawable.defaultavator);
        } else {
            Glide.with(PnApplication.getInstance()).load(Util.getImageUrl(commentModel.id, commentModel.avatar)).into(holder.replyAvatar);
        }
        if (commentModel.pic == null) {
            holder.commentPicture.setImageResource(R.drawable.defaultavator);
        } else {
            Glide.with(PnApplication.getInstance()).load(Util.getImageUrl(commentModel.id, commentModel.pic)).into(holder.commentPicture);
        }
    }

    @Override
    public int getItemCount() {
        return mCommentModelList.size();
    }

    public String getPraiseText(String pariseType) {
        String text = "";
        switch (pariseType) {
            case PraiseConst.TYPE_DIANZAN:
                text = PraiseConst.DESC_LIST[0];
                break;
            case PraiseConst.TYPE_WEIXIAO:
                text = PraiseConst.DESC_LIST[1];
                break;
            case PraiseConst.TYPE_DAXIAO:
                text = PraiseConst.DESC_LIST[2];
                break;
            case PraiseConst.TYPE_KUXIAO:
                text = PraiseConst.DESC_LIST[3];
                break;
            default:
                break;
        }
        return text;
    }
}