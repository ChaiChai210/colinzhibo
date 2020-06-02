package com.huaxin.video.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseFragment;
import com.example.librarybase.base.BindEventBus;
import com.example.librarybase.base.Event;
import com.huaxin.library.custom.DYLoadingView;
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
import com.tencent.liteav.demo.play.SuperPlayerModel;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

@BindEventBus
public class TiktokFragment1 extends BaseFragment implements VodPlayerView.ActionListener {
    @BindView(R2.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R2.id.loading_view)
    DYLoadingView loadingView;

    private List<TiktokEntity> mData = new ArrayList<>();
    private TiktokAdapter mTiktokAdapter;
    private int mPage = 1;

    private int currentPosition;
    private VodPlayerView dyVideoPlayer;
    private boolean hasMore = true;

    private LinearLayoutManager mLayoutManager;

    private boolean isFirstLoad = true;

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoad) {
            isFirstLoad = false;
            playVideos(currentPosition);
        }
        if (dyVideoPlayer != null) {
            dyVideoPlayer.resumePlay();
        }
        ((Activity) mContext).getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (dyVideoPlayer != null) {
            dyVideoPlayer.pausePlay();
        }
        ((Activity) mContext).getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    @Override
    protected void loadData() {
        getData();
    }


    private void getData() {
        HashMap params = new HashMap<>();
        params.put("page", mPage);
        params.put("page_size", 10);
        params.put("mobile_code", AppUtils.getDeviceId(mContext));
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
        initRecyclerView();
        initListener();
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

    private void initRecyclerView() {

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper() {
            // 在 Adapter的 onBindViewHolder 之后执行
            @Override
            public int findTargetSnapPosition(RecyclerView.LayoutManager layoutManager, int velocityX, int velocityY) {
                int targetPos = super.findTargetSnapPosition(layoutManager, velocityX, velocityY);
                Log.e("chai", targetPos + "");
                if (targetPos >= mTiktokAdapter.getItemCount() - 2 && hasMore) {
                    mPage++;
                    getData();
                }
                View view = mLayoutManager.findViewByPosition(currentPosition);
                if (view != null) {
                    ImageView logo = view.findViewById(R.id.iv_logo);
                    VodPlayerView dyVideoPlayer = view.findViewById(R.id.dy_video_player);
                    dyVideoPlayer.stopPlay();
                    if (logo != null && logo.getVisibility() == View.GONE) {
                        logo.setVisibility(View.VISIBLE);
                    }
                }
                currentPosition = targetPos;
                mRecyclerView.post(() -> {
                    playVideos(targetPos);
                });
                return targetPos;
            }

            @Nullable
            @Override
            public View findSnapView(RecyclerView.LayoutManager layoutManager) {

                return super.findSnapView(layoutManager);
            }
        };
        pagerSnapHelper.attachToRecyclerView(mRecyclerView);
        mLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mTiktokAdapter = new TiktokAdapter(mData);
        mRecyclerView.setAdapter(mTiktokAdapter);
        mRecyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

    }

    @Override
    protected int getContentViewId() {
        return R.layout.video_frg_tiktok;
    }

    @Override
    public void onPlayBegin() {
        loadingView.stop();
        loadingView.setVisibility(View.GONE);
    }

    @Override
    public void onPlayLoading() {
        loadingView.setVisibility(View.VISIBLE);
        loadingView.start();
    }

    @Override
    public void onFirstFrame() {
        View itemView = mLayoutManager.findViewByPosition(currentPosition);
        if (itemView == null) return;
        ImageView logo = itemView.findViewById(R.id.iv_logo);
        if (logo != null) {
            if (logo.getVisibility() == View.VISIBLE) {
                logo.setVisibility(View.GONE);
            }
        }
    }


}
