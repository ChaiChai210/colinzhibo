package com.huaxin.usercenter.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.R2;
import com.huaxin.library.adapter.CommunityAdapter;
import com.huaxin.library.custom.SuperPlayerView;
import com.huaxin.library.entity.CommunityRecommend;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

@Route(path = ARConstants.PATH_MY_POST)
public class MyPostActivity extends BaseActivity implements CommunityAdapter.CollectListener, SuperPlayerView.OnSuperPlayerViewCallback {
    @BindView(R2.id.rv_content)
    RecyclerView rvContent;
    @BindView(R2.id.srl_common)
    SmartRefreshLayout srlCommon;
    private int last_page = 1;
    private int pageNum = 1;
    //2是图片，3是视频，1是文字,0,推荐
    private int type = 0;
    private CommunityAdapter communityRecommentAdapter;
    private List<CommunityRecommend.DataBean> mData = new ArrayList<>();
    private int likeClickPos;

    private SuperPlayerView mSuperPlayerView;
    private FrameLayout mCurrentFrame;
    private int mCurrentPosition = -1;
    private LinearLayoutManager manager;

    @Override
    protected void loadData() {
        getData();
    }

    private void getData() {
        HashMap params = new HashMap<>();
        params.put("uid", AppUtils.getUid());
        HttpUtils.postData(UrlConstants.MY_POST, params, new JsonCallback<BaseEntity<CommunityRecommend>>() {
            @Override
            public void onSuccess(BaseEntity<CommunityRecommend> data) {
                last_page = data.getData().getLast_page();
                setView(data.getData().getData());
            }
        });
    }

    private void setView(List<CommunityRecommend.DataBean> data) {
        if (pageNum == 1) {
            communityRecommentAdapter.replaceData(data);
        } else {
            communityRecommentAdapter.addData(data);
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.fragment_community_recommend;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        setTitle("我的帖子");

        mSuperPlayerView = new SuperPlayerView(this);
//        mSuperPlayerView.setBackVisibility(View.GONE);
        mSuperPlayerView.setPlayerViewCallback(this);

        srlCommon.setOnRefreshListener(refreshLayout -> {
            pageNum = 1;
            getData();
            refreshLayout.finishRefresh();
        });
        srlCommon.setOnLoadMoreListener(refreshLayout -> {
            Logger.e("page", pageNum);
            if (pageNum < last_page) {
                refreshLayout.finishLoadMore();
                pageNum++;
                getData();
            } else {
                refreshLayout.finishLoadMoreWithNoMoreData();
                ToastUtils.showShort("没有更多数据");
            }
        });
        manager = new LinearLayoutManager(this);
        rvContent.setLayoutManager(manager);
        communityRecommentAdapter = new CommunityAdapter(mData);
        communityRecommentAdapter.setOnItemChildClickListener((adapter, view1, position) -> {
            if (view1.getId() == R.id.con_video) {
                mCurrentPosition = position;
                ViewGroup viewGroup = (ViewGroup) manager.findViewByPosition(position);
                removeParentView();
                mCurrentFrame = viewGroup.findViewById(R.id.fl_video);
                mCurrentFrame.addView(mSuperPlayerView, 0);
                mSuperPlayerView.play(mData.get(position).getContent(),mData.get(position).getTitle());
            }
        });
        communityRecommentAdapter.setCollectListener(this);
        rvContent.setAdapter(communityRecommentAdapter);
    }

    private void removeParentView() {
        ViewGroup parent = (ViewGroup) mSuperPlayerView.getParent();
        if (parent != null) {
            parent.removeView(mSuperPlayerView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    public void onclick(int id, int type) {
        if (type == 1) {
            AppUtils.copyToClipboard(this, "后台接口配置");
            ToastUtils.showShort("已复制到粘贴板");
        } else if (2 == type) {
            likeClickPos = id;
            doLike();
        }
    }


    private void doLike() {
        if (mData.get(likeClickPos).isIs_like()) {
            HttpUtils.postDisLike(mData.get(likeClickPos).getId(), AppUtils.getDeviceId(this), 2, 2,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            mData.get(likeClickPos).setIs_like(false);
                            mData.get(likeClickPos).setLikes_count(mData.get(likeClickPos).getLikes_count() - 1);
                            communityRecommentAdapter.notifyItemChanged(likeClickPos);
                        }
                    });

        } else {
            HttpUtils.postLike(mData.get(likeClickPos).getId(), AppUtils.getDeviceId(this), 2, 2,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            mData.get(likeClickPos).setIs_like(true);
                            mData.get(likeClickPos).setLikes_count(mData.get(likeClickPos).getLikes_count() + 1);
                            communityRecommentAdapter.notifyItemChanged(likeClickPos);
                        }
                    });
        }
    }


    @Override
    public void onStartFullScreenPlay() {
        removeParentView();
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.
                FLAG_KEEP_SCREEN_ON);
        viewGroup.addView(mSuperPlayerView);
    }

    @Override
    public void onStopFullScreenPlay() {
        removeParentView();
        if (mCurrentFrame != null) {
            mCurrentFrame.addView(mSuperPlayerView, 0);
        }
        Window windowBack = getWindow();
        //设置当前窗体为全屏显示
        int flagBack = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
        windowBack.clearFlags(flagBack);
        ImmersionBar.with(this).fitsSystemWindows(true).init();
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

}
