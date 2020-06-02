package com.huaxin.usercenter.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ArrayUtils;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.adapter.VideoNavigatorAdapter;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.fragment.SystemMsgFrg;
import com.huaxin.usercenter.fragment.UserMsgFrg;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.PATH_USER_MSG)
public class UserMsgActivity extends BaseActivity implements VideoNavigatorAdapter.OnItemClickListener {
    @BindView(R2.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R2.id.view_pager)
    ViewPager2 viewPager;
    @BindView(R2.id.iv_back)
    ImageView ivBack;

    private List<Fragment> mChildFragments = new ArrayList<>();
    private List<String> mTitles;

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected void loadData() {

    }



    @Override
    protected int getContentViewId() {
        return R.layout.act_user_msg;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        final CommonNavigator commonNavigator = new CommonNavigator(this);
        mTitles = ArrayUtils.asArrayList(getResources().getStringArray(R.array.msg_tabs));
        mChildFragments.add(new UserMsgFrg());
        mChildFragments.add(new SystemMsgFrg());
        VideoNavigatorAdapter navigatorAdapter = new VideoNavigatorAdapter(mTitles);
        navigatorAdapter.setOnItemClickListener(this);
        commonNavigator.setAdapter(navigatorAdapter);
        magicIndicator.setNavigator(commonNavigator);
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
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
        ivBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @Override
    public void onItemClick(int position) {
        if (viewPager != null) {
            viewPager.setCurrentItem(position);
        }
    }
}
