package com.huaxin.library.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.example.librarybase.base.BaseActivity;
import com.example.librarybase.base.BaseFragment;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.R;
import com.huaxin.library.R2;
import com.huaxin.library.custom.SuperPlayerView;
import com.huaxin.library.db.WatchRecord;
import com.huaxin.library.db.WatchRecordRepository;
import com.huaxin.library.utils.Util;
import com.tencent.liteav.demo.play.SuperVodPlayerView;

import butterknife.BindView;

public abstract class AbVideoFragment1 extends BaseFragment implements SuperVodPlayerView.OnSuperPlayerViewCallback, SuperPlayerView.OnPlayEvent {
    @BindView(R2.id.rv_content)
    protected RecyclerView rvContent;
    private LinearLayoutManager manager;
    private SuperVodPlayerView mSuperPlayerView;
    private FrameLayout mCurrentFrame;
    private int mCurrentPosition = -1;

    private WatchRecordRepository watchRecordRepository;

    @Override
    protected void loadData() {
        watchRecordRepository = new WatchRecordRepository(mContext);
    }


    protected void playVideo(String url, String title, int position) {
        removeParentView();
        mCurrentPosition = position;
        ViewGroup viewGroup = (ViewGroup) manager.findViewByPosition(position);
        mCurrentFrame = viewGroup.findViewById(R.id.fl_video);
        mCurrentFrame.addView(mSuperPlayerView, 0);
        mSuperPlayerView.play(url, title);
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mSuperPlayerView = new SuperVodPlayerView(getContext());
//        mSuperPlayerView.setOnPlayEvent(this);
        mSuperPlayerView.setPlayerViewCallback(this);
        manager = new LinearLayoutManager(mContext);
        rvContent.setLayoutManager(manager);
        ((SimpleItemAnimator) rvContent.getItemAnimator()).setSupportsChangeAnimations(false);

        rvContent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy != 0) {
                    int firstVisiblePosition = manager.findFirstVisibleItemPosition();
                    int lastVisiblePosition = manager.findLastVisibleItemPosition();
                    int currentPlayPosition = mCurrentPosition;
                    if (currentPlayPosition >= 0) {
                        if ((currentPlayPosition <= firstVisiblePosition || currentPlayPosition >= lastVisiblePosition - 1)) {
                            if (Util.getViewVisiblePercent(manager.findViewByPosition(mCurrentPosition)) < 0.2f) {
                                if (mSuperPlayerView != null && mSuperPlayerView.isPlaying()) {
                                    pause(mCurrentPosition);
                                }

                                if (mCurrentFrame != null) {
                                    mCurrentFrame.removeView(mSuperPlayerView);
                                    mCurrentFrame = null;
                                }
                            }
                        }
                    }
                }
            }
        });
    }


    private void removeParentView() {
        ViewGroup parent = (ViewGroup) mSuperPlayerView.getParent();
        if (parent != null) {
            parent.removeView(mSuperPlayerView);
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_community_recommend;
    }

    @Override
    public void onStartFullScreenPlay() {
        if (getContext() instanceof BaseActivity) {
            removeParentView();
            ViewGroup viewGroup = (ViewGroup) ((BaseActivity) getContext()).getWindow().getDecorView();
            ((BaseActivity) getContext()).getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            ((BaseActivity) getContext()).getWindow().addFlags(WindowManager.LayoutParams.
                    FLAG_KEEP_SCREEN_ON);
            viewGroup.addView(mSuperPlayerView);
        }
    }

    @Override
    public void onStopFullScreenPlay() {
        if (getContext() instanceof BaseActivity) {
            removeParentView();
            if (mCurrentFrame != null) {
                mCurrentFrame.addView(mSuperPlayerView, 0);
            }
            Window windowBack = ((BaseActivity) getContext()).getWindow();
            //设置当前窗体为全屏显示
            int flagBack = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            windowBack.clearFlags(flagBack);
            ImmersionBar.with((Activity) mContext).fitsSystemWindows(true).init();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCurrentPosition != -1) {
            pause(mCurrentPosition);
        }
    }

    private void pause(int pos) {
        if (mSuperPlayerView != null) {
            mSuperPlayerView.onPause();
        }
    }

    @Override
    public void onClickFloatCloseBtn() {

    }

    @Override
    public void onClickSmallReturnBtn() {

    }

    @Override
    public void onStartFloatWindowPlay() {

    }

    @Override
    public void onReadyPlay() {

    }

    @Override
    public void onFirstFrame() {
    }


    @Override
    public void onPlayerPause() {
        pause(mCurrentPosition);
    }

    protected abstract WatchRecord getWatchRecord(int mCurrentPosition, float duration);

    @Override
    public void onFinish() {
    }

}
