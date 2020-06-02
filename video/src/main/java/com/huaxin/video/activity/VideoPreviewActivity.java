package com.huaxin.video.activity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.huaxin.library.custom.SuperPlayerView;
import com.huaxin.library.custom.TitleLayout;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.Constant;
import com.huaxin.library.utils.TimeUtils;
import com.huaxin.video.R;
import com.huaxin.video.R2;
import com.kproduce.roundcorners.RoundButton;
import com.tencent.liteav.demo.play.SuperPlayerConst;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@Route(path = ARConstants.VIDEO_PREVIEW)
public class VideoPreviewActivity extends BaseActivity implements SuperPlayerView.OnPlayEvent {
    @BindView(R2.id.player)
    SuperPlayerView mSuperVideoView;
    @BindView(R2.id.rb_30s)
    RadioButton rb30s;
    @BindView(R2.id.rb_1min)
    RadioButton rb1min;
    @BindView(R2.id.rb_2min)
    RadioButton rb2min;
    @BindView(R2.id.rg_choose_duration)
    RadioGroup rgChooseDuration;
    @BindView(R2.id.btn_submit)
    RoundButton btnSubmit;
    private int pauseStart;
    private long duration;
    private int seconds;
    @Override
    protected void loadData() {
        Bundle bundle = getIntent().getBundleExtra(Constant.VIDEO_BUNDLE);
        if(bundle != null)
        {
            String url = bundle.getString(Constant.VIDEO_URL);
            duration = bundle.getLong(Constant.VIDEO_DURATION);
            seconds = (int) (duration/1000);
            mSuperVideoView.play(url, "");
    }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected int getContentViewId() {
        return R.layout.video_act_preview;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("精彩预览");
        mSuperVideoView.setBackVisibility(View.GONE);
        mSuperVideoView.setOnPlayEvent(this);
        mSuperVideoView.hideFullScreen();

        rgChooseDuration.setOnCheckedChangeListener((radioGroup, id) -> {
                    if (id == R.id.rb_30s) {
                        duration = 30;
                    } else if (id == R.id.rb_1min) {
                        duration = 60;
                    }else if(id == R.id.rb_2min){
                        duration = 120;
                    }
                }
        );

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


    @Override
    public void onReadyPlay() {

    }

    @Override
    public void onFirstFrame() {

    }

    @Override
    public void onPlayerPause() {
        pause();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSuperVideoView.onPause();
    }

    private void pause() {
        if (mSuperVideoView != null) {
            mSuperVideoView.onPause();
            if (mSuperVideoView.getmCurrentTimeWhenPause() > 0) {
                pauseStart = (int) mSuperVideoView.getmCurrentTimeWhenPause();
            }
        }
    }

    @Override
    public void onFinish() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSuperVideoView.getPlayState() == SuperPlayerConst.PLAYSTATE_PLAYING) {
            mSuperVideoView.onResume();
        }
        if (mSuperVideoView.getPlayMode() == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
            //隐藏虚拟按键，并且全屏
            View decorView = getWindow().getDecorView();
            if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                decorView.setSystemUiVisibility(View.GONE);
            } else if (Build.VERSION.SDK_INT >= 19) {
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
            }
        }
    }

    @OnClick(R2.id.btn_submit)
    public void onViewClicked() {
        if (pauseStart == 0) {
            ToastUtils.showShort("请暂停视频选择开始时间");
        }


    }
}

