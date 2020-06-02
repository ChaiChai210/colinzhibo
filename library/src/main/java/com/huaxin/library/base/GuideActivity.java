package com.huaxin.library.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.blankj.utilcode.util.SPUtils;
import com.example.librarybase.base.BaseActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.R;
import com.huaxin.library.R2;
import com.huaxin.library.custom.guide.CallBack;
import com.huaxin.library.custom.guide.GuideCustomViews;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.Constant;

import butterknife.BindView;

public class GuideActivity extends BaseActivity implements CallBack {

    private final int[] mPageImages = {
            R.mipmap.luancher_1,
            R.mipmap.luancher_2,
            R.mipmap.luancher_3,
            R.mipmap.luancher_4,
            R.mipmap.luancher_5
    };

    private final int[] mGuidePoint = {
            R.mipmap.icon_guide_point_select,
            R.mipmap.icon_guide_point_unselect
    };
    @BindView(R2.id.guide_CustomView)
    GuideCustomViews guideCustomView;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_guide;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        guideCustomView.setData(mPageImages, mGuidePoint, this);
    }
    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        ImmersionBar.with(this).fullScreen(true).init();
    }

    @Override
    public boolean showTitleView() {
        return false;
    }

    @Override
    public void callSlidingPosition(int position) {
        Log.e("callSlidingPosition", "滑动位置 callSlidingPosition " + position);
    }

    @Override
    public void callSlidingLast() {
        Log.e("callSlidingLast", "滑动到最后一个callSlidingLast");
    }

    @Override
    public void onClickLastListener() {
        SPUtils.getInstance().put(Constant.SP_FIRST_INSTALL, false);
        ARouter.getInstance().build(ARConstants.MAIN_ACTIVITY).navigation(this, new NavCallback() {
            @Override
            public void onArrival(Postcard postcard) {
                GuideActivity.this.finish();
            }
        });
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (guideCustomView != null) {
            guideCustomView.clear();
        }
    }


}
