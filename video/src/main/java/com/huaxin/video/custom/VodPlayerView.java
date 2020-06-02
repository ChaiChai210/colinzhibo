package com.huaxin.video.custom;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.huaxin.video.R;
import com.tencent.liteav.demo.play.SuperPlayerGlobalConfig;
import com.tencent.liteav.demo.play.SuperPlayerModel;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.io.File;

/**
 * Created by liyuejiao on 2018/7/3.
 * <p>
 * 超级播放器view
 * <p>
 * 具备播放器基本功能，此外还包括横竖屏切换、悬浮窗播放、画质切换、硬件加速、倍速播放、镜像播放、手势控制等功能，同时支持直播与点播
 * <p>
 * 使用方式极为简单，只需要在布局文件中引入并获取到该控件，通过{@link #( SuperPlayerModel )}传入{@link SuperPlayerModel}即可实现视频播放
 * <p>
 * 1、播放视频{@link #( SuperPlayerModel )}
 * <p>
 * <p>
 * 3、点播相关：初始化播放器{@link #initVodPlayer(Context)}，播放事件监听{@link #onPlayEvent(TXVodPlayer, int, Bundle)}，
 * 网络事件监听{@link #onNetStatus(TXVodPlayer, Bundle)}
 * <p>
 * <p>
 * <p>
 * 5、退出播放释放内存{@link #()}
 */

public class VodPlayerView extends RelativeLayout implements ITXVodPlayListener {
    private TXCloudVideoView mTXCloudVideoView;
    private TXVodPlayer mVodPlayer;
    private boolean mPaused;//生命周期暂停
    private ActionListener mActionListener;
    private boolean mStartPlay;
    private boolean mEndPlay;
    private String mCachePath;
    private TXVodPlayConfig mTXVodPlayConfig;
    private boolean mAutoPlay = true;
    private boolean mShow = false;
    private String mCurrentUrl;
    private float mCurrentTimeWhenPause; // 记录onPause暂停时的时间，在播放widevine格式的时候，onResume需要Seek回去，否则需要等到下一个I帧到来才能有画面。

    private Context mContext;
    // UI
    private ViewGroup mRootView;                      // SuperPlayerView的根view

    //    //    封面
//    private ImageView cover;
    public float getmCurrentTimeWhenPause() {
        return mCurrentTimeWhenPause;
    }

    public VodPlayerView(Context context) {
        super(context);
        initView(context);
    }


