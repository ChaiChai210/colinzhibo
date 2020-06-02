package com.huaxin.video.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.video.R;
import com.huaxin.video.entity.VideoTypeEntity;


import java.util.List;

/**
 * 视频推荐分类
 */
public class RecommendTypeAdapter extends BaseQuickAdapter<VideoTypeEntity, BaseViewHolder> {
    public RecommendTypeAdapter(@Nullable List data) {
        super(R.layout.item_video_type, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, @Nullable VideoTypeEntity videoTypeEntity) {
        baseViewHolder.setText(R.id.tv_title, videoTypeEntity.getName());
        ImageView imageView = baseViewHolder.getView(R.id.iv_logo);
        ImageUtils.displayImage(getContext(),videoTypeEntity.getImage(),R.mipmap.video_item_holder,imageView);
    }
}
