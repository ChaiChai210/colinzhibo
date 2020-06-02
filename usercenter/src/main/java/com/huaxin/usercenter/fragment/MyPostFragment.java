package com.huaxin.usercenter.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.ToastUtils;
import com.example.librarybase.base.BaseActivity;
import com.gyf.immersionbar.ImmersionBar;
import com.huaxin.library.R2;
import com.huaxin.library.adapter.CommunityAdapter;
import com.example.librarybase.base.BaseFragment;
import com.huaxin.library.custom.SuperPlayerView;
import com.huaxin.library.entity.CommunityRecommend;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.AppUtils;
import com.huaxin.library.utils.UrlConstants;
import com.huaxin.usercenter.R;
import com.orhanobut.logger.Logger;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class MyPostFragment extends BaseFragment implements CommunityAdapter.CollectListener, SuperPlayerView.OnSuperPlayerViewCallback {
    public static final String TYPE = "type";

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
        mData = data;
        if (pageNum == 1) {
            communityRecommentAdapter.replaceData(mData);
        } else {
            communityRecommentAdapter.addData(mData);
        }
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        mSuperPlayerView = new SuperPlayerView(getContext());
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
        manager = new LinearLayoutManager(mContext);
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
    protected int getContentViewId() {
        return R.layout.fragment_community_recommend;
    }

    @Override
    public void onclick(int id, int type) {
        if (type == 1) {
            AppUtils.copyToClipboard(mContext, "后台接口配置");
            ToastUtils.showShort("已复制到粘贴板");
        } else if (2 == type) {
            likeClickPos = id;
            doLike();
        }
    }


    private void doLike() {
        if (mData.get(likeClickPos).isIs_like()) {
            HttpUtils.postDisLike(mData.get(likeClickPos).getId(), AppUtils.getDeviceId(mContext), 2, 2,
                    new JsonCallback<BaseEntity>() {
                        @Override
                        protected void onSuccess(BaseEntity bean) {
                            mData.get(likeClickPos).setIs_like(false);
                            mData.get(likeClickPos).setLikes_count(mData.get(likeClickPos).getLikes_count() - 1);
                            communityRecommentAdapter.notifyItemChanged(likeClickPos);
                        }
                    });

        } else {
            HttpUtils.postLike(mData.get(likeClickPos).getId(), AppUtils.getDeviceId(mContext), 2, 2,
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
            ImmersionBar.with(getActivity()).fitsSystemWindows(true).init();
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
}
