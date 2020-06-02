package com.tencent.liteav.demo.play;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tencent.liteav.basic.log.TXCLog;
import com.tencent.liteav.demo.play.bean.TCResolutionName;
import com.tencent.liteav.demo.play.bean.TCVideoQuality;
import com.tencent.liteav.demo.play.controller.IControllerCallback;
import com.tencent.liteav.demo.play.controller.TCControllerFloat;
import com.tencent.liteav.demo.play.controller.TCControllerFullScreen;
import com.tencent.liteav.demo.play.controller.TCControllerWindow;
import com.tencent.liteav.demo.play.net.TCLogReport;
import com.tencent.liteav.demo.play.protocol.IPlayInfoProtocol;
import com.tencent.liteav.demo.play.protocol.IPlayInfoRequestCallback;
import com.tencent.liteav.demo.play.protocol.TCPlayInfoParams;
import com.tencent.liteav.demo.play.protocol.TCPlayInfoProtocolV2;
import com.tencent.liteav.demo.play.protocol.TCPlayInfoProtocolV4;
import com.tencent.liteav.demo.play.utils.TCImageUtil;
import com.tencent.liteav.demo.play.utils.TCNetWatcher;
import com.tencent.liteav.demo.play.utils.TCUrlUtil;
import com.tencent.liteav.demo.play.utils.TCVideoQualityUtil;
import com.tencent.liteav.demo.play.view.TCDanmuView;
import com.tencent.rtmp.ITXVodPlayListener;
import com.tencent.rtmp.TXBitrateItem;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.TXVodPlayConfig;
import com.tencent.rtmp.TXVodPlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by liyuejiao on 2018/7/3.
 * <p>
 * 超级播放器view
 * <p>
 * 具备播放器基本功能，此外还包括横竖屏切换、悬浮窗播放、画质切换、硬件加速、倍速播放、镜像播放、手势控制等功能，同时支持直播与点播
 * <p>
 * 使用方式极为简单，只需要在布局文件中引入并获取到该控件，通过{@link #playWithModel(SuperPlayerModel)}传入{@link SuperPlayerModel}即可实现视频播放
 * <p>
 * 1、播放视频{@link #playWithModel(SuperPlayerModel)}
 * <p>
 * 2、设置回调{@link #setPlayerViewCallback(OnSuperPlayerViewCallback)}
 * <p>
 * 3、点播相关：初始化播放器{@link #initVodPlayer(Context)}，播放事件监听{@link #onPlayEvent(TXVodPlayer, int, Bundle)}，
 * 网络事件监听{@link #onNetStatus(TXVodPlayer, Bundle)}
 * <p>
 * <p>
 * 5、controller回调实现{@link #mControllerCallback}
 * <p>
 * 5、退出播放释放内存{@link #resetPlayer()}
 */

public class SuperVodPlayerView extends RelativeLayout implements ITXVodPlayListener {
    private static final String TAG = "SuperPlayerView";

    public boolean isPlaying() {
        return mVodPlayer.isPlaying();
    }

    private enum PLAYER_TYPE {
        PLAYER_TYPE_NULL,
        PLAYER_TYPE_VOD,
    }

    private Context mContext;
    // UI
    private ViewGroup mRootView;                      // SuperPlayerView的根view
    private TXCloudVideoView mTXCloudVideoView;              // 腾讯云视频播放view
    private TCControllerFullScreen mControllerFullScreen;          // 全屏模式控制view
    private TCControllerWindow mControllerWindow;              // 窗口模式控制view
    private TCControllerFloat mControllerFloat;               // 悬浮窗模式控制view
    private TCDanmuView mDanmuView;                     // 弹幕
    private ViewGroup.LayoutParams mLayoutParamWindowMode;         // 窗口播放时SuperPlayerView的布局参数
    private ViewGroup.LayoutParams mLayoutParamFullScreenMode;     // 全屏播放时SuperPlayerView的布局参数
    private LayoutParams mVodControllerWindowParams;     // 窗口controller的布局参数
    private LayoutParams mVodControllerFullScreenParams; // 全屏controller的布局参数

    private WindowManager mWindowManager;                 // 悬浮窗窗口管理器
    private WindowManager.LayoutParams mWindowParams;                  // 悬浮窗布局参数

    private SuperPlayerModel mCurrentModel;                  // 当前播放的model
    private IPlayInfoProtocol mCurrentProtocol;               // 当前视频信息协议类

    private TXVodPlayer mVodPlayer;                     // 点播播放器
    private TXVodPlayConfig mVodPlayConfig;                 // 点播播放器配置

    private OnSuperPlayerViewCallback mPlayerViewCallback;            // SuperPlayerView回调
    private TCNetWatcher mWatcher;                       // 网络质量监视器
    private String mCurrentPlayVideoURL;           // 当前播放的url
    private int mCurrentPlayType;               // 当前播放类型
    private int mCurrentPlayMode = SuperPlayerConst.PLAYMODE_WINDOW;    // 当前播放模式
    private int mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING; // 当前播放状态
    private boolean mIsMultiBitrateStream;          // 是否是多码流url播放
    private boolean mIsPlayWithFileId;              // 是否是腾讯云fileId播放
    private long mReportLiveStartTime = -1;      // 直播开始时间，用于上报使用时长
    private long mReportVodStartTime = -1;       // 点播开始时间，用于上报使用时长
    private boolean mDefaultQualitySet;             // 标记播放多码流url时是否设置过默认画质
    private boolean mLockScreen = false;            // 是否锁定屏幕
    private boolean mChangeHWAcceleration;          // 切换硬解后接收到第一个关键帧前的标记位
    private int mSeekPos;                       // 记录切换硬解时的播放时间
    private PLAYER_TYPE mCurPlayType = PLAYER_TYPE.PLAYER_TYPE_NULL;    //当前播放类型

    private final int OP_SYSTEM_ALERT_WINDOW = 24;    // 支持TYPE_TOAST悬浮窗的最高API版本

    public SuperVodPlayerView(Context context) {
        super(context);
        initView(context);
    }

    public SuperVodPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SuperVodPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        mRootView = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.super_vod_player_view, null);
        mTXCloudVideoView = mRootView.findViewById(R.id.cloud_video_view);
        mControllerFullScreen = mRootView.findViewById(R.id.controller_large);
        mControllerWindow = mRootView.findViewById(R.id.controller_small);
        mControllerFloat = mRootView.findViewById(R.id.controller_float);
        mDanmuView = mRootView.findViewById(R.id.danmaku_view);

        mVodControllerWindowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mVodControllerFullScreenParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        mControllerFullScreen.setCallback(mControllerCallback);
        mControllerWindow.setCallback(mControllerCallback);
        mControllerFloat.setCallback(mControllerCallback);

        removeAllViews();
        mRootView.removeView(mDanmuView);
        mRootView.removeView(mTXCloudVideoView);
        mRootView.removeView(mControllerWindow);
        mRootView.removeView(mControllerFullScreen);
        mRootView.removeView(mControllerFloat);

        addView(mTXCloudVideoView);
        initVodPlayer(context);
        mVodPlayer.setPlayerView(mTXCloudVideoView);
        if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
            addView(mControllerFullScreen);
            mControllerFullScreen.hide();
        } else if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
            addView(mControllerWindow);
            mControllerWindow.hide();
        }
        addView(mDanmuView);
        updatePlayType( SuperPlayerConst.PLAYTYPE_VOD);
        post(new Runnable() {
            @Override
            public void run() {
                if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
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
        TCLogReport.getInstance().setAppName(context);
        TCLogReport.getInstance().setPackageName(context);
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

//        File sdcardDir = context.getExternalFilesDir(null);
//        if (sdcardDir != null) {
//            mVodPlayConfig.setCacheFolderPath(sdcardDir.getPath() + "/txcache");
//        }
        mVodPlayConfig.setCacheFolderPath(Environment.getExternalStorageDirectory().getPath() + "/txcache");
        mVodPlayConfig.setMaxCacheItems(config.maxCacheItem);
        mVodPlayer.setConfig(mVodPlayConfig);
        mVodPlayer.setRenderMode(config.renderMode);
        mVodPlayer.setVodListener(this);
        mVodPlayer.enableHardwareDecode(config.enableHWAcceleration);
    }

    //    public void play(String url) {
//        playVodURL(url);
//
//    }
    private String headTitle;

    public void play(String url, String title) {
        headTitle = title;
//        updateVideoProgress(0, 0);
        playVodURL(url);
    }



    /**
     * 播放FileId视频
     *
     * @param protocol
     */
    private void playModeVideo(IPlayInfoProtocol protocol) {
        playVodURL(protocol.getUrl());
        List<TCVideoQuality> videoQualityArrayList = protocol.getVideoQualityList();
        if (videoQualityArrayList != null) {
            mControllerFullScreen.setVideoQualityList(videoQualityArrayList);
            mIsMultiBitrateStream = false;
        } else {
            mIsMultiBitrateStream = true;
        }
        TCVideoQuality defaultVideoQuality = protocol.getDefaultVideoQuality();
        if (defaultVideoQuality != null)
            mControllerFullScreen.updateVideoQuality(defaultVideoQuality);
    }


    /**
     * 播放点播url
     */
    private void playVodURL(String url) {
        if (url == null || "".equals(url)) return;
        mCurrentPlayVideoURL = url;
        if (url.contains(".m3u8")) {
            mIsMultiBitrateStream = true;
        }
        if (mVodPlayer != null) {
            mDefaultQualitySet = false;
            mVodPlayer.setStartTime(0);
            mVodPlayer.setAutoPlay(true);
            mVodPlayer.setVodListener(this);
            if (mCurrentProtocol != null && mCurrentProtocol.getToken() != null) {
                TXCLog.d(TAG, "TOKEN: " + mCurrentProtocol.getToken());
                mVodPlayer.setToken(mCurrentProtocol.getToken());
            } else {
                mVodPlayer.setToken(null);
            }
            int ret = mVodPlayer.startPlay(url);
            if (ret == 0) {
                mCurrentPlayState = SuperPlayerConst.PLAYSTATE_PLAYING;
                mCurPlayType = PLAYER_TYPE.PLAYER_TYPE_VOD;
                if (mDanmuView != null && mDanmuView.isPrepared() && mDanmuView.isPaused()) {
                    mDanmuView.resume();
                }
                TXCLog.e(TAG, "playVodURL mCurrentPlayState:" + mCurrentPlayState);
            }
        }
        mIsPlayWithFileId = false;
    }


    /**
     * 更新标题
     *
     * @param title 视频名称
     */
    private void updateTitle(String title) {
//        mControllerWindow.updateTitle(title);
        mControllerFullScreen.updateTitle(title);
    }

    /**
     * 更新播放进度
     *
     * @param current  当前播放进度(秒)
     * @param duration 总时长(秒)
     */
    private void updateVideoProgress(long current, long duration) {
        mControllerWindow.updateVideoProgress(current, duration);
        mControllerFullScreen.updateVideoProgress(current, duration);
    }

    /**
     * 更新播放类型
     *
     * @param playType
     */
    private void updatePlayType(int playType) {
        mCurrentPlayType = playType;
        mControllerWindow.updatePlayType(playType);
        mControllerFullScreen.updatePlayType(playType);
    }

    /**
     * 更新播放状态
     *
     * @param playState
     */
    private void updatePlayState(int playState) {
        mCurrentPlayState = playState;
        mControllerWindow.updatePlayState(playState);
        mControllerFullScreen.updatePlayState(playState);
    }

    /**
     * resume生命周期回调
     */
    public void onResume() {
        if (mDanmuView != null && mDanmuView.isPrepared() && mDanmuView.isPaused()) {
            mDanmuView.resume();
        }
        resume();
    }

    private void resume() {
        if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD && mVodPlayer != null) {
            mVodPlayer.resume();
        }
    }

    /**
     * pause生命周期回调
     */
    public void onPause() {
        if (mDanmuView != null && mDanmuView.isPrepared()) {
            mDanmuView.pause();
        }
        pause();
    }


    private void pause() {
        if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD && mVodPlayer != null) {
            mVodPlayer.pause();
        }
    }

    /**
     * 重置播放器
     */
    public void resetPlayer() {
        if (mDanmuView != null) {
            mDanmuView.release();
            mDanmuView = null;
        }
        stopPlay();
    }

    /**
     * 停止播放
     */
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

    /**
     * 上报播放时长
     */
    private void reportPlayTime() {
        if (mReportLiveStartTime != -1) {
            long reportEndTime = System.currentTimeMillis();
            long diff = (reportEndTime - mReportLiveStartTime) / 1000;
            TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_LIVE_TIME, diff, 0);
            mReportLiveStartTime = -1;
        }
        if (mReportVodStartTime != -1) {
            long reportEndTime = System.currentTimeMillis();
            long diff = (reportEndTime - mReportVodStartTime) / 1000;
            TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_VOD_TIME, diff, mIsPlayWithFileId ? 1 : 0);
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

    /**
     * 控制是否全屏显示
     */
    private void fullScreen(boolean isFull) {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            if (isFull) {
                //隐藏虚拟按键，并且全屏
                View decorView = activity.getWindow().getDecorView();
                if (decorView == null) return;
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                    decorView.setSystemUiVisibility(View.GONE);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
                    decorView.setSystemUiVisibility(uiOptions);
                }
            } else {
                View decorView = activity.getWindow().getDecorView();
                if (decorView == null) return;
                if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
                    decorView.setSystemUiVisibility(View.VISIBLE);
                } else if (Build.VERSION.SDK_INT >= 19) {
                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
        }
    }

    /**
     * 初始化controller回调
     */
    private IControllerCallback mControllerCallback = new IControllerCallback() {
        @Override
        public void onSwitchPlayMode(int requestPlayMode) {
            if (mCurrentPlayMode == requestPlayMode) return;
            if (mLockScreen) return;
            if (requestPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                fullScreen(true);
            } else {
                fullScreen(false);
            }
            mControllerFullScreen.hide();
            mControllerWindow.hide();
            mControllerFloat.hide();
            //请求全屏模式
            if (requestPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                if (mLayoutParamFullScreenMode == null)
                    return;
                removeView(mControllerWindow);
                addView(mControllerFullScreen, mVodControllerFullScreenParams);
                setLayoutParams(mLayoutParamFullScreenMode);
                rotateScreenOrientation(SuperPlayerConst.ORIENTATION_LANDSCAPE);
                if (mPlayerViewCallback != null) {
                    mPlayerViewCallback.onStartFullScreenPlay();
                }
                if (!TextUtils.isEmpty(headTitle))
                    updateTitle(headTitle);
            }
            // 请求窗口模式
            else if (requestPlayMode == SuperPlayerConst.PLAYMODE_WINDOW) {
                // 当前是悬浮窗
                if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_FLOAT) {
                    try {
                        Context viewContext = SuperVodPlayerView.this.getContext();
                        Intent intent = null;
                        if (viewContext instanceof Activity) {
                            intent = new Intent(SuperVodPlayerView.this.getContext(), viewContext.getClass());
                        } else {
                            Toast.makeText(viewContext, "悬浮播放失败", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        mContext.startActivity(intent);
                        pause();
                        if (mLayoutParamWindowMode == null)
                            return;
                        mWindowManager.removeView(mControllerFloat);

                        if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                            mVodPlayer.setPlayerView(mTXCloudVideoView);
                        }
                        resume();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 当前是全屏模式
                else if (mCurrentPlayMode == SuperPlayerConst.PLAYMODE_FULLSCREEN) {
                    if (mLayoutParamWindowMode == null)
                        return;
                    removeView(mControllerFullScreen);
                    addView(mControllerWindow, mVodControllerWindowParams);
                    setLayoutParams(mLayoutParamWindowMode);
                    rotateScreenOrientation(SuperPlayerConst.ORIENTATION_PORTRAIT);
                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback.onStopFullScreenPlay();
                    }
                }
            }
            //请求悬浮窗模式
            else if (requestPlayMode == SuperPlayerConst.PLAYMODE_FLOAT) {
                TXCLog.i(TAG, "requestPlayMode Float :" + Build.MANUFACTURER);
                SuperPlayerGlobalConfig prefs = SuperPlayerGlobalConfig.getInstance();
                if (!prefs.enableFloatWindow) {
                    return;
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 6.0动态申请悬浮窗权限
                    if (!Settings.canDrawOverlays(mContext)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                        intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                        mContext.startActivity(intent);
                        return;
                    }
                } else {
                    if (!checkOp(mContext, OP_SYSTEM_ALERT_WINDOW)) {
                        Toast.makeText(mContext, "进入设置页面失败,请手动开启悬浮窗权限", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                pause();

                mWindowManager = (WindowManager) mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                mWindowParams = new WindowManager.LayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mWindowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
                } else {
                    mWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                mWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mWindowParams.format = PixelFormat.TRANSLUCENT;
                mWindowParams.gravity = Gravity.LEFT | Gravity.TOP;

                SuperPlayerGlobalConfig.TXRect rect = prefs.floatViewRect;
                mWindowParams.x = rect.x;
                mWindowParams.y = rect.y;
                mWindowParams.width = rect.width;
                mWindowParams.height = rect.height;
                try {
                    mWindowManager.addView(mControllerFloat, mWindowParams);
                } catch (Exception e) {
                    Toast.makeText(SuperVodPlayerView.this.getContext(), "悬浮播放失败", Toast.LENGTH_SHORT).show();
                    return;
                }

                TXCloudVideoView videoView = mControllerFloat.getFloatVideoView();
                if (videoView != null) {
                    mVodPlayer.setPlayerView(videoView);
                    resume();
                }
                // 悬浮窗上报
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_FLOATMOE, 0, 0);
            }
            mCurrentPlayMode = requestPlayMode;
        }

        @Override
        public void onBackPressed(int playMode) {
            switch (playMode) {
                case SuperPlayerConst.PLAYMODE_FULLSCREEN:// 当前是全屏模式，返回切换成窗口模式
                    onSwitchPlayMode(SuperPlayerConst.PLAYMODE_WINDOW);
                    break;
                case SuperPlayerConst.PLAYMODE_WINDOW:// 当前是窗口模式，返回退出播放器
                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback.onClickSmallReturnBtn();
                    }
                    if (mCurrentPlayState == SuperPlayerConst.PLAYSTATE_PLAYING) {
                        onSwitchPlayMode(SuperPlayerConst.PLAYMODE_FLOAT);
                    }
                    break;
                case SuperPlayerConst.PLAYMODE_FLOAT:// 当前是悬浮窗，退出
                    mWindowManager.removeView(mControllerFloat);
                    if (mPlayerViewCallback != null) {
                        mPlayerViewCallback.onClickFloatCloseBtn();
                    }
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onFloatPositionChange(int x, int y) {
            mWindowParams.x = x;
            mWindowParams.y = y;
            mWindowManager.updateViewLayout(mControllerFloat, mWindowParams);
        }

        @Override
        public void onPause() {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.pause();
                }
            }
            updatePlayState(SuperPlayerConst.PLAYSTATE_PAUSE);
        }

        @Override
        public void onResume() {
            if (mCurrentPlayState == SuperPlayerConst.PLAYSTATE_END) { //重播
                playVodURL(mCurrentPlayVideoURL);
            } else if (mCurrentPlayState == SuperPlayerConst.PLAYSTATE_PAUSE) { //继续播放
                if (mVodPlayer != null) {
                    mVodPlayer.resume();
                }
            }
            updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
        }

        @Override
        public void onSeekTo(int position) {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.seek(position);
                }
            }
        }

        @Override
        public void onResumeLive() {

            updatePlayType(SuperPlayerConst.PLAYTYPE_LIVE);
        }

        @Override
        public void onDanmuToggle(boolean isOpen) {
            if (mDanmuView != null) {
                mDanmuView.toggle(isOpen);
            }
        }

        @Override
        public void onSnapshot() {
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    mVodPlayer.snapshot(new TXLivePlayer.ITXSnapshotListener() {
                        @Override
                        public void onSnapshot(Bitmap bmp) {
                            showSnapshotWindow(bmp);
                        }
                    });
                }
            }
        }

        @Override
        public void onQualityChange(TCVideoQuality quality) {
            mControllerFullScreen.updateVideoQuality(quality);
            if (mCurrentPlayType == SuperPlayerConst.PLAYTYPE_VOD) {
                if (mVodPlayer != null) {
                    if (quality.url != null) { // br!=0;index=-1;url!=null   //br=0;index!=-1;url!=null
                        // 说明是非多bitrate的m3u8子流，需要手动seek
                        float currentTime = mVodPlayer.getCurrentPlaybackTime();
                        mVodPlayer.stopPlay(true);
                        TXCLog.i(TAG, "onQualitySelect quality.url:" + quality.url);
                        mVodPlayer.setStartTime(currentTime);
                        mVodPlayer.startPlay(quality.url);
                    } else { //br!=0;index!=-1;url=null
                        TXCLog.i(TAG, "setBitrateIndex quality.index:" + quality.index);
                        // 说明是多bitrate的m3u8子流，会自动无缝seek
                        mVodPlayer.setBitrateIndex(quality.index);
                    }
                }
            }
            //清晰度上报
            TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_CHANGE_RESOLUTION, 0, 0);
        }

        @Override
        public void onSpeedChange(float speedLevel) {
            if (mVodPlayer != null) {
                mVodPlayer.setRate(speedLevel);
            }
            //速度改变上报
            TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_CHANGE_SPEED, 0, 0);
        }

        @Override
        public void onMirrorToggle(boolean isMirror) {
            if (mVodPlayer != null) {
                mVodPlayer.setMirror(isMirror);
            }
            if (isMirror) {
                //镜像上报
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_MIRROR, 0, 0);
            }
        }

        @Override
        public void onHWAccelerationToggle(boolean isAccelerate) {
            mChangeHWAcceleration = true;
            if (mVodPlayer != null) {
                mVodPlayer.enableHardwareDecode(isAccelerate);
                mSeekPos = (int) mVodPlayer.getCurrentPlaybackTime();
                TXCLog.i(TAG, "save pos:" + mSeekPos);
                stopPlay();
                playModeVideo(mCurrentProtocol);
            }
            // 硬件加速上报
            if (isAccelerate) {
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_HW_DECODE, 0, 0);
            } else {
                TCLogReport.getInstance().uploadLogs(TCLogReport.ELK_ACTION_SOFT_DECODE, 0, 0);
            }
        }
    };


    /**
     * 显示截图窗口
     *
     * @param bmp
     */
    private void showSnapshotWindow(final Bitmap bmp) {
        if (bmp == null) return;
        final PopupWindow popupWindow = new PopupWindow(mContext);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_new_vod_snap, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_snap);
        imageView.setImageBitmap(bmp);
        popupWindow.setContentView(view);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(mRootView, Gravity.TOP, 1800, 300);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TCImageUtil.save2MediaStore(mContext, bmp);
            }
        });
        this.postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();
            }
        }, 3000);
    }

    /**
     * 旋转屏幕方向
     *
     * @param orientation
     */
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
     * 具体可参考官网文档：https://cloud.tencent.com/document/product/881/20216
     *
     * @param player
     * @param event  事件id.id类型请参考 {@linkplain TXLiveConstants#PLAY_EVT_CONNECT_SUCC 播放事件列表}.
     * @param param
     */
    /**
     * 点播播放器回调
     * <p>
     * 具体可参考官网文档：https://cloud.tencent.com/document/product/881/20216
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
        switch (event) {
            case TXLiveConstants.PLAY_EVT_VOD_PLAY_PREPARED://视频播放开始
//                mControllerWindow.hideBackground();
                updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
                if (mIsMultiBitrateStream) {
                    List<TXBitrateItem> bitrateItems = mVodPlayer.getSupportedBitrates();
                    if (bitrateItems == null || bitrateItems.size() == 0)
                        return;
                    Collections.sort(bitrateItems); //masterPlaylist多清晰度，按照码率排序，从低到高
                    List<TCVideoQuality> videoQualities = new ArrayList<>();
                    int size = bitrateItems.size();

                    List<TCResolutionName> resolutionNames = (mCurrentProtocol != null) ? mCurrentProtocol.getResolutionNameList() : null;
                    for (int i = 0; i < size; i++) {
                        TXBitrateItem bitrateItem = bitrateItems.get(i);
                        TCVideoQuality quality;
                        if (resolutionNames != null) {
                            quality = TCVideoQualityUtil.convertToVideoQuality(bitrateItem, mCurrentProtocol.getResolutionNameList());
                        } else {
                            quality = TCVideoQualityUtil.convertToVideoQuality(bitrateItem, i);
                        }
                        videoQualities.add(quality);
                    }
                    if (!mDefaultQualitySet) {
                        mVodPlayer.setBitrateIndex(bitrateItems.get(bitrateItems.size() - 1).index); //默认播放码率最高的
                        mControllerFullScreen.updateVideoQuality(videoQualities.get(videoQualities.size() - 1));
                        mDefaultQualitySet = true;
                    }
                    mControllerFullScreen.setVideoQualityList(videoQualities);
                }
                break;
            case TXLiveConstants.PLAY_EVT_RCV_FIRST_I_FRAME:
                if (mChangeHWAcceleration) { //切换软硬解码器后，重新seek位置
                    TXCLog.i(TAG, "seek pos:" + mSeekPos);
                    mControllerCallback.onSeekTo(mSeekPos);
                    mChangeHWAcceleration = false;
                }
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_END:
                updatePlayState(SuperPlayerConst.PLAYSTATE_END);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_PROGRESS:
                int progress = param.getInt(TXLiveConstants.EVT_PLAY_PROGRESS_MS);
                int duration = param.getInt(TXLiveConstants.EVT_PLAY_DURATION_MS);
                updateVideoProgress(progress / 1000, duration / 1000);
                break;
            case TXLiveConstants.PLAY_EVT_PLAY_BEGIN: {
                updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
                break;
            }
            case TXLiveConstants.PLAY_EVT_PLAY_LOADING:
                updatePlayState(SuperPlayerConst.PLAYSTATE_LOADING);
                break;
            case TXLiveConstants.PLAY_EVT_VOD_LOADING_END:
                updatePlayState(SuperPlayerConst.PLAYSTATE_PLAYING);
                break;
            default:
                break;
        }
        if (event < 0) {// 播放点播文件失败
            mVodPlayer.stopPlay(true);
            updatePlayState(SuperPlayerConst.PLAYSTATE_PAUSE);
            Toast.makeText(mContext, param.getString(TXLiveConstants.EVT_DESCRIPTION), Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public void onNetStatus(TXVodPlayer player, Bundle status) {

    }

    /**
     * 切换播放模式
     *
     * @param playMode
     */
    public void requestPlayMode(int playMode) {
        if (playMode == SuperPlayerConst.PLAYMODE_WINDOW) {
            if (mControllerCallback != null) {
                mControllerCallback.onSwitchPlayMode(SuperPlayerConst.PLAYMODE_WINDOW);
            }
        } else if (playMode == SuperPlayerConst.PLAYMODE_FLOAT) {
            if (mPlayerViewCallback != null) {
                mPlayerViewCallback.onStartFloatWindowPlay();
            }
            if (mControllerCallback != null) {
                mControllerCallback.onSwitchPlayMode(SuperPlayerConst.PLAYMODE_FLOAT);
            }
        }
    }

    /**
     * 检查悬浮窗权限
     * <p>
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
                TXCLog.e(TAG, Log.getStackTraceString(e));
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
        return mCurrentPlayMode;
    }

    /**
     * 获取当前播放状态
     *
     * @return
     */
    public int getPlayState() {
        return mCurrentPlayState;
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
        if (mControllerWindow != null) {
            mControllerWindow.release();
        }
        if (mControllerFullScreen != null) {
            mControllerFullScreen.release();
        }
        if (mControllerFloat != null) {
            mControllerFloat.release();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        try {
            release();
        } catch (Exception e) {
            TXCLog.e(TAG, Log.getStackTraceString(e));
        } catch (Error e) {
            TXCLog.e(TAG, Log.getStackTraceString(e));
        }
    }
}
