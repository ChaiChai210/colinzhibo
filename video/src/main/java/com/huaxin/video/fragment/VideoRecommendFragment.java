package com.huaxin.video.fragment;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.example.librarybase.base.BaseFragment;
import com.huaxin.library.decoration.GridLayoutItemDecoration;
import com.huaxin.library.http.BaseEntity;
import com.huaxin.library.http.HttpUtils;
import com.huaxin.library.http.JsonCallback;
import com.huaxin.library.utils.ARConstants;
import com.huaxin.video.R;
import com.huaxin.video.R2;
import com.huaxin.library.adapter.BannerAdapter;
import com.huaxin.video.adapter.RecommendTypeAdapter;
import com.huaxin.video.adapter.VideoAdapter;
import com.huaxin.library.entity.BannerItem;
import com.huaxin.video.entity.VideoEntity;
import com.huaxin.video.entity.VideoListEntity;
import com.huaxin.video.entity.VideoTypeEntity;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.xuexiang.xui.widget.banner.recycler.BannerLayout;
import com.xuexiang.xui.widget.statelayout.MultipleStatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class VideoRecommendFragment extends BaseFragment implements GroupedRecyclerViewAdapter.OnChildClickListener, GroupedRecyclerViewAdapter.OnFooterClickListener, VideoAdapter.OnHeaderChildClickListener {

    @BindView(R2.id.rcv_type)
    RecyclerView mRcvType;
//    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R2.id.refresh)
    SmartRefreshLayout refresh;
    @BindView(R2.id.bl_horizontal)
    BannerLayout blHorizontal;
    @BindView(R2.id.multiple_status_view)
    MultipleStatusView mMultipleStatusView;

    private BannerAdapter bannerAdapter;
    private VideoAdapter mVideoAdapter;
    private List<VideoListEntity> mData = new ArrayList<>();

    private List<VideoTypeEntity> mVideoTypeEntitys = new ArrayList<>();
    private RecommendTypeAdapter mRecommendTypeAdapter;

    @Override
    protected void loadData() {

        getBanner();
        getTypeList();
        getVideoList();
    }

    private void getBanner() {
        HttpUtils.getBanner(3, new JsonCallback<BaseEntity<List<BannerItem>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<BannerItem>> data) {
                bannerAdapter = new BannerAdapter(data.getData());
                blHorizontal.setAdapter(bannerAdapter);
            }
        });
    }

    private void getVideoList() {
        HttpUtils.getVideoList(new JsonCallback<BaseEntity<List<VideoListEntity>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<VideoListEntity>> data) {
                mData.clear();
                mData.addAll(data.getData());
                mVideoAdapter.notifyDataSetChanged();
            }
        });
    }

    private void getTypeList() {
        HttpUtils.getVideoType(new JsonCallback<BaseEntity<List<VideoTypeEntity>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<VideoTypeEntity>> data) {
                mRecommendTypeAdapter.replaceData(data.getData());
            }
        });
    }


    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) mMultipleStatusView.getContentView();
        refresh.setEnableLoadMore(false);

//        热门分类
        mRcvType.setLayoutManager(new GridLayoutManager(getContext(), 4));
        mRecommendTypeAdapter = new RecommendTypeAdapter(mVideoTypeEntitys);
        mRcvType.setAdapter(mRecommendTypeAdapter);
        mRecommendTypeAdapter.setOnItemClickListener((adapter, view1, position) -> {
            VideoTypeEntity videoTypeEntity = mVideoTypeEntitys.get(position);
            ARouter.getInstance().build(ARConstants.PATH_VIDEO_ALL_TYPE).withInt("type", videoTypeEntity.getId()).navigation();
        });
        mRcvType.addItemDecoration(new GridLayoutItemDecoration(mContext, R.drawable.video_item_divider));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mVideoAdapter.getItemViewType(position) == VideoAdapter.TYPE_HEADER || mVideoAdapter.getItemViewType(position) == VideoAdapter.TYPE_FOOTER)
                    return 2;
                else {
                    return 1;
                }
            }
        });

        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.addItemDecoration(new GridLayoutItemDecoration(mContext, R.drawable.video_item_divider));
        mVideoAdapter = new VideoAdapter(getContext(), mData);
        mVideoAdapter.setOnChildClickListener(this);
        mVideoAdapter.setOnFooterClickListener(this);
        mVideoAdapter.setOnHeaderChildClickListener(this);
        recyclerView.setAdapter(mVideoAdapter);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.video_frg_recommend;
    }

    @Override
    public void onChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition, int childPosition) {
        int id = mData.get(groupPosition).getData().get(childPosition).getId();
        ARouter.getInstance().build(ARConstants.PATH_VIDEO_PLAY).withInt("id", id).navigation();
    }

    @Override
    public void onFooterClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition) {
        int id = mVideoAdapter.mData.get(groupPosition).getId();
        HttpUtils.getVideoFilter(id, new JsonCallback<BaseEntity<List<VideoEntity>>>() {
            @Override
            protected void onSuccess(BaseEntity<List<VideoEntity>> data) {
                mData.get(groupPosition).setData(data.getData());
                mVideoAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onHeaderChildClick(GroupedRecyclerViewAdapter adapter, BaseViewHolder holder, int groupPosition) {
        ARouter.getInstance().build(ARConstants.PATH_VIDEO_ALL_TYPE).withInt("type", mData.get(groupPosition).getId()).navigation();
    }
}
