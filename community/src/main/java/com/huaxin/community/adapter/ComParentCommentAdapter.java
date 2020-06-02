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
import com.huaxin.library.entity.ComParentComment;
import com.huaxin.library.entity.UserInfo;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.library.utils.TimeUtils;


public class ComParentCommentAdapter extends ItemViewBinder<ComParentComment, ComParentCommentAdapter.ViewHolder> {

    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {

        void onItemClick(ComParentComment c);
        void onHeadClick(ComParentComment c);

        void onItemLongClick(ComParentComment c);

    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.com_item_parent_comment, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, ComParentComment item) {
        UserInfo userInfo = item.getUserInfo();
        ImageUtils.displayImage(viewHolder.mContext, userInfo.getThumb(), R.mipmap.find_item_holder, viewHolder.iv_avatar);
        viewHolder.tv_nickname.setText(userInfo.getNickname());
        viewHolder.tv_comment_detail.setText(String.format("%s%s", item.getText(), TimeUtils.getTimeFormatText(item.getComment_date())));
        viewHolder.tv_nickname.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onHeadClick(item);
            }
        });
        viewHolder.itemView.setOnClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.onItemClick(item);
            }
        });
        viewHolder.itemView.setOnLongClickListener(v -> {
            if(onItemClickListener != null){
                onItemClickListener.onItemLongClick(item);
            }
            return true;
        });
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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
