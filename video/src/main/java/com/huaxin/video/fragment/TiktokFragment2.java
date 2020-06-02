package com.huaxin.video.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.librarybase.base.BaseFragment;
import com.huaxin.video.R;
import com.huaxin.video.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TiktokFragment2 extends BaseFragment {

    @BindView(R2.id.view_pager)
    ViewPager2 viewPager;
    private List<Fragment> mChildFragments = new ArrayList<>();

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mChildFragments.add(new TiktokFragment1());
        mChildFragments.add(new TiktokUserInfoFragment());
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return mChildFragments.get(position);
            }

            @Override
            public int getItemCount() {
                return mChildFragments.size();
            }
        });
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_tiktok_item;
    }
}
