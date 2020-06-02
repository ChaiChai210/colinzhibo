package com.huaxin.library.custom;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.R;
import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.liteav.demo.play.SuperPlayerConst;
import com.tencent.liteav.demo.play.SuperPlayerGlobalConfig;
import com.tencent.liteav.demo.play.bean.TCVideoQuality;
import com.tencent.liteav.demo.play.colin.TCVodControllerBase;
import com.tencent.liteav.demo.play.colin.TCVodControllerLarge;
import com.tencent.liteav.demo.play.colin.TCVodControllerSmall;
import com.tencent.liteav.demo.play.common.TCPlayerConstants;
import com.tencent.liteav.demo.play.net.TCLogReport;
import com.tencent.liteav.demo.play.utils.TCNetWatcher;
import com.tencent.liteav.demo.play.view.TCDanmuView;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by liyuejiao on 2018/7/3.
 * by AV
 */

public class SuperPlayerView extends RelativeLayout implements ITXVodPlayListener, ITXLivePlayListener {
    private static final String TAG = "SuperPlayerView";
    private Context mContext;

    private int mPlayMode = SuperPlayerConst.PLAYMODE_WINDOW;
    private boolean mLockScreen = false;

    // UI
    private ViewGroup mRootView;
    private TXCloudVideoView mTXCloudVideoView;
    private TCVodControllerLarge mVodControllerLarge;
    private TCVodControllerSmall mVodControllerSmall;

    private ViewGroup.LayoutParams mLayoutParamWindowMode;
    private ViewGroup.LayoutParams mLayoutParamFullScreenMode;
    private LayoutParams mVodControllerSmallParams;
    private LayoutParams mVodControllerLargeParams;
    // 点播播放器
    private TXVodPlayer mVodPlayer;
    private TXVodPlayConfig mVodPlayConfig;
    private OnSuperPlayerViewCallback mPlayerViewCallback;
    private int mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING;
    private boolean mDefaultSet;
    private long mReportLiveStartTime = -1;
    private long mReportVodStartTime = -1;
    private int mCurrentPlayType;
    private TCNetWatcher mWatcher;
    private boolean mIsMultiBitrateStream;
    private boolean mIsPlayWithFileid;
    private String mCurrentPlayVideoURL;
    //    private SuperPlayerModelWrapper mCurrentModelWrapper;
    private boolean mChangeHWAcceleration;
    private int mSeekPos;
    private float mCurrentTimeWhenPause; // 记录onPause暂停时的时间，在播放widevine格式的时候，onResume需要Seek回去，否则需要等到下一个I帧到来才能有画面。
    private String headTitle;
    private boolean mAutoPlay = true;
    //播放回调
    private OnPlayEvent onPlayEvent;

    private TCDanmuView mDanmuView;                     // 弹幕

    public SuperPlayerView(Context context) {
        super(context);
        initView(context);

    }

