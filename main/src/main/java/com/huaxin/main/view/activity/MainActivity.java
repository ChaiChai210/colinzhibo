package com.huaxin.main.view.activity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.huaxin.library.R2;
import com.huaxin.library.entity.ConfigEntity;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.FragmentUtils;
import com.huaxin.library.utils.Util;
import com.huaxin.main.R;
import com.huaxin.main.adapter.MainStateAdapter;
import com.huaxin.main.dialog.UpdatePopWindow;
import com.huaxin.main.view.fragment.MainLiveFragment;
import com.lxj.xpopup.XPopup;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

@Route(path = ARConstants.MAIN_ACTIVITY)
public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R2.id.vp_content)
    ViewPager2 mVpContent;
    @BindView(R2.id.bn_main_tab)
    BottomNavigationView mMainBottomTab;
    private List<Fragment> mMainFragments;
    private MainStateAdapter mMainAdapter;

    private long clickTime = 0; // 第一次点击的时间
    private ConfigEntity userEntity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MMKV.initialize(this);
        userEntity = com.huaxin.library.utils.AppUtils.getConfig();
        ConfigEntity.AndroidBean android = null;
        if (userEntity != null) {
            android = userEntity.getAndroid();
        }
        if (android != null && android.getVersion_code() > AppUtils.getAppVersionCode()) {
            showUpdate(android);
        }
    }
    private void showUpdate(ConfigEntity.AndroidBean android) {
        UpdatePopWindow customPopup = new UpdatePopWindow(this, android);
        new XPopup.Builder(this)
                .dismissOnTouchOutside(!android.isForce_update())
                .asCustom(customPopup)
                .show();
    }
    @Override
    protected void loadData() {

    }

    @Override
    public void onBackPressed() {
        exit();
    }

    private void exit() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            ToastUtils.showShort("再按一次后退键退出程序");
            clickTime = System.currentTimeMillis();
        } else {
            this.finish();
        }
    }

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        mMainFragments = new ArrayList<>();
        mMainFragments.clear();

        mMainFragments.add(new MainLiveFragment());
        mMainFragments.add(FragmentUtils.getVideoFragment());
        mMainFragments.add(FragmentUtils.getCommunityFragment());
        mMainFragments.add(FragmentUtils.getMineFragment());
        mMainBottomTab.setSelectedItemId(R.id.main_video);
        mMainBottomTab.setOnNavigationItemSelectedListener(this);
        mMainBottomTab.setItemIconTintList(null);
        mVpContent.setOffscreenPageLimit(2);
        mVpContent.setUserInputEnabled(false);
        mMainAdapter = new MainStateAdapter(this, mMainFragments);
        mVpContent.setAdapter(mMainAdapter);
        mVpContent.setCurrentItem(1);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.checkPermission(this);
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int i = menuItem.getItemId();
        if (i == R.id.main_live) {
            mVpContent.setCurrentItem(0);
        } else if (i == R.id.main_video) {
            mVpContent.setCurrentItem(1);
        } else if (i == R.id.main_forum) {
            mVpContent.setCurrentItem(2);
        } else if (i == R.id.main_user) {
            mVpContent.setCurrentItem(3);
        }
        return true;
    }
}

