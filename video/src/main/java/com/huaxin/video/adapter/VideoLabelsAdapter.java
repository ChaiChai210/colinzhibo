package com.huaxin.video.adapter;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.entity.LabelDetail;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.Constant;
import com.huaxin.video.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 视频标签
 */
public class VideoLabelsAdapter extends BaseQuickAdapter<LabelDetail, BaseViewHolder> {

    private ArrayList<LabelDetail> mData;

    public VideoLabelsAdapter(@Nullable ArrayList<LabelDetail> data) {
        super(R.layout.item_video_choose_label, data);
        mData = data;
        addChildClickViewIds(R.id.tv_tag);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable LabelDetail item) {
        TextView hotSearchItem = holder.getView(com.huaxin.library.R.id.tv_tag);
        hotSearchItem.setText(item.getName());
        ViewGroup.LayoutParams lp = hotSearchItem.getLayoutParams();
        if (holder.getAdapterPosition() == mData.size() - 1) {
            holder.getView(R.id.ib_delete).setVisibility(View.GONE);
            hotSearchItem.setGravity(Gravity.CENTER_HORIZONTAL);
//            hotSearchItem.setOnClickListener(v ->
//                    ARouter.getInstance().build(ARConstants.VIDEO_CHOOSE_LABEL)
//                    .withParcelableArrayList(Constant.EXTRA_CHOSEN_LABELS, getLabels())
//                    .navigation((Activity) getContext(), ARConstants.REQUEST_CHOOSE_LABELS));

//            hotSearchItem.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        } else {
            holder.getView(R.id.ib_delete).setVisibility(View.VISIBLE);
            holder.getView(R.id.ib_delete).setOnClickListener(v ->
                    {
                        mData.remove(item);
                        notifyItemRemoved(holder.getAdapterPosition());
                    }
            );
        }

    }

    private ArrayList<LabelDetail> getLabels() {
        mData.remove(mData.size() - 1);
        return mData;
    }

}
