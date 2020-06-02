package com.huaxin.video.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.video.R;
import com.huaxin.video.entity.VideoDiscoverRsp;


import java.util.List;

/**
 * 视频发现列表适配器
 */
public class VideoDiscoverAdapter extends BaseQuickAdapter<VideoDiscoverRsp.DataBean, BaseViewHolder> {
    public VideoDiscoverAdapter(@Nullable List<VideoDiscoverRsp.DataBean> data) {
        super(R.layout.video_item_discover, data);
        addChildClickViewIds(R.id.ibn_play, R.id.iv_like,R.id.iv_download);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, @Nullable VideoDiscoverRsp.DataBean item) {
        baseViewHolder.setText(R.id.tv_title, item.getTitle());
        ImageView image = baseViewHolder.getView(R.id.iv_preview);
        ImageUtils.displayImage(getContext(), item.getThumb(), R.mipmap.find_item_holder, image);
        baseViewHolder.setText(R.id.tv_play_num, String.format(getContext().getResources().getString(R.string.video_play_num), item.getPlay_count()));
        ImageView like = baseViewHolder.getView(R.id.iv_like);

        if (item.isIs_collection()) {
            like.setImageResource(R.drawable.ic_like_checked);
        } else {
            like.setImageResource(R.drawable.ic_like_normal);
        }

        ImageView download = baseViewHolder.getView(R.id.iv_download);
    }
}
