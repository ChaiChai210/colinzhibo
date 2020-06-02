package com.huaxin.usercenter.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.entity.SystemMsgBean;

import java.util.List;

public class SystemMsgAdapter extends BaseQuickAdapter<SystemMsgBean, BaseViewHolder> {
    public SystemMsgAdapter(@Nullable List<SystemMsgBean> data) {
        super(R.layout.item_system_msg, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable SystemMsgBean item) {
        holder.setText(R.id.tv_title,item.getNickname());
        holder.setText(R.id.tv_content,item.getContetnt());
        String time = "发布于"+item.getCreated_at();
        holder.setText(R.id.tv_time,time);

//        helper.setText(com.huaxin.library.R.id.tv_time, TimeUtils.getTimeFormatText(data.getComment_date()));
        ImageUtils.displayAvatar(getContext(),item.getAvatar(),holder.getView(R.id.civ_avatar));
    }
}
