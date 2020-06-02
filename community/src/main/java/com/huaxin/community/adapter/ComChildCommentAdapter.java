package com.huaxin.community.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drakeet.multitype.ItemViewBinder;
import com.huaxin.community.R;
import com.huaxin.library.entity.ChildComment;
import com.huaxin.library.entity.UserInfo;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.library.utils.TimeUtils;


public class ComChildCommentAdapter extends ItemViewBinder<ChildComment, ComChildCommentAdapter.ViewHolder> {
    private OnChildItemClickListener onItemClickListener;
    private OnChildItemLongClickListener onChildItemLongClickListener;
    public interface OnChildItemClickListener {

        void onItemClick(ChildComment c);

        void onHeadClick(ChildComment c);


    }
    public interface OnChildItemLongClickListener {

        void onItemLongClick(ChildComment c);

    }
    public void setonLongClick(OnChildItemLongClickListener onChildItemLongClickListener){
        this.onChildItemLongClickListener = onChildItemLongClickListener;
    }

    public void setOnItemClickListener(OnChildItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.com_item_child_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, ChildComment item) {
        UserInfo userInfo = item.getUserInfo();
        ImageUtils.displayImage(viewHolder.mContext, userInfo.getThumb(), R.mipmap.find_item_holder, viewHolder.iv_avatar);
        viewHolder.tv_nickname.setText(userInfo.getNickname());
        viewHolder.tv_comment_detail.setText(String.format("%s%s", item.getText(), TimeUtils.getTimeFormatText(item.getComment_date())));
        viewHolder.tv_nickname.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onHeadClick(item);
            }
        });
        viewHolder.tv_comment_detail.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(item);
            }
        });
        viewHolder.itemView.setOnLongClickListener(v -> {
            if (onChildItemLongClickListener != null) {
                onChildItemLongClickListener.onItemLongClick(item);
            }
            return true;
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Context mContext;
        ImageView iv_avatar;
        TextView tv_nickname;
        TextView tv_comment_detail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mContext = itemView.getContext();
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_nickname = itemView.findViewById(R.id.tv_nickname);
            tv_comment_detail = itemView.findViewById(R.id.tv_comment_detail);
        }
    }


}
