package com.huaxin.library.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;

import com.huaxin.library.R;
import com.huaxin.library.custom.VideoTabPagerIndicator;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

import java.util.List;

public class VideoNavigatorAdapter extends CommonNavigatorAdapter {
    private List<String> mTitles;
    private OnItemClickListener mOnItemClickListener;

    public VideoNavigatorAdapter(List<String> titles) {
        mTitles = titles;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }


    @Override
    public IPagerTitleView getTitleView(Context context, int index) {
        SimplePagerTitleView titleView = new SimplePagerTitleView(context);
        titleView.setNormalColor(context.getResources().getColor(R.color.child_tab_normal));
        titleView.setTextSize(17);
        titleView.setGravity(Gravity.CENTER);
        titleView.setBackgroundResource(R.drawable.video_tab_bg);
        titleView.setSelectedColor(Color.WHITE);
        titleView.setText(mTitles.get(index));
        titleView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(index);
            }
        });
        return titleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        VideoTabPagerIndicator indicator = new VideoTabPagerIndicator(context);
        indicator.setBackgroundResource(R.drawable.video_tab_bg);
        return indicator;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
