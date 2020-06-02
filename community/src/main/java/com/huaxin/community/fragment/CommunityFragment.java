package com.huaxin.community.fragment;

import android.os.Bundle;
import android.view.View;

import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BindEventBus;
import com.example.librarybase.base.Event;
import com.huaxin.community.R;
import com.huaxin.library.R2;
import com.huaxin.library.adapter.CommunityAdapter;
import com.huaxin.library.base.AbVideoFragment;
import com.huaxin.library.db.WatchRecord;
import com.huaxin.library.entity.CommunityRecommend;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.EventCode;
import com.huaxin.library.utils.UrlConstants;
import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

@BindEventBus
public class CommunityFragment extends AbVideoFragment implements CommunityAdapter.CollectListener {
    public static final String TYPE = "type";

    @BindView(R2.id.srl_common)
    SmartRefreshLayout srlCommon;
    private int last_page = 1;
    private int pageNum = 1;
    //2是图片，3是视频，1是文字,0,推荐
    private int type = 0;
    private CommunityAdapter communityRecommentAdapter;
    private List<CommunityRecommend.DataBean> mData = new ArrayList<>();
    private int likeClickPos;


    public static CommunityFragment newInstance(int type) {
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        CommunityFragment fragment = new CommunityFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void loadData() {
        super.loadData();
        type = getArguments().getInt(TYPE);
    }

    private void getData(int type) {
        HashMap params = new HashMap<>();
        params.put("type", type);
        params.put("page", pageNum);
        params.put("mobile_code", AppUtils.getDeviceId(mContext));
        HttpUtils.postData(UrlConstants.GET_COMMNUNITY_RECOMMEND, params, new JsonCallback<BaseEntity<CommunityRecommend>>() {
            @Override
            public void onSuccess(BaseEntity<CommunityRecommend> data) {
                last_page = data.getData().getLast_page();
                setView(data.getData().getData());
            }

//            @Override
//            public void onStart(Request<BaseEntity<CommunityRecommend>, ? extends Request> request) {
//                super.onStart(request);
//                mBaseLoadService.showCallback(LoadingCallback.class);
//            }
        });
    }

    private void setView(List<CommunityRecommend.DataBean> data) {
//        mData = data;
        if (pageNum == 1) {
            communityRecommentAdapter.replaceData(data);
        } else {
            communityRecommentAdapter.addData(data);
        }
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        super.initView(view, savedInstanceState);
        srlCommon.setOnRefreshListener(refreshLayout -> {
            pageNum = 1;
            getData(type);
            refreshLayout.finishRefresh();
        });
        srlCommon.setOnLoadMoreListener(refreshLayout -> {
            Logger.e("page", pageNum);
            refreshLayout.setDisableContentWhenLoading(true);
            if (pageNum < last_page) {
                refreshLayout.finishLoadMore();
                pageNum++;
                getData(type);
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
                ToastUtils.showShort("没有更多数据");
            }
        });
        communityRecommentAdapter = new CommunityAdapter(mData);
        communityRecommentAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.con_video) {
                playVideo(mData.get(position).getContent(),mData.get(position).getTitle(), position);
            }
        });
        communityRecommentAdapter.setCollectListener(this);
        rvContent.setAdapter(communityRecommentAdapter);
    }

    @Override
    protected void publishPlayCount() {

    }

    @Override
    protected WatchRecord getWatchRecord(int mCurrentPosition, float duration) {
        WatchRecord user = new WatchRecord();
        CommunityRecommend.DataBean videoDetailEntity = mData.get(mCurrentPosition);
        user.setId(videoDetailEntity.getId());
        user.setName(videoDetailEntity.getTitle());
        user.setIntro(videoDetailEntity.getTitle());
        user.setThumb(videoDetailEntity.getCover());
        user.setVideo_href(videoDetailEntity.getContent());
        user.setPlayDuration(duration);
        user.setDuration(videoDetailEntity.getDuration());
        user.setTimestamp(System.currentTimeMillis());
        return user;
    }


    @Override
    public void onclick(int id, int type) {
        if (type == 1) {
            AppUtils.copyToClipboard(mContext, "后台接口配置");
            ToastUtils.showShort("已复制到粘贴板");
        }
    }

    private void doLikeSuccess(int likeClickPos) {
        mData.get(likeClickPos).setIs_like(true);
        mData.get(likeClickPos).setLikes_count(mData.get(likeClickPos).getLikes_count() + 1);
        communityRecommentAdapter.notifyItemChanged(likeClickPos);
    }

    private void doDislikeSuccess(int likeClickPos) {
        mData.get(likeClickPos).setIs_like(false);
        mData.get(likeClickPos).setLikes_count(mData.get(likeClickPos).getLikes_count() - 1);
        communityRecommentAdapter.notifyItemChanged(likeClickPos);
    }




    @Override
    public void onResume() {
        super.onResume();
        getData(type);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleEvent(Event<Integer> event) {
        // do something
        if (event.getCode() == EventCode.LIKE) {
            int pos = event.getData();
            doLikeSuccess(pos);
        } else if (event.getCode() == EventCode.DISLIKE) {
            int pos = event.getData();
            doDislikeSuccess(pos);
        }

    }
}
