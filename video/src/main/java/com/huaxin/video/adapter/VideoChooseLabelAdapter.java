package com.huaxin.video.adapter;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.entity.LabelClassify;
import com.huaxin.library.entity.LabelDetail;
import com.huaxin.video.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类视频适配器
 */
public class VideoChooseLabelAdapter extends BaseQuickAdapter<LabelDetail, BaseViewHolder> {

    private ArrayList<LabelDetail> mData;

    public VideoChooseLabelAdapter(@Nullable ArrayList<LabelDetail> data) {
        super(R.layout.item_video_choose_label, data);
        mData = data;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable LabelDetail item) {
        TextView hotSearchItem = holder.getView(com.huaxin.library.R.id.tv_tag);
        hotSearchItem.setText(item.getName());
        holder.getView(R.id.ib_delete).setOnClickListener(v ->
                {
                    mData.remove(item);
                    notifyItemRemoved(holder.getAdapterPosition());
                }
        );
    }


}
