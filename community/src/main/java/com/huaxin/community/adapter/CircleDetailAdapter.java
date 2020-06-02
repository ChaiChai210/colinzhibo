package com.huaxin.community.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.community.R;
import com.huaxin.library.entity.LabelClassify;
import com.huaxin.library.entity.LabelDetail;

import java.util.List;

public class CircleDetailAdapter extends BaseQuickAdapter<LabelDetail, BaseViewHolder> {

    public CircleDetailAdapter(@Nullable List<LabelDetail> data) {
        super(com.huaxin.library.R.layout.item_circle_detail, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, LabelDetail item) {
        helper.setText(com.huaxin.library.R.id.tv_title, item.getName());
        helper.setText(com.huaxin.library.R.id.tv_intro, item.getDescription());

        Glide.with(getContext()).load(item.getAvatar()).placeholder(R.drawable.ic_avatar).circleCrop().into((ImageView) helper.getView(com.huaxin.library.R.id.iv_avatar));
    }
}
