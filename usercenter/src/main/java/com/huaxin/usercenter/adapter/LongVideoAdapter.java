package com.huaxin.usercenter.adapter;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.entity.LongVideoBean;

import java.util.List;

public class LongVideoAdapter extends BaseQuickAdapter<LongVideoBean, BaseViewHolder> {
    private boolean edited;
    private boolean allChoose;

    public void setEdited(boolean edited) {
        this.edited = edited;
        notifyDataSetChanged();
    }
    public void setAllChoose(boolean allChoose) {
        this.allChoose = allChoose;
        notifyDataSetChanged();
    }

    public LongVideoAdapter(@Nullable List<LongVideoBean> data) {
        super(R.layout.item_long_video, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder holder, @Nullable LongVideoBean item) {
        holder.setText(R.id.tv_video_name,item.getTitle());
        holder.setText(R.id.tv_video_intro,item.getIntro());
        String time = item.getDuration();
        holder.setText(R.id.tv_time,time);

        ImageView choose = holder.getView(R.id.iv_choose);

        holder.setText(R.id.tv_time, item.getCreated_at());
        ImageUtils.displayImage(getContext(),item.getThumb(),R.drawable.user_video_place_holder,holder.getView(R.id.iv_avatar));
        if(edited){
            choose.setVisibility(View.VISIBLE);
            if(item.isChecked()){
                Glide.with(getContext()).load(R.drawable.ic_cb_checked).into(choose);
            }else {
                Glide.with(getContext()).load(R.drawable.ic_cb_unchecked).into(choose);
            }
            choose.setOnClickListener(v -> {
                item.setChecked(!item.isChecked());
                notifyDataSetChanged();
            });
        }else {
            choose.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(v ->
                ARouter.getInstance().build(ARConstants.PATH_VIDEO_PLAY).withInt("id", item.getId()).navigation()
        );

    }
}