    public SuperPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SuperPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        mContext = context;
        mRootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.super_vod_view, null);
        mTXCloudVideoView = (TXCloudVideoView) mRootView.findViewById(R.id.cloud_video_view);
        mVodControllerLarge = (TCVodControllerLarge) mRootView.findViewById(R.id.controller_large);
        mVodControllerSmall = (TCVodControllerSmall) mRootView.findViewById(R.id.controller_small);
        mDanmuView = mRootView.findViewById(R.id.danmaku_view);
        mVodControllerSmallParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mVodControllerLargeParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mVodControllerLarge.setVodController(mVodController);
        mVodControllerSmall.setVodController(mVodController);
        removeAllViews();
        mRootView.removeView(mDanmuView);
        mRootView.removeView(mTXCloudVideoView);
        mRootView.removeView(mVodControllerSmall);
        mRootView.removeView(mVodControllerLarge);
        mCurrentPlayType = SuperPlayerConst.PLAYTYPE_VOD;
        addView(mTXCloudVideoView);
        initVodPlayer(context);
        mVodPlayer.setPlayerView(mTXCloudVideoView);
        if (mPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
            addView(mVodControllerLarge);
            mVodControllerLarge.hide();
        } else if (mPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
            addView(mVodControllerSmall);
            mVodControllerSmall.hide();
        }
        addView(mDanmuView);
        mVodControllerSmall.updatePlayType(SuperPlayerConst.PLAYTYPE_VOD);
        mVodControllerLarge.updatePlayType(SuperPlayerConst.PLAYTYPE_VOD);
        mVodControllerSmall.updateVideoProgress(0, 0, 0);
        mVodControllerLarge.updateVideoProgress(0, 0, 0);
        post(new Runnable() {
            @Override
            public void run() {
                if (mPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
                    mLayoutParamWindowMode = getLayoutParams();
                }
                try {
                    // 依据上层Parent的LayoutParam类型来实例化一个新的fullscreen模式下的LayoutParam
                    Class parentLayoutParamClazz = getLayoutParams().getClass();
                    Constructor constructor = parentLayoutParamClazz.getDeclaredConstructor(int.class, int.class);
                    mLayoutParamFullScreenMode = (ViewGroup.LayoutParams) constructor.newInstance(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        changeLight();
    }

    public void changeLight() {
        if (mContext instanceof Activity) {
            Activity activity = (Activity) mContext;
            WindowManager.LayoutParams attr = activity.getWindow().getAttributes();
            attr.screenBrightness = -1;
            activity.getWindow().setAttributes(attr);
        }
    }

    /**
     * 初始化点播播放器
     *
     * @param context
     */
    private void initVodPlayer(Context context) {
        if (mVodPlayer != null)
            return;
        mVodPlayer = new TXVodPlayer(context);
        SuperPlayerGlobalConfig config = SuperPlayerGlobalConfig.getInstance();
        mVodPlayConfig = new TXVodPlayConfig();
        mVodPlayConfig.setCacheFolderPath(Environment.getExternalStorageDirectory().getPath() + "/txcache");
        mVodPlayConfig.setMaxCacheItems(config.maxCacheItem);
        mVodPlayer.setConfig(mVodPlayConfig);
        mVodPlayer.setRenderMode(config.renderMode);
        mVodPlayer.setVodListener(this);
        mVodPlayer.enableHardwareDecode(config.enableHWAcceleration);
    }


    /**
     * 播放点播
     */
    private void playVodURL(String url, boolean autoPlay) {
        mCurrentPlayVideoURL = url;
        TXCLog.i(TAG, "playVodURL videoURL:" + url);

        if (url.contains(".m3u8")) {
            mIsMultiBitrateStream = true;
        }
        if (mVodPlayer != null) {
            mDefaultSet = false;
            mVodPlayer.setAutoPlay(autoPlay);
            mVodPlayer.setVodListener(this);
            int ret = mVodPlayer.startPlay(url);
            if (ret == 0) {
                mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING;
                if (mDanmuView != null && mDanmuView.isPrepared() && mDanmuView.isPaused()) {
                    mDanmuView.resume();
                }
                TXCLog.e(TAG, "playVodURL mCurrentPlayState:" + mCurrentPlayState);
            }
        }
        mIsPlayWithFileid = false;
    }


    public void onResume() {
        if (mDanmuView != null && mDanmuView.isPrepared() && mDanmuView.isPaused()) {
            mDanmuView.resume();
        }
        resume();
    }

    private void resume() {
        if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
            if (mVodPlayer != null) {
                mVodPlayer.resume();
            }
        }
    }

    public void onPause() {
        if (mDanmuView != null && mDanmuView.isPrepared()) {
            mDanmuView.pause();
        }
        pause();
    }

    public float getmCurrentTimeWhenPause() {
        return mCurrentTimeWhenPause;
    }

    private void pause() {
        if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
            if (mVodPlayer != null) {
                // 解决Widevine有声音画面不动问题
//                if (mCurrentModelWrapper != null && mCurrentModelWrapper.currentPlayingType == SuperPlayerModelWrapper.URL_DASH_WIDE_VINE) {
//                    mCurrentTimeWhenPause = mVodPlayer.getCurrentPlaybackTime();
//                }
                mCurrentTimeWhenPause = mVodPlayer.getCurrentPlaybackTime();
                mVodPlayer.pause();
            }
        }
    }

    public void resetPlayer() {
        if (mDanmuView != null) {
            mDanmuView.release();
            mDanmuView = null;
        }
        stopPlay();
    }


    public interface OnPlayEvent {
        void onReadyPlay();

        void onFirstFrame();

        void onPlayerPause();

        void onFinish();
    }

    public void setOnPlayEvent(OnPlayEvent onPlayStart) {
        this.onPlayEvent = onPlayStart;
    }

    private void stopPlay() {
        if (mVodPlayer != null) {
            mVodPlayer.setVodListener(null);
            mVodPlayer.stopPlay(false);
        }
        if (mWatcher != null) {
            mWatcher.stop();
        }
        mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PAUSE;
        TXCLog.e(TAG, "stopPlay mCurrentPlayState:" + mCurrentPlayState);
        reportPlayTime();
    }

    private void reportPlayTime() {
        if (mReportLiveStartTime != -1) {
            long reportEndTime = System.currentTimeMillis();
            long diff = (reportEndTime - mReportLiveStartTime) / 1000;
            TCLogReport.getInstance().uploadLogs(TCPlayerConstants.ELK_ACTION_LIVE_TIME, diff, 0);
            mReportLiveStartTime = -1;
        }
        if (mReportVodStartTime != -1) {
            long reportEndTime = System.currentTimeMillis();
            long diff = (reportEndTime - mReportVodStartTime) / 1000;
            TCLogReport.getInstance().uploadLogs(TCPlayerConstants.ELK_ACTION_VOD_TIME, diff, mIsPlayWithFileid ? 1 : 0);
            mReportVodStartTime = -1;
        }
    }

    /**
     * 设置超级播放器的回掉
     *
     * @param callback
     */
    public void setPlayerViewCallback(OnSuperPlayerViewCallback callback) {
        mPlayerViewCallback = callback;
    }


    private void fullScreen(boolean isFull) {//控制是否全屏显示
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            if (isFull) {
                //隐藏虚拟按键，并且全屏
                ImmersionBar.with(activity).fitsSystemWindows(false).init();
                View decorView = activity.getWindow().getDecorView();
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                    decorView.setSystemUiVisibility(View.GONE);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            } else {
                View decorView = activity.getWindow().getDecorView();
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                    decorView.setSystemUiVisibility(View.VISIBLE);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }

        }
    }

    /**
     * 播放器控制
     */
    private TCVodControllerBase.VodController mVodController = new TCVodControllerBase.VodController() {
        /**
         * 请求播放模式：窗口/全屏/悬浮窗
         * @param requestPlayMode
         */
        @Override
        public void onRequestPlayMode(int requestPlayMode) {
            if (mPlayMode == requestPlayMode)
                return;

            if (mLockScreen) //锁屏
                return;

            if (requestPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {

                fullScreen(true);
            } else {
                fullScreen(false);
            }
            mVodControllerSmall.hide();
            mVodControllerLarge.hide();
            //请求全屏模式
            if (requestPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                TXCLog.i(TAG, "requestPlayMode FullScreen");
                if (mLayoutParamFullScreenMode == null)
                    return;
                removeView(mVodControllerSmall);
                addView(mVodControllerLarge, mVodControllerLargeParams);
                setLayoutParams(mLayoutParamFullScreenMode);
                rotateScreenOrientation(SuperPlayerConst.ORIENTATION_LANDSCAPE);
                if (mPlayerViewCallback != null) {
                    mPlayerViewCallback.onStartFullScreenPlay();
                }
                //added by av
                if (!TextUtils.isEmpty(headTitle))
                    mVodControllerLarge.updateTitle(headTitle);

            }
            // 请求窗口模式
            else if (requestPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
                TXCLog.i(TAG, "requestPlayMode Window");
                if (mPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                    if (mLayoutParamWindowMode == null)
                        return;

                    removeView(mVodControllerLarge);
                    addView(mVodControllerSmall, mVodControllerSmallParams);
                    setLayoutParams(mLayoutParamWindowMode);
                    rotateScreenOrientation(SuperPlayerConst.ORIENTATION_PORTRAIT);

                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback.onStopFullScreenPlay();
                    }
                }
            }
            mPlayMode = requestPlayMode;
        }

        /**
         * 返回
         * @param playMode
         */
        @Override
        public void onBackPress(int playMode) {
            // 当前是全屏模式，返回切换成窗口模式
            if (playMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                onRequestPlayMode(SuperPlayerConst.PLAYMODE_WINDOW);
            }
            // 当前是窗口模式，返回退出播放器
            else if (playMode == SuperPlayerConst.PLAYMODE_WINDOW) {
                if (mPlayerViewCallback != null) {
                    mPlayerViewCallback.onClickSmallReturnBtn();
                }
//                if (mCurrentPlayState == SuperPlayerConst.PLAYSTATE_PLAYING) {
//                    onRequestPlayMode(SuperPlayerConst.PLAYMODE_FLOAT);
//                }
            }
        }

        @Override
        public void resume() {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.resume();
                }
            }
            mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING;
            mVodControllerSmall.updatePlayState(true);
            mVodControllerLarge.updatePlayState(true);

            mVodControllerLarge.updateReplay(false);
            mVodControllerSmall.updateReplay(false);
        }

        @Override
        public void pause() {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.pause();
                }
            } else {
                if (mWatcher != null) {
                    mWatcher.stop();
                }
            }
            mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PAUSE;
            if (onPlayEvent != null) {
                onPlayEvent.onPlayerPause();
            }
            TXCLog.e("lyj", "pause mCurrentPlayState:" + mCurrentPlayState);
            mVodControllerSmall.updatePlayState(false);
            mVodControllerLarge.updatePlayState(false);
        }

        @Override
        public float getDuration() {
            return mVodPlayer.getDuration();
        }

        @Override
        public float getCurrentPlaybackTime() {
            return mVodPlayer.getCurrentPlaybackTime();
        }

        @Override
        public void seekTo(int position) {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.seek(position);
                }
            }

        }

        @Override
        public boolean isPlaying() {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                return mVodPlayer.isPlaying();
            } else {
                return mCurrentPlayState == SuperPlayerConst.PLAYSTATE_PLAYING;
            }
        }

        /**
         * 切换弹幕开关
         * @param on
         */
        @Override
        public void onDanmuku(boolean on) {
            if (mDanmuView != null) {
                mDanmuView.toggle(on);
            }
        }

        /**
         * 截屏
         */
        @Override
        public void onSnapshot() {

        }

        /**
         * 清晰度选择
         * @param quality
         */
        @Override
        public void onQualitySelect(TCVideoQuality quality) {
        }

        /**
         * 速度改变
         * @param speedLevel
         */
        @Override
        public void onSpeedChange(float speedLevel) {
            if (mVodPlayer != null) {
                mVodPlayer.setRate(speedLevel);
            }

            //速度改变上报
            TCLogReport.getInstance().uploadLogs(TCPlayerConstants.ELK_ACTION_CHANGE_SPEED, 0, 0);
        }

        /**
         * 是否镜像
         * @param isMirror
         */
        @Override
        public void onMirrorChange(boolean isMirror) {
            if (mVodPlayer != null) {
                mVodPlayer.setMirror(isMirror);
            }

            if (isMirror) {
                //镜像上报
                TCLogReport.getInstance().uploadLogs(TCPlayerConstants.ELK_ACTION_MIRROR, 0, 0);
            }
        }

        /**
         * 是否启用硬件加速
         * @param isAccelerate
         */
        @Override
        public void onHWAcceleration(boolean isAccelerate) {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                mChangeHWAcceleration = true;
                if (mVodPlayer != null) {
                    mVodPlayer.enableHardwareDecode(isAccelerate);

                    mSeekPos = (int) mVodPlayer.getCurrentPlaybackTime();
                    TXCLog.i(TAG, "save pos:" + mSeekPos);

                    stopPlay();
                    playVodURL(mCurrentPlayVideoURL, true);
                }
            } else {
            }
            // 硬件加速上报
            if (isAccelerate) {
                TCLogReport.getInstance().uploadLogs(TCPlayerConstants.ELK_ACTION_HW_DECODE, 0, 0);
            } else {
                TCLogReport.getInstance().uploadLogs(TCPlayerConstants.ELK_ACTION_SOFT_DECODE, 0, 0);
            }
        }

        /**
         * 悬浮窗位置更新
         * @param x
         * @param y
         */
        @Override
        public void onFloatUpdate(int x, int y) {
        }

        /**
         * 重新播放
         */
        @Override
        public void onReplay() {
            if (!TextUtils.isEmpty(mCurrentPlayVideoURL)) {
                playVodURL(mCurrentPlayVideoURL, true);
            }

//            if (mVodControllerLarge != null) {
//                mVodControllerLarge.updateReplay(false);
//            }
//            if (mVodControllerSmall != null) {
//                mVodControllerSmall.updateReplay(false);
//            }
        }

        @Override
        public void resumeLive() {
            mVodControllerSmall.updatePlayType(SuperPlayerConst.PLAYTYPE_LIVE);
            mVodControllerLarge.updatePlayType(SuperPlayerConst.PLAYTYPE_LIVE);
        }

    };

    /**
     * 旋转屏幕方向
     *
     * @param orientation
     */
    @SuppressLint("SourceLockedOrientationActivity")
    private void rotateScreenOrientation(int orientation) {
        switch (orientation) {
            case SuperPlayerConst.ORIENTATION_LANDSCAPE:
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case SuperPlayerConst.ORIENTATION_PORTRAIT:
                ((Activity) mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
        }
    }

    /**
     * 点播播放器回调
     *
     * @param player
     * @param event  事件id.id类型请参考 {@linkplain TXLiveConstants#PLAY_EVT_CONNECT_SUCC 播放事件列表}.
     * @param param
     */
    @Override
    public void onPlayEvent(TXVodPlayer player, int event, Bundle param) {
        if (event != TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            String playEventLog = "TXVodPlayer onPlayEvent event: " + event + ", " + param.getString(TXLiveConstants.EVT_DESCRIPTION);
            TXCLog.d(TAG, playEventLog);
        }
        if (event == TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED) { //视频播放开始
            mVodControllerSmall.dismissBackground();
            mVodControllerSmall.updateLiveLoadingState(false);
            mVodControllerLarge.updateLiveLoadingState(false);
            mVodControllerSmall.updatePlayState(true);
            mVodControllerLarge.updatePlayState(true);
            mVodControllerSmall.updateReplay(false);
            mVodControllerLarge.updateReplay(false);
            if (onPlayEvent != null) {
                onPlayEvent.onReadyPlay();
            }

        } else if (event == TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME) {
            if (onPlayEvent != null) {
                onPlayEvent.onFirstFrame();
            }
            if (mChangeHWAcceleration) { //切换软硬解码器后，重新seek位置
                TXCLog.i(TAG, "seek pos:" + mSeekPos);
                mVodController.seekTo(mSeekPos);
                mChangeHWAcceleration = false;
            }
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_END) {
            if (onPlayEvent != null) {
                onPlayEvent.onFinish();
            }
            mCurrentPlayState = SuperPlayerConst.PLAYSTATE_END;
            mVodControllerSmall.updatePlayState(false);
            mVodControllerLarge.updatePlayState(false);
            mVodControllerSmall.updateReplay(true);
            mVodControllerLarge.updateReplay(true);
        } else if (event == TXLiveConstants.PLAY_EVT_PLAY_PROGRESS) {
            int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS);
            int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS);
            int playable = param.getInt(TXLiveConstants.EVT_PLAYABLE_DURATION_MS);
            mVodControllerSmall.updateVideoProgress(progress / 1000, playable / 1000, duration / 1000);
            mVodControllerLarge.updateVideoProgress(progress / 1000, playable / 1000, duration / 1000);
        } else if (event == TXLiveConstants.PLAY_ERR_HLS_KEY
                || event == TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {// 播放点播文件失败
            Toast.makeText(getContext(), param.getString(TXLiveConstants.EVT_DESCRIPTION) + ",尝试其他链接播放", Toast.LENGTH_SHORT).show();
            mVodPlayer.stopPlay(true);
            mVodControllerSmall.updatePlayState(false);
            mVodControllerLarge.updatePlayState(false);
        }
        if (event < 0
//                && event != TXLiveConstants.PLAY_ERR_VOD_LOAD_LICENSE_FAIL
                && event != TXLiveConstants.PLAY_ERR_HLS_KEY
//                && event != TXLiveConstants.PLAY_ERR_VOD_UNSUPPORT_DRM
                && event != TXLiveConstants.PLAY_ERR_NET_DISCONNECT) {
            mVodPlayer.stopPlay(true);
            mVodControllerSmall.updatePlayState(false);
            mVodControllerLarge.updatePlayState(false);
            Toast.makeText(mContext, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {

    }


    @Override
    public void onPlayEvent(int i, Bundle bundle) {

    }

    @Override
    public void onNetStatus(Bundle status) {

    }

    public void requestPlayMode(int playMode) {
        if (playMode == SuperPlayerConst.PLAYMODE_WINDOW) {
            if (mVodController != null) {
                mVodController.onRequestPlayMode(SuperPlayerConst.PLAYMODE_WINDOW);
            }
        } else if (playMode == SuperPlayerConst.PLAYMODE_FLOAT) {
            if (mPlayerViewCallback != null) {
                mPlayerViewCallback.onStartFloatWindowPlay();
            }
            if (mVodController != null) {
                mVodController.onRequestPlayMode(SuperPlayerConst.PLAYMODE_FLOAT);
            }
        }
    }

    private final int OP_SYSTEM_ALERT_WINDOW = 24;

    /**
     * API <18，默认有悬浮窗权限，不需要处理。无法接收无法接收触摸和按键事件，不需要权限和无法接受触摸事件的源码分析
     * API >= 19 ，可以接收触摸和按键事件
     * API >=23，需要在manifest中申请权限，并在每次需要用到权限的时候检查是否已有该权限，因为用户随时可以取消掉。
     * API >25，TYPE_TOAST 已经被谷歌制裁了，会出现自动消失的情况
     */
    private boolean checkOp(Context context, int op) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Method method = AppOpsManager.class.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
        }
        return true;
    }

    /**
     * 获取当前播放模式
     *
     * @return
     */
    public int getPlayMode() {
        return mPlayMode;
    }

    /**
     * 获取当前播放状态
     *
     * @return
     */
    public int getPlayState() {
        return mCurrentPlayState;
    }

    public void setmCurrentPlayState() {
        mVodControllerSmall.updateVideoProgress(0, 0, 0);
        mVodControllerLarge.updateVideoProgress(0, 0, 0);
        mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING;
        mVodControllerSmall.updatePlayState(true);
        mVodControllerLarge.updatePlayState(true);

        mVodControllerLarge.updateReplay(false);
        mVodControllerSmall.updateReplay(false);
    }

    public void setBackVisibility(int visibility) {
        mVodControllerSmall.setBackVisibility(visibility);
    }

    /**
     * 控制操作页面不显示
     *
     * @param b
     */
    public void setShowController(boolean b) {
        mVodControllerSmall.setShowController(false);
        mVodControllerLarge.setShowController(false);
    }

    /**
     * SuperPlayerView的回调接口
     */
    public interface OnSuperPlayerViewCallback {

        /**
         * 开始全屏播放
         */
        void onStartFullScreenPlay();

        /**
         * 结束全屏播放
         */
        void onStopFullScreenPlay();

        /**
         * 点击悬浮窗模式下的x按钮
         */
        void onClickFloatCloseBtn();

        /**
         * 点击小播放模式的返回按钮
         */
        void onClickSmallReturnBtn();

        /**
         * 开始悬浮窗播放
         */
        void onStartFloatWindowPlay();
    }

    public void release() {
        if (mVodControllerSmall != null) {
            mVodControllerSmall.release();
        }
        if (mVodControllerLarge != null) {
            mVodControllerLarge.release();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            release();
        } catch (Exception | Error ignored) {
        }
    }


    public void play(String url, String title) {
        headTitle = title;
        playVodURL(url, true);
    }

    public void preplay(String url, String title) {
        headTitle = title;
        playVodURL(url, false);
    }


    public boolean isPlaying() {
        return mVodPlayer.isPlaying();
    }

    public void hideFullScreen()
    {
        mVodControllerSmall.hideFullScreen();
    }
}
