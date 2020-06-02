package com.huaxin.community.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.ArrayUtils;
import com.huaxin.community.R;
import com.huaxin.community.R2;
import com.huaxin.library.adapter.VideoNavigatorAdapter;
import com.huaxin.library.adapter.VideoStateAdapter;
import com.example.librarybase.base.BaseFragment;
import com.huaxin.library.intercepter.LoginNavigationCallbackImpl;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.RouteUtils;

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
@Route(path = RouteUtils.Community_Fragment_Main)
public class MainComunityFragment extends BaseFragment implements VideoNavigatorAdapter.OnItemClickListener {

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
        mTitles = ArrayUtils.asArrayList(getResources().getStringArray(R.array.community_tabs));
        mChildFragments.add(CommunityFragment.newInstance(0));
        mChildFragments.add(CommunityFragment.newInstance(2));
        mChildFragments.add(CommunityFragment.newInstance(1));
        mChildFragments.add(CommunityFragment.newInstance(3));
        VideoNavigatorAdapter navigatorAdapter = new VideoNavigatorAdapter(mTitles);
        navigatorAdapter.setOnItemClickListener(this);
        commonNavigator.setAdapter(navigatorAdapter);
        magicIndicator.setNavigator(commonNavigator);
        viewPager.setAdapter(new VideoStateAdapter(getChildFragmentManager(), mChildFragments));
        ViewPagerHelper.bind(magicIndicator, viewPager);
//        viewPager.setAdapter(new FragmentStateAdapter(this) {
//            @NonNull
//            @Override
//            public Fragment createFragment(int position) {
//                return mChildFragments.get(position);
//            }
//
//            @Override
//            public int getItemCount() {
//                return mChildFragments.size();
//            }
//        });
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("chia","position"+positionOffset+"off"+positionOffsetPixels);
//                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
//            }
//
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                Log.e("chia","onPageSelected"+position);
//                magicIndicator.onPageSelected(position);
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int state) {
//                Log.e("chia","onPageScrollStateChanged"+state);
//                magicIndicator.onPageScrollStateChanged(state);
//            }
//        });
        ivPublish.setOnClickListener(v ->
                doPublish());

        ivSearch.setOnClickListener(v ->
                ARouter.getInstance().build(ARConstants.PATH_COMMUNITY_SEARCH).navigation());
    }

    private void doPublish() {
        ARouter.getInstance().build(ARConstants.PATH_COMMUNTIY_PUBLISH).navigation(mContext, new LoginNavigationCallbackImpl());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_forum;
    }


    @Override
    public void onItemClick(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }
}

