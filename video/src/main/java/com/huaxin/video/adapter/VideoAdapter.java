package com.huaxin.video.adapter;

import android.content.Context;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.huaxin.library.adapter.BaseGroupedRecyclerViewAdapter;
import com.huaxin.library.utils.ImageUtils;
import com.huaxin.video.R;
import com.huaxin.video.entity.VideoEntity;
import com.huaxin.video.entity.VideoListEntity;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * 视频推荐列表适配器
 */
public class VideoAdapter extends BaseGroupedRecyclerViewAdapter<VideoListEntity> {

    private OnHeaderChildClickListener mOnHeaderChildClickListener;

    public VideoAdapter(Context context, List<VideoListEntity> data) {
        super(context);
        mData = data;
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
        return true;
    }

    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.adapter_video_header;
    }

    @Override
    public int getFooterLayout(int viewType) {
        return R.layout.adapter_video_foot;
    }

    @Override
    public int getChildLayout(int viewType) {
        return R.layout.adapter_video_item;
    }

    @Override
    public void onBindHeaderViewHolder(com.donkingliang.groupedadapter.holder.BaseViewHolder holder, int groupPosition) {
        VideoListEntity listEntity = mData.get(groupPosition);
        if (listEntity != null) {
            holder.setText(R.id.tv_name, listEntity.getName());
            holder.get(R.id.tv_more).setOnClickListener(v -> {
                if (mOnHeaderChildClickListener != null) {
                    ViewParent parent = holder.itemView.getParent();
                    int gPosition = parent instanceof FrameLayout ? groupPosition : getGroupPositionForPosition(holder.getLayoutPosition());
                    if (gPosition >= 0 && gPosition < mStructures.size()) {
                        mOnHeaderChildClickListener.onHeaderChildClick(VideoAdapter.this,
                                (BaseViewHolder) holder, gPosition);
                    }
                }
            });
        }
    }

    @Override
    public void onBindFooterViewHolder(com.donkingliang.groupedadapter.holder.BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(com.donkingliang.groupedadapter.holder.BaseViewHolder holder, int groupPosition, int childPosition) {
        if (mData.get(groupPosition).getData() != null) {
            VideoEntity childData = mData.get(groupPosition).getData().get(childPosition);
            holder.setText(R.id.tv_video_hint, childData.getTitle());
            RoundedImageView image = holder.get(R.id.rvi_image);
            ImageUtils.displayImage(mContext,childData.getThumb(),R.mipmap.video_item_holder,image);
        }
    }

    public void setOnHeaderChildClickListener(OnHeaderChildClickListener listener) {
        mOnHeaderChildClickListener = listener;
    }

    public interface OnHeaderChildClickListener {
        void onHeaderChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition);
    }
}
