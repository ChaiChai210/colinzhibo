package com.huaxin.usercenter.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ArrayUtils;
import com.example.librarybase.base.BaseActivity;
import com.example.librarybase.base.Event;
import com.example.librarybase.base.EventBusHelper;
import com.huaxin.library.adapter.VideoNavigatorAdapter;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.EventCode;
import com.huaxin.usercenter.R;
import com.huaxin.usercenter.R2;
import com.huaxin.usercenter.fragment.MyVideoFrg;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.PATH_USER_VIDEO)
public class VideoActivity extends BaseActivity implements VideoNavigatorAdapter.OnItemClickListener {
    @BindView(R2.id.magic_indicator)
    MagicIndicator magicIndicator;
    @BindView(R2.id.view_pager)
    ViewPager2 viewPager;
    @BindView(R2.id.iv_back)
    ImageView ivBack;
    @BindView(R2.id.tv_edit)
    TextView tvEdit;


    private List<Fragment> mChildFragments = new ArrayList<>();
    private List<String> mTitles;

    private boolean isEdit;
    private int type = 1;

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.act_video_collect;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        final CommonNavigator commonNavigator = new CommonNavigator(this);
        mTitles = ArrayUtils.asArrayList(getResources().getStringArray(R.array.video_collect_tabs));
        mChildFragments.add(MyVideoFrg.newInstance(1));
        mChildFragments.add(MyVideoFrg.newInstance(2));
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
                type = position + 1;
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });

        ivBack.setOnClickListener(v -> finish());

        tvEdit.setOnClickListener(v -> {
            isEdit = !isEdit;
            if (isEdit) {
                tvEdit.setText("完成");
                EventBusHelper.post(new Event(EventCode.CODE_EDIT, -1));
            } else {
                tvEdit.setText("编辑");

                EventBusHelper.post(new Event(EventCode.CODE_FINISH_EDIT, -1));
            }
        });


//        tvChooseAll.setOnClickListener(v ->
//                EventBusHelper.post(new Event(EventCode.CHOOSE_ALL, type)));
//        tvDelete.setOnClickListener(v ->
//                EventBusHelper.post(new Event(EventCode.DELETE, type)));
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
