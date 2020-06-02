package com.huaxin.video.adapter;

import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.video.R;
import com.huaxin.video.entity.VideoClassifyDetailEntity;


import java.util.List;

/**
 * 分类视频适配器
 */
public class VideoClassifyAdapter extends BaseQuickAdapter<VideoClassifyDetailEntity.DataBean, BaseViewHolder> {


    public VideoClassifyAdapter(@Nullable List<VideoClassifyDetailEntity.DataBean> data) {
        super(R.layout.item_video_view, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, @Nullable VideoClassifyDetailEntity.DataBean item) {
        baseViewHolder.setText(R.id.tv_title, item.getTitle());
        ImageView image = baseViewHolder.getView(R.id.riv_image);
        ImageUtils.displayImage(getContext(), item.getThumb(), R.mipmap.video_item_holder, image);
        baseViewHolder.itemView.setOnClickListener(v -> {
            ARouter.getInstance().build(ARConstants.PATH_VIDEO_PLAY).withInt("id", item.getId()).navigation();
        });
    }


}