    public VodPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public VodPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化view
     *
     * @param context
     */
    private void initView(Context context) {
        mContext = context;
        mRootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.vod_player, this);
        mTXCloudVideoView = mRootView.findViewById(R.id.cloud_video_view);
//        cover = mRootView.findViewById(R.id.iv_cover);
        mTXCloudVideoView.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        initVodPlayer(context);
//        TCLogReport.getInstance().setAppName(context);
//        TCLogReport.getInstance().setPackageName(context);
    }

    /**
     * 初始化点播播放器，原版的
     *
     * @param context
     */
    private void initVodPlayer(Context context) {
        if (mVodPlayer != null)
            return;
        mVodPlayer = new TXVodPlayer(context);
        SuperPlayerGlobalConfig config = SuperPlayerGlobalConfig.getInstance();
        mTXVodPlayConfig = new TXVodPlayConfig();
        mTXVodPlayConfig.setMaxCacheItems(15);
        mTXVodPlayConfig.setProgressInterval(200);

        File sdcardDir = context.getExternalFilesDir(null);
        if (sdcardDir != null) {
            mTXVodPlayConfig.setCacheFolderPath(sdcardDir.getPath() + "/txcache");
        }
        mVodPlayer.setConfig(mTXVodPlayConfig);
        mVodPlayer.setVodListener(this);
        mVodPlayer.setPlayerView(mTXCloudVideoView);
        mVodPlayer.enableHardwareDecode(config.enableHWAcceleration);
    }

    /**
     * 播放器事件回调
     */
    @Override
    public void onPlayEvent(TXVodPlayer txVodPlayer, int e, Bundle bundle) {
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN://加载完成，开始播放的回调
                mStartPlay = true;
                if (mActionListener != null) {
                    mActionListener.onPlayBegin();
                }

                break;
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING: //开始加载的回调
                if (mActionListener != null) {
                    mActionListener.onPlayLoading();
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END://获取到视频播放完毕的回调
                if (!mEndPlay) {
                    mEndPlay = true;
                    if (!mPaused) {
                        replay();
                    }

                   /* if (mVideoBean != null) {
                        VideoHttpUtil.videoWatchEnd(mVideoBean.getUid(), mVideoBean.getId());
                    }*/
                }
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME://获取到视频首帧回调
                if (mActionListener != null) {
                    mActionListener.onFirstFrame();
                }
                if (mPaused && mVodPlayer != null) {
                    mVodPlayer.pause();
                }
                break;
//            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION://获取到视频宽高回调
//                onVideoSizeChanged(bundle.getInt("EVT_PARAM1", 0), bundle.getInt("EVT_PARAM2", 0));
//                break;
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer txVodPlayer, Bundle bundle) {

    }


    /**
     * 获取到视频宽高回调
     */
    public void onVideoSizeChanged(float videoWidth, float videoHeight) {
        if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
            LayoutParams params = (LayoutParams) mTXCloudVideoView.getLayoutParams();
            int targetH = 0;
            if (videoWidth / videoHeight > 0.5625f) {//横屏 9:16=0.5625
                targetH = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
            } else {
                targetH = ViewGroup.LayoutParams.MATCH_PARENT;
            }
            if (targetH != params.height) {
                params.height = targetH;
                mTXCloudVideoView.requestLayout();
            }

        }
    }

    /**
     * 开始播放
     */
    public void startPlay(String url) {
        mCurrentUrl = url;
        Log.e("chia", mCurrentUrl);
        mStartPlay = false;
        mEndPlay = false;
        if (TextUtils.isEmpty(mCurrentUrl)) {
            return;
        }
        if (mTXVodPlayConfig == null) {
            mTXVodPlayConfig = new TXVodPlayConfig();
            mTXVodPlayConfig.setMaxCacheItems(10);
            mTXVodPlayConfig.setProgressInterval(200);
        }
        if (mCurrentUrl.endsWith(".m3u8")) {
            mTXVodPlayConfig.setCacheFolderPath(null);
        } else {
            mTXVodPlayConfig.setCacheFolderPath(mCachePath);
        }
        mVodPlayer.setConfig(mTXVodPlayConfig);
        mVodPlayer.setAutoPlay(mAutoPlay);
        if (mVodPlayer != null && !mVodPlayer.isPlaying()) {
            mVodPlayer.startPlay(mCurrentUrl);
        }
        // VideoHttpUtil.videoWatchStart(videoBean.getUid(), videoBean.getId());
    }

    /**
     * 停止播放
     */
    public void stopPlay() {
        if (mVodPlayer != null) {
            mVodPlayer.stopPlay(false);
        }

    }

    /**
     * 循环播放
     */
    private void replay() {
        if (mVodPlayer != null) {
            mVodPlayer.seek(0);
            mVodPlayer.resume();
        }
    }

    public void release() {
        //VideoHttpUtil.cancel(VideoHttpConsts.VIDEO_WATCH_START);
        //VideoHttpUtil.cancel(VideoHttpConsts.VIDEO_WATCH_END);
        if (mVodPlayer != null) {
            mVodPlayer.stopPlay(false);
            mVodPlayer.setVodListener(null);
        }
        mVodPlayer = null;
        mActionListener = null;
    }

    /**
     * 生命周期暂停
     */
    public void pausePlay() {
        mPaused = true;
        if (mVodPlayer != null) {
            mVodPlayer.pause();
            mCurrentTimeWhenPause = mVodPlayer.getCurrentPlaybackTime();
        }
    }

    /**
     * 生命周期恢复
     */
    public void resumePlay() {
        if (mPaused) {
            if (mVodPlayer != null) {
                mVodPlayer.resume();
                mVodPlayer.seek(mCurrentTimeWhenPause);
                mCurrentTimeWhenPause = 0;
            }
        }
        mPaused = false;
    }

    public boolean isPlaying() {
        if (mVodPlayer != null) {
            return mVodPlayer.isPlaying();
        } else {
            return false;
        }

    }

    public void setUrl(String url) {
        mCurrentUrl = url;
    }


    public interface ActionListener {
        void onPlayBegin();

        void onPlayLoading();

        void onFirstFrame();
    }


    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public void setAutoPlay(boolean isAutoPlay) {
        mAutoPlay = isAutoPlay;
    }
}
