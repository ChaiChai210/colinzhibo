package com.huaxin.video.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseFragment;
import com.example.librarybase.base.BindEventBus;
import com.example.librarybase.base.Event;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.video.R;
import com.huaxin.video.R2;
import com.huaxin.video.adapter.TiktokAdapter;
import com.huaxin.video.custom.VodPlayerView;
import com.huaxin.video.entity.TiktokEntity;
import com.huaxin.library.custom.DYLoadingView;
import com.huaxin.video.views.layoutmanager.VerticalViewPager;
import com.tencent.liteav.demo.play.SuperPlayerModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

@BindEventBus
public class TiktokFragment3 extends BaseFragment implements VodPlayerView.ActionListener {
    @BindView(R2.id.vp2)
    ViewPager2 mViewPager;
    @BindView(R2.id.loading_view)
    DYLoadingView loadingView;

    private List<TiktokEntity> mData = new ArrayList<>();
    private TiktokAdapter mTiktokAdapter;
    private int mPage = 1;
    /**
     * 当前播放位置
     */
    private int mCurPos;
    private RecyclerView mViewPagerImpl;

    private int currentPosition;
    private VodPlayerView dyVideoPlayer;
    private boolean hasMore = true;

    private LinearLayoutManager mLayoutManager;

    private boolean isFirstLoad = true;

    @Override
    public void onResume() {
        super.onResume();
//        if (isFirstLoad) {
//            isFirstLoad = false;
//            playVideos(currentPosition);
//        }
        if (dyVideoPlayer != null) {
            dyVideoPlayer.resumePlay();
        }
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


    @Override
    public void onPause() {
        super.onPause();
        Log.e("chia", "onPause");
        if (dyVideoPlayer != null) {
            dyVideoPlayer.pausePlay();
//            insert(currentPosition, dyVideoPlayer.getmCurrentTimeWhenPause());
        }
        ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected void loadData() {
//        watchRecordRepository = new WatchRecordRepository(mContext);
        getData();
    }


    private void getData() {
        HashMap params = new HashMap<>();
        params.put("page", mPage);
        params.put("page_size", 10);
        HttpUtils.postData(UrlConstants.VIDEO_TIKTOK, params, new JsonCallback<BaseEntity<List<TiktokEntity>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<TiktokEntity>> data) {
                if (data.getData().size() < 10) {
                    hasMore = false;
                }
                if (1 == mPage) {
                    mTiktokAdapter.replaceData(data.getData());
                } else {
                    mTiktokAdapter.addData(data.getData());
                }
            }

        });
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        initViewPager();
        initListener();
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(4);
        mTiktokAdapter = new TiktokAdapter(mData);
        mViewPager.setAdapter(mTiktokAdapter);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            private int mCurItem;

            /**
             * VerticalViewPager是否反向滑动
             */
            private boolean mIsReverseScroll;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                if (position == mCurItem) {
                    return;
                }
                mIsReverseScroll = position < mCurItem;
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == mCurPos) return;
                mViewPager.post(() -> startPlay(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                if (state == VerticalViewPager.SCROLL_STATE_DRAGGING) {
                    mCurItem = mViewPager.getCurrentItem();
                }
//                if (state == ViewPager2.SCROLL_STATE_IDLE) {
//                    mPreloadManager.resumePreload(mCurPos, mIsReverseScroll);
//                } else {
//                    mPreloadManager.pausePreload(mCurPos, mIsReverseScroll);
//                }
            }
        });
        //ViewPage2内部是通过RecyclerView去实现的，它位于ViewPager2的第0个位置
        mViewPagerImpl = (RecyclerView) mViewPager.getChildAt(0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Event<Integer> event) {

        if (event.getCode() == EventCode.COLLECT_VIDEO) {
            int position = event.getData();
            HttpUtils.postLike(mData.get(position).getId(), AppUtils.getDeviceId(mContext), 1, 1,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            mData.get(position).setIs_collection(true);
                            mData.get(position).setCollection_count(mData.get(position).getCollection_count() + 1);
                            mTiktokAdapter.notifyItemChanged(position);
                        }
                    });
        } else if (event.getCode() == EventCode.DISCOLLECT_VIDEO) {
            int position = event.getData();
            HttpUtils.postDisLike(mData.get(position).getId(), AppUtils.getDeviceId(mContext), 1, 1,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            mData.get(position).setIs_collection(false);
                            mData.get(position).setCollection_count(mData.get(position).getCollection_count() - 1);
                            mTiktokAdapter.notifyItemChanged(position);
                        }
                    });
        }

    }

    private void initListener() {
        mTiktokAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.tv_collection_count) {

            } else if (view1.getId() == R.id.tv_comment) {
                ToastUtils.showShort("ddd");
            }
        });
    }

    public void playVideos(int position) {
        View itemView = mLayoutManager.findViewByPosition(position);
        if (itemView == null) return;
        dyVideoPlayer = itemView.findViewById(R.id.dy_video_player);

        //视频播放
        if (dyVideoPlayer != null) {
            SuperPlayerModel model = new SuperPlayerModel();
            model.url = mData.get(position).getVideo_href();
            dyVideoPlayer.startPlay(model.url);
//            dyVideoPlayer.playWithUrl(UrlConstants.BASE_URL + mData.get(position).getVideo_href());
            dyVideoPlayer.setActionListener(this);

        }
    }

    private void startPlay(int position) {
        View itemView = mViewPagerImpl.getLayoutManager().findViewByPosition(position);
        if (itemView == null) return;
        dyVideoPlayer = itemView.findViewById(R.id.dy_video_player);

        //视频播放
        if (dyVideoPlayer != null) {
            SuperPlayerModel model = new SuperPlayerModel();
            model.url = mData.get(position).getVideo_href();
            dyVideoPlayer.startPlay(model.url);
//            dyVideoPlayer.playWithUrl(UrlConstants.BASE_URL + mData.get(position).getVideo_href());
            dyVideoPlayer.setActionListener(this);

        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.video_frg_tiktok_vp2;
    }

    @Override
    public void onPlayBegin() {
//        loadingView.stop();
//        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onPlayLoading() {
//        loadingView.setVisibility(View.VISIBLE);
//        loadingView.start();
    }

    @Override
    public void onFirstFrame() {
//        View itemView = mLayoutManager.findViewByPosition(currentPosition);
//        if (itemView == null) return;
//        ImageView logo = itemView.findViewById(R.id.iv_logo);
//        if (logo != null) {
//            if (logo.getVisibility() == View.VISIBLE) {
//                logo.setVisibility(View.GONE);
//            }
//        }
    }

//    @Override
//    public void onChildViewAttachedToWindow(@NonNull View view) {
//        Log.e("chia","onChildViewAttachedToWindow");
//        playVideos(currentPosition);
//
//    }
//
//    @Override
//    public void onChildViewDetachedFromWindow(@NonNull View view) {
//
//    }

}
