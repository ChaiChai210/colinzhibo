package com.huaxin.usercenter.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.huaxin.library.adapter.BaseGroupedRecyclerViewAdapter;
import com.huaxin.library.db.Download;
import com.huaxin.library.db.WatchRecord;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.library.utils.TimeUtils;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.entity.DownloadListEntity;

import java.util.List;

public class DownloadAdapter extends BaseGroupedRecyclerViewAdapter<DownloadListEntity> {
    private boolean edited;

    public DownloadAdapter(Context context, List<DownloadListEntity> data) {
        super(context);
        mData = data;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount() {
        return mData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mData.get(groupPosition).getData() != null) {
            return mData.get(groupPosition).getData().size();
        } else {
            return 0;
        }

    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.layout_download_head;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.item_download;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        holder.setText(R.id.tv_title, mData.get(groupPosition).getName());
//        holder.get(R.id.iv_up).setVisibility(View.GONE);
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
        Download watchRecord = mData.get(groupPosition).getData().get(childPosition);
        holder.setText(R.id.tv_video_name, watchRecord.getTitle());
        holder.setText(R.id.tv_video_intro, watchRecord.getIntro());

        ImageView choose = holder.get(R.id.iv_choose);

        ImageUtils.displayImage(mContext, watchRecord.getThumb(), R.drawable.user_video_place_holder, holder.get(R.id.iv_avatar));
//        if (edited) {
//            choose.setVisibility(View.VISIBLE);
//            if (watchRecord.isChecked()) {
////                choose.setImageDrawable(R.drawable.ic_cb_checked);
//                Glide.with(mContext).load(R.drawable.ic_cb_checked).into(choose);
//            } else {
//                Glide.with(mContext).load(R.drawable.ic_cb_unchecked).into(choose);
//            }
//            choose.setOnClickListener(v -> {
//                watchRecord.setChecked(!watchRecord.isChecked());
//                notifyChildChanged(groupPosition,childPosition);
//            });
//        } else {
//            choose.setVisibility(View.GONE);
//        }
//
//        holder.itemView.setOnClickListener(v ->
//                ARouter.getInstance().build(ARConstants.PATH_VIDEO_PLAY).withInt("id", (int) watchRecord.getId()).navigation()
//        );
    }

}
