package com.huaxin.video.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ArrayUtils;
import com.huaxin.library.adapter.VideoNavigatorAdapter;
import com.example.librarybase.base.BaseFragment;
import com.huaxin.library.intercepter.LoginNavigationCallbackImpl;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.RouteUtils;
import com.huaxin.video.R;
import com.huaxin.video.R2;
import com.huaxin.library.adapter.VideoStateAdapter;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * @author office
 * @describe
 * @date 2020/1/1  15:14
 * - 社区模块
 */
@Route(path = RouteUtils.Video_Fragment_Main)
public class MainVideoFragment extends BaseFragment implements VideoNavigatorAdapter.OnItemClickListener {

    @BindView(R2.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R2.id.view_pager)
    ViewPager viewPager;
    @BindView(R2.id.iv_search)
    ImageView ivSearch;
    @BindView(R2.id.iv_publish)
    ImageView ivPublish;
    private List<Fragment> mChildFragments = new ArrayList<>();
    private List<String> mTitles;

    @Override
    protected void loadData() {

    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        final CommonNavigator commonNavigator = new CommonNavigator(mContext);
        mTitles = ArrayUtils.asArrayList(getResources().getStringArray(R.array.video_tabs));
        mChildFragments.add(new VideoRecommendFragment());
        mChildFragments.add(new VideoDiscoverFragment());
        mChildFragments.add(new TiktokFragment2());
        VideoNavigatorAdapter navigatorAdapter = new VideoNavigatorAdapter(mTitles);
        navigatorAdapter.setOnItemClickListener(this);
        commonNavigator.setAdapter(navigatorAdapter);
        magicIndicator.setNavigator(commonNavigator);
        viewPager.setAdapter(new VideoStateAdapter(getChildFragmentManager(), mChildFragments));
        ViewPagerHelper.bind(magicIndicator, viewPager);
        ivPublish.setOnClickListener(v ->
                doPublish());
        ivSearch.setOnClickListener(v ->
                ARouter.getInstance().build(ARConstants.PATH_VIDEO_SEARCH).navigation());
    }

    private void doPublish() {
        ARouter.getInstance().build(ARConstants.VIDEO_PUBLISH).navigation(mContext, new LoginNavigationCallbackImpl());
    }
    @Override
    protected int getContentViewId() {
        return R.layout.fragment_video1;
    }


    @Override
    public void onItemClick(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }
}

