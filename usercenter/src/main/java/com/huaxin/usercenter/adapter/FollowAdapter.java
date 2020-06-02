package com.huaxin.usercenter.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.entity.FollowBean;

import java.util.List;

public class FollowAdapter extends BaseQuickAdapter<FollowBean, BaseViewHolder> {
    public FollowAdapter(  @Nullable List<FollowBean> data) {
        super(R.layout.item_user_follow, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable FollowBean followBean) {
        holder.setText(R.id.tv_nickname,followBean.getNickname());
        ImageUtils.displayAvatar(getContext(),followBean.getThumb(),holder.getView(R.id.civ_avatar));
    }
}
