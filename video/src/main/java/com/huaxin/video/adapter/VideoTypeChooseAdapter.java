package com.huaxin.video.adapter;

import android.view.View;
import android.widget.CheckedTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.video.R;
import com.huaxin.video.entity.VideoTypeEntity;

import java.util.List;

/**
 * 视频分类过滤适配器
 */
public class VideoTypeChooseAdapter extends BaseQuickAdapter<VideoTypeEntity, BaseViewHolder> {

    private int mTypeId;


    public VideoTypeChooseAdapter(int typeId, @Nullable List<VideoTypeEntity> data) {
        super(R.layout.item_video_type_choose, data);
        mTypeId = typeId;
    }

    public void setChecked(int pos) {
        mTypeId = getData().get(pos).getId();

    }

    public int getChecked() {
        int pos = 0;
        for (int i = 0; i < getData().size(); i++) {
            if (mTypeId == getData().get(i).getId()) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, @Nullable VideoTypeEntity videoTypeEntity) {
        CheckedTextView checkedTextView = baseViewHolder.getView(R.id.tv_title);
        checkedTextView.setText(videoTypeEntity.getName());
        checkedTextView.setChecked(videoTypeEntity.getId() == mTypeId);
    }

    @Override
    protected void setOnItemClick(@NonNull View v, int position) {
        super.setOnItemClick(v, position);
        mTypeId = getData().get(position).getId();
    }
}
