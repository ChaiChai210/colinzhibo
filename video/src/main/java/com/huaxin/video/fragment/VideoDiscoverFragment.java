package com.huaxin.video.fragment;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BindEventBus;
import com.example.librarybase.base.Event;
import com.huaxin.library.R2;
import com.huaxin.library.base.AbVideoFragment;
import com.huaxin.library.db.Download;
import com.huaxin.library.db.DownloadDao;
import com.huaxin.library.db.MyDatabase;
import com.huaxin.library.db.WatchRecord;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.AntiShakeUtils;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.library.utils.okdownload.DownloadManager;
import com.huaxin.video.R;
import com.huaxin.video.adapter.VideoDiscoverAdapter;
import com.huaxin.video.entity.VideoDiscoverRsp;
import com.liulishuo.okdownload.DownloadTask;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.tencent.qcloud.tim.uikit.utils.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

@BindEventBus
public class VideoDiscoverFragment extends AbVideoFragment {
    //    @BindView(R2.id.rv_content)
//    RecyclerView rvContent;
    @BindView(R2.id.srl_common)
    SmartRefreshLayout srlCommon;

    private static int PAGE_SIZE = 10;
    private int last_page = 1;
    private int pageNum = 1;

    private VideoDiscoverAdapter videoDiscoverAdapter;
    private List<VideoDiscoverRsp.DataBean> mData = new ArrayList<>();

    private DownloadManager mDownloadManager;
    private DownloadTask task;
    private DownloadDao downloadDao;

    @Override
    protected void loadData() {
        super.loadData();
        mDownloadManager = DownloadManager.getInstance();
        setSave(true);
        getData();
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        downloadDao = MyDatabase.getInstance(mContext).downloadDao();
        srlCommon.setOnRefreshListener(refreshLayout -> {
            pageNum = 1;
            getData();
            refreshLayout.finishRefresh();
        });
        srlCommon.setOnLoadMoreListener(refreshLayout -> {
            if (pageNum < last_page) {
                refreshLayout.finishLoadMore();
                pageNum++;
                getData();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
                ToastUtils.showShort("没有更多数据");
            }
        });

        videoDiscoverAdapter = new VideoDiscoverAdapter(mData);
        videoDiscoverAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.iv_like) {
                //收藏或者取消收藏
                if (AntiShakeUtils.isInvalidClick(view1))
                    return;
                doLike(position);
            } else if (view1.getId() == R.id.ibn_play) {
                if (AntiShakeUtils.isInvalidClick(view1))
                    return;
                playVideo(mData.get(position).getVideo_href(), mData.get(position).getTitle(), position);
            } else if (view1.getId() == R.id.iv_download) {
                if (AntiShakeUtils.isInvalidClick(view1))
                    return;
                doDownload(position);
            }
        });
        rvContent.setAdapter(videoDiscoverAdapter);
    }

    private void doDownload(int position) {
        VideoDiscoverRsp.DataBean dataBean = mData.get(position);
        String url = dataBean.getVideo_href();
        url = url.replace("m3u8", "mp4");
        if (downloadDao.getWatchCount(url) == 1) {
            ToastUtil.toastShortMessage("已经添加到下载列表");
        } else {
            ToastUtil.toastShortMessage("添加到下载列表,到我的下载查看");
            mDownloadManager.addTask(url);
            mDownloadManager.start(url);
            Download item = new Download();
            item.setId(dataBean.getId());
            item.setUrl(url);
            item.setIntro(dataBean.getIntro());
            item.setThumb(dataBean.getThumb());
            item.setTitle(dataBean.getTitle());
            downloadDao.insert(item);
        }


    }

    @Override
    protected void publishPlayCount() {
        HashMap params = new HashMap<>();
        params.put("id", mData.get(mCurrentPosition).getId());
        HttpUtils.postData(UrlConstants.PLAY_STATISTICS, params, new JsonCallback<BaseEntity>() {
            @Override
            protected void onSuccess(BaseEntity data) {
                mData.get(mCurrentPosition).setPlay_count(mData.get(mCurrentPosition).getPlay_count() + 1);
                videoDiscoverAdapter.notifyItemChanged(mCurrentPosition);
            }
        });
    }

    @Override
    protected WatchRecord getWatchRecord(int mCurrentPosition, float duration) {
        WatchRecord user = new WatchRecord();
        VideoDiscoverRsp.DataBean videoDetailEntity = mData.get(mCurrentPosition);
        user.setId(videoDetailEntity.getId());
        user.setName(videoDetailEntity.getTitle());
        user.setIntro(videoDetailEntity.getIntro());
        user.setThumb(videoDetailEntity.getThumb());
        user.setVideo_href(videoDetailEntity.getVideo_href());
        user.setPlayDuration(duration);
        user.setDuration(videoDetailEntity.getDuration());
        user.setTimestamp(System.currentTimeMillis());
        return user;
    }


    private void doLike(int position) {
        if (mData.get(position).isIs_collection()) {
            HttpUtils.postDisLike(mData.get(position).getId(), AppUtils.getDeviceId(mContext), 1, 1,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            ToastUtils.showShort("取消收藏成功");
                            mData.get(position).setIs_collection(false);
                            videoDiscoverAdapter.notifyItemChanged(position);
                        }
                    });

        } else {
            HttpUtils.postLike(mData.get(position).getId(), AppUtils.getDeviceId(mContext), 1, 1,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            ToastUtils.showShort("收藏成功");
                            mData.get(position).setIs_collection(true);
                            videoDiscoverAdapter.notifyItemChanged(position);
                        }
                    });
        }
    }


    private void getData() {
        HashMap params = new HashMap<>();
        params.put("page_size", PAGE_SIZE);
        params.put("page", pageNum);
        params.put("mobile_code", AppUtils.getDeviceId(mContext));
        HttpUtils.postData(UrlConstants.GET_VIDEO_FIND, params, new JsonCallback<BaseEntity<VideoDiscoverRsp>>() {
            @Override
            public void onSuccess(BaseEntity<VideoDiscoverRsp> data) {
                last_page = data.getData().getLast_page();
                setView(data.getData().getData());
            }
        });
    }

    private void setView(List<VideoDiscoverRsp.DataBean> data) {
//        mData = data;
        if (pageNum == 1) {
            videoDiscoverAdapter.replaceData(data);
        } else {
            videoDiscoverAdapter.addData(data);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Event<Integer> event) {
        if (event.getCode() == EventCode.REFRESH_VIDEO) {
            pageNum = 1;
            getData();
        }
    }


}
